package com.zhketech.client.zkth.app.project.pagers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.adapters.ButtomSlidingAdapter;
import com.zhketech.client.zkth.app.project.adapters.RecyclerViewGridAdapter;
import com.zhketech.client.zkth.app.project.base.BaseActivity;
import com.zhketech.client.zkth.app.project.beans.ButtomSlidingBean;
import com.zhketech.client.zkth.app.project.beans.SipGroupBean;
import com.zhketech.client.zkth.app.project.callbacks.SipGroupResourcesCallback;
import com.zhketech.client.zkth.app.project.global.AppConfig;
import com.zhketech.client.zkth.app.project.utils.Logutils;
import com.zhketech.client.zkth.app.project.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Root on 2018/6/29.
 * sip分组界面
 */

public class SipGroupPager extends BaseActivity {


    Context mContext;
    @BindView(R.id.sip_group_recyclearview)
    public RecyclerView recyclearview;
    List<SipGroupBean> mList = new ArrayList<>();


    RecyclerView bottomSlidingView;

    @Override
    public int intiLayout() {
        return R.layout.activity_sip_group;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        mContext = this;

    }

    @Override
    public void initData() {
        getSipGroupResources();
    }

    private void getSipGroupResources() {
        if (mList != null && mList.size() > 0) {
            mList.clear();
        }
        SipGroupResourcesCallback sipGroupResourcesCallback = new SipGroupResourcesCallback(new SipGroupResourcesCallback.SipGroupDataCallback() {
            @Override
            public void callbackSuccessData(List<SipGroupBean> dataList) {
                if (dataList != null && dataList.size() > 0) {
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
                } else {
                    promptNoData();
                }
            }

            @Override
            public void callbackFailData(String infor) {
                promptNoData();
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
            getPortSipGroupResources();
        }
    }
    private void getPortSipGroupResources() {
        bottomSlidingView = findViewById(R.id.bottom_sliding_recyclerview);
       GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,1);
       gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        bottomSlidingView.setLayoutManager(gridLayoutManager);
        List<ButtomSlidingBean> mlist = new ArrayList<>();
        for (int i = 1;i<101;i++){
            mlist.add(new ButtomSlidingBean("a"+i));
        }
        Logutils.i("size:"+mlist.size());
        ButtomSlidingAdapter ada = new ButtomSlidingAdapter(mContext,mlist);
        bottomSlidingView.setAdapter(ada);

    }

}
