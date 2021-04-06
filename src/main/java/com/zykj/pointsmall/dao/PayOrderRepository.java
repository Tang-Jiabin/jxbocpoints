package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.PayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayOrderRepository extends JpaRepository<PayOrder,Integer> {
    PayOrder findByOrderNo(String orderNo);

    PayOrder findByCustomerIdAndProductIdAndOrderStatus(String customerId, Integer productId, int i);

    List<PayOrder> findAllByCustomerId(String token);

    List<PayOrder> findAllByOrderStatus(int status);
}
