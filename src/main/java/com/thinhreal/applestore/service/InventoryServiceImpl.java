package com.thinhreal.applestore.service;

import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.entity.ProductEntity;
import com.thinhreal.applestore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;

    @Override
    public ProductEntity reserveStock(Long productId, int quantity) {
        validateQuantity(quantity);

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Cannot find product with id: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new BusinessException("Insufficient stock for product: " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        return productRepository.save(product);
    }

    @Override
    public void releaseStock(Long productId, int quantity) {
        validateQuantity(quantity);

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Cannot find product with id: " + productId));

        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("Quantity must be greater than zero");
        }
    }
}
