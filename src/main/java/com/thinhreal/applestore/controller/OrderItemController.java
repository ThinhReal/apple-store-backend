package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.model.entity.OrderItemEntity;
import com.thinhreal.applestore.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/orderItems")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemRepository orderItemRepository;

    public List<OrderItemEntity> getAllIOrderItem(){
        return orderItemRepository.findAll();
    }
    public OrderItemEntity createOrderItem(@RequestBody OrderItemEntity orderItemEntity) {
        return orderItemRepository.save(orderItemEntity);
    }
}
