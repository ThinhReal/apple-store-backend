package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.dto.order.AdminOrderResponse;
import com.thinhreal.applestore.model.dto.order.UpdateOrderStatusRequest;
import com.thinhreal.applestore.model.enums.OrderStatus;
import com.thinhreal.applestore.model.enums.UserRole;
import com.thinhreal.applestore.security.JwtService;
import com.thinhreal.applestore.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<AdminOrderResponse>> getAllOrders(
            @AuthenticationPrincipal JwtService.AuthenticatedUser user
    ) {
        assertAdmin(user);
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AdminOrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            @AuthenticationPrincipal JwtService.AuthenticatedUser user
    ) {
        assertAdmin(user);

        OrderStatus status;
        try {
            status = OrderStatus.valueOf(request.getStatus().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid order status: " + request.getStatus());
        }

        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    private void assertAdmin(JwtService.AuthenticatedUser user) {
        if (user == null) {
            throw new BusinessException("You must be signed in to manage orders");
        }

        if (user.role() != UserRole.ADMIN) {
            throw new BusinessException("Only admin accounts can manage orders");
        }
    }
}
