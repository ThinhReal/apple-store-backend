package com.thinhreal.applestore.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItemEntity> orderItems;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @Column(name = "category", nullable = false)
    private String categoryName;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "product_tasting_notes", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "note")
    @OrderColumn(name = "note_order")
    private List<String> tastingNotes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_best_for", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "use_case")
    @OrderColumn(name = "use_case_order")
    private List<String> bestFor = new ArrayList<>();

    private String origin;

    private String season;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "flavor_profile", columnDefinition = "json")
    private FlavorProfileValue flavorProfile;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @Column(name = "image_url")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
