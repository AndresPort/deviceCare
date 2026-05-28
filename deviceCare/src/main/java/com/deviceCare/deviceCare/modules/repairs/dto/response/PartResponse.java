package com.deviceCare.deviceCare.modules.repairs.dto.response;

import com.deviceCare.deviceCare.modules.repairs.model.enums.PartSourceType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class PartResponse {

    private UUID id;
    private PartSourceType sourceType;
    private String customPartName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String notes;
    private Instant createdAt;
}