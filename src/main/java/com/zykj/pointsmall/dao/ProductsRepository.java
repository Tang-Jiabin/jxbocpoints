package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.BocUserInfo;
import com.zykj.pointsmall.pojo.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tang
 */
@Repository
public interface ProductsRepository extends JpaRepository<Products,Integer> , JpaSpecificationExecutor<Products> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    List<Products> findAllByProductsIdIn(List<Integer> productsIdList);


    List<Products> findAllByVipOrVip(int vip, int vip2);

    Products findByName(String name);

    List<Products> findAllByParentId(Integer productsId);

    List<Products> findAllByVipOrVipAndStatus(int i, int i1, int i2);

    List<Products> findAllByProductsIdInAndStatus(List<Integer> productsIdList, int i);

    List<Products> findAllByParentIdIn(List<Integer> productsIdList);

    List<Products> findAllByStatus(int i);

    List<Products> findAllByParentIdNotAndStatus(int parentId, int status);

    List<Products> findAllByParentIdAndStatus(int parentId, int status);

    List<Products> findAllByParentIdInAndStatus(List<Integer> productsIdList, int i);
}
