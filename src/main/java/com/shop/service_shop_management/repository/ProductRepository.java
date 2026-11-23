
package com.shop.service_shop_management.repository;

import com.shop.service_shop_management.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findByBill_Id(Long billId);

    List<Product> findByProductTypeContainingIgnoreCase(String productType);
}
