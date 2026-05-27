package com.deviceCare.deviceCare.modules.repairs.repository;

import com.deviceCare.deviceCare.modules.repairs.model.RepairOrderPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface RepairOrderPartRepository extends JpaRepository<RepairOrderPart, UUID> {

    List<RepairOrderPart> findAllByRepairOrderIdAndDeletedAtIsNull(UUID repairOrderId);

    @Query("""
        SELECT COALESCE(SUM(p.quantity * p.unitPrice), 0)
        FROM RepairOrderPart p
        WHERE p.repairOrder.id = :repairOrderId
        AND p.deletedAt IS NULL
    """)
    BigDecimal calculatePartsCost(UUID repairOrderId);
}