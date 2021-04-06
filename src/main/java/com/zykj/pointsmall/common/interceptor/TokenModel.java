package com.zykj.pointsmall.common.interceptor;

import lombok.Data;

/**
 * Token的Model类，可以增加字段提高安全性，例如时间戳、url签名
 *
 */
@Data
public class TokenModel {


    private String userId;

    private String token;

    public TokenModel(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

}
