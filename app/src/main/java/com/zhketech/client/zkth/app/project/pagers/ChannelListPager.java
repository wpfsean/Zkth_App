package com.zhketech.client.zkth.app.project.pagers;

import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.adapters.ChannelListRecycleViewAdapter;
import com.zhketech.client.zkth.app.project.onvif.Device;
import com.zhketech.client.zkth.app.project.utils.GsonUtils;
import com.zhketech.client.zkth.app.project.utils.Logutils;
import com.zhketech.client.zkth.app.project.utils.SharedPreferencesUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChannelListPager extends AppCompatActivity {

    //recyclevire
    @BindView(R.id.channel_list_layout)
    RecyclerView recyclerView;
    //adapter
    ChannelListRecycleViewAdapter adapter;
    //盛放数据的集合
    List<Device> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //remove actionbar
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        hideStatusBar();
        setContentView(R.layout.activity_channel_list_pager);
        ButterKnife.bind(this);
        initData();
    }

    /**
     *获取本地数据并显示
     */
    private void initData() {
        String dataSources = (String) SharedPreferencesUtils.getObject(ChannelListPager.this, "result", "");
        if (TextUtils.isEmpty(dataSources)) {
            return;
        }
        List<Device> mlist = GsonUtils.getGsonInstace().str2List(dataSources);
        if (mlist != null && mlist.size() > 0) {
            dataList = mlist;
            adapter = new ChannelListRecycleViewAdapter(ChannelListPager.this, dataList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(adapter);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChannelListPager.this,"No data !!!",Toast.LENGTH_SHORT).show();
                }
            });
            Logutils.i("NoData");
        }
    }

    /**
     * finish this pager
     */
    @OnClick(R.id.finish_back_layout)
    public void finishPager(View view) {
        ChannelListPager.this.finish();
    }

    /**
     * refresh this pager data and show
     */
    @OnClick(R.id.channel_refresh)
    public void refresh(View view) {
        initData();
        Toast.makeText(ChannelListPager.this, "已刷新！", Toast.LENGTH_SHORT).show();
    }

    /**
     *  start preview video
     */
    @OnClick(R.id.start_play_video_layout)
    public void startPreview(View view) {

        int previewDataCount = adapter.previewData.size();
        Logutils.i("Count:" + previewDataCount);
        if (previewDataCount < 4) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChannelListPager.this, "请复选四个选项！！！", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        for (Device d : adapter.previewData) {
            Logutils.i("ssL:" + d.toString());
        }
        Intent intent = new Intent();
        intent.setClass(ChannelListPager.this, MutilScreenPager.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("previewdata", (Serializable) adapter.previewData);
        ChannelListPager.this.startActivity(intent);
        ChannelListPager.this.finish();
    }

    /**
     * hide title status
     */
    protected void hideStatusBar() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.INVISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
