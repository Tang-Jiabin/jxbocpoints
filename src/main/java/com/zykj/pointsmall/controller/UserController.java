package com.zykj.pointsmall.controller;

import com.alibaba.fastjson.JSONObject;
import com.zykj.pointsmall.common.*;
import com.zykj.pointsmall.common.interceptor.Authorization;
import com.zykj.pointsmall.common.interceptor.TokenManager;
import com.zykj.pointsmall.common.interceptor.TokenModel;
import com.zykj.pointsmall.common.log.WebLog;
import com.zykj.pointsmall.dao.ActivityRepository;
import com.zykj.pointsmall.dto.*;
import com.zykj.pointsmall.pojo.*;
import com.zykj.pointsmall.service.*;
import com.zykj.pointsmall.vo.InputDetailsVO;
import com.zykj.pointsmall.vo.InvitationVO;
import com.zykj.pointsmall.vo.StaffVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-04
 */
@Slf4j
@RestController
@RequestMapping(value = "user")
public class UserController {

    @Resource
    private HttpServletRequest request;
    private final UserService userService;
    private final TokenManager tokenManager;
    private final CipherUtil cipherUtil;
    private final IntegralService integralService;
    private final ActivityService activityService;
    private final ProductsService productsService;
    private final UserMilkService milkService;
    private final AddressService addressService;
    private final StaffService staffService;
    private final RedisUtil redisUtil;


    @Autowired
    public UserController(RedisUtil redisUtil, BankConfig bankConfig, UserService userService, TokenManager tokenManager, CipherUtil cipherUtil, IntegralService integralService, ActivityService activityService, ProductsService productsService, UserMilkService milkService, AddressService addressService, StaffService staffService, RedisUtil redisUtil1) {
        this.integralService = integralService;
        this.userService = userService;
        this.tokenManager = tokenManager;
        this.cipherUtil = cipherUtil;
        this.activityService = activityService;
        this.productsService = productsService;
        this.milkService = milkService;
        this.addressService = addressService;
        this.staffService = staffService;
        this.redisUtil = redisUtil1;
    }

    @WebLog("登录")
    @PostMapping(value = "login")
    public ServerResponse<JSONObject> login() {

        String data = request.getParameter("data");

        JSONObject auInfo = getAuthorizeInfo(data);
        if (auInfo == null) {
            return ServerResponse.createMessage(410, "请重新授权");
        }

        String mBody = auInfo.getString("body");
        String sKey = auInfo.getString("skey");

        mBody = cipherUtil.deAES(mBody, sKey);
        log.info("最终的明文报文体: {}", mBody);

        JSONObject bodyData = JSONObject.parseObject(mBody);
        UserDTO userDTO = userService.userJsonToBean(bodyData);

        if (userDTO == null) {
            return ServerResponse.createMessage(410, "请重新授权");
        }

        userDTO = userService.add(userDTO);

        if (userDTO == null) {
            return ServerResponse.createMessage(410, "请重新授权");
        }

        if (!"47370".equals(userDTO.getIbknum())) {
            return ServerResponse.createMessage(410, "仅限江西用户参与");
        }

        JSONObject result = new JSONObject();
        result.put("bState", 0);
        result.put("phone", 0);
        if (StringUtils.hasText(userDTO.getMobile())) {
            result.put("bState", 1);
            result.put("phone", MobileUtil.midReplaceStar(userDTO.getMobile()));
        }

        TokenModel tokenModel = tokenManager.createToken(userDTO.getCustomerId());
        if (tokenModel == null) {
            return ServerResponse.createMessage(410, "请重新授权");
        }
        result.put("token", tokenModel.getToken());


        return ServerResponse.createSuccess(result);
    }

    @Authorization
    @WebLog("绑定手机号")
    @PostMapping(value = "bindingPhone")
    public ServerResponse bingPhone(String phone, String code, @RequestAttribute String token) {

        if (!MobileUtil.phoneValidation(phone)) {
            return ServerResponse.createMessage(411, "手机号错误");
        }

        if (!StringUtils.hasText(code)) {
            return ServerResponse.createMessage(412, "验证码不能为空");
        }

        UserDTO userDTO = userService.bindingPhone(token, phone, code);
        if (userDTO == null) {
            return ServerResponse.createMessage(413, "验证码错误");
        }

        StaffDTO staffDTO = staffService.getStaffInfo(token);
        int staff = 0;

        if(staffDTO != null){
            staff = 1;
        }

        return ServerResponse.ok(staff);
    }

    @WebLog("获取验证码")
    @PostMapping(value = "getCaptcha")
    public ServerResponse<String> getCaptcha(@RequestParam String phone) {
        log.info("手机号：" + phone);

        //手机号格式验证
        if (!MobileUtil.phoneValidation(phone)) {
            return ServerResponse.createMessage(400, "手机号格式错误");
        }

        userService.sendSms(phone);

        return ServerResponse.createMessage(200, "短信发送成功");
    }

    @WebLog("首页")
    @Authorization
    @GetMapping(value = "getHomePage")
    public ServerResponse<JSONObject> getHomePage(@RequestAttribute String token) throws UnsupportedEncodingException {

        //积分
        Integral integral = integralService.getUserIntegral(token);

        //牛奶
        UserMilk milk = milkService.findByUserId(token);

        //大众权益
        List<ProductsCategoryDTO> publicProducts = productsService.getPublicProducts();

        //活动专区
        List<Activity> activityList = activityService.getOngoingActivity(token);

        //查询关注状态
        userService.QueryAttentionStatus(token);

        //员工验证
        StaffDTO staffDto = staffService.getStaffInfo(token);
        int staff = 0;
        if (staffDto != null) {
            staff = 1;
        }

        JSONObject resultData = new JSONObject();
        resultData.put("fraction", integral.getResidualFraction());
        resultData.put("publicProducts", publicProducts);
        resultData.put("activityList", activityList.stream().sorted(Comparator.comparing(Activity::getActivityId).reversed()).limit(3).collect(Collectors.toList()));
        resultData.put("milk", milk.getNumber());
        resultData.put("notCollected", milk.getNotCollected());
        resultData.put("adopt", milk.getAdopt());
        resultData.put("share", AES.Encrypt(token, AES.AES_KEY));
        resultData.put("staff", staff);


        return ServerResponse.createSuccess(resultData);
    }

    @Authorization
    @WebLog("个人中心")
    @GetMapping("getPersonal")
    public ServerResponse<JSONObject> getPersonal(@RequestAttribute String token) {
        Optional<BocUserInfo> userInfoOptional = userService.getPersonalInfo(token);
        JSONObject jsonObject = new JSONObject();
        if (userInfoOptional.isPresent()) {
            jsonObject.put("phone", "0");
            if (StringUtils.hasText(userInfoOptional.get().getMobile())) {
                jsonObject.put("phone", MobileUtil.midReplaceStar(userInfoOptional.get().getMobile()));
            }
        }
        Integral integral = integralService.getUserIntegral(token);
        jsonObject.put("integral", integral.getResidualFraction());
        return ServerResponse.createSuccess(jsonObject);
    }

    @Authorization
    @WebLog("签到")
    @GetMapping("clock")
    public ServerResponse<UserMilkDTO> clock(@RequestAttribute String token) {

        UserMilkDTO userMilkDTO = milkService.addMilk(token);

        return ServerResponse.createSuccess(userMilkDTO);
    }

    @WebLog("获取地址")
    @Authorization
    @GetMapping(value = "getAddress")
    public ServerResponse<Address> getAddress(@RequestAttribute String token) {
        Address address = addressService.findByCustomerId(token);
        if (address == null) {
            return ServerResponse.createMessage(411, "无地址信息");
        }
        address.setCustomerId("");
        return ServerResponse.createSuccess(address);
    }

    @WebLog("添加地址")
    @Authorization
    @GetMapping(value = "addAddress")
    public ServerResponse<Address> addAddress(String name, String phone, String region, String address, @RequestAttribute String token) {
        if (!MobileUtil.phoneValidation(phone)) {
            return ServerResponse.createMessage(411, "请填写正确手机号码");
        }

        Address add = addressService.addAddress(name, phone, region, address, token);

        return ServerResponse.createSuccess(add);
    }

    @WebLog("接受邀请")
    @GetMapping("accept")
    public ServerResponse accept(String share, String phone) {
        String customerId = AES.Decrypt(share, AES.AES_KEY);
        Optional<BocUserInfo> userInfoOptional = userService.findUserInfo(customerId);
        if (userInfoOptional.isPresent()) {
            return userService.accept(userInfoOptional.get(), phone);
        }
        return ServerResponse.ok();
    }

    @Authorization
    @WebLog("查询邀请信息")
    @GetMapping("getAcceptInfo")
    public ServerResponse<JSONObject> getAcceptInfo(@RequestAttribute String token) {
        List<Invitation> invitationList = userService.getAcceptInfo(token);
        Activity activity = activityService.findActivityJumpEnLink(18,token);

        List<InvitationVO> voList = new ArrayList<>();
        InvitationVO invitationVO;
        for (Invitation invitation : invitationList) {
            invitationVO = new InvitationVO();
            invitationVO.setStatus(invitation.getStatus() == 2 ? "已参加" : "未参加");
            invitationVO.setPhone(MobileUtil.midReplaceStar(invitation.getAcceptPhone()));
            invitationVO.setPrize("5积分");
            voList.add(invitationVO);
        }
        JSONObject json = new JSONObject();
        json.put("voList",voList);
        json.put("JumpLink",activity.getJumpLink());

        return ServerResponse.ok(json);
    }


    @Authorization
    @WebLog("领养")
    @GetMapping("adopt")
    public ServerResponse adopt(@RequestAttribute String token) {

        String key = token + "_milk";
        String notCollected = redisUtil.getString(key);

        UserMilk milk = milkService.findByUserId(token);

        if (!StringUtils.hasText(notCollected)) {
            redisUtil.getStringRedisTemplate().opsForList().rightPush("milk_not_collected_list", key);
            redisUtil.setString(key, "1");
            milk.setNotCollected(1);
            milk.setAdopt(1);
            milk = milkService.save(milk);
        }

        return ServerResponse.ok();
    }

    private JSONObject getAuthorizeInfo(String data) {
        if (!StringUtils.hasText(data)) {
            return null;
        }
        JSONObject jsonData = JSONObject.parseObject(data);
        if (jsonData == null) {
            return null;
        }
        String cipherText = jsonData.getString("cipherText");
        if (!StringUtils.hasText(cipherText)) {
            return null;
        }
        return JSONObject.parseObject(cipherText);
    }

    //=================================================================================================

    @Authorization
    @WebLog("员工提交数据")
    @PostMapping("submitData")
    public ServerResponse submitData(@RequestBody StaffInfoDto staffInfoDto, @RequestAttribute String token) {


        if (!MobileUtil.phoneValidation(staffInfoDto.getPhone())) {
            return ServerResponse.createMessage(411, "手机号格式不正确");
        }

        staffInfoDto.setCustomerId(token);

        staffService.addActivityInfo(staffInfoDto);

        return ServerResponse.ok();
    }

    @Authorization
    @WebLog("查询录入信息")
    @GetMapping("getInputDetails")
    public ServerResponse getInputDetails(@RequestAttribute String token) {
        List<StaffActivityInfo> activityInfos = staffService.getInputDetails(token);

        InputDetailsVO inputDetailsVO;
        List<InputDetailsVO> voList = new ArrayList<>();

        for (StaffActivityInfo activityInfo : activityInfos) {
            inputDetailsVO = new InputDetailsVO();
            inputDetailsVO.setPhone(MobileUtil.midReplaceStar(activityInfo.getPhone()));
            inputDetailsVO.setDate(DateUtil.getFormat(activityInfo.getCreateDate()));
            List<String> list = new ArrayList<>();
            list.add(activityInfo.getZfb() == 1 ? "绑定支付宝" : "");
            list.add(activityInfo.getWx() == 1 ? "绑定微信" : "");
            list.add(activityInfo.getZzjy() == 1 ? "转账交易" : "");
            list.add(activityInfo.getShjf() == 1 ? "生活缴费" : "");
            list.add(activityInfo.getEtc() == 1 ? "ETC" : "");
            list.add(activityInfo.getJhgh() == 1 ? "结汇购汇" : "");
            list.add(activityInfo.getJj() == 1 ? "基金" : "");
            list.add(activityInfo.getYb() == 1 ? "医保电子凭证" : "");
            list.add(activityInfo.getKjhk() == 1 ? "跨境汇款" : "");
            list.add(activityInfo.getXykhk() == 1 ? "信用卡还款" : "");
            list.add(activityInfo.getZhgjs() == 1 ? "账户贵金属交易" : "");
            list.add(activityInfo.getHd1() == 1 ? "活动1" : "");
            list.add(activityInfo.getHd2() == 1 ? "活动2" : "");
            list.removeIf(String::isEmpty);
            inputDetailsVO.setBusiness(list);
            voList.add(inputDetailsVO);
        }

        return ServerResponse.ok(voList);
    }

    @Authorization
    @WebLog("查询员工信息")
    @GetMapping("getStaffInfo")
    public ServerResponse<StaffVO> getStaffInfo(@RequestAttribute String token) {

        StaffDTO staffInfo = staffService.getStaffDetailsInfo(token);

        if (staffInfo == null) {
            return ServerResponse.createMessage(411, "您不是员工角色");
        }

        StaffVO staffVO = new StaffVO();
        staffVO.setBranchName(staffInfo.getBranchName());
        staffVO.setOutletsName(staffInfo.getOutletsName());
        staffVO.setStaffName(staffInfo.getStaffName());
        staffVO.setAllRanking(staffInfo.getAllRanking());
        staffVO.setOutletsRanking(staffInfo.getOutletsRanking());

        return ServerResponse.ok(staffVO);
    }

    @Authorization
    @WebLog("全辖排名")
    @GetMapping("getAllRankings")
    public ServerResponse getAllRankings(@RequestAttribute String token) {
        RankingDTO rankingDTO = staffService.getAllRankings("",token);
        return ServerResponse.ok(rankingDTO.getEmployeeRankingDTOList());
    }

    @Authorization
    @WebLog("网点员工排名")
    @GetMapping("getNetworkStaffRankings")
    public ServerResponse getNetworkStaffRankings(String outletsName,@RequestAttribute String token) {
        RankingDTO rankingDTO = staffService.getAllRankings(outletsName,token);
        return ServerResponse.ok(rankingDTO.getEmployeeRankingDTOList());
    }

    @Authorization
    @WebLog("分行排名")
    @GetMapping("getBranchRanking")
    public ServerResponse getBranchRanking(@RequestAttribute String token) {
        List<BranchRankingDTO> branchRanking = staffService.getBranchRanking(token);
        return ServerResponse.ok(branchRanking);
    }

    @Authorization
    @WebLog("网点排名")
    @GetMapping("getNetworkRanking")
    public ServerResponse getNetworkRanking(String branchName, @RequestAttribute String token) {

        List<OutletsRankingDTO> networkRanking = staffService.getNetworkRanking(branchName, token);
        return ServerResponse.ok(networkRanking);
    }


    //=================================================================================================

}
