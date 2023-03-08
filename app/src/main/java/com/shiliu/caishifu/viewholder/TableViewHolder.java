package com.shiliu.caishifu.viewholder;

import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.shiliu.caishifu.R;
import com.shiliu.caishifu.widget.NineGridView;

/**
 * 表单ViewHolder
 *
 */
public class TableViewHolder extends BaseViewHolder {

    public ListView productTableView;

    public TableViewHolder(View itemView) {
        super(itemView);
        productTableView = (ListView) itemView.findViewById(R.id.vegetable_list);
    }

}