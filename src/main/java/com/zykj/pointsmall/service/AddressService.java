package com.zykj.pointsmall.service;

import com.zykj.pointsmall.dao.AddressRepository;
import com.zykj.pointsmall.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 地址
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-15
 */

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }


    public Address findByCustomerId(String token) {

        return addressRepository.findByCustomerId(token);
    }

    public Address addAddress(String name, String phone, String region, String address, String token) {
        Address dbAddress = addressRepository.findByCustomerId(token);

        if (dbAddress == null) {
            dbAddress = new Address();
        }
        dbAddress.setName(name);
        dbAddress.setPhone(phone);
        dbAddress.setRegion(region);
        dbAddress.setAddress(address);
        dbAddress.setCustomerId(token);
        dbAddress.setUpdateDate(new Date());

        return addressRepository.save(dbAddress);
    }
}
