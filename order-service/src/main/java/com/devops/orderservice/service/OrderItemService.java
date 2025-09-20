package com.devops.orderservice.service;

import com.devops.orderservice.client.ExternalServiceClient;
import com.devops.orderservice.exception.ResourceNotFoundException;
import com.devops.orderservice.model.dto.response.OrderItemResponseDto;
import com.devops.orderservice.model.dto.response.ProductResponseDto;
import com.devops.orderservice.model.entity.Order;
import com.devops.orderservice.model.entity.OrderItem;
import com.devops.orderservice.repository.OrderItemRepository;
import com.devops.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ExternalServiceClient externalServiceClient;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public OrderItemResponseDto addItemToOrder(Long orderId, Long productId, Integer quantity) {
        Order order = findOrderById(orderId);
        ProductResponseDto product = validateProductExists(productId);

        // Check if item already exists in order
        return orderItemRepository.findByOrderId(orderId).stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .map(existingItem -> updateOrderItemQuantity(existingItem, quantity))
                .orElseGet(() -> createNewOrderItem(order, productId, quantity, product.getPrice()));
    }

    public OrderItemResponseDto updateOrderItemQuantity(Long orderItemId, Integer quantity) {
        OrderItem orderItem = findOrderItemById(orderItemId);
        return updateOrderItemQuantity(orderItem, quantity);
    }

    public void removeItemFromOrder(Long orderItemId) {
        OrderItem orderItem = findOrderItemById(orderItemId);
        orderItemRepository.delete(orderItem);
    }

    private OrderItemResponseDto createNewOrderItem(Order order, Long productId, Integer quantity, BigDecimal unitPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);
        orderItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)));

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return mapToResponseDto(savedOrderItem);
    }

    private OrderItemResponseDto updateOrderItemQuantity(OrderItem orderItem, Integer additionalQuantity) {
        orderItem.setQuantity(orderItem.getQuantity() + additionalQuantity);
        orderItem.setTotalPrice(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));

        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        return mapToResponseDto(updatedOrderItem);
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    private OrderItem findOrderItemById(Long orderItemId) {
        return orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", orderItemId));
    }

    private ProductResponseDto validateProductExists(Long productId) {
        try {
            return externalServiceClient.getProduct(productId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
    }

    private OrderItemResponseDto mapToResponseDto(OrderItem orderItem) {
        OrderItemResponseDto dto = modelMapper.map(orderItem, OrderItemResponseDto.class);

        // Get product details
        try {
            ProductResponseDto product = externalServiceClient.getProduct(orderItem.getProductId());
            dto.setProduct(product);
        } catch (Exception e) {
            // Handle gracefully - product might be deleted
        }

        return dto;
    }
}