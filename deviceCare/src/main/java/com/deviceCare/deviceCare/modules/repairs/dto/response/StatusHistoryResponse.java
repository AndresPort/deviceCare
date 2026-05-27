package com.deviceCare.deviceCare.modules.repairs.dto.response;

import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class StatusHistoryResponse {

    private UUID id;
    private RepairStatus oldStatus;
    private RepairStatus newStatus;
    private String changedByName;
    private String notes;
    private OffsetDateTime changedAt;
}