package com.devops.orderservice.controller;

import com.devops.orderservice.model.dto.request.OrderRequestDto;
import com.devops.orderservice.model.dto.response.OrderResponseDto;
import com.devops.orderservice.model.enums.OrderStatus;
import com.devops.orderservice.model.enums.PaymentStatus;
import com.devops.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getUserOrders(@PathVariable Long userId) {
        List<OrderResponseDto> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long userId, @PathVariable Long orderId) {
        OrderResponseDto order = orderService.getOrderById(userId, orderId);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@PathVariable Long userId,
                                                        @Valid @RequestBody OrderRequestDto requestDto) {
        OrderResponseDto createdOrder = orderService.createOrder(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable Long userId,
                                                              @PathVariable Long orderId,
                                                              @RequestParam OrderStatus status) {
        OrderResponseDto updatedOrder = orderService.updateOrderStatus(userId, orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{orderId}/payment-status")
    public ResponseEntity<OrderResponseDto> updatePaymentStatus(@PathVariable Long userId,
                                                                @PathVariable Long orderId,
                                                                @RequestParam PaymentStatus paymentStatus) {
        OrderResponseDto updatedOrder = orderService.updatePaymentStatus(userId, orderId, paymentStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.noContent().build();
    }
}