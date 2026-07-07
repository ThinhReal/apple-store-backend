package com.thinhreal.applestore.service;

import com.thinhreal.applestore.api.model.RequestOrderDTO;
import com.thinhreal.applestore.api.model.RequestOrderItemDTO;
import com.thinhreal.applestore.api.model.ResponseOrderDTO;
import com.thinhreal.applestore.api.model.ResponseOrderItemDTO;
import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.entity.OrderEntity;
import com.thinhreal.applestore.model.entity.OrderItemEntity;
import com.thinhreal.applestore.model.entity.ProductEntity;
import com.thinhreal.applestore.model.entity.UserEntity;
import com.thinhreal.applestore.model.enums.OrderStatus;
import com.thinhreal.applestore.repository.OrderRepository;
import com.thinhreal.applestore.repository.ProductRepository;
import com.thinhreal.applestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ResponseOrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ResponseOrderDTO createOrder(RequestOrderDTO requestOrderDTO) {
        validateOrderItems(requestOrderDTO.getOrderItems());

        UserEntity user = userRepository.findById(requestOrderDTO.getUserId())
                .orElseThrow(() -> new BusinessException("Cannot find user with id: " + requestOrderDTO.getUserId()));

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrder_date(ZonedDateTime.now());
        order.setOrderItems(new ArrayList<>());

        double totalAmount = buildOrderItems(order, requestOrderDTO.getOrderItems());
        order.setTotal_amount(totalAmount);

        OrderEntity saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public ResponseOrderDTO getOrderById(Long id) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find order with id: " + id));
        return toDto(entity);
    }

    @Transactional
    public ResponseOrderDTO updateOrder(Long id, RequestOrderDTO requestOrderDTO) {
        validateOrderItems(requestOrderDTO.getOrderItems());

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find order with id: " + id));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only pending orders can be updated");
        }

        UserEntity user = userRepository.findById(requestOrderDTO.getUserId())
                .orElseThrow(() -> new BusinessException("Cannot find user with id: " + requestOrderDTO.getUserId()));

        restoreStock(order.getOrderItems());
        order.getOrderItems().clear();
        order.setUser(user);

        double totalAmount = buildOrderItems(order, requestOrderDTO.getOrderItems());
        order.setTotal_amount(totalAmount);

        OrderEntity saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Transactional
    public void deleteOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find order with id: " + id));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only pending orders can be deleted");
        }

        restoreStock(order.getOrderItems());
        orderRepository.delete(order);
    }

    private void validateOrderItems(List<RequestOrderItemDTO> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new BusinessException("Order must contain at least one item");
        }
    }

    private double buildOrderItems(OrderEntity order, List<RequestOrderItemDTO> itemDtos) {
        double totalAmount = 0.0;

        for (RequestOrderItemDTO itemDto : itemDtos) {
            ProductEntity product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new BusinessException("Cannot find product with id: " + itemDto.getProductId()));

            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                throw new BusinessException("Quantity must be greater than zero");
            }

            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
            productRepository.save(product);

            double unitPrice = product.getPrice().doubleValue();
            totalAmount += unitPrice * itemDto.getQuantity();

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnit_price(unitPrice);
            order.getOrderItems().add(orderItem);
        }

        return totalAmount;
    }

    private void restoreStock(List<OrderItemEntity> orderItems) {
        if (orderItems == null) {
            return;
        }

        for (OrderItemEntity item : orderItems) {
            ProductEntity product = item.getProduct();
            if (product != null) {
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }
    }

    private ResponseOrderDTO toDto(OrderEntity entity) {
        ResponseOrderDTO dto = new ResponseOrderDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }
        if (entity.getOrder_date() != null) {
            dto.setOrderDate(entity.getOrder_date().toOffsetDateTime());
        }
        dto.setTotalAmount(entity.getTotal_amount());
        dto.setStatus(mapStatus(entity.getStatus()));

        if (entity.getOrderItems() != null) {
            dto.setOrderItems(entity.getOrderItems().stream()
                    .map(this::toItemDto)
                    .toList());
        }

        return dto;
    }

    private ResponseOrderItemDTO toItemDto(OrderItemEntity item) {
        ResponseOrderItemDTO dto = new ResponseOrderItemDTO();
        dto.setId(item.getId());
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
        }
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnit_price());
        return dto;
    }

    private ResponseOrderDTO.StatusEnum mapStatus(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case PENDING, PROCESSING -> ResponseOrderDTO.StatusEnum.PENDING;
            case SHIPPED -> ResponseOrderDTO.StatusEnum.SHIPPED;
            case DELIVERED -> ResponseOrderDTO.StatusEnum.DELIVERED;
            case CANCELLED -> ResponseOrderDTO.StatusEnum.PENDING;
        };
    }
}
