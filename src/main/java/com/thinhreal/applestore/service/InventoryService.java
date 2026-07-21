package com.thinhreal.applestore.service;

import com.thinhreal.applestore.model.entity.ProductEntity;

public interface InventoryService {

    ProductEntity reserveStock(Long productId, int quantity);

    void releaseStock(Long productId, int quantity);
}
