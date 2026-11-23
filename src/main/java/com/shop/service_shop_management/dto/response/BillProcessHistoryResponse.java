package com.shop.service_shop_management.dto.response;

import com.shop.service_shop_management.enums.ProcessStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class BillProcessHistoryResponse {

    private Long id;
    private ProcessStatus previousStatus;
    private ProcessStatus newStatus;
    private OffsetDateTime processedAt;
    private List<BillProcessHistoryItemResponse> items;
}