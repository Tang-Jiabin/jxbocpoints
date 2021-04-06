package com.zykj.pointsmall.dto;

import com.zykj.pointsmall.pojo.Privilege;
import com.zykj.pointsmall.pojo.Products;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 权益商品
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PrivilegeProductsDTO extends Privilege {

    private List<Products> productsList;

    private String cDate;
}
