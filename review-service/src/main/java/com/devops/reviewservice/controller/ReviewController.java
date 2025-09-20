package com.devops.reviewservice.controller;

import com.devops.reviewservice.model.dto.request.ReviewRequestDto;
import com.devops.reviewservice.model.dto.response.ReviewResponseDto;
import com.devops.reviewservice.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getUserReviews(@PathVariable Long userId) {
        List<ReviewResponseDto> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable Long userId,
                                                          @Valid @RequestBody ReviewRequestDto requestDto) {
        ReviewResponseDto createdReview = reviewService.createReview(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Long userId,
                                                          @PathVariable Long reviewId,
                                                          @Valid @RequestBody ReviewRequestDto requestDto) {
        ReviewResponseDto updatedReview = reviewService.updateReview(userId, reviewId, requestDto);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long userId, @PathVariable Long reviewId) {
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.noContent().build();
    }
}