package com.thinhreal.applestore.service;

import com.thinhreal.applestore.api.model.FlavorProfileDTO;
import com.thinhreal.applestore.api.model.RequestProductDTO;
import com.thinhreal.applestore.api.model.ResponseProductDTO;
import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.entity.CategoryEntity;
import com.thinhreal.applestore.model.entity.ProductEntity;
import com.thinhreal.applestore.repository.CategoryRepository;
import com.thinhreal.applestore.repository.OrderItemRepository;
import com.thinhreal.applestore.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllProducts_whenProductsExist_returnsMappedResponseDtos() {
        CategoryEntity category = buildCategoryEntity(1L, "Smartphones", "Mobile devices");
        ProductEntity iphone = buildProductEntity(1L, "iPhone 16 Pro", "Latest Apple smartphone", category, 999.99, 50);
        when(productRepository.findAll()).thenReturn(List.of(iphone));

        List<ResponseProductDTO> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("1");
        assertThat(result.get(0).getName()).isEqualTo("iPhone 16 Pro");
        assertThat(result.get(0).getDescription()).isEqualTo("Latest Apple smartphone");
        assertThat(result.get(0).getPrice()).isEqualTo(999.99);
        assertThat(result.get(0).getStockQuantity()).isEqualTo(50);
        assertThat(result.get(0).getCategory().getName()).isEqualTo("Smartphones");
        verify(productRepository).findAll();
    }

    @Test
    void createProduct_whenRequestIsValid_savesEntityAndReturnsResponseDto() {
        RequestProductDTO request = buildRequestProductDto("1", "iPhone 16 Pro", "Latest Apple smartphone", 999.99, 50);
        CategoryEntity category = buildCategoryEntity(1L, "Smartphones", "Mobile devices");
        ProductEntity savedEntity = buildProductEntity(1L, "iPhone 16 Pro", "Latest Apple smartphone", category, 999.99, 50);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(savedEntity);

        ResponseProductDTO result = productService.createProduct(request);

        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("iPhone 16 Pro");
        assertThat(result.getDescription()).isEqualTo("Latest Apple smartphone");
        assertThat(result.getCategory().getName()).isEqualTo("Smartphones");

        ArgumentCaptor<ProductEntity> entityCaptor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getName()).isEqualTo("iPhone 16 Pro");
        assertThat(entityCaptor.getValue().getDescription()).isEqualTo("Latest Apple smartphone");
        assertThat(entityCaptor.getValue().getCategory()).isEqualTo(category);
    }

    @Test
    void createProduct_whenExtendedFieldsProvided_mapsAllResponseFields() {
        RequestProductDTO request = buildRequestProductDto("1", "Orchard Reserve Cider", "Small-batch cider.", 18.99, 12);
        request.setOrigin("Sonoma County, CA");
        request.setSeason("Fall harvest");
        request.setImageUrl("https://example.com/cider.jpg");
        request.setTastingNotes(List.of("crisp", "apple"));
        request.setBestFor(List.of("cheese boards", "dinner parties"));

        FlavorProfileDTO flavorProfile = new FlavorProfileDTO();
        flavorProfile.setSweetnessLevel(6);
        flavorProfile.setTartnessLevel(4);
        flavorProfile.setOverallProfile("Bright and refreshing");
        flavorProfile.setDominantNotes(List.of("green apple", "citrus"));
        flavorProfile.setTastingDescription("Clean finish with natural sweetness");
        request.setFlavorProfile(flavorProfile);

        CategoryEntity category = buildCategoryEntity(1L, "Cider & Juice", "Fresh-pressed ciders");
        ProductEntity savedEntity = buildProductEntity(1L, "Orchard Reserve Cider", "Small-batch cider.", category, 18.99, 12);
        savedEntity.setOrigin("Sonoma County, CA");
        savedEntity.setSeason("Fall harvest");
        savedEntity.setImageUrl("https://example.com/cider.jpg");
        savedEntity.setTastingNotes(List.of("crisp", "apple"));
        savedEntity.setBestFor(List.of("cheese boards", "dinner parties"));
        savedEntity.setCreatedAt(OffsetDateTime.parse("2026-07-19T10:00:00Z"));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(savedEntity);

        ResponseProductDTO result = productService.createProduct(request);

        assertThat(result.getOrigin()).isEqualTo("Sonoma County, CA");
        assertThat(result.getSeason()).isEqualTo("Fall harvest");
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/cider.jpg");
        assertThat(result.getTastingNotes()).containsExactly("crisp", "apple");
        assertThat(result.getBestFor()).containsExactly("cheese boards", "dinner parties");
        assertThat(result.getCreatedAt()).isEqualTo(OffsetDateTime.parse("2026-07-19T10:00:00Z"));

        ArgumentCaptor<ProductEntity> entityCaptor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getOrigin()).isEqualTo("Sonoma County, CA");
        assertThat(entityCaptor.getValue().getTastingNotes()).containsExactly("crisp", "apple");
        assertThat(entityCaptor.getValue().getFlavorProfile().getOverallProfile()).isEqualTo("Bright and refreshing");
    }

    @Test
    void createProduct_whenCategoryNotFound_throwsBusinessException() {
        RequestProductDTO request = buildRequestProductDto("99", "iPhone 16 Pro", "Latest Apple smartphone", 999.99, 50);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot find category with id: 99");

        verify(productRepository, never()).save(any());
    }

    @Test
    void getProductById_whenProductExists_returnsResponseDto() {
        CategoryEntity category = buildCategoryEntity(1L, "Smartphones", "Mobile devices");
        ProductEntity entity = buildProductEntity(1L, "iPhone 16 Pro", "Latest Apple smartphone", category, 999.99, 50);
        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));

        ResponseProductDTO result = productService.getProductById("1");

        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("iPhone 16 Pro");
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_whenProductNotFound_throwsBusinessException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById("99"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot find product with id: 99");

        verify(productRepository).findById(99L);
    }

    @Test
    void updateProduct_whenProductExists_updatesFieldsAndReturnsResponseDto() {
        RequestProductDTO request = buildRequestProductDto("2", "MacBook Pro M3", "High performance laptop", 1599.99, 20);
        CategoryEntity oldCategory = buildCategoryEntity(1L, "Smartphones", "Mobile devices");
        CategoryEntity newCategory = buildCategoryEntity(2L, "Laptops", "MacBooks");
        ProductEntity existing = buildProductEntity(1L, "iPhone 16 Pro", "Latest Apple smartphone", oldCategory, 999.99, 50);
        ProductEntity updated = buildProductEntity(1L, "MacBook Pro M3", "High performance laptop", newCategory, 1599.99, 20);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(existing)).thenReturn(updated);

        ResponseProductDTO result = productService.updateProduct("1", request);

        assertThat(result.getName()).isEqualTo("MacBook Pro M3");
        assertThat(result.getDescription()).isEqualTo("High performance laptop");
        assertThat(result.getCategory().getName()).isEqualTo("Laptops");
        verify(productRepository).save(existing);
    }

    @Test
    void updateProduct_whenProductNotFound_throwsBusinessException() {
        RequestProductDTO request = buildRequestProductDto("1", "MacBook Pro M3", "High performance laptop", 1599.99, 20);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct("99", request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot find product with id: 99");

        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_whenProductExistsAndHasNoOrderItems_deletesProduct() {
        CategoryEntity category = buildCategoryEntity(1L, "Smartphones", "Mobile devices");
        ProductEntity entity = buildProductEntity(1L, "iPhone 16 Pro", "Latest Apple smartphone", category, 999.99, 50);
        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(orderItemRepository.existsByProduct_Id(1L)).thenReturn(false);

        productService.deleteProduct("1");

        verify(productRepository).findById(1L);
        verify(orderItemRepository).existsByProduct_Id(1L);
        verify(productRepository).delete(entity);
    }

    @Test
    void deleteProduct_whenProductNotFound_throwsBusinessException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct("99"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot find product with id: 99");

        verify(orderItemRepository, never()).existsByProduct_Id(any());
        verify(productRepository, never()).delete(any());
    }

    @Test
    void deleteProduct_whenProductHasOrderItems_throwsBusinessException() {
        CategoryEntity category = buildCategoryEntity(1L, "Smartphones", "Mobile devices");
        ProductEntity entity = buildProductEntity(1L, "iPhone 16 Pro", "Latest Apple smartphone", category, 999.99, 50);
        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(orderItemRepository.existsByProduct_Id(1L)).thenReturn(true);

        assertThatThrownBy(() -> productService.deleteProduct("1"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot delete product with existing order items");

        verify(productRepository, never()).delete(any());
    }

    private RequestProductDTO buildRequestProductDto(
            String categoryId,
            String name,
            String description,
            Double price,
            Integer stockQuantity
    ) {
        RequestProductDTO request = new RequestProductDTO(categoryId, name, price, stockQuantity);
        request.setDescription(description);
        return request;
    }

    private CategoryEntity buildCategoryEntity(Long id, String name, String description) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setDescription(description);
        return entity;
    }

    private ProductEntity buildProductEntity(
            Long id,
            String name,
            String description,
            CategoryEntity category,
            Double price,
            Integer stockQuantity
    ) {
        ProductEntity entity = new ProductEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setDescription(description);
        entity.setCategory(category);
        entity.setCategoryName(category.getName());
        entity.setPrice(BigDecimal.valueOf(price));
        entity.setStockQuantity(stockQuantity);
        return entity;
    }
}
