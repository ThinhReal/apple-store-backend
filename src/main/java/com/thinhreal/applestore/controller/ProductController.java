package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.api.ProductsApi;
import com.thinhreal.applestore.api.model.ProductCreateDTO;
import com.thinhreal.applestore.api.model.ProductDTO;
import com.thinhreal.applestore.model.entity.ProductEntity;
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

    @Override
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productRepository.findAll().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(products);
    }

    @Override
    public ResponseEntity<ProductDTO> createProduct(ProductCreateDTO productCreateDTO) {
        ProductEntity saved = productRepository.save(toEntity(productCreateDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    private ProductDTO toDto(ProductEntity entity) {
        ProductDTO dto = new ProductDTO(
                entity.getName(),
                entity.getPrice().doubleValue(),
                entity.getStockQuantity()
        );
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    private ProductEntity toEntity(ProductCreateDTO dto) {
        ProductEntity entity = new ProductEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(BigDecimal.valueOf(dto.getPrice()));
        entity.setStockQuantity(dto.getStockQuantity());
        return entity;
    }
}
