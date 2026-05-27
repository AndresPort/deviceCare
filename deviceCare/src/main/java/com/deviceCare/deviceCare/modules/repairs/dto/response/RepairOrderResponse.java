package com.deviceCare.deviceCare.modules.repairs.dto.response;

import com.deviceCare.deviceCare.modules.repairs.model.enums.DamageLevel;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairPriority;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class RepairOrderResponse {

    private UUID id;
    private Long orderNumber;

    private UUID deviceId;
    private String deviceModel;
    private String deviceBrand;
    private String clientName;

    private UUID assignedTechnicianId;
    private String assignedTechnicianName;

    private RepairStatus status;
    private RepairPriority priority;
    private DamageLevel damageLevel;

    private String clientProblem;
    private String technicalDiagnosis;

    private boolean clientApproved;
    private OffsetDateTime clientApprovedAt;
    private String clientApprovalNotes;

    private BigDecimal estimatedCost;
    private BigDecimal laborCost;
    private BigDecimal partsCost;
    private BigDecimal totalCost;

    private LocalDate estimatedDeliveryDate;
    private OffsetDateTime deliveredAt;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}