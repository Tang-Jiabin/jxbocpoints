package com.zykj.pointsmall.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zykj.pointsmall.common.*;
import com.zykj.pointsmall.dao.*;
import com.zykj.pointsmall.dto.GoodsTypeInfoDTO;
import com.zykj.pointsmall.dto.PrivilegeProductsDTO;
import com.zykj.pointsmall.dto.ProductsCategoryDTO;
import com.zykj.pointsmall.pojo.*;
import com.zykj.pointsmall.vo.ProductsVO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 商品
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-07
 */
@Slf4j
@Service
public class ProductsService {

    private final PrivilegeUserRepository privilegeUserRepository;
    private final PrivilegeRepository privilegeRepository;
    private final ProductsRepository productsRepository;
    private final ProductsPrivilegeRepository productsPrivilegeRepository;
    private final ProductsCategoryRepository productsCategoryRepository;
    private final PrizeInfoRepository prizeInfoRepository;

    @Autowired
    public ProductsService(PrivilegeUserRepository privilegeUserRepository, PrivilegeRepository privilegeRepository, ProductsRepository productsRepository, ProductsPrivilegeRepository productsPrivilegeRepository, ProductsCategoryRepository productsCategoryRepository, PrizeInfoRepository prizeInfoRepository) {
        this.privilegeUserRepository = privilegeUserRepository;
        this.privilegeRepository = privilegeRepository;
        this.productsRepository = productsRepository;
        this.productsPrivilegeRepository = productsPrivilegeRepository;
        this.productsCategoryRepository = productsCategoryRepository;
        this.prizeInfoRepository = prizeInfoRepository;
    }

    public List<Products> getVipUserProductsList(String customerId) {
        //查询用户特权
        List<PrivilegeUser> privilegeUserList = privilegeUserRepository.findAllByCustomerId(customerId);

        List<Integer> privilegeIdList = privilegeUserList.stream().map(PrivilegeUser::getPrivilegeId).collect(Collectors.toList());

        //查询特权对应商品
        List<ProductsPrivilege> productsPrivilegeList = productsPrivilegeRepository.findAllByPrivilegeIdIn(privilegeIdList);

        List<Integer> productsIdList = productsPrivilegeList.stream().map(ProductsPrivilege::getProductsId).collect(Collectors.toList());

        List<Products> productsList = productsRepository.findAllByProductsIdIn(productsIdList);

        productsIdList = productsList.stream().map(Products::getParentId).collect(Collectors.toList());

        return productsRepository.findAllByProductsIdIn(productsIdList);
    }

    public List<ProductsCategoryDTO> getPublicProducts() {

        List<ProductsCategory> categoryList = productsCategoryRepository.findAllByStatus(1);

        List<Products> productsList = productsRepository.findAllByVipOrVipAndStatus(2, 1, 1);
        List<Integer> productsIdList = productsList.stream().map(Products::getParentId).collect(Collectors.toList());
        List<Products> finalProductsList = productsRepository.findAllByProductsIdIn(productsIdList);

        return getProductsCategoryDTOS(finalProductsList, categoryList);
    }

    public Products add(Products products) {
        if (products == null) {
            return null;
        }
        if (StringUtils.hasText(products.getName())) {
            Products pro = productsRepository.findByName(products.getName());

            if (pro == null) {
                products = productsRepository.save(products);
            }
        } else {
            return null;
        }

        return products;
    }

    public Optional<Products> getProductDetails(Integer productId) {
        return productsRepository.findById(productId);
    }

    public List<Products> getSubProductList(Integer productsId) {
        return productsRepository.findAllByParentId(productsId);
    }

    public List<Products> getLicensedGoods(Products products, String customerId) {

        return productsRepository.findAllByParentId(products.getProductsId());

    }

    public List<ProductsCategoryDTO> getVipUserCategoryProductsList(String token) {
        List<Products> vipUserProductsList = getVipUserProductsList(token);
        List<ProductsCategory> categoryList = productsCategoryRepository.findAllByStatus(1);
        return getProductsCategoryDTOS(vipUserProductsList, categoryList);
    }


    @NotNull
    private List<ProductsCategoryDTO> getProductsCategoryDTOS(List<Products> vipUserProductsList, List<ProductsCategory> categoryList) {
        List<ProductsCategoryDTO> categoryDTOList = new ArrayList<>();
        List<Products> pList;
        ProductsCategoryDTO categoryDTO;

        for (ProductsCategory category : categoryList) {
            categoryDTO = new ProductsCategoryDTO();
            categoryDTO.setName(category.getName());
            pList = new ArrayList<>();
            for (Products products : vipUserProductsList) {
                if (category.getCategoryId().equals(products.getCategoryId())) {
                    pList.add(products);
                }
            }
            pList = pList.stream().sorted(Comparator.comparing(Products::getProductsId).reversed()).collect(Collectors.toList());
            categoryDTO.setProductsList(pList);
            categoryDTOList.add(categoryDTO);
        }

        return categoryDTOList;
    }


    public List<PrivilegeProductsDTO> getVipProductsList(String token) {

        List<PrivilegeUser> puList = privilegeUserRepository.findAllByCustomerIdAndStatus(token, 1);

        List<Integer> privilegeIdList = puList.stream().map(PrivilegeUser::getPrivilegeId).collect(Collectors.toList());

        List<ProductsPrivilege> ppList = productsPrivilegeRepository.findAllByPrivilegeIdIn(privilegeIdList);

        List<Integer> productsIdList = ppList.stream().map(ProductsPrivilege::getParentId).collect(Collectors.toList());

        List<Products> productsList = productsRepository.findAllByProductsIdInAndStatus(productsIdList, 1);

        List<Privilege> privilegeList = setPrivilegeStatus(privilegeIdList);

        List<PrivilegeProductsDTO> ppDtoList = new ArrayList<>();
        PrivilegeProductsDTO ppDto;

        for (Privilege privilege : privilegeList) {
            ppDto = new PrivilegeProductsDTO();
            ppDto.setPrivilegeId(privilege.getPrivilegeId());
            ppDto.setName(privilege.getName());
            ppDto.setPrivilegeDesc(privilege.getPrivilegeDesc());
            ppDto.setState(privilege.getState());
            ppDto.setCreateDate(privilege.getCreateDate());
            if (privilege.getClosingDate() != null) {
                String str = DateUtil.getFormat(privilege.getClosingDate(), "MM月dd日");
                ppDto.setCDate(str);
            } else {
                ppDto.setCDate("28日");
            }
            ppDtoList.add(ppDto);
        }


        Products save;

        for (PrivilegeProductsDTO privilegeProductsDTO : ppDtoList) {
            List<Products> productList = new ArrayList<>();
            Set<Integer> id = new HashSet<>();
            for (ProductsPrivilege productsPrivilege : ppList) {
                if (privilegeProductsDTO.getPrivilegeId().equals(productsPrivilege.getPrivilegeId())) {
                    for (Products products : productsList) {
                        if (productsPrivilege.getParentId().equals(products.getProductsId())) {
                            if (id.add(productsPrivilege.getParentId())) {
                                products.setVipPrice(productsPrivilege.getVipPrice());
                                products.setVipIntegral(productsPrivilege.getVipIntegral());
                                productList.add(products);
                            }
                        }
                    }
                }
            }
            privilegeProductsDTO.setProductsList(productList);
        }
        return ppDtoList;
    }

    public static void main(String[] args) {

        Date start = Date.from(LocalDateTime.of(2021, 3, 17, 9, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(LocalDateTime.of(2021, 3, 19, 10, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
        Date date = new Date();

        if (start.after(date) && end.before(date)) {
            System.out.println("开始");
        }
    }

    private List<Privilege> setPrivilegeStatus(List<Integer> privilegeIdList) {
        List<Privilege> privilegeList = privilegeRepository.findAll();
        Date date = new Date();
        List<Privilege> privileges = new ArrayList<>();
        for (Privilege privilege : privilegeList) {
            if (date.after(privilege.getStartDate()) && date.before(privilege.getClosingDate())) {
                privilege.setState(1);
            } else {
                privilege.setState(2);
            }
            privilege = privilegeRepository.saveAndFlush(privilege);
            if (privilegeIdList.contains(privilege.getPrivilegeId()) && privilege.getState() == 1) {
                privileges.add(privilege);
            }
        }
        return privileges;
    }


    public List<Products> getVipLicensedGoods(Products products, String token) {

        //商品权限
        List<ProductsPrivilege> ppList = productsPrivilegeRepository.findAllByParentId(products.getProductsId());
        //用户权限
        List<PrivilegeUser> puList = privilegeUserRepository.findAllByCustomerIdAndStatus(token, 1);
        //获取所有子商品
        List<Products> productsList = productsRepository.findAllByParentId(products.getProductsId());

        List<Products> returnProductsList = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        Products returnProducts = new Products();

        for (Products products1 : productsList) {
            for (ProductsPrivilege productsPrivilege : ppList) {
                if (products1.getProductsId().equals(productsPrivilege.getProductsId())) {
                    for (PrivilegeUser privilegeUser : puList) {
                        if (privilegeUser.getPrivilegeId().equals(productsPrivilege.getPrivilegeId())) {
                            if (set.add(products1.getProductsId())) {
                                returnProducts = new Products();
                                returnProducts.setProductsId(products1.getProductsId());
                                returnProducts.setName(products1.getName());
                                returnProducts.setMoney(products1.getMoney());
                                returnProducts.setImgUrl(products1.getImgUrl());
                                returnProducts.setDetailImgUrl(products1.getDetailImgUrl());
                                returnProducts.setNumber(productsPrivilege.getNumber());
                                returnProducts.setVip(products1.getVip());
                                returnProducts.setPrice(productsPrivilege.getVipPrice());
                                returnProducts.setIntegral(productsPrivilege.getVipIntegral());
                                returnProducts.setVipPrice(productsPrivilege.getVipPrice());
                                returnProducts.setVipIntegral(productsPrivilege.getVipIntegral());
                                returnProducts.setCategoryId(products1.getCategoryId());
                                returnProducts.setTypeId(products1.getTypeId());
                                returnProducts.setParentId(products1.getParentId());
                                returnProducts.setStatus(products1.getStatus());
                                returnProductsList.add(returnProducts);
                            }
                        }
                    }
                }
            }
        }

        return returnProductsList;
    }

    public Page<ProductsVO> findGoodsPage(int page, String name) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "productsId");
        Page<Products> productsPage = productsRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + name + "%"));
            }

            list.add(criteriaBuilder.equal(root.get("parentId").as(Integer.class), 0));
            list.add(criteriaBuilder.equal(root.get("status").as(Integer.class), 1));

            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);

        List<Products> productsList = productsPage.getContent();

        List<Integer> productsIdList = productsList.stream().map(Products::getProductsId).collect(Collectors.toList());
        List<Products> subProductsList = productsRepository.findAllByParentIdInAndStatus(productsIdList, 1);

        List<ProductsVO> productsVOList = new ArrayList<>();
        ProductsVO productsVO;
        List<Products> subList;

        for (Products products : productsList) {
            productsVO = getProductsVO(products);

            subList = new ArrayList<>();
            for (Products subProducts : subProductsList) {
                if (products.getProductsId().equals(subProducts.getParentId())) {
                    subList.add(subProducts);
                }
            }
            productsVO.setSubProductList(subList);
            productsVOList.add(productsVO);
        }


        Page<ProductsVO> productsVOPage = new PageImpl<>(productsVOList, pageable, productsPage.getTotalElements());

        return productsVOPage;

    }

    public List<ProductsVO> getAllProducts() {
        List<Products> productsList = productsRepository.findAllByParentIdAndStatus(0, 1);
        List<Integer> productsIdList = productsList.stream().map(Products::getProductsId).collect(Collectors.toList());
        List<Products> subProductsList = productsRepository.findAllByParentIdInAndStatus(productsIdList, 1);

        List<ProductsVO> productsVOList = new ArrayList<>();
        ProductsVO productsVO;
        for (Products products : productsList) {

            productsVO = getProductsVO(products);
            List<Products> subProductVOList = new ArrayList<>();
            for (Products subProducts : subProductsList) {

                if (products.getProductsId().equals(subProducts.getParentId())) {
                    subProductVOList.add(subProducts);
                }
            }
            productsVO.setSubProductList(subProductVOList);
            productsVOList.add(productsVO);
        }


        return productsVOList;

    }

    @NotNull
    private ProductsVO getProductsVO(Products products) {
        ProductsVO productsVO = new ProductsVO();
        productsVO.setProductsId(products.getProductsId());
        productsVO.setName(products.getName());
        productsVO.setMoney(products.getMoney());
        productsVO.setImgUrl(products.getImgUrl());
        productsVO.setDetailImgUrl(products.getDetailImgUrl());
        productsVO.setNumber(products.getNumber());
        productsVO.setVip(products.getVip());
        return productsVO;
    }


    public void add(String name, Integer type, Integer category, Integer level, MultipartFile listPic, MultipartFile detailPic, String subProducts) {

        SnowflakeIdFactory factory = new SnowflakeIdFactory(2, 2);
        String listPicUrl = getFileUrl(listPic, factory);
        String detailPicUrl = getFileUrl(detailPic, factory);

        Products products = new Products();

        JSONArray array = JSONObject.parseArray(subProducts);
        for (int i = 0; i < array.size(); i++) {

            Products subProduct;

            JSONObject productJson = JSON.parseObject(array.getString(i));
            String productName = productJson.getString("name");
            String money = productJson.getString("money");
            String number = productJson.getString("number");
            String vipPrice = productJson.getString("vipPrice");
            String vipIntegral = productJson.getString("vipIntegral");
            JSONArray codeList = productJson.getJSONArray("codeList");

            if (i == 0) {
                products = setNewProducts(name, money, number, listPicUrl, detailPicUrl, vipPrice, vipIntegral, 0, type, category, level);
                products = productsRepository.saveAndFlush(products);
            }

            subProduct = setNewProducts(productName, money, number, listPicUrl, detailPicUrl, vipPrice, vipIntegral, products.getProductsId(), type, category, level);
            subProduct = productsRepository.saveAndFlush(subProduct);

        }
    }

    private String getFileUrl(MultipartFile file, SnowflakeIdFactory factory) {
        String path = "jx/boc/pointsmall/";
        String fileName = file.getOriginalFilename();
        path = path + factory.nextId() + fileName.substring(fileName.indexOf("."));
        try {
            return AliOssUtil.upload(path, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Products setNewProducts(String productName, String money, String number, String listPicUrl, String detailPicUrl, String vipPrice, String vipIntegral, Integer parentId, Integer type, Integer category, Integer level) {
        Products products = new Products();
        products.setName(productName);
        products.setMoney(new BigDecimal(money));
        products.setImgUrl(listPicUrl);
        products.setDetailImgUrl(detailPicUrl);
        products.setNumber(Integer.valueOf(number));
        products.setVip(level);
        products.setPrice(new BigDecimal(vipPrice));
        products.setIntegral(Integer.valueOf(vipIntegral));
        products.setVipPrice(new BigDecimal(vipPrice));
        products.setVipIntegral(Integer.valueOf(vipIntegral));
        products.setCategoryId(category);
        products.setTypeId(type);
        products.setParentId(parentId);
        products.setStatus(1);

        return products;
    }

    public ProductsVO getProductDetailsInfo(Integer productsId) {
        Optional<Products> productsOptional = productsRepository.findById(productsId);

        if (productsOptional.isPresent()) {
            Products products = productsOptional.get();
            List<Products> productsList = productsRepository.findAllByParentIdAndStatus(products.getProductsId(), 1);
            ProductsVO productsVO = BeanUtils.productToVO(products);
            assert productsVO != null;
            productsVO.setSubProductList(productsList);
            return productsVO;
        } else {
            return null;
        }
    }

    public void modifyProducts(Integer productsId, String name, Integer type, Integer category, Integer level, MultipartFile listPic, MultipartFile detailPic) {

        Optional<Products> productsOptional = productsRepository.findById(productsId);
        if (productsOptional.isPresent()) {
            SnowflakeIdFactory factory = new SnowflakeIdFactory(2, 2);
            Products products = productsOptional.get();
            if (listPic != null) {
                String listPicUrl = getFileUrl(listPic, factory);
                products.setImgUrl(listPicUrl);
            }
            if (detailPic != null) {
                String detailPicUrl = getFileUrl(detailPic, factory);
                products.setDetailImgUrl(detailPicUrl);
            }
            products.setName(name);
            products.setTypeId(type);
            products.setCategoryId(category);
            products.setVip(level);
            products = productsRepository.save(products);
        }

    }

    public void delProducts(Integer productsId) {
        Optional<Products> productsOptional = productsRepository.findById(productsId);
        if (productsOptional.isPresent()) {
            Products products = productsOptional.get();
            products.setStatus(2);
            productsRepository.save(products);
        }
    }

    public GoodsTypeInfoDTO getGoodsTypeInfo(Integer productsId) {
        Optional<Products> productsOptional = productsRepository.findById(productsId);
        if (productsOptional.isPresent()) {
            Products products = productsOptional.get();
            List<PrizeInfo> prizeInfoList = prizeInfoRepository.findAllByProductsIdAndStatusIsNot(productsId, 3);
            GoodsTypeInfoDTO typeInfoDTO = new GoodsTypeInfoDTO();
            typeInfoDTO.setProductsId(productsId);
            typeInfoDTO.setName(products.getName());
            typeInfoDTO.setMoney(products.getMoney());
            typeInfoDTO.setNumber(products.getNumber());
            typeInfoDTO.setPrice(products.getPrice());
            typeInfoDTO.setIntegral(products.getIntegral());
            typeInfoDTO.setPrizeInfoList(prizeInfoList);
            return typeInfoDTO;

        } else {
            return null;
        }
    }

    public void delCode(Integer prizeId) {
        Optional<PrizeInfo> prizeInfoOptional = prizeInfoRepository.findById(prizeId);
        if (prizeInfoOptional.isPresent()) {
            PrizeInfo prizeInfo = prizeInfoOptional.get();
            prizeInfo.setStatus(3);
            prizeInfoRepository.save(prizeInfo);
        }
    }

    public void addCode(Integer productsId, String code) {
        if (!StringUtils.hasText(code)) {
            return;
        }
        if (!code.contains("http")) {
            code = "https://" + code;
        }
        Optional<Products> productsOptional = productsRepository.findById(productsId);
        if (productsOptional.isPresent()) {
            Products products = productsOptional.get();
            PrizeInfo prizeInfo = new PrizeInfo();
            prizeInfo.setPrizeName(products.getName());
            prizeInfo.setStatus(1);
            prizeInfo.setCustomerId("");
            prizeInfo.setCreateDate(new Date());
            prizeInfo.setDistributionDate(new Date());
            prizeInfo.setCode(code);
            prizeInfo.setOrderId(0);
            prizeInfo.setProductsId(products.getProductsId());
            prizeInfo = prizeInfoRepository.save(prizeInfo);
        }


    }

    public void bathAddCode(Integer productsId, MultipartFile codeExcel) {
        Optional<Products> productsOptional = productsRepository.findById(productsId);
        if (productsOptional.isPresent()) {
            Products products = productsOptional.get();
            Optional<Products> baseProducts = productsRepository.findById(products.getParentId());
            String name = "";
            if (baseProducts.isPresent()) {
                name = baseProducts.get().getName() + " " + products.getName();
            }

            List<List<String>> result = new ArrayList<>();
            List<String> codeList = new ArrayList<>();
            try {
                ExcelUtil excelUtil = new ExcelUtil(result);
                excelUtil.readOneSheet(codeExcel.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (List<String> list : result) {
                codeList.addAll(list);
            }
            int cpuNum = Runtime.getRuntime().availableProcessors();
            int paging = cpuNum * 2 + 1;

            List<List<String>> lists = FileUtil.averageAssign(codeList, paging);

            AtomicInteger atomicInteger = new AtomicInteger();

            for (List<String> list : lists) {
                String finalName = name;
                Thread t = new Thread(() -> {
                    atomicInteger.incrementAndGet();
                    uploadCodeFile(list,productsId, finalName);
                });
                t.setName("ulFile-Thread-" + atomicInteger.get());
                t.start();
            }
        }
    }

    private void uploadCodeFile(List<String> codeList,Integer productsId,String name){
        PrizeInfo prizeInfo;
        for (String code : codeList) {
            prizeInfo = new PrizeInfo();
            prizeInfo.setPrizeName(name);
            prizeInfo.setStatus(1);
            prizeInfo.setCustomerId("");
            prizeInfo.setCreateDate(new Date());
            prizeInfo.setDistributionDate(new Date());
            prizeInfo.setCode(code);
            prizeInfo.setOrderId(0);
            prizeInfo.setProductsId(productsId);
            prizeInfoRepository.save(prizeInfo);
        }
    }

    public void editType(Integer productsId, String name, Integer money, Integer number, String price, Integer integral) {
        Optional<Products> productsOptional = productsRepository.findById(productsId);
        if (productsOptional.isPresent()) {
            Products products = productsOptional.get();
            products.setName(name);
            products.setMoney(new BigDecimal(money));
            products.setNumber(number);
            products.setPrice(new BigDecimal(price));
            products.setIntegral(integral);
            productsRepository.save(products);
        }


    }
}
