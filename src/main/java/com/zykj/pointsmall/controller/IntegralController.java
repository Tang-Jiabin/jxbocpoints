package com.zykj.pointsmall.controller;

import com.zykj.pointsmall.common.ServerResponse;
import com.zykj.pointsmall.common.interceptor.Authorization;
import com.zykj.pointsmall.common.log.WebLog;
import com.zykj.pointsmall.pojo.Integral;
import com.zykj.pointsmall.pojo.IntegralDetailed;
import com.zykj.pointsmall.service.IntegralService;
import com.zykj.pointsmall.vo.IntegralVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 积分
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-11
 */
@Slf4j
@RestController
@RequestMapping("integral")
public class IntegralController {

    @Autowired
    private IntegralService integralService;

    @WebLog("积分详情")
    @Authorization
    @GetMapping("getDetails")
    public ServerResponse<IntegralVO> getDetails(@RequestAttribute String token){

        IntegralVO integralVO = integralService.getDetails(token);

        return ServerResponse.createSuccess(integralVO);
    }
}
