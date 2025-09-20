package com.devops.productservice.repository;

import com.devops.productservice.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByActiveTrue();

    Optional<Category> findByIdAndActiveTrue(Long id);

    Optional<Category> findByNameIgnoreCaseAndActiveTrue(String name);

    boolean existsByNameIgnoreCaseAndActiveTrue(String name);
}