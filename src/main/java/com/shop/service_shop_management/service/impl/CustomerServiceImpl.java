
package com.shop.service_shop_management.service.impl;

import com.shop.service_shop_management.dto.request.CreateCustomerRequest;
import com.shop.service_shop_management.dto.request.UpdateCustomerAddressRequest;
import com.shop.service_shop_management.dto.response.CustomerResponse;
import com.shop.service_shop_management.entity.Customer;
import com.shop.service_shop_management.repository.CustomerRepository;
import com.shop.service_shop_management.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {

        Customer customer = new Customer();

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        Customer savedCustomer = customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getCustomersByPhone(String phone) {

        return customerRepository.findAllByPhone(phone)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomersByName(String name) {

        return customerRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomersByPhone(String phone) {

        return customerRepository.findByPhoneContaining(phone)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private CustomerResponse mapToResponse(Customer customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getAddress()
        );
    }

    @Override
    public CustomerResponse updateCustomerAddress(
            Long customerId,
            UpdateCustomerAddressRequest request) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found with ID: " + customerId));

        customer.setAddress(request.getAddress());

        Customer updatedCustomer = customerRepository.save(customer);

        return mapToResponse(updatedCustomer);
    }
    
    customer.setWhatsappOptIn(Boolean.TRUE.equals(request.getWhatsappOptIn()));
}
