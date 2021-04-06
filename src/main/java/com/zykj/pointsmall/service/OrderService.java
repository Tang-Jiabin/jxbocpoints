package com.zykj.pointsmall.service;

import com.zykj.pointsmall.dao.PayOrderRepository;
import com.zykj.pointsmall.dao.PrizeInfoRepository;
import com.zykj.pointsmall.dao.ProductsRepository;
import com.zykj.pointsmall.pojo.PayOrder;
import com.zykj.pointsmall.pojo.PrizeInfo;
import com.zykj.pointsmall.pojo.Products;
import com.zykj.pointsmall.vo.PayOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
@Service
public class OrderService {

    private final PayOrderRepository orderRepository;
    private final ProductsRepository productsRepository;
    private final PrizeInfoRepository prizeInfoRepository;

    @Autowired
    public OrderService(PayOrderRepository orderRepository, ProductsRepository productsRepository, PrizeInfoRepository prizeInfoRepository) {
        this.orderRepository = orderRepository;
        this.productsRepository = productsRepository;
        this.prizeInfoRepository = prizeInfoRepository;
    }

    public List<PayOrderVO> getDetails(String token) {
        List<PayOrder> orderList = orderRepository.findAllByCustomerId(token);
        List<Integer> productIdList = orderList.stream().map(PayOrder::getProductId).collect(Collectors.toList());
        List<Products> productsList = productsRepository.findAllByProductsIdIn(productIdList);

        List<PayOrderVO> orderVOList = new ArrayList<>();
        PayOrderVO orderVO;

        for (PayOrder payOrder : orderList) {
            for (Products products : productsList) {
                if(payOrder.getProductId().equals(products.getProductsId())){
                    orderVO = new PayOrderVO();
                    BeanUtils.copyProperties(payOrder,orderVO);
                    orderVO.setProducts(products);
                    orderVO.setPrivilegeId(payOrder.getPrivilegeId());
                    orderVOList.add(orderVO);
                }
            }
        }

        return orderVOList;

    }

    public List<PayOrder> findAllByOrderStatus(int status) {
        return orderRepository.findAllByOrderStatus(status);
    }

    public PayOrderVO getOrderDetails(String orderNo) {
        PayOrderVO orderVO = new PayOrderVO();
        PayOrder order = orderRepository.findByOrderNo(orderNo);
        if (order == null) {
            return null;
        }
        BeanUtils.copyProperties(order,orderVO);
        Optional<Products> productsOptional = productsRepository.findById(order.getProductId());
        if (productsOptional.isPresent()) {
            orderVO.setProducts(productsOptional.get());
            PrizeInfo prizeInfo = prizeInfoRepository.findByOrderIdAndCustomerId(order.getOrderId(),order.getCustomerId());
            if (prizeInfo != null) {
                orderVO.setCode(prizeInfo.getCode());
            }

        }

        return orderVO;
    }
}
