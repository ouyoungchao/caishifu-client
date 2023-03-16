package com.shiliu.caishifu.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.shiliu.caishifu.R;

/**
 * 表单ViewHolder
 *
 */
public class TableViewHolder extends BaseViewHolder {

    public ListView momentItemList;
    public LinearLayout momentTable;

    public TableViewHolder(View itemView) {
        super(itemView);
        momentTable = itemView.findViewById(R.id.rl_moment_table);
        momentItemList = (ListView) itemView.findViewById(R.id.moment_item_list);
    }

}