package com.shiliu.caishifu.viewholder;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shiliu.caishifu.R;

import static com.shiliu.caishifu.adapter.FeedImageAdapter.ITEM_ADD;


public class FeedImageViewHolder extends RecyclerView.ViewHolder {

    private SimpleDraweeView simpleDraweeView;
    private Context context;

    public FeedImageViewHolder(View itemView) {
        super(itemView);
        simpleDraweeView = itemView.findViewById(R.id.image);
        context = itemView.getContext();
    }

    public void bindView(String url) {
        if (url.equals(ITEM_ADD)) {
            Uri uri = Uri.parse("res://" + context.getPackageName() + "/" + R.mipmap.ic_add_pic);
            simpleDraweeView.setImageURI(uri);
        } else {
            simpleDraweeView.setImageURI(url);
        }
    }
}
