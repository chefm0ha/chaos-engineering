package com.devops.userservice.controller;

import com.devops.userservice.model.dto.request.AddressRequestDto;
import com.devops.userservice.model.dto.response.AddressResponseDto;
import com.devops.userservice.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getUserAddresses(@PathVariable Long userId) {
        List<AddressResponseDto> addresses = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddressById(@PathVariable Long userId,
                                                             @PathVariable Long addressId) {
        AddressResponseDto address = addressService.getAddressById(userId, addressId);
        return ResponseEntity.ok(address);
    }

    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(@PathVariable Long userId,
                                                            @Valid @RequestBody AddressRequestDto requestDto) {
        AddressResponseDto createdAddress = addressService.createAddress(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(@PathVariable Long userId,
                                                            @PathVariable Long addressId,
                                                            @Valid @RequestBody AddressRequestDto requestDto) {
        AddressResponseDto updatedAddress = addressService.updateAddress(userId, addressId, requestDto);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}