package com.zykj.pointsmall.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zykj.pointsmall.common.*;
import com.zykj.pointsmall.common.log.WebLog;
import com.zykj.pointsmall.dao.ActivityRepository;
import com.zykj.pointsmall.dao.IntegralDetailedRepository;
import com.zykj.pointsmall.dao.IntegralRepository;
import com.zykj.pointsmall.pojo.Activity;
import com.zykj.pointsmall.pojo.Integral;
import com.zykj.pointsmall.service.ActivityService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 活动
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-11
 */
@Slf4j
@RestController
@RequestMapping("activity")
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityRepository activityRepository;
    private final IntegralDetailedRepository detailedRepository;
    private final IntegralRepository integralRepository;
    private final RedisUtil redisUtil;

    @Autowired
    public ActivityController(ActivityService activityService, ActivityRepository activityRepository, IntegralDetailedRepository detailedRepository, IntegralRepository integralRepository, RedisUtil redisUtil) {
        this.activityService = activityService;
        this.activityRepository = activityRepository;
        this.detailedRepository = detailedRepository;
        this.integralRepository = integralRepository;
        this.redisUtil = redisUtil;
    }

    @WebLog("提交活动")
    @PostMapping("complete")
    public ServerResponse<String> complete(@RequestBody String param) {

        activityService.submitActivity(param);

        return ServerResponse.ok();
    }

    @WebLog("添加活动")
    @PostMapping("add")
    public ServerResponse<String> add(String activityName,
                                      String activityLink,
                                      String activityImg,
                                      Integer type,
                                      String startTime,
                                      String endTime,
                                      String timestamp,
                                      String sign) {

        String veSign = activityName + "|" + activityLink + "|" + activityImg + "|" + type + "|" + startTime + "|" + endTime + "|" + timestamp;
        byte[] signByte = Base64.getEncoder().encode(veSign.getBytes(StandardCharsets.UTF_8));
        veSign = DigestUtils.md5DigestAsHex(signByte);

        if (!veSign.equals(sign)) {
            return ServerResponse.createMessage(411, "签名验证失败");
        }
        Activity activity = activityService.findByName(activityName);
        if (activity != null) {
            return ServerResponse.createMessage(412, "活动名称已存在");
        }
        activity = activityService.add(activityName, activityLink, activityImg, type,startTime, endTime);

        if (activity == null) {
            return ServerResponse.createMessage(413, "添加失败");
        }

//        公钥：
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD1WHEiE/fGdcHDXRVCodYiku+14YiLvqFIGwTXN8sdqahbNqMSCh/W68YvMVBn6V2aD5+mWAC2LxIOynSnasCrJGF6pTqWRPzPxa+JVDPAbijDmpw7TofnfziiZVES3oiFQO6onnvmCpCRVYlPQccp74PQG6VhzGFYg5Xqmcx3ewIDAQAB";
//        私钥：
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAPVYcSIT98Z1wcNdFUKh1iKS77XhiIu+oUgbBNc3yx2pqFs2oxIKH9brxi8xUGfpXZoPn6ZYALYvEg7KdKdqwKskYXqlOpZE/M/Fr4lUM8BuKMOanDtOh+d/OKJlURLeiIVA7qiee+YKkJFViU9Bxynvg9AbpWHMYViDleqZzHd7AgMBAAECgYEAvmhrNNVmYIGXZTVigJn1BFQg8Xkdbcb+iGVftl+4pUa8QD4BBMkSuMu2vX16N4rnd99UOdbmhn21eAEr05qH90V1XLaFU9BAFNIRZVAmdKWcnOifEmGSPeUa7Fj5fZvaBW/WINhvM98Jq+GKTOQWh0oONmfmSOLw2DxHgtYbB5ECQQD9+WNrLYOEAWf7xK92J6TdN78G+wtQ3e23DJChNu2zd/zrNtMhWG260oeCFnydPG6zsPby2kKG5HSseY36o/3FAkEA901vE8nMJlyhxVDgr0UHiy1X8NrscRUyOlJVsemxQZ2ETxi4Woo3e3krTQe+aD8s+09rF5iyAQRmEHiBJ6E0PwJALZm0fw7p+S6jc/IJqwZNNKcItoEms/tU7jPkV/3bygh2MeljGS6ebRfV2sac00KBgeP5QvtrTHsCc+FTW7hMHQJAKVLIF89ljJd39KeyE7d+LjRyeG2siqzXCsXNSgBZvTQb8reXvpFP+hLiOGnt2C+OTaQUqgm63M6mW2Q3eK7IMwJBAIbfERLd+n2EmKY8xkxwvSs8gHZKPIRNlF9EQinoTdqIL8fvRcXOSZLo4yx25tLnHH0qVzwaKwJMXtJX9/l93ks=";

        try {
            JSONObject json = new JSONObject();
            json.put("activityId", activity.getActivityDesc());
            json.put("key", activity.getAlias());
            String data = RSAUtil.publicKeyEncryption(json.toJSONString(), publicKey);
            return ServerResponse.createSuccess(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServerResponse.ok();
    }

    @WebLog("提交活动")
    @PostMapping("submit")
    public ServerResponse<String> submit(String activityId, String sign) {

        Integer state = activityService.submit(activityId, sign);

        if (state == null) {
            return ServerResponse.createMessage(411, "提交失败");
        }
        switch (state){
            case 200:
                return ServerResponse.createMessage(200,"提交成功");
            case 404:
                return ServerResponse.createMessage(404,"活动未找到");
            case 405:
                return ServerResponse.createMessage(405,"用户不存在");
            case 406:
                return ServerResponse.createMessage(406,"用户已参与活动");
            default:
                return ServerResponse.createMessage(411,"提交失败");
        }
    }

    public static void main(String[] args) throws Exception {

//        test1();
        test2();
//        test3();

    }

    private static void test1() {
        //添加活动
        String url = "http://127.0.0.1:8088/jxbocpoints/activity/add";

        String activityName = "支付1元抽好礼";
        String activityLink = "https://gt.qll-times.com/api/opa/0/04debc61-c6a8-4782-bf25-1da7b5548e27/gtg0vuzc";
        String activityImg = "https://image.qll-times.com/2021/03/a6cdada08afb4a629e0b30c2f2d34917.jpg";
        String startTime = "1614528000000";
        String endTime = "1617206400000";
        String timestamp = "1615529661690";

        String sign = activityName + "|" + activityLink + "|" + activityImg + "|" + startTime + "|" + endTime + "|" + timestamp;
        byte[] signByte = Base64.getEncoder().encode(sign.getBytes(StandardCharsets.UTF_8));
        sign = DigestUtils.md5DigestAsHex(signByte);
        System.out.println("sign:" + sign);
        FormBody body = new FormBody.Builder()
                .add("activityName", activityName)
                .add("activityLink", activityLink)
                .add("activityImg", activityImg)
                .add("startTime", startTime)
                .add("endTime", endTime)
                .add("timestamp", timestamp)
                .add("sign", sign)
                .build();

        String str = OkhttpUtil.getInstance().httpPost(url, body);

        System.out.println(str);

        JSONObject jsonObject = JSON.parseObject(str);
        if (jsonObject.getInteger("status") == 200) {
            String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAPVYcSIT98Z1wcNdFUKh1iKS77XhiIu+oUgbBNc3yx2pqFs2oxIKH9brxi8xUGfpXZoPn6ZYALYvEg7KdKdqwKskYXqlOpZE/M/Fr4lUM8BuKMOanDtOh+d/OKJlURLeiIVA7qiee+YKkJFViU9Bxynvg9AbpWHMYViDleqZzHd7AgMBAAECgYEAvmhrNNVmYIGXZTVigJn1BFQg8Xkdbcb+iGVftl+4pUa8QD4BBMkSuMu2vX16N4rnd99UOdbmhn21eAEr05qH90V1XLaFU9BAFNIRZVAmdKWcnOifEmGSPeUa7Fj5fZvaBW/WINhvM98Jq+GKTOQWh0oONmfmSOLw2DxHgtYbB5ECQQD9+WNrLYOEAWf7xK92J6TdN78G+wtQ3e23DJChNu2zd/zrNtMhWG260oeCFnydPG6zsPby2kKG5HSseY36o/3FAkEA901vE8nMJlyhxVDgr0UHiy1X8NrscRUyOlJVsemxQZ2ETxi4Woo3e3krTQe+aD8s+09rF5iyAQRmEHiBJ6E0PwJALZm0fw7p+S6jc/IJqwZNNKcItoEms/tU7jPkV/3bygh2MeljGS6ebRfV2sac00KBgeP5QvtrTHsCc+FTW7hMHQJAKVLIF89ljJd39KeyE7d+LjRyeG2siqzXCsXNSgBZvTQb8reXvpFP+hLiOGnt2C+OTaQUqgm63M6mW2Q3eK7IMwJBAIbfERLd+n2EmKY8xkxwvSs8gHZKPIRNlF9EQinoTdqIL8fvRcXOSZLo4yx25tLnHH0qVzwaKwJMXtJX9/l93ks=";

            String plaintext = null;
            try {
                String data = jsonObject.getString("data");
                plaintext = RSAUtil.privateKeyDecryption(data, privateKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(plaintext);

        }
    }

    private static void test2() throws Exception {

        //提交活动
        String url = "https://boc.pay.zhongyunkj.cn/jxbocpoints/activity/submit";
        String key = "I0IMcAGoEfb1s49aTQzQKQ==";
        JSONObject json = new JSONObject();
        json.put("activityId", "1372739796233506816");
        json.put("customerId", "289782113");
        json.put("phone", "18810086016");
        json.put("integral", 10);
        System.out.println(json.toJSONString());

        String ciphertext = AESUtil.encryptToString(json.toJSONString(), key);
        FormBody body = new FormBody.Builder()
                .add("activityId", "1372739796233506816")
                .add("sign", ciphertext)
                .build();
        System.out.println(ciphertext);
        System.out.println(URLEncoder.encode(ciphertext));
        String str = OkhttpUtil.getInstance().httpPost(url, body);

        System.out.println(str);

    }

    static void test3() throws Exception {
        //解析数据
        String data = "SRm1l0T7QYvVZm4EXzFFC/bqQtGBSabGkUkz1dMWGSTQQZiHFkc1AFEQwHxDwwAbwCXeRsEiECz6CkOZ+EbfIg==";
        //新客三重礼
        String key = "zVe/LlL6P33fqsJYFW1BXg==";
        //幸运盲盒
        key = "L/rkE1mb9nJm6Ph6pGToVw==";
        String s = AESUtil.decryptToString(data, key);
        System.out.println(s);
    }


}
