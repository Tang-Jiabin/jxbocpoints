package com.zykj.pointsmall.dto;

import com.zykj.pointsmall.pojo.Products;
import lombok.Data;

import java.util.List;

/**
 * 分类礼品
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-07
 */

@Data
public class ProductsCategoryDTO {

    private String name;

    private List<Products> productsList;
}
