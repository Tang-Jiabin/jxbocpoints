package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 特权
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-07
 */
@Data
@Entity
@Table(name = "jx_boc_privilege")
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer privilegeId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 描述
     */
    private String privilegeDesc;

    /**
     * 状态 1-开启 2-关闭
     */
    private Integer state;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 截止日期
     */
    private Date closingDate;

    /**
     * 开始日期
     */
    private Date startDate;
}
