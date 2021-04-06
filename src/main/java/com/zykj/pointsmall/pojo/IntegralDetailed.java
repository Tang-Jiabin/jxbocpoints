package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 积分明细
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-07
 */
@Data
@Entity
@Table(name = "jx_boc_integral_detailed")
public class IntegralDetailed {

    @Id
    private String detailId;

    /**
     * 积分ID
     */
    private Integer integralId;

    /**
     * 积分
     */
    private Integer fraction;

    /**
     * 明细类型 1-收入  2-支付
     */
    private Integer type;

    /**
     * 积分来源 1-签到 2-活动 3-牛奶 4-关注
     */
    private Integer source;


    private Integer activityId;

    /**
     * 购买商品名称
     */
    private  String tradeName;
    /**
     * 交易日期
     */
    private Date transactionDate;

}
