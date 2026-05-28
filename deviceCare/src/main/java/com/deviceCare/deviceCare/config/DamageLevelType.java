package com.deviceCare.deviceCare.config;

import com.deviceCare.deviceCare.modules.repairs.model.enums.DamageLevel;

public class DamageLevelType extends PostgreSQLEnumType<DamageLevel> {
    public DamageLevelType() {
        super(DamageLevel.class);
    }
}