package com.deviceCare.deviceCare.modules.repairs.model;

import com.deviceCare.deviceCare.modules.users.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "repair_order_accessories")
public class RepairOrderAccessory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_order_id", nullable = false)
    private RepairOrder repairOrder;

    @Column(name = "accessory_name", nullable = false, length = 100)
    private String accessoryName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "is_received", nullable = false)
    private boolean received = true;

    @Column(name = "condition_notes")
    private String conditionNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;
}