package com.zykj.pointsmall.pojo;

import com.sun.javafx.beans.IDProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * 菜单权限
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-02-26
 */
@Data
@Entity
@Table(name = "jx_boc_menu_power")
public class MenuPower {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer mpId;

    private Integer adminId;

    private Integer menuId;
}
