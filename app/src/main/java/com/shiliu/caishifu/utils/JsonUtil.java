package com.shiliu.caishifu.utils;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class JsonUtil {
    private static final String TAG = "JsonUtil";

    /**
     * json转list
     *
     * @param jsonString json字符串
     * @param clazz      list内对象类型
     * @param <T>        泛型
     * @return list
     */
    public static <T> List<T> jsonArrayToList(String jsonString, Class<T> clazz) {
        List<T> list;
        try {
            list = JSONArray.parseArray(jsonString, clazz);
        } catch (Exception e) {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * 对象转json
     * @param object 需要转换的对象
     * @return json序列化的字符串
     */
    public static String objectToJson(Object object){
        return JSONObject.toJSONString(object);
    }

    /**
     * 字符串转换成对象
     * @param json 序列化的对象字符串
     * @param clazz 对象类
     * @param <T> 返回类型
     * @return T实例
     */
    public static <T> T jsoToObject(String json, Class<T> clazz){
        return JSONObject.parseObject(json,clazz);
    }

    /**
     *
     * @param inputStream 输入流
     * @param clazz 解析类型
     * @param <T> 返回类
     * @return 类实例
     */
    public static <T> T jsoToObject(InputStream inputStream, Class<T> clazz){
        try {
            return JSONObject.parseObject(inputStream,clazz);
        } catch (IOException e) {
            Log.e(TAG, "jsoToObject: parse stream to " + clazz.getName() + " error ", e );
        }
        return null;
    }

}