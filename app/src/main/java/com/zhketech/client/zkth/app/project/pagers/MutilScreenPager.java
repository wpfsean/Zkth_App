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
import butterknife.OnClick;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;

/**
 * Created by Root on 2018/6/29.
 * <p>
 * 播放视频时Activty
 */

public class MutilScreenPager extends BaseActivity implements ViewPager.OnPageChangeListener, NodePlayerDelegate, View.OnClickListener {

    //当前的屏幕状态（true 横屏， false 竖屏）
    boolean isLand = true;


    //竖屏底总横向滑动的viewPager
    ViewPager bottomViewPager;
    //单屏的NodeMediaclient播放器
    NodePlayer singlePlayer;
    NodePlayer firstPalyer, secondPlayer, thirdPlayer, fourthPlayer;
    private List<View> views;

    @BindView(R.id.single_player_layout)
    NodePlayerView single_player_layout;

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
    //第二个视频所在的背景而
    @BindView(R.id.second_surfaceview_relativelayout)
    public RelativeLayout second_surfaceview_relativelayout;


    @BindView(R.id.third_player_layout)//第三个视频的view
            NodePlayerView thirdPlayerView;
    //第三个视频所在的背景而
    @BindView(R.id.third_surfaceview_relativelayout)
    public RelativeLayout third_surfaceview_relativelayout;


    @BindView(R.id.fourth_player_layout)//第四个视频的view
            NodePlayerView fourthPlayerView;
    //第四个视频所在的背景而
    @BindView(R.id.fourth_surfaceview_relativelayout)
    public RelativeLayout fourth_surfaceview_relativelayout;

    //每四个progressbar
    @BindView(R.id.fourth_pr_layout)
    ProgressBar fourth_pr_layout;

    //返回按钮
    @BindView(R.id.fourth_dispaly_loading_layout)
    TextView fourth_dispaly_loading_layout;


    //判断这四个视频 中否被选中
    boolean firstViewSelect = false;
    boolean secondViewSelect = false;
    boolean thirdViewSelect = false;
    boolean fourthViewSelect = false;

    ImageButton single_screen_button_selecte;
    ImageButton four_screen_button_select;


    @BindView(R.id.four_surfaceview_parent_relativelayout)
    RelativeLayout four_surfaceview_parent_relativelayout;

    @BindView(R.id.single_surfaceview_parent_relativelayout)
    RelativeLayout single_surfaceview_parent_relativelayout;


    @Override
    public int intiLayout() {
        return R.layout.activity_multi_screen;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

        firstPalyer = new NodePlayer(this);
        firstPalyer.setPlayerView(firstPlayerView);
        firstPalyer.setInputUrl("rtsp://admin:pass@19.0.0.211:554/H264?ch=16&subtype=1");
        firstPalyer.setNodePlayerDelegate(this);
        firstPalyer.setAudioEnable(AppConfig.isVideoSound);
        firstPalyer.setVideoEnable(true);
        firstPalyer.start();
        firstPlayerView.setOnClickListener(this);


        secondPlayer = new NodePlayer(this);
        secondPlayer.setPlayerView(secondPlayerView);
        secondPlayer.setInputUrl("rtmp://live.hkstv.hk.lxdns.com/live/hks");
        secondPlayer.setNodePlayerDelegate(this);
        secondPlayer.setAudioEnable(AppConfig.isVideoSound);
        secondPlayer.setVideoEnable(true);
        secondPlayer.start();
        secondPlayerView.setOnClickListener(this);


        thirdPlayer = new NodePlayer(this);
        thirdPlayer.setPlayerView(thirdPlayerView);
        thirdPlayer.setInputUrl("rtmp://live.hkstv.hk.lxdns.com/live/hks");
        thirdPlayer.setNodePlayerDelegate(this);
        thirdPlayer.setAudioEnable(AppConfig.isVideoSound);
        thirdPlayer.setVideoEnable(true);
        thirdPlayer.start();
        thirdPlayerView.setOnClickListener(this);

        fourthPlayer = new NodePlayer(this);
        fourthPlayer.setPlayerView(fourthPlayerView);
        fourthPlayer.setInputUrl("rtsp://admin:pass@19.0.0.213:554/H264?ch=1&amp;subtype=0&amp;proto=Onvif");
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
    protected void onStart() {
        super.onStart();
        Log.i("TAG", "///Start");
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (AppConfig.direction == 2) {
            Log.i("TAG", "横屏时不走");
        } else if (AppConfig.direction == 1) {
            initViewPagerData();
            initView();
            isLand = false;
            single_screen_button_selecte = this.findViewById(R.id.single_screen_button_selecte);
            four_screen_button_select = this.findViewById(R.id.four_screen_button_select);
            four_screen_button_select.setSelected(true);

        }
    }


    //竖屏的viewpager展示数据
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

        if (firstPalyer == player) {
            if (event == 1001) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        first_pr_layout.setVisibility(View.GONE);
                        first_dispaly_loading_layout.setVisibility(View.GONE);
                    }
                });
            } else if (event == 1003) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        first_dispaly_loading_layout.setVisibility(View.VISIBLE);
                        first_pr_layout.setVisibility(View.GONE);
                        first_dispaly_loading_layout.setText(msg);
                    }
                });
            }
            Log.i("TAG", player.isPlaying() + "");
            Log.i("TAG", "event:" + event + "\n" + msg);
        }

        if (fourthPlayer == player) {
            if (event == 1001) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fourth_pr_layout.setVisibility(View.GONE);
                        fourth_dispaly_loading_layout.setVisibility(View.GONE);
                    }
                });
            } else if (event == 1003) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fourth_dispaly_loading_layout.setVisibility(View.VISIBLE);
                        fourth_pr_layout.setVisibility(View.GONE);
                        fourth_dispaly_loading_layout.setText(msg);
                    }
                });
            }
            Log.i("TAG", player.isPlaying() + "");
            Log.i("TAG", "event:" + event + "\n" + msg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_player_layout:
                first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_select_shape);
                second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                firstViewSelect = true;
                secondViewSelect = false;
                thirdViewSelect = false;
                fourthViewSelect = false;
                break;
            case R.id.second_player_layout:
                first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_select_shape);
                third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                firstViewSelect = false;
                secondViewSelect = true;
                thirdViewSelect = false;
                fourthViewSelect = false;
                break;
            case R.id.third_player_layout:
                first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_select_shape);
                fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                firstViewSelect = false;
                secondViewSelect = false;
                thirdViewSelect = true;
                fourthViewSelect = false;
                break;
            case R.id.fourth_player_layout:
                first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_select_shape);
                firstViewSelect = false;
                secondViewSelect = false;
                thirdViewSelect = false;
                fourthViewSelect = true;
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

    public void initVerticakScreenData() {

    }


    @OnClick(R.id.finish_back_layout)
    public void finishThisActivity(View view) {
        if (firstPalyer != null) firstPalyer.release();
        firstPalyer = null;
        if (secondPlayer != null) secondPlayer.release();
        secondPlayer = null;
        if (thirdPlayer != null) thirdPlayer.release();
        thirdPlayer = null;
        if (fourthPlayer != null) fourthPlayer.release();
        fourthPlayer = null;
        if (singlePlayer != null) singlePlayer.release();
        singlePlayer = null;
        MutilScreenPager.this.finish();
    }


    @OnClick(R.id.single_screen_button_selecte)
    public void singleScreenVideo(View view) {

        if (isLand == false) {
            single_screen_button_selecte.setSelected(true);
            four_screen_button_select.setSelected(false);

        }

        if (firstPalyer != null && firstPalyer.isPlaying()) {
            firstPalyer.pause();
            firstPalyer.stop();
        }
        if (secondPlayer != null && secondPlayer.isPlaying()) {
            secondPlayer.pause();
            secondPlayer.stop();
        }

        if (thirdPlayer != null && thirdPlayer.isPlaying()) {
            thirdPlayer.pause();
            thirdPlayer.stop();
        }

        if (fourthPlayer != null && fourthPlayer.isPlaying()) {
            fourthPlayer.pause();
            fourthPlayer.stop();
        }

        firstPlayerView.setVisibility(View.GONE);
        secondPlayerView.setVisibility(View.GONE);
        thirdPlayerView.setVisibility(View.GONE);
        fourthPlayerView.setVisibility(View.GONE);
        four_surfaceview_parent_relativelayout.setVisibility(View.GONE);
        single_surfaceview_parent_relativelayout.setVisibility(View.VISIBLE);

        single_player_layout.setVisibility(View.VISIBLE);
        singlePlayer = new NodePlayer(this);
        singlePlayer.setPlayerView(single_player_layout);
        singlePlayer.setInputUrl("rtmp://live.hkstv.hk.lxdns.com/live/hks");
        singlePlayer.setNodePlayerDelegate(this);
        singlePlayer.setAudioEnable(AppConfig.isVideoSound);
        singlePlayer.setVideoEnable(true);
        singlePlayer.start();
        fourthPlayerView.setOnClickListener(this);
    }


    @OnClick(R.id.four_screen_button_select)
    public void fourScreenVideo(View view){

        if (singlePlayer != null && singlePlayer.isPlaying()){
            singlePlayer.pause();
            singlePlayer.stop();
        }

        four_surfaceview_parent_relativelayout.setVisibility(View.VISIBLE);
        single_surfaceview_parent_relativelayout.setVisibility(View.GONE);
        single_player_layout.setVisibility(View.GONE);

        firstPlayerView.setVisibility(View.VISIBLE);
        secondPlayerView.setVisibility(View.VISIBLE);
        thirdPlayerView.setVisibility(View.VISIBLE);
        fourthPlayerView.setVisibility(View.VISIBLE);

        firstPalyer.start();
        secondPlayer.start();
        thirdPlayer.start();
        fourthPlayer.start();


    }
}
