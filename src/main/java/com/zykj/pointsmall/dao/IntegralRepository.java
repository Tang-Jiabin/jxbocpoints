package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.Integral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tang
 */
@Repository
public interface IntegralRepository extends JpaRepository<Integral,Integer> {
    Integral findByCustomerId(String customerId);

    List<Integral> findAllByCustomerIdIn(List<String> customerIdList);

    Integral findByPhone(String phone);
}
