package com.deviceCare.deviceCare.modules.payments.dto;

import com.deviceCare.deviceCare.modules.payments.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PaymentRequest {

    @NotNull(message = "La orden de reparación es obligatoria")
    private UUID repairOrderId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal amount;

    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethod paymentMethod;

    private String transactionReference;
}