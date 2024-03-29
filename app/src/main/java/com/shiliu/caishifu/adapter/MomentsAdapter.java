package com.shiliu.caishifu.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.image.ImageWatcherHelper;
import com.shiliu.caishifu.listener.MomentsListener;
import com.shiliu.caishifu.model.Moments;
import com.shiliu.caishifu.model.MomentsComment;
import com.shiliu.caishifu.model.MomentsType;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.moments.TextViewHolder;
import com.shiliu.caishifu.utils.CollectionUtils;
import com.shiliu.caishifu.utils.PreferencesUtil;
import com.shiliu.caishifu.utils.TimeUtil;
import com.shiliu.caishifu.viewholder.BaseViewHolder;
import com.shiliu.caishifu.viewholder.TableViewHolder;
import com.shiliu.caishifu.widget.CommentsView;
import com.shiliu.caishifu.widget.MomentsLikeListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜市场
 *
 */
public class MomentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MomentsAdapter";
    
    // header
    public static final int TYPE_HEADER = 0;
    // 文本
    public static final int TYPE_TEXT = 1;
    // 表单
    public static final int TYPE_TABLE = 2;
    // 视频
    public static final int TYPE_VIDEO = 3;
    // 网页
    public static final int TYPE_WEB = 4;

    private View mHeaderView;

    private Activity mContent;
    private List<Moments> mMomentsList;
    MomentsListener mMomentsListener;

    private RequestOptions mRequestOptions;
    private DrawableTransitionOptions mDrawableTransitionOptions;
    private ImageWatcherHelper iwHelper;
    User mUser;

    public MomentsAdapter(List<Moments> mMomentsList, Activity context, MomentsListener momentsListener) {
        mContent = context;
        mUser = PreferencesUtil.getInstance().getUser();
        this.mMomentsList = mMomentsList;
        this.mMomentsListener = momentsListener;
        this.mRequestOptions = new RequestOptions().centerCrop();
        this.mDrawableTransitionOptions = DrawableTransitionOptions.withCrossFade();
    }

    public void setData(List<Moments> momentsList) {
        this.mMomentsList = momentsList;
        notifyDataSetChanged();
    }

    public void setIwHelper(ImageWatcherHelper iwHelper) {
        this.iwHelper = iwHelper;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TABLE) {
            return new TableViewHolder(mContent.getLayoutInflater().inflate(R.layout.moments_table_fragement, parent, false));
        } /*else if (viewType == TYPE_VIDEO) {
            return new VideoViewHolder(mContent.getLayoutInflater().inflate(R.layout.item_my_moments_video, parent, false));
        } else if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(mContent.getLayoutInflater().inflate(R.layout.item_my_moments_header, parent, false));
        }*/ else {
            // 默认text
            return new TextViewHolder(mContent.getLayoutInflater().inflate(R.layout.moments_text_fragment, parent, false));
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        Log.d(TAG, "getRealPosition: "+ position + " and mHeaderView = " + mHeaderView);
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int pos) {
        Log.d(TAG, "onBindViewHolder: " + pos);
        final int position = getRealPosition(viewHolder);
        Moments moments = mMomentsList.get(position);
        if (viewHolder instanceof TextViewHolder) {
            //将数据添加到布局中
            TextViewHolder textViewHolder = (TextViewHolder) viewHolder;
        } else if (viewHolder instanceof TableViewHolder) {
            //将数据添加到另一个布局中
            final TableViewHolder tableViewHolder = (TableViewHolder) viewHolder;
            tableViewHolder.momentTable.setVisibility(View.VISIBLE);
            setMeasuredDimension(tableViewHolder.momentTable, moments.getProducts().size());
            MomentItemAdapter momentItemAdapter = new MomentItemAdapter(mContent, moments.getProducts());
            tableViewHolder.momentItemList.setAdapter(momentItemAdapter);
            momentItemAdapter.notifyDataSetChanged();
        }
        // 处理公用部分
        final BaseViewHolder baseViewHolder = (BaseViewHolder) viewHolder;
        // 头像
        if (!TextUtils.isEmpty(moments.getUserAvatar())) {
            baseViewHolder.mAvatarSdv.setImageURI(Uri.parse(moments.getUserAvatar()));
        }
        // 昵称
        baseViewHolder.mNickNameTv.setText(moments.getUserNickName());
        baseViewHolder.mAvatarSdv.setOnClickListener(view -> toUserInfo(moments.getUserId()));
        baseViewHolder.mNickNameTv.setOnClickListener(view -> toUserInfo(moments.getUserId()));
        // 内容
        baseViewHolder.mContentEtv.setText(moments.getContent());
        if (!CollectionUtils.isEmpty(moments.getLikeUserList())
                || !CollectionUtils.isEmpty(moments.getMomentsCommentList())) {
            baseViewHolder.mLikeAndCommentLl.setVisibility(View.VISIBLE);
            // 点赞列表
            if (!CollectionUtils.isEmpty(moments.getLikeUserList())) {
                baseViewHolder.mLikeLv.setVisibility(View.VISIBLE);

                baseViewHolder.mLikeLv.setData(moments.getLikeUserList());
                baseViewHolder.mLikeLv.setOnItemClickListener(new MomentsLikeListView.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        Log.d(TAG, "onClick: mLikeLv.setOnItemClickListener");
//                        onclickUser(mList.get(position).getFabulous().get(position).getUserid() + "");
                    }
                });
            } else {
                baseViewHolder.mLikeLv.setVisibility(View.GONE);
            }
            // 评论列表
            if (!CollectionUtils.isEmpty(moments.getMomentsCommentList())) {
                baseViewHolder.mCommentsCv.setVisibility(View.VISIBLE);
                baseViewHolder.mCommentsCv.setData(moments.getMomentsCommentList());
                baseViewHolder.mCommentsCv.setOnCommentListener(new CommentsView.CommentListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void CommentClick(View view, int position, MomentsComment momentsComment) {
                        Log.d(TAG, "CommentClick: ");
                        //如果点击得 是自己
                        if (momentsComment.getUserId().equals(mUser.getUserId())) {
                            //如果是自己发的，可以删除,请求网络，返回数据刷新页面
//                            showDeletePopWindow(baseViewHolder.mCommentsCv, mList.get(position).getId(), (bean),
//                                    baseViewHolder.getLayoutPosition() - 1, position);
                        } else {
                            // 不相同则开始回复，这里需要判断是回复说说的发布者，还是评论者
                            if (null != mMomentsListener) {
                                //返回主页去弹出评论
                                mMomentsListener.onPinlunEdit(view, 1,
                                        momentsComment.getUserId(), momentsComment.getUserNickName());//谁对谁回复，需要判断
                            }
                        }
                    }

                    @SuppressLint("NewApi")
                    @Override
                    public void CommentLongClick(View view, int position1, MomentsComment momentsComment) {
                        Log.d(TAG, "CommentLongClick: ");
//                        showCopyPopWindow(baseViewHolder.mCommentsCv, momentsComment.getContent());
                    }

                    @Override
                    public void toUser(String userid) {
                        toUserInfo(userid);
                    }
                });
//
                baseViewHolder.mCommentsCv.notifyDataSetChanged();
            } else {
                baseViewHolder.mCommentsCv.setVisibility(View.GONE);
            }
        } else {
            baseViewHolder.mLikeAndCommentLl.setVisibility(View.GONE);
        }
        if (moments.getUserId().equals(mUser.getUserId())) {
            baseViewHolder.mDeleteTv.setVisibility(View.VISIBLE);
        } else {
            baseViewHolder.mDeleteTv.setVisibility(View.GONE);
        }
        if (moments.getTimestamp() != 0) {
            String prettyTime = TimeUtil.getMomentPrettyTime(moments.getTimestamp());
            baseViewHolder.mTimeTv.setText(prettyTime);
        }
        baseViewHolder.mCommentIv.setOnClickListener(view -> {
            Log.d(TAG, "onBindViewHolder: baseViewHolder.mCommentIv.setOnClickListener");
            if (mMomentsListener != null) {
                mMomentsListener.onClickLikeAndComment(view, position);
            }
        });
//        baseViewHolder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (expandFoldListener != null) {
//                    expandFoldListener.deletePengyouquan(mList.get(position).getId());
//                }
//            }
//        });
    }

    private void setMeasuredDimension(LinearLayout momentTable, int size){
        float height = 0;
        if(size != 0){
            height = mContent.getResources().getDimension(R.dimen.product_item_raw_spacing) * (size -1) + mContent.getResources().getDimension(R.dimen.product_item_height) * size;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)height);
        momentTable.setLayoutParams(params);
    }

    public int getItemViewType(int position) {
        if (mHeaderView == null) {
            if (MomentsType.TEXT.getType().equals(mMomentsList.get(position).getType())) {
                return TYPE_TEXT;
            } else if (MomentsType.TABLE.getType().equals(mMomentsList.get(position).getType())) {
                Log.d(TAG, "getItemViewType: mHeaderView = null and " + position);
                return TYPE_TABLE;
            } else if (MomentsType.VIDEO.getType().equals(mMomentsList.get(position).getType())) {
                return TYPE_VIDEO;
            } else {
                return TYPE_TEXT;
            }
        } else {
            if (position == 0) {
                return TYPE_HEADER;
            } else if (MomentsType.TEXT.getType().equals(mMomentsList.get(position - 1).getType())) {
                return TYPE_TEXT;
            } else if (MomentsType.TABLE.getType().equals(mMomentsList.get(position - 1).getType())) {
                Log.d(TAG, "getItemViewType: " + position);
                return TYPE_TABLE;
            } else if (MomentsType.VIDEO.getType().equals(mMomentsList.get(position - 1).getType())) {
                return TYPE_VIDEO;
            } else {
                return TYPE_TEXT;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mHeaderView != null) {
            return mMomentsList.size() + 1;
        } else {
            return mMomentsList.size();
        }
    }


//    //    加载网络图片圆角
//    public void loadImage(Context context, String path, ImageView imageView) {
//        RoundedCorners roundedCorners = new RoundedCorners(20);//数字为圆角度数
//        RequestOptions coverRequestOptions = new RequestOptions()
//                .transforms(new CenterCrop(), roundedCorners)//, roundedCorners
////                .error(R.mipmap.default_img)//加载错误显示
////                .placeholder(R.mipmap.default_img)//加载中显示
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全部
////                .skipMemoryCache(true)
//                ;//不做内存缓存
//
//        //Glide 加载图片简单用法
//        Glide.with(context)
//                .load(path)
//                .apply(coverRequestOptions)
//                .into(imageView);
//    }

    // 加载视频缩略图
    public void loadVideoThumbnail(Context context, String path, ImageView imageView) {
//        RoundedCorners roundedCorners = new RoundedCorners(20);//数字为圆角度数
        RequestOptions coverRequestOptions = new RequestOptions()
                .transforms(new CenterCrop())//, roundedCorners
//                .error(R.mipmap.default_img)//加载错误显示
//                .placeholder(R.mipmap.default_img)//加载中显示
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全部
//                .skipMemoryCache(true)//多种原因造成闪烁，这里是之一，如果坚持跳过内存缓存，需要tag,增加noty的各种判断，
                //但是不跳过，可能因为数据过大会boom,
                ;//不做内存缓存

        //Glide 加载图片简单用法
        Glide.with(context)
                .load(path)
                .apply(coverRequestOptions)
                .into(imageView);


    }

    /**
     * 进入用户详情页
     *
     * @param userId 用户ID
     */
    private void toUserInfo(String userId) {
        if (null != mMomentsListener) {
            mMomentsListener.toUserInfo(userId);
        }
    }

    private PopupWindow mPopupWindow;
/*
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showCopyPopWindow(CommentsView rvComment, String content) {
        View contentView = getCopyPopupWindowContentView(content);
        mPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
        int[] windowPos = PopupWindowUtil.calculatePopWindowPos(rvComment, contentView, 0, 0);
        mPopupWindow.showAsDropDown(rvComment, 0, -60, windowPos[1]);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
    }*/

   /* private View getCopyPopupWindowContentView(final String content) {
        // 布局ID
        int layoutId = R.layout.popup_copy;
        View contentView = LayoutInflater.from(mContent).inflate(layoutId, null);
        View.OnClickListener menuItemOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
                ClipboardManager mCM = (ClipboardManager) mContent.getSystemService(CLIPBOARD_SERVICE);
                mCM.setPrimaryClip(ClipData.newPlainText(null, content));
                Toast.makeText(mContent, mContent.getString(R.string.copied), Toast.LENGTH_SHORT).show();
            }
        };
        contentView.findViewById(R.id.menu_copy).setOnClickListener(menuItemOnClickListener);
        return contentView;
    }*/

   /* @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showDeletePopWindow(View view, int ids, ExplorePostPinglunBean id, int layoutPosition, int position) {
        View contentView = getPopupWindowContentView(ids, id, layoutPosition, position);
        mPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
        int[] windowPos = PopupWindowUtil.calculatePopWindowPos(view, contentView, 0, 0);
        mPopupWindow.showAsDropDown(view, 0, -40, windowPos[1]);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
    }*/

    /*private View getPopupWindowContentView(final int ids, final ExplorePostPinglunBean id, final int layoutPosition, final int position) {
        // 布局ID
        int layoutId = R.layout.popup_delete;
        View contentView = LayoutInflater.from(mContent).inflate(layoutId, null);
        View.OnClickListener menuItemOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                    if (mMomentsListener != null) {
                        mMomentsListener.deleteMypinglun(ids, id);
                    }
                }
            }
        };
        contentView.findViewById(R.id.menu_delete).setOnClickListener(menuItemOnClickListener);
        return contentView;
    }*/

    public List<Uri> getPhotoUriList(List<String> photoList) {
        List<Uri> photoUriList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(photoList)) {
            for (String photo : photoList) {
                photoUriList.add(Uri.parse(photo));
            }
        }
        return photoUriList;
    }

}