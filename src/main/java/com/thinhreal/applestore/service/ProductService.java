package com.thinhreal.applestore.service;

import com.thinhreal.applestore.api.model.FlavorProfileDTO;
import com.thinhreal.applestore.api.model.RequestProductDTO;
import com.thinhreal.applestore.api.model.ResponseCategoryDTO;
import com.thinhreal.applestore.api.model.ResponseProductDTO;
import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.entity.CategoryEntity;
import com.thinhreal.applestore.model.entity.FlavorProfileValue;
import com.thinhreal.applestore.model.entity.ProductEntity;
import com.thinhreal.applestore.repository.CategoryRepository;
import com.thinhreal.applestore.repository.OrderItemRepository;
import com.thinhreal.applestore.repository.ProductRepository;
import com.thinhreal.applestore.util.ApiIdConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;

    public List<ResponseProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public ResponseProductDTO createProduct(RequestProductDTO requestProductDTO) {
        ProductEntity saved = productRepository.save(toEntity(requestProductDTO));
        return toDto(saved);
    }

    public ResponseProductDTO getProductById(String id) {
        Long productId = ApiIdConverter.parseLongId(id, "product");
        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Cannot find product with id: " + id));
        return toDto(entity);
    }

    public ResponseProductDTO updateProduct(String id, RequestProductDTO requestProductDTO) {
        Long productId = ApiIdConverter.parseLongId(id, "product");
        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Cannot find product with id: " + id));
        applyDto(entity, requestProductDTO);
        ProductEntity saved = productRepository.save(entity);
        return toDto(saved);
    }

    public void deleteProduct(String id) {
        Long productId = ApiIdConverter.parseLongId(id, "product");
        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Cannot find product with id: " + id));
        if (orderItemRepository.existsByProduct_Id(productId)) {
            throw new BusinessException("Cannot delete product with existing order items");
        }
        productRepository.delete(entity);
    }

    private ResponseProductDTO toDto(ProductEntity entity) {
        ResponseProductDTO dto = new ResponseProductDTO();
        dto.setId(ApiIdConverter.toApiId(entity.getId()));
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice().doubleValue());
        dto.setStockQuantity(entity.getStockQuantity());
        dto.setOrigin(entity.getOrigin());
        dto.setSeason(entity.getSeason());
        dto.setImageUrl(entity.getImageUrl());
        dto.setTastingNotes(copyList(entity.getTastingNotes()));
        dto.setBestFor(copyList(entity.getBestFor()));
        dto.setFlavorProfile(toFlavorProfileDto(entity.getFlavorProfile()));
        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(entity.getCreatedAt());
        }
        if (entity.getCategory() != null) {
            dto.setCategory(toCategoryDto(entity.getCategory()));
        }
        return dto;
    }

    private ResponseCategoryDTO toCategoryDto(CategoryEntity category) {
        ResponseCategoryDTO dto = new ResponseCategoryDTO();
        dto.setId(ApiIdConverter.toApiId(category.getId()));
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
        entity.setDescription(dto.getDescription());
        entity.setPrice(BigDecimal.valueOf(dto.getPrice()));
        entity.setStockQuantity(dto.getStockQuantity());
        entity.setOrigin(dto.getOrigin());
        entity.setSeason(dto.getSeason());
        entity.setImageUrl(dto.getImageUrl());
        replaceList(entity.getTastingNotes(), dto.getTastingNotes());
        replaceList(entity.getBestFor(), dto.getBestFor());
        entity.setFlavorProfile(toFlavorProfileValue(dto.getFlavorProfile()));

        Long categoryId = ApiIdConverter.parseLongId(dto.getCategoryId(), "category");
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException("Cannot find category with id: " + dto.getCategoryId()));
        entity.setCategory(category);
        entity.setCategoryName(category.getName());
    }

    private FlavorProfileValue toFlavorProfileValue(FlavorProfileDTO dto) {
        if (dto == null) {
            return null;
        }

        FlavorProfileValue value = new FlavorProfileValue();
        value.setSweetnessLevel(dto.getSweetnessLevel());
        value.setTartnessLevel(dto.getTartnessLevel());
        value.setOverallProfile(dto.getOverallProfile());
        value.setTastingDescription(dto.getTastingDescription());
        replaceList(value.getDominantNotes(), dto.getDominantNotes());
        return value;
    }

    private FlavorProfileDTO toFlavorProfileDto(FlavorProfileValue value) {
        if (value == null) {
            return null;
        }

        FlavorProfileDTO dto = new FlavorProfileDTO();
        dto.setSweetnessLevel(value.getSweetnessLevel());
        dto.setTartnessLevel(value.getTartnessLevel());
        dto.setOverallProfile(value.getOverallProfile());
        dto.setTastingDescription(value.getTastingDescription());
        dto.setDominantNotes(copyList(value.getDominantNotes()));
        return dto;
    }

    private List<String> copyList(List<String> source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(source);
    }

    private void replaceList(List<String> target, List<String> source) {
        target.clear();
        if (source != null && !source.isEmpty()) {
            target.addAll(source);
        }
    }
}
