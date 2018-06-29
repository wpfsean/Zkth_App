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
                openActivity(MainPager.class);
                break;
            case R.id.userlogin_button_cancel_layout:
                toastShort("can not finish");
                break;
        }
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

        int dir = LoginPager.this.getResources().getConfiguration().orientation;
        Log.i("TAG", "dir:" + dir);
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
}
