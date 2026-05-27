package com.deviceCare.deviceCare.modules.repairs.dto.request;

import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairOrderFileStage;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadRequest {

    @NotBlank(message = "La URL del archivo es obligatoria")
    private String fileUrl;

    private RepairOrderFileStage stage;

    private String description;
}