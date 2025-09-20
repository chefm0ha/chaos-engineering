package com.devops.orderservice.controller;

import com.devops.orderservice.model.dto.response.OrderResponseDto;
import com.devops.orderservice.model.enums.OrderStatus;
import com.devops.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class GlobalOrderController {

    private final OrderService orderService;

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponseDto> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponseDto> getOrderByNumber(@PathVariable String orderNumber) {
        OrderResponseDto order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(order);
    }
}