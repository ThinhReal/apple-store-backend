package com.thinhreal.applestore.model.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class AdminOrderResponse {

    private final Long id;

    @JsonProperty("user_id")
    private final Long userId;

    @JsonProperty("customer_email")
    private final String customerEmail;

    @JsonProperty("customer_name")
    private final String customerName;

    @JsonProperty("order_date")
    private final OffsetDateTime orderDate;

    @JsonProperty("total_amount")
    private final Double totalAmount;

    private final String status;

    @JsonProperty("order_items")
    private final List<AdminOrderItemResponse> orderItems;
}
