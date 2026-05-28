package com.deviceCare.deviceCare.modules.repairs.model;

import com.deviceCare.deviceCare.common.base.BaseEntity;
import com.deviceCare.deviceCare.config.DamageLevelType;
import com.deviceCare.deviceCare.config.PostgreSQLEnumType;
import com.deviceCare.deviceCare.config.RepairPriorityType;
import com.deviceCare.deviceCare.config.RepairStatusType;
import com.deviceCare.deviceCare.modules.devices.model.Device;
import com.deviceCare.deviceCare.modules.repairs.model.enums.DamageLevel;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairPriority;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairStatus;
import com.deviceCare.deviceCare.modules.users.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "repair_orders")
public class RepairOrder extends BaseEntity {

    @Column(name = "order_number", unique = true, insertable = false, updatable = false)
    private Long orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by")
    private User receivedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_technician")
    private User assignedTechnician;

    @Type(RepairStatusType.class)
    @Column(name = "status", nullable = false, columnDefinition = "repair_status")
    private RepairStatus status = RepairStatus.RECEIVED;

    @Type(RepairPriorityType.class)
    @Column(name = "priority", nullable = false, columnDefinition = "repair_priority")
    private RepairPriority priority = RepairPriority.NORMAL;

    @Type(DamageLevelType.class)
    @Column(name = "damage_level", columnDefinition = "damage_level")
    private DamageLevel damageLevel;

    @Column(name = "client_problem", nullable = false)
    private String clientProblem;

    @Column(name = "technical_diagnosis")
    private String technicalDiagnosis;

    @Column(name = "client_approved", nullable = false)
    private boolean clientApproved = false;

    @Column(name = "client_approved_at")
    private Instant clientApprovedAt;

    @Column(name = "client_approval_notes")
    private String clientApprovalNotes;

    @Column(name = "estimated_cost", precision = 12, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "labor_cost", precision = 12, scale = 2)
    private BigDecimal laborCost = BigDecimal.ZERO;

    @Column(name = "estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @OneToMany(mappedBy = "repairOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepairOrderStatusHistory> statusHistory = new ArrayList<>();

    @OneToMany(mappedBy = "repairOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepairOrderFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "repairOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepairOrderAccessory> accessories = new ArrayList<>();

    @OneToMany(mappedBy = "repairOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepairOrderPart> parts = new ArrayList<>();
}