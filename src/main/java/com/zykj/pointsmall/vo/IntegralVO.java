package com.zykj.pointsmall.vo;

import com.zykj.pointsmall.pojo.Integral;
import com.zykj.pointsmall.pojo.IntegralDetailed;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 积分
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IntegralVO extends Integral {

    private List<IntegralDetailed> detailedList;
}
