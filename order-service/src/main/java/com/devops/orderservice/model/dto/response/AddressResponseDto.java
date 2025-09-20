package com.devops.orderservice.model.dto.response;

import com.devops.orderservice.model.enums.AddressType;
import lombok.Data;

@Data
public class AddressResponseDto {
    private Long id;
    private AddressType type;
    private String firstName;
    private String lastName;
    private String streetAddress;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;
    private String phone;
    private Boolean isDefault;
}