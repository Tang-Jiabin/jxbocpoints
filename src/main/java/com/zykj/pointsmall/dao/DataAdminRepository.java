package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.DataAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataAdminRepository extends JpaRepository<DataAdmin,Integer> {

    Optional<DataAdmin> findByLoginNoAndPwd(String adminName, String adminPwd);
}
