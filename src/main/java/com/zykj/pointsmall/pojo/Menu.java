package com.zykj.pointsmall.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * 菜单
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-02-26
 */
@Data
@Entity
@Table(name = "jx_boc_menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer menuId;
    private Integer parentId;
    private String name;
    private String icon;
    private String url;
    private String hidden;

}
