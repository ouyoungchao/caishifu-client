package com.shiliu.caishifu.viewholder;

import android.view.View;

import com.shiliu.caishifu.R;
import com.shiliu.caishifu.moments.BaseViewHolder;
import com.shiliu.caishifu.widget.NineGridView;

/**
 * 图片ViewHolder
 *
 */
public class ImageViewHolder extends BaseViewHolder {

    public NineGridView mPhotosGv;

    public ImageViewHolder(View itemView) {
        super(itemView);
        mPhotosGv = (NineGridView) itemView.findViewById(R.id.gv_photos);
    }

}