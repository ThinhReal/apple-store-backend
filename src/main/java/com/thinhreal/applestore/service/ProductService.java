package com.thinhreal.applestore.service;

import com.thinhreal.applestore.api.model.RequestProductDTO;
import com.thinhreal.applestore.api.model.ResponseCategoryDTO;
import com.thinhreal.applestore.api.model.ResponseProductDTO;
import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.entity.CategoryEntity;
import com.thinhreal.applestore.model.entity.ProductEntity;
import com.thinhreal.applestore.repository.CategoryRepository;
import com.thinhreal.applestore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ResponseProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public ResponseProductDTO createProduct(RequestProductDTO requestProductDTO) {
        ProductEntity saved = productRepository.save(toEntity(requestProductDTO));
        return toDto(saved);
    }

    public ResponseProductDTO getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find product with id: " + id));
        return toDto(entity);
    }

    public ResponseProductDTO updateProduct(Long id, RequestProductDTO requestProductDTO) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find product with id: " + id));
        applyDto(entity, requestProductDTO);
        ProductEntity saved = productRepository.save(entity);
        return toDto(saved);
    }

    public void deleteProduct(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find product with id: " + id));
        productRepository.delete(entity);
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
        dto.setId(category.getId());
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
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BusinessException("Cannot find category with id: " + dto.getCategoryId()));
        entity.setCategory(category);
    }
}
