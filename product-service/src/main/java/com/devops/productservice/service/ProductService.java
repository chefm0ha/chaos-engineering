package com.devops.productservice.service;

import com.devops.productservice.exception.ResourceNotFoundException;
import com.devops.productservice.model.dto.request.ProductRequestDto;
import com.devops.productservice.model.dto.response.ProductResponseDto;
import com.devops.productservice.model.entity.Category;
import com.devops.productservice.model.entity.Product;
import com.devops.productservice.repository.CategoryRepository;
import com.devops.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = findProductById(id);
        return mapToResponseDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        validateCategoryExists(categoryId);
        return productRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAvailableProducts() {
        return productRepository.findAvailableProducts().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Product product = mapToEntity(requestDto);

        if (requestDto.getCategoryId() != null) {
            Category category = findCategoryById(requestDto.getCategoryId());
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        Product existingProduct = findProductById(id);

        modelMapper.map(requestDto, existingProduct);

        if (requestDto.getCategoryId() != null) {
            Category category = findCategoryById(requestDto.getCategoryId());
            existingProduct.setCategory(category);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToResponseDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    public ProductResponseDto updateStock(Long id, Integer stockQuantity) {
        Product product = findProductById(id);
        product.setStockQuantity(stockQuantity);
        Product updatedProduct = productRepository.save(product);
        return mapToResponseDto(updatedProduct);
    }

    private Product findProductById(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    private void validateCategoryExists(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
    }

    private Product mapToEntity(ProductRequestDto dto) {
        return modelMapper.map(dto, Product.class);
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        return modelMapper.map(product, ProductResponseDto.class);
    }
}