package com.deviceCare.deviceCare.config;

import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairPriority;

public class RepairPriorityType extends PostgreSQLEnumType<RepairPriority> {
    public RepairPriorityType() {
        super(RepairPriority.class);
    }
}