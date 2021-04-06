package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * 数据后台管理员
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-15
 */
@Data
@Entity
@Table(name = "jx_boc_data_admin")
public class DataAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer dataAdminId;

    private String loginNo;

    private String pwd;

    private String name;

    private Integer type;

}
