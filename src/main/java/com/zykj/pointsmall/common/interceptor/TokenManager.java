package com.zykj.pointsmall.common.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zykj.pointsmall.common.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 验证token的实现类
 *
 * @author tang
 */
@Slf4j
@Service("tokenManager")
public class TokenManager {

    private final RedisUtil redisUtil;

    private static final String SECRET = "R78{7(53!~3&>5}3}61^~LX,0m";
    private static final String ISSUER = "tangjiabin";
    private static final String KEY = "token";

    @Autowired
    public TokenManager(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }


    public TokenModel createToken(String userId) {

        String token = null;
        try {
            token = JWT.create()
                    .withIssuer(ISSUER)
                    .withJWTId(UUID.randomUUID().toString().toUpperCase())
                    .withClaim(KEY, userId)
                    .sign(Algorithm.HMAC256(SECRET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        TokenModel model = new TokenModel(userId, token);
        redisUtil.setStringSECONDS(userId, token, 60 * 60L * 12);

        return model;
    }

    public TokenModel getToken(String token) throws UnsupportedEncodingException, JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET))
                .withIssuer(ISSUER)
                .build(); //Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);
        String userId = jwt.getClaim(KEY).asString();
        return new TokenModel(userId, jwt.getToken());
    }


    public boolean checkToken(String token) throws UnsupportedEncodingException, JWTVerificationException {
        if (!StringUtils.hasLength(token)) {
            return false;
        }
        TokenModel tokenModel = getToken(token);

        String dbToken = null;
        try {

            if (!ObjectUtils.isEmpty(tokenModel)) {
                dbToken = redisUtil.getString(tokenModel.getUserId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbToken != null && token.equals(dbToken);
    }


}
