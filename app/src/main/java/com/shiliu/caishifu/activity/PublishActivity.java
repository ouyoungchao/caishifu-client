package com.shiliu.caishifu.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shiliu.caishifu.R;
import com.shiliu.caishifu.adapter.MyAddressAdapter;
import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.dao.UserDao;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.utils.ExampleUtil;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.widget.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublishActivity extends CommonActivity{
    private static final String TAG = "PublishActivity";
    
    @BindView(R.id.im_vegetable_table_data_add)
    ImageView mAddProductItem;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_activity);
        ButterKnife.bind(this);

        initStatusBar();
    }

    private void initView() {

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
