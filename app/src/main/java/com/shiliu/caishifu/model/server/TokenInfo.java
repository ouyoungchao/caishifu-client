package com.shiliu.caishifu.model.server;

import java.io.Serializable;

public class TokenInfo implements Serializable {

    private String tokenHead;
    private String token;

    public TokenInfo() {
    }

    public TokenInfo(String tokenHead, String token) {
        this.tokenHead = tokenHead;
        this.token = token;
    }

    public String getTokenHead() {
        return tokenHead;
    }

    public void setTokenHead(String tokenHead) {
        this.tokenHead = tokenHead;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
