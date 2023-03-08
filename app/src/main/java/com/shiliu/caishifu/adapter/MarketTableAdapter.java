package com.shiliu.caishifu.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.activity.AddProductActivity;
import com.shiliu.caishifu.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布信息适配类
 *
 */
public class MarketTableAdapter extends PublishItemAdapter {
    private static final String TAG = "PublishItemAdapter";

    Context mContext;
    List<Product> productList = new ArrayList<>();

    public MarketTableAdapter(Context mContext, List<Product> productList) {
        super(mContext,productList);
        this.mContext = mContext;
        this.productList = productList;
    }

    public void setData(List<Product> dataList) {
        this.productList = dataList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Product getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final Product product = productList.get(position);
        Log.i(TAG, "getView: " + position + " " + product.toString());
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.publish_product_item, null);
            viewHolder.productItem = convertView.findViewById(R.id.rl_vegetable_table_item);
            viewHolder.name = convertView.findViewById(R.id.tv_vegetable_table_item_name);
            viewHolder.price = convertView.findViewById(R.id.tv_vegetable_table_item_price);
            viewHolder.supply = convertView.findViewById(R.id.tv_vegetable_table_item_supply);
            viewHolder.mVegetablePicture2 = convertView.findViewById(R.id.sdv_vegetable_item_picture2);
            viewHolder.mVegetablePicture1 = convertView.findViewById(R.id.sdv_vegetable_item_picture1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(product.getName());
        viewHolder.price.setText(String.valueOf(product.getPrice()));
        viewHolder.supply.setText(String.valueOf(product.getSupply()));
        viewHolder.mVegetablePicture1.setVisibility(View.VISIBLE);
        viewHolder.mVegetablePicture2.setVisibility(View.VISIBLE);
        if(product.getPictures().size() == 1){
            viewHolder.mVegetablePicture2.setImageURI(product.getPictures().get(0));
            Log.i(TAG, "layout info: " + viewHolder.mVegetablePicture1.getLayoutParams().width +" "+ viewHolder.mVegetablePicture1.getLayoutParams().height);
        }else if(product.getPictures().size() == 2){
            viewHolder.mVegetablePicture1.setImageURI(product.getPictures().get(0));
            viewHolder.mVegetablePicture2.setImageURI(product.getPictures().get(1));
            Log.i(TAG, "picture 1 layout info: " + viewHolder.mVegetablePicture1.getLayoutParams().width +" "+ viewHolder.mVegetablePicture1.getLayoutParams().height);
            Log.i(TAG, "picture 2 layout info: " + viewHolder.mVegetablePicture2.getLayoutParams().width +" "+ viewHolder.mVegetablePicture2.getLayoutParams().height);
        }
        viewHolder.productItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddProductActivity.class);
                intent.putExtra("position",position);
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
                return false;
            }
        });

        return convertView;
    }

    class ViewHolder {
        RelativeLayout productItem;
        TextView name;
        TextView price;
        TextView supply;
        SimpleDraweeView mVegetablePicture2;
        SimpleDraweeView mVegetablePicture1;
    }

}