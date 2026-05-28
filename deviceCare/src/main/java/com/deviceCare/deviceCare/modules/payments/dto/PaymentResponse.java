package com.deviceCare.deviceCare.modules.payments.dto;

import com.deviceCare.deviceCare.modules.payments.model.enums.PaymentMethod;
import com.deviceCare.deviceCare.modules.payments.model.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class PaymentResponse {

    private UUID id;
    private UUID repairOrderId;
    private Long orderNumber;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionReference;
    private Instant paidAt;
    private Instant createdAt;
}