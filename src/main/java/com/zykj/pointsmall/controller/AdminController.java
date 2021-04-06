package com.zykj.pointsmall.controller;

import com.alibaba.fastjson.JSONObject;
import com.zykj.pointsmall.common.ServerResponse;
import com.zykj.pointsmall.common.interceptor.Authorization;
import com.zykj.pointsmall.common.log.WebLog;
import com.zykj.pointsmall.dto.AdminDTO;
import com.zykj.pointsmall.dto.BranchOutletsDTO;
import com.zykj.pointsmall.dto.GoodsTypeInfoDTO;
import com.zykj.pointsmall.dto.PrivilegeDTO;
import com.zykj.pointsmall.pojo.Privilege;
import com.zykj.pointsmall.service.AdminService;
import com.zykj.pointsmall.service.PrivilegeService;
import com.zykj.pointsmall.service.ProductsService;
import com.zykj.pointsmall.service.UserService;
import com.zykj.pointsmall.vo.ActivityDataVO;
import com.zykj.pointsmall.vo.PrivilegeVO;
import com.zykj.pointsmall.vo.ProductsVO;
import com.zykj.pointsmall.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 管理员
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-02-04
 */
@Slf4j
@RestController
@RequestMapping("admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final ProductsService productsService;
    private final PrivilegeService privilegeService;

    public AdminController(AdminService adminService, UserService userService, ProductsService productsService, PrivilegeService privilegeService) {
        this.adminService = adminService;
        this.userService = userService;
        this.productsService = productsService;
        this.privilegeService = privilegeService;
    }

    @WebLog("管理员登录")
    @PostMapping(value = "signIn")
    public ServerResponse<AdminDTO> signIn(@RequestBody AdminDTO adminDTO) {

        AdminDTO login = adminService.login(adminDTO);

        if (!StringUtils.hasText(login.getToken())) {
            return ServerResponse.createMessage(411, "用户名密码错误");
        }

        return ServerResponse.createSuccess(login);
    }

    @WebLog("用户列表")
    @Authorization
    @GetMapping(value = "getUserList")
    public ServerResponse getUserList(int page, String userNo) {

        Page<UserVO> userPage = userService.findUserPage(page, userNo);

        return ServerResponse.ok(userPage);
    }

    @WebLog("商品列表")
    @Authorization
    @GetMapping(value = "getGoodsPage")
    public ServerResponse getGoodsPage(int page, String name) {

        Page<ProductsVO> goodsPage = productsService.findGoodsPage(page, name);

        return ServerResponse.ok(goodsPage);
    }

    @WebLog("商品信息")
    @Authorization
    @GetMapping(value = "getGoodsInfo")
    public ServerResponse getGoodsInfo(Integer productsId) {

        ProductsVO productDetailsInfo = productsService.getProductDetailsInfo(productsId);

        return ServerResponse.ok(productDetailsInfo);
    }


    @WebLog("商品分类信息")
    @Authorization
    @GetMapping(value = "getGoodsTypeInfo")
    public ServerResponse getGoodsTypeInfo(Integer productsId) {

        GoodsTypeInfoDTO goodsTypeInfo = productsService.getGoodsTypeInfo(productsId);

        return ServerResponse.ok(goodsTypeInfo);
    }

    @WebLog("所有商品列表")
    @Authorization
    @GetMapping(value = "getAllProducts")
    public ServerResponse getAllProducts() {
        List<ProductsVO> allProducts = productsService.getAllProducts();
        return ServerResponse.ok(allProducts);
    }


    @WebLog("特权列表")
    @Authorization
    @GetMapping(value = "getPrivilegePage")
    public ServerResponse getPrivilegePage(int page, String name) {
        Page<PrivilegeVO> privilegeVOPage = privilegeService.privilegePage(page, name);
        return ServerResponse.ok(privilegeVOPage);
    }


    @WebLog("特权信息")
    @Authorization
    @GetMapping(value = "getPrivilegeInfo")
    public ServerResponse getPrivilegeInfo(Integer privilegeId) {
        JSONObject json = privilegeService.getPrivilegeInfo(privilegeId);
        return ServerResponse.ok(json);
    }


    @WebLog("添加特权")
    @PostMapping(value = "addPrivilege")
    public ServerResponse addPrivilege(@RequestBody PrivilegeDTO privilegeDTO) {
        Privilege privilege = privilegeService.addPrivilege(privilegeDTO);
        return ServerResponse.ok(privilege);
    }


    @WebLog("添加特权文件")
    @PostMapping(value = "addPrivilegeFile")
    public ServerResponse addPrivilegeFile(@RequestParam Integer privilegeId, @RequestParam MultipartFile userFile) {
        try {
            privilegeService.addPrivilegeFile(privilegeId, userFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ServerResponse.ok();
    }

    @WebLog("添加商品")
    @PostMapping(value = "addProducts")
    public ServerResponse addProducts(@RequestParam String name,
                                      @RequestParam Integer type,
                                      @RequestParam Integer category,
                                      @RequestParam Integer level,
                                      @RequestParam MultipartFile listPic,
                                      @RequestParam MultipartFile detailPic,
                                      @RequestParam String subProducts) {
        productsService.add(name, type, category, level, listPic, detailPic, subProducts);
        return ServerResponse.ok();
    }


    @WebLog("修改商品")
    @PostMapping(value = "modifyProducts")
    public ServerResponse modifyProducts(@RequestParam Integer productsId,
                                         @RequestParam String name,
                                         @RequestParam Integer type,
                                         @RequestParam Integer category,
                                         @RequestParam Integer level,
                                         @RequestParam(required = false) MultipartFile listPic,
                                         @RequestParam(required = false) MultipartFile detailPic) {
        productsService.modifyProducts(productsId, name, type, category, level, listPic, detailPic);
        return ServerResponse.ok();
    }


    @WebLog("删除商品")
    @PostMapping(value = "delProducts")
    public ServerResponse delProducts(@RequestParam Integer productsId) {
        productsService.delProducts(productsId);
        return ServerResponse.ok();
    }


    @WebLog("删除兑换码")
    @PostMapping(value = "delCode")
    public ServerResponse delCode(@RequestParam Integer prizeId) {
        productsService.delCode(prizeId);
        return ServerResponse.ok();
    }

    @WebLog("添加兑换码")
    @PostMapping(value = "addCode")
    public ServerResponse addCode(@RequestParam Integer productsId, @RequestParam String code) {
        productsService.addCode(productsId, code);
        return ServerResponse.ok();
    }


    @WebLog("批量添加兑换码")
    @PostMapping(value = "bathAddCode")
    public ServerResponse bathAddCode(@RequestParam Integer productsId, @RequestParam(required = false) MultipartFile codeExcel) {
        productsService.bathAddCode(productsId, codeExcel);
        return ServerResponse.ok();
    }

    @WebLog("修改商品分类")
    @PostMapping(value = "editType")
    public ServerResponse editType(Integer productsId, String name, Integer money, Integer number, String price, Integer integral) {
        productsService.editType(productsId, name, money, number, price, integral);
        return ServerResponse.ok();
    }

    //==========================================================

    @WebLog("数据后台管理员登录")
    @PostMapping(value = "data/signIn")
    public ServerResponse<AdminDTO> dataSignIn(@RequestBody AdminDTO adminDTO) {

        AdminDTO login = adminService.dataAdminlogin(adminDTO);

        if (!StringUtils.hasText(login.getToken())) {
            return ServerResponse.createMessage(411, "用户名密码错误");
        }

        return ServerResponse.createSuccess(login);
    }

    @WebLog("数据后台管理员修改密码")
    @Authorization
    @PostMapping(value = "data/updatePwd")
    public ServerResponse<AdminDTO> updatePwd(@RequestBody AdminDTO adminDTO, @RequestAttribute String token) {

        Integer adminId = Integer.valueOf(token);
        AdminDTO update = adminService.dataAdminUpdatePwd(adminDTO, adminId);

        if (StringUtils.hasText(update.getAdminName())) {
            return ServerResponse.ok();
        }
        return ServerResponse.createMessage(411, "修改失败");
    }

    @WebLog("数据后台数据")
    @GetMapping(value = "data/get")
    public ServerResponse<Object> dataGet(Integer page, Integer limit, String name, String branchName, String outletsName, Integer canyu, Integer month) {

        Object[] arr = adminService.getData(page, limit, name, branchName, outletsName, canyu, month);


        return ServerResponse.createMessage(0, String.valueOf(arr[1]), arr[0]);
    }


    @WebLog("数据后台分行信息")
    @Authorization
    @GetMapping(value = "data/getBranchInfo")
    public ServerResponse getBranchInfo(@RequestAttribute String token) {
        Integer dataAdminId = Integer.valueOf(token);
        List<BranchOutletsDTO> branchInfo = adminService.getBranchInfo(dataAdminId);

        return ServerResponse.ok(branchInfo);
    }

}
