package com.zykj.pointsmall.service;

import com.alibaba.fastjson.JSONObject;
import com.zykj.pointsmall.common.interceptor.TokenManager;
import com.zykj.pointsmall.common.interceptor.TokenModel;
import com.zykj.pointsmall.dao.*;
import com.zykj.pointsmall.dto.AdminDTO;
import com.zykj.pointsmall.dto.BranchOutletsDTO;
import com.zykj.pointsmall.dto.OutletsRankingDTO;
import com.zykj.pointsmall.pojo.*;
import com.zykj.pointsmall.vo.ActivityDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 管理员
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-02-04
 */
@Slf4j
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final TokenManager tokenManager;
    private final MenuPowerRepository menuPowerRepository;
    private final MenuRepository menuRepository;
    private final DataAdminRepository dataAdminRepository;
    private final ActivityInfoRepository activityInfoRepository;
    private final StaffRepository staffRepository;
    private final BranchRepository branchRepository;
    private final OutletsRepository outletsRepository;

    public AdminService(AdminRepository adminRepository, TokenManager tokenManager, MenuPowerRepository menuPowerRepository, MenuRepository menuRepository, DataAdminRepository dataAdminRepository, ActivityInfoRepository activityInfoRepository, StaffRepository staffRepository, BranchRepository branchRepository, OutletsRepository outletsRepository) {
        this.adminRepository = adminRepository;
        this.tokenManager = tokenManager;
        this.menuPowerRepository = menuPowerRepository;
        this.menuRepository = menuRepository;
        this.dataAdminRepository = dataAdminRepository;
        this.activityInfoRepository = activityInfoRepository;
        this.staffRepository = staffRepository;
        this.branchRepository = branchRepository;
        this.outletsRepository = outletsRepository;
    }

    public AdminDTO login(AdminDTO adminDTO) {

        if (!StringUtils.hasText(adminDTO.getAdminName()) && !StringUtils.hasText(adminDTO.getAdminPwd())) {
            return new AdminDTO();
        }

        Admin admin = adminRepository.findByAdminNameAndAdminPwd(adminDTO.getAdminName(), adminDTO.getAdminPwd());

        if (admin == null) {
            return new AdminDTO();
        }

        TokenModel token = tokenManager.createToken(String.valueOf(admin.getAdminId()));

        //设置菜单
        List<MenuPower> menuPowerList = menuPowerRepository.findAllByAdminId(admin.getAdminId());

        List<Integer> menuIdList = menuPowerList.stream().map(MenuPower::getMenuId).collect(Collectors.toList());

        List<Menu> menuList = menuRepository.findAllByMenuIdIn(menuIdList);

        List<JSONObject> menuVOList = new ArrayList<>();
        JSONObject menuVO = new JSONObject();
        JSONObject subMenuVO = new JSONObject();
        List<JSONObject> menuVOS;
        for (Menu menu : menuList) {
            if (menu.getParentId().equals(0)) {
                menuVO = new JSONObject();
                menuVO.put("name", menu.getName());
                menuVO.put("icon", menu.getIcon());
                menuVO.put("url", menu.getUrl());
                menuVO.put("hidden", menu.getHidden());
                menuVOS = new ArrayList<>();
                for (Menu sub : menuList) {
                    if (menu.getMenuId().equals(sub.getParentId())) {
                        subMenuVO = new JSONObject();
                        subMenuVO.put("name", sub.getName());
                        subMenuVO.put("icon", sub.getIcon());
                        subMenuVO.put("url", sub.getUrl());
                        subMenuVO.put("hidden", sub.getHidden());
                        menuVOS.add(subMenuVO);
                    }
                }
                menuVO.put("list", menuVOS);
                menuVOList.add(menuVO);
            }
        }

        adminDTO = new AdminDTO();
        adminDTO.setToken(token.getToken());
        adminDTO.setAdminName(admin.getAdminName());
        adminDTO.setMenuJson(menuVOList.toString());
        System.out.println(menuVOList.toString());
        return adminDTO;
    }

    public AdminDTO dataAdminlogin(AdminDTO adminDTO) {
        if (!StringUtils.hasText(adminDTO.getAdminName()) && !StringUtils.hasText(adminDTO.getAdminPwd())) {
            return new AdminDTO();
        }
        Optional<DataAdmin> dataAdminOptional = dataAdminRepository.findByLoginNoAndPwd(adminDTO.getAdminName(), adminDTO.getAdminPwd());
        if (dataAdminOptional.isPresent()) {
            DataAdmin dataAdmin = dataAdminOptional.get();
            TokenModel token = tokenManager.createToken(String.valueOf(dataAdmin.getDataAdminId()));
            adminDTO = new AdminDTO();
            adminDTO.setAdminName(dataAdmin.getName());
            adminDTO.setToken(token.getToken());
            adminDTO.setAdminId(dataAdmin.getDataAdminId());
            return adminDTO;
        }
        return new AdminDTO();

    }

    public Object[] getData(Integer page, Integer limit, String name, String branchName, String outletsName, Integer canyu, Integer month) {
        page--;

        Pageable pageable = PageRequest.of(page, limit, Sort.Direction.DESC, "dateTime");
        Page<ActivityInfo> activityInfoPage = activityInfoRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtils.hasText(name)) {
                log.info("搜索名称:{}",name);
                list.add(criteriaBuilder.like(root.get("activityName").as(String.class), "%" + name + "%"));
            }
            if (StringUtils.hasText(branchName) && !"全部".equals(branchName)) {
                log.info("搜索分行:{}",branchName);
                list.add(criteriaBuilder.equal(root.get("branchName").as(String.class), branchName));
            }
            if (StringUtils.hasText(outletsName) && !"全部".equals(outletsName)) {
                log.info("搜索网点:{}",outletsName);
                list.add(criteriaBuilder.equal(root.get("outletsName").as(String.class), outletsName));
            }

            if (canyu != null && canyu != 0) {
                if (canyu == 1 && month != 0) {
                    log.info("搜索参与月份:{}",month);
                    list.add(criteriaBuilder.equal(root.get("month").as(Integer.class), month));
                }
                if (canyu == 2 && month != 0) {
                    log.info("搜索未参与月份：{}",month);
                    list.add(criteriaBuilder.notEqual(root.get("month").as(Integer.class), month));
                }
            }

            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);

        List<ActivityInfo> all = activityInfoPage.getContent();
        List<ActivityDataVO> arrayList = new ArrayList<>();

        for (ActivityInfo activityInfo : all) {
            boolean non = true;
            for (ActivityDataVO activityDataVO : arrayList) {
                if (activityInfo.getCustomerId().equals(activityDataVO.getCustomerId()) || activityInfo.getPhone().equals(activityDataVO.getPhone())) {
                    non = false;
                    activityDataVO.setActivityName(activityDataVO.getActivityName().contains(activityInfo.getActivityName()) ? activityDataVO.getActivityName() : activityDataVO.getActivityName() + "," + activityInfo.getActivityName());
                    activityDataVO.setMonth(activityDataVO.getMonth().contains(String.valueOf(activityInfo.getMonth())) ? activityDataVO.getMonth() : activityDataVO.getMonth() + "," + activityInfo.getMonth());
                    break;
                }
            }
            if (non) {
                ActivityDataVO dataVO = new ActivityDataVO();
                dataVO.setActivityName(activityInfo.getActivityName());
                dataVO.setCustomerId(activityInfo.getCustomerId());
                dataVO.setStaff(activityInfo.getStaff());
                dataVO.setPhone(activityInfo.getPhone());
                dataVO.setBranchNo(activityInfo.getBranchName());
                dataVO.setOutletsNo(activityInfo.getOutletsName());
                dataVO.setMonth(String.valueOf(activityInfo.getMonth()));
                arrayList.add(dataVO);
            }
        }

        return new Object[]{arrayList,activityInfoPage.getTotalElements()};
    }

    public List<BranchOutletsDTO> getBranchInfo(Integer dataAdminId) {
        Optional<DataAdmin> dataAdminOptional = dataAdminRepository.findById(dataAdminId);

        List<Branch> branchList = new ArrayList<>();
        List<Outlets> outletsList = new ArrayList<>();

        if (dataAdminOptional.isPresent()) {
            DataAdmin dataAdmin = dataAdminOptional.get();
            if (dataAdmin.getType().equals(0)) {
                List<Branch> branchAll = branchRepository.findAll();
                List<Outlets> outletsAll = outletsRepository.findAll();

                Branch branch = new Branch();
                branch.setBranchId(0);
                branch.setBranchName("全部");
                branch.setBranchNo("全部");
                branchAll.add(branch);

                Outlets outlets = new Outlets();
                outlets.setOutletsId(0);
                outlets.setOutletsName("全部");
                outlets.setOutletsNo("全部");
                outlets.setBranchId(0);
                outletsAll.add(outlets);

                branchList.addAll(branchAll);
                outletsList.addAll(outletsAll);
            }
            if (dataAdmin.getType().equals(1)) {
                Optional<Branch> branchOptional = branchRepository.findByBranchNo(dataAdmin.getLoginNo());
                if (branchOptional.isPresent()) {
                    Branch branch = branchOptional.get();
                    List<Outlets> outletsAll = outletsRepository.findAllByBranchId(branch.getBranchId());

                    branchList.add(branch);
                    outletsList.addAll(outletsAll);
                }
            }
            if (dataAdmin.getType().equals(2)) {
                List<Outlets> outletsAll = outletsRepository.findAllByOutletsNo(dataAdmin.getLoginNo());
                if (outletsAll.size() >= 1) {
                    Optional<Branch> branchOptional = branchRepository.findById(outletsAll.get(0).getBranchId());
                    if (branchOptional.isPresent()) {
                        Branch branch = branchOptional.get();

                        branchList.add(branch);
                        outletsList.addAll(outletsAll);
                    }
                }
            }

            List<BranchOutletsDTO> branchOutletsList = new ArrayList<>();
            List<OutletsRankingDTO> outletsRankingList;
            BranchOutletsDTO branchOutletsDTO;
            OutletsRankingDTO outletsRankingDTO;
            for (Branch branch : branchList) {
                branchOutletsDTO = new BranchOutletsDTO();
                branchOutletsDTO.setBranchName(branch.getBranchName());
                branchOutletsDTO.setBranchNo(branch.getBranchNo());
                outletsRankingList = new ArrayList<>();
                for (Outlets outlets : outletsList) {
                    if (branch.getBranchId().equals(outlets.getBranchId())){
                        outletsRankingDTO = new OutletsRankingDTO();
                        outletsRankingDTO.setOutletsNo(outlets.getOutletsNo());
                        outletsRankingDTO.setOutletsName(outlets.getOutletsName());
                        outletsRankingDTO.setOutletsNumber(0);
                        outletsRankingList.add(outletsRankingDTO);
                    }
                }
                branchOutletsDTO.setOutletsList(outletsRankingList);
                branchOutletsList.add(branchOutletsDTO);
            }

            return branchOutletsList;
        }

        return new ArrayList<>();
    }

    public AdminDTO dataAdminUpdatePwd(AdminDTO adminDTO,Integer adminId) {
        String adminName = adminDTO.getAdminName();
        String adminPwd = adminDTO.getAdminPwd();

        Optional<DataAdmin> adminOptional = dataAdminRepository.findById(adminId);
        adminDTO = new AdminDTO();

        if (adminOptional.isPresent()) {
            DataAdmin dataAdmin = adminOptional.get();
            if(StringUtils.hasText(adminPwd)){
                dataAdmin.setPwd(adminPwd);
            }
            DataAdmin save = dataAdminRepository.save(dataAdmin);
            adminDTO.setAdminName(dataAdmin.getName());
        }


        return adminDTO;
    }
}
