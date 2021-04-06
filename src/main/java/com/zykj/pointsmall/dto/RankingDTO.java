package com.zykj.pointsmall.dto;

import lombok.Data;

import java.util.List;

/**
 * 排名
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-21
 */
@Data
public class RankingDTO {

    private String name;

    private String branchName;

    private String outletsName;

    private Integer number;

    private Integer myRanking;

    List<EmployeeRankingDTO> employeeRankingDTOList;
}
