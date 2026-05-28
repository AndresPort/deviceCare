package com.deviceCare.deviceCare.config;

import com.deviceCare.deviceCare.modules.repairs.model.enums.PartSourceType;

public class PartSourceTypeEnum extends PostgreSQLEnumType<PartSourceType> {
    public PartSourceTypeEnum() {
        super(PartSourceType.class);
    }
}