package com.shiliu.caishifu.fragement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.activity.PublishActivity;
import com.shiliu.caishifu.activity.MyProfileActivity;
import com.shiliu.caishifu.adapter.FriendsCircleAdapter;
import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.dao.FriendsCircleDao;
import com.shiliu.caishifu.engine.GlideEngine;
import com.shiliu.caishifu.model.FriendsCircle;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.model.server.UserResult;
import com.shiliu.caishifu.utils.CollectionUtils;
import com.shiliu.caishifu.utils.ExampleUtil;
import com.shiliu.caishifu.utils.JsonUtil;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.utils.OssUtil;
import com.shiliu.caishifu.utils.PreferencesUtil;
import com.shiliu.caishifu.widget.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 发布卖菜信息
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

    Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = getUser(this.getContext());
        networkUtil = NetworkUtil.getInstance(this.getContext());
        mFriendsCircleDao = new FriendsCircleDao();
        mTimeStamp = 0L;
        mDialog = new LoadingDialog(this.getContext());
        mFriendsCircleList = mFriendsCircleDao.getFriendsCircleList(Constant.DEFAULT_PAGE_SIZE, mTimeStamp);
        FriendsCircleAdapter.ClickListener clickListener = new FriendsCircleAdapter.ClickListener() {
            @Override
            public void onClick(Object... objects) {
                String circleId = String.valueOf(objects[1]);
                int position = Integer.parseInt(String.valueOf(objects[2]));
                mCircleId = circleId;
                mBottomLl.setVisibility(View.VISIBLE);
                mCommentEt.setFocusable(true);
                mCommentEt.setFocusableInTouchMode(true);
                mCommentEt.requestFocus();
                SellVegetableFragment.this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                mFriendsCircleLv.smoothScrollToPosition(position);
            }
        };

//        mCommentEt.addTextChangedListener(new TextChange());
       /* mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.setMessage("正在发表...");
                mDialog.show();
                String commentContent = mCommentEt.getText().toString();
                addFriendsCircleComment(mCircleId, commentContent);

                // 处理软键盘和编辑栏
                mBottomLl.setVisibility(View.GONE);
                mCommentEt.setText("");
            }
        });*/

       /* mAdapter = new FriendsCircleAdapter(mFriendsCircleList, this.getContext(), clickListener);
        mFriendsCircleLv.setAdapter(mAdapter);
        getFriendsCircleList(mUser.getUserId(), Constant.DEFAULT_PAGE_SIZE, false);


        mFriendsCircleLv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                mBottomLl.setVisibility(View.GONE);
                return false;
            }
        });*/
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


    public void publishMessage(View view) {
        switch (view.getId()) {
            case R.id.rl_vegetable_publish:
                LayoutInflater layoutInflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.add_friends_circle_popup_window, null);
                // 给popwindow加上动画效果
                LinearLayout mPopRootLl = view.findViewById(R.id.ll_pop_root);
                view.startAnimation(AnimationUtils.loadAnimation(this.getActivity().getApplicationContext(), R.anim.fade_in));
                mPopRootLl.startAnimation(AnimationUtils.loadAnimation(this.activity.getApplicationContext(), R.anim.push_bottom_in));
                // 设置popwindow的宽高
                DisplayMetrics dm = new DisplayMetrics();
                this.activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                mPopupWindow = new PopupWindow(view, dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);

                // 使其聚集
                mPopupWindow.setFocusable(true);
                // 设置允许在外点击消失
                mPopupWindow.setOutsideTouchable(true);

                // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                backgroundAlpha(0.5f);  //透明度

                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        backgroundAlpha(1f);
                    }
                });
                RelativeLayout publishLayout = this.activity.findViewById(R.id.rl_root);
                // 弹出的位置
                mPopupWindow.showAtLocation(publishLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                TextView addByAlbumRl = view.findViewById(R.id.tv_add_by_album);
                addByAlbumRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPhotoDialog();
                    }
                });

                // 取消
                RelativeLayout mCancelRl = view.findViewById(R.id.rl_cancel);
                mCancelRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                    }
                });
                break;

        }
    }

    private void showPhotoDialog() {
        EasyPhotos.createAlbum(this, false, false, GlideEngine.getInstance())
                .setFileProviderAuthority("com.shiliu.caishifu.fileprovider")
                .setCount(8)//参数说明：最大可选数，默认1
                .start(UPLOAD_PICTURE);
    }

    /**
     * 设置添加屏幕的背景透明度
     * 1.0完全不透明，0.0f完全透明
     *
     * @param bgAlpha 透明度值
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getActivity().getWindow().getAttributes();
        // 0.0-1.0
        lp.alpha = bgAlpha;
        this.getActivity().getWindow().setAttributes(lp);
    }


    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean commentEtHasText = mCommentEt.getText().length() > 0;
            if (commentEtHasText) {
               /* mSendBtn.setBackgroundColor(Color.parseColor("#45c01a"));
                mSendBtn.setTextColor(Color.parseColor("#ffffff"));
                mSendBtn.setEnabled(true);*/
            } else {
                /*mSendBtn.setBackgroundColor(Color.parseColor("#cccccc"));
                mSendBtn.setTextColor(Color.parseColor("#666667"));
                mSendBtn.setEnabled(false);*/
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void initView(View view) {
        mFriendsCircleLv = view.findViewById(R.id.ll_friends_circle);
        activity = this.getActivity();
    }


    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null)
                mManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void back(View view) {
//        finish();
    }

    private void getFriendsCircleList(String userId, final int pageSize, final boolean isAdd) {
        mTimeStamp = isAdd ? mTimeStamp : 0L;
       /* String url = Constant.BASE_URL + "friendsCircle?userId=" + userId + "&pageSize=" + pageSize + "&timestamp=" + mTimeStamp;

        networkUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                if (isAdd) {
//                    // 上拉加载
//                    mRefreshLayout.finishLoadMore();
//                } else {
//                    // 下拉刷新
//                    mRefreshLayout.finishRefresh();
//                }

                List<FriendsCircle> list = JSONArray.parseArray(response, FriendsCircle.class);
                if (null != list && list.size() > 0) {
                    for (FriendsCircle friendsCircle : list) {
                        FriendsCircle checkFriendsCircle = mFriendsCircleDao.getFriendsCircleByCircleId(friendsCircle.getCircleId());
                        if (null != friendsCircle.getLikeUserList()) {
                            friendsCircle.setLikeUserJsonArray(JSON.toJSONString(friendsCircle.getLikeUserList()));
                        }
                        if (null != friendsCircle.getFriendsCircleCommentList()) {
                            friendsCircle.setFriendsCircleCommentJsonArray(JSON.toJSONString(friendsCircle.getFriendsCircleCommentList()));
                        }

                        if (null == checkFriendsCircle) {
                            // 不存在,插入
                            mFriendsCircleDao.addFriendsCircle(friendsCircle);
                        } else {
                            // 存在,修改
                            friendsCircle.setId(checkFriendsCircle.getId());
                            mFriendsCircleDao.addFriendsCircle(friendsCircle);
                        }
                    }
                    List<FriendsCircle> friendsCircleList = mFriendsCircleDao.getFriendsCircleList(pageSize, mTimeStamp);
                    if (isAdd) {
                        // 上拉加载
                        mAdapter.addData(friendsCircleList);
                    } else {
                        // 下拉刷新
                        mAdapter.setData(friendsCircleList);
                    }
                    mTimeStamp = friendsCircleList.get(friendsCircleList.size() - 1).getTimestamp();
                    mAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                if (isAdd) {
//                    // 上拉加载
//                    mRefreshLayout.finishLoadMore();
//                } else {
//                    // 下拉刷新
//                    mRefreshLayout.finishRefresh();
//                }
                // 网络错误，从本地读取
                List<FriendsCircle> friendsCircleList = mFriendsCircleDao.getFriendsCircleList(pageSize, mTimeStamp);
                if (null != friendsCircleList && friendsCircleList.size() > 0) {
                    if (isAdd) {
                        // 上拉加载
                        mAdapter.addData(friendsCircleList);
                    } else {
                        // 下拉刷新
                        mAdapter.setData(friendsCircleList);
                    }
                    mTimeStamp = friendsCircleList.get(friendsCircleList.size() - 1).getTimestamp();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });*/
    }

    private void addFriendsCircleComment(final String circleId, final String content) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("content", content);
        paramMap.put("userId", mUser.getUserId());


    }

}
