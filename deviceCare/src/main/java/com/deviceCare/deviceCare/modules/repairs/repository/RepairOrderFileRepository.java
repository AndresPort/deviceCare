package com.deviceCare.deviceCare.modules.repairs.repository;

import com.deviceCare.deviceCare.modules.repairs.model.RepairOrderFile;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairOrderFileStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepairOrderFileRepository extends JpaRepository<RepairOrderFile, UUID> {

    List<RepairOrderFile> findAllByRepairOrderIdAndDeletedAtIsNull(UUID repairOrderId);

    List<RepairOrderFile> findAllByRepairOrderIdAndStageAndDeletedAtIsNull(
            UUID repairOrderId, RepairOrderFileStage stage);
}