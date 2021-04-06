package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    Staff findByPhone(String phone);

    Staff findByCustomerId(String token);

    List<Staff> findAllByBranchId(Integer branchId);

    List<Staff> findAllByBranchIdIn(List<Integer> branchIdList);

    List<Staff> findAllByCustomerIdIsNotNull();

    List<Staff> findAllByBranchIdInAndCustomerIdIsNotNull(List<Integer> branchIdList);

    List<Staff> findAllByBranchIdAndCustomerIdIsNotNull(Integer branchId);
}
