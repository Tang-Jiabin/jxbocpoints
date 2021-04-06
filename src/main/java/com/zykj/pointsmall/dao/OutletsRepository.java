package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.Outlets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutletsRepository extends JpaRepository<Outlets,Integer> {
    List<Outlets> findAllByBranchId(Integer branchId);

    List<Outlets> findAllByOutletsName(String outletsName);

    List<Outlets> findAllByOutletsNo(String loginNo);
}
