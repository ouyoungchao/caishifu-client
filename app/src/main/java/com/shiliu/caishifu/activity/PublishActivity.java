package com.shiliu.caishifu.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.adapter.PublishItemAdapter;
import com.shiliu.caishifu.model.User;
import butterknife.BindView;
import butterknife.OnClick;

public class PublishActivity extends BaseActivity{
    private static final String TAG = "PublishActivity";
    
    @BindView(R.id.im_vegetable_table_data_add)
    ImageView mAddProductItem;

    @BindView(R.id.vegetable_list)
    ListView productListView;

    @BindView(R.id.placeholder_container)
    RelativeLayout productContainer;

    private PublishItemAdapter publishItemAdapter;
    private User user;

    @Override
    public int getContentView() {
        return R.layout.publish_activity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = getUser();
        // 本地读取
        if(!user.getProductList().isEmpty()){
            productContainer.setVisibility(View.VISIBLE);
        }
        publishItemAdapter.setData(user.getProductList());
        publishItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void initData() {
        user = getUser();
        publishItemAdapter = new PublishItemAdapter(this, user.getProductList());
        productListView.setAdapter(publishItemAdapter);
    }

    @Override
    public void initView() {
        initStatusBar();
    }

    @Override
    public void initListener() {

    }

    @OnClick({R.id.im_vegetable_table_data_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.im_vegetable_table_data_add:
                Intent addProductItem = new Intent(this, AddProductActivity.class);
                startActivity(addProductItem);
                break;
        }
    }

    public void back(View view) {
        finish();
    }


}
