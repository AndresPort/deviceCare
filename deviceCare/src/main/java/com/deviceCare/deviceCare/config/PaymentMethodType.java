package com.deviceCare.deviceCare.config;

import com.deviceCare.deviceCare.modules.payments.model.enums.PaymentMethod;

public class PaymentMethodType extends PostgreSQLEnumType<PaymentMethod> {
    public PaymentMethodType() {
        super(PaymentMethod.class);
    }
}
