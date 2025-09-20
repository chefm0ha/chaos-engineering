package com.devops.reviewservice.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequestDto {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    private String comment;
}