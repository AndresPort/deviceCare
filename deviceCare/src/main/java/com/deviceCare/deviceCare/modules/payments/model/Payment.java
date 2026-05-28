package com.deviceCare.deviceCare.modules.payments.model;

import com.deviceCare.deviceCare.config.PaymentMethodType;
import com.deviceCare.deviceCare.config.PaymentStatusType;
import com.deviceCare.deviceCare.config.PostgreSQLEnumType;
import com.deviceCare.deviceCare.modules.payments.model.enums.PaymentMethod;
import com.deviceCare.deviceCare.modules.payments.model.enums.PaymentStatus;
import com.deviceCare.deviceCare.modules.repairs.model.RepairOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_order_id", nullable = false)
    private RepairOrder repairOrder;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Type(PaymentMethodType.class)
    @Column(name = "payment_method", nullable = false, columnDefinition = "payment_method_type")
    private PaymentMethod paymentMethod;

    @Type(PaymentStatusType.class)
    @Column(name = "payment_status", nullable = false, columnDefinition = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "transaction_reference", length = 150)
    private String transactionReference;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}