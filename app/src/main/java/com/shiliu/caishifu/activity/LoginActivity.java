package com.shiliu.caishifu.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.dao.UserDao;
import com.shiliu.caishifu.model.DeviceInfo;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.utils.CountDownTimerUtils;
import com.shiliu.caishifu.utils.DeviceInfoUtil;
import com.shiliu.caishifu.utils.ExampleUtil;
import com.shiliu.caishifu.utils.JsonUtil;
import com.shiliu.caishifu.utils.MD5Util;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.utils.PreferencesUtil;
import com.shiliu.caishifu.utils.ValidateUtil;
import com.shiliu.caishifu.widget.LoadingDialog;

import com.shiliu.caishifu.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 登录
 */
public class LoginActivity extends BaseLoginActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.et_phone)
    EditText mPhoneEt;

    @BindView(R.id.et_verification_code)
    EditText mVerificationEt;

    @BindView(R.id.btn_verification_code)
    Button mVerificationBtn;

    @BindView(R.id.et_account)
    EditText mAccountEt;

    @BindView(R.id.et_password)
    EditText mPasswordEt;

    @BindView(R.id.btn_login)
    Button mLoginBtn;

    @BindView(R.id.tv_login_type)
    TextView mLoginTypeTv;

    @BindView(R.id.ll_login_via_mobile_number)
    LinearLayout mLoginViaVerificationCode;

    @BindView(R.id.ll_login_via_account_id)
    LinearLayout mLoginViaAccountId;

    LoadingDialog mDialog;

    UserDao mUserDao;

    private NetworkUtil networkUtil;

    private String mLoginType = Constant.LOGIN_TYPE_PHONE_AND_PASSWORD;

    @Override
    public int getContentView() {
        return R.layout.login_activity;
    }


    @Override
    public void initView() {
        initStatusBar();
    }

    @Override
    public void initListener() {
        mPhoneEt.addTextChangedListener(new TextChange());
        mAccountEt.addTextChangedListener(new TextChange());
        mPasswordEt.addTextChangedListener(new TextChange());
        mVerificationEt.addTextChangedListener(new TextChange());
    }

    @Override
    public void initData() {
        networkUtil = NetworkUtil.getInstance(this);
        mDialog = new LoadingDialog(LoginActivity.this);
        mUserDao = new UserDao();
    }


    public void back(View view) {
        finish();
    }

    @OnClick({R.id.tv_login_type, R.id.btn_login,R.id.btn_verification_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login_type:
                // 登录方式切换
                // 帐号密码登录切换成验证码登录
                if (Constant.LOGIN_TYPE_PHONE_AND_PASSWORD.equals(mLoginType)) {
                    // 当前登录方式手机号登录
                    // 切换为其他账号登录
                    mLoginViaVerificationCode.setVisibility(View.VISIBLE);
                    mLoginViaAccountId.setVisibility(View.GONE);
                    mLoginType = Constant.LOGIN_TYPE_PHONE_AND_VERIFICATION_CODE;
                    mPhoneEt.setText("");

                    mTitleTv.setText(getString(R.string.login_via_verification_code));
                    mLoginTypeTv.setText(getString(R.string.login_via_account_password));

                } else {
                    // 验证码登录，切换为帐号登录
                    // 切换为手
                    mLoginViaAccountId.setVisibility(View.VISIBLE);
                    mLoginViaVerificationCode.setVisibility(View.GONE);

                    mLoginType = Constant.LOGIN_TYPE_PHONE_AND_PASSWORD;
                    mAccountEt.setText("");
                    mPasswordEt.setText("");

                    mTitleTv.setText(getString(R.string.login_via_account_password));
                    mLoginTypeTv.setText(getString(R.string.login_via_verification_code));
                }
                break;

            case R.id.btn_login:
                if (Constant.LOGIN_TYPE_PHONE_AND_PASSWORD.equals(mLoginType)) {
                    String telephone = mAccountEt.getText().toString();
                    String password = mPasswordEt.getText().toString();
                    login(mLoginType, telephone, password, null);
                } else {
                    String telephone = mAccountEt.getText().toString();
                    String verificationCode = mVerificationEt.getText().toString();
                    login(mLoginType, telephone, null, verificationCode);
                }
                break;
            case R.id.btn_verification_code:
                CountDownTimerUtils countDownTimerUtils = new CountDownTimerUtils(mVerificationBtn,60000,1000);
                countDownTimerUtils.start();
                String telephone = mPhoneEt.getText().toString();
                obtainVerificationCode(telephone);
                break;
        }
    }

    /**
     * 获取验证码
     *
     * @param telephone
     */
    private void obtainVerificationCode(String telephone) {
    }


    /**
     * 登录
     *
     * @param loginType        登录类型
     * @param telephone        手机号
     * @param password         密码
     * @param verificationCode 验证码
     */
    private void login(final String loginType, String telephone, String password, String verificationCode) {
        mDialog.setMessage(getString(R.string.logging_in));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        DeviceInfo deviceInfo = DeviceInfoUtil.getInstance().getDeviceInfo(this);
        String url = Constant.BASE_URL + "users/login";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("loginType", loginType);
        paramMap.put("telephone", telephone);
        paramMap.put("password", MD5Util.encode(password, "utf8"));
        paramMap.put("deviceInfo", JSON.toJSONString(deviceInfo));
        networkUtil.doPostRequest(url, JsonUtil.objectToJson(paramMap), new NetworkUtil.NetworkCallbak() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure login ", e);
                ExampleUtil.initToast(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT);
                mDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "server response: " + response);
                // TODO: 2022/10/15
                User user = null;
//                final User user = JSON.parseObject(response, User.class);
                Log.d(TAG, "userId:" + user.getUserId());

                // 登录极光im
                JMessageClient.login(user.getUserId(), user.getUserImPassword(), new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        if (responseCode == 0) {
                            // 极光im登录成功
                            // 登录成功后设置user和isLogin至sharedpreferences中
                            PreferencesUtil.getInstance().setUser(user);
                            PreferencesUtil.getInstance().setLogin(true);
                            // 注册jpush
//                            JPushInterface.setAlias(PhoneLoginFinalActivity.this, sequence, user.getUserId());
//                            List<User> contactList = user.getContactList();
//                            if (!CollectionUtils.isEmpty(contactList)) {
//                                for (User contact : contactList) {
//                                    if (null != contact) {
//                                        contact.setIsFriend(Constant.IS_FRIEND);
//                                        mUserDao.saveUser(contact);
//                                    }
//                                }
//                            }
//                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // 极光im登录失败
//                            Toast.makeText(PhoneLoginFinalActivity.this,
//                                    R.string.account_or_password_error, Toast.LENGTH_SHORT)
//                                    .show();
                        }
                        // 上面都是耗时操作
                        mDialog.dismiss();
                    }
                });

            }
        });
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (Constant.LOGIN_TYPE_PHONE_AND_PASSWORD.equals(mLoginType)) {
                // 手机号登录
                boolean phoneEtHasText = ValidateUtil.isValidChinesePhone(mAccountEt.getText().toString());
                boolean passwordEtHasText = ValidateUtil.validatePassword(mPasswordEt.getText().toString());
                if (phoneEtHasText && passwordEtHasText) {
                    // "下一步"按钮可用
                    mLoginBtn.setBackgroundResource(R.drawable.btn_enable);
                    mLoginBtn.setTextColor(getColor(R.color.btn_text_enable));
                    mLoginBtn.setEnabled(true);
                } else {
                    // "下一步"按钮不可用
                    mLoginBtn.setBackgroundResource(R.drawable.btn_disable);
                    mLoginBtn.setTextColor(getColor(R.color.btn_text_disable));
                    mLoginBtn.setEnabled(false);
                }
            } else {
                // 验证码登录
                boolean phoneEtHasText = ValidateUtil.isValidChinesePhone(mPhoneEt.getText().toString());
                if (phoneEtHasText) {
                    mVerificationBtn.setEnabled(true);
                    mVerificationBtn.setTextColor(getColor(R.color.btn_text_enable));
                    mVerificationBtn.setBackgroundResource(R.drawable.btn_enable);
                } else {
                    mVerificationBtn.setEnabled(false);
                    mVerificationBtn.setTextColor(getColor(R.color.btn_text_disable));
                    mVerificationBtn.setBackgroundResource(R.drawable.btn_disable);
                }
                boolean verificationEtHasText = mVerificationEt.getText().length() == 4;
                if (phoneEtHasText && verificationEtHasText) {
                    // "登录"按钮可用
                    mLoginBtn.setBackgroundResource(R.drawable.btn_enable);
                    mLoginBtn.setTextColor(getColor(R.color.btn_text_enable));
                    mLoginBtn.setEnabled(true);
                } else {
                    // "登录"按钮不可用
                    mLoginBtn.setBackgroundResource(R.drawable.btn_disable);
                    mLoginBtn.setTextColor(getColor(R.color.btn_text_disable));
                    mLoginBtn.setEnabled(false);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

}