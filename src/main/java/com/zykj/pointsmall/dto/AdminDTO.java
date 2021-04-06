package com.zykj.pointsmall.dto;

import lombok.Data;

/**
 * @author : J.Tang
 * @version : v1.0
 * @email : seven_tjb@163.com
 * @create : 2018-08-29 21:43
 **/
@Data
public class AdminDTO {

    private Integer adminId;

    private String adminName;

    private String adminPwd;

    private String token;

    private String menuJson;

}
