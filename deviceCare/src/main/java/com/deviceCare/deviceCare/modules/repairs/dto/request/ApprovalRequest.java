package com.deviceCare.deviceCare.modules.repairs.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalRequest {

    @NotNull(message = "La aprobación es obligatoria")
    private boolean approved;

    private String notes;
}