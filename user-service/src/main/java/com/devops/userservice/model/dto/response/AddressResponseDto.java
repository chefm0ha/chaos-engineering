package com.devops.userservice.model.dto.response;

import com.devops.userservice.model.enums.AddressType;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}