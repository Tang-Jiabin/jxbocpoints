package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.PrizeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrizeInfoRepository extends JpaRepository<PrizeInfo,Integer> {
    List<PrizeInfo> findAllByProductsIdAndStatus(Integer productId, int i);

    PrizeInfo findByOrderIdAndCustomerId(Integer orderId, String customerId);

    List<PrizeInfo> findAllByProductsIdAndStatusIsNot(Integer productsId, int i);
}
