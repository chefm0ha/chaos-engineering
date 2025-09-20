package com.devops.productservice.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}