package com.zykj.pointsmall.dto;

import lombok.Data;

import java.util.List;

/**
 * 分行网点联动数据
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-23
 */

@Data
public class BranchOutletsDTO {

    private String branchName;

    private String branchNo;

    private List<OutletsRankingDTO> outletsList;
}
