package com.zykj.pointsmall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zykj.pointsmall.pojo.UserMilk;
import lombok.Data;

import java.util.Date;

/**
 * 牛奶
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-15
 */

@Data
@JsonFormat
public class UserMilkDTO {

    private Integer number;

    private Date updateDate;

    private Integer totalNumber;

    private Integer update;

    public UserMilkDTO(UserMilk milk){
        this.setNumber(milk.getNumber());
        this.setTotalNumber(milk.getTotalNumber());
        this.setUpdateDate(milk.getUpdateDate());
    }
}
