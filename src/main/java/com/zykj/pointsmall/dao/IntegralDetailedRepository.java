package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.IntegralDetailed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegralDetailedRepository extends JpaRepository<IntegralDetailed,String> {
    List<IntegralDetailed> findAllByIntegralId(Integer integralId);

    IntegralDetailed findByActivityIdAndIntegralId(Integer activityId, Integer integralId);
}
