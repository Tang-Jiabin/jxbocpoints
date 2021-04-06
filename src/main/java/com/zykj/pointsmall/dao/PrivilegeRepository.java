package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.Privilege;
import com.zykj.pointsmall.pojo.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tang
 */
@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege,Integer>, JpaSpecificationExecutor<Privilege> {
    List<Privilege> findAllByPrivilegeIdInAndState(List<Integer> privilegeIdList, int state);

    Privilege findByPrivilegeId(Integer privilegeId);
}
