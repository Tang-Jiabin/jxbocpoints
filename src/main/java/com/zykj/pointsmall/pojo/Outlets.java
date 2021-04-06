package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * 网点
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-19
 */
@Data
@Entity
@Table(name = "jx_boc_outlets")
public class Outlets {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer outletsId;

    private String outletsName;

    private String outletsNo;

    private Integer branchId;
}
