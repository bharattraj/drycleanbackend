
package com.shop.service_shop_management.controller;

import com.shop.service_shop_management.dto.request.CreateCustomerRequest;
import com.shop.service_shop_management.dto.request.UpdateCustomerAddressRequest;
import com.shop.service_shop_management.dto.response.CustomerResponse;
import com.shop.service_shop_management.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCustomer(request));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {

        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<List<CustomerResponse>> getCustomersByPhone(
            @PathVariable String phone) {

        return ResponseEntity.ok(
                customerService.getCustomersByPhone(phone));
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<CustomerResponse>> searchByName(
            @RequestParam String name) {

        return ResponseEntity.ok(
                customerService.searchCustomersByName(name));
    }

    @GetMapping("/search/phone")
    public ResponseEntity<List<CustomerResponse>> searchByPhone(
            @RequestParam String phone) {

        return ResponseEntity.ok(
                customerService.searchCustomersByPhone(phone));
    }

    @PatchMapping("/{customerId}/address")
    public ResponseEntity<CustomerResponse> updateCustomerAddress(
            @PathVariable Long customerId,
            @Valid @RequestBody UpdateCustomerAddressRequest request) {

        return ResponseEntity.ok(
                customerService.updateCustomerAddress(customerId, request));
    }
}
