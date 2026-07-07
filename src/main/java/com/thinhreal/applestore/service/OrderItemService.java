package com.thinhreal.applestore.service;

import com.thinhreal.applestore.api.model.RequestCreateOrderItemDTO;
import com.thinhreal.applestore.api.model.RequestUpdateOrderItemDTO;
import com.thinhreal.applestore.api.model.ResponseOrderItemDTO;
import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.entity.OrderEntity;
import com.thinhreal.applestore.model.entity.OrderItemEntity;
import com.thinhreal.applestore.model.entity.ProductEntity;
import com.thinhreal.applestore.model.enums.OrderStatus;
import com.thinhreal.applestore.repository.OrderItemRepository;
import com.thinhreal.applestore.repository.OrderRepository;
import com.thinhreal.applestore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ResponseOrderItemDTO> getAllOrderItems() {
        return orderItemRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ResponseOrderItemDTO createOrderItem(RequestCreateOrderItemDTO request) {
        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException("Cannot find order with id: " + request.getOrderId()));
        ensurePendingOrder(order);

        ProductEntity product = resolveProductWithStock(request.getProductId(), request.getQuantity());

        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(request.getQuantity());
        orderItem.setUnit_price(product.getPrice().doubleValue());

        if (order.getOrderItems() == null) {
            order.setOrderItems(new ArrayList<>());
        }
        order.getOrderItems().add(orderItem);

        OrderItemEntity saved = orderItemRepository.save(orderItem);
        recalculateOrderTotal(order);
        orderRepository.save(order);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public ResponseOrderItemDTO getOrderItemById(Long id) {
        OrderItemEntity entity = orderItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find order item with id: " + id));
        return toDto(entity);
    }

    @Transactional
    public ResponseOrderItemDTO updateOrderItem(Long id, RequestUpdateOrderItemDTO request) {
        OrderItemEntity orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find order item with id: " + id));

        OrderEntity order = orderItem.getOrder();
        ensurePendingOrder(order);

        restoreStock(orderItem);

        ProductEntity product = resolveProductWithStock(request.getProductId(), request.getQuantity());
        orderItem.setProduct(product);
        orderItem.setQuantity(request.getQuantity());
        orderItem.setUnit_price(product.getPrice().doubleValue());

        OrderItemEntity saved = orderItemRepository.save(orderItem);
        recalculateOrderTotal(order);
        orderRepository.save(order);

        return toDto(saved);
    }

    @Transactional
    public void deleteOrderItem(Long id) {
        OrderItemEntity orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find order item with id: " + id));

        OrderEntity order = orderItem.getOrder();
        ensurePendingOrder(order);

        restoreStock(orderItem);
        order.getOrderItems().remove(orderItem);
        orderItemRepository.delete(orderItem);
        recalculateOrderTotal(order);
        orderRepository.save(order);
    }

    private void ensurePendingOrder(OrderEntity order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Order items can only be modified when order status is PENDING");
        }
    }

    private ProductEntity resolveProductWithStock(Long productId, Integer quantity) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Cannot find product with id: " + productId));

        if (quantity == null || quantity <= 0) {
            throw new BusinessException("Quantity must be greater than zero");
        }

        if (product.getStockQuantity() < quantity) {
            throw new BusinessException("Insufficient stock for product: " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
        return product;
    }

    private void restoreStock(OrderItemEntity orderItem) {
        ProductEntity product = orderItem.getProduct();
        if (product != null && orderItem.getQuantity() != null) {
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }
    }

    private void recalculateOrderTotal(OrderEntity order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            order.setTotal_amount(0.0);
            return;
        }

        double total = order.getOrderItems().stream()
                .mapToDouble(item -> item.getUnit_price() * item.getQuantity())
                .sum();
        order.setTotal_amount(total);
    }

    private ResponseOrderItemDTO toDto(OrderItemEntity entity) {
        ResponseOrderItemDTO dto = new ResponseOrderItemDTO();
        dto.setId(entity.getId());
        if (entity.getProduct() != null) {
            dto.setProductId(entity.getProduct().getId());
        }
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnit_price());
        return dto;
    }
}
