
package com.shop.service_shop_management.dto.response;

import com.shop.service_shop_management.enums.ProcessStatus;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class BillResponse {

    private Long id;
    private String billNumber;
    private CustomerResponse customer;
    private LocalDate billDate;
    private LocalDate expectedDeliveryDate;
    private ProcessStatus processStatus;
    private BigDecimal totalAmount;
    private OffsetDateTime createdAt;
    private OffsetDateTime deliveredAt;
    private List<ProductResponse> products;
}
