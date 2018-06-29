package com.zhketech.client.zkth.app.project.pagers;

import android.content.res.Configuration;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.base.BaseActivity;
import com.zhketech.client.zkth.app.project.global.AppConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;

/**
 * Created by Root on 2018/6/29.
 * <p>
 * 播放视频时Activty
 */

public class MutilScreenPager extends BaseActivity implements ViewPager.OnPageChangeListener,NodePlayerDelegate ,View.OnClickListener{

    //竖屏底总横向滑动的viewPager
    ViewPager bottomViewPager;
    //单屏的NodeMediaclient播放器
    NodePlayer singlePlayer;
    NodePlayer firstPalyer, secondPlayer, thirdPlayer, fourthPlayer;
    private List<View> views;

    @BindView(R.id.first_player_layout)//第一个视频的view
            NodePlayerView firstPlayerView;
    //第一个视频 的Progressbar
    @BindView(R.id.first_pr_layout)
    ProgressBar first_pr_layout;
    //第一个视频 的loading
    @BindView(R.id.first_dispaly_loading_layout)
    TextView first_dispaly_loading_layout;
    //第一个视频所在的背景而
    @BindView(R.id.first_surfaceview_relativelayout)
    public RelativeLayout first_surfaceview_relativelayout;

    @BindView(R.id.second_player_layout)//第二个视频的view
            NodePlayerView secondPlayerView;

    @BindView(R.id.third_player_layout)//第三个视频的view
            NodePlayerView thirdPlayerView;

    @BindView(R.id.fourth_player_layout)//第四个视频的view
            NodePlayerView fourthPlayerView;

    @BindView(R.id.fourth_pr_layout)
    ProgressBar fourth_pr_layout;
    @BindView(R.id.fourth_dispaly_loading_layout)
    TextView fourth_dispaly_loading_layout;




    @Override
    public int intiLayout() {
        return R.layout.activity_multi_screen;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

        firstPalyer = new NodePlayer(this);
        firstPalyer.setPlayerView(firstPlayerView);
        firstPalyer.setInputUrl("rtmp://live.hkstv.hk.lxdns.com/live/hks");
        firstPalyer.setNodePlayerDelegate(this);
        firstPalyer.setAudioEnable(AppConfig.isVideoSound);
        firstPalyer.setVideoEnable(true);
        firstPalyer.start();
        firstPlayerView.setOnClickListener(this);

        fourthPlayer= new NodePlayer(this);
        fourthPlayer.setPlayerView(fourthPlayerView);
        fourthPlayer.setInputUrl("rtmp://live.hkstv.hk.lxdns.com/live/hksdddddddddddd");
        fourthPlayer.setNodePlayerDelegate(this);
        fourthPlayer.setAudioEnable(AppConfig.isVideoSound);
        fourthPlayer.setVideoEnable(true);
        fourthPlayer.start();
        fourthPlayerView.setOnClickListener(this);

    }

    @Override
    public void initData() {
        int direction = this.getResources().getConfiguration().orientation;
        Log.i("TAG", "direction:" + direction + "\n" + new Date().toString());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (AppConfig.direction == 2) {
            Log.i("TAG", "横屏时不走");
        } else if (AppConfig.direction == 1) {
            initViewPagerData();
            initView();
        }
    }


    private void initViewPagerData() {
        bottomViewPager = this.findViewById(R.id.mutilscreent_viewpager);
        views = new ArrayList<View>();
        LayoutInflater li = getLayoutInflater();
        views.add(li.inflate(R.layout.function1_button_activity, null));
        views.add(li.inflate(R.layout.function2_button_activity, null));
        views.add(li.inflate(R.layout.function2_button_activity, null));
        initFuntionBtnSelected();
        ButtomViewPagerAda ada = new ButtomViewPagerAda();
        bottomViewPager.setAdapter(ada);
        bottomViewPager.setOnPageChangeListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("TAG", "///Start");
    }


    private void initFuntionBtnSelected() {
        View view = views.get(0);
        final ImageButton imageButton = view.findViewById(R.id.video_play_button_layout);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageButton.setBackgroundResource(R.mipmap.port_video_surveillance_btn_selected);
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onEventCallback(NodePlayer player, int event, final String msg) {

        if (firstPalyer == player){
            if (event == 1001){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        first_pr_layout.setVisibility(View.GONE);
                        first_dispaly_loading_layout.setVisibility(View.GONE);
                    }
                });
            }else if (event == 1003){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        first_dispaly_loading_layout.setVisibility(View.VISIBLE);
                        first_pr_layout.setVisibility(View.GONE);
                        first_dispaly_loading_layout.setText(msg);
                    }
                });
            }
            Log.i("TAG",player.isPlaying()+"");
            Log.i("TAG","event:"+event+"\n"+msg);
        }

        if (fourthPlayer == player){
            if (event == 1001){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fourth_pr_layout.setVisibility(View.GONE);
                        fourth_dispaly_loading_layout.setVisibility(View.GONE);
                    }
                });
            }else if (event == 1003){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fourth_dispaly_loading_layout.setVisibility(View.VISIBLE);
                        fourth_pr_layout.setVisibility(View.GONE);
                        fourth_dispaly_loading_layout.setText(msg);
                    }
                });
            }
            Log.i("TAG",player.isPlaying()+"");
            Log.i("TAG","event:"+event+"\n"+msg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.first_player_layout:
                first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_select_shape);
                break;
        }
    }

    //最底部的viewpager适配器
    class ButtomViewPagerAda extends PagerAdapter {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        //有多少个切换页
        @Override
        public int getCount() {
            return views.size();
        }

        //对超出范围的资源进行销毁
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(views.get(position));
        }

        //对显示的资源进行初始化
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }
    }

    public void initVerticakScreenData(){

    }



}
