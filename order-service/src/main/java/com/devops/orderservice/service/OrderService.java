package com.devops.orderservice.service;

import com.devops.orderservice.client.ExternalServiceClient;
import com.devops.orderservice.exception.ResourceNotFoundException;
import com.devops.orderservice.model.dto.request.OrderRequestDto;
import com.devops.orderservice.model.dto.response.AddressResponseDto;
import com.devops.orderservice.model.dto.response.OrderResponseDto;
import com.devops.orderservice.model.dto.response.UserResponseDto;
import com.devops.orderservice.model.entity.Order;
import com.devops.orderservice.model.entity.OrderItem;
import com.devops.orderservice.model.enums.OrderStatus;
import com.devops.orderservice.model.enums.PaymentStatus;
import com.devops.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ExternalServiceClient externalServiceClient;
    private final ModelMapper modelMapper;

    private static final String ORDER = "Order";

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getUserOrders(Long userId) {
        validateUserExists(userId);

        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long userId, Long orderId) {
        Order order = findOrderByUserAndId(userId, orderId);
        return mapToResponseDto(order);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER, "orderNumber", orderNumber));
        return mapToResponseDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public OrderResponseDto createOrder(Long userId, OrderRequestDto requestDto) {
        // Validate external dependencies
        validateUserExists(userId);
        validateAddressExists(userId, requestDto.getShippingAddressId());

        // Create order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddressId(requestDto.getShippingAddressId());
        order.setPaymentMethod(requestDto.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setNotes(requestDto.getNotes());
        order.setTotalAmount(BigDecimal.ZERO); // Will be calculated when items are added

        Order savedOrder = orderRepository.save(order);
        return mapToResponseDto(savedOrder);
    }

    public OrderResponseDto updateOrderStatus(Long userId, Long orderId, OrderStatus status) {
        Order order = findOrderByUserAndId(userId, orderId);
        order.setStatus(status);

        Order updatedOrder = orderRepository.save(order);
        return mapToResponseDto(updatedOrder);
    }

    public OrderResponseDto updatePaymentStatus(Long userId, Long orderId, PaymentStatus paymentStatus) {
        Order order = findOrderByUserAndId(userId, orderId);
        order.setPaymentStatus(paymentStatus);

        Order updatedOrder = orderRepository.save(order);
        return mapToResponseDto(updatedOrder);
    }

    public void cancelOrder(Long userId, Long orderId) {
        Order order = findOrderByUserAndId(userId, orderId);

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Cannot cancel order that has been shipped or delivered");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public void recalculateOrderTotal(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER, "id", orderId));

        BigDecimal total = order.getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
        orderRepository.save(order);
    }

    private Order findOrderByUserAndId(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER, "id", orderId));
    }

    private void validateUserExists(Long userId) {
        try {
            externalServiceClient.getUser(userId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
    }

    private void validateAddressExists(Long userId, Long addressId) {
        try {
            externalServiceClient.getAddress(userId, addressId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Address", "id", addressId);
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + LocalDateTime.now().getYear() + "-" +
                String.format("%06d", UUID.randomUUID().hashCode() & 0xFFFFFF);
    }

    private OrderResponseDto mapToResponseDto(Order order) {
        OrderResponseDto dto = modelMapper.map(order, OrderResponseDto.class);

        // Get user details
        try {
            UserResponseDto user = externalServiceClient.getUser(order.getUserId());
            dto.setUser(user);
        } catch (Exception e) {
            // Handle gracefully - user might be deleted
        }

        // Get shipping address details
        if (order.getShippingAddressId() != null) {
            try {
                AddressResponseDto address = externalServiceClient.getAddress(order.getUserId(), order.getShippingAddressId());
                dto.setShippingAddress(address);
            } catch (Exception e) {
                // Handle gracefully - address might be deleted
            }
        }

        return dto;
    }
}