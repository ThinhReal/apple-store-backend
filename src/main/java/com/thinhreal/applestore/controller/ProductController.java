package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.api.ProductsApi;
import com.thinhreal.applestore.api.model.RequestProductDTO;
import com.thinhreal.applestore.api.model.ResponseCategoryDTO;
import com.thinhreal.applestore.api.model.ResponseProductDTO;
import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.entity.CategoryEntity;
import com.thinhreal.applestore.model.entity.ProductEntity;
import com.thinhreal.applestore.repository.Category;
import com.thinhreal.applestore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductsApi {

    private final ProductRepository productRepository;
    private final Category categoryRepository;

    @Override
    public ResponseEntity<List<ResponseProductDTO>> getAllProducts() {
        List<ResponseProductDTO> products = productRepository.findAll().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(products);
    }

    @Override
    public ResponseEntity<ResponseProductDTO> createProduct(RequestProductDTO requestProductDTO) {
        ProductEntity saved = productRepository.save(toEntity(requestProductDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @Override
    public ResponseEntity<ResponseProductDTO> getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find product with id: " + id));
        return ResponseEntity.ok(toDto(entity));
    }

    @Override
    public ResponseEntity<ResponseProductDTO> updateProduct(Long id, RequestProductDTO requestProductDTO) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find product with id: " + id));
        applyDto(entity, requestProductDTO);
        ProductEntity saved = productRepository.save(entity);
        return ResponseEntity.ok(toDto(saved));
    }

    @Override
    public ResponseEntity<Void> deleteProduct(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find product with id: " + id));
        productRepository.delete(entity);
        return ResponseEntity.noContent().build();
    }

    private ResponseProductDTO toDto(ProductEntity entity) {
        ResponseProductDTO dto = new ResponseProductDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice().doubleValue());
        dto.setStockQuantity(entity.getStockQuantity());
        if (entity.getCategory() != null) {
            dto.setCategory(toCategoryDto(entity.getCategory()));
        }
        return dto;
    }

    private ResponseCategoryDTO toCategoryDto(CategoryEntity category) {
        ResponseCategoryDTO dto = new ResponseCategoryDTO();
        dto.setId(String.valueOf(category.getId()));
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    private ProductEntity toEntity(RequestProductDTO dto) {
        ProductEntity entity = new ProductEntity();
        applyDto(entity, dto);
        return entity;
    }

    private void applyDto(ProductEntity entity, RequestProductDTO dto) {
        entity.setName(dto.getName());
        entity.setPrice(BigDecimal.valueOf(dto.getPrice()));
        entity.setStockQuantity(dto.getStockQuantity());
        CategoryEntity category = categoryRepository.findById(Long.parseLong(dto.getCategoryId()))
                .orElseThrow(() -> new BusinessException("Cannot find category with id: " + dto.getCategoryId()));
        entity.setCategory(category);
    }
}
