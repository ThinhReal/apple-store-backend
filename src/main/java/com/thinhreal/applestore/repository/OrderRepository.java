package com.thinhreal.applestore.repository;

import com.thinhreal.applestore.model.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query("""
            SELECT DISTINCT o FROM OrderEntity o
            LEFT JOIN FETCH o.orderItems items
            LEFT JOIN FETCH items.product
            WHERE o.user.id = :userId
            ORDER BY o.order_date DESC
            """)
    List<OrderEntity> findAllByUserIdWithItems(@Param("userId") Long userId);

    @Query("""
            SELECT DISTINCT o FROM OrderEntity o
            LEFT JOIN FETCH o.user
            LEFT JOIN FETCH o.orderItems items
            LEFT JOIN FETCH items.product
            ORDER BY o.order_date DESC
            """)
    List<OrderEntity> findAllWithItemsAndUser();

    @Query("""
            SELECT o FROM OrderEntity o
            LEFT JOIN FETCH o.user
            LEFT JOIN FETCH o.orderItems items
            LEFT JOIN FETCH items.product
            WHERE o.id = :orderId
            """)
    Optional<OrderEntity> findByIdWithItems(@Param("orderId") Long orderId);
}
