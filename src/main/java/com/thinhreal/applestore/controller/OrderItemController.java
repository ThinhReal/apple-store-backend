package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.api.OrderItemsApi;
import com.thinhreal.applestore.api.model.RequestCreateOrderItemDTO;
import com.thinhreal.applestore.api.model.RequestUpdateOrderItemDTO;
import com.thinhreal.applestore.api.model.ResponseOrderItemDTO;
import com.thinhreal.applestore.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderItemController implements OrderItemsApi {

    private final OrderItemService orderItemService;

    @Override
    public ResponseEntity<List<ResponseOrderItemDTO>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    @Override
    public ResponseEntity<ResponseOrderItemDTO> createOrderItem(RequestCreateOrderItemDTO requestCreateOrderItemDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderItemService.createOrderItem(requestCreateOrderItemDTO));
    }

    @Override
    public ResponseEntity<ResponseOrderItemDTO> getOrderItemById(Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    @Override
    public ResponseEntity<ResponseOrderItemDTO> updateOrderItem(Long id, RequestUpdateOrderItemDTO requestUpdateOrderItemDTO) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(id, requestUpdateOrderItemDTO));
    }

    @Override
    public ResponseEntity<Void> deleteOrderItem(Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
