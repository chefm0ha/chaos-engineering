package com.devops.orderservice.model.dto.response;

import com.devops.orderservice.model.enums.OrderStatus;
import com.devops.orderservice.model.enums.PaymentMethod;
import com.devops.orderservice.model.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String notes;
    private UserResponseDto user;
    private AddressResponseDto shippingAddress;
    private List<OrderItemResponseDto> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}