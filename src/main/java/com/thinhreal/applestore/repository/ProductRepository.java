package com.thinhreal.applestore.repository;

import com.thinhreal.applestore.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // This interface now has save(), findAll(), findById(), delete() automatically!
}