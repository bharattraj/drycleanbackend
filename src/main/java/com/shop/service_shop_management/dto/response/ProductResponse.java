package com.shop.service_shop_management.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductResponse {

    private Long id;

    private String category;

    private String productType;

    private Integer quantity;

    private BigDecimal amount;
}