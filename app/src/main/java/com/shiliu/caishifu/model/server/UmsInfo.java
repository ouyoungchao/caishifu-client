package com.shiliu.caishifu.model.server;

import java.io.Serializable;

public class UmsInfo implements Serializable {
    private UmsLoginInfo umsLoginInfo;

    public UmsInfo() {
    }

    public UmsInfo(UmsLoginInfo umsLoginInfo) {
        this.umsLoginInfo = umsLoginInfo;
    }

    public UmsLoginInfo getUmsLoginInfo() {
        return umsLoginInfo;
    }

    public void setUmsLoginInfo(UmsLoginInfo umsLoginInfo) {
        this.umsLoginInfo = umsLoginInfo;
    }
}
