package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 商品权限
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-07
 */
@Data
@Entity
@Table(name = "jx_boc_products__privilege")
public class ProductsPrivilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ppId;

    private Integer parentId;
    /**
     * 商品id
     */
    private Integer productsId;

    /**
     * 权限id
     */
    private Integer privilegeId;

    /**
     * 会员购买价格
     */
    private BigDecimal vipPrice;

    /**
     * 会员购买积分
     */
    private Integer vipIntegral;

    /**
     * 权限类型  1-多选1  2-多选多
     */
    private Integer type;

    private Integer number;

    /**
     * 可买次数(默认1)
     */
    private Integer buyFeq;

    /**
     * 状态 1-开启 2-关闭
     */
    private Integer status;
}
