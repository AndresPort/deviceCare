package com.deviceCare.deviceCare.modules.repairs.model;

import com.deviceCare.deviceCare.config.PostgreSQLEnumType;
import com.deviceCare.deviceCare.config.RepairStatusType;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairStatus;
import com.deviceCare.deviceCare.modules.users.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "repair_order_status_history")
@EntityListeners(AuditingEntityListener.class)
public class RepairOrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_order_id", nullable = false)
    private RepairOrder repairOrder;

    @Type(RepairStatusType.class)
    @Column(name = "old_status", columnDefinition = "repair_status")
    private RepairStatus oldStatus;

    @Type(RepairStatusType.class)
    @Column(name = "new_status", nullable = false, columnDefinition = "repair_status")
    private RepairStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(name = "notes")
    private String notes;

    @CreatedDate
    @Column(name = "changed_at", nullable = false, updatable = false)
    private Instant changedAt;
}