package com.zhketech.client.zkth.app.project.pagers;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.base.BaseActivity;
import com.zhketech.client.zkth.app.project.beans.VideoBen;
import com.zhketech.client.zkth.app.project.callbacks.RequestVideoSourcesThread;
import com.zhketech.client.zkth.app.project.global.AppConfig;
import com.zhketech.client.zkth.app.project.onvif.Device;
import com.zhketech.client.zkth.app.project.onvif.Onvif;
import com.zhketech.client.zkth.app.project.taking.tils.Linphone;
import com.zhketech.client.zkth.app.project.taking.tils.RegistrationCallback;
import com.zhketech.client.zkth.app.project.taking.tils.SipService;
import com.zhketech.client.zkth.app.project.utils.GsonUtils;
import com.zhketech.client.zkth.app.project.utils.Logutils;
import com.zhketech.client.zkth.app.project.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

public class LoginPager extends BaseActivity implements View.OnClickListener {

    Context context;
    public static final int FLAGE = 1000;
    List<Device> dataSources = new ArrayList<>();
    int num = -1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case FLAGE:
                    Bundle bundle = msg.getData();
                    Device device = (Device) bundle.getSerializable("device");
                    dataSources.add(device);
                    if (dataSources.size() == num) {

                        Logutils.i(dataSources.toString());
                        Log.i("TAG", dataSources.size() + "");
                        Logutils.i("Date:" + new Date().toString());
                        String json = GsonUtils.getGsonInstace().list2String(dataSources);
                        if (TextUtils.isEmpty(json)) {
                            Logutils.i("为空了");
                            return;
                        }
                        AppConfig.data = json;
                        SharedPreferencesUtils.putObject(LoginPager.this,"result",json);
                        Logutils.i("SUccess");
//                        List<Device> mlist = GsonUtils.getGsonInstace().str2List(json);
//                        if (mlist != null)
//                            Logutils.i(mlist.size() + "");
                    }
                    break;
            }
        }
    };

    @Override
    public int intiLayout() {
        return R.layout.login_pager_activity;
    }

    @Override
    public void initView() {
        context = this;
        ButterKnife.bind(this);
        initViewAndListern();
    }

    @Override
    public void initData() {
        Log.i("TAG", "sb");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.userlogin_button_layout:
                registerSip();
                openActivity(MainPager.class);
                break;
            case R.id.userlogin_button_cancel_layout:
//                toastShort("can not finish");
//                promptNoData();

                onvifRtsp();

                break;
        }
    }

    //解析rtsp数据
    private void onvifRtsp() {
        Logutils.i("Date:" + new Date().toString());
        RequestVideoSourcesThread requestVideoSourcesThread = new RequestVideoSourcesThread(LoginPager.this, new RequestVideoSourcesThread.GetDataListener() {
            @Override
            public void getResult(List<VideoBen> mList) {
                if (mList != null && mList.size() > 0) {
                    num = mList.size();
                    for (int i = 0; i < mList.size(); i++) {
                        String ip = mList.get(i).getIp();
                        final Device device = new Device();
                        device.setUserName(mList.get(i).getUsername());
                        device.setPsw(mList.get(i).getPassword());
                        device.setName(mList.get(i).getName());
                        device.setChannel(mList.get(i).getChannel());
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
                    Logutils.i("No data");
                }
            }
        });
        requestVideoSourcesThread.start();
    }

    private void registerSip() {
        if (!SipService.isReady()) {
            Linphone.startService(this);
        }
        Linphone.setAccount("7008", "123456", "19.0.0.60");
        Linphone.login();
    }

    public void initViewAndListern() {
        ImageButton loginButtonSure = this.findViewById(R.id.userlogin_button_layout);
        ImageButton loginButtonCancel = this.findViewById(R.id.userlogin_button_cancel_layout);
        loginButtonSure.setOnClickListener(this);
        loginButtonCancel.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Linphone.addCallback(new RegistrationCallback() {
            @Override
            public void registrationOk() {
                super.registrationOk();
                Logutils.i("成功");
            }

            @Override
            public void registrationFailed() {
                super.registrationFailed();
                Logutils.i("失败");
                registerSip();
            }
        }, null);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (AppConfig.direction == 2) {
            Log.i("TAG", "横屏时不走");
        } else if (AppConfig.direction == 1) {
            initViewAndListern();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SipService.removePhoneCallback();
        SipService.removePhoneCallback();

    }
}
