package com.shiliu.caishifu.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.shiliu.caishifu.R;
import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.dao.AreaDao;
import com.shiliu.caishifu.model.Address;
import com.shiliu.caishifu.model.Area;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.model.server.ResultCode;
import com.shiliu.caishifu.model.server.UserResult;
import com.shiliu.caishifu.utils.ExampleUtil;
import com.shiliu.caishifu.utils.JsonUtil;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.utils.PreferencesUtil;
import com.shiliu.caishifu.widget.ConfirmDialog;
import com.shiliu.caishifu.widget.LoadingDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 新增地址
 *
 */
public class AddAddressActivity extends BaseActivity {
    private static final String TAG = "AddAddressActivity";

    private static final int REQUEST_CODE_CONTACTS = 0;
    private static final int REQUEST_CODE_LOCATION = 1;

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
    @BindView(R.id.et_phone)
    EditText mPhoneEt;

    @BindView(R.id.et_address_detail)
    EditText mAddressDetailEt;

    @BindView(R.id.et_address_info)
    EditText mAddressInfoEt;

    @BindView(R.id.tv_save)
    TextView mSaveTv;

    @BindView(R.id.iv_location)
    ImageView mLocationIv;

    @BindView(R.id.vi_name)
    View mNameVi;

    @BindView(R.id.vi_phone)
    View mPhoneVi;

    @BindView(R.id.ll_address_detail)
    View mAddressDetailVi;

    NetworkUtil networkUtil;
    User mUser;
    LoadingDialog mDialog;
    AreaDao mAreaDao;

    @Override
    public int getContentView() {
        return R.layout.add_or_modify_address_activity;
    }

    @Override
    public void initView() {
        initStatusBar();
        mTitleTv.setText(getString(R.string.add_address));
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {
        mNameEt.addTextChangedListener(new TextChange());
        mPhoneEt.addTextChangedListener(new TextChange());
        mAddressInfoEt.addTextChangedListener(new TextChange());
        mAddressDetailEt.addTextChangedListener(new TextChange());
    }

    @Override
    public void initData() {
        networkUtil = NetworkUtil.getInstance(this);
        mUser = getUser();
        mDialog = new LoadingDialog(AddAddressActivity.this);
        mAreaDao = new AreaDao();
        //设置地址初始值
        PreferencesUtil.getInstance().init(this);
        PreferencesUtil.getInstance().setPickedProvince("");
        PreferencesUtil.getInstance().setPickedCity("");
        PreferencesUtil.getInstance().setPickedDistrict("");
        PreferencesUtil.getInstance().setPickedPostCode("");
    }

    public void back(View view) {
        String name = mNameEt.getText().toString();
        String phone = mPhoneEt.getText().toString();
        String addressInfo = mAddressInfoEt.getText().toString();
        String addressDetail = mAddressDetailEt.getText().toString();

        if (!TextUtils.isEmpty(name) ||
                !TextUtils.isEmpty(phone) ||
                !TextUtils.isEmpty(addressInfo) ||
                !TextUtils.isEmpty(addressDetail)) {
            final ConfirmDialog confirmDialog = new ConfirmDialog(AddAddressActivity.this, getString(R.string.tips),
                    getString(R.string.add_address_abandon_tips),
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

    @OnFocusChange({R.id.et_name, R.id.et_phone, R.id.et_address_detail})
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.et_name:
                if (hasFocus) {
                    mNameVi.setBackgroundColor(getColor(R.color.caishifu_btn_green));
                } else {
                    mNameVi.setBackgroundColor(getColor(R.color.picker_list_divider));
                }
                break;
            case R.id.et_phone:
                if (hasFocus) {
                    mPhoneVi.setBackgroundColor(getColor(R.color.caishifu_btn_green));
                } else {
                    mPhoneVi.setBackgroundColor(getColor(R.color.picker_list_divider));
                }
                break;
            case R.id.et_address_detail:
                if (hasFocus) {
                    mAddressDetailVi.setBackgroundColor(getColor(R.color.caishifu_btn_green));
                } else {
                    mAddressDetailVi.setBackgroundColor(getColor(R.color.picker_list_divider));
                }
                break;
        }
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String name = mNameEt.getText().toString();
            String phone = mPhoneEt.getText().toString();
            String addressInfo = mAddressInfoEt.getText().toString();
            String addressDetail = mAddressDetailEt.getText().toString();

            if (!TextUtils.isEmpty(name) &&
                    !TextUtils.isEmpty(phone) &&
                    !TextUtils.isEmpty(addressInfo) &&
                    !TextUtils.isEmpty(addressDetail)) {
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

    @OnClick({R.id.et_address_info, R.id.tv_save, R.id.iv_location})
    public void onClick(View view) {
        String[] permissions;
        switch (view.getId()) {
            //选择省市
            case R.id.et_address_info:
                startActivity(new Intent(AddAddressActivity.this, PickProvinceActivity.class));
                break;
            case R.id.tv_save:
                mDialog.setMessage(getString(R.string.saving));
                mDialog.show();
                String name = mNameEt.getText().toString();
                String phone = mPhoneEt.getText().toString();
                String addressProvince = PreferencesUtil.getInstance().getPickedProvince();
                String addressCity = PreferencesUtil.getInstance().getPickedCity();
                String addressDistrict = PreferencesUtil.getInstance().getPickedDistrict();
                String addressDetail = mAddressDetailEt.getText().toString();
                addAddress(name, phone, addressProvince, addressCity, addressDistrict, addressDetail);
                break;
            //地图选择
            case R.id.iv_location:
                permissions = new String[]{"android.permission.ACCESS_FINE_LOCATION"};
                requestPermissions(AddAddressActivity.this, permissions, REQUEST_CODE_LOCATION);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String pickedProvince = PreferencesUtil.getInstance().getPickedProvince();
        String pickedCity = PreferencesUtil.getInstance().getPickedCity();
        String pickedDistrict = PreferencesUtil.getInstance().getPickedDistrict();
        if (!TextUtils.isEmpty(pickedProvince)
                && !TextUtils.isEmpty(pickedCity)
                && !TextUtils.isEmpty(pickedDistrict)) {
            StringBuffer addressInfoBuffer = new StringBuffer();
            addressInfoBuffer.append(pickedProvince).append(" ")
                    .append(pickedCity).append(" ")
                    .append(pickedDistrict);
            mAddressInfoEt.setText(addressInfoBuffer.toString());
            // 光标移至最后
            CharSequence charSequence = mAddressInfoEt.getText();
            if (charSequence instanceof Spannable) {
                Spannable spanText = (Spannable) charSequence;
                Selection.setSelection(spanText, charSequence.length());
            }
        }
    }

    private void addAddress(final String name, final String phone,
                            final String addressProvince,
                            final String addressCity, final String addressDistrict, final String addressDetail) {
        Address address = new Address(name,phone,addressProvince,addressCity,addressDistrict,addressDetail,"");
        List<Address> addressList = new ArrayList<>();
        addressList.add(address);
        if(!mUser.getAddressList().isEmpty()){
            addressList.addAll(mUser.getAddressList());
        }
        Map<String,Object> requestParamMap = new HashMap<>();
        requestParamMap.put("addressList", JsonUtil.objectToJson(addressList));
        updateUserProperties(requestParamMap, networkUtil, new NetworkUtil.NetworkCallbak() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.w(TAG, "onFailure addAddress", e);
                mDialog.dismiss();
                ExampleUtil.showToast(AddAddressActivity.this, getResources().getString(R.string.update_user_properties_failed), Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (needLogin(response)) {
                    login();
                } else {
                    UserResult userResult = JsonUtil.jsoToObject(response.body().byteStream(), UserResult.class);
                    if (userResult.getCode() == ResultCode.SUCCESS.getCode() && userResult.getData() != null){
                        mUser.setAddressList(addressList);
                        PreferencesUtil.getInstance().setUser(mUser);
                        mDialog.dismiss();
                        ExampleUtil.showToast(AddAddressActivity.this, getResources().getString(R.string.update_user_properties_success), Toast.LENGTH_SHORT);
                        Log.i(TAG, "onResponse: add address success");
                    }else {
                        mDialog.dismiss();
                        ExampleUtil.showToast(AddAddressActivity.this, userResult.getMessage(), Toast.LENGTH_SHORT);
                        Log.w(TAG, "onResponse: add address failed " + userResult.toString());
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CONTACTS:
                    if (data != null) {
                        Uri uri = data.getData();
                        String name = null;
                        String phoneNumber = null;
                        ContentResolver contentResolver = getContentResolver();
                        Cursor cursor = contentResolver.query(uri,
                                null, null, null, null);
                        while (cursor.moveToNext()) {
                            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        cursor.close();
                        phoneNumber = phoneNumber.replaceAll(" ", "");
                        if (!TextUtils.isEmpty(name)) {
                            mNameEt.setText(name);
                        }
                        if (!TextUtils.isEmpty(phoneNumber)) {
                            mPhoneEt.setText(phoneNumber);
                        }
                    }
                    break;
                case REQUEST_CODE_LOCATION:
                    // 获取省市区以及详细地址信息
                    String province = data.getStringExtra("province");
                    String city = data.getStringExtra("city");
                    String district = data.getStringExtra("district");
                    String addressDetail = data.getStringExtra("addressDetail");
                    PreferencesUtil.getInstance().setPickedProvince(province);
                    PreferencesUtil.getInstance().setPickedCity(city);
                    PreferencesUtil.getInstance().setPickedDistrict(district);

                    StringBuffer addressInfoBuffer = new StringBuffer();
                    addressInfoBuffer.append(province).append(" ")
                            .append(city).append(" ")
                            .append(district);
                    mAddressInfoEt.setText(addressInfoBuffer.toString());
                    mAddressDetailEt.setText(addressDetail);
                    break;
            }
        }
    }

    /**
     * 动态权限
     */
    public void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //Android 6.0开始的动态权限，这里进行版本判断
            ArrayList<String> mPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {
                // 非初次进入App且已授权
                switch (requestCode) {
                    case REQUEST_CODE_CONTACTS:
                        showContacts();
                        break;
                    case REQUEST_CODE_LOCATION:
                        showMapPicker();
                        break;
                }

            } else {
                // 请求权限方法
                String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                // 这个触发下面onRequestPermissionsResult这个回调
                ActivityCompat.requestPermissions(activity, requestPermissions, requestCode);
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
            switch (requestCode) {
                case REQUEST_CODE_CONTACTS:
                    // 同意通讯录权限,开启通讯录服务
                    showContacts();
                    break;
                case REQUEST_CODE_LOCATION:
                    // 同意定位权限,进入地图选择器
                    showMapPicker();
                    break;
            }
        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(AddAddressActivity.this, permissions[0], requestCode);
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
            final ConfirmDialog mConfirmDialog = new ConfirmDialog(AddAddressActivity.this, getString(R.string.request_permission),
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

    /**
     * 跳转到通讯录
     */
    private void showContacts() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PICK");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android.cursor.dir/phone_v2");
        startActivityForResult(intent, REQUEST_CODE_CONTACTS);
    }

    /**
     * 进入地图选择页面
     */
    private void showMapPicker() {
        Intent intent = new Intent(AddAddressActivity.this, MapPickerActivity.class);
        // 是否发送定位
        // true:发送定位  false:无发送按钮，显示定位
        intent.putExtra("sendLocation", true);

        // 定位类型
        // 获取省市区信息
        intent.putExtra("locationType", Constant.LOCATION_TYPE_AREA);
        startActivityForResult(intent, REQUEST_CODE_LOCATION);
    }

}