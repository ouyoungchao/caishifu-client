package com.shiliu.caishifu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shiliu.caishifu.R;
import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.utils.PreferencesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 更多信息
 *
 */
public class MyMoreProfileActivity extends CommonActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.rl_sex)
    RelativeLayout mSexRl;

    @BindView(R.id.rl_sign)
    RelativeLayout mSignRl;

    @BindView(R.id.tv_sex)
    TextView mSexTv;

    @BindView(R.id.tv_sign)
    TextView mSignTv;

    User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_more_profile_activity);
        ButterKnife.bind(this);
        initStatusBar();
        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        initView();
    }


    public void initView() {
        setTitleStrokeWidth(mTitleTv);
        String userSex = mUser.getUserSex();
        if (Constant.USER_SEX_MALE.equals(userSex)) {
            mSexTv.setText(getString(R.string.sex_male));
        } else if (Constant.USER_SEX_FEMALE.equals(userSex)) {
            mSexTv.setText(getString(R.string.sex_female));
        }
        mSignTv.setText(mUser.getUserSign());
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.rl_sex,R.id.rl_sign})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_sex:
                startActivity(new Intent(this, SetGenderActivity.class));
                break;
            case R.id.rl_sign:
                // 签名
                startActivity(new Intent(this, EditSignActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = PreferencesUtil.getInstance().getUser();
        if (Constant.USER_SEX_MALE.equals(user.getUserSex())) {
            mSexTv.setText(getString(R.string.sex_male));
        } else if (Constant.USER_SEX_FEMALE.equals(user.getUserSex())) {
            mSexTv.setText(getString(R.string.sex_female));
        } else {
            mSexTv.setText("");
        }

        // 个性签名
        mSignTv.setText(user.getUserSign());
    }

}