package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 员工活动
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-18
 */
@Data
@Entity
@Table(name = "jx_boc_staff_activity_info")
public class StaffActivityInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer saId;

    private Integer zfb;
    private Integer wx;
    private Integer zzjy;
    private Integer shjf;
    private Integer etc;
    private Integer jhgh;
    private Integer jj;
    private Integer yb;
    private Integer kjhk;
    private Integer xykhk;
    private Integer zhgjs;
    private String phone;
    private Integer hd1;
    private Integer hd2;

    private Date createDate;

    private Date updateDate;

    private String customerId;
}
