
package com.shop.service_shop_management.repository;

import com.shop.service_shop_management.entity.Bill;
import com.shop.service_shop_management.enums.ProcessStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByBillNumber(String billNumber);

    boolean existsByBillNumber(String billNumber);

    List<Bill> findByCustomer_Id(Long customerId);

    List<Bill> findByProcessStatus(ProcessStatus processStatus);

    // Fetch all bills except the selected process status.
    List<Bill> findByProcessStatusNot(ProcessStatus processStatus);

    List<Bill> findByBillDate(LocalDate billDate);

    List<Bill> findByExpectedDeliveryDate(LocalDate expectedDeliveryDate);

    List<Bill> findByExpectedDeliveryDateBeforeAndProcessStatusNot(
            LocalDate date, ProcessStatus status);

    long countByBillDate(LocalDate billDate);
}
