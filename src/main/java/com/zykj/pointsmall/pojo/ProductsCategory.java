package com.zykj.pointsmall.pojo;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

import javax.persistence.*;
import java.util.Date;

/**
 * 商品类别
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-04
 */
@Data
@Entity
@Table(name = "jx_boc_products_category")
public class ProductsCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    /**
     * 类别名称
     */
    private String name;

    /**
     * 状态 1-开启 2-关闭
     */
    private Integer status;

    /**
     * 创建日期
     */
    private Date createDate;


}
