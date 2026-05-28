package com.deviceCare.deviceCare.config;

import com.deviceCare.deviceCare.modules.payments.model.enums.PaymentStatus;

public class PaymentStatusType extends PostgreSQLEnumType<PaymentStatus> {
    public PaymentStatusType() {
        super(PaymentStatus.class);
    }
}