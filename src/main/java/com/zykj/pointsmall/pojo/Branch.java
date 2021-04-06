package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * 分行
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-19
 */
@Data
@Entity
@Table(name = "jx_boc_branch")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer branchId;

    private String branchName;

    private String branchNo;
}
