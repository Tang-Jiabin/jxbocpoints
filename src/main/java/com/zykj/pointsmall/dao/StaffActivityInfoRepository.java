package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.StaffActivityInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffActivityInfoRepository extends JpaRepository<StaffActivityInfo,Integer> {
    StaffActivityInfo findByPhone(String phone);

    List<StaffActivityInfo> findAllByCustomerId(String token);

    List<StaffActivityInfo> findAllByCustomerIdIn(List<String> staffCustomerIdList);
}
