package com.deviceCare.deviceCare.modules.repairs.dto.response;

import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairPriority;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class RepairOrderSummaryResponse {

    private UUID id;
    private Long orderNumber;
    private String deviceModel;
    private String deviceBrand;
    private String clientName;
    private String assignedTechnicianName;
    private RepairStatus status;
    private RepairPriority priority;
    private BigDecimal totalCost;
    private LocalDate estimatedDeliveryDate;
    private Instant createdAt;
}