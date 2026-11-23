package com.shop.service_shop_management.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BillProcessHistoryItemResponse {

    private Long productId;
    private String category;
    private String productType;
    private Integer expectedQuantity;
    private Integer verifiedQuantity;
}