package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 支付订单
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-11
 */

@Data
@Entity
@Table(name = "jx_boc_pay_order")
public class PayOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    private String merchantNo;
    private String orderNo;
    private String orderSeq;
    private String cardTyp;
    private String payTime;
    /**
     * 1-未支付 2-已支付 3-已退款 4-已取消 5-已完成
     **/
    private Integer orderStatus;

    private String payAmount;

    private Integer integral;

    private Date createDate;

    private String customerId;

    /**商品id*/
    private Integer productId;

    private Integer privilegeId;

    private String refundNo;

    private Date refundDate;

    private Date cancelDate;

    private String code;

    private Integer addressId;

}
