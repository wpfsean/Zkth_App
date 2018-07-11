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
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.base.BaseActivity;
import com.zhketech.client.zkth.app.project.beans.LoginParameters;
import com.zhketech.client.zkth.app.project.beans.VideoBen;
import com.zhketech.client.zkth.app.project.callbacks.LoginIntoServerThread;
import com.zhketech.client.zkth.app.project.callbacks.RequestVideoSourcesThread;
import com.zhketech.client.zkth.app.project.global.AppConfig;
import com.zhketech.client.zkth.app.project.onvif.Device;
import com.zhketech.client.zkth.app.project.onvif.Onvif;
import com.zhketech.client.zkth.app.project.taking.tils.Linphone;
import com.zhketech.client.zkth.app.project.taking.tils.RegistrationCallback;
import com.zhketech.client.zkth.app.project.taking.tils.SipService;
import com.zhketech.client.zkth.app.project.utils.GsonUtils;
import com.zhketech.client.zkth.app.project.utils.Logutils;
import com.zhketech.client.zkth.app.project.utils.PhoneUtils;
import com.zhketech.client.zkth.app.project.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginPager extends BaseActivity implements View.OnClickListener {

    //LoginProgressBar
    @BindView(R.id.login_progressbar_layout)
    ProgressBar loginBar;


    //用户名
    @BindView(R.id.edit_username_layout)
    EditText userName;
    //密码
    @BindView(R.id.edit_userpass_layout)
    EditText userPwd;
    //记住密码Checkbox
    @BindView(R.id.remember_pass_layout)
    Checkable rememberPwd;
    //自动登录CheckBox
    @BindView(R.id.auto_login_layout)
    Checkable autoLoginCheckBox;
    //服务器
    @BindView(R.id.edit_serviceip_layout)
    EditText serverIp;
    //updateServerIpCheckBox
    @BindView(R.id.remembe_serverip_layout)
    CheckBox updateServerIpCheckBox;

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
            //Dosomething
            initThisPageData();
        }
        initViewAndListern();
    }

    /**
     * 初始化本页面的数据
     */
    private void initThisPageData() {
        String nativeIp = PhoneUtils.displayIpAddress(LoginPager.this);
        if (!TextUtils.isEmpty(nativeIp)) {
            SharedPreferencesUtils.putObject(LoginPager.this, "nativeIp", nativeIp);
        }
        Logutils.i("NativeIp:" + nativeIp);
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userlogin_button_layout:
                //登录到服务器
                loginIntoServer();
                break;
            case R.id.userlogin_button_cancel_layout:
                //测试channel页面
                openActivity(ChannelListPager.class);
                break;
        }
    }

    /**
     * 登录到服务器
     */
    private void loginIntoServer() {

//        final String name = userName.getText().toString().trim();
//        final String pass = userPwd.getText().toString().trim();
//        final String server_IP = serverIp.getText().toString().trim();
//        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(server_IP)) {
//
//            LoginParameters loginParameters = new LoginParameters();
//            loginParameters.setUsername(name);
//            loginParameters.setPass(pass);
//            loginParameters.setServer_ip(server_IP);
//            String localIp = (String) SharedPreferencesUtils.getObject(LoginPager.this, "nativeIp", "");
//            if (TextUtils.isEmpty(localIp)) {
//                Logutils.e("Local_Ip is empty !!!");
//                return;
//            }
//            loginParameters.setNative_ip(localIp);
//            LoginIntoServerThread loginThread = new LoginIntoServerThread(LoginPager.this, loginParameters, new LoginIntoServerThread.IsLoginListern() {
//                @Override
//                public void loginStatus(String status) {
//                    if (!TextUtils.isEmpty(status)) {
//
//                        String result = status;
//                        Logutils.i(result);
//                        if (status.equals("success")) {
                            try {
                                Thread.sleep(2 * 1000);
                                openActivityAndCloseThis(MainPager.class);
                            } catch (InterruptedException e) {
                                Logutils.e("Execption:" + e.getMessage());
                            }
//                        } else if (status.contains("fail")) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    toastShort("Login Filed !!\n Please enter the correct informatiom !!!");
//                                }
//                            });
//                        }
//                    }
//                }
//            });
//            loginThread.start();
//        } else {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(LoginPager.this, "Can not be empty !!!", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginBar.setVisibility(View.GONE);
            }
        });
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
            initThisPageData();
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
