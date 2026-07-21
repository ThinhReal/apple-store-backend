package com.thinhreal.applestore.model.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminOrderItemResponse {

    private final Long id;

    @JsonProperty("product_id")
    private final String productId;

    @JsonProperty("product_name")
    private final String productName;

    private final Integer quantity;

    @JsonProperty("unit_price")
    private final Double unitPrice;
}
