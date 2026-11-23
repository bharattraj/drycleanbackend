package com.shop.service_shop_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bill_process_history_items")
@Getter
@Setter
@NoArgsConstructor
public class BillProcessHistoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_history_id", nullable = false)
    private BillProcessHistory processHistory;

    // Reference to the original product in the bill.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "expected_quantity", nullable = false)
    private Integer expectedQuantity;

    @Column(name = "verified_quantity", nullable = false)
    private Integer verifiedQuantity;
}