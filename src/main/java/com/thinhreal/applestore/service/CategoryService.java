package com.thinhreal.applestore.service;

import com.thinhreal.applestore.api.model.RequestCategoryDTO;
import com.thinhreal.applestore.api.model.ResponseCategoryDTO;
import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.entity.CategoryEntity;
import com.thinhreal.applestore.repository.CategoryRepository;
import com.thinhreal.applestore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<ResponseCategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public ResponseCategoryDTO createCategory(RequestCategoryDTO requestCategoryDTO) {
        CategoryEntity saved = categoryRepository.save(toEntity(requestCategoryDTO));
        return toDto(saved);
    }

    public ResponseCategoryDTO getCategoryById(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find category with id: " + id));
        return toDto(entity);
    }

    public ResponseCategoryDTO updateCategory(Long id, RequestCategoryDTO requestCategoryDTO) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find category with id: " + id));
        applyDto(entity, requestCategoryDTO);
        CategoryEntity saved = categoryRepository.save(entity);
        return toDto(saved);
    }

    public void deleteCategory(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find category with id: " + id));
        if (productRepository.existsByCategory_Id(id)) {
            throw new BusinessException("Cannot delete category with existing products");
        }
        categoryRepository.delete(entity);
    }

    private ResponseCategoryDTO toDto(CategoryEntity entity) {
        ResponseCategoryDTO dto = new ResponseCategoryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    private CategoryEntity toEntity(RequestCategoryDTO dto) {
        CategoryEntity entity = new CategoryEntity();
        applyDto(entity, dto);
        return entity;
    }

    private void applyDto(CategoryEntity entity, RequestCategoryDTO dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
    }
}
