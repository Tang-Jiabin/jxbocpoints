package com.zykj.pointsmall.controller;

import com.alibaba.fastjson.JSONObject;
import com.bocnet.common.security.PKCS7Tool;
import com.zykj.pointsmall.common.*;
import com.zykj.pointsmall.common.interceptor.Authorization;
import com.zykj.pointsmall.common.log.WebLog;
import com.zykj.pointsmall.pojo.*;
import com.zykj.pointsmall.service.IntegralService;
import com.zykj.pointsmall.service.PayOrderService;
import com.zykj.pointsmall.service.PrivilegeService;
import com.zykj.pointsmall.service.ProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 支付
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-11
 */
@Slf4j
@RestController
@RequestMapping("pay")
public class PayController {

    @Resource
    private HttpServletRequest request;

    private final BankConfig bankConfig;
    private final ProductsService productsService;
    private final PrivilegeService privilegeService;
    private final PayOrderService payOrderService;
    private final IntegralService integralService;


    @Autowired
    public PayController(BankConfig bankConfig, ProductsService productsService, PrivilegeService privilegeService, PayOrderService payOrderService, IntegralService integralService) {
        this.bankConfig = bankConfig;
        this.productsService = productsService;
        this.privilegeService = privilegeService;
        this.payOrderService = payOrderService;
        this.integralService = integralService;
    }

    @WebLog("支付")
    @Authorization
    @PostMapping(value = "getPayParam")
    public ServerResponse<JSONObject> getPayParam(Integer productId,Integer privilegeId,@RequestAttribute String token) throws Exception {
        JSONObject jsonObject = new JSONObject();
        Optional<Products> productsOptional = productsService.getProductDetails(productId);

        BigDecimal price = new BigDecimal(0.00);
        Integer integral = 0;
        if (productsOptional.isPresent()) {
            ProductsPrivilege productsPrivilege = null;
            Products products = productsOptional.get();
            if (products.getNumber() <= 0) {
                return ServerResponse.createMessage(440, "库存不足");
            }
            if (privilegeId != 0) {
                products = privilegeService.getUserProductRights(token, products,privilegeId);
                Optional<ProductsPrivilege> optional = privilegeService.getPrivilegeProducts(privilegeId, productId);
                if (optional.isPresent()) {
                    productsPrivilege = optional.get();
                    if (productsPrivilege.getNumber()<=0) {
                        return ServerResponse.createMessage(440, "库存不足");
                    }
                }
                price = products.getVipPrice();
                integral = products.getVipIntegral();
            } else {
                price = products.getPrice();
                integral = products.getIntegral();
            }

            Integral userIntegral = integralService.getUserIntegral(token);
            if (userIntegral == null) {
                return ServerResponse.createMessage(440, "积分不足");
            }
            if (userIntegral.getResidualFraction() < integral) {
                return ServerResponse.createMessage(440, "积分不足");
            }


            SnowflakeIdFactory idFactory = new SnowflakeIdFactory(10, 10);

            Map<String, String> map = new HashMap<>(12);
            Double d = 1.00d;

            String orderNo = String.valueOf(idFactory.nextId());
            String paymount = BigDecimalUtil.format(price);
            String requestTime = DateUtil.getFormat(new Date(), "yyyyMMddHHmmss");
            String merchantNo = bankConfig.getMerchantNo();
            String orderUrl = bankConfig.getOrderUrl();
            String version = "1.0.1";
            String messageId = "0000212";
            String security = "P7";
            String custBackFlag = "1";

            //支付参数
            map.put("orderNo", orderNo);
            map.put("curCode", "001");
            map.put("orderAmount", paymount);
            map.put("orderTime", requestTime);
            map.put("orderNote", "众耘科技");
            map.put("subMerchantName", "众耘科技");
            map.put("orderUrl", orderUrl);
            map.put("subMerchantCode", "OF0001");
            map.put("subMerchantClass", "0001");
            map.put("subMerchantZone", "cz340000");
            String xml = map2xml(map);

            String message = Base64Utils.encodeToString(xml.getBytes(StandardCharsets.UTF_8));

            String keyStorePath = bankConfig.getKeyStorePath();
            String keystorePwd = bankConfig.getKeystorePwd();
            PKCS7Tool tool = null;
            String sign = null;

            InputStream inputStream = PayController.class.getResourceAsStream(keyStorePath);
            tool = PKCS7Tool.getSigner(inputStream, KeyStore.getDefaultType(), keystorePwd, keystorePwd);
            sign = tool.sign(xml.getBytes(StandardCharsets.UTF_8));

            Map<String, String> rmap = new HashMap<>(8);
            rmap.put("merchantNo", Base64Utils.encodeToString(merchantNo.getBytes(StandardCharsets.UTF_8)));
            rmap.put("version", Base64Utils.encodeToString(version.getBytes(StandardCharsets.UTF_8)));
            rmap.put("messageId", Base64Utils.encodeToString(messageId.getBytes(StandardCharsets.UTF_8)));
            rmap.put("security", Base64Utils.encodeToString(security.getBytes(StandardCharsets.UTF_8)));
            rmap.put("custBackFlag", custBackFlag);
            rmap.put("message", message);
            rmap.put("signature", sign);
            Object obj = JSONObject.toJSON(rmap);

            String payParam = obj.toString();

            if (StringUtils.hasLength(payParam)) {
                if (productsPrivilege != null) {
                    productsPrivilege.setNumber(productsPrivilege.getNumber()-1);
                    privilegeService.saveProductsPrivilege(productsPrivilege);
                }
                PayOrder order = payOrderService.createOrder(merchantNo, orderNo, paymount, token, products, integral,privilegeId);
                jsonObject.put("data", payParam);
                return ServerResponse.createMessage(200,order.getOrderNo(),jsonObject);
            }
        }

        return ServerResponse.createMessage(440, "库存不足");
    }

    @WebLog("支付回调")
    @RequestMapping("/payRsps")
    public String payRsps(HttpServletRequest request) throws Exception {

        String merchantNo = request.getParameter("merchantNo");
        String orderNo = request.getParameter("orderNo");
        String orderSeq = request.getParameter("orderSeq");
        String cardTyp = request.getParameter("cardTyp");
        String payTime = request.getParameter("payTime");
        String orderStatus = request.getParameter("orderStatus");
        String payAmount = request.getParameter("payAmount");
        String signData = request.getParameter("signData");
        log.info("orderNo###接收订单回调,订单编号：" + orderNo);
        String plainText = merchantNo + "|" + orderNo + "|" + orderSeq + "|" + cardTyp + "|" + payTime + "|" + orderStatus + "|" + payAmount;
        //获取验签根证书，对P7验签使用二级根证书
        String certificatePath = bankConfig.getCertificatePath();
        InputStream resourceAsStream = PayController.class.getClassLoader().getResourceAsStream(certificatePath);
        PKCS7Tool tool = PKCS7Tool.getVerifier(resourceAsStream, "DER");

        tool.verify(signData, plainText.getBytes(StandardCharsets.UTF_8), null);


        //1-支付成功
        if (("1".equals(orderStatus))) {
            PayOrder payOrder = payOrderService.findByOrderNo(orderNo);

            if (payOrder == null) {
                log.error("支付结果未查询到：{}", plainText);
                return "";
            }
            //屏蔽重复通知
            if (payOrder.getOrderStatus() != 1) {
                return "";
            }

            payOrder.setOrderStatus(2);
            payOrder.setPayTime(payTime);
            payOrder.setCardTyp(cardTyp);
            payOrder.setOrderSeq(orderSeq);
            payOrderService.save(payOrder);
            payOrderService.paySuccess(orderNo,payOrder.getCustomerId());
            return "";
        } else {
            log.error("订单状态错误：{}", plainText);
        }
        return "";
    }

    @GetMapping(value = "cancelPay")
    public void cancelPay(Integer productId, @RequestAttribute String customerId) {
        payOrderService.cancellation(productId,customerId);

    }

    @WebLog("支付成功")
    @Authorization
    @GetMapping("paySuccess")
    public ServerResponse paySuccess(String orderNo,@RequestAttribute String token){

        payOrderService.paySuccess(orderNo,token);

        return ServerResponse.ok();
    }


    public String map2xml(Map map) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
        String requestTime = df.format(new Date());// new Date()为获取当前系统时间
        String xmlStr = "<request><head><requestTime>" + requestTime + "</requestTime></head><body>";
        Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            xmlStr += "<" + entry.getKey() + ">" + entry.getValue() + "</" + entry.getKey() + ">";
        }
        xmlStr += "</body></request>";
        return xmlStr;
    }
}
