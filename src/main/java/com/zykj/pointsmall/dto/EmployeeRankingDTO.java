package com.zykj.pointsmall.dto;

import lombok.Data;

/**
 * 员工排名
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-20
 */
@Data
public class EmployeeRankingDTO {

    private Integer isMe;

    private String name;

    private String branchName;

    private String outletsName;

    private Integer number;

}
