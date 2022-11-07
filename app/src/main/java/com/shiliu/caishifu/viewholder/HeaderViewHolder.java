package com.shiliu.caishifu.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shiliu.caishifu.R;

/**
 * 头ViewHolder
 *
 */
public class HeaderViewHolder extends RecyclerView.ViewHolder {

    // 头像
    public SimpleDraweeView mAvatarSdv;
    // 昵称
    public TextView mNickNameTv;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        mAvatarSdv = itemView.findViewById(R.id.sdv_avatar);
        mNickNameTv = itemView.findViewById(R.id.tv_nick_name);
    }

}