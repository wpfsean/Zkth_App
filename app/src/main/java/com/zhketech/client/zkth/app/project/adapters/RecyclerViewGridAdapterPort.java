package com.zhketech.client.zkth.app.project.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.beans.SipGroupBean;
import com.zhketech.client.zkth.app.project.utils.Logutils;

import java.util.List;

/**
 * SipGroup分组竖屏的适配器
 * <p>
 * Created by Root on 2018/5/21.
 */

public class RecyclerViewGridAdapterPort extends RecyclerView.Adapter<RecyclerViewGridAdapterPort.GridViewHolder> {
    private Context mContext;
    private List<SipGroupBean> mDateBeen;

    public RecyclerViewGridAdapterPort(Context context, List<SipGroupBean> dateBeen) {
        this.mContext = context;
        this.mDateBeen = dateBeen;

        Logutils.i("SBBBBBBBBBBBBBBB:"+mDateBeen.toString());
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.sip_group_recyclearview_item_port, null);
        GridViewHolder holder = new GridViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        holder.tv.setText("ddddddddd");
    }

    @Override
    public int getItemCount() {
        return 6;

    }


    public class GridViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public GridViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.show_sip_group_name);
        }
    }
}
