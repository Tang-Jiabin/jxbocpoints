package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.MenuPower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuPowerRepository extends JpaRepository<MenuPower,Integer> {
    List<MenuPower> findAllByAdminId(Integer adminId);
}
