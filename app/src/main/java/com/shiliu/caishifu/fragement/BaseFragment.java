package com.shiliu.caishifu.fragement;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

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

import okhttp3.Call;
import okhttp3.Response;

public class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    
    public User mUser;

    public User getUser(Context context) {
        synchronized (this) {
            if (mUser == null) {
                mUser = PreferencesUtil.getInstance().getUser();
                if (null == mUser) {
                    NetworkUtil.getInstance(context).doGetReRequest(Constant.BASE_URL + "caishifu/userInfo", getAuthorizationHeader(), new NetworkUtil.NetworkCallbak() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.w(TAG, "onFailure: get user ", e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            UserResult userResult = JsonUtil.jsoToObject(response.body().byteStream(), UserResult.class);
                            if(userResult.getCode() == ResultCode.SUCCESS.getCode() && userResult.getData() != null) {
                                User user = JsonUtil.jsoToObject(JsonUtil.objectToJson(userResult.getData()),User.class);
                                if (user != null) {
                                    mUser = user;
                                    PreferencesUtil.getInstance().setUser(user);
                                }
                                Log.i(TAG, "onResponse: get user " + user.getUserNickName());
                            } else {
                                Log.w(TAG, "onResponse: get user error " + userResult.getMessage());
                            }
                        }
                    });
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

    /**
     * 渲染标题粗细程度
     *
     * @param textView 标题textView
     */
    protected void setTitleStrokeWidth(TextView textView) {
        TextPaint paint = textView.getPaint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        // 控制字体加粗的程度
        paint.setStrokeWidth(0.8f);
    }

}
