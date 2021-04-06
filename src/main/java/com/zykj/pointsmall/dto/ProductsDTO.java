package com.zykj.pointsmall.dto;

import lombok.Data;

import java.util.List;

/**
 * 上传商品
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-04
 */
@Data
public class ProductsDTO {
    private String name;
    private String money;
    private String number;
    private String vipPrice;
    private String vipIntegral;
    private String codeList;
}
