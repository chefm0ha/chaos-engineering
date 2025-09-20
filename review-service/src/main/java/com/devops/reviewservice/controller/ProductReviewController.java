package com.devops.reviewservice.controller;

import com.devops.reviewservice.model.dto.response.ReviewResponseDto;
import com.devops.reviewservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getProductReviews(@PathVariable Long productId) {
        List<ReviewResponseDto> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long productId) {
        Double averageRating = reviewService.getAverageRating(productId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getReviewCount(@PathVariable Long productId) {
        Long count = reviewService.getReviewCount(productId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/high-rated")
    public ResponseEntity<List<ReviewResponseDto>> getHighRatedReviews(@PathVariable Long productId,
                                                                       @RequestParam(defaultValue = "4") Integer minRating) {
        List<ReviewResponseDto> reviews = reviewService.getHighRatedReviews(productId, minRating);
        return ResponseEntity.ok(reviews);
    }
}