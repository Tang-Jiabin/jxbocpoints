package com.zykj.pointsmall.dto;

import com.zykj.pointsmall.pojo.Branch;
import com.zykj.pointsmall.pojo.Staff;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工信息
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StaffDTO  extends Staff {

    private String branchName;

    private String branchNo;

    private String outletsName;

    private String outletsNo;

    private Integer allRanking;

    private Integer outletsRanking;


}
