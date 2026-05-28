package com.deviceCare.deviceCare.modules.devices.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class DeviceResponse {

    private UUID id;
    private UUID clientId;
    private String clientName;
    private String brandName;
    private String typeName;
    private String model;
    private String serialNumber;
    private String color;
    private String physicalCondition;
    private String description;
    private Instant createdAt;
}