package com.deviceCare.deviceCare.modules.repairs.dto.request;

import com.deviceCare.deviceCare.modules.repairs.model.enums.PartSourceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PartRequest {

    @NotNull(message = "El tipo de fuente es obligatorio")
    private PartSourceType sourceType;

    private String customPartName;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer quantity;

    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private BigDecimal unitPrice;

    private String notes;
}