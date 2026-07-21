package com.thinhreal.applestore.service;

import com.thinhreal.applestore.api.model.RequestOrderDTO;
import com.thinhreal.applestore.api.model.RequestOrderItemDTO;
import com.thinhreal.applestore.api.model.ResponseOrderDTO;
import com.thinhreal.applestore.api.model.ResponseOrderItemDTO;
import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.dto.order.AdminOrderItemResponse;
import com.thinhreal.applestore.model.dto.order.AdminOrderResponse;
import com.thinhreal.applestore.model.dto.order.CustomerOrderItemResponse;
import com.thinhreal.applestore.model.dto.order.CustomerOrderResponse;
import com.thinhreal.applestore.model.entity.OrderEntity;
import com.thinhreal.applestore.model.entity.OrderItemEntity;
import com.thinhreal.applestore.model.entity.ProductEntity;
import com.thinhreal.applestore.model.entity.UserEntity;
import com.thinhreal.applestore.model.enums.OrderStatus;
import com.thinhreal.applestore.repository.OrderRepository;
import com.thinhreal.applestore.repository.UserRepository;
import com.thinhreal.applestore.util.ApiIdConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;

    @Override
    public List<ResponseOrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<CustomerOrderResponse> getOrdersForUser(Long userId) {
        return orderRepository.findAllByUserIdWithItems(userId).stream()
                .map(this::toCustomerOrderDto)
                .toList();
    }

    @Override
    public List<AdminOrderResponse> getAllOrdersForAdmin() {
        return orderRepository.findAllWithItemsAndUser().stream()
                .map(this::toAdminOrderDto)
                .toList();
    }

    @Override
    @Transactional
    public AdminOrderResponse updateOrderStatus(Long id, OrderStatus newStatus) {
        OrderEntity order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new BusinessException("Cannot find order with id: " + id));

        OrderStatus currentStatus = order.getStatus();
        if (currentStatus == newStatus) {
            return toAdminOrderDto(order);
        }

        if (newStatus == OrderStatus.CANCELLED && currentStatus != OrderStatus.CANCELLED) {
            restoreStock(order.getOrderItems());
        } else if (currentStatus == OrderStatus.CANCELLED && newStatus != OrderStatus.CANCELLED) {
            reserveStockForOrder(order.getOrderItems());
        }

        order.setStatus(newStatus);
        OrderEntity saved = orderRepository.save(order);
        return toAdminOrderDto(saved);
    }

    @Override
    @Transactional
    public ResponseOrderDTO createOrder(RequestOrderDTO requestOrderDTO) {
        validateOrderItems(requestOrderDTO.getOrderItems());

        UserEntity user = userRepository.findById(requestOrderDTO.getUserId())
                .orElseThrow(() -> new BusinessException("Cannot find user with id: " + requestOrderDTO.getUserId()));

        return createOrderForUser(user.getId(), requestOrderDTO.getOrderItems());
    }

    @Override
    @Transactional
    public ResponseOrderDTO createOrderForUser(Long userId, List<RequestOrderItemDTO> orderItems) {
        validateOrderItems(orderItems);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Cannot find user with id: " + userId));

        Map<Long, Integer> quantitiesByProductId = mergeOrderItemQuantities(orderItems);
        OrderEntity order = initializePendingOrder(user);

        double totalAmount = 0.0;

        for (Map.Entry<Long, Integer> entry : quantitiesByProductId.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();

            ProductEntity product = inventoryService.reserveStock(productId, quantity);
            double unitPrice = product.getPrice().doubleValue();
            totalAmount += unitPrice * quantity;

            order.getOrderItems().add(buildOrderItem(order, product, quantity, unitPrice));
        }

        order.setTotal_amount(totalAmount);
        OrderEntity savedOrder = orderRepository.save(order);
        return toDto(savedOrder);
    }

    @Override
    public ResponseOrderDTO getOrderById(Long id) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find order with id: " + id));
        return toDto(entity);
    }

    @Override
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

        Map<Long, Integer> quantitiesByProductId = mergeOrderItemQuantities(requestOrderDTO.getOrderItems());
        double totalAmount = 0.0;

        for (Map.Entry<Long, Integer> entry : quantitiesByProductId.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();

            ProductEntity product = inventoryService.reserveStock(productId, quantity);
            double unitPrice = product.getPrice().doubleValue();
            totalAmount += unitPrice * quantity;

            order.getOrderItems().add(buildOrderItem(order, product, quantity, unitPrice));
        }

        order.setTotal_amount(totalAmount);
        OrderEntity saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
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

    private OrderEntity initializePendingOrder(UserEntity user) {
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrder_date(ZonedDateTime.now());
        order.setOrderItems(new ArrayList<>());
        return order;
    }

    private Map<Long, Integer> mergeOrderItemQuantities(List<RequestOrderItemDTO> orderItems) {
        Map<Long, Integer> quantitiesByProductId = new LinkedHashMap<>();

        for (RequestOrderItemDTO itemDto : orderItems) {
            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                throw new BusinessException("Quantity must be greater than zero");
            }

            Long productId = ApiIdConverter.parseLongId(itemDto.getProductId(), "product");
            quantitiesByProductId.merge(productId, itemDto.getQuantity(), Integer::sum);
        }

        return quantitiesByProductId;
    }

    private OrderItemEntity buildOrderItem(
            OrderEntity order,
            ProductEntity product,
            int quantity,
            double unitPrice
    ) {
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setUnit_price(unitPrice);
        return orderItem;
    }

    private void validateOrderItems(List<RequestOrderItemDTO> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new BusinessException("Order must contain at least one item");
        }
    }

    private void restoreStock(List<OrderItemEntity> orderItems) {
        if (orderItems == null) {
            return;
        }

        for (OrderItemEntity item : orderItems) {
            ProductEntity product = item.getProduct();
            if (product != null && item.getQuantity() != null) {
                inventoryService.releaseStock(product.getId(), item.getQuantity());
            }
        }
    }

    private void reserveStockForOrder(List<OrderItemEntity> orderItems) {
        if (orderItems == null) {
            return;
        }

        for (OrderItemEntity item : orderItems) {
            ProductEntity product = item.getProduct();
            if (product != null && item.getQuantity() != null) {
                inventoryService.reserveStock(product.getId(), item.getQuantity());
            }
        }
    }

    private AdminOrderResponse toAdminOrderDto(OrderEntity entity) {
        UserEntity user = entity.getUser();
        List<AdminOrderItemResponse> items = entity.getOrderItems() == null
                ? List.of()
                : entity.getOrderItems().stream()
                        .map(item -> AdminOrderItemResponse.builder()
                                .id(item.getId())
                                .productId(item.getProduct() != null
                                        ? ApiIdConverter.toApiId(item.getProduct().getId())
                                        : null)
                                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnit_price())
                                .build())
                        .toList();

        return AdminOrderResponse.builder()
                .id(entity.getId())
                .userId(user != null ? user.getId() : null)
                .customerEmail(user != null ? user.getEmail() : null)
                .customerName(formatCustomerName(user))
                .orderDate(entity.getOrder_date() != null ? entity.getOrder_date().toOffsetDateTime() : null)
                .totalAmount(entity.getTotal_amount())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .orderItems(items)
                .build();
    }

    private String formatCustomerName(UserEntity user) {
        if (user == null) {
            return null;
        }

        String firstName = user.getFirst_name() != null ? user.getFirst_name().trim() : "";
        String lastName = user.getLast_name() != null ? user.getLast_name().trim() : "";
        String fullName = (firstName + " " + lastName).trim();

        return fullName.isEmpty() ? null : fullName;
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
            dto.setProductId(ApiIdConverter.toApiId(item.getProduct().getId()));
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

    private CustomerOrderResponse toCustomerOrderDto(OrderEntity entity) {
        List<CustomerOrderItemResponse> items = entity.getOrderItems() == null
                ? List.of()
                : entity.getOrderItems().stream()
                        .map(item -> CustomerOrderItemResponse.builder()
                                .id(item.getId())
                                .productId(item.getProduct() != null
                                        ? ApiIdConverter.toApiId(item.getProduct().getId())
                                        : null)
                                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnit_price())
                                .build())
                        .toList();

        return CustomerOrderResponse.builder()
                .id(entity.getId())
                .orderDate(entity.getOrder_date() != null ? entity.getOrder_date().toOffsetDateTime() : null)
                .totalAmount(entity.getTotal_amount())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .orderItems(items)
                .build();
    }
}
