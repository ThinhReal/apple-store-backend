package com.thinhreal.applestore.repository;

import com.thinhreal.applestore.model.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Category extends JpaRepository<CategoryEntity, Long> {
}
