package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.ProductsPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author tang
 */
@Repository
public interface ProductsPrivilegeRepository extends JpaRepository<ProductsPrivilege,Long> {
    List<ProductsPrivilege> findAllByPrivilegeIdIn(List<Integer> privilegeIdList);

    List<ProductsPrivilege> findAllByProductsIdIn(List<Integer> productIdList);

    List<ProductsPrivilege> findAllByProductsId(Integer productsId);

    Optional<ProductsPrivilege> findByProductsIdAndPrivilegeId(Integer productsId,Integer privilegeId);

    List<ProductsPrivilege> findAllByParentId(Integer productsId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    List<ProductsPrivilege> findAllByPrivilegeId(Integer privilegeId);
}
