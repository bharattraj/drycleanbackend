package com.shop.service_shop_management.controller;

import com.shop.service_shop_management.dto.request.ConfirmProcessRequest;
import com.shop.service_shop_management.dto.request.CreateBillRequest;
import com.shop.service_shop_management.dto.request.UpdateBillStatusRequest;
import com.shop.service_shop_management.dto.response.BillProcessHistoryResponse;
import com.shop.service_shop_management.dto.response.BillResponse;
import com.shop.service_shop_management.enums.ProcessStatus;
import com.shop.service_shop_management.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BillController {

    private final BillService billService;

    @PostMapping
    public ResponseEntity<BillResponse> createBill(@Valid @RequestBody CreateBillRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(billService.createBill(request));
    }

    // Fetch a bill using its physical bill number.
    @GetMapping("/{billNumber}")
    public ResponseEntity<BillResponse> getBillByNumber(@PathVariable String billNumber) {

        return ResponseEntity.ok(billService.getBillByNumber(billNumber));
    }

    // Fetch a bill using its database ID from the QR code.
    @GetMapping("/by-id/{billId}")
    public ResponseEntity<BillResponse> getBillById(@PathVariable Long billId) {

        return ResponseEntity.ok(billService.getBillById(billId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BillResponse>> getBillsByCustomer(@PathVariable Long customerId) {

        return ResponseEntity.ok(billService.getBillsByCustomerId(customerId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BillResponse>> getBillsByStatus(@PathVariable ProcessStatus status) {

        return ResponseEntity.ok(billService.getBillsByStatus(status));
    }

    // Fetch all bills which are not yet delivered.
    @GetMapping("/pending")
    public ResponseEntity<List<BillResponse>> getPendingBills() {

        return ResponseEntity.ok(billService.getPendingBills());
    }

    @PatchMapping("/{billNumber}/status")
    public ResponseEntity<BillResponse> updateBillStatus(@PathVariable String billNumber, @Valid @RequestBody UpdateBillStatusRequest request) {

        return ResponseEntity.ok(billService.updateBillStatus(billNumber, request));
    }

    // Confirm product quantities and move the bill to its next stage.
    @PostMapping("/{billNumber}/process")
    public ResponseEntity<BillResponse> confirmProcess(@PathVariable String billNumber, @Valid @RequestBody ConfirmProcessRequest request) {

        return ResponseEntity.ok(billService.confirmProcess(billNumber, request));
    }

    // Fetch the complete processing history of a bill.
    @GetMapping("/{billNumber}/process-history")
    public ResponseEntity<List<BillProcessHistoryResponse>> getProcessHistory(@PathVariable String billNumber) {

        return ResponseEntity.ok(billService.getProcessHistory(billNumber));
    }
}
