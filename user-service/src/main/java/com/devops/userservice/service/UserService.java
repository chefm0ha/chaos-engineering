package com.devops.userservice.service;

import com.devops.userservice.exception.DuplicateResourceException;
import com.devops.userservice.exception.ResourceNotFoundException;
import com.devops.userservice.model.dto.request.UserRequestDto;
import com.devops.userservice.model.dto.response.AddressResponseDto;
import com.devops.userservice.model.dto.response.UserResponseDto;
import com.devops.userservice.model.entity.Address;
import com.devops.userservice.model.entity.User;
import com.devops.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsersWithAddresses() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDtoWithAddresses)
                .toList();
    }

    // Add this mapping method
    private UserResponseDto mapToResponseDtoWithAddresses(User user) {
        UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);

        // Map addresses
        List<AddressResponseDto> addresses = user.getAddresses().stream()
                .filter(Address::getActive)
                .map(address -> modelMapper.map(address, AddressResponseDto.class))
                .toList();

        dto.setAddresses(addresses);
        return dto;
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = findUserById(id);
        return mapToResponseDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToResponseDto(user);
    }

    public UserResponseDto createUser(UserRequestDto requestDto) {
        validateUniqueFields(requestDto.getUsername(), requestDto.getEmail());

        User user = mapToEntity(requestDto);
        user.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));

        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        User existingUser = findUserById(id);

        // Check for uniqueness only if username/email changed
        if (!existingUser.getUsername().equals(requestDto.getUsername()) &&
                userRepository.existsByUsername(requestDto.getUsername())) {
                throw new DuplicateResourceException("User", "username", requestDto.getUsername());
            }

        if (!existingUser.getEmail().equals(requestDto.getEmail()) &&
                userRepository.existsByEmail(requestDto.getEmail())) {
                throw new DuplicateResourceException("User", "email", requestDto.getEmail());
            }


        modelMapper.map(requestDto, existingUser);
        if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return mapToResponseDto(updatedUser);
    }

    public void deleteUser(Long id) {
        User user = findUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private void validateUniqueFields(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("User", "username", username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User", "email", email);
        }
    }

    private User mapToEntity(UserRequestDto dto) {
        return modelMapper.map(dto, User.class);
    }

    private UserResponseDto mapToResponseDto(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }
}