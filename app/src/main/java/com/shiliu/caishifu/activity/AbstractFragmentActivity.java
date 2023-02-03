package com.shiliu.caishifu.activity;

import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.model.server.ResultCode;
import com.shiliu.caishifu.model.server.TokenInfo;
import com.shiliu.caishifu.model.server.UserResult;
import com.shiliu.caishifu.utils.JsonUtil;
import com.shiliu.caishifu.utils.NetworkUtil;
import com.shiliu.caishifu.utils.PreferencesUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class AbstractFragmentActivity extends FragmentActivity {
    private static final String TAG = "AbstractFragmentActivit";
    public User mUser;

    public User getUser() {
        synchronized (this) {
            if (mUser == null) {
                mUser = PreferencesUtil.getInstance().getUser();
                if (null == mUser) {
                    Response response = NetworkUtil.getInstance(this).doGetReRequest(Constant.BASE_URL + "caishifu/userInfo", getAuthorizationHeader());
                    if (response == null) {
                        Log.w(TAG, "onFailure: get user ");
                    } else {
                        UserResult userResult = JsonUtil.jsoToObject(response.body().byteStream(), UserResult.class);
                        if (userResult.getCode() == ResultCode.USERINFO_GET_SUCCESS.getCode() && userResult.getData() != null) {
                            User user = JsonUtil.jsoToObject(JsonUtil.objectToJson(userResult.getData()), User.class);
                            if (user != null) {
                                mUser = user;
                                PreferencesUtil.getInstance().setUser(user);
                            }
                            Log.i(TAG, "onResponse: get user " + user.getUserNickName());
                        } else {
                            Log.w(TAG, "onResponse: get user error " + userResult.getMessage());
                        }
                    }
                }
            }
        }
        return mUser;
    }

    public Map<String, String> getAuthorizationHeader() {
        Map<String, String> headers = new HashMap<>();
        TokenInfo tokenInfo = PreferencesUtil.getInstance().tokenInfo();
        if (tokenInfo != null) {
            headers.put(Constant.AUTHORIZATION, tokenInfo.getTokenHead() + tokenInfo.getToken());
        }
        return headers;
    }

    public boolean needLogin(Response response){
        if(response.code() == ResultCode.UNAUTHORIZED.getCode()){
            return true;
        }
        return false;
    }

    public void login(){
        startActivity(new Intent(this, LoginActivity.class));
    }
}
