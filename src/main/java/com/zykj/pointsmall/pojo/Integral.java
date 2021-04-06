package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 积分
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-07
 */
@Data
@Entity
@Table(name = "jx_boc_integral")
public class Integral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer integralId;

    /**
     * 用户号
     */
    private String customerId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 已使用积分
     */
    private Integer usedFraction;

    /**
     * 剩余积分
     */
    private Integer residualFraction;

    /**
     * 总积分
     */
    private Integer totalFraction;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 更新日期
     */
    private Date updateDate;


}
