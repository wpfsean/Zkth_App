package com.zhketech.client.zkth.app.project.pagers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridLayout;

import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.adapters.RecyclerViewGridAdapter;
import com.zhketech.client.zkth.app.project.base.BaseActivity;
import com.zhketech.client.zkth.app.project.beans.SipGroupBean;
import com.zhketech.client.zkth.app.project.callbacks.SipGroupResourcesCallback;
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

public class SipGroup extends BaseActivity {

    Context mContext;

    @BindView(R.id.sip_group_recyclearview)
    public RecyclerView sip_group_recyclearview;
    SpaceItemDecoration sp;
    List<SipGroupBean> mList = new ArrayList<>();

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
        sp = new SpaceItemDecoration(5, 20);
        getGroupStatusData();

    }
    private void getGroupStatusData() {
        if (mList != null && mList.size() > 0) {
            mList.clear();
        }

        SipGroupResourcesCallback sipGroupResourcesCallback = new SipGroupResourcesCallback(new SipGroupResourcesCallback.SipGroupDataCallback() {
            @Override
            public void callbackSuccessData(List<SipGroupBean> dataList) {
                if (dataList != null && dataList.size() > 0) {
                    mList = dataList;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerViewGridAdapter recyclerViewGridAdapter = new RecyclerViewGridAdapter(mContext, mList);
                            sip_group_recyclearview.setAdapter(recyclerViewGridAdapter);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
                            gridLayoutManager.setReverseLayout(false);
                            gridLayoutManager.setOrientation(GridLayout.VERTICAL);
                            //  sip_group_recyclearview.addItemDecoration(sp);
                            sip_group_recyclearview.setLayoutManager(gridLayoutManager);
                            recyclerViewGridAdapter.setItemClickListener(new RecyclerViewGridAdapter.MyItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Logutils.i("positon:" + position);
//                                    int group_id = mList.get(position).getGroup_id();
//                                    Intent intent = new Intent();
//                                    intent.putExtra("group_id", group_id);
//                                    intent.setClass(SipGroup.this, SipInfor.class);
//                                    startActivity(intent);
                                }
                            });
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toastShort("未从服务器上获取到sip数据...");
                        }
                    });
                }
            }
            @Override
            public void callbackFailData(String infor) {

            }
        });
        sipGroupResourcesCallback.start();
    }

}
