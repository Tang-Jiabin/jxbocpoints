package com.zykj.pointsmall.service;

import com.zykj.pointsmall.common.SnowflakeIdFactory;
import com.zykj.pointsmall.dao.*;
import com.zykj.pointsmall.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 支付
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-11
 */
@Slf4j
@Service
public class PayOrderService {

    private final PayOrderRepository payOrderRepository;
    private final ProductsRepository productsRepository;
    private final IntegralRepository integralRepository;
    private final IntegralDetailedRepository detailedRepository;
    private final ProductsPrivilegeRepository productsPrivilegeRepository;
    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeUserRepository privilegeUserRepository;
    private final PrizeInfoRepository prizeInfoRepository;

    @Autowired
    public PayOrderService(PayOrderRepository payOrderRepository, ProductsRepository productsRepository, IntegralRepository integralRepository, IntegralDetailedRepository detailedRepository, ProductsPrivilegeRepository productsPrivilegeRepository, PrivilegeRepository privilegeRepository, PrivilegeUserRepository privilegeUserRepository, PrizeInfoRepository prizeInfoRepository) {
        this.payOrderRepository = payOrderRepository;
        this.productsRepository = productsRepository;
        this.integralRepository = integralRepository;
        this.detailedRepository = detailedRepository;
        this.productsPrivilegeRepository = productsPrivilegeRepository;
        this.privilegeRepository = privilegeRepository;
        this.privilegeUserRepository = privilegeUserRepository;
        this.prizeInfoRepository = prizeInfoRepository;
    }


    public PayOrder save(PayOrder order) {
        return payOrderRepository.saveAndFlush(order);
    }

    public PayOrder findByOrderNo(String orderNo) {
        return payOrderRepository.findByOrderNo(orderNo);
    }

    public PayOrder findByCustomerIdAndProductIdAndOrderStatus(String customerId, Integer productId, int i) {
        return payOrderRepository.findByCustomerIdAndProductIdAndOrderStatus(customerId, productId, i);
    }

    public synchronized PayOrder createOrder(String merchantNo, String orderNo, String paymount, String token, Products products, Integer integral,Integer privilegeId) {

        PayOrder order = payOrderRepository.findByCustomerIdAndProductIdAndOrderStatus(token, products.getProductsId(), 1);
        if (order == null) {
            order = new PayOrder();

            //减少库存
            products.setNumber(products.getNumber() - 1);
            productsRepository.saveAndFlush(products);
            //减积分
            Integral integral1 = integralRepository.findByCustomerId(token);
            integral1.setResidualFraction(integral1.getResidualFraction() - integral);
            integral1.setUsedFraction(integral1.getUsedFraction() + integral);
            integralRepository.saveAndFlush(integral1);

        }
        order.setPrivilegeId(privilegeId);
        order.setMerchantNo(merchantNo);
        order.setOrderNo(orderNo);
        order.setCreateDate(new Date());
        order.setOrderStatus(1);
        order.setPayAmount(paymount);
        order.setCustomerId(token);
        order.setProductId(products.getProductsId());
        order.setIntegral(integral);

        return payOrderRepository.saveAndFlush(order);
    }


    public void cancellation(Integer productId, String customerId) {
        PayOrder payOrder = payOrderRepository.findByCustomerIdAndProductIdAndOrderStatus(customerId, productId, 1);

        if (payOrder != null) {
            payOrder.setOrderStatus(4);
            payOrder.setCancelDate(new Date());
            payOrderRepository.saveAndFlush(payOrder);
            //恢复库存
            Optional<Products> productsOptional = productsRepository.findById(productId);
            if (productsOptional.isPresent()) {
                Products products = productsOptional.get();
                products.setNumber(products.getNumber() + 1);
                productsRepository.saveAndFlush(products);
            }

            //恢复积分
            Integral integral1 = integralRepository.findByCustomerId(customerId);
            integral1.setResidualFraction(integral1.getResidualFraction() + payOrder.getIntegral());
            integral1.setUsedFraction(integral1.getUsedFraction() - payOrder.getIntegral());
            integralRepository.saveAndFlush(integral1);

            //恢复特权数量
            if (payOrder.getPrivilegeId()!= null && payOrder.getPrivilegeId() != 0) {
                Optional<ProductsPrivilege> optional = productsPrivilegeRepository.findByProductsIdAndPrivilegeId(payOrder.getProductId(), payOrder.getPrivilegeId());
                if (optional.isPresent()) {
                    ProductsPrivilege productsPrivilege = optional.get();
                    productsPrivilege.setNumber(productsPrivilege.getNumber());
                    productsPrivilegeRepository.saveAndFlush(productsPrivilege);
                }
            }
        }

    }

    public synchronized void paySuccess(String orderNo, String token) {
        PayOrder payOrder = payOrderRepository.findByOrderNo(orderNo);
        if (payOrder != null) {
            if (payOrder.getOrderStatus() == 2) {
                return;
            }
            payOrder.setOrderStatus(2);
            payOrder = payOrderRepository.saveAndFlush(payOrder);

            //派发奖品
            List<PrizeInfo> prizeInfoList = prizeInfoRepository.findAllByProductsIdAndStatus(payOrder.getProductId(), 1);
            if (prizeInfoList.size() > 0) {
                PrizeInfo prizeInfo = prizeInfoList.get(0);
                prizeInfo.setStatus(2);
                prizeInfo.setCustomerId(payOrder.getCustomerId());
                prizeInfo.setDistributionDate(new Date());
                prizeInfo.setOrderId(payOrder.getOrderId());
                prizeInfoRepository.save(prizeInfo);

            } else {
                log.info("奖品不足：orderId:{}", payOrder.getOrderId());
            }

            Integral integral = integralRepository.findByCustomerId(token);
            Optional<Products> products = productsRepository.findById(payOrder.getProductId());
            SnowflakeIdFactory factory = new SnowflakeIdFactory(1, 1);
            IntegralDetailed detailed = new IntegralDetailed();
            detailed.setDetailId(String.valueOf(factory.nextId()));
            detailed.setIntegralId(integral.getIntegralId());
            detailed.setFraction(payOrder.getIntegral());
            detailed.setType(2);
            detailed.setSource(0);
            if (products.isPresent()) {
                detailed.setTradeName(products.get().getName());
            } else {
                detailed.setTradeName("购买商品");
            }
            detailed.setTransactionDate(new Date());
            detailedRepository.save(detailed);

            List<ProductsPrivilege> ppList = productsPrivilegeRepository.findAllByProductsId(payOrder.getProductId());

            List<PrivilegeUser> privilegeUserList = privilegeUserRepository.findAllByCustomerId(payOrder.getCustomerId());

            for (PrivilegeUser privilegeUser : privilegeUserList) {
                for (ProductsPrivilege productsPrivilege : ppList) {
                    if (productsPrivilege.getProductsId().equals(payOrder.getProductId())) {
                        if (privilegeUser.getPrivilegeId().equals(productsPrivilege.getPrivilegeId())) {
                            privilegeUser.setStatus(2);
                            privilegeUserRepository.save(privilegeUser);
                            break;
                        }
                    }
                }
            }

        }

    }
}
