package com.shiliu.caishifu.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.dao.UserDao;
import com.shiliu.caishifu.model.ResponseMsg;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.utils.CommonUtil;
import com.shiliu.caishifu.utils.ExampleUtil;
import com.shiliu.caishifu.utils.FileUtil;
import com.shiliu.caishifu.utils.JsonUtil;
import com.shiliu.caishifu.utils.MD5Util;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.utils.PreferencesUtil;
import com.shiliu.caishifu.utils.ValidateUtil;
import com.shiliu.caishifu.widget.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


public class RegisterActivity extends BaseActivity2 {

    private static final String TAG = "RegisterActivity";
    public static int sequence = 1;

    private NetworkUtil networkUtil;

    //注册头像上传按钮
    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    //买家logo
    @BindView(R.id.register_buyer)
    ImageView selectBuye;

    //卖家logo
    @BindView(R.id.register_seller)
    ImageView selectSeller;

    //同意logo
    @BindView(R.id.iv_agreement)
    ImageView mAgreementIv;

    //同意勾选框
    @BindView(R.id.tv_agreement)
    TextView mAgreementTv;

    //昵称
    @BindView(R.id.et_nick_name)
    EditText mNickNameEt;

    //电话号码
    @BindView(R.id.et_phone)
    EditText mPhoneEt;

    //密码
    @BindView(R.id.et_password)
    EditText mPasswordEt;

    //注册按钮
    @BindView(R.id.btn_register)
    Button mRegisterBtn;

    //加载会话
    LoadingDialog mDialog;

    //用户数据库操作接口
    UserDao mUserDao;

    private static final int UPDATE_AVATAR_BY_TAKE_CAMERA = 1;
    private static final int UPDATE_AVATAR_BY_ALBUM = 2;

    private String imageName;
    private String userAvatar;
    /**
     * 是否同意协议
     */
    private boolean mIsAgree = false;

    /**
     * 是否买家
     */
    private boolean isBuyer = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        ButterKnife.bind(this);

        initStatusBar();

        networkUtil = NetworkUtil.getInstance(this);
        mDialog = new LoadingDialog(RegisterActivity.this);
        mUserDao = new UserDao();
        initView();
    }

    private void initView() {
        String agreement = "<font color=" + "\"" + "#AAAAAA" + "\">" + "已阅读并同意" + "</font>" + "<u>"
                + "<font color=" + "\"" + "#576B95" + "\">" + "《软件许可及服务协议》"
                + "</font>" + "</u>";
        mAgreementTv.setText(Html.fromHtml(agreement));

        mNickNameEt.addTextChangedListener(new TextChange());
        mPhoneEt.addTextChangedListener(new TextChange());
        mPasswordEt.addTextChangedListener(new TextChange());
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.sdv_avatar, R.id.iv_agreement, R.id.btn_register, R.id.register_seller, R.id.register_buyer})
    public void onClick(View view) {
        switch (view.getId()) {
            //上传按钮
            case R.id.sdv_avatar:
                showPhotoDialog();
                break;
            //买家按钮
            case R.id.register_buyer:
                if (isBuyer) {
                    selectBuye.setBackgroundResource(R.mipmap.icon_choose_false);
                    selectSeller.setBackgroundResource(R.mipmap.icon_choose_true);
                    isBuyer = false;
                } else {
                    selectBuye.setBackgroundResource(R.mipmap.icon_choose_true);
                    selectSeller.setBackgroundResource(R.mipmap.icon_choose_false);
                    isBuyer = true;
                }
                break;
            //卖家按钮
            case R.id.register_seller:
                if (!isBuyer) {
                    selectBuye.setBackgroundResource(R.mipmap.icon_choose_true);
                    selectSeller.setBackgroundResource(R.mipmap.icon_choose_false);
                    isBuyer = true;
                } else {
                    selectBuye.setBackgroundResource(R.mipmap.icon_choose_false);
                    selectSeller.setBackgroundResource(R.mipmap.icon_choose_true);
                    isBuyer = false;
                }
                break;
            //同意按钮
            case R.id.iv_agreement:
                if (mIsAgree) {
                    mAgreementIv.setBackgroundResource(R.mipmap.icon_choose_false);
                    mIsAgree = false;
                } else {
                    mAgreementIv.setBackgroundResource(R.mipmap.icon_choose_true);
                    mIsAgree = true;
                }
                checkSubmit();
                break;
            //注册按钮
            case R.id.btn_register:
                mDialog.setMessage(getString(R.string.registering));
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                String nickName = mNickNameEt.getText().toString();
                String telephone = mPhoneEt.getText().toString();
                String password = mPasswordEt.getText().toString();
                if (!ValidateUtil.validatePassword(password)) {
                    mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, R.string.password_rules,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                register(nickName, telephone, password, isBuyer, userAvatar);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPDATE_AVATAR_BY_TAKE_CAMERA:
                    final File file = new File(Environment.getExternalStorageDirectory(), imageName);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> imageList = FileUtil.uploadFile(Constant.BASE_URL + "oss/file", file.getPath());
                            if (null != imageList && imageList.size() > 0) {
                                userAvatar = imageList.get(0);
                            }
                        }
                    }).start();
                    mAvatarSdv.setImageURI(Uri.fromFile(file));
                    break;
                case UPDATE_AVATAR_BY_ALBUM:
                    if (data != null) {
                        Uri uri = data.getData();
                        final String filePath = FileUtil.getFilePathByUri(this, uri);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<String> imageList = FileUtil.uploadFile(Constant.BASE_URL + "oss/file", filePath);
                                if (null != imageList && imageList.size() > 0) {
                                    userAvatar = imageList.get(0);
                                }
                            }
                        }).start();

                        mAvatarSdv.setImageURI(uri);
                    }
                    break;
            }
        }
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkSubmit();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    /**
     * 显示上传头像对话框
     */
    private void showPhotoDialog() {
        final AlertDialog photoDialog = new AlertDialog.Builder(this).create();
        photoDialog.show();
        Window window = photoDialog.getWindow();
        window.setContentView(R.layout.alert_abandon_dialog);
        TextView mTakePicTv = window.findViewById(R.id.tv_content1);
        mTakePicTv.setText("拍照");
        mTakePicTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageName = CommonUtil.generateId() + ".png";
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                        new File(Environment.getExternalStorageDirectory(), imageName)));
                startActivityForResult(cameraIntent, UPDATE_AVATAR_BY_TAKE_CAMERA);
                photoDialog.dismiss();
            }
        });
        TextView mAlbumTv = window.findViewById(R.id.tv_content2);
        mAlbumTv.setText("相册");
        mAlbumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, UPDATE_AVATAR_BY_ALBUM);
                photoDialog.dismiss();
            }
        });
    }

    /**
     * 注册
     *
     * @param nickName 昵称
     *                 +
     * @param telephone    手机号
     * @param password 密码
     * @param isBuyer  是否为采购
     */
    private void register(String nickName, String telephone, String password, boolean isBuyer, String userAvatar) {
        String url = Constant.BASE_URL + "caishifu/register";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("nickName", nickName);
        paramMap.put("telephone", telephone);
        paramMap.put("isBuyer", isBuyer ? "true" : "false");
        paramMap.put("password", MD5Util.encode(password, "utf8"));
        paramMap.put("icon",userAvatar);
        networkUtil.doPostRequest(url, JsonUtil.objectToJson(paramMap), new NetworkUtil.NetworkCallbak() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + url,e);
                mDialog.dismiss();
                ExampleUtil.initToast(RegisterActivity.this, getResources().getString(R.string.register_failed), Toast.LENGTH_SHORT);
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.toString());
                int code = response.code();
                switch (code) {
                    case 200:
                        final User user = JsonUtil.jsoToObject(response.body().byteStream(), User.class);
                        Log.d(TAG, "onResponse userId:" + user.getUserId());
                        // 登录成功后设置user和isLogin至sharedpreferences中
                        PreferencesUtil.getInstance().setUser(user);
//                        PreferencesUtil.getInstance().setLogin(true);
                        // 注册jpush实现消息推送
                        // TODO: 2022/9/12
//                        JPushInterface.setAlias(RegisterActivity.this, sequence, user.getUserId());
                        List<User> friendList = user.getContactList();
                        if (null != friendList && friendList.size() > 0) {
                            for (User userFriend : friendList) {
                                if (null != userFriend) {
                                    userFriend.setIsFriend(Constant.IS_FRIEND);
                                    mUserDao.saveUser(userFriend);
                                }
                            }
                        }
                        // TODO: 2022/9/12
//                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                        // 登录极光im
                        // TODO: 2022/9/12
                       /* JMessageClient.login(user.getUserId(), user.getUserImPassword(), new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                Log.d(TAG, "responseCode: " + responseCode + ", responseMessage: " + responseMessage);
                            }
                        });*/
                    case 400:
                        String responseCode = response.headers().get("responseCode");
                        if (ResponseMsg.USER_EXISTS.getResponseCode().equals(responseCode)) {
                            Toast.makeText(RegisterActivity.this,
                                    R.string.user_exists, Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            mDialog.dismiss();
                            ExampleUtil.initToast(RegisterActivity.this, getResources().getString(R.string.account_or_password_error), Toast.LENGTH_SHORT);
                        }

                }
            }
        });
    }

    /**
     * 表单是否填充完成(昵称,手机号,密码,是否同意协议)
     */
    private void checkSubmit() {
        boolean nickNameHasText = mNickNameEt.getText().toString().length() > 0;
        boolean phoneHasText = mPhoneEt.getText().toString().length() > 0;
        boolean passwordHasText = mPasswordEt.getText().toString().length() > 0;
        if (nickNameHasText && phoneHasText && passwordHasText && mIsAgree) {
            // 注册按钮可用
            mRegisterBtn.setBackgroundColor(getResources().getColor(R.color.register_btn_bg_enable));
            mRegisterBtn.setTextColor(getResources().getColor(R.color.register_btn_text_enable));
            mRegisterBtn.setEnabled(true);
        } else {
            // 注册按钮不可用
            mRegisterBtn.setBackgroundColor(getResources().getColor(R.color.register_btn_bg_disable));
            mRegisterBtn.setTextColor(getResources().getColor(R.color.register_btn_text_disable));
            mRegisterBtn.setEnabled(false);
        }
    }
}
