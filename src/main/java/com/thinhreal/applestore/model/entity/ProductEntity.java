package com.thinhreal.applestore.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data // Lombok: Generates Getters, Setters, toString
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "iPhone 15 Pro"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price; // BigDecimal is highly recommended for exact currency calculations!

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private String category; // We can upgrade this to an Enum later
}