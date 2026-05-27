package com.deviceCare.deviceCare.modules.devices.service;

import com.deviceCare.deviceCare.common.exception.BusinessException;
import com.deviceCare.deviceCare.modules.clients.model.Client;
import com.deviceCare.deviceCare.modules.clients.repository.ClientRepository;
import com.deviceCare.deviceCare.modules.devices.dto.DeviceRequest;
import com.deviceCare.deviceCare.modules.devices.dto.DeviceResponse;
import com.deviceCare.deviceCare.modules.devices.model.Device;
import com.deviceCare.deviceCare.modules.devices.model.DeviceBrand;
import com.deviceCare.deviceCare.modules.devices.model.DeviceType;
import com.deviceCare.deviceCare.modules.devices.repository.DeviceBrandRepository;
import com.deviceCare.deviceCare.modules.devices.repository.DeviceRepository;
import com.deviceCare.deviceCare.modules.devices.repository.DeviceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final ClientRepository clientRepository;
    private final DeviceBrandRepository brandRepository;
    private final DeviceTypeRepository typeRepository;

    @Transactional
    public DeviceResponse create(DeviceRequest request) {
        if (request.getSerialNumber() != null &&
                deviceRepository.existsBySerialNumberAndDeletedAtIsNull(
                        request.getSerialNumber())) {
            throw BusinessException.conflict("El número de serie ya está registrado");
        }

        Device device = new Device();
        mapRequestToDevice(request, device);

        return toResponse(deviceRepository.save(device));
    }

    public List<DeviceResponse> findByClient(UUID clientId) {
        return deviceRepository.findAllByClientIdAndDeletedAtIsNull(clientId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DeviceResponse findById(UUID id) {
        Device device = deviceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Dispositivo no encontrado"));
        return toResponse(device);
    }

    @Transactional
    public DeviceResponse update(UUID id, DeviceRequest request) {
        Device device = deviceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Dispositivo no encontrado"));

        if (request.getSerialNumber() != null &&
                !request.getSerialNumber().equals(device.getSerialNumber()) &&
                deviceRepository.existsBySerialNumberAndDeletedAtIsNull(
                        request.getSerialNumber())) {
            throw BusinessException.conflict("El número de serie ya está registrado");
        }

        mapRequestToDevice(request, device);
        return toResponse(deviceRepository.save(device));
    }

    @Transactional
    public void delete(UUID id) {
        Device device = deviceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Dispositivo no encontrado"));
        device.setDeletedAt(OffsetDateTime.now());
        deviceRepository.save(device);
    }

    private void mapRequestToDevice(DeviceRequest request, Device device) {
        Client client = clientRepository.findByIdAndDeletedAtIsNull(request.getClientId())
                .orElseThrow(() -> BusinessException.notFound("Cliente no encontrado"));
        device.setClient(client);

        if (request.getBrandId() != null) {
            DeviceBrand brand = brandRepository.findByIdAndDeletedAtIsNull(request.getBrandId())
                    .orElseThrow(() -> BusinessException.notFound("Marca no encontrada"));
            device.setBrand(brand);
        }

        if (request.getTypeId() != null) {
            DeviceType type = typeRepository.findByIdAndDeletedAtIsNull(request.getTypeId())
                    .orElseThrow(() -> BusinessException.notFound("Tipo no encontrado"));
            device.setType(type);
        }

        device.setModel(request.getModel());
        device.setSerialNumber(request.getSerialNumber());
        device.setColor(request.getColor());
        device.setPhysicalCondition(request.getPhysicalCondition());
        device.setDescription(request.getDescription());
    }

    private DeviceResponse toResponse(Device device) {
        DeviceResponse response = new DeviceResponse();
        response.setId(device.getId());
        response.setModel(device.getModel());
        response.setSerialNumber(device.getSerialNumber());
        response.setColor(device.getColor());
        response.setPhysicalCondition(device.getPhysicalCondition());
        response.setDescription(device.getDescription());
        response.setCreatedAt(device.getCreatedAt());

        if (device.getClient() != null) {
            response.setClientId(device.getClient().getId());
            if (device.getClient().getUser() != null) {
                response.setClientName(
                        device.getClient().getUser().getFirstName() + " " +
                                device.getClient().getUser().getLastName()
                );
            }
        }

        if (device.getBrand() != null) {
            response.setBrandName(device.getBrand().getName());
        }

        if (device.getType() != null) {
            response.setTypeName(device.getType().getName());
        }

        return response;
    }
}