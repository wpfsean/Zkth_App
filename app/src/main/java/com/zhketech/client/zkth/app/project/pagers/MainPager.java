package com.zhketech.client.zkth.app.project.pagers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.base.ActivityManager;
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

public class MainPager extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.button_setup)
    ImageButton button_setup;
    @BindView(R.id.button_video)
    ImageButton button_video;

    @BindView(R.id.button_intercom)
    ImageButton button_intercom;

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
    public void goToMutilScreen(){
        openActivity(MutilScreenPager.class);
    }

    //SipGroup界面
    public void goToIntercomScreen(){openActivity(SipGroup.class);}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (AppConfig.direction == 2) {
            Log.i("TAG","横屏时不走");
        } else if (AppConfig.direction == 1) {
            Log.i("TAG","竖屏时走这个方法，哈哈");
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
                Logutils.i("incomingCall:"+linphoneCall.getRemoteAddress().getDisplayName());

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
}
