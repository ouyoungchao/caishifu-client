package com.shiliu.caishifu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.shiliu.caishifu.model.PositionInfo;
import com.shiliu.caishifu.model.User;
import com.shiliu.caishifu.model.server.TokenInfo;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PreferencesUtil {
    private static final String TAG = "PreferencesUtil";
    private static SharedPreferences preferences;
    public static PreferencesUtil preferencesUtil;
    private SharedPreferences.Editor editor = null;
    private static Context context;
    private Object object;

    public static PreferencesUtil getInstance() {
        if (preferencesUtil == null) {
            synchronized (PreferencesUtil.class) {
                if (preferencesUtil == null) {
                    // 使用双重同步锁
                    preferencesUtil = new PreferencesUtil();
                }
            }
        }
        return preferencesUtil;
    }

    public void init(Context context) {
        Log.d(TAG, "init preferences");
        PreferencesUtil.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context
                .getApplicationContext());
        Log.d(TAG, "init preferences " + preferences);
    }
    
    public static SharedPreferences getPreferences(){
        synchronized (PreferencesUtil.class){
            if(preferences == null){
                preferences = PreferenceManager.getDefaultSharedPreferences(context
                        .getApplicationContext());
            }
            return preferences;
        }
    }

    /**
     * 问题在于，这个Context哪来的我们不能确定，很大的可能性，你在某个Activity里面为了方便，直接传了个this;
     * 这样问题就来了，我们的这个类中的sInstance是一个static且强引用的，在其内部引用了一个Activity作为Context，也就是说，
     * 我们的这个Activity只要我们的项目活着，就没有办法进行内存回收。而我们的Activity的生命周期肯定没这么长，造成了内存泄漏。
     * 所以这里使用context.getApplicationContext()
     */
    private PreferencesUtil() {

    }

    /**
     * 保存数据 , 所有的类型都适用
     *
     * @param key
     * @param object
     */
    public synchronized void saveParam(String key, Object object) {
        if (editor == null)
            editor = getPreferences().edit();
        // 得到object的类型
        String type = object.getClass().getSimpleName();
        if ("String".equals(type)) {
            // 保存String 类型
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            // 保存integer 类型
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            // 保存 boolean 类型
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            // 保存float类型
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            // 保存long类型
            editor.putLong(key, (Long) object);
        } else {
            if (!(object instanceof Serializable)) {
                throw new IllegalArgumentException(object.getClass().getName() + " 必须实现Serializable接口!");
            }

            // 不是基本类型则是保存对象
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                String productBase64 = Base64.encodeToString(
                        baos.toByteArray(), Base64.DEFAULT);
                editor.putString(key, productBase64);
                Log.d(this.getClass().getSimpleName(), "save object success");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(this.getClass().getSimpleName(), "save object error");
            }
        }
        editor.commit();
    }

    /**
     * 移除信息
     */
    public synchronized void remove(String key) {
        if (editor == null)
            editor = getPreferences().edit();
        editor.remove(key);
        editor.commit();
    }


    /**
     * 得到保存数据的方法，所有类型都适用
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object getParam(String key, Object defaultObject) {
        if (defaultObject == null) {
            return getObject(key);
        }

        String type = defaultObject.getClass().getSimpleName();

        if ("String".equals(type)) {
            return getPreferences().getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return getPreferences().getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return getPreferences().getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return getPreferences().getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return getPreferences().getLong(key, (Long) defaultObject);
        }
        return getObject(key);
    }

    /**
     * Whether to use for the first time
     *
     * @return
     */
    public boolean isFirst() {
        return (Boolean) getParam("isFirst", true);
    }

    /**
     * set user first use is false
     *
     * @return
     */
    public void setFirst(Boolean isFirst) {
        saveParam("isFirst", isFirst);
    }

    /**
     * Set up the first time login
     *
     * @return
     */
    public boolean isLogin() {
        return (Boolean) getParam("isLogin", false);
    }

    /**
     * @return
     */
    public void setLogin(Boolean isLogin) {
        saveParam("isLogin", isLogin);
    }

    public void setNewMsgsUnreadNumber(int newMsgsUnreadNumber) {
        saveParam("newMsgsUnreadNumber", newMsgsUnreadNumber);
    }

    public Integer getNewMsgsUnreadNumber() {
        return (Integer) getParam("newMsgsUnreadNumber", 0);
    }

    public void setUser(User user) {
        saveParam("user", JSON.toJSONString(user));
    }

    public User getUser() {
        User user = null;
        try {
            user = JSON.parseObject((String) getParam("user", ""), User.class);
        } catch (Exception e) {
            Log.e(TAG, "getUser error",e);
        }
        return user;
    }

    public TokenInfo tokenInfo() {
        TokenInfo tokenInfo = null;
        try {
            tokenInfo = JSON.parseObject((String) getParam("tokenInfo", ""), TokenInfo.class);
        } catch (Exception e){
            Log.w(TAG, "tokenInfo: error",e );
        }
        return tokenInfo;
    }


    public void setNewFriendsUnreadNumber(int newFriendsUnreadNumber) {
        saveParam("newFriendsUnreadNumber", newFriendsUnreadNumber);
    }

    public Integer getNewFriendsUnreadNumber() {
        return (Integer) getParam("newFriendsUnreadNumber", 0);
    }

    public Object getObject(String key) {
        String wordBase64 = getPreferences().getString(key, "");
        byte[] base64 = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            ObjectInputStream bis = new ObjectInputStream(bais);
            object = bis.readObject();
            Log.d(this.getClass().getSimpleName(), "Get object success");
            return object;
        } catch (Exception e) {

        }
        Log.e(this.getClass().getSimpleName(), "Get object is error");
        return null;
    }

    public void setPickedProvince(String provinceName) {
        saveParam("pickedProvince", provinceName);
    }

    public String getPickedProvince() {
        return (String) getParam("pickedProvince", "");
    }

    public void setPickedCity(String cityName) {
        saveParam("pickedCity", cityName);
    }

    public String getPickedCity() {
        return (String) getParam("pickedCity", "");
    }

    public void setPickedDistrict(String districtName) {
        saveParam("pickedDistrict", districtName);
    }

    public String getPickedDistrict() {
        return (String) getParam("pickedDistrict", "");
    }

    public void setPickedPostCode(String postCode) {
        saveParam("pickedPostCode", postCode);
    }

    public String getPickedPostCode() {
        return (String) getParam("pickedPostCode", "");
    }

    /**
     * 设置位置信息
     *
     * @param positionInfo 位置信息
     */
    public void setPositionInfo(PositionInfo positionInfo) {
        saveParam("positionInfo", JSON.toJSONString(positionInfo));
    }

    /**
     * 获取位置信息
     *
     * @return 位置信息
     */
    public PositionInfo getPositionInfo() {
        PositionInfo positionInfo;
        try {
            positionInfo = JSON.parseObject((String) getParam("positionInfo", ""), PositionInfo.class);
        } catch (Exception e) {
            positionInfo = new PositionInfo();
        }
        return positionInfo;
    }

    /**
     * 是否开启"附近的人"
     *
     * @return true:是  false:否
     */
    public boolean isOpenPeopleNearby() {
        return (Boolean) getParam("isOpenPeopleNearby", false);
    }

    /**
     * 设置是否开启附近的人
     *
     * @param isOpenPeopleNearby 是否开启附近的人
     */
    public void setOpenPeopleNearby(Boolean isOpenPeopleNearby) {
        saveParam("isOpenPeopleNearby", isOpenPeopleNearby);
    }

    public <T> void setList(String key, List<T> list) {
        saveParam(key, JSON.toJSONString(list));
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        List<T> list;
        try {
            list = JSON.parseArray((String) getParam(key, ""), clazz);
        } catch (Exception e) {
            list = new ArrayList<>();
        }
        return list;
    }


}
