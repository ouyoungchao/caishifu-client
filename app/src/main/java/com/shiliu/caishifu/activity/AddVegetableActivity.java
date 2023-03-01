package com.shiliu.caishifu.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.engine.GlideEngine;
import com.shiliu.caishifu.model.Product;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.utils.CollectionUtils;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.widget.ConfirmDialog;
import com.shiliu.caishifu.widget.LoadingDialog;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * 新增地址
 *
 */
public class AddVegetableActivity extends BaseActivity {
    private static final String TAG = "AddVegetableActivity";

    private static final int REQUEST_CODE_CONTACTS = 0;
    private static final int REQUEST_CODE_LOCATION = 1;
    private static final int UPDATE_VEGETABLE_PICTURE = 2;

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    /**
     * 收货人
     */
    @BindView(R.id.et_name)
    EditText mNameEt;

    /**
     * 手机号码
     */
    @BindView(R.id.et_price)
    EditText mPriceEt;

    @BindView(R.id.et_supply)
    EditText mSupplyEt;

    @BindView(R.id.tv_save)
    TextView mSaveTv;

    @BindView(R.id.vi_name)
    View mNameVi;

    @BindView(R.id.vi_price)
    View mPriceVi;

    @BindView(R.id.vi_supply)
    View mSupplyVi;

    @BindView(R.id.sdv_vegetable_picture2)
    View mVegetablePicture2;

    NetworkUtil networkUtil;
    User mUser;
    LoadingDialog mDialog;

    List<String> pictures = new ArrayList<>();

    @Override
    public int getContentView() {
        return R.layout.add_or_modify_vegetable_activity;
    }

    @Override
    public void initView() {
        initStatusBar();
        if(Boolean.valueOf(mUser.getIsBuyer())) {
            mTitleTv.setText(getString(R.string.buyInfo));
        }else{
            mTitleTv.setText(getString(R.string.sellInfo));
        }
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {
        mNameEt.addTextChangedListener(new TextChange());
        mPriceEt.addTextChangedListener(new TextChange());
        mSupplyEt.addTextChangedListener(new TextChange());
    }

    @Override
    public void initData() {
        networkUtil = NetworkUtil.getInstance(this);
        mUser = getUser();
        mDialog = new LoadingDialog(AddVegetableActivity.this);
    }

    public void back(View view) {
        String name = mNameEt.getText().toString();
        String price = mPriceEt.getText().toString();
        String supply = mSupplyEt.getText().toString();

        if (!TextUtils.isEmpty(name) ||
                !TextUtils.isEmpty(price) ||
                !TextUtils.isEmpty(supply)) {
            final ConfirmDialog confirmDialog = new ConfirmDialog(AddVegetableActivity.this, getString(R.string.tips),
                    Boolean.valueOf(mUser.getIsBuyer())?getString(R.string.add_buy_vegetable_abandon_tips):getString(R.string.add_sell_vegetable_abandon_tips),
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
    }

    @OnFocusChange({R.id.et_name, R.id.et_price, R.id.et_supply, R.id.sdv_vegetable_picture1,R.id.sdv_vegetable_picture2})
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
            case R.id.sdv_vegetable_picture1:
            case R.id.sdv_vegetable_picture2:
                showPhotoDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 显示图片对话框
     */
    private void showPhotoDialog() {
        EasyPhotos.createAlbum(this, false, false, GlideEngine.getInstance())
                .setFileProviderAuthority("com.shiliu.caishifu.fileprovider")
                .setCount(1)//参数说明：最大可选数，默认1
                .start(UPDATE_VEGETABLE_PICTURE);
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String name = mNameEt.getText().toString();
            String price = mPriceEt.getText().toString();
            String supply = mSupplyEt.getText().toString();

            if (!TextUtils.isEmpty(name) &&
                    !TextUtils.isEmpty(price) &&
                    !TextUtils.isEmpty(supply)) {
                // 可保存
                mSaveTv.setTextColor(0xFFFFFFFF);
                mSaveTv.setEnabled(true);
            } else {
                // 不可保存
                mSaveTv.setTextColor(getColor(R.color.btn_text_default_color));
                mSaveTv.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    @OnClick({R.id.tv_save})
    public void onClick(View view) {
        String[] permissions;
        switch (view.getId()) {
            case R.id.tv_save:
                mDialog.setMessage(getString(R.string.saving));
                mDialog.show();
                String name = mNameEt.getText().toString();
                String price = mPriceEt.getText().toString();
                String supply = mSupplyEt.getText().toString();
                addVegetable(name, price, supply,pictures);
                break;
           default:
               break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void addVegetable(final String name, final String price,
                            final String supply, List<String> pictures) {
        Product product = new Product(name,Float.parseFloat(price),Integer.parseInt(supply), pictures);
        mUser.getProductList().add(product);
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
                        pictures.add(resultPhotos.get(0).path);
                        if(pictures.size() == 1){
                            mVegetablePicture2.setVisibility(View.VISIBLE);
                        }
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
            handleRejectPermission(AddVegetableActivity.this, permissions[0], requestCode);
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
            final ConfirmDialog mConfirmDialog = new ConfirmDialog(AddVegetableActivity.this, getString(R.string.request_permission),
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