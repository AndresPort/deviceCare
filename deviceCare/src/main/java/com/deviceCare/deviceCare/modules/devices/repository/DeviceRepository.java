package com.deviceCare.deviceCare.modules.devices.repository;

import com.deviceCare.deviceCare.modules.devices.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {

    Optional<Device> findByIdAndDeletedAtIsNull(UUID id);

    List<Device> findAllByClientIdAndDeletedAtIsNull(UUID clientId);

    boolean existsBySerialNumberAndDeletedAtIsNull(String serialNumber);
}