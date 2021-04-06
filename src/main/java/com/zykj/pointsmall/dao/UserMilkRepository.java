package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.Integral;
import com.zykj.pointsmall.pojo.UserMilk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author tang
 */
@Repository
public interface UserMilkRepository extends JpaRepository<UserMilk, Integral> {

    UserMilk findByCustomerId(String userId);
}
