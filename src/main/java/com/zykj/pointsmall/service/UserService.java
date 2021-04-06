package com.zykj.pointsmall.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zykj.pointsmall.common.*;
import com.zykj.pointsmall.common.sms.CCPRestSDK;
import com.zykj.pointsmall.dao.*;
import com.zykj.pointsmall.dto.UserDTO;
import com.zykj.pointsmall.pojo.*;
import com.zykj.pointsmall.vo.PrivilegeVO;
import com.zykj.pointsmall.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-05
 */
@Slf4j
@Service
public class UserService {


    private final BocUserInfoRepository userRepository;
    private final RedisUtil redisUtil;
    private final InvitationRepository invitationRepository;
    private final StaffRepository staffRepository;
    private final IntegralService integralService;
    private final PrivilegeUserRepository privilegeUserRepository;
    private final PrivilegeRepository privilegeRepository;

    @Autowired
    public UserService(BocUserInfoRepository userRepository, RedisUtil redisUtil, InvitationRepository invitationRepository, StaffRepository staffRepository, IntegralService integralService, PrivilegeUserRepository privilegeUserRepository, PrivilegeRepository privilegeRepository) {
        this.userRepository = userRepository;
        this.redisUtil = redisUtil;
        this.invitationRepository = invitationRepository;
        this.staffRepository = staffRepository;
        this.integralService = integralService;
        this.privilegeUserRepository = privilegeUserRepository;
        this.privilegeRepository = privilegeRepository;
    }

    public UserDTO userJsonToBean(JSONObject jsonData) {
        String customerId = jsonData.getString("customerId");
        if (!StringUtils.hasLength(customerId)) {
            return null;
        }
        return jsonData.toJavaObject(UserDTO.class);
    }

    public synchronized UserDTO add(UserDTO userDTO) {
        String customerId = userDTO.getCustomerId();
        if (StringUtils.hasLength(customerId)) {
            Optional<BocUserInfo> userInfoOptional = userRepository.findById(customerId);
            if (!userInfoOptional.isPresent()) {
                BocUserInfo userInfo = BeanUtils.userDtoToUserPo(userDTO);
                userInfo.setCreateDate(DateUtil.getFormat(new Date()));
                userInfo = userRepository.saveAndFlush(userInfo);
                return BeanUtils.userPoToUserDto(userInfo);
            } else {
                BocUserInfo userInfo = userInfoOptional.get();
                if(!StringUtils.hasText(userInfo.getIbknum())){
                    userInfo.setIbknum(userDTO.getIbknum());
                    userInfo.setBranchId(userDTO.getBranchId());
                    userInfo = userRepository.save(userInfo);
                }

                return BeanUtils.userPoToUserDto(userInfo);
            }
        }
        return null;
    }


    @Async
    public void sendSms(String phone) {

        //生成6位数验证码
        String code = String.valueOf(new SecureRandom().nextInt(899999) + 100000);
        //使用发短信SDK
        CCPRestSDK ccpRestSDK = new CCPRestSDK();
        Map<String, Object> result = ccpRestSDK.sendTemplateSMS(phone, "443273", new String[]{code, "10分钟"});

        //如果返回成功则存入redis
        if ("000000".equals(result.get("statusCode"))) {
            redisUtil.setStringSECONDS(phone, code, 60 * 10L);
        }
        log.info("验证码：{} 发送结果：{}", code, result);
        redisUtil.setStringSECONDS(phone, code, 60 * 10L);
    }

    public UserDTO bindingPhone(String token, String phone, String code) {

        String dCode = redisUtil.getString(phone);
        if (!StringUtils.hasText(dCode) || !code.equals(dCode)) {
            return null;
        }

        Optional<BocUserInfo> userInfoOptional = userRepository.findById(token);

        if (userInfoOptional.isPresent()) {
            BocUserInfo userInfo = userInfoOptional.get();
            userInfo.setMobile(phone);
            userInfo = userRepository.saveAndFlush(userInfo);

            Staff staff = staffRepository.findByPhone(phone);

            if (staff != null) {
                staff.setBindingDate(new Date());
                staff.setCustomerId(token);
                staffRepository.save(staff);
            }

            Invitation invitation = invitationRepository.findByAcceptPhone(phone);

            if (invitation != null) {
                invitation.setStatus(2);
                invitation.setAcceptCustomerId(userInfo.getCustomerId());
                invitation.setAcceptDate(new Date());
                invitationRepository.save(invitation);
            }

            return BeanUtils.userPoToUserDto(userInfo);
        }

        return null;
    }

    public Optional<BocUserInfo> getPersonalInfo(String token) {
        return userRepository.findById(token);
    }


    public Optional<BocUserInfo> findUserInfo(String customerId) {
        return userRepository.findById(customerId);
    }

    public ServerResponse accept(BocUserInfo inviteUserInfo, String phone) {
        if (inviteUserInfo.getMobile().equals(phone)) {
            return ServerResponse.createMessage(411, "不能邀请自己");
        }
        Invitation invitation = invitationRepository.findByAcceptPhone(phone);
        if (invitation != null) {
            return ServerResponse.createMessage(412, "已接受其他用户邀请");
        }

        BocUserInfo userInfo = userRepository.findByMobile(phone);

        Invitation invite = new Invitation();
        invite.setInviteCustomerId(inviteUserInfo.getCustomerId());
        invite.setInvitePhone(inviteUserInfo.getMobile());
        invite.setAcceptCustomerId("");
        invite.setAcceptPhone(phone);
        invite.setStatus(1);
        if (userInfo != null) {
            invite.setStatus(2);
            invite.setAcceptCustomerId(userInfo.getCustomerId());
        }
        invite.setInviteDate(new Date());
        return ServerResponse.ok();
    }

    public List<Invitation> getAcceptInfo(String token) {

        return invitationRepository.findAllByInviteCustomerId(token);
    }

    @Async("asyncServiceExecutor")
    public void QueryAttentionStatus(String token) {
        BocUserInfo userInfo = userRepository.findByCustomerIdAndFollow(token, 0);
        if (userInfo != null && userInfo.getFollow() == 0) {
            SnowflakeIdFactory factory = new SnowflakeIdFactory(2, 2);
            String nonce = String.valueOf(factory.nextId());
            if (StringUtils.hasText(userInfo.getMobile())) {
                JSONObject jsonObject = getAttentionStatus(userInfo.getMobile(), nonce);
                String code = jsonObject.getString("code");
                if ("200".equals(code)) {
                    setFollowStatus(token, userInfo, jsonObject);
                }
                log.info(jsonObject.toJSONString());
            }
        }
    }

    private void setFollowStatus(String token, BocUserInfo userInfo, JSONObject jsonObject) {
        String data = jsonObject.getString("data");
        if (data.contains("true")) {
            if (userInfo.getFollow() == 0) {
                userInfo.setFollow(1);
                userRepository.save(userInfo);
                integralService.addIntegral(token, 20, 4, 0, "关注公众号");
            }
        }
    }

    private JSONObject getAttentionStatus(String phone, String nonce) {

        String key = "e6bfb9952bbfc22642e342b1759845be";

        String md5 = key + phone.substring(6) + nonce + key;

        String sign = DigestUtils.md5DigestAsHex(md5.getBytes());

        String str = OkhttpUtil.getInstance().httpGet("http://bocjx.bzh001.com/api/attention?mobile=" + phone + "&nonce=" + nonce + "&sign=" + sign);
        JSONObject jsonObject = JSON.parseObject(str);
        return jsonObject;
    }


    public Page<UserVO> findUserPage(int page, String userNo) {

        int size = 10;

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createDate");
        Page<BocUserInfo> userPage = userRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtils.hasText(userNo)) {
                list.add(criteriaBuilder.like(root.get("customerId").as(String.class), "%" + userNo + "%"));
            }

            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);

        List<BocUserInfo> userInfoList = userPage.getContent();
        List<UserVO> userVOList = new ArrayList<>();
        UserVO userVO;

        //查询积分
        List<String> customerIdList = userInfoList.stream().map(BocUserInfo::getCustomerId).collect(Collectors.toList());
        List<Integral> integralList = integralService.findAllByCustomerIdIn(customerIdList);

        //查询特权
        List<PrivilegeUser> privilegeUserList = privilegeUserRepository.findAllByCustomerIdIn(customerIdList);
        List<Integer> privilegeIdList = privilegeUserList.stream().map(PrivilegeUser::getPrivilegeId).collect(Collectors.toList());
        List<Privilege> privilegeList = privilegeRepository.findAllByPrivilegeIdInAndState(privilegeIdList, 1);
        List<PrivilegeVO> privilegeVOList = new ArrayList<>();
        PrivilegeVO privilegeVO;

        for (BocUserInfo userInfo : userInfoList) {
            userVO = new UserVO();
            userVO.setCustomerId(userInfo.getCustomerId());
            userVO.setMobile(userInfo.getMobile());
            userVO.setCreateDate(userInfo.getCreateDate());
            if (userInfo.getCreateDate() == null) {
                userVO.setCreateDate(DateUtil.getFormat(new Date()));
            }

            userVO.setIbknum(userInfo.getIbknum());

            for (Integral integral : integralList) {
                if (userInfo.getCustomerId().equals(integral.getCustomerId())) {
                    userVO.setIntegral(integral.getResidualFraction());
                    break;
                }
            }
            if (userVO.getIntegral() == null) {
                userVO.setIntegral(0);
            }

            privilegeVOList = new ArrayList<>();


            for (PrivilegeUser privilegeUser : privilegeUserList) {
                if (userInfo.getCustomerId().equals(privilegeUser.getCustomerId())) {
                    for (Privilege privilege : privilegeList) {
                        if (privilege.getPrivilegeId().equals(privilegeUser.getPrivilegeId())) {
                            privilegeVO = new PrivilegeVO();
                            privilegeVO.setPrivilegeId(privilege.getPrivilegeId());
                            privilegeVO.setName(privilege.getName());
                            privilegeVOList.add(privilegeVO);
                        }
                    }
                }
            }
            userVO.setPrivilegeList(privilegeVOList);
            userVOList.add(userVO);
        }

        return new PageImpl<>(userVOList, pageable, userPage.getTotalElements());
    }
}
