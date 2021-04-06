package com.zykj.pointsmall.service;

import com.alibaba.fastjson.JSONObject;
import com.zykj.pointsmall.common.DateUtil;
import com.zykj.pointsmall.common.ExcelUtil;
import com.zykj.pointsmall.common.FileUtil;
import com.zykj.pointsmall.dao.*;
import com.zykj.pointsmall.dto.PrProductsDTO;
import com.zykj.pointsmall.dto.PrivilegeDTO;
import com.zykj.pointsmall.dto.TransferDTO;
import com.zykj.pointsmall.pojo.Privilege;
import com.zykj.pointsmall.pojo.PrivilegeUser;
import com.zykj.pointsmall.pojo.Products;
import com.zykj.pointsmall.pojo.ProductsPrivilege;
import com.zykj.pointsmall.vo.PrivilegeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 权限
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-11
 */
@Slf4j
@Service
public class PrivilegeService {

    private final ProductsRepository productsRepository;
    private final BocUserInfoRepository bocUserInfoRepository;
    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeUserRepository privilegeUserRepository;
    private final ProductsPrivilegeRepository productsPrivilegeRepository;

    @Autowired
    public PrivilegeService(ProductsRepository productsRepository, BocUserInfoRepository bocUserInfoRepository, PrivilegeRepository privilegeRepository, PrivilegeUserRepository privilegeUserRepository, ProductsPrivilegeRepository productsPrivilegeRepository) {
        this.productsRepository = productsRepository;
        this.bocUserInfoRepository = bocUserInfoRepository;
        this.privilegeRepository = privilegeRepository;
        this.privilegeUserRepository = privilegeUserRepository;
        this.productsPrivilegeRepository = productsPrivilegeRepository;
    }

    public Products getUserProductRights(String token, Products products,Integer privilegeId) {

        Optional<ProductsPrivilege> optional = productsPrivilegeRepository.findByProductsIdAndPrivilegeId(products.getProductsId(), privilegeId);

        if (optional.isPresent()) {
            ProductsPrivilege productsPrivilege = optional.get();
            products.setVipIntegral(productsPrivilege.getVipIntegral());
            products.setVipPrice(productsPrivilege.getVipPrice());
        }

        return products;
    }

    public Page<PrivilegeVO> privilegePage(int page, String name) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "privilegeId");
        Page<Privilege> privilegePage = privilegeRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + name + "%"));
            }

            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);

        List<Privilege> privilegeList = privilegePage.getContent();
        List<Integer> privilegeIdList = privilegeList.stream().map(Privilege::getPrivilegeId).collect(Collectors.toList());
        List<ProductsPrivilege> productsPrivilegeList = productsPrivilegeRepository.findAllByPrivilegeIdIn(privilegeIdList);
        List<Integer> productsIdList = productsPrivilegeList.stream().map(ProductsPrivilege::getProductsId).collect(Collectors.toList());
        List<Products> productsList = productsRepository.findAllByProductsIdIn(productsIdList);

        PrivilegeVO productsVO;
        List<PrivilegeVO> productsVOList = new ArrayList<>();
        List<Products> products;

        for (Privilege privilege : privilegeList) {
            productsVO = new PrivilegeVO();
            productsVO.setPrivilegeId(privilege.getPrivilegeId());
            productsVO.setName(privilege.getName());
            productsVO.setCreateDate(privilege.getCreateDate());
            productsVO.setStartDate(privilege.getStartDate());
            productsVO.setClosingDate(privilege.getClosingDate());

            products = new ArrayList<>();
            for (ProductsPrivilege productsPrivilege : productsPrivilegeList) {
                if (privilege.getPrivilegeId().equals(productsPrivilege.getPrivilegeId())) {
                    for (Products product : productsList) {
                        if (productsPrivilege.getProductsId().equals(product.getProductsId())) {
                            product.setNumber(productsPrivilege.getNumber());
                            products.add(product);
                        }
                    }
                }
            }
            productsVO.setProductsList(products);
            productsVOList.add(productsVO);
        }

        Page<PrivilegeVO> productsVOPage = new PageImpl<>(productsVOList, pageable, privilegePage.getTotalElements());

        return productsVOPage;

    }

    public JSONObject getPrivilegeInfo(Integer privilegeId) {

        JSONObject json = new JSONObject();

        if (privilegeId != null) {
            Optional<Privilege> privilegeOptional = privilegeRepository.findById(privilegeId);
            if (privilegeOptional.isPresent()) {
                Privilege privilege = privilegeOptional.get();
                json.put("privilege", 1);
                json.put("privilegeId", privilege.getPrivilegeId());
                json.put("privilegeName", privilege.getName());
                json.put("closingDate", DateUtil.getFormat(privilege.getClosingDate(), "yyyy-MM-dd HH:mm:ss"));
                json.put("startDate", DateUtil.getFormat(privilege.getStartDate(), "yyyy-MM-dd HH:mm:ss"));

                //权限商品
                List<ProductsPrivilege> productsPrivilegeList = productsPrivilegeRepository.findAllByPrivilegeId(privilege.getPrivilegeId());
                List<Products> productsList = productsRepository.findAllByProductsIdIn(productsPrivilegeList.stream().map(ProductsPrivilege::getProductsId).collect(Collectors.toList()));
                List<Products> parentProductsList = productsRepository.findAllByProductsIdIn(productsPrivilegeList.stream().map(ProductsPrivilege::getParentId).collect(Collectors.toList()));
                List<PrProductsDTO> productsDTOList = new ArrayList<>();

                for (Products products : productsList) {
                    PrProductsDTO prProductsDTO = new PrProductsDTO();
                    prProductsDTO.setParentProductId(products.getParentId());
                    prProductsDTO.setSubProductId(products.getProductsId());

                    prProductsDTO.setName(products.getName());
                    prProductsDTO.setMoney(products.getMoney().toString());
                    prProductsDTO.setNumber(products.getNumber());

                    for (Products products1 : parentProductsList) {
                        if (products.getParentId().equals(products1.getProductsId())) {
                            prProductsDTO.setParentName(products1.getName());
                            break;
                        }
                    }

                    for (ProductsPrivilege productsPrivilege : productsPrivilegeList) {
                        if (products.getProductsId().equals(productsPrivilege.getProductsId())) {
                            prProductsDTO.setNumber(productsPrivilege.getNumber());
                            prProductsDTO.setVipPrice(productsPrivilege.getVipPrice().toString());
                            prProductsDTO.setVipIntegral(productsPrivilege.getVipIntegral().toString());
                            break;
                        }
                    }
                    productsDTOList.add(prProductsDTO);
                }

                json.put("priProducts", productsDTOList);

                //权限用户
                List<PrivilegeUser> privilegeUserList = privilegeUserRepository.findAllByPrivilegeIdAndStatus(privilege.getPrivilegeId(), 1);
                json.put("userTable", privilegeUserList);

            } else {
                json.put("privilege", 2);
            }
        } else {
            json.put("privilege", 3);
        }

        return json;
    }

    public void setPrivilegeInfo(TransferDTO transferDTO) {

        Integer privilegeId = transferDTO.getPrivilegeId();
        String name = transferDTO.getName();
        List<Map<String, String>> userList = transferDTO.getUser();
        List<Map<String, String>> goodsList = transferDTO.getGoods();

        if (!StringUtils.hasText(name) || userList == null || goodsList == null) {
            return;
        }
        Privilege privilege;
        if (privilegeId == 0) {
            privilege = new Privilege();
            privilege.setCreateDate(new Date());
            privilege.setPrivilegeDesc("");
            privilege.setState(1);
            if (StringUtils.hasText(name)) {
                privilege.setName(name);
            }
            privilege = privilegeRepository.save(privilege);
        } else {
            Optional<Privilege> privilegeOptional = privilegeRepository.findById(privilegeId);
            if (privilegeOptional.isPresent()) {
                privilege = privilegeOptional.get();
            } else {
                return;
            }
        }

        //特权用户
        List<PrivilegeUser> privilegeUserList = privilegeUserRepository.findAllByPrivilegeId(privilege.getPrivilegeId());

        for (PrivilegeUser privilegeUser : privilegeUserList) {
            privilegeUser.setStatus(2);
        }
        privilegeUserRepository.saveAll(privilegeUserList);

        List<PrivilegeUser> saveUserList = new ArrayList<>();
        PrivilegeUser pUser;
        for (Map<String, String> map : userList) {
            String customerId = map.get("value");
            boolean exist = false;

            for (PrivilegeUser privilegeUser : privilegeUserList) {
                if (customerId.equals(privilegeUser.getCustomerId())) {
                    privilegeUser.setStatus(1);
                    exist = true;
                    saveUserList.add(privilegeUser);
                }
            }

            if (!exist) {
                pUser = new PrivilegeUser();
                pUser.setPrivilegeId(privilege.getPrivilegeId());
                pUser.setCustomerId(customerId);
                pUser.setStatus(1);
                saveUserList.add(pUser);
            }
        }
        privilegeUserRepository.saveAll(saveUserList);


        //特权商品
        List<ProductsPrivilege> productsPrivilegeList = productsPrivilegeRepository.findAllByPrivilegeId(privilege.getPrivilegeId());
        if (productsPrivilegeList.size() > 0) {
            for (ProductsPrivilege productsPrivilege : productsPrivilegeList) {
                productsPrivilege.setStatus(2);
            }
            productsPrivilegeRepository.saveAll(productsPrivilegeList);
        }
        List<Integer> productsIdList = goodsList.stream().map(map -> Integer.valueOf(map.get("value"))).collect(Collectors.toList());
        List<Products> productsList = productsRepository.findAllByProductsIdIn(productsIdList);
        ProductsPrivilege productsPrivilege;
        List<ProductsPrivilege> productsPrivileges = new ArrayList<>();

        for (Map<String, String> map : goodsList) {
            Integer productsId = Integer.valueOf(map.get("value"));
            for (Products products : productsList) {
                if (productsId.equals(products.getProductsId())) {
                    productsPrivilege = new ProductsPrivilege();
                    productsPrivilege.setParentId(products.getParentId());
                    productsPrivilege.setProductsId(productsId);
                    productsPrivilege.setPrivilegeId(privilege.getPrivilegeId());
                    productsPrivilege.setVipPrice(products.getVipPrice());
                    productsPrivilege.setVipIntegral(products.getVipIntegral());
                    productsPrivilege.setType(1);
                    productsPrivilege.setBuyFeq(1);
                    productsPrivilege.setStatus(1);
                    productsPrivileges.add(productsPrivilege);
                }
            }
        }
        productsPrivilegeRepository.saveAll(productsPrivileges);
    }


    @Transactional
    public Privilege addPrivilege(PrivilegeDTO privilegeDTO) {
        Integer privilegeId = privilegeDTO.getPrivilegeId();
        String privilegeName = privilegeDTO.getPrivilegeName();
        Privilege privilege = null;
        if (privilegeId == 0) {
            privilege = new Privilege();
            privilege.setName(privilegeName);
            privilege.setPrivilegeDesc(privilegeName);
            privilege.setState(1);
            privilege.setCreateDate(new Date());
        } else {
            privilege = privilegeRepository.findByPrivilegeId(privilegeId);
            privilege.setName(privilegeName);
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        Date closingDate = null;
        try {
            startDate = df.parse(privilegeDTO.getStartDate());
            closingDate = df.parse(privilegeDTO.getClosingDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (closingDate != null) {
            privilege.setClosingDate(closingDate);
        }
        if (startDate != null) {
            privilege.setStartDate(startDate);
        }
        privilege = privilegeRepository.save(privilege);
        privilegeDTO.setPrivilegeId(privilege.getPrivilegeId());
        privilegeDTO.setPrivilegeName(privilege.getName());

        updatePrivilege(privilegeDTO);

        return privilege;

    }

    public void updatePrivilege(PrivilegeDTO privilegeDTO) {

        List<ProductsPrivilege> ppList = productsPrivilegeRepository.findAllByPrivilegeId(privilegeDTO.getPrivilegeId());
        productsPrivilegeRepository.deleteAll(ppList);

        List<PrProductsDTO> priProducts = privilegeDTO.getPriProducts();

        for (PrProductsDTO priProduct : priProducts) {
            ProductsPrivilege productsPrivilege = new ProductsPrivilege();
            productsPrivilege.setParentId(priProduct.getParentProductId());
            productsPrivilege.setProductsId(priProduct.getSubProductId());
            productsPrivilege.setPrivilegeId(privilegeDTO.getPrivilegeId());
            productsPrivilege.setVipPrice(new BigDecimal(priProduct.getVipPrice()));
            productsPrivilege.setVipIntegral(Integer.valueOf(priProduct.getVipIntegral()));
            productsPrivilege.setType(1);
            productsPrivilege.setNumber(priProduct.getNumber());
            productsPrivilege.setBuyFeq(1);
            productsPrivilege.setStatus(1);
            productsPrivilegeRepository.saveAndFlush(productsPrivilege);
        }


    }

    public void addPrivilegeFile(Integer privilegeId, MultipartFile userFile) throws IOException {

        List<String> userList = new ArrayList<>();

        try {
            List<List<String>> result = new ArrayList<>();
            ExcelUtil excel = new ExcelUtil(result);
            excel.readOneSheet(userFile.getInputStream());

            for (List<String> list : result) {
                userList.addAll(list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        int cpuNum = Runtime.getRuntime().availableProcessors();
        int paging = cpuNum * 2 + 1;

        List<List<String>> lists = FileUtil.averageAssign(userList, paging);

        AtomicInteger atomicInteger = new AtomicInteger();

        for (List<String> list : lists) {
            Thread t = new Thread(() -> {
                atomicInteger.incrementAndGet();
                uploadUserFile(privilegeId, list);
//                cdl.countDown();
            });
            t.setName("ulFile-Thread-" + atomicInteger.get());
            t.start();
        }

    }

    private void uploadUserFile(Integer privilegeId, List<String> list) {
        PrivilegeUser user = null;
        List<PrivilegeUser> userList = new ArrayList<>();
        for (String customerId : list) {
            user = new PrivilegeUser();
            user.setPrivilegeId(privilegeId);
            user.setCustomerId(customerId);
            user.setStatus(1);
            userList.add(user);
            privilegeUserRepository.save(user);
        }

        log.info("上传完成");

    }

    public Optional<ProductsPrivilege> getPrivilegeProducts(Integer privilegeId, Integer productId) {

        return productsPrivilegeRepository.findByProductsIdAndPrivilegeId(productId, privilegeId);

    }

    public void saveProductsPrivilege(ProductsPrivilege productsPrivilege) {
        productsPrivilegeRepository.saveAndFlush(productsPrivilege);
    }
}
