package com.deviceCare.deviceCare.modules.repairs.repository;

import com.deviceCare.deviceCare.modules.repairs.model.RepairOrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepairOrderStatusHistoryRepository
        extends JpaRepository<RepairOrderStatusHistory, UUID> {

    List<RepairOrderStatusHistory> findAllByRepairOrderIdOrderByChangedAtAsc(UUID repairOrderId);
}