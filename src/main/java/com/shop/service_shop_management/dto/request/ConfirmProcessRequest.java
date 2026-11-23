package com.shop.service_shop_management.dto.request;

import com.shop.service_shop_management.enums.ProcessStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfirmProcessRequest {

    // The next status the staff wants to move the bill to.
    @NotNull
    private ProcessStatus targetStatus;

    // Quantity verification for every product in the bill.
    @NotEmpty
    private List<@Valid ProductQuantityVerificationRequest> products;
}