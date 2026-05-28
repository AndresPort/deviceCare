package com.deviceCare.deviceCare.config;

import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairStatus;

public class RepairStatusType extends PostgreSQLEnumType<RepairStatus> {
    public RepairStatusType() {
        super(RepairStatus.class);
    }
}