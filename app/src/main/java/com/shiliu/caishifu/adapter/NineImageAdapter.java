package com.shiliu.caishifu.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.moments.adapter.Utils;
import com.shiliu.caishifu.utils.ScreenUtils;
import com.shiliu.caishifu.widget.NineGridView;

import java.util.List;


/**
 * 九宫格图片适配器
 *
 */
public class NineImageAdapter implements NineGridView.NineGridAdapter<String> {

    private List<String> mImageBeans;

    private Context mContext;

    private RequestOptions mRequestOptions;

    private DrawableTransitionOptions mDrawableTransitionOptions;

    public NineImageAdapter(Context context, RequestOptions requestOptions, DrawableTransitionOptions drawableTransitionOptions, List<String> imageBeans) {
        this.mContext = context;
        this.mDrawableTransitionOptions = drawableTransitionOptions;
        this.mImageBeans = imageBeans;
        int itemSize = (ScreenUtils.getScreenWidth(mContext) - 2 * Utils.dp2px(4) - Utils.dp2px(54)) / 3;
        if (mImageBeans.size() > 1) {
            this.mRequestOptions = requestOptions.override(itemSize, itemSize);
        } else {
            this.mRequestOptions = requestOptions.override(itemSize, itemSize * 3 / 2);
        }
    }

    @Override
    public int getCount() {
        return mImageBeans == null ? 0 : mImageBeans.size();
    }

    @Override
    public String getItem(int position) {
        return mImageBeans == null ? null : position < mImageBeans.size() ? mImageBeans.get(position) : null;
    }

    @Override
    public View getView(int position, View itemView) {
        ImageView imageView;
        if (itemView == null) {
            imageView = new ImageView(mContext);
            imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nine_image_bg));
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            imageView = (ImageView) itemView;
        }
        String url = mImageBeans.get(position);
        if (url == null || url.equals("")) {
            mRequestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .skipMemoryCache(true)
                    .error(R.color.grey).placeholder(R.color.grey);
            Glide.with(mContext)
                    .load(R.color.grey)
                    .apply(mRequestOptions)
                    .transition(mDrawableTransitionOptions)
                    .into(imageView);
        } else {
            mRequestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .skipMemoryCache(true)
                    .error(R.color.grey).placeholder(R.color.grey);
            Glide.with(mContext)
                    .load(url)
                    .apply(mRequestOptions)
                    .transition(mDrawableTransitionOptions)
                    .into(imageView);
        }
        return imageView;
    }

}