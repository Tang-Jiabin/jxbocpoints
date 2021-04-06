package com.zykj.pointsmall.dto;

import lombok.Data;

/**
 * 特权商品
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-02
 */
@Data
public class PrProductsDTO {

    private Integer parentProductId;
    private Integer subProductId;
    private String parentName;
    private String name;
    private String money;
    private Integer number;
    private String vipPrice;
    private String vipIntegral;

}
