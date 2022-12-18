package com.shiliu.caishifu.model.server;

public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    //注册相关响应码，以10xx开头
    REGISTER_FAILED_USER_EXIST(1002, "用户已存在"),
    REGISTER_USER_SUCCESS(1001, "用户注册成功"),
    //登录相关响应码以11xx开头
    LOGIN_PARAM_INVALID(1102, "请输入完整登录信息"),
    LOGIN_USERNAME_OR_PASSWOR_ERROR(1103, "用户名或密码错误"),
    LOGIN_USERNAME_IS_FIRBIDDEN(1104, "用户被禁用"),
    LOGIN_ERROR(1105, "服务器登录异常"),
    //验证码相关错误
    VERIFICATION_CODE_INVALID(1200, "验证码错误"),
    VERIFICATION_GET_SUCCESS(1201,"获取验证码成功"),
    VERIFICATION_GET_FAILED(1201,"证码获取失败"),


    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限");
    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
