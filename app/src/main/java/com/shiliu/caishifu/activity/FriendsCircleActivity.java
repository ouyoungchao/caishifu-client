package com.shiliu.caishifu.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
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

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huantansheng.easyphotos.EasyPhotos;
import com.shiliu.caishifu.R;
import com.shiliu.caishifu.adapter.FriendsCircleAdapter;
import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.dao.FriendsCircleDao;
import com.shiliu.caishifu.engine.GlideEngine;
import com.shiliu.caishifu.model.FriendsCircle;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.utils.PreferencesUtil;
import com.shiliu.caishifu.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;


/**
 * 朋友圈
 *
 * @author zhou
 */
public class FriendsCircleActivity extends CommonActivity {

    private RelativeLayout mRootRl;
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

    private static final int UPLOAD_PICTURE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_circle_activity);
        initView();
        mUser = getUser();
        networkUtil = NetworkUtil.getInstance(this);
        mFriendsCircleDao = new FriendsCircleDao();
        mTimeStamp = 0L;
        mDialog = new LoadingDialog(FriendsCircleActivity.this);

        View headerView = LayoutInflater.from(this).inflate(R.layout.friends_circle_header_item, null);

        mAddFriendsCircleIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.add_friends_circle_popup_window, null);
                // 给popwindow加上动画效果
                LinearLayout mPopRootLl = view.findViewById(R.id.ll_pop_root);
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                mPopRootLl.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_bottom_in));
                // 设置popwindow的宽高
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
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
                // 弹出的位置
                mPopupWindow.showAtLocation(mRootRl, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                RelativeLayout addByAlbumRl = view.findViewById(R.id.tv_add_by_album);
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
            }
        });

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
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                mFriendsCircleLv.smoothScrollToPosition(position);
            }
        };

        mCommentEt.addTextChangedListener(new TextChange());
        mSendBtn.setOnClickListener(new View.OnClickListener() {
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
        });

        mAdapter = new FriendsCircleAdapter(mFriendsCircleList, this, clickListener);
        mFriendsCircleLv.setAdapter(mAdapter);
        mFriendsCircleLv.addHeaderView(headerView, null, false);
        mFriendsCircleLv.setHeaderDividersEnabled(false);

        // headerView
        ImageView mCoverIv = headerView.findViewById(R.id.iv_cover);
        mCoverIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        TextView mNickNameTv = headerView.findViewById(R.id.tv_nick_name);
        mNickNameTv.setText(mUser.getUserNickName());

        SimpleDraweeView mAvatarSdv = headerView.findViewById(R.id.sdv_avatar);
        if (!TextUtils.isEmpty(mUser.getUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(mUser.getUserAvatar()));
        }
        getFriendsCircleList(mUser.getUserId(), Constant.DEFAULT_PAGE_SIZE, false);

//        mRefreshLayout.setPrimaryColorsId(android.R.color.black, android.R.color.white);
//        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//                // 下拉刷新
//                getFriendsCircleList(mUser.getUserId(), Constant.DEFAULT_PAGE_SIZE, false);
//            }
//        });
//        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshlayout) {
//                // 上拉加载
//                getFriendsCircleList(mUser.getUserId(), Constant.DEFAULT_PAGE_SIZE, true);
//            }
//        });

        mFriendsCircleLv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                mBottomLl.setVisibility(View.GONE);
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPLOAD_PICTURE:

            }
        }
    }



    private void showPhotoDialog() {
        EasyPhotos.createAlbum(this, false, false, GlideEngine.getInstance())
                .setFileProviderAuthority("com.shiliu.caishifu.fileprovider")
                .setCount(1)//参数说明：最大可选数，默认1
                .start(UPLOAD_PICTURE);
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean commentEtHasText = mCommentEt.getText().length() > 0;
            if (commentEtHasText) {
                mSendBtn.setBackgroundColor(Color.parseColor("#45c01a"));
                mSendBtn.setTextColor(Color.parseColor("#ffffff"));
                mSendBtn.setEnabled(true);
            } else {
                mSendBtn.setBackgroundColor(Color.parseColor("#cccccc"));
                mSendBtn.setTextColor(Color.parseColor("#666667"));
                mSendBtn.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void initView() {
        mManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mRootRl = findViewById(R.id.rl_root);
        mAddFriendsCircleIv = findViewById(R.id.iv_add_friends_circle);
        mFriendsCircleLv = findViewById(R.id.ll_friends_circle);

        mBottomLl = findViewById(R.id.ll_bottom);
        mCommentEt = findViewById(R.id.et_comment);
        mSendBtn = findViewById(R.id.btn_send);

        // 上拉加载，下拉刷新
//        mRefreshLayout = findViewById(R.id.srl_friends_circle);
    }


    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                mManager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void back(View view) {
        finish();
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

    /**
     * 设置添加屏幕的背景透明度
     * 1.0完全不透明，0.0f完全透明
     *
     * @param bgAlpha 透明度值
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // 0.0-1.0
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

}
