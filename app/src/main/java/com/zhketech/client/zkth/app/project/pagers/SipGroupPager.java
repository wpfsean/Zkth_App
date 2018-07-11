package com.zhketech.client.zkth.app.project.pagers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.adapters.ButtomSlidingAdapter;
import com.zhketech.client.zkth.app.project.adapters.RecyclerViewGridAdapter;
import com.zhketech.client.zkth.app.project.adapters.RecyclerViewGridAdapterPort;
import com.zhketech.client.zkth.app.project.base.BaseActivity;
import com.zhketech.client.zkth.app.project.beans.ButtomSlidingBean;
import com.zhketech.client.zkth.app.project.beans.SipGroupBean;
import com.zhketech.client.zkth.app.project.callbacks.SipGroupResourcesCallback;
import com.zhketech.client.zkth.app.project.global.AppConfig;
import com.zhketech.client.zkth.app.project.utils.Logutils;
import com.zhketech.client.zkth.app.project.utils.SipHttpUtils;
import com.zhketech.client.zkth.app.project.utils.SpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Root on 2018/6/29.
 * sip分组界面
 */

public class SipGroupPager extends BaseActivity {

    Context mContext;
    //展示sipGroup信息的RecyclerView
    @BindView(R.id.sip_group_recyclearview)
    public RecyclerView recyclearview;
    //存放SipGroup信息的集合
    List<SipGroupBean> mList = new ArrayList<>();
    //底部滑动的recyclerview
    RecyclerView bottomSlidingView;

    String callNumber = "";

    @Override
    public int intiLayout() {
        return R.layout.activity_sip_group;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        mContext = this;
    }

    //ftp://wang1210@wang1210.ftp-gz01.bcehost.com:8010/webroot/zhketech/dutyRoomData.php
    @Override
    public void initData() {
        SipHttpUtils sipHttpUtils = new SipHttpUtils(AppConfig.DUTY_ROOM_URL, new SipHttpUtils.GetHttpData() {
            @Override
            public void httpData(String result) {
                if (TextUtils.isEmpty(result)) {
                    return;
                }
                if (result.contains("Execption")) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    JSONObject data = jsonArray.getJSONObject(0);
                    String name = data.getString("name");
                    String number = data.getString("number");
                    String server = data.getString("server");
                    if (!TextUtils.isEmpty(number))
                        callNumber = number;
                } catch (JSONException e) {
                    callNumber = "";
                    e.printStackTrace();
                }

            }
        });
        sipHttpUtils.start();
        getSipGroupResources();
    }

    /**
     * 获取sip分组信息并展示
     */
    private void getSipGroupResources() {
        if (mList != null && mList.size() > 0) {
            mList.clear();
        }
        SipGroupResourcesCallback sipGroupResourcesCallback = new SipGroupResourcesCallback(new SipGroupResourcesCallback.SipGroupDataCallback() {
            @Override
            public void callbackSuccessData(final List<SipGroupBean> dataList) {
                if (dataList != null && dataList.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mList = dataList;
                            RecyclerViewGridAdapter recyclerViewGridAdapter = new RecyclerViewGridAdapter(mContext, mList);
                            recyclearview.setAdapter(recyclerViewGridAdapter);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
                            gridLayoutManager.setReverseLayout(false);
                            gridLayoutManager.setOrientation(GridLayout.VERTICAL);
                            recyclearview.setLayoutManager(gridLayoutManager);
                            recyclerViewGridAdapter.setItemClickListener(new RecyclerViewGridAdapter.MyItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    int group_id = mList.get(position).getGroup_id();
                                    Intent intent = new Intent();
                                    intent.putExtra("group_id", group_id);
                                    intent.setClass(SipGroupPager.this, SipInforPager.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            promptNoData();
                        }
                    });
                }
            }

            @Override
            public void callbackFailData(String infor) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        promptNoData();
//                    }
//                });
            }
        });
        sipGroupResourcesCallback.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (AppConfig.direction == 2) {
            Log.i("TAG", "横屏");
        } else if (AppConfig.direction == 1) {
            Log.i("TAG", "竖屏");
            portDisplayRecyclerView();
            bottomSlidingDisplay();
        }
    }

    /**
     * 竖屏显示的信息
     */
    private void portDisplayRecyclerView() {

        final RecyclerView rw = findViewById(R.id.sip_group_recyclearview);

        if (mList != null && mList.size() > 0) {
            mList.clear();
        }

        SipGroupResourcesCallback sipGroupThread = new SipGroupResourcesCallback(new SipGroupResourcesCallback.SipGroupDataCallback() {
            @Override
            public void callbackSuccessData(final List<SipGroupBean> mList) {
                Logutils.i("Mlist:" + mList.toString());
                if (mList != null && mList.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
                            gridLayoutManager.setReverseLayout(false);
                            gridLayoutManager.setOrientation(GridLayout.VERTICAL);
                            rw.setLayoutManager(gridLayoutManager);
                            rw.addItemDecoration(new SpaceItemDecoration(30, 40));
                            //     LinearLayoutManager 种 含有3 种布局样式  第一个就是最常用的 1.横向 , 2. 竖向,3.偏移
                            rw.setLayoutManager(gridLayoutManager);  //给RecyClerView 添加设置好的布局样式
                            RecyclerViewGridAdapterPort ada = new RecyclerViewGridAdapterPort(SipGroupPager.this, mList);
                            rw.setAdapter(ada);
                        }
                    });


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            promptNoData();
                        }
                    });
                }
            }

            @Override
            public void callbackFailData(String infor) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        promptNoData();
                    }
                });
            }
        });

        sipGroupThread.start();
    }


    /**
     * 竖屏时底部滑动的显示
     */
    private void bottomSlidingDisplay() {
        bottomSlidingView = findViewById(R.id.bottom_sliding_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        bottomSlidingView.setLayoutManager(gridLayoutManager);
        List<ButtomSlidingBean> mlist = new ArrayList<>();
        for (int i = 1; i < 101; i++) {
            mlist.add(new ButtomSlidingBean("a" + i));
        }
        Logutils.i("size:" + mlist.size());
        ButtomSlidingAdapter ada = new ButtomSlidingAdapter(mContext, mlist);
        bottomSlidingView.setAdapter(ada);

    }

    /**
     * 上一页按钮
     *
     * @param view
     */
    @OnClick(R.id.sip_group_lastpage_layout)
    public void showPreviewBtn(View view) {
        noDataPrompt("No data !!!");
    }

    /**
     * 下一页按钮
     *
     * @param view
     */
    @OnClick(R.id.sip_group_nextpage_layout)
    public void showNextBtn(View view) {
        noDataPrompt("No data !!!");
    }

    /**
     * 视频对讲
     *
     * @param view
     */
    @OnClick(R.id.video_calls_duty_room_intercom_layout)
    public void showVideoIntercomBtn(View view) {
        if (!TextUtils.isEmpty(callNumber)) {
            Intent intent = new Intent(this, SingleCallActivity.class);
            intent.putExtra("isCall", true);
            intent.putExtra("userName", callNumber);
            intent.putExtra("isVideo", true);
            startActivity(intent);
            SipGroupPager.this.finish();
        }else {
            Toast.makeText(SipGroupPager.this,"No Number",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 语音对讲
     *
     * @param view
     */
    @OnClick(R.id.voice_calls_duty_room_intercom_layout)
    public void showVoiceIntercomBtn(View view) {
        if (!TextUtils.isEmpty(callNumber)) {
            Intent intent = new Intent(this, SingleCallActivity.class);
            intent.putExtra("isCall", true);
            intent.putExtra("userName", callNumber);
            intent.putExtra("isVideo", true);
            startActivity(intent);
            SipGroupPager.this.finish();
        }else {
            Toast.makeText(SipGroupPager.this,"No Number",Toast.LENGTH_SHORT).show();
        }
    }


    private void noDataPrompt(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SipGroupPager.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
