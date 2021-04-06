package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 参加活动信息
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-22
 */

@Data
@Entity
@Table(name = "jx_boc_activity_info")
public class ActivityInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 客户号
     */
    private String customerId;

    /**
     * 员工
     */
    private String staff;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 机构号
     */
    private String branchName;
    /**
     * 网点号
     */
    private String outletsName;
    /**
     * 月份
     */
    private Integer month;
    /**
     * 年
     */
    private Integer year;
    /**
     * 日
     */
    private Integer day;

    /**
     * 日期
     */
    private LocalDateTime dateTime;

}
