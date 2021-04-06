package com.zykj.pointsmall.controller;

import com.alibaba.fastjson.JSONObject;
import com.zykj.pointsmall.common.BeanUtils;
import com.zykj.pointsmall.common.ServerResponse;
import com.zykj.pointsmall.common.interceptor.Authorization;
import com.zykj.pointsmall.common.log.WebLog;
import com.zykj.pointsmall.dto.PrivilegeProductsDTO;
import com.zykj.pointsmall.dto.ProductsCategoryDTO;
import com.zykj.pointsmall.pojo.Products;
import com.zykj.pointsmall.pojo.ProductsPrivilege;
import com.zykj.pointsmall.service.ProductsService;
import com.zykj.pointsmall.vo.ProductsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 商品
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-08
 */
@Slf4j
@RestController
@RequestMapping("products")
public class ProductsController {

    private final ProductsService productsService;

    @Autowired
    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @WebLog("获取商品详情")
    @Authorization
    @GetMapping("getProductDetails")
    public ServerResponse<ProductsVO> getProductDetails(@RequestParam Integer productId, @RequestAttribute String token) {
        Optional<Products> productsOptional = productsService.getProductDetails(productId);
        if (!productsOptional.isPresent()) {
            return ServerResponse.createMessage(411,"商品信息错误");
        }
        Products products = productsOptional.get();
        if (products.getParentId()!=0) {
            return ServerResponse.createMessage(411,"商品信息错误");
        }
        List<Products> subProductList = productsService.getLicensedGoods(products,token);

        ProductsVO productsVO = BeanUtils.productToVO(products);
        if (productsVO != null) {
            productsVO.setSubProductList(subProductList);
        }
        return ServerResponse.createSuccess(productsVO);
    }

    @WebLog("获取商品详情")
    @Authorization
    @GetMapping("getVipProductDetails")
    public ServerResponse<ProductsVO> getVipProductDetails(@RequestParam Integer productId, @RequestAttribute String token) {
        Optional<Products> productsOptional = productsService.getProductDetails(productId);
        if (!productsOptional.isPresent()) {
            return ServerResponse.createMessage(411,"商品信息错误");
        }
        Products products = productsOptional.get();
        if (products.getParentId()!=0) {
            return ServerResponse.createMessage(411,"商品信息错误");
        }
        List<Products> subProductList = productsService.getVipLicensedGoods(products,token);

        ProductsVO productsVO = BeanUtils.productToVO(products);
        if (productsVO != null) {
            productsVO.setSubProductList(subProductList);
        }
        return ServerResponse.createSuccess(productsVO);
    }

    @WebLog("获取会员商品列表")
    @GetMapping("getVipGoods")
    @Authorization
    public ServerResponse<List<PrivilegeProductsDTO>> getVipGoods(@RequestAttribute String token){

        //专属权益
        List<PrivilegeProductsDTO> productsDTOS = productsService.getVipProductsList(token);

        return ServerResponse.createSuccess(productsDTOS);
    }

    @WebLog("获取大众商品列表")
    @GetMapping("getPublicGoods")
    @Authorization
    public ServerResponse<List<ProductsCategoryDTO>> getPublicGoods(@RequestAttribute String token){

        //大众权益
        List<ProductsCategoryDTO> categoryDTOList = productsService.getPublicProducts();

        return ServerResponse.createSuccess(categoryDTOList);
    }
}
