package com.zykj.pointsmall.vo;

import com.zykj.pointsmall.pojo.Privilege;
import com.zykj.pointsmall.pojo.Products;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 权限
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-02-08
 */

@Data
public class PrivilegeVO {

    private Integer privilegeId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 截止日期
     */
    private Date closingDate;

    /**
     * 开始日期
     */
    private Date startDate;

    private List<Products> productsList;

}
