package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 商品
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-04
 */
@Data
@Entity
@Table(name = "jx_boc_products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    /**
     * 购买价格
     */
    private BigDecimal price;

    /**
     * 购买积分
     */
    private Integer integral;

    /**
     * 会员购买价格
     */
    private BigDecimal vipPrice;

    /**
     * 会员购买积分
     */
    private Integer vipIntegral;

    /**
     * 种类ID 1-视频会员 2-音频娱乐 3-生活优惠 4-安全出行 5-话费直充
     */
    private Integer categoryId;

    /**
     * 类别Id 1-普通兑换链接  2-微信跳转链接
     */
    private Integer typeId;

    /**
     * 父类ID 0-基类
     */
    private Integer parentId;

    /**
     * 状态 1-开启 2-关闭
     */
    private Integer status;


}
