package com.shiliu.caishifu.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.engine.GlideEngine;
import com.shiliu.caishifu.model.Product;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.utils.CollectionUtils;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.utils.PreferencesUtil;
import com.shiliu.caishifu.widget.ConfirmDialog;
import com.shiliu.caishifu.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * 新增商品
 */
public class AddProductActivity extends BaseActivity {
    private static final String TAG = "AddProductActivity";

    private static final int REQUEST_CODE_CONTACTS = 0;
    private static final int REQUEST_CODE_LOCATION = 1;
    private static final int UPDATE_VEGETABLE_PICTURE = 2;

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.et_name)
    EditText mNameEt;

    @BindView(R.id.et_price)
    EditText mPriceEt;

    @BindView(R.id.et_supply)
    EditText mSupplyEt;

    @BindView(R.id.tv_save_vegetable_item)
    TextView mSaveTv;

    @BindView(R.id.vi_name)
    View mNameVi;

    @BindView(R.id.vi_price)
    View mPriceVi;

    @BindView(R.id.vi_supply)
    View mSupplyVi;

    @BindView(R.id.sdv_vegetable_picture1)
    SimpleDraweeView mVegetablePicture1;

    @BindView(R.id.sdv_vegetable_picture2)
    SimpleDraweeView mVegetablePicture2;

    @BindView(R.id.sdv_vegetable_picture_add)
    SimpleDraweeView mVegetablePictureAdd;

    @BindView(R.id.rl_product_item_delete)
    RelativeLayout deleteProduct;

    NetworkUtil networkUtil;
    User mUser;
    LoadingDialog mDialog;

    private int position;

    List<String> pictures = new ArrayList<>(2);

    @Override
    public int getContentView() {
        return R.layout.add_or_modify_product_activity;
    }

    @Override
    public void initView() {
        mUser = getUser();
        initStatusBar();
        if (Boolean.valueOf(mUser.getIsBuyer())) {
            mTitleTv.setText(getString(R.string.buyInfo));
        } else {
            mTitleTv.setText(getString(R.string.sellInfo));
        }
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {
        mNameEt.addTextChangedListener(new TextChange());
        mPriceEt.addTextChangedListener(new TextChange());
        mSupplyEt.addTextChangedListener(new TextChange());
        mVegetablePicture2.addOnLayoutChangeListener(new PictureChange());
    }

    @Override
    public void initData() {
        networkUtil = NetworkUtil.getInstance(this);
        mDialog = new LoadingDialog(AddProductActivity.this);
        mNameEt.setText(this.getIntent().getStringExtra("name"));
        mPriceEt.setText(this.getIntent().getStringExtra("price"));
        mSupplyEt.setText(this.getIntent().getStringExtra("supply"));
        List<String> selectedPictures = this.getIntent().getStringArrayListExtra("pictures");
        if (selectedPictures != null && !selectedPictures.isEmpty()) {
            pictures.addAll(selectedPictures);
            if (selectedPictures.size() == 1) {
                mVegetablePicture2.setVisibility(View.VISIBLE);
                mVegetablePicture2.setImageURI(selectedPictures.get(0));

            } else {
                mVegetablePicture2.setVisibility(View.VISIBLE);
                mVegetablePicture1.setVisibility(View.VISIBLE);
                mVegetablePictureAdd.setVisibility(View.GONE);
                mVegetablePicture2.setImageURI(selectedPictures.get(0));
                mVegetablePicture1.setImageURI(selectedPictures.get(1));
            }
        }
        position = this.getIntent().getIntExtra("position", -1);
        if (position != -1) {
            deleteProduct.setVisibility(View.VISIBLE);
        }
    }

    public void back(View view) {
        String name = mNameEt.getText().toString();
        String price = mPriceEt.getText().toString();
        String supply = mSupplyEt.getText().toString();
        if (position == -1) {
            if (!TextUtils.isEmpty(name) ||
                    !TextUtils.isEmpty(price) ||
                    !TextUtils.isEmpty(supply)) {
                final ConfirmDialog confirmDialog = new ConfirmDialog(AddProductActivity.this, getString(R.string.tips),
                        Boolean.valueOf(mUser.getIsBuyer()) ? getString(R.string.add_buy_vegetable_abandon_tips) : getString(R.string.add_sell_vegetable_abandon_tips),
                        getString(R.string.ok), getString(R.string.cancel), getColor(R.color.navy_blue));
                confirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOkClick() {
                        confirmDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onCancelClick() {
                        confirmDialog.dismiss();
                    }
                });
                // 点击空白处消失
                confirmDialog.setCancelable(true);
                confirmDialog.show();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @OnClick({R.id.tv_save_vegetable_item, R.id.sdv_vegetable_picture_add, R.id.sdv_vegetable_picture1, R.id.sdv_vegetable_picture2, R.id.rl_product_item_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save_vegetable_item:
                String name = mNameEt.getText().toString();
                if (name.trim().isEmpty()) {
                    mNameVi.setBackgroundColor(getColor(R.color.colorAccent));
                    Log.w(TAG, "input name is error");
                    break;
                }
                float price;
                try {
                    price = Float.parseFloat(mPriceEt.getText().toString());
                    if (price <= 0.0) {
                        mPriceVi.setBackgroundColor(getColor(R.color.colorAccent));
                        Log.w(TAG, "input price must more than 0.0");
                        break;
                    }
                } catch (NumberFormatException e) {
                    mPriceVi.setBackgroundColor(getColor(R.color.colorAccent));
                    Log.w(TAG, "input price is error");
                    break;
                }
                int supply;
                try {
                    supply = Integer.parseInt(mSupplyEt.getText().toString());
                    if (supply <= 0.0) {
                        mSupplyVi.setBackgroundColor(getColor(R.color.colorAccent));
                        Log.w(TAG, "input supply must more than 0");
                        break;
                    }
                } catch (NumberFormatException e) {
                    mSupplyVi.setBackgroundColor(getColor(R.color.colorAccent));
                    Log.w(TAG, "input supply is error");
                    break;
                }
                addVegetable(name, price, supply, pictures);
                break;
            case R.id.sdv_vegetable_picture_add:
                showPhotoDialog();
                break;
            case R.id.sdv_vegetable_picture1:
                if (pictures.size() == 2) {
                    Intent intent = new Intent(this, BigImageActivity.class);
                    intent.putExtra("imgUrl", pictures.get(1));
                    startActivity(intent);
                }
                break;
            case R.id.sdv_vegetable_picture2:
                if (pictures.size() >= 1) {
                    Intent intent = new Intent(this, BigImageActivity.class);
                    intent.putExtra("imgUrl", pictures.get(0));
                    startActivity(intent);
                }
                break;
            case R.id.rl_product_item_delete:
                final ConfirmDialog confirmDialog = new ConfirmDialog(AddProductActivity.this, getString(R.string.tips),
                        getString(R.string.confirm_delete),
                        getString(R.string.ok), getString(R.string.cancel), getColor(R.color.navy_blue));
                confirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOkClick() {
                        confirmDialog.dismiss();
                        mUser.getProductList().remove(position);
                        PreferencesUtil.getInstance().setUser(mUser);
                        finish();
                    }

                    @Override
                    public void onCancelClick() {
                        confirmDialog.dismiss();
                    }
                });
                // 点击空白处消失
                confirmDialog.setCancelable(true);
                confirmDialog.show();
                break;
            default:
                break;
        }
    }

    @OnFocusChange({R.id.et_name, R.id.et_price, R.id.et_supply})
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.et_name:
                if (hasFocus) {
                    mNameVi.setBackgroundColor(getColor(R.color.caishifu_btn_green));
                } else {
                    mNameVi.setBackgroundColor(getColor(R.color.picker_list_divider));
                }
                break;
            case R.id.et_price:
                if (hasFocus) {
                    mPriceVi.setBackgroundColor(getColor(R.color.caishifu_btn_green));
                } else {
                    mPriceVi.setBackgroundColor(getColor(R.color.picker_list_divider));
                }
                break;
            case R.id.et_supply:
                if (hasFocus) {
                    mSupplyVi.setBackgroundColor(getColor(R.color.caishifu_btn_green));
                } else {
                    mSupplyVi.setBackgroundColor(getColor(R.color.picker_list_divider));
                }
                break;
        }
    }

    /**
     * 显示图片对话框
     */
    private void showPhotoDialog() {
        EasyPhotos.createAlbum(this, false, false, GlideEngine.getInstance())
                .setFileProviderAuthority("com.shiliu.caishifu.fileprovider")
                .setCount(2 - pictures.size())//参数说明：最大可选数，默认1
                .start(UPDATE_VEGETABLE_PICTURE);
    }

    class PictureChange implements View.OnLayoutChangeListener {

        @Override
        public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
            listener();
        }
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            listener();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void listener() {
        String name = mNameEt.getText().toString();
        String price = mPriceEt.getText().toString();
        String supply = mSupplyEt.getText().toString();
        if (!TextUtils.isEmpty(name) &&
                !TextUtils.isEmpty(price) &&
                !TextUtils.isEmpty(supply) &&
                !pictures.isEmpty()) {
            // 可保存
            mSaveTv.setTextColor(getColor(R.color.common_text_color_black));
            mSaveTv.setEnabled(true);
        } else {
            // 不可保存
            mSaveTv.setTextColor(getColor(R.color.btn_text_default_color));
            mSaveTv.setEnabled(false);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void addVegetable(final String name, final float price,
                              final int supply, List<String> pictures) {
        Product product = new Product(name, price, supply, pictures);
        if (position != -1) {
            mUser.getProductList().set(position, product);
        } else {
            mUser.addProductList(product);
        }
        PreferencesUtil.getInstance().setUser(mUser);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPDATE_VEGETABLE_PICTURE:
                    // 返回对象集合: 包含图片的宽、高、大小、用户是否选中原图选项等信息
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    if (!CollectionUtils.isEmpty(resultPhotos)) {
                        mVegetablePicture1.setVisibility(View.VISIBLE);
                        mVegetablePicture2.setVisibility(View.VISIBLE);
                        if (resultPhotos.size() == 2) {
                            String picture1 = new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME).path(
                                    resultPhotos.get(0).path).build().toString();
                            String picture2 = new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME).path(
                                    resultPhotos.get(1).path).build().toString();
                            mVegetablePicture2.setImageURI(picture1);
                            mVegetablePicture1.setImageURI(picture2);
                            pictures.add(picture1);
                            pictures.add(picture2);
                        } else {
                            String picture = new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME).path(
                                    resultPhotos.get(0).path).build().toString();
                            if (pictures.size() == 0) {
                                mVegetablePicture2.setVisibility(View.VISIBLE);
                                mVegetablePicture2.setImageURI(picture);
                                pictures.add(picture);
                            } else {
                                mVegetablePicture1.setVisibility(View.VISIBLE);
                                mVegetablePicture1.setImageURI(picture);
                                pictures.add(picture);
                            }
                        }
                    }
                    if (pictures.size() == 2) {
                        mVegetablePictureAdd.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        }
    }


    /**
     * requestPermissions的回调
     * 一个或多个权限请求结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        // 判断是否拒绝  拒绝后要怎么处理 以及取消再次提示的处理
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                break;
            }
        }
        if (hasAllGranted) {

        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(AddProductActivity.this, permissions[0], requestCode);
        }
    }

    public void handleRejectPermission(final Activity context, String permission, int requestCode) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            String content = "";
            switch (requestCode) {
                case REQUEST_CODE_CONTACTS:
                    content = getString(R.string.request_permission_contacts);
                    break;
                case REQUEST_CODE_LOCATION:
                    content = getString(R.string.request_permission_location);
                    break;
            }
            final ConfirmDialog mConfirmDialog = new ConfirmDialog(AddProductActivity.this, getString(R.string.request_permission),
                    content,
                    getString(R.string.go_setting), getString(R.string.cancel), getColor(R.color.navy_blue));
            mConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                @Override
                public void onOkClick() {
                    mConfirmDialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }

                @Override
                public void onCancelClick() {
                    mConfirmDialog.dismiss();
                }
            });
            // 点击空白处消失
            mConfirmDialog.setCancelable(false);
            mConfirmDialog.show();
        }
    }


}