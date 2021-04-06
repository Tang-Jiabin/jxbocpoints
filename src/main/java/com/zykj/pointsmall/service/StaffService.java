package com.zykj.pointsmall.service;

import com.zykj.pointsmall.dao.BranchRepository;
import com.zykj.pointsmall.dao.OutletsRepository;
import com.zykj.pointsmall.dao.StaffActivityInfoRepository;
import com.zykj.pointsmall.dao.StaffRepository;
import com.zykj.pointsmall.dto.*;
import com.zykj.pointsmall.pojo.Branch;
import com.zykj.pointsmall.pojo.Outlets;
import com.zykj.pointsmall.pojo.Staff;
import com.zykj.pointsmall.pojo.StaffActivityInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 员工
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-18
 */
@Slf4j
@Service
public class StaffService {

    private final StaffActivityInfoRepository staffActivityInfoRepository;
    private final StaffRepository staffRepository;
    private final OutletsRepository outletsRepository;
    private final BranchRepository branchRepository;

    @Autowired
    public StaffService(StaffActivityInfoRepository staffActivityInfoRepository, StaffRepository staffRepository, OutletsRepository outletsRepository, BranchRepository branchRepository) {
        this.staffActivityInfoRepository = staffActivityInfoRepository;
        this.staffRepository = staffRepository;
        this.outletsRepository = outletsRepository;
        this.branchRepository = branchRepository;
    }

    public void addActivityInfo(StaffInfoDto staffInfoDto) {

        StaffActivityInfo staffActivityInfo = staffActivityInfoRepository.findByPhone(staffInfoDto.getPhone());

        if (staffActivityInfo == null) {
            staffActivityInfo = new StaffActivityInfo();
            staffActivityInfo.setCreateDate(new Date());
        }
        BeanUtils.copyProperties(staffInfoDto, staffActivityInfo);
        staffActivityInfo.setUpdateDate(new Date());
        staffActivityInfoRepository.save(staffActivityInfo);
    }

    public List<StaffActivityInfo> getInputDetails(String token) {
        return staffActivityInfoRepository.findAllByCustomerId(token);
    }



    public StaffDTO getStaffDetailsInfo(String token) {

        Staff staff = staffRepository.findByCustomerId(token);

        if (staff == null) {
            return null;
        }

        Optional<Branch> branchOptional = branchRepository.findById(staff.getBranchId());

        Optional<Outlets> outletsOptional = outletsRepository.findById(staff.getOutletsId());

        StaffDTO staffDTO = new StaffDTO();
        if (branchOptional.isPresent()) {
            staffDTO.setBranchName(branchOptional.get().getBranchName());
            staffDTO.setBranchNo(branchOptional.get().getBranchNo());
        }
        if (outletsOptional.isPresent()) {
            staffDTO.setOutletsName(outletsOptional.get().getOutletsName());
            staffDTO.setOutletsNo(outletsOptional.get().getOutletsNo());
            RankingDTO outletsRankings = getAllRankings(outletsOptional.get().getOutletsName(), token);
            staffDTO.setOutletsRanking(outletsRankings.getMyRanking());
        }

        staffDTO.setStaffId(staff.getStaffId());
        staffDTO.setCustomerId(staff.getCustomerId());
        staffDTO.setStaffName(staff.getStaffName());
        staffDTO.setStaffNo(staff.getStaffNo());
        staffDTO.setPhone(staff.getPhone());
        staffDTO.setBranchId(staff.getBranchId());
        staffDTO.setOutletsId(staff.getOutletsId());
        staffDTO.setBindingDate(staff.getBindingDate());
        RankingDTO rankingDTO = getAllRankings("", token);
        staffDTO.setAllRanking(rankingDTO.getMyRanking());
        return staffDTO;
    }


    public RankingDTO getAllRankings(String outletsName, String token) {
        List<Staff> staffList = new ArrayList<>();
        List<StaffActivityInfo> activityInfoList = new ArrayList<>();
        List<Branch> branchList = new ArrayList<>();
        List<Outlets> outletsList = new ArrayList<>();

        if (StringUtils.hasText(outletsName)) {
            outletsList = outletsRepository.findAllByOutletsName(outletsName);

            List<Integer> branchIdList = outletsList.stream().map(Outlets::getBranchId).collect(Collectors.toList());

            branchList = branchRepository.findAllByBranchIdIn(branchIdList);

            staffList = staffRepository.findAllByBranchIdInAndCustomerIdIsNotNull(branchIdList);

            List<String> customerIdList = staffList.stream().map(Staff::getCustomerId).collect(Collectors.toList());

            activityInfoList = staffActivityInfoRepository.findAllByCustomerIdIn(customerIdList);

        } else {
            staffList = staffRepository.findAllByCustomerIdIsNotNull();

            activityInfoList = staffActivityInfoRepository.findAll();

            branchList = branchRepository.findAll();

            outletsList = outletsRepository.findAll();
        }

        EmployeeRankingDTO employeeRanking = new EmployeeRankingDTO();
        EmployeeRankingDTO self = new EmployeeRankingDTO();

        RankingDTO rankingDTO = new RankingDTO();
        List<EmployeeRankingDTO> employeeRankingDTOList = new ArrayList<>();

        for (Staff staff : staffList) {
            int invite = 0;
            employeeRanking = new EmployeeRankingDTO();
            employeeRanking.setIsMe(0);
            employeeRanking.setName(staff.getStaffName());

            if (staff.getCustomerId().equals(token)) {
                employeeRanking.setIsMe(1);
            }
            for (Branch branch : branchList) {
                if (branch.getBranchId().equals(staff.getBranchId())) {
                    employeeRanking.setBranchName(branch.getBranchName());
                    break;
                }
            }

            for (Outlets outlets : outletsList) {
                if (staff.getOutletsId().equals(outlets.getOutletsId())) {
                    employeeRanking.setOutletsName(outlets.getOutletsName());
                    break;
                }
            }

            for (StaffActivityInfo staffActivityInfo : activityInfoList) {
                if (staff.getCustomerId().equals(staffActivityInfo.getCustomerId())) {
                    invite = invite + 1;
                }
            }
            employeeRanking.setNumber(invite);
            employeeRankingDTOList.add(employeeRanking);
            if (employeeRanking.getIsMe() == 1) {

                self = employeeRanking;

                rankingDTO.setName(employeeRanking.getName());
                rankingDTO.setBranchName(employeeRanking.getBranchName());
                rankingDTO.setOutletsName(employeeRanking.getOutletsName());
                rankingDTO.setNumber(employeeRanking.getNumber());
            }
        }

        employeeRankingDTOList = employeeRankingDTOList.stream().sorted(Comparator.comparing(EmployeeRankingDTO::getNumber).reversed()).collect(Collectors.toList());

        rankingDTO.setEmployeeRankingDTOList(employeeRankingDTOList);

        if (employeeRankingDTOList.contains(self)) {
            int index = employeeRankingDTOList.indexOf(self);
            rankingDTO.setMyRanking(index + 1);
        }

        return rankingDTO;
    }

    public List<OutletsRankingDTO> getNetworkRanking(String branchName, String token) {

        Branch branch = branchRepository.findByBranchName(branchName);
        if (branch == null) {
            return null;
        }
        List<Outlets> outletsList = outletsRepository.findAllByBranchId(branch.getBranchId());

        List<Staff> staffList = staffRepository.findAllByBranchIdAndCustomerIdIsNotNull(branch.getBranchId());

        List<String> staffCustomerIdList = staffList.stream().map(Staff::getCustomerId).collect(Collectors.toList());

        List<StaffActivityInfo> activityInfoList = staffActivityInfoRepository.findAllByCustomerIdIn(staffCustomerIdList);

        List<OutletsRankingDTO> outletsRankingDTOList = new ArrayList<>();
        OutletsRankingDTO outletsRankingDTO = new OutletsRankingDTO();


        for (Outlets outlets : outletsList) {
            outletsRankingDTO = new OutletsRankingDTO();
            outletsRankingDTO.setOutletsNo(outlets.getOutletsNo());
            outletsRankingDTO.setOutletsName(outlets.getOutletsName());

            int invite = 0;
            for (Staff staff : staffList) {
                if (outlets.getOutletsId().equals(staff.getOutletsId())) {
                    for (StaffActivityInfo staffActivityInfo : activityInfoList) {
                        if (staff.getCustomerId().equals(staffActivityInfo.getCustomerId())) {
                            invite = invite + 1;
                        }
                    }
                }
            }
            outletsRankingDTO.setOutletsNumber(invite);
            outletsRankingDTOList.add(outletsRankingDTO);
        }

        outletsRankingDTOList = outletsRankingDTOList.stream().sorted(Comparator.comparing(OutletsRankingDTO::getOutletsNumber).reversed()).collect(Collectors.toList());
        return outletsRankingDTOList;

    }

    public List<BranchRankingDTO> getBranchRanking(String token) {

        List<Staff> staffList = staffRepository.findAllByCustomerIdIsNotNull();

        List<StaffActivityInfo> activityInfoList = staffActivityInfoRepository.findAll();

        List<Branch> branchList = branchRepository.findAll();
        List<BranchRankingDTO> branchRankingDTOList = new ArrayList<>();
        BranchRankingDTO branchRankingDTO = new BranchRankingDTO();


        for (Branch branch : branchList) {
            branchRankingDTO = new BranchRankingDTO();
            branchRankingDTO.setBranchNo(branch.getBranchNo());
            branchRankingDTO.setBranchName(branch.getBranchName());

            int invite = 0;
            for (Staff staff : staffList) {
                if (branch.getBranchId().equals(staff.getBranchId())) {
                    for (StaffActivityInfo staffActivityInfo : activityInfoList) {
                        if (staff.getCustomerId().equals(staffActivityInfo.getCustomerId())) {
                            invite = invite + 1;
                        }
                    }
                }
            }
            branchRankingDTO.setBranchNumber(invite);
            branchRankingDTOList.add(branchRankingDTO);
        }
        branchRankingDTOList = branchRankingDTOList.stream().sorted(Comparator.comparing(BranchRankingDTO::getBranchNumber).reversed()).collect(Collectors.toList());
        return branchRankingDTOList;
    }

    public StaffDTO getStaffInfo(String token) {
        Staff staff = staffRepository.findByCustomerId(token);

        if (staff == null) {
            return null;
        }
        StaffDTO staffDTO = new StaffDTO();
        BeanUtils.copyProperties(staff,staffDTO);

        return staffDTO;

    }
}
