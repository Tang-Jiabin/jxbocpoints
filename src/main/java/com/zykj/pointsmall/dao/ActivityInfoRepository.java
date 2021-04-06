package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.ActivityInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityInfoRepository extends JpaRepository<ActivityInfo,Integer>, JpaSpecificationExecutor<ActivityInfo> {
}
