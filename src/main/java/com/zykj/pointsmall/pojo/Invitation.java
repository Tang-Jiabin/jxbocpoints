package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 邀请
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-15
 */
@Data
@Entity
@Table(name = "jx_boc_invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer inviteId;

    private String inviteCustomerId;

    private String invitePhone;

    private String acceptCustomerId;

    private String acceptPhone;

    private Date acceptDate;

    private Date inviteDate;
    /**
     * 邀请状态 1-邀请未注册 2-邀请已注册
     */
    private Integer status;
}
