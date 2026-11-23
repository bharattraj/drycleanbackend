package com.shop.service_shop_management.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductQuantityVerificationRequest {

    // Existing product ID from the bill.
    @NotNull
    private Long productId;

    // Actual quantity counted by the staff.
    @NotNull
    @Min(0)
    private Integer verifiedQuantity;
}