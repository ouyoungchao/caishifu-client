package com.shiliu.caishifu.activity;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shiliu.caishifu.R;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.model.server.ResultCode;
import com.shiliu.caishifu.model.server.UserResult;
import com.shiliu.caishifu.utils.ExampleUtil;
import com.shiliu.caishifu.utils.JsonUtil;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.utils.PreferencesUtil;
import com.shiliu.caishifu.widget.LoadingDialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 修改个性签名
 *
 */
public class EditSignActivity extends BaseActivity {
    private static final String TAG = "EditSignActivity";

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.et_sign)
    EditText mSignEt;

    @BindView(R.id.tv_save)
    TextView mSaveTv;

    @BindView(R.id.tv_sign_length)
    TextView mSignLengthTv;

    final int maxSignLength = 30;

    NetworkUtil networkUtil;
    LoadingDialog mDialog;
    User mUser;

    @Override
    public int getContentView() {
        return R.layout.edit_sign_activity;
    }

    @Override
    public void initView() {
        initStatusBar();
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {
        mSignEt.addTextChangedListener(new TextChange());
        mSaveTv.setOnClickListener(view -> {
            mDialog.setMessage(getString(R.string.saving));
            mDialog.show();
            String userId = mUser.getUserId();
            String userNickName = mSignEt.getText().toString();
            updateUserSign(userNickName);
        });
    }

    @Override
    public void initData() {
        PreferencesUtil.getInstance().init(this);
        networkUtil = NetworkUtil.getInstance(this);
        mDialog = new LoadingDialog(EditSignActivity.this);
        mUser = getUser();

        mSignEt.setText(mUser.getUserSign());
        // 剩余可编辑字数
        int leftSignLength = maxSignLength - mSignEt.length();
        if (leftSignLength >= 0) {
            mSignLengthTv.setText(String.valueOf(leftSignLength));
        }

        // 光标移至最后
        CharSequence charSequence = mSignEt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newSign = mSignEt.getText().toString();
            String oldSign = mUser.getUserSign() == null ? "" : mUser.getUserSign();
            // 是否填写
            boolean isSignHasText = mSignEt.length() > 0;
            // 是否修改
            boolean isSignChanged = !oldSign.equals(newSign);

            if (isSignHasText && isSignChanged) {
                // 可保存
                mSaveTv.setTextColor(0xFFFFFFFF);
                mSaveTv.setEnabled(true);
            } else {
                // 不可保存
                mSaveTv.setTextColor(getColor(R.color.btn_text_default_color));
                mSaveTv.setEnabled(false);
            }

            int leftSignLength = maxSignLength - mSignEt.length();
            if (leftSignLength >= 0) {
                mSignLengthTv.setText(String.valueOf(leftSignLength));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // 是否填写
            int leftSignLength = maxSignLength - mSignEt.length();
            if (leftSignLength >= 0) {
                mSignLengthTv.setText(String.valueOf(leftSignLength));
            }
        }
    }

    public void back(View view) {
        finish();
    }

    private void updateUserSign(final String userSign) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userSign", userSign);

        updateUserProperties(paramMap, networkUtil, new NetworkUtil.NetworkCallbak() {
            @Override
            public void onFailure(Call call, IOException e) {
                mDialog.dismiss();
                Log.w(TAG, "onFailure update user's sex ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (needLogin(response)) {
                    login();
                } else {
                    UserResult userResult = JsonUtil.jsoToObject(response.body().byteStream(), UserResult.class);
                    if (userResult.getCode() == ResultCode.SUCCESS.getCode() && userResult.getData() != null) {
                        mUser.setUserSign(userSign);
                        PreferencesUtil.getInstance().setUser(mUser);
                        ExampleUtil.showToast(EditSignActivity.this, userResult.getMessage(), Toast.LENGTH_SHORT);
                        Log.i(TAG, "onResponse: update sign success");
                        finish();
                    } else{
                        ExampleUtil.showToast(EditSignActivity.this, userResult.getMessage(), Toast.LENGTH_SHORT);
                        Log.i(TAG, "onResponse: update sign failed");
                        mDialog.dismiss();
                    }
                }
            }
        });
    }
}
