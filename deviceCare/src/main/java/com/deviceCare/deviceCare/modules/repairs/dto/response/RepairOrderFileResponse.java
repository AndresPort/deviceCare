package com.deviceCare.deviceCare.modules.repairs.dto.response;

import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairOrderFileStage;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class RepairOrderFileResponse {

    private UUID id;
    private String fileUrl;
    private RepairOrderFileStage stage;
    private String description;
    private String uploadedByName;
    private Instant uploadedAt;
}