package com.zykj.pointsmall.vo;

import com.zykj.pointsmall.pojo.Products;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-08
 */

@Data
public class ProductsVO {


    private Integer productsId;

    /**
     * 名称
     */
    private String name;

    /**
     * 产品价值
     */
    private BigDecimal money;

    /**
     * 图片链接
     */
    private String imgUrl;

    /**
     * 详情图
     */
    private String detailImgUrl;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 特权 1-普通用户  2-普通用户+特权用户 3-特权用户
     */
    private Integer vip;

    private Integer categoryId;

    private Integer typeId;


    private List<Products> subProductList;
}
