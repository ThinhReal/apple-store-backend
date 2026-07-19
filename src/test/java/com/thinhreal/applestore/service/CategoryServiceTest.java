package com.thinhreal.applestore.service;

import com.thinhreal.applestore.api.model.RequestCategoryDTO;
import com.thinhreal.applestore.api.model.ResponseCategoryDTO;
import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.entity.CategoryEntity;
import com.thinhreal.applestore.repository.CategoryRepository;
import com.thinhreal.applestore.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategories_whenCategoriesExist_returnsMappedResponseDtos() {
        // Given
        CategoryEntity smartphones = buildCategoryEntity(1L, "Smartphones", "Mobile devices");
        CategoryEntity laptops = buildCategoryEntity(2L, "Laptops", "MacBooks");
        when(categoryRepository.findAll()).thenReturn(List.of(smartphones, laptops));

        // When
        List<ResponseCategoryDTO> result = categoryService.getAllCategories();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("1");
        assertThat(result.get(0).getName()).isEqualTo("Smartphones");
        assertThat(result.get(0).getDescription()).isEqualTo("Mobile devices");
        assertThat(result.get(1).getId()).isEqualTo("2");
        assertThat(result.get(1).getName()).isEqualTo("Laptops");
        verify(categoryRepository).findAll();
    }

    private RequestCategoryDTO buildRequestCategoryDto(String name, String description) {
        RequestCategoryDTO request = new RequestCategoryDTO(name);
        request.setDescription(description);
        return request;
    }

    @Test
    void createCategory_whenRequestIsValid_savesEntityAndReturnsResponseDto() {
        // Given
        RequestCategoryDTO request = buildRequestCategoryDto("Smartphones", "Mobile devices");

        CategoryEntity savedEntity = buildCategoryEntity(1L, "Smartphones", "Mobile devices");
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(savedEntity);

        // When
        ResponseCategoryDTO result = categoryService.createCategory(request);

        // Then
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("Smartphones");
        assertThat(result.getDescription()).isEqualTo("Mobile devices");

        ArgumentCaptor<CategoryEntity> entityCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
        verify(categoryRepository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getName()).isEqualTo("Smartphones");
        assertThat(entityCaptor.getValue().getDescription()).isEqualTo("Mobile devices");
    }

    @Test
    void getCategoryById_whenCategoryExists_returnsResponseDto() {
        // Given
        CategoryEntity entity = buildCategoryEntity(1L, "Smartphones", "Mobile devices");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(entity));

        // When
        ResponseCategoryDTO result = categoryService.getCategoryById("1");

        // Then
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("Smartphones");
        assertThat(result.getDescription()).isEqualTo("Mobile devices");
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryById_whenCategoryNotFound_throwsBusinessException() {
        // Given
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> categoryService.getCategoryById("99"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot find category with id: 99");

        verify(categoryRepository).findById(99L);
    }

    @Test
    void updateCategory_whenCategoryExists_updatesFieldsAndReturnsResponseDto() {
        // Given
        RequestCategoryDTO request = buildRequestCategoryDto("Tablets", "Tablet devices");
        CategoryEntity existing = buildCategoryEntity(1L, "Smartphones", "Mobile devices");
        CategoryEntity updated = buildCategoryEntity(1L, "Tablets", "Tablet devices");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(updated);

        // When
        ResponseCategoryDTO result = categoryService.updateCategory("1", request);

        // Then
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("Tablets");
        assertThat(result.getDescription()).isEqualTo("Tablet devices");
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(existing);
    }

    @Test
    void updateCategory_whenCategoryNotFound_throwsBusinessException() {
        // Given
        RequestCategoryDTO request = buildRequestCategoryDto("Tablets", "Tablet devices");
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> categoryService.updateCategory("99", request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot find category with id: 99");

        verify(categoryRepository).findById(99L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_whenCategoryExistsAndHasNoProducts_deletesCategory() {
        // Given
        CategoryEntity entity = buildCategoryEntity(1L, "Smartphones", "Mobile devices");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(productRepository.existsByCategory_Id(1L)).thenReturn(false);

        // When
        categoryService.deleteCategory("1");

        // Then
        verify(categoryRepository).findById(1L);
        verify(productRepository).existsByCategory_Id(1L);
        verify(categoryRepository).delete(entity);
    }

    @Test
    void deleteCategory_whenCategoryNotFound_throwsBusinessException() {
        // Given
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> categoryService.deleteCategory("99"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot find category with id: 99");

        verify(categoryRepository).findById(99L);
        verify(productRepository, never()).existsByCategory_Id(any());
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void deleteCategory_whenCategoryHasProducts_throwsBusinessException() {
        // Given
        CategoryEntity entity = buildCategoryEntity(1L, "Smartphones", "Mobile devices");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(productRepository.existsByCategory_Id(1L)).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> categoryService.deleteCategory("1"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot delete category with existing products");

        verify(categoryRepository).findById(1L);
        verify(productRepository).existsByCategory_Id(1L);
        verify(categoryRepository, never()).delete(any());
    }

    private CategoryEntity buildCategoryEntity(Long id, String name, String description) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setDescription(description);
        return entity;
    }
}
