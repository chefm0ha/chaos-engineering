package com.devops.productservice.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean active;
    private CategoryResponseDto category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}