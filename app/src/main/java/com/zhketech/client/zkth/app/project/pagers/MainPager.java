package com.zhketech.client.zkth.app.project.pagers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.base.ActivityManager;
import com.zhketech.client.zkth.app.project.base.BaseActivity;
import com.zhketech.client.zkth.app.project.global.AppConfig;
import com.zhketech.client.zkth.app.project.services.SendheartService;
import com.zhketech.client.zkth.app.project.taking.tils.Linphone;
import com.zhketech.client.zkth.app.project.taking.tils.PhoneCallback;
import com.zhketech.client.zkth.app.project.taking.tils.RegistrationCallback;
import com.zhketech.client.zkth.app.project.taking.tils.SipService;
import com.zhketech.client.zkth.app.project.utils.Logutils;
import org.linphone.core.LinphoneCall;


import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainPager extends BaseActivity implements View.OnClickListener {

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

    public  static  int timeFlage = 10001;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == timeFlage){
                long time = System.currentTimeMillis();
                Date date = new Date(time);
                SimpleDateFormat timeD = new SimpleDateFormat("HH:mm:ss");
                timeTextView.setText(timeD.format(date));
                SimpleDateFormat dateD = new SimpleDateFormat("MM月dd日 EEE");
                dateTextView.setText(dateD.format(date));
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
        startService(new Intent(this, SendheartService.class));
        TimeThread timeThread = new TimeThread();
        new Thread(timeThread).start();
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
    public void goToIntercomScreen(){openActivity(SipGroupPager.class);}

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

    @Override
    protected void onPause() {
        super.onPause();
        SipService.removePhoneCallback();;
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
