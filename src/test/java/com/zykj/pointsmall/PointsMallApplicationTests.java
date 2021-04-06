package com.zykj.pointsmall;

import com.alibaba.fastjson.JSONObject;
import com.zykj.pointsmall.common.FileUtil;
import com.zykj.pointsmall.common.OkhttpUtil;
import com.zykj.pointsmall.common.RedisUtil;
import com.zykj.pointsmall.common.interceptor.TokenManager;
import com.zykj.pointsmall.dao.*;
import com.zykj.pointsmall.pojo.Branch;
import com.zykj.pointsmall.pojo.Outlets;
import com.zykj.pointsmall.pojo.Staff;
import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class PointsMallApplicationTests {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private OutletsRepository outletsRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private PrizeInfoRepository prizeInfoRepository;
    @Autowired
    private PrivilegeUserRepository privilegeUserRepository;
    @Autowired
    private BocUserInfoRepository userInfoRepository;
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private DataAdminRepository dataAdminRepository;

//    @Test
//    void contextLoads() {
//        String key = "12324_milk";
//        String not = redisUtil.getString(key);
//
//        System.out.println(StringUtils.hasText(not));
//        if (StringUtils.hasText(not)) {
//            redisUtil.setString(key, "1");
//        }
//        redisUtil.incr(key);
//
//        not = redisUtil.getString(key);
//        System.out.println(not);
//
//    }

//    @Test
//    void addStaff() throws Exception {
//        String path = "/Users/tang/Desktop/手机银行行长员工注册信息（终版）.xlsx";
//        File file = new File(path);
//
//        InputStream inputStream = new FileInputStream(file);
//
//        List<Map<String, String>> list = FileUtil.readExcel(inputStream, "");
//
//        Set<String> branchSet = new HashSet<>();
//        Set<String> outletsSet = new HashSet<>();
//        Set<String> userSet = new HashSet<>();
//        Branch branch;
//        Outlets outlets;
//        Staff staff;
//
//        List<Outlets> outletsList = outletsRepository.findAll();
//
//        for (Map<String, String> map : list) {
//
//            String userName = map.get("员工姓名").trim();
//            String userNo = map.get("员工工号").trim();
//            String userPhone = map.get("员工手机号").trim();
//            String outletsNo = map.get("网点机构号").trim();
//            Integer oid = 0;
//            Integer bid = 0;
//
//            for (Outlets outlets1 : outletsList) {
//                if(outlets1.getOutletsNo().equals(outletsNo)){
//                    oid = outlets1.getOutletsId();
//                    bid = outlets1.getBranchId();
//                }
//            }
//
//
//            if(userSet.add(userNo)){
//                staff = new Staff();
//                staff.setStaffName(userName);
//                staff.setStaffNo(userNo);
//                staff.setPhone(userPhone);
//                staff.setBranchId(bid);
//                staff.setOutletsId(oid);
//                try {
//                    staff = staffRepository.save(staff);
//                }catch (Exception e){
//                    System.out.println("失败"+staff.toString());
//                }
//
//            }
//
//        }
//    }



//    @Test
//    void 导入兑换码() throws Exception {
//        String path = "/Users/tang/Desktop/中行江西积分商城/乐享-微信立减金10元-0224.xlsx";
//        File file = new File(path);
//        FileInputStream inputStream = new FileInputStream(file);
//
//        List<Map<String, String>> list = FileUtil.readExcel(inputStream, "");
//
//        PrizeInfo prizeInfo;
//        List<PrizeInfo> prizeInfoList = new ArrayList<>();
//
//        for (Map<String, String> map : list) {
//            String 兑换链接 = map.get("卡密");
//            prizeInfo = new PrizeInfo();
//            prizeInfo.setPrizeName("微信立减金10元");
//            prizeInfo.setStatus(1);
//            prizeInfo.setCustomerId("");
//            prizeInfo.setCreateDate(new Date());
//            prizeInfo.setDistributionDate(new Date());
//            prizeInfo.setCode(兑换链接);
//            prizeInfo.setOrderId(0);
//            prizeInfo.setProductsId(113);
//            prizeInfoList.add(prizeInfo);
//            System.out.println(prizeInfo);
//
//        }
//
//        prizeInfoRepository.saveAll(prizeInfoList);
//        System.out.println(prizeInfoList.size());
//
//    }


//    @Test
//    void 导入特权用户() throws FileNotFoundException {
//        String path = "/Users/tang/Desktop/分层营销第一期数据（发众耘）.xlsx";
//        File file = new File(path);
//        FileInputStream inputStream = new FileInputStream(file);
//
//        List<Map<String, String>> list = FileUtil.readExcel(inputStream, "");
//
//
//        List<PrivilegeUser> puList = new ArrayList<>();
//
//        for (Map<String, String> map : list) {
//            PrivilegeUser privilegeUser = new PrivilegeUser();
//            privilegeUser.setPrivilegeId(1);
//            privilegeUser.setCustomerId(map.get("网银客户号"));
//            privilegeUser.setStatus(1);
//            System.out.println(map.get("网银客户号"));
//            puList.add(privilegeUser);
//        }
//
//        privilegeUserRepository.saveAll(puList);
//    }

//    @Test
//    void 获取token() {
////        306476657 227725665
//        TokenModel tokenModel = tokenManager.createToken("306476657");
//        redisUtil.setString("306476657", tokenModel.getToken());
//        System.out.println(tokenModel.getToken());
//    }

//    @Test
//    void 更新特权用户(){
//
//        List<PrivilegeUser> privilegeUserList = privilegeUserRepository.findAllByStatus(2);
//
//        for (PrivilegeUser privilegeUser : privilegeUserList) {
//            privilegeUser.setStatus(1);
//        }
//        privilegeUserRepository.saveAll(privilegeUserList);
//
//    }

    @Test
    void 测试接口() {
        String url = "http://localhost:8088/jxbocpoints/admin/data/get?page=1&limit=10";
        String s = OkhttpUtil.getInstance().httpGet(url);
        System.out.println(s);
    }




}
