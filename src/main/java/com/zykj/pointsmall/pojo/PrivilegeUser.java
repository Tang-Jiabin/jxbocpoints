package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * 权限关联表
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-07
 */
@Data
@Entity
@Table(name = "jx_boc_privilege__user")
public class PrivilegeUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer puId;

    /**
     * 权限id
     */
    private Integer privilegeId;

    /**
     * 用户id
     */
    private String customerId;

    /**
     * 状态 1-开启 2-关闭
     */
    private Integer status;
}
