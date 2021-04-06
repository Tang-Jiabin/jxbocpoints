package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer> {
    Admin findByAdminNameAndAdminPwd(String adminName, String adminPwd);
}
