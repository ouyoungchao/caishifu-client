package com.shiliu.caishifu.fragement;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.activity.FriendsCircleActivity;
import com.shiliu.caishifu.activity.MainActivity;
import com.shiliu.caishifu.activity.MomentsActivity;
import com.shiliu.caishifu.widget.ConfirmDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DiscoverFragment extends BaseFragment {

    @BindView(R.id.tv_buyer)
    TextView mBuyerTv;

    @BindView(R.id.tv_seller)
    TextView mSellerTv;


    Fragment sellVegetableFragment = new SellVegetableFragment();

    Fragment buyVegetableFragment = new BuyVegetableFragment();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitleStrokeWidth(mBuyerTv);
        setTitleStrokeWidth(mSellerTv);
       /* if (PreferencesUtil.getInstance().isOpenPeopleNearby()) {
            mOpenPeopleNearbyIv.setVisibility(View.VISIBLE);
        } else {
            mOpenPeopleNearbyIv.setVisibility(View.GONE);
        }*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discover_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MainActivity.REQUEST_CODE_SCAN) {
                String isbn = data.getStringExtra("CaptureIsbn");
                /*if (!TextUtils.isEmpty(isbn)) {
                    if (isbn.contains("http")) {
                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        intent.putExtra(WebViewActivity.RESULT, isbn);
                        startActivity(intent);
                    } else {
                        try {
                            QrCodeContent qrCodeContent = JSON.parseObject(isbn, QrCodeContent.class);
                            if (QrCodeContent.QR_CODE_TYPE_USER.equals(qrCodeContent.getType())) {
                                startActivity(new Intent(getActivity(), UserInfoActivity.class).
                                        putExtra("userId", String.valueOf(qrCodeContent.getContentMap().get("userId"))));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }*/
            }
        }
    }

    /**
     * 动态权限
     */
    public void requestPerms(Activity activity, String[] permissions, int requestCode) {
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
                    case MainActivity.REQUEST_CODE_CAMERA:
                        break;
                    case MainActivity.REQUEST_CODE_LOCATION:
                        break;
                }

            } else {
                // 请求权限方法
                String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                // 这个触发下面onRequestPermissionsResult这个回调
                requestPermissions(requestPermissions, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            // 同意权限做的处理,开启服务提交通讯录
            switch (requestCode) {
                case MainActivity.REQUEST_CODE_CAMERA:
                    break;
                case MainActivity.REQUEST_CODE_LOCATION:
                    break;
            }
        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(getActivity(), permissions[0], requestCode);
        }
    }

    public void handleRejectPermission(final Activity context, String permission, int requestCode) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            String content = "";
            // 非初次进入App且已授权
            switch (requestCode) {
                case MainActivity.REQUEST_CODE_CAMERA:
                    content = getString(R.string.request_permission_camera);
                    break;
                case MainActivity.REQUEST_CODE_LOCATION:
                    content = getString(R.string.request_permission_location);
                    break;
            }
            final ConfirmDialog mConfirmDialog = new ConfirmDialog(getActivity(), "权限申请",
                    content,
                    "去设置", "取消", context.getColor(R.color.navy_blue));
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



    @OnClick({R.id.tv_seller,R.id.tv_buyer})
    public void onClick(View view) {
        String[] permissions;
        FragmentTransaction trx = this.getActivity().getSupportFragmentManager()
                .beginTransaction();
        switch (view.getId()) {
            case R.id.tv_seller:
                trx.hide(buyVegetableFragment);
                if(!sellVegetableFragment.isAdded()) {
                    trx.add(R.id.rl_discover_fragment_container, sellVegetableFragment);
                }
                trx.show(sellVegetableFragment).commit();
                mBuyerTv.setBackgroundColor(getResources().getColor(R.color.common_top_bar));
                mSellerTv.setBackgroundColor(getResources().getColor(R.color.common_bg));
                break;
            case R.id.tv_buyer:
                trx.hide(sellVegetableFragment);
                if(!buyVegetableFragment.isAdded()) {
                    trx.add(R.id.rl_discover_fragment_container, buyVegetableFragment);
                }
                trx.show(buyVegetableFragment).commit();
                mSellerTv.setBackgroundColor(getResources().getColor(R.color.common_top_bar));
                mBuyerTv.setBackgroundColor(getResources().getColor(R.color.common_bg));
                break;
            default: break;
        }
    }

}