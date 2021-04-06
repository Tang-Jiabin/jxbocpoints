package com.zykj.pointsmall.dto;

import com.zykj.pointsmall.pojo.PrizeInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-12
 */

@Data
public class GoodsTypeInfoDTO {

    private Integer productsId;

    /**
     * 名称
     */
    private String name;

    /**
     * 产品价值
     */
    private BigDecimal money;

    /**
     * 数量
     */
    private Integer number;


    /**
     * 购买价格
     */
    private BigDecimal price;

    /**
     * 购买积分
     */
    private Integer integral;

    private List<PrizeInfo> prizeInfoList;

}
