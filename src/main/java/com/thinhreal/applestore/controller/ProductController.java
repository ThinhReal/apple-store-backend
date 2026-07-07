package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.api.ProductsApi;
import com.thinhreal.applestore.api.model.RequestProductDTO;
import com.thinhreal.applestore.api.model.ResponseProductDTO;
import com.thinhreal.applestore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductsApi {

    private final ProductService productService;

    @Override
    public ResponseEntity<List<ResponseProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Override
    public ResponseEntity<ResponseProductDTO> createProduct(RequestProductDTO requestProductDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(requestProductDTO));
    }

    @Override
    public ResponseEntity<ResponseProductDTO> getProductById(Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Override
    public ResponseEntity<ResponseProductDTO> updateProduct(Long id, RequestProductDTO requestProductDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, requestProductDTO));
    }

    @Override
    public ResponseEntity<Void> deleteProduct(Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
