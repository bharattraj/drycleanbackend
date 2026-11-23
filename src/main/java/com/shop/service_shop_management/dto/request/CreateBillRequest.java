
package com.shop.service_shop_management.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateBillRequest {

    // Physical bill number entered by shopkeeper
    @NotBlank(message = "Bill number is required")
    @Size(max = 30, message = "Bill number cannot exceed 30 characters")
    private String billNumber;

    // Existing customer: frontend sends this ID
    private Long customerId;

    // New customer: frontend sends customer details
    @Valid
    private CreateCustomerRequest customer;

    @NotNull(message = "Expected delivery date is required")
    private LocalDate expectedDeliveryDate;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.00", message = "Total amount cannot be negative")
    private BigDecimal totalAmount;

    @NotEmpty(message = "At least one product is required")
    @Valid
    private List<CreateProductRequest> products;
}
