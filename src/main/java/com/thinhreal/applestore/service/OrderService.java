package com.thinhreal.applestore.service;

import com.thinhreal.applestore.api.model.RequestOrderDTO;
import com.thinhreal.applestore.api.model.RequestOrderItemDTO;
import com.thinhreal.applestore.api.model.ResponseOrderDTO;
import com.thinhreal.applestore.model.dto.order.AdminOrderResponse;
import com.thinhreal.applestore.model.dto.order.CustomerOrderResponse;
import com.thinhreal.applestore.model.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    List<ResponseOrderDTO> getAllOrders();

    List<CustomerOrderResponse> getOrdersForUser(Long userId);

    List<AdminOrderResponse> getAllOrdersForAdmin();

    ResponseOrderDTO createOrder(RequestOrderDTO requestOrderDTO);

    ResponseOrderDTO createOrderForUser(Long userId, List<RequestOrderItemDTO> orderItems);

    ResponseOrderDTO getOrderById(Long id);

    ResponseOrderDTO updateOrder(Long id, RequestOrderDTO requestOrderDTO);

    AdminOrderResponse updateOrderStatus(Long id, OrderStatus status);

    void deleteOrder(Long id);
}
