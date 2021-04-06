package com.zykj.pointsmall.service;

import com.zykj.pointsmall.common.RedisUtil;
import com.zykj.pointsmall.dao.UserMilkRepository;
import com.zykj.pointsmall.dto.UserMilkDTO;
import com.zykj.pointsmall.pojo.UserMilk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * 牛奶
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-14
 */
@Service
public class UserMilkService {

    private final UserMilkRepository milkRepository;
    private final IntegralService integralService;
    private final RedisUtil redisUtil;

    @Autowired
    public UserMilkService(UserMilkRepository milkRepository, IntegralService integralService, RedisUtil redisUtil) {
        this.milkRepository = milkRepository;
        this.integralService = integralService;
        this.redisUtil = redisUtil;
    }


    public UserMilk findByUserId(String userId) {
        UserMilk milk = milkRepository.findByCustomerId(userId);
        if (milk == null) {
            milk = new UserMilk();
            milk.setCustomerId(userId);
            milk.setNumber(0);
            milk.setUpdateDate(new Date());
            milk.setTotalNumber(0);
            milk.setAdopt(0);
            milk.setNotCollected(0);
            milk = milkRepository.save(milk);
        }else {
            String key = userId+"_milk";
            String not = redisUtil.getString(key);

            if(StringUtils.hasText(not)){
                milk.setNotCollected(Integer.valueOf(not));
            }
        }

        return milk;
    }

    public UserMilkDTO addMilk(String token) {
        UserMilk milk = findByUserId(token);
        String key = token + "_milk";
        String notCollected = redisUtil.getString(key);
        UserMilkDTO milkDTO = new UserMilkDTO(milk);
        milkDTO.setUpdate(0);
        if(StringUtils.hasText(notCollected)){
            int number = milk.getNumber() + Integer.valueOf(notCollected);
            milk.setNumber(number);
            milk.setTotalNumber(milk.getTotalNumber() + Integer.valueOf(notCollected));
            milk = setMilk(milk, 3);
            redisUtil.setString(key, "0");

            if(number != milk.getNumber()){
                milkDTO.setUpdate(1);
            }
            milkDTO.setNumber(milk.getNumber());
            milkDTO.setTotalNumber(milk.getTotalNumber());

        }

        return milkDTO;
    }

    private UserMilk setMilk(UserMilk milk, int fra) {

        milk.setUpdateDate(new Date());
        int jifen = 0;
        int number = milk.getNumber() / 900;
        for (int i = 0; i < number; i++) {
            if (milk.getNumber() >= 900) {
                milk.setNumber(milk.getNumber() - 900);
                jifen = jifen + fra;
            }
        }
        if(jifen != 0){
            integralService.addIntegral(milk.getCustomerId(), jifen, 3, 0, "牛奶兑换");
        }
        return milkRepository.save(milk);

    }


    public UserMilk save(UserMilk milk) {
        return milkRepository.save(milk);
    }
}
