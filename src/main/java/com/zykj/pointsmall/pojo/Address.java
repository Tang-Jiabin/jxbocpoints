package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 地址
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-14
 */
@Data
@Entity
@Table(name = "jx_boc_address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer addressId;

    private String name;

    private String phone;

    private String region;

    private String address;

    private Date updateDate;

    private String customerId;
}
