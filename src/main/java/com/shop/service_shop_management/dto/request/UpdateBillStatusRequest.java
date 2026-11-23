
package com.shop.service_shop_management.dto.request;

import com.shop.service_shop_management.enums.ProcessStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBillStatusRequest {

    @NotNull(message = "Status is required")
    private ProcessStatus status;
}
