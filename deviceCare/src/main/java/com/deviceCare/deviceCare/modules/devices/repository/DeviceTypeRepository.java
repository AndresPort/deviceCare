package com.deviceCare.deviceCare.modules.devices.repository;

import com.deviceCare.deviceCare.modules.devices.model.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceTypeRepository extends JpaRepository<DeviceType, UUID> {

    Optional<DeviceType> findByIdAndDeletedAtIsNull(UUID id);

    List<DeviceType> findAllByDeletedAtIsNull();

    boolean existsByNameAndDeletedAtIsNull(String name);
}