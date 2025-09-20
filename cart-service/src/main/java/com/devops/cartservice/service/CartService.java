package com.devops.cartservice.service;

import com.devops.cartservice.client.ExternalServiceClient;
import com.devops.cartservice.exception.ResourceNotFoundException;
import com.devops.cartservice.model.dto.request.CartRequestDto;
import com.devops.cartservice.model.dto.response.CartResponseDto;
import com.devops.cartservice.model.dto.response.ProductResponseDto;
import com.devops.cartservice.model.entity.Cart;
import com.devops.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ExternalServiceClient externalServiceClient;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<CartResponseDto> getUserCart(Long userId) {
        validateUserExists(userId);

        return cartRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public CartResponseDto addToCart(Long userId, CartRequestDto requestDto) {
        validateUserExists(userId);
        validateProductExists(requestDto.getProductId());

        return cartRepository.findByUserIdAndProductId(userId, requestDto.getProductId())
                .map(existingCart -> updateCartQuantity(existingCart, requestDto.getQuantity()))
                .orElseGet(() -> createNewCartItem(userId, requestDto));
    }

    public CartResponseDto updateCartItem(Long userId, Long cartId, CartRequestDto requestDto) {
        Cart cart = findCartByUserAndId(userId, cartId);
        validateProductExists(requestDto.getProductId());

        cart.setProductId(requestDto.getProductId());
        cart.setQuantity(requestDto.getQuantity());

        Cart updatedCart = cartRepository.save(cart);
        return mapToResponseDto(updatedCart);
    }

    public void removeFromCart(Long userId, Long cartId) {
        Cart cart = findCartByUserAndId(userId, cartId);
        cartRepository.delete(cart);
    }

    public void clearCart(Long userId) {
        validateUserExists(userId);
        cartRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Long getCartItemCount(Long userId) {
        validateUserExists(userId);
        return cartRepository.countByUserId(userId);
    }

    private CartResponseDto createNewCartItem(Long userId, CartRequestDto requestDto) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(requestDto.getProductId());
        cart.setQuantity(requestDto.getQuantity());

        Cart savedCart = cartRepository.save(cart);
        return mapToResponseDto(savedCart);
    }

    private CartResponseDto updateCartQuantity(Cart existingCart, Integer additionalQuantity) {
        existingCart.setQuantity(existingCart.getQuantity() + additionalQuantity);
        Cart updatedCart = cartRepository.save(existingCart);
        return mapToResponseDto(updatedCart);
    }

    private Cart findCartByUserAndId(Long userId, Long cartId) {
        return cartRepository.findByIdAndUserId(cartId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", cartId));
    }

    private void validateUserExists(Long userId) {
        try {
            externalServiceClient.getUser(userId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
    }

    private void validateProductExists(Long productId) {
        try {
            externalServiceClient.getProduct(productId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
    }

    private CartResponseDto mapToResponseDto(Cart cart) {
        CartResponseDto dto = modelMapper.map(cart, CartResponseDto.class);

        // Get product details from Product Service
        ProductResponseDto product = externalServiceClient.getProduct(cart.getProductId());
        dto.setProduct(product);

        // Calculate subtotal
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
        dto.setSubtotal(subtotal);

        return dto;
    }
}