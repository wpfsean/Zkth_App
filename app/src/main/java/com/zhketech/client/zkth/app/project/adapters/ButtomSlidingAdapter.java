package com.zhketech.client.zkth.app.project.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.beans.ButtomSlidingBean;

import java.util.List;

/**
 * Created by Root on 2018/7/9.
 */

public class ButtomSlidingAdapter extends RecyclerView.Adapter<ButtomSlidingAdapter.ViewHolder> {

    Context context;
    List<ButtomSlidingBean> dataList;

    public ButtomSlidingAdapter(Context context, List<ButtomSlidingBean> dataList) {
        this.context = context;
        this.dataList = dataList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.function3_button_activity, null);
        //实例化ViewHolder
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.t.setText(dataList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView t;

        public ViewHolder(View itemView) {
            super(itemView);
//            t = itemView.findViewById(R.id.item_text_intercom_button_layout);
        }
    }
}
