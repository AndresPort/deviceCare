package com.deviceCare.deviceCare.modules.repairs.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import java.util.UUID;

@Getter
@Setter
public class AccessoryResponse {

    private UUID id;
    private String accessoryName;
    private Integer quantity;
    private boolean received;
    private String conditionNotes;
    private Instant createdAt;
}