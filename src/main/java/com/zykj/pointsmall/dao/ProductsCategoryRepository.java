package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.ProductsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsCategoryRepository extends JpaRepository<ProductsCategory,Integer> {
    List<ProductsCategory> findAllByStatus(int status);
}
