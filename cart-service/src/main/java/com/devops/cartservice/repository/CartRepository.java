package com.devops.cartservice.repository;

import com.devops.cartservice.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUserId(Long userId);

    Optional<Cart> findByUserIdAndProductId(Long userId, Long productId);

    Optional<Cart> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.userId = :userId AND c.productId = :productId")
    void deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT COUNT(c) FROM Cart c WHERE c.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}