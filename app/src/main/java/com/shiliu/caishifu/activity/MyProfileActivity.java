package com.shiliu.caishifu.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.engine.GlideEngine;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.model.server.UserResult;
import com.shiliu.caishifu.utils.CommonUtil;
import com.shiliu.caishifu.utils.CollectionUtils;
import com.shiliu.caishifu.utils.ExampleUtil;
import com.shiliu.caishifu.utils.JsonUtil;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.utils.OssUtil;
import com.shiliu.caishifu.utils.PreferencesUtil;
import com.shiliu.caishifu.utils.ThreadUtil;
import com.shiliu.caishifu.widget.ConfirmDialog;
import com.shiliu.caishifu.widget.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 个人信息界面
 *
 */
public class MyProfileActivity extends BaseActivity {
    private static final String TAG = "MyProfileActivity";

    // 头像
    @BindView(R.id.rl_avatar)
    RelativeLayout mAvatarRl;

    // 昵称
    @BindView(R.id.rl_nick_name)
    RelativeLayout mNickNameRl;


    @BindView(R.id.rl_sex)
    RelativeLayout mSexRl;

    @BindView(R.id.rl_sign)
    RelativeLayout mSignRl;

    @BindView(R.id.tv_sex)
    TextView mSexTv;

    @BindView(R.id.tv_sign)
    TextView mSignTv;

    // 我的地址
    @BindView(R.id.rl_address)
    RelativeLayout mAddressRl;

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.tv_nick_name)
    TextView mNickNameTv;


    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    NetworkUtil networkUtil;
    LoadingDialog mDialog;

    private static final int UPDATE_AVATAR_BY_TAKE_CAMERA = 1;
    private static final int UPDATE_AVATAR_BY_ALBUM = 2;
    private static final int UPDATE_USER_NICK_NAME = 3;
    private static final int UPDATE_USER_AVATAR = 5;
    private static final int UPDATE_USER_SEX = 6;
    private static final int UPDATE_USER_SIGN = 7;

    User mUser;
    String mImageName;

    @Override
    public int getContentView() {
        return R.layout.my_profile_activity;
    }

    @Override
    public void initView() {
        initStatusBar();
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        networkUtil = NetworkUtil.getInstance(this);
        mDialog = new LoadingDialog(MyProfileActivity.this);

        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mNickNameTv.setText(mUser.getUserNickName());

        String userAvatar = mUser.getUserAvatar();
        if (!TextUtils.isEmpty(userAvatar)) {
            String resizeAvatarUrl = OssUtil.resize(userAvatar);
            mAvatarSdv.setImageURI(resizeAvatarUrl);
        }

        if (Constant.USER_SEX_MALE == mUser.getUserSex()) {
            mSexTv.setText(getString(R.string.sex_male));
        } else if (Constant.USER_SEX_FEMALE == mUser.getUserSex()) {
            mSexTv.setText(getString(R.string.sex_female));
        }
        mSignTv.setText(mUser.getUserSign());

        initCamera();
    }

    @OnClick({R.id.rl_avatar,R.id.sdv_avatar,R.id.rl_nick_name,
            R.id.rl_sex, R.id.rl_sign, R.id.rl_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_avatar:
                showPhotoDialog();
                break;
            case R.id.sdv_avatar:
                Intent intent = new Intent(this, BigImageActivity.class);
                intent.putExtra("imgUrl", mUser.getUserAvatar());
                startActivity(intent);
                break;
            case R.id.rl_nick_name:
                // 昵称
                startActivityForResult(new Intent(this, EditNameActivity.class), UPDATE_USER_NICK_NAME);
                break;
            case R.id.rl_address:
                startActivity(new Intent(this, MyAddressActivity.class));
                break;
            case R.id.rl_sex:
                startActivityForResult(new Intent(this, SetGenderActivity.class), UPDATE_USER_SEX);
                break;
            case R.id.rl_sign:
                // 签名
                startActivityForResult(new Intent(this, EditSignActivity.class),UPDATE_USER_SIGN);
                break;
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final User user = PreferencesUtil.getInstance().getUser();
            switch (requestCode) {
                case UPDATE_USER_NICK_NAME:
                    // 昵称
                    mNickNameTv.setText(user.getUserNickName());
                    break;
                case UPDATE_USER_AVATAR:
                    mDialog.setMessage("正在上传头像");
                    mDialog.show();
                    mDialog.setCanceledOnTouchOutside(false);
                    // 返回对象集合: 包含图片的宽、高、大小、用户是否选中原图选项等信息
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    if (!CollectionUtils.isEmpty(resultPhotos)) {
                        ThreadUtil.getExecutors().submit(new Thread(() -> {
                                updateUserAvatar(resultPhotos.get(0).path);
                        }));
                    }
                    break;
                case UPDATE_USER_SEX:
                    mSexTv.setText(user.getUserSex());
                    break;
                case UPDATE_USER_SIGN:
                    mSignTv.setText(user.getUserSign());
                    break;
            }
        }
    }

    /**
     * 显示修改头像对话框
     */
    private void showPhotoDialog() {
        EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                .setFileProviderAuthority("com.shiliu.caishifu.fileprovider")
                .setCount(1)//参数说明：最大可选数，默认1
                .start(UPDATE_USER_AVATAR);
    }

    /**
     * 上传头像
     * @param filePath
     */
    private void updateUserAvatar(final String filePath) {
        String url = Constant.BASE_URL + "caishifu/updateMember";
        File file = new File(filePath);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",file.getName(),fileBody)
                .build();
        networkUtil.doPostWithMultiBody(url, getAuthorizationHeader(), requestBody, new NetworkUtil.NetworkCallbak() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: update user avatar", e);
                mDialog.dismiss();
                ExampleUtil.showToast(MyProfileActivity.this, getResources().getString(R.string.update_user_avatar_failed), Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                UserResult userResult = JsonUtil.jsoToObject(response.body().byteStream(), UserResult.class);
                if(needLogin(response)){
                    login();
                }else {
                    User user = (User) JsonUtil.jsoToObject((String) userResult.getData().toString(), User.class);
                    mUser.setUserAvatar(user.getUserAvatar());
                    PreferencesUtil.getInstance().setUser(mUser);
                    mDialog.dismiss();
                    ExampleUtil.showToast(MyProfileActivity.this, getResources().getString(R.string.update_user_avatar_success), Toast.LENGTH_SHORT);

                }
            }
        });
    }

    /**
     * android 7.0系统解决拍照的问题
     */
    private void initCamera() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
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
                case UPDATE_AVATAR_BY_TAKE_CAMERA:
                    showCamera();
                    break;
                case UPDATE_AVATAR_BY_ALBUM:
                    showAlbum();
                    break;
            }
        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(MyProfileActivity.this, permissions[0], requestCode);
        }
    }

    public void handleRejectPermission(final Activity context, String permission, int requestCode) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            String content = "";
            // 非初次进入App且已授权
            switch (requestCode) {
                case UPDATE_AVATAR_BY_TAKE_CAMERA:
                    content = getString(R.string.request_permission_camera);
                    break;
                case UPDATE_AVATAR_BY_ALBUM:
                    content = getString(R.string.request_permission_storage);
                    break;
            }

            final ConfirmDialog mConfirmDialog = new ConfirmDialog(MyProfileActivity.this, "权限申请",
                    content,
                    "去设置", "取消", getColor(R.color.navy_blue));
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
     * 跳转到相机
     */
    private void showCamera() {
        mImageName = CommonUtil.generateId() + ".png";
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                new File(Environment.getExternalStorageDirectory(), mImageName)));
        startActivityForResult(cameraIntent, UPDATE_AVATAR_BY_TAKE_CAMERA);
    }

    /**
     * 跳转到相册
     */
    private void showAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, UPDATE_AVATAR_BY_ALBUM);
    }

}