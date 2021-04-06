package com.zykj.pointsmall.vo;

import lombok.Data;

/**
 * 数据后台
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-19
 */

@Data
public class ActivityDataVO {


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
    private String branchNo;
    /**
     * 网点号
     */
    private String outletsNo;
    /**
     * 月份
     */
    private String month;


}
