package com.zhketech.client.zkth.app.project.pagers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.base.ActivityManager;
import com.zhketech.client.zkth.app.project.base.BaseActivity;
import com.zhketech.client.zkth.app.project.beans.SipBean;
import com.zhketech.client.zkth.app.project.beans.VideoBen;
import com.zhketech.client.zkth.app.project.callbacks.BatteryAndWifiCallback;
import com.zhketech.client.zkth.app.project.callbacks.BatteryAndWifiService;
import com.zhketech.client.zkth.app.project.callbacks.RequestSipSourcesThread;
import com.zhketech.client.zkth.app.project.callbacks.RequestVideoSourcesThread;
import com.zhketech.client.zkth.app.project.global.AppConfig;
import com.zhketech.client.zkth.app.project.onvif.Device;
import com.zhketech.client.zkth.app.project.onvif.Onvif;
import com.zhketech.client.zkth.app.project.services.SendheartService;
import com.zhketech.client.zkth.app.project.taking.tils.Linphone;
import com.zhketech.client.zkth.app.project.taking.tils.PhoneCallback;
import com.zhketech.client.zkth.app.project.taking.tils.RegistrationCallback;
import com.zhketech.client.zkth.app.project.taking.tils.SipService;
import com.zhketech.client.zkth.app.project.utils.GsonUtils;
import com.zhketech.client.zkth.app.project.utils.Logutils;
import com.zhketech.client.zkth.app.project.utils.SharedPreferencesUtils;

import org.linphone.core.LinphoneCall;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainPager extends BaseActivity implements View.OnClickListener {

    //标识
    public static final int FLAGE = 1000;
    //设备信息集合
    List<Device> dataSources = new ArrayList<>();
    //记录CMS返回的数据问题
    int num = -1;
    //设置按键
    @BindView(R.id.button_setup)
    ImageButton button_setup;
    //视频按键
    @BindView(R.id.button_video)
    ImageButton button_video;
    //对讲按键
    @BindView(R.id.button_intercom)
    ImageButton button_intercom;
    //时间
    @BindView(R.id.main_incon_time)
    TextView timeTextView;
    //日期
    @BindView(R.id.main_icon_date)
    TextView dateTextView;

    public static int timeFlage = 10001;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //主页面时间显示
            if (msg.what == timeFlage) {
                long time = System.currentTimeMillis();
                Date date = new Date(time);
                SimpleDateFormat timeD = new SimpleDateFormat("HH:mm:ss");
                timeTextView.setText(timeD.format(date));
                SimpleDateFormat dateD = new SimpleDateFormat("MM月dd日 EEE");
                dateTextView.setText(dateD.format(date));
            } else if (msg.what == FLAGE){
                //onvif数据处理
                Bundle bundle = msg.getData();
                Device device = (Device) bundle.getSerializable("device");
                dataSources.add(device);
                if (dataSources.size() == num) {
                    Logutils.i(dataSources.toString());
                    Log.i("TAG", dataSources.size() + "");
                    Logutils.i("Date:" + new Date().toString());
                    String json = GsonUtils.getGsonInstace().list2String(dataSources);
                    if (TextUtils.isEmpty(json)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                promptNoData();
                            }
                        });
                        Logutils.i("为空了");
                        return;
                    }
                    AppConfig.data = json;
                    SharedPreferencesUtils.putObject(MainPager.this, "result", json);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        toastShort("init data fished !!!");
                        }
                    });
                }
            }
        }
    };


    @Override
    public int intiLayout() {
        return R.layout.main_pager_activity;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        button_setup.setOnClickListener(this);
        button_video.setOnClickListener(this);
        button_intercom.setOnClickListener(this);
    }

    @Override
    public void initData() {
        //启动心跳service
        startService(new Intent(this, SendheartService.class));
        //电量心跳、wifi的service
        startService(new Intent(this, BatteryAndWifiService.class));
        TimeThread timeThread = new TimeThread();
        new Thread(timeThread).start();

        //获取服务器的sip信息并注册sip到服务器
        RequestSipSourcesThread sipThread = new RequestSipSourcesThread(MainPager.this, "0", new RequestSipSourcesThread.SipListern() {
            @Override
            public void getDataListern(List<SipBean> mList) {
                String nativeIp = (String) SharedPreferencesUtils.getObject(MainPager.this, "nativeIp", "");
                if (mList != null && mList.size() > 0) {
                    for (SipBean s : mList) {
                        if (s.getIp().equals(nativeIp)) {
                            String sipName = s.getName();
                            String sipNum = s.getNumber();
                            String sipPwd = s.getSippass();
                            String sipServer = s.getSipserver();
                            if (!TextUtils.isEmpty(sipNum) && !TextUtils.isEmpty(sipPwd) && !TextUtils.isEmpty(sipServer)) {
                                SharedPreferencesUtils.putObject(MainPager.this, "sipName", sipName);
                                SharedPreferencesUtils.putObject(MainPager.this, "sipNum", sipNum);
                                SharedPreferencesUtils.putObject(MainPager.this, "sipPwd", sipPwd);
                                SharedPreferencesUtils.putObject(MainPager.this, "sipServer", sipServer);
                                registerSipIntoServer(sipNum, sipPwd, sipServer);
                            }
                            break;
                        }
                    }
                }
            }
        });
        sipThread.start();
        //解析onvif
        onvifRtsp();
    }

    /**
     * 解析onvif中的rtsp
     */
    private void onvifRtsp() {
        Logutils.i("Date:" + new Date().toString());
        RequestVideoSourcesThread requestVideoSourcesThread = new RequestVideoSourcesThread(MainPager.this, new RequestVideoSourcesThread.GetDataListener() {
            @Override
            public void getResult(List<VideoBen> mList) {
                if (mList != null && mList.size() > 0) {
                    num = mList.size();
                    for (int i = 0; i < mList.size(); i++) {
                        String ip = mList.get(i).getIp();
                        final Device device = new Device();
                        device.setVideoBen(mList.get(i));
                        device.setServiceUrl("http://" + ip + "/onvif/device_service");
                        Onvif onvif = new Onvif(device, new Onvif.GetRtspCallback() {
                            @Override
                            public void getDeviceInfoResult(String rtsp, boolean isSuccess, Device mDevice) {
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("device", mDevice);
                                message.setData(bundle);
                                message.what = FLAGE;
                                handler.sendMessage(message);
                            }
                        });
                        new Thread(onvif).start();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            promptNoData();
                        }
                    });
                }
            }
        });
        requestVideoSourcesThread.start();
    }


    /**
     * 注册sip到服务器
     *
     * @param sipNum
     * @param sipPwd
     * @param sipServer
     */
    private void registerSipIntoServer(String sipNum, String sipPwd, String sipServer) {

        if (!SipService.isReady()) {
            Linphone.startService(this);
        }
        Linphone.setAccount(sipNum, sipPwd, sipServer);
        Linphone.login();
    }

    //切换屏幕的方向
    public void setDirection() {
        if (AppConfig.direction == 1) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            AppConfig.direction = 2;
        } else if (AppConfig.direction == 2) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            AppConfig.direction = 1;
        }
        //remove所有的activity并重新打开应用
        ActivityManager.getActivityStackManager().removeAllActivity();
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    //监控画面
    public void goToMutilScreen() {
        openActivity(MutilScreenPager.class);
    }

    //SipGroup界面
    public void goToIntercomScreen() {
        openActivity(SipGroupPager.class);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (AppConfig.direction == 2) {
            Log.i("TAG", "横屏时不走");
        } else if (AppConfig.direction == 1) {
            Log.i("TAG", "竖屏时走这个方法，哈哈");
            initView();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_setup:
                setDirection();
                break;
            case R.id.button_video:
                goToMutilScreen();
                break;
            case R.id.button_intercom:
                goToIntercomScreen();
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        BatteryAndWifiService.addBatterCallback(new BatteryAndWifiCallback() {
            @Override
            public void getBatteryData(int level) {
                super.getBatteryData(level);
                //  Logutils.i("电量："+level);
            }

            @Override
            public void getWifiData(int rssi) {
                super.getWifiData(rssi);
                // Logutils.i("信号："+rssi);
            }
        });

        Linphone.addCallback(new RegistrationCallback() {
            @Override
            public void registrationNone() {
                Logutils.i("registrationNone");
            }

            @Override
            public void registrationProgress() {
                super.registrationProgress();
                Logutils.i("registrationProgress");
            }

            @Override
            public void registrationOk() {
                super.registrationOk();
                Logutils.i("registrationOk");
            }

            @Override
            public void registrationCleared() {
                super.registrationCleared();
                Logutils.i("registrationCleared");

            }

            @Override
            public void registrationFailed() {
                super.registrationFailed();
                Logutils.i("registrationFailed");

            }
        }, new PhoneCallback() {
            @Override
            public void incomingCall(LinphoneCall linphoneCall) {
                super.incomingCall(linphoneCall);
                Logutils.i("incomingCall:" + linphoneCall.getRemoteAddress().getDisplayName());

            }

            @Override
            public void outgoingInit() {
                super.outgoingInit();
                Logutils.i("outgoingInit");

            }

            @Override
            public void callConnected() {
                super.callConnected();
                Logutils.i("callConnected");

            }

            @Override
            public void callEnd() {
                super.callEnd();
                Logutils.i("callEnd");

            }

            @Override
            public void callReleased() {
                super.callReleased();
                Logutils.i("callReleased");

            }

            @Override
            public void error() {
                super.error();
                Logutils.i("error");

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        SipService.removePhoneCallback();
        ;
    }

    //显示时间的线程
    class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = timeFlage;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }
}
