package com.zykj.pointsmall.vo;

import com.zykj.pointsmall.pojo.BocUserInfo;
import com.zykj.pointsmall.pojo.Privilege;
import lombok.Data;

import javax.persistence.Id;
import java.util.List;

/**
 * 用户
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-02-08
 */

@Data
public class UserVO {

    /**
     * 客户号
     */
    @Id
    private String customerId;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 创建日期
     */
    private String createDate;

    /**
     * 地区编号 40740河北
     */
    private String ibknum;

    private Integer integral;

    private List<PrivilegeVO> privilegeList;
}
