package com.thinhreal.applestore.model.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutItemRequest {

    @NotBlank
    @JsonProperty("product_id")
    private String productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
