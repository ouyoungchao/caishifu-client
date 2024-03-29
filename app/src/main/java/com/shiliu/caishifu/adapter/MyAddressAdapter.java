package com.shiliu.caishifu.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.shiliu.caishifu.R;
import com.shiliu.caishifu.activity.ModifyAddressActivity;
import com.shiliu.caishifu.model.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的地址
 *
 */
public class MyAddressAdapter extends BaseAdapter {

    Context mContext;
    List<Address> mAddressList = new ArrayList<>();

    public MyAddressAdapter(Context context, List<Address> addressList) {
        this.mContext = context;
        this.mAddressList = addressList;
    }

    public void setData(List<Address> dataList) {
        this.mAddressList = dataList;
    }

    @Override
    public int getCount() {
        return mAddressList.size();
    }

    @Override
    public Address getItem(int position) {
        return mAddressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final Address address = mAddressList.get(position);
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.my_address_item, null);

            viewHolder.mRootLl = convertView.findViewById(R.id.ll_root);
            viewHolder.mUserInfoTv = convertView.findViewById(R.id.tv_user_info);
            viewHolder.mAddressInfoTv = convertView.findViewById(R.id.tv_address_info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        StringBuffer userInfoBuffer = new StringBuffer();
        userInfoBuffer.append(address.getName()).append("，").append(address.getPhone());
        StringBuffer addressInfoBuffer = new StringBuffer();
        addressInfoBuffer.append(address.getProvince()).append(" ")
                .append(address.getCity()).append(" ")
                .append(address.getDistrict()).append(" ")
                .append(address.getDetail());
        viewHolder.mUserInfoTv.setText(userInfoBuffer.toString());
        viewHolder.mAddressInfoTv.setText(addressInfoBuffer.toString());
        viewHolder.mRootLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ModifyAddressActivity.class);
                intent.putExtra("address", address);
                mContext.startActivity(intent);
            }
        });
        viewHolder.mRootLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        return convertView;
    }

    class ViewHolder {
        LinearLayout mRootLl;
        TextView mUserInfoTv;
        TextView mAddressInfoTv;
    }

}