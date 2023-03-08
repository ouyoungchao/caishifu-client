package com.shiliu.caishifu.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.activity.PublishActivity;
import com.shiliu.caishifu.adapter.FriendsCircleAdapter;
import com.shiliu.caishifu.dao.FriendsCircleDao;
import com.shiliu.caishifu.model.FriendsCircle;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.utils.CollectionUtils;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


/**
 * 卖菜信息
 */
public class SellVegetableFragment extends BaseFragment {
    private static final String TAG = "SellVegetableFragment";

    private ImageView mAddFriendsCircleIv;
    private ListView mFriendsCircleLv;
    private User mUser;
    private NetworkUtil networkUtil;
    private FriendsCircleDao mFriendsCircleDao;
    private List<FriendsCircle> mFriendsCircleList = new ArrayList<>();
    FriendsCircleAdapter mAdapter;
    //    RefreshLayout mRefreshLayout;
    long mTimeStamp;

    private LoadingDialog mDialog;

    private LinearLayout mBottomLl;
    private EditText mCommentEt;
    private Button mSendBtn;

    private InputMethodManager mManager;
    private String mCircleId;

    // 弹窗
    private PopupWindow mPopupWindow;

    private static final int UPLOAD_PICTURE = 3;

    MomentsFragment momentsFragment = new MomentsFragment();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = getUser(this.getContext());
        networkUtil = NetworkUtil.getInstance(this.getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sell_vegatable_fragment, container, false);
        initView(view);
        ButterKnife.bind(this, view);
        View publishView = this.getActivity().findViewById(R.id.rl_vegetable_publish);
        publishView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: sell publish");
                Intent editPublishIntent = new Intent(getContext(), PublishActivity.class);
                startActivity(editPublishIntent);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.getActivity().RESULT_OK) {
            switch (requestCode) {
                case UPLOAD_PICTURE:
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    if (!CollectionUtils.isEmpty(resultPhotos)) {
                        Intent editPublishIntent = new Intent(this.getContext(), PublishActivity.class);
                        editPublishIntent.putParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS, resultPhotos);
                        startActivity(editPublishIntent);
                    }
                    break;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        FragmentTransaction trx = getActivity().getSupportFragmentManager()
                .beginTransaction();
        if (!momentsFragment.isAdded()) {
            Log.i(TAG, "onResume: add momentFragment");
            trx.add(R.id.rl_momments_container, momentsFragment);
        }
        trx.show(momentsFragment).commit();
    }





    private void initView(View view) {
        mFriendsCircleLv = view.findViewById(R.id.ll_friends_circle);
    }


    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                mManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void back(View view) {
//        finish();
    }


}
