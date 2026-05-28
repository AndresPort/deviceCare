package com.deviceCare.deviceCare.modules.payments.dto;

import com.deviceCare.deviceCare.modules.payments.model.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PaymentSummaryResponse {

    private UUID repairOrderId;
    private Long orderNumber;
    private BigDecimal totalCost;
    private BigDecimal totalPaid;
    private BigDecimal balance;
    private PaymentStatus globalStatus;
    private List<PaymentResponse> payments;
}