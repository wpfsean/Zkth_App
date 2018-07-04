package com.zhketech.client.zkth.app.project.pagers;

import android.content.res.Configuration;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.base.BaseActivity;
import com.zhketech.client.zkth.app.project.beans.VideoBen;
import com.zhketech.client.zkth.app.project.callbacks.SendAlarmToServer;
import com.zhketech.client.zkth.app.project.global.AppConfig;
import com.zhketech.client.zkth.app.project.onvif.Device;
import com.zhketech.client.zkth.app.project.onvif.MediaProfile;
import com.zhketech.client.zkth.app.project.utils.ControlPtz;
import com.zhketech.client.zkth.app.project.utils.GsonUtils;
import com.zhketech.client.zkth.app.project.utils.Logutils;
import com.zhketech.client.zkth.app.project.utils.PageModel;
import com.zhketech.client.zkth.app.project.utils.SharedPreferencesUtils;

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

public class MutilScreenPager extends BaseActivity implements ViewPager.OnPageChangeListener, NodePlayerDelegate, View.OnClickListener, View.OnTouchListener {

    //当前的屏幕状态（true 横屏， false 竖屏）
    boolean isLand = true;
    PageModel pm;//分页加载器
    PageModel singlePm;//分页加载器;//分页加载器
    int videoCurrentPage = 1;//当前页码

    //数据集合
    List<Device> devicesList = new ArrayList<>();
    //当前四屏的数据集合
    List<Device> currentList = new ArrayList<>();
    //单屏时要播放的数据
    List<Device> currentSingleList = new ArrayList<>();
    //竖屏底总横向滑动的viewPager
    ViewPager bottomViewPager;
    //单屏的NodeMediaclient播放器
    NodePlayer singlePlayer;
    NodePlayer firstPalyer, secondPlayer, thirdPlayer, fourthPlayer;
    private List<View> views;

    //单屏显示的player布局
    @BindView(R.id.single_player_layout)
    NodePlayerView single_player_layout;
    //第一个视频的view
    @BindView(R.id.first_player_layout)
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
    //第二个视频的view
    @BindView(R.id.second_player_layout)
    NodePlayerView secondPlayerView;
    //第二个视频所在的背景而
    @BindView(R.id.second_surfaceview_relativelayout)
    public RelativeLayout second_surfaceview_relativelayout;
    @BindView(R.id.second_pr_layout)
    ProgressBar second_pr_layout;
    //第一个视频 的loading
    @BindView(R.id.seond_dispaly_loading_layout)
    TextView second_dispaly_loading_layout;

    //第三个视频的view
    @BindView(R.id.third_player_layout)
    NodePlayerView thirdPlayerView;
    //第三个视频所在的背景而
    @BindView(R.id.third_surfaceview_relativelayout)
    public RelativeLayout third_surfaceview_relativelayout;

    //第三个视频 的progressbar
    @BindView(R.id.third_pr_layout)
    ProgressBar third_pr_layout;
    //第一个视频 的loading
    @BindView(R.id.third_dispaly_loading_layout)
    TextView third_dispaly_loading_layout;

    //第四个视频的view
    @BindView(R.id.fourth_player_layout)
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

    //单屏播放时的progressbar
    @BindView(R.id.single_player_progressbar_layout)
    ProgressBar single_player_progressbar_layout;
    //单屏时显示 的Loading
    @BindView(R.id.dispaly_video_loading_layout)
    TextView dispaly_video_loading_layout;
    //显示视频信息的Textview
    @BindView(R.id.display_video_information_text_layout)
    TextView display_video_information_text_layout;

    //上下左右四个键
    @BindView(R.id.video_ptz_up)
    ImageButton video_ptz_up;
    @BindView(R.id.video_ptz_down)
    ImageButton video_ptz_down;
    @BindView(R.id.video_ptz_left)
    ImageButton video_ptz_left;
    @BindView(R.id.video_ptz_right)
    ImageButton video_ptz_right;
    //放大缩小键盘
    @BindView(R.id.video_zoomout_button)
    ImageButton video_zoomout_button;
    @BindView(R.id.video_zoombig_button)
    ImageButton video_zoombig_button;

    //当前状态是四分屏
    boolean isCurrentFourScreen = true;
    //当前状态是单屏
    boolean isCurrentSingleScreen = false;
    //判断这四个视频 中否被选中
    boolean firstViewSelect = false;
    boolean secondViewSelect = false;
    boolean thirdViewSelect = false;
    boolean fourthViewSelect = false;

    //竖屏内四个之一按键
    ImageButton single_screen_button_selecte;
    ImageButton four_screen_button_select;

    //四屏所在 的父布局
    @BindView(R.id.four_surfaceview_parent_relativelayout)
    RelativeLayout four_surfaceview_parent_relativelayout;
    //单屏所在的父布局
    @BindView(R.id.single_surfaceview_parent_relativelayout)
    RelativeLayout single_surfaceview_parent_relativelayout;


    @Override
    public int intiLayout() {
        return R.layout.activity_multi_screen;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        video_ptz_up.setOnTouchListener(this);
        video_ptz_down.setOnTouchListener(this);
        video_ptz_left.setOnTouchListener(this);
        video_ptz_right.setOnTouchListener(this);
        video_zoomout_button.setOnTouchListener(this);
        video_zoombig_button.setOnTouchListener(this);

        firstPalyer = new NodePlayer(this);
        firstPalyer.setPlayerView(firstPlayerView);
        firstPlayerView.setOnClickListener(this);

        secondPlayer = new NodePlayer(this);
        secondPlayer.setPlayerView(secondPlayerView);
        secondPlayerView.setOnClickListener(this);


        thirdPlayer = new NodePlayer(this);
        thirdPlayer.setPlayerView(thirdPlayerView);
        thirdPlayerView.setOnClickListener(this);

        fourthPlayer = new NodePlayer(this);
        fourthPlayer.setPlayerView(fourthPlayerView);
        fourthPlayerView.setOnClickListener(this);

    }


    @Override
    public void initData() {
        //取出事先解析好的数据
        String dataSources = (String) SharedPreferencesUtils.getObject(MutilScreenPager.this, "result", "");
        if (TextUtils.isEmpty(dataSources)) {
            promptNoData();
            return;
        }
        List<Device> mlist = GsonUtils.getGsonInstace().str2List(dataSources);
        if (mlist != null && mlist.size() > 0) {
            devicesList = mlist;
        }
        //初始页面显示的四屏数据
        pm = new PageModel(devicesList, 4);
        currentList = pm.getObjects(videoCurrentPage);
        //初始页面单屏的数据
        singlePm = new PageModel(devicesList, 1);
        currentSingleList = singlePm.getObjects(videoCurrentPage);
        initPlayer();
    }

    //初始化四个播放器
    private void initPlayer() {

        if (currentList.size() == 4) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    first_pr_layout.setVisibility(View.VISIBLE);
                    first_dispaly_loading_layout.setVisibility(View.VISIBLE);
                    first_dispaly_loading_layout.setText("Loading...");

                    second_pr_layout.setVisibility(View.VISIBLE);
                    second_dispaly_loading_layout.setVisibility(View.VISIBLE);
                    second_dispaly_loading_layout.setText("Loading...");

                    third_pr_layout.setVisibility(View.VISIBLE);
                    third_dispaly_loading_layout.setVisibility(View.VISIBLE);
                    third_dispaly_loading_layout.setText("Loading...");

                    fourth_pr_layout.setVisibility(View.VISIBLE);
                    fourth_dispaly_loading_layout.setVisibility(View.VISIBLE);
                    fourth_dispaly_loading_layout.setText("Loading...");
                }
            });


            String rtsp1 = "";
            if (!TextUtils.isEmpty(currentList.get(0).getRtspUrl())) {
                rtsp1 = currentList.get(0).getRtspUrl();
            } else {
                rtsp1 = "";
            }
            String rtsp2 = "";
            if (!TextUtils.isEmpty(currentList.get(1).getRtspUrl())) {
                rtsp2 = currentList.get(1).getRtspUrl();
            } else {
                rtsp2 = "";
            }
            String rtsp3 = "";
            if (!TextUtils.isEmpty(currentList.get(2).getRtspUrl())) {
                rtsp3 = currentList.get(2).getRtspUrl();
            } else {
                rtsp3 = "";
            }
            String rtsp4 = "";
            if (!TextUtils.isEmpty(currentList.get(3).getRtspUrl())) {
                rtsp4 = currentList.get(3).getRtspUrl();
            } else {
                rtsp4 = "";
            }

            Logutils.i("rtsp1:" + rtsp1 + "\n" + "rtsp2:" + rtsp2 + "\n" + "rtsp3:" + rtsp3 + "\n" + "rtsp4:" + rtsp4);

            if (firstPalyer != null && firstPalyer.isPlaying()) {
                firstPalyer.stop();
            }
            if (secondPlayer != null && secondPlayer.isPlaying()) {
                secondPlayer.stop();
            }
            if (thirdPlayer != null && thirdPlayer.isPlaying()) {
                thirdPlayer.stop();
            }
            if (fourthPlayer != null && fourthPlayer.isPlaying()) {
                fourthPlayer.stop();
            }
            firstPalyer.setInputUrl(rtsp1);
            firstPalyer.setNodePlayerDelegate(this);
            firstPalyer.setAudioEnable(AppConfig.isVideoSound);
            firstPalyer.setVideoEnable(true);
            firstPalyer.start();
            secondPlayer.setInputUrl(rtsp2);
            secondPlayer.setNodePlayerDelegate(this);
            secondPlayer.setAudioEnable(AppConfig.isVideoSound);
            secondPlayer.setVideoEnable(true);
            secondPlayer.start();
            thirdPlayer.setInputUrl(rtsp3);
            thirdPlayer.setNodePlayerDelegate(this);
            thirdPlayer.setAudioEnable(AppConfig.isVideoSound);
            thirdPlayer.setVideoEnable(true);
            thirdPlayer.start();
            fourthPlayer.setInputUrl(rtsp4);
            fourthPlayer.setNodePlayerDelegate(this);
            fourthPlayer.setAudioEnable(AppConfig.isVideoSound);
            fourthPlayer.setVideoEnable(true);
            fourthPlayer.start();
        }
    }

    /**
     * 视频资源向下翻页
     */
    @OnClick(R.id.video_nextpage_button)
    public void videoNextPage(View view) {
        videoCurrentPage++;
        if (isCurrentFourScreen) {
            if (isCurrentFourScreen) {
                if (pm != null && pm.isHasNextPage()) {
                    currentList = pm.getObjects(videoCurrentPage);
                    initPlayer();
                }
            }
        }
        if (isCurrentSingleScreen) {
            if (singlePm != null && singlePm.isHasNextPage()) {
                currentSingleList = singlePm.getObjects(videoCurrentPage);
                String rtsp = "";
                if (!TextUtils.isEmpty(currentSingleList.get(0).getRtspUrl())) {
                    rtsp = currentSingleList.get(0).getRtspUrl();
                }
                initSinglePlayer(rtsp);
            }
        }


    }

    /**
     * 视频资源向上翻页
     */
    @OnClick(R.id.video_previous_button)
    public void videoPreviousPage(View view) {
        videoCurrentPage--;
        if (isCurrentFourScreen) {
            if (pm != null && pm.isHasPreviousPage()) {
                currentList = pm.getObjects(videoCurrentPage);
                initPlayer();
            }
        }

        if (isCurrentSingleScreen) {
            if (singlePm != null && singlePm.isHasPreviousPage()) {
                currentSingleList = singlePm.getObjects(videoCurrentPage);
                String rtsp = "";
                if (!TextUtils.isEmpty(currentSingleList.get(0).getRtspUrl())) {
                    rtsp = currentSingleList.get(0).getRtspUrl();
                }
                initSinglePlayer(rtsp);
            } else {
                videoCurrentPage = 1;
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i("TAG", "///Start");
    }

    //切换竖屏时会走以下的方法
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (AppConfig.direction == 2) {
            Log.i("TAG", "横屏时不走");
        } else if (AppConfig.direction == 1) {
            isCurrentSingleScreen = false;
            isCurrentFourScreen = true;
            initViewPagerData();
            initView();
            isLand = false;
            initPlayer();
            single_screen_button_selecte = this.findViewById(R.id.single_screen_button_selecte);
            four_screen_button_select = this.findViewById(R.id.four_screen_button_select);
            four_screen_button_select.setSelected(true);
            single_screen_button_selecte.setSelected(false);

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
        }
        if (secondPlayer == player) {
            if (event == 1001) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        second_pr_layout.setVisibility(View.GONE);
                        second_dispaly_loading_layout.setVisibility(View.GONE);
                    }
                });
            } else if (event == 1003) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        second_dispaly_loading_layout.setVisibility(View.VISIBLE);
                        second_pr_layout.setVisibility(View.GONE);
                        second_dispaly_loading_layout.setText(msg);
                    }
                });
            }
        }


        if (thirdPlayer == player) {
            if (event == 1001) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        third_pr_layout.setVisibility(View.GONE);
                        third_dispaly_loading_layout.setVisibility(View.GONE);
                    }
                });
            } else if (event == 1003) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        third_dispaly_loading_layout.setVisibility(View.VISIBLE);
                        third_pr_layout.setVisibility(View.GONE);
                        third_dispaly_loading_layout.setText(msg);
                    }
                });
            }
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
        }
        if (singlePlayer == player) {
            if (event == 1001) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        single_player_progressbar_layout.setVisibility(View.GONE);
                        dispaly_video_loading_layout.setVisibility(View.GONE);
                    }
                });
            } else if (event == 1003) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dispaly_video_loading_layout.setVisibility(View.VISIBLE);
                        single_player_progressbar_layout.setVisibility(View.GONE);
                        dispaly_video_loading_layout.setText(msg);
                    }
                });
            }

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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {

            case R.id.video_zoomout_button:
                String ptz = "";
                String token = "";
                if (isCurrentSingleScreen) {
                    ptz = currentSingleList.get(0).getPtzUrl();
                    token = currentSingleList.get(0).getVideoBen().getToken();
                    if (TextUtils.isEmpty(ptz) && TextUtils.isEmpty(token)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toastShort("No support ptz");
                            }
                        });
                    } else {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            ControlPtz controlPtz = new ControlPtz(ptz, token, "stop", 0.00, 0.00);
                            controlPtz.start();
                        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            ControlPtz controlPtz = new ControlPtz(ptz, token, "zoom_s", -0.3, 0.00);
                            controlPtz.start();
                        }
                    }
                }

                break;

            case R.id.video_zoombig_button:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ControlPtz controlPtz = new ControlPtz("http://19.0.0.224/onvif/ptz_service", "000", "stop", 0.00, 0.00);
                    controlPtz.start();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ControlPtz controlPtz = new ControlPtz("http://19.0.0.224/onvif/ptz_service", "000", "zoom_b", 0.3, 0.00);
                    controlPtz.start();
                }

                break;
            case R.id.video_ptz_up:
                isNoselected();
                if (firstViewSelect) {
                    ptzMoveUp(event, firstViewSelect, 0);
                }
                if (secondViewSelect) {
                    ptzMoveUp(event, secondViewSelect, 1);
                }
                if (thirdViewSelect) {
                    ptzMoveUp(event, thirdViewSelect, 2);
                }
                if (fourthViewSelect) {
                    ptzMoveUp(event, fourthViewSelect, 3);
                }
                break;
            case R.id.video_ptz_down:
                isNoselected();

                break;
            case R.id.video_ptz_left:
                break;
            case R.id.video_ptz_right:
                break;
        }

        return false;
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

    //退出本页面
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

    //single screen play video
    @OnClick(R.id.single_screen_button_selecte)
    public void singleScreenVideo(View view) {

        isCurrentFourScreen = false;
        isCurrentSingleScreen = true;

        if (isLand == false) {
            four_screen_button_select.setSelected(false);
            single_screen_button_selecte.setSelected(true);
        }


        if (firstViewSelect) {
            String rtsp = "";
            if (!TextUtils.isEmpty(currentList.get(0).getRtspUrl())) {
                rtsp = currentList.get(0).getRtspUrl();
            }
            initSinglePlayer(rtsp);
        }
        if (secondViewSelect) {
            String rtsp = "";
            if (!TextUtils.isEmpty(currentList.get(1).getRtspUrl())) {
                rtsp = currentList.get(1).getRtspUrl();
            }
            initSinglePlayer(rtsp);
        }

        if (thirdViewSelect) {
            String rtsp = "";
            if (!TextUtils.isEmpty(currentList.get(2).getRtspUrl())) {
                rtsp = currentList.get(2).getRtspUrl();
            }
            initSinglePlayer(rtsp);
        }

        if (fourthViewSelect) {
            String rtsp = "";
            if (!TextUtils.isEmpty(currentList.get(3).getRtspUrl())) {
                rtsp = currentList.get(3).getRtspUrl();
            }
            initSinglePlayer(rtsp);
        }


    }

    //单屏播放
    private void initSinglePlayer(String rtsp) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dispaly_video_loading_layout.setVisibility(View.VISIBLE);
                single_player_progressbar_layout.setVisibility(View.VISIBLE);
                dispaly_video_loading_layout.setText("Loading");
            }
        });

        if (singlePlayer != null && singlePlayer.isPlaying()) {
            singlePlayer.pause();
            singlePlayer.stop();
        }

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
        singlePlayer.setInputUrl(rtsp);
        singlePlayer.setNodePlayerDelegate(this);
        singlePlayer.setAudioEnable(AppConfig.isVideoSound);
        singlePlayer.setVideoEnable(true);
        singlePlayer.start();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                display_video_information_text_layout.setText(currentSingleList.get(0).getVideoBen().getName());
            }
        });
        single_player_layout.setOnClickListener(this);
    }

    //four screen play video
    @OnClick(R.id.four_screen_button_select)
    public void fourScreenVideo(View view) {

        isCurrentSingleScreen = false;
        isCurrentFourScreen = true;

        if (singlePlayer != null && singlePlayer.isPlaying()) {
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
        if (isLand == false) {
            four_screen_button_select.setSelected(true);
            single_screen_button_selecte.setSelected(false);
        }


    }


    //未选中的状态
    public void isNoselected() {
        if (firstViewSelect == false && secondViewSelect == false && thirdViewSelect == false && fourthViewSelect == false) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toastShort("Please select one window");
                    return;
                }
            });
        }
    }

    //ptz云台向上转动
    private void ptzMoveUp(MotionEvent event, boolean whichSelect, int posion) {
        if (whichSelect) {
            String firstPtzUrl = currentList.get(posion).getPtzUrl();
            if (!TextUtils.isEmpty(firstPtzUrl)) {
                ArrayList<MediaProfile> m = currentList.get(posion).getProfiles();
                if (m.size() > 0) {
                    String token = m.get(0).getToken();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        ControlPtz controlPtz = new ControlPtz(firstPtzUrl, m.get(1).getToken(),
                                "stop", 0.00, 0.00);
                        controlPtz.start();
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ControlPtz controlPtz = new ControlPtz(firstPtzUrl, m.get(1).getToken(), "top", 0.00, 0.01);
                        controlPtz.start();
                    }
                }
            }
        } else {
            toastShort("This video is not support Ptz!");
            return;
        }

    }


    //报警
    @OnClick(R.id.send_alarmtoServer_button)
    public void sendAlarmToServer(View view) {

            if (isCurrentSingleScreen) {
                if (currentSingleList != null && currentSingleList.size() > 0) {
                    VideoBen v = currentSingleList.get(0).getVideoBen();
                    SendAlarmToServer sendAlarmToServer = new SendAlarmToServer(v);
                    sendAlarmToServer.start();
                }
            }
            if (isCurrentFourScreen) {

                if (firstViewSelect) {
                    sendToAlarm(1);
                } else if (secondViewSelect) {
                    sendToAlarm(2);
                } else if (thirdViewSelect) {
                    sendToAlarm(3);
                } else if (fourthViewSelect) {
                    sendToAlarm(4);
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toastShort("please select one window");
                        }
                    });
                }
            }
    }
    //报警
    private void sendToAlarm(int tag) {

        VideoBen v = null;
        if (currentList != null && currentList.size() > 0) {
            if (tag == 1) {
                v = currentList.get(0).getVideoBen();
            } else if (tag == 2) {
                v = currentList.get(1).getVideoBen();
            } else if (tag == 3) {
                v = currentList.get(2).getVideoBen();
            } else if (tag == 4) {
                v = currentList.get(3).getVideoBen();
            }
            if (v == null){return;}
            SendAlarmToServer sendAlarmToServer = new SendAlarmToServer(v);
            sendAlarmToServer.start();
        }
    }

}
