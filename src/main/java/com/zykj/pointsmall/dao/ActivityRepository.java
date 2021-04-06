package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tang
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    List<Activity> findAllByState(int state);

    Activity findByAlias(String activityName);

    Activity findByName(String activityName);

    Activity findByActivityDesc(String activityId);
}
