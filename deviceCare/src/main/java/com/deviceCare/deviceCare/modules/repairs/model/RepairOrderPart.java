package com.deviceCare.deviceCare.modules.repairs.model;

import com.deviceCare.deviceCare.config.PartSourceTypeEnum;
import com.deviceCare.deviceCare.config.PostgreSQLEnumType;
import com.deviceCare.deviceCare.modules.repairs.model.enums.PartSourceType;
import com.deviceCare.deviceCare.modules.users.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "repair_order_parts")
public class RepairOrderPart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_order_id", nullable = false)
    private RepairOrder repairOrder;

    @Column(name = "custom_part_name", length = 255)
    private String customPartName;

    @Type(PartSourceTypeEnum.class)
    @Column(name = "source_type", nullable = false, columnDefinition = "part_source_type")
    private PartSourceType sourceType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;
}