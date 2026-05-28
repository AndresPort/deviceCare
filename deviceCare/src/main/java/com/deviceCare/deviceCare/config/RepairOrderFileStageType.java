package com.deviceCare.deviceCare.config;

import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairOrderFileStage;

public class RepairOrderFileStageType extends PostgreSQLEnumType<RepairOrderFileStage> {
    public RepairOrderFileStageType() {
        super(RepairOrderFileStage.class);
    }
}