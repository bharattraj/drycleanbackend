package com.shop.service_shop_management.repository;

import com.shop.service_shop_management.entity.BillProcessHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillProcessHistoryRepository
        extends JpaRepository<BillProcessHistory, Long> {

    // Get a bill's complete process history, newest step first.
    List<BillProcessHistory>
    findByBill_BillNumberOrderByProcessedAtDesc(String billNumber);
}