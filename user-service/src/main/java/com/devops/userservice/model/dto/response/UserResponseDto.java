package com.devops.userservice.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}