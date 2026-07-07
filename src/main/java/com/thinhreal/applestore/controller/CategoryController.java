package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.api.CategoriesApi;
import com.thinhreal.applestore.api.model.RequestCategoryDTO;
import com.thinhreal.applestore.api.model.ResponseCategoryDTO;
import com.thinhreal.applestore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoriesApi {

    private final CategoryService categoryService;

    @Override
    public ResponseEntity<List<ResponseCategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Override
    public ResponseEntity<ResponseCategoryDTO> createCategory(RequestCategoryDTO requestCategoryDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(requestCategoryDTO));
    }

    @Override
    public ResponseEntity<ResponseCategoryDTO> getCategoryById(Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Override
    public ResponseEntity<ResponseCategoryDTO> updateCategory(Long id, RequestCategoryDTO requestCategoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, requestCategoryDTO));
    }

    @Override
    public ResponseEntity<Void> deleteCategory(Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
