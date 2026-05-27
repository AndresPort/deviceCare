package com.deviceCare.deviceCare.modules.repairs.dto.request;

import com.deviceCare.deviceCare.modules.repairs.model.enums.DamageLevel;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class RepairOrderRequest {

    @NotNull(message = "El dispositivo es obligatorio")
    private UUID deviceId;

    private UUID assignedTechnicianId;

    private RepairPriority priority = RepairPriority.NORMAL;

    private DamageLevel damageLevel;

    @NotBlank(message = "La descripción del problema es obligatoria")
    private String clientProblem;

    private String technicalDiagnosis;

    private BigDecimal estimatedCost;

    private BigDecimal laborCost;

    private LocalDate estimatedDeliveryDate;
}