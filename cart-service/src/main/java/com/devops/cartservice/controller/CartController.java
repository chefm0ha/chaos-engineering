package com.devops.cartservice.controller;

import com.devops.cartservice.model.dto.request.CartRequestDto;
import com.devops.cartservice.model.dto.response.CartResponseDto;
import com.devops.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getUserCart(@PathVariable Long userId) {
        List<CartResponseDto> cartItems = cartService.getUserCart(userId);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping
    public ResponseEntity<CartResponseDto> addToCart(@PathVariable Long userId,
                                                     @Valid @RequestBody CartRequestDto requestDto) {
        CartResponseDto cartItem = cartService.addToCart(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<CartResponseDto> updateCartItem(@PathVariable Long userId,
                                                          @PathVariable Long cartId,
                                                          @Valid @RequestBody CartRequestDto requestDto) {
        CartResponseDto updatedCartItem = cartService.updateCartItem(userId, cartId, requestDto);
        return ResponseEntity.ok(updatedCartItem);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long userId, @PathVariable Long cartId) {
        cartService.removeFromCart(userId, cartId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCartItemCount(@PathVariable Long userId) {
        Long count = cartService.getCartItemCount(userId);
        return ResponseEntity.ok(count);
    }
}