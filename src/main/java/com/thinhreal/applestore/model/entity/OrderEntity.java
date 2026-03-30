package com.thinhreal.applestore.model.entity;
 import java.time.ZonedDateTime;
 import java.util.List;

 import com.thinhreal.applestore.model.enums.OrderStatus;
 import jakarta.persistence.*;
 import lombok.AllArgsConstructor;
 import lombok.Getter;
 import lombok.NoArgsConstructor;
 import lombok.Setter;

@Entity
@Table(name="orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserEntity user;

    //Parent of OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItemEntity> orderItems;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private ZonedDateTime order_date;
    private Double total_amount;
}

