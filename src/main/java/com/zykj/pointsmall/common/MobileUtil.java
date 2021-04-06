package com.zykj.pointsmall.common;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-07
 */

public class MobileUtil {

    private static String regex = "0?(13|14|15|16|17|18|19)[0-9]{9}";
    private static Pattern p = null;

    public static boolean phoneValidation(String phone) {

        if (phone.length() != 11) {
            return false;
        } else {
            p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            if (isMatch) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String midReplaceStar(String phone){
        if(!StringUtils.hasText(phone) || phone.length() != 11){
            return "";
        }
        return phone.replace(phone.substring(3,7),"****");
    }


}
