package com.zykj.pointsmall.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * 特权
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-03-02
 */
@Data
public class PrivilegeDTO {

    private Integer privilegeId;
    private String privilegeName;
    private String closingDate;
    private String startDate;
    private List<PrProductsDTO> priProducts;


}
