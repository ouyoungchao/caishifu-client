package com.shiliu.caishifu.utils;

import android.content.Context;
import android.content.Entity;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtil {
    private static final String TAG = "NetworkUtil";

    private static NetworkUtil instance;


    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).build();

    public static synchronized NetworkUtil getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkUtil();
        }
        return instance;
    }

    private NetworkUtil() {
    }


    public Response doGetReRequest(String url, Map<String,String> headers) {
        Request.Builder builder = new Request.Builder();
        if(!headers.isEmpty()){
            Iterator<Map.Entry<String,String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,String> entry = iterator.next();
                builder.addHeader(entry.getKey(),entry.getValue());
            }
        }
        builder.url(url).build();
        try {
            return okHttpClient.newCall(builder.build()).execute();
        } catch (IOException e) {
            Log.e(TAG, "doGetRequest: "+ url,e );
        }
        return null;
    }


    public void doGetReRequest(String url, Map<String,String> headers, final NetworkCallbak callbak){
        Request.Builder builder = new Request.Builder();
        if(!headers.isEmpty()){
            Iterator<Map.Entry<String,String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,String> entry = iterator.next();
                builder.addHeader(entry.getKey(),entry.getValue());
            }
        }
        builder.url(url).build();
        okHttpClient.newCall(builder.build()).enqueue(callbak);
    }

    public void doPostRequestWithFormBody(String url, Map<String,String> body, final NetworkCallbak callbak){
        FormBody.Builder formBody = new FormBody.Builder();
        if(!body.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = body.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                formBody.add(entry.getKey(),entry.getValue());
            }
        }
        Request request = new Request.Builder().url(url).post(formBody.build()).build();
        okHttpClient.newCall(request).enqueue(callbak);
    }

    public void doPostWithMultiBody(String url, Map<String, String> header, MultipartBody body, final NetworkCallbak callbak){
        Request.Builder builder = new Request.Builder().url(url);
        if(!header.isEmpty()){
            Iterator<Map.Entry<String, String>> iterator = header.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,String> entry = iterator.next();
                builder.addHeader(entry.getKey(),entry.getValue());
            }
        }
        if(body != null){
            builder.method("POST",body);
        }
        okHttpClient.newCall(builder.build()).enqueue(callbak);
    }

    public void doPostRequest(String url, String json, final NetworkCallbak callbak) {
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
        Request request = new Request.Builder().url(url).post(body).build();
        okHttpClient.newCall(request).enqueue(callbak);
    }

    public Response doPostRequest(String url, String json){
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
        Request request = new Request.Builder().url(url).post(body).build();
        try {
             return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            Log.e(TAG, "doPostRequest: "+ url , e);
        }
        return null;
    }

    /**
     * 请求异步回调函数
     */
    public static interface NetworkCallbak extends Callback {

    }

}
