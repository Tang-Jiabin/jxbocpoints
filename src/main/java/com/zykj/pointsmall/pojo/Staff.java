package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.stream.IntStream;

/**
 * 员工
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-19
 */
@Data
@Entity
@Table(name = "jx_boc_staff")
public class Staff {
    

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer staffId;

    private String customerId;

    private String staffName;

    private String staffNo;

    private String phone;

    private Integer branchId;

    private Integer outletsId;

    private Date bindingDate;
}
