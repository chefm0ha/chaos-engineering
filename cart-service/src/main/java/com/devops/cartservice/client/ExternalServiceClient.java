package com.devops.cartservice.client;

import com.devops.cartservice.model.dto.response.ProductResponseDto;
import com.devops.cartservice.model.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExternalServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.user-service.url:http://localhost:8081}")
    private String userServiceUrl;

    @Value("${services.product-service.url:http://localhost:8082}")
    private String productServiceUrl;

    public UserResponseDto getUser(Long userId) {
        String url = userServiceUrl + "/api/users/" + userId;
        return restTemplate.getForObject(url, UserResponseDto.class);
    }

    public ProductResponseDto getProduct(Long productId) {
        String url = productServiceUrl + "/api/products/" + productId;
        return restTemplate.getForObject(url, ProductResponseDto.class);
    }
}