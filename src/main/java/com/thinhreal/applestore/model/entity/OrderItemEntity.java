package com.thinhreal.applestore.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="OrderItem")
@NoArgsConstructor
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Children of Order
    @ManyToOne
    @JoinColumn(name="order_id", referencedColumnName = "id")
    private OrderEntity order;

    //Children of Product
    @ManyToOne
    @JoinColumn(name="product_id")
    private ProductEntity product;

    private Integer quantity;
    private Double unit_price;
}
