package com.devops.userservice.service;

import com.devops.userservice.exception.ResourceNotFoundException;
import com.devops.userservice.model.dto.request.AddressRequestDto;
import com.devops.userservice.model.dto.response.AddressResponseDto;
import com.devops.userservice.model.entity.Address;
import com.devops.userservice.model.entity.User;
import com.devops.userservice.repository.AddressRepository;
import com.devops.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private static final String ADDRESS = "address";

    @Transactional(readOnly = true)
    public List<AddressResponseDto> getUserAddresses(Long userId) {
        validateUserExists(userId);
        return addressRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AddressResponseDto getAddressById(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserIdAndActiveTrue(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ADDRESS, "id", addressId));
        return mapToResponseDto(address);
    }

    public AddressResponseDto createAddress(Long userId, AddressRequestDto requestDto) {
        User user = findUserById(userId);

        Address address = mapToEntity(requestDto);
        address.setUser(user);

        if (requestDto.getIsDefault()) {
            clearDefaultAddresses(userId);
        }

        Address savedAddress = addressRepository.save(address);
        return mapToResponseDto(savedAddress);
    }

    public AddressResponseDto updateAddress(Long userId, Long addressId, AddressRequestDto requestDto) {
        Address existingAddress = addressRepository.findByIdAndUserIdAndActiveTrue(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ADDRESS, "id", addressId));

        if (requestDto.getIsDefault() && !existingAddress.getIsDefault()) {
            clearDefaultAddresses(userId);
        }

        modelMapper.map(requestDto, existingAddress);
        Address updatedAddress = addressRepository.save(existingAddress);
        return mapToResponseDto(updatedAddress);
    }

    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserIdAndActiveTrue(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ADDRESS, "id", addressId));
        address.setActive(false);
        addressRepository.save(address);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private void clearDefaultAddresses(Long userId) {
        addressRepository.findByUserIdAndIsDefaultTrueAndActiveTrue(userId)
                .ifPresent(defaultAddress -> {
                    defaultAddress.setIsDefault(false);
                    addressRepository.save(defaultAddress);
                });
    }

    private Address mapToEntity(AddressRequestDto dto) {
        return modelMapper.map(dto, Address.class);
    }

    private AddressResponseDto mapToResponseDto(Address address) {
        return modelMapper.map(address, AddressResponseDto.class);
    }
}