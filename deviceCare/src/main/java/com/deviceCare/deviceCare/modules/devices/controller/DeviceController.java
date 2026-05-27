package com.deviceCare.deviceCare.modules.devices.controller;

import com.deviceCare.deviceCare.common.dto.ApiResponse;
import com.deviceCare.deviceCare.modules.devices.dto.DeviceRequest;
import com.deviceCare.deviceCare.modules.devices.dto.DeviceResponse;
import com.deviceCare.deviceCare.modules.devices.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION')")
    public ResponseEntity<ApiResponse<DeviceResponse>> create(
            @Valid @RequestBody DeviceRequest request) {
        DeviceResponse device = deviceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(device, "Dispositivo registrado correctamente"));
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> findByClient(
            @PathVariable UUID clientId) {
        List<DeviceResponse> devices = deviceService.findByClient(clientId);
        return ResponseEntity.ok(ApiResponse.ok(devices, "Dispositivos obtenidos correctamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<DeviceResponse>> findById(@PathVariable UUID id) {
        DeviceResponse device = deviceService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(device, "Dispositivo obtenido correctamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION')")
    public ResponseEntity<ApiResponse<DeviceResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody DeviceRequest request) {
        DeviceResponse device = deviceService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok(device, "Dispositivo actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        deviceService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Dispositivo eliminado correctamente"));
    }
}