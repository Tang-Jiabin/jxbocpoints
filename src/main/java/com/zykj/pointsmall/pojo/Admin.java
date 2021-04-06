package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * 管理员
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-02-04
 */
@Data
@Entity
@Table(name = "jx_boc_admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer adminId;

    private String adminName;

    private String adminPwd;

    private String loginDate;

    private String ip;
}
