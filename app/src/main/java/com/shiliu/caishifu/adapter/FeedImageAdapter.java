package com.shiliu.caishifu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import com.shiliu.caishifu.R;
import com.shiliu.caishifu.listener.OnItemClickListener;
import com.shiliu.caishifu.viewholder.FeedImageViewHolder;

import java.util.List;


public class FeedImageAdapter extends RecyclerView.Adapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> datas;

    public static final String ITEM_ADD = "item_add";

    public static final int TYPE_ADD = 0;
    public static final int TYPE_PIC = 1;

    private OnItemClickListener itemClickListener;

    public FeedImageAdapter(Context context, List<String> images) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        datas = images;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.feed_image_item, parent, false);
        FeedImageViewHolder viewHolder = new FeedImageViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        FeedImageViewHolder viewHolder = (FeedImageViewHolder) holder;
        viewHolder.bindView(datas.get(position));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (datas == null || datas.size() == 0) return 0;
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (datas.get(position).equals(ITEM_ADD)) {
            return TYPE_ADD;
        }
        return TYPE_PIC;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
