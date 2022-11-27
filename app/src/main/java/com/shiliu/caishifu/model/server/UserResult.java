package com.shiliu.caishifu.model.server;

public class UserResult extends CommonResult {
    UmsInfo data;

    public UserResult() {
    }

    public UserResult(long code, String message, UmsInfo data) {
        super(code, message);
        this.data = data;
    }

    public UmsInfo getData() {
        return data;
    }

    public void setData(UmsInfo data) {
        this.data = data;
    }
}
