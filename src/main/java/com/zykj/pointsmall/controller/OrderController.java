package com.zykj.pointsmall.controller;

import com.zykj.pointsmall.common.DateUtil;
import com.zykj.pointsmall.common.RedisUtil;
import com.zykj.pointsmall.common.ServerResponse;
import com.zykj.pointsmall.common.interceptor.Authorization;
import com.zykj.pointsmall.common.log.WebLog;
import com.zykj.pointsmall.pojo.PayOrder;
import com.zykj.pointsmall.service.OrderService;
import com.zykj.pointsmall.service.PayOrderService;
import com.zykj.pointsmall.service.TimeService;
import com.zykj.pointsmall.vo.PayOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-12
 */
@Slf4j
@RestController
@RequestMapping("order")
public class OrderController {

    private final OrderService orderService;
    private final PayOrderService payOrderService;
    private final RedisUtil redisUtil;
    private final TimeService timeService;


    @Autowired
    public OrderController(OrderService orderService, PayOrderService payOrderService, RedisUtil redisUtil, TimeService timeService) {
        this.orderService = orderService;
        this.payOrderService = payOrderService;
        this.redisUtil = redisUtil;
        this.timeService = timeService;
    }

    @WebLog("订单详情")
    @Authorization
    @GetMapping("getDetails")
    public ServerResponse<List<PayOrderVO>> getDetails(@RequestAttribute String token) {

        List<PayOrderVO> orderVOList = orderService.getDetails(token);
        orderVOList = orderVOList.stream().sorted(Comparator.comparing(PayOrderVO::getOrderId).reversed()).collect(Collectors.toList());
        return ServerResponse.createSuccess(orderVOList);
    }

    @WebLog("订单详情")
    @Authorization
    @GetMapping("getOrderDetails")
    public ServerResponse<PayOrderVO> getOrderDetails(String orderNo,@RequestAttribute String token) {

        PayOrderVO orderVO = orderService.getOrderDetails(orderNo);
        if (!orderVO.getCustomerId().equals(token)) {
            return ServerResponse.createSuccess(null);
        }
        return ServerResponse.createSuccess(orderVO);
    }

    @Scheduled(cron = "0 */5 * * * ?")
    @GetMapping(value = "timing")
    public void timing() {
        timeService.order();
    }

//    @Scheduled(cron = "0/15 * * * * ?")
    @GetMapping(value = "setNotCollected")
    public void setNotCollected() {
        timeService.setNotCollected();
    }
}
