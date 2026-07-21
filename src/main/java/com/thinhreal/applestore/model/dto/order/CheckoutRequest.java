package com.thinhreal.applestore.model.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CheckoutRequest {

    @NotEmpty
    @Valid
    @JsonProperty("order_items")
    private List<CheckoutItemRequest> orderItems;
}
