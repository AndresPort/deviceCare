package com.deviceCare.deviceCare.modules.repairs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessoryRequest {

    @NotBlank(message = "El nombre del accesorio es obligatorio")
    private String accessoryName;

    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer quantity = 1;

    private boolean received = true;

    private String conditionNotes;
}