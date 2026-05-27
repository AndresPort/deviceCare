package com.deviceCare.deviceCare.modules.devices.repository;

import com.deviceCare.deviceCare.modules.devices.model.DeviceBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceBrandRepository extends JpaRepository<DeviceBrand, UUID> {

    Optional<DeviceBrand> findByIdAndDeletedAtIsNull(UUID id);

    List<DeviceBrand> findAllByDeletedAtIsNull();

    boolean existsByNameAndDeletedAtIsNull(String name);
}