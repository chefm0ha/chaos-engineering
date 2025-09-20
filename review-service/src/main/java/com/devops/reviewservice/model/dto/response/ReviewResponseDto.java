package com.devops.reviewservice.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponseDto {
    private Long id;
    private Integer rating;
    private String title;
    private String comment;
    private Boolean verifiedPurchase;
    private Boolean active;
    private UserResponseDto user;
    private ProductResponseDto product;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}