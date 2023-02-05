package com.shiliu.caishifu.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.shiliu.caishifu.R;
import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.model.server.ResultCode;
import com.shiliu.caishifu.model.server.UserResult;
import com.shiliu.caishifu.utils.ExampleUtil;
import com.shiliu.caishifu.utils.JsonUtil;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.utils.OssUtil;
import com.shiliu.caishifu.utils.PreferencesUtil;
import com.shiliu.caishifu.utils.StatusBarUtil;
import com.shiliu.caishifu.widget.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 更改名字
 *
 * @author zhou
 */
public class EditNameActivity extends CommonActivity {
    private static final String TAG = "EditNameActivity";

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.et_nick)
    EditText mNickNameEt;

    @BindView(R.id.v_nick)
    View mNickView;

    @BindView(R.id.tv_save)
    TextView mSaveTv;

    NetworkUtil networkUtil;
    LoadingDialog mDialog;
    User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_name_activity);
        ButterKnife.bind(this);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(EditNameActivity.this, R.color.common_bg_light_grey);

        PreferencesUtil.getInstance().init(this);

        mUser = PreferencesUtil.getInstance().getUser();
        networkUtil = NetworkUtil.getInstance(this);
        mDialog = new LoadingDialog(EditNameActivity.this);
        initView();

        mSaveTv.setOnClickListener(view -> {
            mDialog.setMessage(getString(R.string.saving));
            mDialog.show();
            String userNickName = mNickNameEt.getText().toString();
            updateUserNickName(userNickName);
        });
    }

    public void initView() {
        setTitleStrokeWidth(mTitleTv);

        mNickNameEt.setText(mUser.getUserNickName());
        // 光标移至最后
        CharSequence charSequence = mNickNameEt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
        mNickNameEt.addTextChangedListener(new TextChange());

        mNickNameEt.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                mNickView.setBackgroundColor(getColor(R.color.divider_green));
            } else {
                mNickView.setBackgroundColor(getColor(R.color.divider_grey));
            }
        });
    }

    public void back(View view) {
        finish();
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newNickName = mNickNameEt.getText().toString();
            String oldNickName = mUser.getUserNickName();
            // 是否填写
            boolean isNickNameHasText = newNickName.length() > 0;
            // 是否修改
            boolean isNickNameChanged = !oldNickName.equals(newNickName);

            if (isNickNameHasText && isNickNameChanged) {
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

    private void updateUserNickName(final String userNickName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userNickName", userNickName);
        updateUserProperties(paramMap, networkUtil, new NetworkUtil.NetworkCallbak() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: update userName ", e);
                mDialog.dismiss();
                ExampleUtil.showToast(EditNameActivity.this, getResources().getString(R.string.update_user_properties_failed), Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (needLogin(response)) {
                    login();
                } else{
                    UserResult userResult = JsonUtil.jsoToObject(response.body().byteStream(), UserResult.class);
                    if (userResult.getCode() == ResultCode.USERINFO_UPDATE_SUCCESS.getCode() && userResult.getData() != null) {
                        mUser.setUserNickName(userNickName);
                        PreferencesUtil.getInstance().setUser(mUser);
                        mDialog.dismiss();
                        ExampleUtil.showToast(EditNameActivity.this, getResources().getString(R.string.update_user_properties_success), Toast.LENGTH_SHORT);
                        Log.i(TAG, "onResponse: update nickName success");
                        finish();
                    }else {
                        mDialog.dismiss();
                        ExampleUtil.showToast(EditNameActivity.this, getResources().getString(R.string.update_user_properties_failed), Toast.LENGTH_SHORT);
                        Log.w(TAG, "onResponse: update nickName failed");
                    }
                }
            }
        });
    }


}