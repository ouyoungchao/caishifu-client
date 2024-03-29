package com.shiliu.caishifu.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.activity.AddProductActivity;
import com.shiliu.caishifu.model.Product;
import com.shiliu.caishifu.utils.OssUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布信息适配类
 */
public class MomentItemAdapter extends BaseAdapter {
    private static final String TAG = "MomentItemAdapter";

    Context mContext;
    List<Product> productList = new ArrayList<>();

    public MomentItemAdapter(Context mContext, List<Product> productList) {
        this.mContext = mContext;
        this.productList = productList;
    }

    public void setData(List<Product> dataList) {
        this.productList = dataList;
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount: " + productList.size());
        return productList.size();
    }

    @Override
    public Product getItem(int position) {
        Log.d(TAG, "getItem: " + position);
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "getItemId: " + position);
        return position;
    }

    @Override
    public boolean areAllItemsEnabled() {
        Log.d(TAG, "areAllItemsEnabled: ");
        return super.areAllItemsEnabled();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final Product product = productList.get(position);
        Log.i(TAG, "getView: " + position + " " + product.toString());
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        convertView = LayoutInflater.from(mContext).inflate(R.layout.moment_table_item, null);
        viewHolder.productItem = convertView.findViewById(R.id.rl_moment_table_item);
        viewHolder.name = convertView.findViewById(R.id.tv_vegetable_table_item_name);
        viewHolder.price = convertView.findViewById(R.id.tv_vegetable_table_item_price);
        viewHolder.supply = convertView.findViewById(R.id.tv_vegetable_table_item_supply);
        viewHolder.mVegetablePicture2 = convertView.findViewById(R.id.sdv_vegetable_item_picture2);
        viewHolder.mVegetablePicture1 = convertView.findViewById(R.id.sdv_vegetable_item_picture1);
        convertView.setTag(viewHolder);
        viewHolder.name.setText(product.getName());
        viewHolder.price.setText(String.valueOf(product.getPrice() + mContext.getResources().getString(R.string.product_price_unit)));
        viewHolder.supply.setText(mContext.getResources().getString(R.string.supply) + String.valueOf(product.getSupply()) + mContext.getResources().getString(R.string.product_supply_unit));
        viewHolder.mVegetablePicture1.setVisibility(View.VISIBLE);
        if (product.getPictures().size() == 1) {
            viewHolder.mVegetablePicture1.setImageURI(OssUtil.resize(product.getPictures().get(0)));
        } else if (product.getPictures().size() == 2) {
            viewHolder.mVegetablePicture2.setVisibility(View.VISIBLE);
            viewHolder.mVegetablePicture1.setImageURI(OssUtil.resize(product.getPictures().get(0)));
            viewHolder.mVegetablePicture2.setImageURI(OssUtil.resize(product.getPictures().get(1)));
        }
        viewHolder.productItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddProductActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("name", product.getName());
                intent.putExtra("price", String.valueOf(product.getPrice()));
                intent.putExtra("supply", String.valueOf(product.getSupply()));
                intent.putStringArrayListExtra("pictures", (ArrayList<String>) product.getPictures());
                mContext.startActivity(intent);
            }
        });
        viewHolder.productItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLongClick: " + position);
                return false;
            }
        });

        return convertView;
    }

    class ViewHolder {
        LinearLayout productItem;
        TextView name;
        TextView price;
        TextView supply;
        SimpleDraweeView mVegetablePicture2;
        SimpleDraweeView mVegetablePicture1;
    }

}