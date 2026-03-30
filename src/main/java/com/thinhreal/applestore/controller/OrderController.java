package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.model.entity.OrderEntity;
import com.thinhreal.applestore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;

    @GetMapping
    public List<OrderEntity> getAllOrder(){
        return orderRepository.findAll();
    }

    @PostMapping
    public OrderEntity createOrder (@RequestBody OrderEntity orderEntity){
        return orderRepository.save(orderEntity);
    }

}
