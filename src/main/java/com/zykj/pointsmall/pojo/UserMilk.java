package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 牛奶
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-14
 */
@Data
@Entity
@Table(name = "jx_boc_user_milk")
public class UserMilk {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer milkId;

    private String customerId;

    private Integer number;

    private Date updateDate;

    private Integer totalNumber;

    private Integer notCollected;

    private Integer adopt;


}
