package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.PrivilegeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tang
 */
@Repository
public interface PrivilegeUserRepository extends JpaRepository<PrivilegeUser,Long> {
    List<PrivilegeUser> findAllByCustomerId(String customerId);

    List<PrivilegeUser> findAllByPrivilegeIdIn(List<Integer> privilegeIdList);

    List<PrivilegeUser> findAllByCustomerIdAndStatus(String token, int i);

    List<PrivilegeUser> findAllByCustomerIdIn(List<String> customerIdList);

    List<PrivilegeUser> findAllByPrivilegeId(Integer privilegeId);

    List<PrivilegeUser> findAllByStatus(int i);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    List<PrivilegeUser> findAllByPrivilegeIdAndStatus(Integer privilegeId, int i);
}
