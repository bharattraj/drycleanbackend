package com.shop.service_shop_management.service;

import com.shop.service_shop_management.dto.request.CreateCustomerRequest;
import com.shop.service_shop_management.dto.request.UpdateCustomerAddressRequest;
import com.shop.service_shop_management.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CreateCustomerRequest request);

    List<CustomerResponse> getCustomersByPhone(String phone);

    List<CustomerResponse> searchCustomersByName(String name);

    List<CustomerResponse> searchCustomersByPhone(String phone);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse updateCustomerAddress(Long customerId, UpdateCustomerAddressRequest request);
}
