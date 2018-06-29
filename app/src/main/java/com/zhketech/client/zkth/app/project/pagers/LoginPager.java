package com.zhketech.client.zkth.app.project.pagers;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.base.BaseActivity;
import com.zhketech.client.zkth.app.project.global.AppConfig;
import com.zhketech.client.zkth.app.project.taking.tils.Linphone;
import com.zhketech.client.zkth.app.project.taking.tils.PhoneCallback;
import com.zhketech.client.zkth.app.project.taking.tils.RegistrationCallback;
import com.zhketech.client.zkth.app.project.taking.tils.SipService;
import com.zhketech.client.zkth.app.project.utils.Logutils;

import org.linphone.core.LinphoneCall;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginPager extends BaseActivity implements View.OnClickListener {

    Context context;


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

        switch (v.getId()){
            case R.id.userlogin_button_layout:
                registerSip();
                openActivity(MainPager.class);
                break;
            case R.id.userlogin_button_cancel_layout:
                toastShort("can not finish");
                promptNoData();
                break;
        }
    }

    private void registerSip() {
        if (!SipService.isReady()){
            Linphone.startService(this);
        }
        Linphone.setAccount("7008","123456","19.0.0.60");
        Linphone.login();
    }

    public void initViewAndListern(){
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
       },null);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (AppConfig.direction == 2) {
            Log.i("TAG","横屏时不走");
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
