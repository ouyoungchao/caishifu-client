package com.shiliu.caishifu.model.server;

public class CommonResult {
    /**
     * 状态码
     */
    private long code;
    /**
     * 提示信息
     */
    private String message;

    protected CommonResult() {
    }

    protected CommonResult(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
