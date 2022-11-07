package com.shiliu.caishifu.fragement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.utils.OssUtil;
import com.shiliu.caishifu.utils.PreferencesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * tab - "我"
 */
public class MeFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @BindView(R.id.tv_name)
    TextView mNickNameTv;

    @BindView(R.id.tv_wx_id)
    TextView mWxIdTv;

    User mUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fregment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUser = PreferencesUtil.getInstance().getUser();
        initView();

    }

    private void initView() {
        mNickNameTv.setText(mUser.getUserNickName());
        String userWxId = mUser.getUserWxId() == null ? "" : mUser.getUserWxId();
        mWxIdTv.setText("帐号:" + userWxId);
        String userAvatar = mUser.getUserAvatar();
        if (!TextUtils.isEmpty(userAvatar)) {
            String resizeAvatarUrl = OssUtil.resize(mUser.getUserAvatar());
            mAvatarSdv.setImageURI(Uri.parse(resizeAvatarUrl));
        }
    }

    @OnClick({R.id.rl_me, R.id.rl_status, R.id.sdv_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            // 个人页面
            case R.id.rl_me:
//                startActivity(new Intent(getActivity(), MyProfileActivity.class));
                break;
            // 状态:
            case R.id.rl_status:
//                startActivity(new Intent(getActivity(), StatusActivity.class));
                break;
            // 头像点击放大
            case R.id.sdv_avatar:
//                Intent intent = new Intent(getActivity(), BigImageActivity.class);
//                intent.putExtra("imgUrl", mUser.getUserAvatar());
//                startActivity(intent);
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUser = PreferencesUtil.getInstance().getUser();
        mNickNameTv.setText(mUser.getUserNickName());
        String userWxId = mUser.getUserWxId() == null ? "" : mUser.getUserWxId();
        mWxIdTv.setText("帐号:" + userWxId);
        if (!TextUtils.isEmpty(mUser.getUserAvatar())) {
            String resizeAvatarUrl = OssUtil.resize(mUser.getUserAvatar());
            mAvatarSdv.setImageURI(Uri.parse(resizeAvatarUrl));
        }
    }
}
