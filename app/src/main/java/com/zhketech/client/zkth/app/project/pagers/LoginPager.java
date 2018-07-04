package com.zhketech.client.zkth.app.project.pagers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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


    //整个项目可能用到的权限
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.USE_SIP,
            Manifest.permission.READ_PHONE_STATE
    };
    //存放未授权的权限
    List<String> mPermissionList = new ArrayList<>();
    Context context;
    //标识
    public static final int FLAGE = 1000;
    //设备信息集合
    List<Device> dataSources = new ArrayList<>();
    //记录CMS返回的数据问题
    int num = -1;
    //hander处理
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
        //判断是否需要权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        } else {
            initData();
        }
        initViewAndListern();
    }

    @Override
    public void initData() {
        Log.i("TAG", "initData");
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

    //注册sip信息
    private void registerSip() {
        if (!SipService.isReady()) {
            Linphone.startService(this);
        }
        Linphone.setAccount("7008", "123456", "19.0.0.60");
        Linphone.login();
    }

    //监听
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

    //切换竖屏会走以下的方法
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
    }

    //权限申请
    private void requestPermission() {
        mPermissionList.clear();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(LoginPager.this, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        } /** * 判断存储委授予权限的集合是否为空 */
        if (!mPermissionList.isEmpty()) {
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(LoginPager.this, permissions, 1);
        } else {
            //未授予的权限为空，表示都授予了 // 后续操作...
            initData();
        }
    }

    boolean mShowRequestPermission = true;//用户是否禁止权限

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(LoginPager.this, permissions[i]);
                        if (showRequestPermission) {//
                            requestPermission();//重新申请权限
                            return;
                        } else {
                            mShowRequestPermission = false;//已经禁止
                            String permisson = permissions[i];
                            android.util.Log.i("TAG", "permisson:" + permisson);
                        }
                    }
                }
                initData();
                break;
            default:
                break;
        }
    }

}
