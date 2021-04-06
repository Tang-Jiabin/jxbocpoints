package com.zykj.pointsmall.service;

import com.zykj.pointsmall.common.SnowflakeIdFactory;
import com.zykj.pointsmall.dao.IntegralDetailedRepository;
import com.zykj.pointsmall.dao.IntegralRepository;
import com.zykj.pointsmall.pojo.Integral;
import com.zykj.pointsmall.pojo.IntegralDetailed;
import com.zykj.pointsmall.vo.IntegralVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 积分
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-07
 */
@Slf4j
@Service
public class IntegralService {

    private final IntegralRepository integralRepository;
    private final IntegralDetailedRepository detailedRepository;

    @Autowired
    public IntegralService(IntegralRepository integralRepository, IntegralDetailedRepository detailedRepository) {
        this.integralRepository = integralRepository;
        this.detailedRepository = detailedRepository;
    }

    public synchronized Integral getUserIntegral(String customerId) {

        Integral integral = integralRepository.findByCustomerId(customerId);

        if (integral == null) {
            integral = new Integral();
            integral.setCustomerId(customerId);
            integral.setUsedFraction(0);
            integral.setResidualFraction(0);
            integral.setTotalFraction(0);
            integral.setCreateDate(new Date());
            integral.setUpdateDate(new Date());
            return integralRepository.saveAndFlush(integral);
        }
        return integral;
    }

    public IntegralVO getDetails(String token) {
        Integral integral = integralRepository.findByCustomerId(token);
        IntegralVO integralVO = new IntegralVO();

        List<IntegralDetailed> detailedList = detailedRepository.findAllByIntegralId(integral.getIntegralId());
        integralVO.setDetailedList(detailedList);
        integralVO.setIntegralId(integral.getIntegralId());
        integralVO.setCustomerId(integral.getCustomerId());
        integralVO.setUsedFraction(integral.getUsedFraction());
        integralVO.setResidualFraction(integral.getResidualFraction());
        integralVO.setTotalFraction(integral.getTotalFraction());
        integralVO.setCreateDate(integral.getCreateDate());
        integralVO.setUpdateDate(integral.getUpdateDate());

        return integralVO;
    }

    /**
     * 添加积分
     *
     * @param customerId 用户id
     * @param fra        积分
     * @param source     来源 1-签到 2-活动 3-牛奶
     * @param activityId 活动id
     * @param tradeName  来源名称
     */
    public void addIntegral(String customerId, Integer fra, Integer source, Integer activityId, String tradeName) {

        Integral integral = integralRepository.findByCustomerId(customerId);
        if (integral == null) {
            return;
        }
        integral.setTotalFraction(integral.getTotalFraction() + fra);
        integral.setResidualFraction(integral.getResidualFraction() + fra);
        integralRepository.save(integral);

        IntegralDetailed detailed = new IntegralDetailed();
        SnowflakeIdFactory factory = new SnowflakeIdFactory(1, 2);
        detailed.setDetailId(String.valueOf(factory.nextId()));
        detailed.setIntegralId(integral.getIntegralId());
        detailed.setFraction(fra);
        detailed.setType(1);
        detailed.setSource(source);
        detailed.setActivityId(activityId);
        detailed.setTradeName(tradeName);
        detailed.setTransactionDate(new Date());
        detailedRepository.save(detailed);
    }

    public List<Integral> findAllByCustomerIdIn(List<String> customerIdList) {
        return integralRepository.findAllByCustomerIdIn(customerIdList);
    }
}
