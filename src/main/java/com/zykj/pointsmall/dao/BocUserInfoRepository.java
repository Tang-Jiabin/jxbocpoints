package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.BocUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author tang
 */
@Repository
public interface BocUserInfoRepository extends JpaRepository<BocUserInfo,String>, JpaSpecificationExecutor<BocUserInfo> {
    BocUserInfo findByMobile(String phone);


    BocUserInfo findByCustomerIdAndFollow(String token, int i);
}
