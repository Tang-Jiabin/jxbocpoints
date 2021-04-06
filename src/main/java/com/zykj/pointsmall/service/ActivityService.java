package com.zykj.pointsmall.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zykj.pointsmall.common.AES;
import com.zykj.pointsmall.common.AESUtil;
import com.zykj.pointsmall.common.SnowflakeIdFactory;
import com.zykj.pointsmall.dao.*;
import com.zykj.pointsmall.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 活动
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-07
 */
@Slf4j
@Service
public class ActivityService {


    private final ActivityRepository activityRepository;
    private final IntegralDetailedRepository detailedRepository;
    private final IntegralRepository integralRepository;
    private final BocUserInfoRepository userInfoRepository;
    private final InvitationRepository invitationRepository;
    private final StaffRepository staffRepository;
    private final BranchRepository branchRepository;
    private final OutletsRepository outletsRepository;
    private final ActivityInfoRepository activityInfoRepository;


    public ActivityService(ActivityRepository activityRepository, IntegralDetailedRepository detailedRepository, IntegralRepository integralRepository, BocUserInfoRepository userInfoRepository, InvitationRepository invitationRepository, StaffRepository staffRepository, BranchRepository branchRepository, OutletsRepository outletsRepository, ActivityInfoRepository activityInfoRepository) {
        this.activityRepository = activityRepository;
        this.detailedRepository = detailedRepository;
        this.integralRepository = integralRepository;
        this.userInfoRepository = userInfoRepository;
        this.invitationRepository = invitationRepository;
        this.staffRepository = staffRepository;
        this.branchRepository = branchRepository;
        this.outletsRepository = outletsRepository;
        this.activityInfoRepository = activityInfoRepository;
    }

    public List<Activity> getOngoingActivity(String token) {

        Optional<BocUserInfo> userInfoOptional = userInfoRepository.findById(token);
        List<Activity> activities = activityRepository.findAllByState(1);
        if (userInfoOptional.isPresent()) {

            for (Activity activity : activities) {
                activity = getEndate(userInfoOptional.get(), activity);
            }
        }

        return activities;
    }

    private Activity getEndate(BocUserInfo userInfo, Activity activity) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customerId", userInfo.getCustomerId());
        jsonObject.put("createDate", userInfo.getCreateDate());
        String data = jsonObject.toJSONString();
        try {
            data = AESUtil.encryptToString(data, activity.getAlias());
            if (activity.getActivityId() != 8 && activity.getActivityId() != 9) {
                data = URLEncoder.encode(data, "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        activity.setJumpLink(activity.getJumpLink() + "?param=" + data);
        return activity;
    }

    @Async
    public void submitActivity(String param) {

        String decrypt = "";
        try {
            decrypt = AES.Decrypt(param, "zykj@12345678#@!");
        } catch (Exception e) {
            e.printStackTrace();
        }


        JSONObject jsonObject = JSON.parseObject(decrypt);
        String customerId = jsonObject.getString("customerId");
        String activityName = jsonObject.getString("activityName");
        Activity activity = activityRepository.findByAlias(activityName);
        if (activity == null) {
            log.info("活动名称为找到:{}", activityName);
            return;
        }

        Optional<BocUserInfo> userInfo = userInfoRepository.findById(customerId);
        if (!userInfo.isPresent()) {
            log.info("活动用户为找到:{}", customerId);
            return;
        }

        Integral integral = integralRepository.findByCustomerId(customerId);

        IntegralDetailed detailed = detailedRepository.findByActivityIdAndIntegralId(activity.getActivityId(), integral.getIntegralId());
        if (detailed != null) {
            log.info("用户已经参与该活动:{}", customerId);
            return;
        }

        integral.setTotalFraction(integral.getTotalFraction() + activity.getIntegral());
        integral.setResidualFraction(integral.getResidualFraction() + activity.getIntegral());
        integral.setUpdateDate(new Date());
        integralRepository.saveAndFlush(integral);

        detailed = new IntegralDetailed();
        SnowflakeIdFactory factory = new SnowflakeIdFactory(1, 2);
        detailed.setDetailId(String.valueOf(factory.nextId()));
        detailed.setIntegralId(integral.getIntegralId());
        detailed.setFraction(activity.getIntegral());
        detailed.setType(1);
        detailed.setSource(2);
        detailed.setActivityId(activity.getActivityId());
        detailed.setTradeName(activity.getName());
        detailed.setTransactionDate(new Date());
        detailedRepository.save(detailed);

    }

    public Activity add(String activityName, String activityLink, String activityImg, Integer type, String startTime, String endTime) {

        Activity activity = activityRepository.findByName(activityName);
        if (activity == null) {
            SnowflakeIdFactory factory = new SnowflakeIdFactory(5, 5);
            activity = new Activity();
            activity.setActivityId(0);
            activity.setName(activityName);
            activity.setJumpLink(activityLink);
            activity.setImgUrl(activityImg);
            activity.setActivityDesc(String.valueOf(factory.nextId()));
            activity.setCreatedDate(new Date());
            activity.setType(type);
            activity.setIntegral(10);

            activity.setState(2);

            String secretKey = null;
            try {
                secretKey = AESUtil.genSecretKeyToString();
                activity.setAlias(secretKey);
                activityRepository.save(activity);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return activity;
    }

    public Activity findByName(String activityName) {
        return activityRepository.findByName(activityName);
    }

    public Integer submit(String activityId, String sign) {

        Activity activity = activityRepository.findByActivityDesc(activityId);
        String name = activity.getName();
        LocalDateTime dateTime = LocalDateTime.now();
        if (activity == null) {
            return 404;
        }


        try {
            sign = URLDecoder.decode(sign);
            String decrypt = AESUtil.decryptToString(sign, activity.getAlias());

            JSONObject json = JSON.parseObject(decrypt);
            String phone = json.getString("phone");
            String customerId = json.getString("customerId");
            Integer addIntegral = json.getInteger("integral");



            Optional<BocUserInfo> userInfo = userInfoRepository.findById(customerId);
            if (!userInfo.isPresent()) {
                log.info("活动用户为找到:{}", customerId);
                return 405;
            }

            Integral integral = null;
            IntegralDetailed detailed = null;
            if (StringUtils.hasText(phone)) {
                integral = integralRepository.findByPhone(phone);
            }

            if (StringUtils.hasText(customerId) && StringUtils.hasText(phone)) {
                Invitation invitation = new Invitation();
                invitation.setInviteCustomerId(customerId);
                invitation.setInvitePhone("");
                invitation.setAcceptCustomerId("");
                invitation.setAcceptPhone(phone);
                invitation.setAcceptDate(new Date());
                invitation.setInviteDate(new Date());
                invitation.setStatus(1);
                invitationRepository.save(invitation);

            }

            if (integral != null) {
                detailed = detailedRepository.findByActivityIdAndIntegralId(activity.getActivityId(), integral.getIntegralId());
            } else {
                integral = new Integral();
                integral.setCustomerId("");
                integral.setPhone(phone);
                integral.setUsedFraction(0);
                integral.setResidualFraction(0);
                integral.setTotalFraction(0);
                integral.setCreateDate(new Date());
                integral.setUpdateDate(new Date());
                integral = integralRepository.save(integral);

            }

            if (detailed != null) {
                log.info("用户已经参与该活动:{}", customerId);
                return 406;
            }

            integral.setTotalFraction(integral.getTotalFraction() + addIntegral);
            integral.setResidualFraction(integral.getResidualFraction() + addIntegral);
            integral.setUpdateDate(new Date());
            integralRepository.saveAndFlush(integral);


            detailed = new IntegralDetailed();
            SnowflakeIdFactory factory = new SnowflakeIdFactory(1, 2);
            detailed.setDetailId(String.valueOf(factory.nextId()));
            detailed.setIntegralId(integral.getIntegralId());
            detailed.setFraction(addIntegral);
            detailed.setType(1);
            detailed.setSource(2);
            detailed.setActivityId(activity.getActivityId());
            detailed.setTradeName(activity.getName());
            detailed.setTransactionDate(new Date());
            detailedRepository.save(detailed);
            addActivityInfo(phone, customerId, name, dateTime);
            return 200;
        } catch (Exception e) {
            e.printStackTrace();
            return 400;
        }
    }

    private void addActivityInfo(String phone, String customerId, String name, LocalDateTime dateTime) {
        Thread thread = new Thread(() -> {
            ActivityInfo info = new ActivityInfo();
            info.setActivityName(name);
            info.setCustomerId(customerId);
            info.setStaff("");
            info.setPhone(phone);
            info.setBranchName("");
            info.setOutletsName("");
            info.setMonth(dateTime.getMonthValue());
            info.setYear(dateTime.getYear());
            info.setDay(dateTime.getDayOfMonth());
            info.setDateTime(dateTime);
            BocUserInfo bocUserInfo = userInfoRepository.findByMobile(phone);
            if (bocUserInfo != null) {
                info.setCustomerId(bocUserInfo.getCustomerId());
            }
            Staff staff = staffRepository.findByCustomerId(customerId);
            if (staff != null) {
                info.setStaff(staff.getStaffName());
                Optional<Branch> branch = branchRepository.findById(staff.getBranchId());
                if (branch.isPresent()) {
                    info.setBranchName(branch.get().getBranchName());
                }
                Optional<Outlets> outlets = outletsRepository.findById(staff.getOutletsId());
                if (outlets.isPresent()) {
                    info.setOutletsName(outlets.get().getOutletsName());
                }

            }
            activityInfoRepository.save(info);

        });
        thread.start();
    }


    public Activity findActivityJumpEnLink(int activityId, String token) {
        Optional<BocUserInfo> optionalUserInfo = userInfoRepository.findById(token);
        Optional<Activity> optionalActivity = activityRepository.findById(activityId);

        if (optionalUserInfo.isPresent() && optionalActivity.isPresent()) {
            return getEndate(optionalUserInfo.get(), optionalActivity.get());
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String str = URLDecoder.decode("VDkwVn0bS5L6RqtOMRv2g5wVN4irix%2B4pLXQHs6iIIzrA3qZ61Znc4ZchE0T5DAFoKhgPoEDqu9%2FnDkOM5inWMbu73NQFLTosCqFMRtqKGVDc%2FA0zRD2o34UJ9TNloN%2F");
        String s = AESUtil.decryptToString(str, "EFXxqHpU4+9J5z9dTq7D4g==");
        System.out.println(s);
    }
}
