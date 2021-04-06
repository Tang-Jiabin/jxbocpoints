package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch,Integer> {
    Branch findByBranchName(String branchName);

    List<Branch> findAllByBranchIdIn(List<Integer> branchIdList);

    Optional<Branch> findByBranchNo(String loginNo);
}
