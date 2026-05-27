package com.deviceCare.deviceCare.modules.repairs.dto.request;

import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusChangeRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private RepairStatus newStatus;

    private String notes;
}