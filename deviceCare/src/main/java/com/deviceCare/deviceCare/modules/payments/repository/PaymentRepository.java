package com.deviceCare.deviceCare.modules.payments.repository;

import com.deviceCare.deviceCare.modules.payments.model.Payment;
import com.deviceCare.deviceCare.modules.payments.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findAllByRepairOrderIdAndDeletedAtIsNull(UUID repairOrderId);

    Optional<Payment> findByIdAndDeletedAtIsNull(UUID id);

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.repairOrder.id = :repairOrderId
        AND p.paymentStatus = :status
        AND p.deletedAt IS NULL
    """)
    BigDecimal sumAmountByRepairOrderIdAndStatus(UUID repairOrderId, PaymentStatus status);

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.repairOrder.id = :repairOrderId
        AND p.deletedAt IS NULL
    """)
    BigDecimal sumTotalByRepairOrderId(UUID repairOrderId);
}