
package com.shop.service_shop_management.service;

import com.shop.service_shop_management.dto.request.ConfirmProcessRequest;
import com.shop.service_shop_management.dto.request.CreateBillRequest;
import com.shop.service_shop_management.dto.request.UpdateBillStatusRequest;
import com.shop.service_shop_management.dto.response.BillProcessHistoryResponse;
import com.shop.service_shop_management.dto.response.BillResponse;
import com.shop.service_shop_management.enums.ProcessStatus;

import java.util.List;

public interface BillService {

    BillResponse createBill(CreateBillRequest request);

    BillResponse getBillByNumber(String billNumber);

    // Fetch a bill using its unique database ID (QR code lookup).
    BillResponse getBillById(Long billId);

    List<BillResponse> getBillsByCustomerId(Long customerId);

    List<BillResponse> getBillsByStatus(ProcessStatus status);

    // Returns all bills that have not yet been delivered.
    List<BillResponse> getPendingBills();

    BillResponse updateBillStatus(
            String billNumber,
            UpdateBillStatusRequest request
    );

    // Verify product quantities and move the bill to its next status.
    BillResponse confirmProcess(
            String billNumber,
            ConfirmProcessRequest request
    );

    // Get the complete process history of a bill.
    List<BillProcessHistoryResponse> getProcessHistory(String billNumber);
}
