package com.deviceCare.deviceCare.modules.repairs.repository;

import com.deviceCare.deviceCare.modules.repairs.model.RepairOrder;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, UUID> {

    Optional<RepairOrder> findByIdAndDeletedAtIsNull(UUID id);

    Optional<RepairOrder> findByOrderNumberAndDeletedAtIsNull(Long orderNumber);

    List<RepairOrder> findAllByDeletedAtIsNull();

    List<RepairOrder> findAllByStatusAndDeletedAtIsNull(RepairStatus status);

    List<RepairOrder> findAllByAssignedTechnicianIdAndDeletedAtIsNull(UUID technicianId);

    List<RepairOrder> findAllByDevice_Client_IdAndDeletedAtIsNull(UUID clientId);

    @Query("""
        SELECT ro FROM RepairOrder ro
        WHERE ro.deletedAt IS NULL
        ORDER BY ro.createdAt DESC
    """)
    List<RepairOrder> findAllOrderedByCreatedAt();
}