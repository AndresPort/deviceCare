package com.deviceCare.deviceCare.modules.devices.model;

import com.deviceCare.deviceCare.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "device_types")
public class DeviceType extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;
}