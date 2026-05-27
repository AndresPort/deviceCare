package com.deviceCare.deviceCare.modules.devices.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeviceRequest {

    @NotNull(message = "El cliente es obligatorio")
    private UUID clientId;

    private UUID brandId;
    private UUID typeId;

    @NotBlank(message = "El modelo es obligatorio")
    private String model;

    private String serialNumber;
    private String color;
    private String physicalCondition;
    private String description;
}