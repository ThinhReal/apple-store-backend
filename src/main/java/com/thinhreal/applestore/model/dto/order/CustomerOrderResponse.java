package com.thinhreal.applestore.model.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class CustomerOrderResponse {

    private final Long id;

    @JsonProperty("order_date")
    private final OffsetDateTime orderDate;

    @JsonProperty("total_amount")
    private final Double totalAmount;

    private final String status;

    @JsonProperty("order_items")
    private final List<CustomerOrderItemResponse> orderItems;
}
