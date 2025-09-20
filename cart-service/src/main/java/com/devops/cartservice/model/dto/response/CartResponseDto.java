package com.devops.cartservice.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartResponseDto {
    private Long id;
    private Integer quantity;
    private ProductResponseDto product;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}