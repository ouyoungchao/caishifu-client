package com.shiliu.caishifu.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.widget.CommentsView;
import com.shiliu.caishifu.widget.ExpandTextView;
import com.shiliu.caishifu.widget.MomentsLikeListView;

/**
 * 菜市场基础ViewHolder
 *
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    // 发布人头像
    public SimpleDraweeView mAvatarSdv;
    // 发布人昵称
    public TextView mNickNameTv;
    // 文字内容
    public ExpandTextView mContentEtv;
    // 发布时间
    public TextView mTimeTv;
    // 删除
    public TextView mDeleteTv;
    // 点赞评论按钮
    public ImageView mCommentIv;
    // 点赞评论
    public LinearLayout mLikeAndCommentLl;
    // 点赞列表
    public MomentsLikeListView mLikeLv;
    // 评论列表
    public CommentsView mCommentsCv;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mAvatarSdv = itemView.findViewById(R.id.sdv_avatar);
        mNickNameTv = itemView.findViewById(R.id.tv_nick_name);
        mContentEtv = itemView.findViewById(R.id.etv_content);
        mTimeTv = itemView.findViewById(R.id.tv_time);
        mDeleteTv = itemView.findViewById(R.id.tv_delete);
        mCommentIv = itemView.findViewById(R.id.iv_comment);
        mLikeLv = itemView.findViewById(R.id.lv_like);
        mCommentsCv = itemView.findViewById(R.id.cv_comment);
        mLikeAndCommentLl = itemView.findViewById(R.id.ll_like_and_comment);
    }

}