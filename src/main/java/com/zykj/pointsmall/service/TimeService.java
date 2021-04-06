package com.zykj.pointsmall.service;

import com.zykj.pointsmall.common.DateUtil;
import com.zykj.pointsmall.common.RedisUtil;
import com.zykj.pointsmall.pojo.PayOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * 时间
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-19
 */
@Slf4j
@Service
public class TimeService {

    private final RedisUtil redisUtil;
    private final OrderService orderService;
    private final PayOrderService payOrderService;

    @Autowired
    public TimeService(RedisUtil redisUtil, OrderService orderService, PayOrderService payOrderService) {
        this.redisUtil = redisUtil;
        this.orderService = orderService;
        this.payOrderService = payOrderService;
    }

    @Async("asyncServiceExecutor")
    public void setNotCollected() {
        long start = System.currentTimeMillis();

        Long collectedListSize = redisUtil.getStringRedisTemplate().opsForList().size("milk_not_collected_list");
        if (collectedListSize != null) {
            for (int i = 0; i < collectedListSize; i++) {
                String key = redisUtil.getStringRedisTemplate().opsForList().index("milk_not_collected_list",i);
                if(StringUtils.hasText(key)){
                    String niunai = redisUtil.getString(key);
                    if(Integer.parseInt(niunai)<999){
                        redisUtil.incr(key);
                    }
                }

            }
        }
        long end = System.currentTimeMillis();
        long consuming = end -start;
//        log.info("=========================== 牛奶定时器 {}ms ===========================",consuming);
    }

    @Async("asyncServiceExecutor")
    public void order() {
        Date date = new Date();
        long start = date.getTime();

//        log.info("=========================== 订单定时器 ===========================");

        List<PayOrder> orderList = orderService.findAllByOrderStatus(1);

        for (PayOrder payOrder : orderList) {
            int min = DateUtil.difference(date, payOrder.getCreateDate(), ChronoUnit.MINUTES);
            if(min > 10){
                payOrderService.cancellation(payOrder.getProductId(),payOrder.getCustomerId());

                log.info("取消订单：{}",payOrder.getOrderNo());
            }
        }
        long end = System.currentTimeMillis();
        long consuming = end -start;
//        log.info("=========================== 结束 {}ms ===========================",consuming);
    }


}
