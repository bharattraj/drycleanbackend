package com.shop.service_shop_management.entity;

import com.shop.service_shop_management.enums.ProcessStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bill_process_history")
@Getter
@Setter
@NoArgsConstructor
public class BillProcessHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bill whose status was changed.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    // Status before this process step.
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private ProcessStatus previousStatus;

    // Status after successful verification.
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private ProcessStatus newStatus;

    @Column(name = "processed_at", nullable = false)
    private OffsetDateTime processedAt;

    @OneToMany(
            mappedBy = "processHistory",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BillProcessHistoryItem> items = new ArrayList<>();

    public void addItem(BillProcessHistoryItem item) {
        items.add(item);
        item.setProcessHistory(this);
    }

    @PrePersist
    protected void onCreate() {
        processedAt = OffsetDateTime.now();
    }
}