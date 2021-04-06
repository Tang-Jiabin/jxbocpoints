package com.zykj.pointsmall.vo;

import lombok.Data;

/**
 * 邀请
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-18
 */
@Data
public class InvitationVO {

    private String phone;

    private String status;

    private String prize;
    /**
     * 跳转链接
     */
    private String jumpLink;
}
