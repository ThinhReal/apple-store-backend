package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.api.model.RequestOrderItemDTO;
import com.thinhreal.applestore.api.model.ResponseOrderDTO;
import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.dto.order.CheckoutRequest;
import com.thinhreal.applestore.model.dto.order.CustomerOrderResponse;
import com.thinhreal.applestore.model.enums.UserRole;
import com.thinhreal.applestore.security.JwtService;
import com.thinhreal.applestore.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class CheckoutController {

    private final OrderService orderService;

    @GetMapping("/me")
    public ResponseEntity<List<CustomerOrderResponse>> getMyOrders(
            @AuthenticationPrincipal JwtService.AuthenticatedUser user
    ) {
        if (user == null) {
            throw new BusinessException("You must be signed in to view your orders");
        }

        if (user.role() != UserRole.CUSTOMER) {
            throw new BusinessException("Only customer accounts can view personal orders");
        }

        return ResponseEntity.ok(orderService.getOrdersForUser(user.id()));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ResponseOrderDTO> checkout(
            @Valid @RequestBody CheckoutRequest request,
            @AuthenticationPrincipal JwtService.AuthenticatedUser user
    ) {
        if (user == null) {
            throw new BusinessException("You must be signed in to place an order");
        }

        if (user.role() != UserRole.CUSTOMER) {
            throw new BusinessException("Only customer accounts can place orders");
        }

        List<RequestOrderItemDTO> orderItems = request.getOrderItems().stream()
                .map(item -> {
                    RequestOrderItemDTO dto = new RequestOrderItemDTO();
                    dto.setProductId(item.getProductId());
                    dto.setQuantity(item.getQuantity());
                    return dto;
                })
                .toList();

        ResponseOrderDTO response = orderService.createOrderForUser(user.id(), orderItems);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
