package com.shiliu.caishifu.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.shiliu.caishifu.utils.PreferencesUtil;
import com.shiliu.caishifu.widget.LoadingDialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 修改性别
 *
 */
public class SetGenderActivity extends CommonActivity {
    private static final String TAG = "SetGenderActivity";

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.rl_male)
    RelativeLayout mMaleRl;

    @BindView(R.id.rl_female)
    RelativeLayout mFemaleRl;

    @BindView(R.id.tv_save)
    TextView mSaveTv;

    @BindView(R.id.iv_male)
    ImageView mMaleIv;

    @BindView(R.id.iv_female)
    ImageView mFemaleIv;

    User mUser;
    LoadingDialog mDialog;
    NetworkUtil networkUtil;
    int mSex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_gender_activity);
        ButterKnife.bind(this);

        initStatusBar();

        mUser = getUser();
        mDialog = new LoadingDialog(SetGenderActivity.this);
        networkUtil = NetworkUtil.getInstance(this);
        initView();
    }

    public void initView() {
        setTitleStrokeWidth(mTitleTv);
        renderSex();
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.rl_male, R.id.rl_female, R.id.tv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_male:
                mMaleIv.setVisibility(View.VISIBLE);
                mFemaleIv.setVisibility(View.GONE);
                mSex = Constant.USER_SEX_MALE;
                break;
            case R.id.rl_female:
                mMaleIv.setVisibility(View.GONE);
                mFemaleIv.setVisibility(View.VISIBLE);
                mSex = Constant.USER_SEX_FEMALE;
                break;
            case R.id.tv_save:
                updateUserSex(mSex);
                break;
        }
    }

    private void renderSex() {
        if (Constant.USER_SEX_MALE == mUser.getUserSex()) {
            // 男
            mMaleIv.setVisibility(View.VISIBLE);
            mFemaleIv.setVisibility(View.GONE);
        } else if (Constant.USER_SEX_FEMALE == mUser.getUserSex()) {
            // 女
            mMaleIv.setVisibility(View.GONE);
            mFemaleIv.setVisibility(View.VISIBLE);
        } else {
            mMaleIv.setVisibility(View.GONE);
            mFemaleIv.setVisibility(View.GONE);
        }
    }

    /**
     * 修改性别
     * @param userSex
     */
    private void updateUserSex(final int userSex) {
        mDialog.setMessage(getString(R.string.saving));
        mDialog.show();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userSex", Integer.valueOf(userSex));
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
                    if (userResult.getCode() == ResultCode.USERINFO_UPDATE_SUCCESS.getCode() && userResult.getData() != null) {
                        mUser.setUserSex(userSex);
                        PreferencesUtil.getInstance().setUser(mUser);
                        mDialog.dismiss();
                        ExampleUtil.showToast(SetGenderActivity.this, getResources().getString(R.string.update_user_properties_success), Toast.LENGTH_SHORT);
                        Log.i(TAG, "onResponse: update sex success");
                        renderSex();
                        finish();
                    }
                }
            }
        });
    }

}