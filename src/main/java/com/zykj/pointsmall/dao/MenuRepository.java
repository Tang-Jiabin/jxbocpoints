package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    List<Menu> findAllByMenuIdIn(List<Integer> menuIdList);
}
