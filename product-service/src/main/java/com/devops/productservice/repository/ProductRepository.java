package com.devops.productservice.repository;

import com.devops.productservice.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrue();

    Optional<Product> findByIdAndActiveTrue(Long id);

    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    List<Product> findByPriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND p.active = true")
    List<Product> findAvailableProducts();

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold AND p.active = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
}