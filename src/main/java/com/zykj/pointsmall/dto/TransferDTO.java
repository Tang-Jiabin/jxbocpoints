package com.zykj.pointsmall.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * ce
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-02-09
 */
@Data
public class TransferDTO {
    private Integer privilegeId;
    private String name;
    private List<Map<String, String>> user;
    private List<Map<String, String>> goods;
}
