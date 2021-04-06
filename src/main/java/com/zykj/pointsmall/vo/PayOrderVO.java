package com.zykj.pointsmall.vo;

import com.zykj.pointsmall.pojo.PayOrder;
import com.zykj.pointsmall.pojo.Products;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 订单
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PayOrderVO extends PayOrder {

    private Products products;

}
