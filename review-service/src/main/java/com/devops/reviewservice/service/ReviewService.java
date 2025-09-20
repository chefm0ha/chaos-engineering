package com.devops.reviewservice.service;

import com.devops.reviewservice.client.ExternalServiceClient;
import com.devops.reviewservice.exception.DuplicateResourceException;
import com.devops.reviewservice.exception.ResourceNotFoundException;
import com.devops.reviewservice.model.dto.request.ReviewRequestDto;
import com.devops.reviewservice.model.dto.response.ProductResponseDto;
import com.devops.reviewservice.model.dto.response.ReviewResponseDto;
import com.devops.reviewservice.model.dto.response.UserResponseDto;
import com.devops.reviewservice.model.entity.Review;
import com.devops.reviewservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ExternalServiceClient externalServiceClient;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getProductReviews(Long productId) {
        validateProductExists(productId);

        return reviewRepository.findByProductIdAndActiveTrue(productId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getUserReviews(Long userId) {
        validateUserExists(userId);

        return reviewRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewById(Long reviewId) {
        Review review = findActiveReviewById(reviewId);
        return mapToResponseDto(review);
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long productId) {
        validateProductExists(productId);
        return reviewRepository.getAverageRatingForProduct(productId);
    }

    @Transactional(readOnly = true)
    public Long getReviewCount(Long productId) {
        validateProductExists(productId);
        return reviewRepository.getReviewCountForProduct(productId);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getHighRatedReviews(Long productId, Integer minRating) {
        validateProductExists(productId);

        return reviewRepository.findByProductIdAndRatingGreaterThanEqual(productId, minRating).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public ReviewResponseDto createReview(Long userId, ReviewRequestDto requestDto) {
        validateUserExists(userId);
        validateProductExists(requestDto.getProductId());
        validateUserHasntReviewedProduct(userId, requestDto.getProductId());

        Review review = mapToEntity(requestDto);
        review.setUserId(userId);

        Review savedReview = reviewRepository.save(review);
        return mapToResponseDto(savedReview);
    }

    public ReviewResponseDto updateReview(Long userId, Long reviewId, ReviewRequestDto requestDto) {
        Review existingReview = findActiveReviewById(reviewId);

        // Ensure user owns the review
        if (!existingReview.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User can only update their own reviews");
        }

        validateProductExists(requestDto.getProductId());

        modelMapper.map(requestDto, existingReview);
        Review updatedReview = reviewRepository.save(existingReview);
        return mapToResponseDto(updatedReview);
    }

    public void deleteReview(Long userId, Long reviewId) {
        Review review = findActiveReviewById(reviewId);

        // Ensure user owns the review
        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User can only delete their own reviews");
        }

        review.setActive(false);
        reviewRepository.save(review);
    }

    private Review findActiveReviewById(Long reviewId) {
        return reviewRepository.findByIdAndActiveTrue(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
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

    private void validateUserHasntReviewedProduct(Long userId, Long productId) {
        if (reviewRepository.findByProductIdAndUserIdAndActiveTrue(productId, userId).isPresent()) {
            throw new DuplicateResourceException("User has already reviewed this product");
        }
    }

    private Review mapToEntity(ReviewRequestDto dto) {
        return modelMapper.map(dto, Review.class);
    }

    private ReviewResponseDto mapToResponseDto(Review review) {
        ReviewResponseDto dto = modelMapper.map(review, ReviewResponseDto.class);

        // Get user details
        try {
            UserResponseDto user = externalServiceClient.getUser(review.getUserId());
            dto.setUser(user);
        } catch (Exception e) {
            // Handle gracefully - user might be deleted
        }

        // Get product details
        try {
            ProductResponseDto product = externalServiceClient.getProduct(review.getProductId());
            dto.setProduct(product);
        } catch (Exception e) {
            // Handle gracefully - product might be deleted
        }

        return dto;
    }
}