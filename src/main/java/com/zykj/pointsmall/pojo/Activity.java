package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 活动
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-07
 */

@Data
@Entity
@Table(name = "jx_boc_activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityId;

    /**
     * 活动名称
     */
    private String name;
    /**
     * 别名  回调名
     */
    private String alias;

    /**
     * 跳转链接
     */
    private String jumpLink;

    /**
     * 图片链接
     */
    private String imgUrl;

    /**
     * 类型 1-app 2-wechat
     */
    private Integer type;

    /**
     * 描述
     */
    private String activityDesc;

    /**
     * 创建日期
     */
    private Date createdDate;

    /**
     * 积分
     */
    private Integer integral;

    /**
     * 状态 1-开启 2-关闭
     */
    private Integer state;
}
