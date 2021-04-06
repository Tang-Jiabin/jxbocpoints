package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 奖品信息
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-02-05
 */
@Data
@Entity
@Table(name = "jx_boc_prize_info")
public class PrizeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prizeId;

    private String prizeName;

    /**
     * 状态 1-未派发 -2已派发 3-已删除
     */
    private Integer status;

    private String customerId;

    private Date createDate;

    /**
     * 派发日期
     */
    private Date distributionDate;

    /**
     * 奖品码
     */
    private String code;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 商品id
     */
    private Integer productsId;
}
