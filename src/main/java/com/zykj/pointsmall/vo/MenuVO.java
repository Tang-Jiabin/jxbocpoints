package com.zykj.pointsmall.vo;

import lombok.Data;

import java.util.List;

/**
 * 菜单
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-02-26
 */
@Data
public class MenuVO {

    private String name;
    private String icon;
    private String url;
    private String hidden;
    private List<MenuVO> menuVOList;
}
