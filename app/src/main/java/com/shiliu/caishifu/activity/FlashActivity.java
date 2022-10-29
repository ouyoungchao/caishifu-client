package com.shiliu.caishifu.activity;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import com.shiliu.caishifu.R;
import com.shiliu.caishifu.utils.AreaUtil;
import com.shiliu.caishifu.utils.PreferencesUtil;
import com.shiliu.caishifu.utils.RegionUtil;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * 首页动画activity
 */
public class FlashActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "FlashActivity";

    private static final int sleepTime = 500;

    Button mLoginBtn;
    Button mRegisterBtn;

    private static final int SHOW_OPERATE_BTN = 0x3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //加载首次启动的layout
        final View view = View.inflate(this, R.layout.flash_activity, null);
        setContentView(view);
        super.onCreate(savedInstanceState);
        initView();
        //初始化sp,sp名称，packageName_preferences
        PreferencesUtil.getInstance().init(this);
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        view.setAnimation(animation);
        ShortcutBadger.removeCount(this);
    }

    /**
     * 初始化登录或注册视图
     */
    private void initView() {
        mLoginBtn = findViewById(R.id.btn_login);
        mRegisterBtn = findViewById(R.id.btn_register);
        //设置点击事件
        mLoginBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 预置地址信息
                    // 初始化省市区
                    AreaUtil.initArea(FlashActivity.this);
                    RegionUtil.initRegion(FlashActivity.this);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Log.e(TAG, "run: init area and region error", e);
                }
                if (PreferencesUtil.getInstance().isLogin()) {
                    // 已登录，跳至主页面
//                    startActivity(new Intent(FlashActivity.this, MainActivity.class));
                    finish();
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(SHOW_OPERATE_BTN));
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(FlashActivity.this, LoginActivity.class));
                break;
            case R.id.btn_register:
                startActivity(new Intent(FlashActivity.this, RegisterActivity.class));
                break;
        }
    }

    /**
     * 显示登录及注册按钮
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SHOW_OPERATE_BTN) {
                mLoginBtn.setVisibility(View.VISIBLE);
                mRegisterBtn.setVisibility(View.VISIBLE);
            }
        }
    };
}