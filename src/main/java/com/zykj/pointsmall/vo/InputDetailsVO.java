package com.zykj.pointsmall.vo;

import lombok.Data;

import java.util.List;

/**
 * 录入信息
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-18
 */
@Data
public class InputDetailsVO {

    private String phone;

    private String date;

    private List<String> business;
}
