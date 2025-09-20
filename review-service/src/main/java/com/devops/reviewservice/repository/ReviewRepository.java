package com.devops.reviewservice.repository;

import com.devops.reviewservice.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductIdAndActiveTrue(Long productId);

    List<Review> findByUserIdAndActiveTrue(Long userId);

    Optional<Review> findByProductIdAndUserIdAndActiveTrue(Long productId, Long userId);

    Optional<Review> findByIdAndActiveTrue(Long id);

    List<Review> findByRatingAndActiveTrue(Integer rating);

    List<Review> findByVerifiedPurchaseTrueAndActiveTrue();

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = :productId AND r.active = true")
    Double getAverageRatingForProduct(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.productId = :productId AND r.active = true")
    Long getReviewCountForProduct(@Param("productId") Long productId);

    @Query("SELECT r FROM Review r WHERE r.productId = :productId AND r.rating >= :minRating AND r.active = true")
    List<Review> findByProductIdAndRatingGreaterThanEqual(@Param("productId") Long productId,
                                                          @Param("minRating") Integer minRating);
}