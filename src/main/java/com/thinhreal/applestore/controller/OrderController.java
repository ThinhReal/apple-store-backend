package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.api.OrdersApi;
import com.thinhreal.applestore.api.model.RequestOrderDTO;
import com.thinhreal.applestore.api.model.ResponseOrderDTO;
import com.thinhreal.applestore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrdersApi {

    private final OrderService orderService;

    @Override
    public ResponseEntity<List<ResponseOrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Override
    public ResponseEntity<ResponseOrderDTO> createOrder(RequestOrderDTO requestOrderDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(requestOrderDTO));
    }

    @Override
    public ResponseEntity<ResponseOrderDTO> getOrderById(Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Override
    public ResponseEntity<ResponseOrderDTO> updateOrder(Long id, RequestOrderDTO requestOrderDTO) {
        return ResponseEntity.ok(orderService.updateOrder(id, requestOrderDTO));
    }

    @Override
    public ResponseEntity<Void> deleteOrder(Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
