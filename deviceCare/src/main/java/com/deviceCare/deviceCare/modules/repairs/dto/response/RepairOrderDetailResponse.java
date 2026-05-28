package com.deviceCare.deviceCare.modules.repairs.dto.response;

import com.deviceCare.deviceCare.modules.repairs.model.enums.DamageLevel;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairPriority;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RepairOrderDetailResponse {

    private UUID id;
    private Long orderNumber;

    private UUID deviceId;
    private String deviceModel;
    private String deviceBrand;
    private String deviceSerialNumber;
    private String deviceColor;

    private UUID clientId;
    private String clientName;
    private String clientDocument;

    private UUID assignedTechnicianId;
    private String assignedTechnicianName;

    private RepairStatus status;
    private RepairPriority priority;
    private DamageLevel damageLevel;

    private String clientProblem;
    private String technicalDiagnosis;

    private boolean clientApproved;
    private Instant clientApprovedAt;
    private String clientApprovalNotes;

    private BigDecimal estimatedCost;
    private BigDecimal laborCost;
    private BigDecimal partsCost;
    private BigDecimal totalCost;

    private LocalDate estimatedDeliveryDate;
    private Instant deliveredAt;

    private List<StatusHistoryResponse> statusHistory;
    private List<RepairOrderFileResponse> files;
    private List<AccessoryResponse> accessories;
    private List<PartResponse> parts;

    private Instant createdAt;
    private Instant updatedAt;
}