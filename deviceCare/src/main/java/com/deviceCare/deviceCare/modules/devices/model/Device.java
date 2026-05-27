package com.deviceCare.deviceCare.modules.devices.model;

import com.deviceCare.deviceCare.common.base.BaseEntity;
import com.deviceCare.deviceCare.modules.clients.model.Client;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "devices")
public class Device extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private DeviceBrand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private DeviceType type;

    @Column(name = "model", nullable = false, length = 150)
    private String model;

    @Column(name = "serial_number", unique = true, length = 150)
    private String serialNumber;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "physical_condition")
    private String physicalCondition;

    @Column(name = "description")
    private String description;
}