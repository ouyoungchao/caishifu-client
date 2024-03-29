package com.shiliu.caishifu.cons;

import java.io.File;

public class Constant {

    /**
     * 地区类型-"省"
     */
    public static final String AREA_TYPE_PROVINCE = "1";

    /**
     * 地区类型-"市"
     */
    public static final String AREA_TYPE_CITY = "2";

    /**
     * 地区类型-"县"
     */
    public static final String AREA_TYPE_DISTRICT = "3";

    /**
     * 本地IP地址
     */
    public static final String BASE_URL = "http://192.168.43.208:8086/";

    public static final String IS_NOT_FRIEND = "0";

    /**
     * 是好友
     */
    public static final String IS_FRIEND = "1";

    /**
     * 星标好友
     */
    public static final String CONTACT_IS_STARRED = "1";

    /**
     * 星标好友分组title
     */
    public static final String STAR_FRIEND_GROUP= "星标朋友";

    /**
     * 非黑名单
     */
    public static final String CONTACT_IS_NOT_BLOCKED = "0";

    /**
     * 黑名单
     */
    public static final String CONTACT_IS_BLOCKED = "1";

    /**
     * 普通注册用户
     */
    public static final String USER_TYPE_REG = "REG";

    /**
     * 手机号/密码登录
     */
    public static final String LOGIN_TYPE_PHONE_AND_PASSWORD = "0";

    /**
     * 手机号/验证码登录
     */
    public static final String LOGIN_TYPE_PHONE_AND_VERIFICATION_CODE = "1";

    /**
     * 单聊
     */
    public static final String TARGET_TYPE_SINGLE = "single";

    /**
     * 群聊
     */
    public static final String TARGET_TYPE_GROUP = "group";

    /**
     * 文本消息
     */
    public static final String MSG_TYPE_TEXT = "text";

    /**
     * 图片消息
     */
    public static final String MSG_TYPE_IMAGE = "image";

    /**
     * 位置消息
     */
    public static final String MSG_TYPE_LOCATION = "location";
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 非星标好友
     */
    public static final String CONTACT_IS_NOT_STARRED = "0";

    public static final int USER_SEX_MALE = 1;
    public static final int USER_SEX_FEMALE = 2;

    public static final String  AUTHORIZATION = "Authorization";

    /**
     * 定位类型-地区信息
     * 获取省市区街道信息
     */
    public static final String LOCATION_TYPE_AREA = "0";
    public static final String DEFAULT_POST_CODE = "000000";
    public static String PICTURE_DIR = "sdcard/caishifu/pictures/";
}
