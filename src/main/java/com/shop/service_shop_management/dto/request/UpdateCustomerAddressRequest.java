
package com.shop.service_shop_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerAddressRequest {

    @NotBlank(message = "Address is required")
    private String address;
}
