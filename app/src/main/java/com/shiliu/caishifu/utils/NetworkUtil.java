package com.shiliu.caishifu.utils;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtil {
    private static final String TAG = "NetworkUtil";

    private static NetworkUtil instance;

    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static synchronized NetworkUtil getInstance(){
        if(instance == null){
            instance = new NetworkUtil();
        }
        return instance;
    }

    private NetworkUtil(){}


    public Response doGetRequest(String url){
        Request request = new Request.Builder().url(url).build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            Log.e(TAG, "getRequest: " + url + " error ",e );
        }
        return null;
    }

    public void doPostRequest(String url, String json, final NetworkCallbak callbak){
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
        Request request = new Request.Builder().url(url).post(body).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callbak.onFailure(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callbak.onResponse(call,response);
            }
        });
    }

    /**
     * 请求异步回调函数
     */
    public static interface NetworkCallbak extends Callback{

    }

}
