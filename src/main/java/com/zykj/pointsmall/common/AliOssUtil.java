package com.zykj.pointsmall.common;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;

import java.io.InputStream;

/**
 * 阿里云oss
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-04
 */

public class AliOssUtil {

    private static final String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    private static final String accessKeyId = "LTAI1LYQPjDca8xH";
    private static final String accessKeySecret = "RXkiJknjyoAK2ml4Y3qv7dDzQ1mc4U";
    private static final String bucketName = "zyboc";
    private static final String url = "https://zyimg.zhongyunkj.cn/";

    public static String upload(String path, InputStream stream) {

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        ossClient.putObject(bucketName, path, stream);

        ossClient.shutdown();

        return url+path;
    }

    public static void getBucket() {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(bucketName);
        ListObjectsV2Result result = ossClient.listObjectsV2(listObjectsV2Request);

        System.out.println("Objects:");
        for (OSSObjectSummary objectSummary : result.getObjectSummaries()) {
            System.out.println(objectSummary.getKey());
        }

        System.out.println("CommonPrefixes:");
        for (String commonPrefix : result.getCommonPrefixes()) {
            System.out.println(commonPrefix);
        }

        ossClient.shutdown();
    }

    public static void main(String[] args) {
        getBucket();

    }

}
