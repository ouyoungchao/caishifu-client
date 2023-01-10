package com.shiliu.caishifu.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.shiliu.caishifu.R;
import com.shiliu.caishifu.activity.LoginActivity;
import com.shiliu.caishifu.cons.Constant;
import com.shiliu.caishifu.model.server.CommonResult;
import com.shiliu.caishifu.model.server.ResultCode;

import java.io.IOException;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Response;

public class SortMessageUtil {

    private static final String TAG = "SortMessageUtil";

    public static void getAuthCode(Context context, String telephone, NetworkUtil.NetworkCallbak callbak) {
        String url = Constant.BASE_URL + "caishifu/getAuthCode?telephone="+telephone;
        NetworkUtil.getInstance(context).doGetReRequest(url, Collections.emptyMap(),callbak);
    }
}
