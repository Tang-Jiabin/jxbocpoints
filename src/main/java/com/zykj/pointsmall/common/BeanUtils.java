package com.zykj.pointsmall.common;

import com.zykj.pointsmall.dto.UserDTO;
import com.zykj.pointsmall.pojo.BocUserInfo;
import com.zykj.pointsmall.pojo.Products;
import com.zykj.pointsmall.vo.ProductsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Bean
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-05
 */
@Slf4j
public class BeanUtils {


    public static BocUserInfo userDtoToUserPo(UserDTO userDTO) {

        if (userDTO == null) {
            log.info("COPY UserDTO IS NULL");
            return null;
        }

        BocUserInfo bocUserInfo = new BocUserInfo();

        if (StringUtils.hasText(userDTO.getCustomerId())) {
            bocUserInfo.setCustomerId(userDTO.getCustomerId());
        }
        if (StringUtils.hasText(userDTO.getDiscountCoupon())) {
            bocUserInfo.setDiscountCoupon(userDTO.getDiscountCoupon());
        }
        if (StringUtils.hasText(userDTO.getVersionNo())) {
            bocUserInfo.setVersionNo(userDTO.getVersionNo());
        }
        if (StringUtils.hasText(userDTO.getCustomerName())) {
            bocUserInfo.setCustomerName(userDTO.getCustomerName());
        }
        if (StringUtils.hasText(userDTO.getBranchId())) {
            bocUserInfo.setBranchId(userDTO.getBranchId());
        }
        if (StringUtils.hasText(userDTO.getMobile())) {
            bocUserInfo.setMobile(userDTO.getMobile());
        }
        if (StringUtils.hasText(userDTO.getCreateDate())) {
            bocUserInfo.setCreateDate(userDTO.getCreateDate());
        }
        if (StringUtils.hasText(userDTO.getCifNumber())) {
            bocUserInfo.setCifNumber(userDTO.getCifNumber());
        }
        if (StringUtils.hasText(userDTO.getIdentityNumber())) {
            bocUserInfo.setIdentityNumber(userDTO.getIdentityNumber());
        }
        if (StringUtils.hasText(userDTO.getIdentityType())) {
            bocUserInfo.setIdentityType(userDTO.getIdentityType());
        }
        if (StringUtils.hasText(userDTO.getIbknum())) {
            bocUserInfo.setIbknum(userDTO.getIbknum());
        }
        if (StringUtils.hasText(userDTO.getLocalType())) {
            bocUserInfo.setLocalType(userDTO.getLocalType());
        }
        if (StringUtils.hasText(userDTO.getOrgId())) {
            bocUserInfo.setOrgId(userDTO.getOrgId());
        }
        if (StringUtils.hasText(userDTO.getMerId())) {
            bocUserInfo.setMerId(userDTO.getMerId());
        }
        if (StringUtils.hasText(userDTO.getGender())) {
            bocUserInfo.setGender(userDTO.getGender());
        }

        return bocUserInfo;
    }

    public static UserDTO userPoToUserDto(BocUserInfo userInfo) {

        if (userInfo == null) {
            log.info("COPY UserPOJO IS NULL");
            return null;
        }

        UserDTO userDTO = new UserDTO();

        if (StringUtils.hasText(userInfo.getCustomerId())) {
            userDTO.setCustomerId(userInfo.getCustomerId());
        }
        if (StringUtils.hasText(userInfo.getDiscountCoupon())) {
            userDTO.setDiscountCoupon(userInfo.getDiscountCoupon());
        }
        if (StringUtils.hasText(userInfo.getVersionNo())) {
            userDTO.setVersionNo(userInfo.getVersionNo());
        }
        if (StringUtils.hasText(userInfo.getCustomerName())) {
            userDTO.setCustomerName(userInfo.getCustomerName());
        }
        if (StringUtils.hasText(userInfo.getBranchId())) {
            userDTO.setBranchId(userInfo.getBranchId());
        }
        if (StringUtils.hasText(userInfo.getMobile())) {
            userDTO.setMobile(userInfo.getMobile());
        }
        if (StringUtils.hasText(userInfo.getCreateDate())) {
            userDTO.setCreateDate(userInfo.getCreateDate());
        }
        if (StringUtils.hasText(userInfo.getCifNumber())) {
            userDTO.setCifNumber(userInfo.getCifNumber());
        }
        if (StringUtils.hasText(userInfo.getIdentityNumber())) {
            userDTO.setIdentityNumber(userInfo.getIdentityNumber());
        }
        if (StringUtils.hasText(userInfo.getIdentityType())) {
            userDTO.setIdentityType(userInfo.getIdentityType());
        }
        if (StringUtils.hasText(userInfo.getIbknum())) {
            userDTO.setIbknum(userInfo.getIbknum());
        }
        if (StringUtils.hasText(userInfo.getLocalType())) {
            userDTO.setLocalType(userInfo.getLocalType());
        }
        if (StringUtils.hasText(userInfo.getOrgId())) {
            userDTO.setOrgId(userInfo.getOrgId());
        }
        if (StringUtils.hasText(userInfo.getMerId())) {
            userDTO.setMerId(userInfo.getMerId());
        }
        if (StringUtils.hasText(userInfo.getGender())) {
            userDTO.setGender(userInfo.getGender());
        }

        return userDTO;
    }

    public static ProductsVO productToVO(Products products) {
        if (products == null) {
            log.info("COPY products IS NULL");
            return null;
        }
        ProductsVO vo = new ProductsVO();
        vo.setProductsId(products.getProductsId());
        vo.setName(products.getName());
        vo.setMoney(products.getMoney());
        vo.setImgUrl(products.getImgUrl());
        vo.setDetailImgUrl(products.getDetailImgUrl());
        vo.setNumber(products.getNumber());
        vo.setVip(products.getVip());
        vo.setCategoryId(products.getCategoryId());
        vo.setTypeId(products.getTypeId());
        vo.setSubProductList(null);

        return vo;
    }
}
