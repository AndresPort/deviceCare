package com.deviceCare.deviceCare.modules.repairs.controller;

import com.deviceCare.deviceCare.common.dto.ApiResponse;
import com.deviceCare.deviceCare.modules.repairs.dto.request.*;
import com.deviceCare.deviceCare.modules.repairs.dto.response.*;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairStatus;
import com.deviceCare.deviceCare.modules.repairs.service.RepairOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/repairs")
@RequiredArgsConstructor
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    // =========================================================
    // ÓRDENES
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION')")
    public ResponseEntity<ApiResponse<RepairOrderDetailResponse>> create(
            @Valid @RequestBody RepairOrderRequest request) {
        RepairOrderDetailResponse order = repairOrderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(order, "Orden creada correctamente"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<List<RepairOrderSummaryResponse>>> findAll() {
        List<RepairOrderSummaryResponse> orders = repairOrderService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(orders, "Órdenes obtenidas correctamente"));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<List<RepairOrderSummaryResponse>>> findByStatus(
            @PathVariable RepairStatus status) {
        List<RepairOrderSummaryResponse> orders = repairOrderService.findByStatus(status);
        return ResponseEntity.ok(ApiResponse.ok(orders, "Órdenes obtenidas correctamente"));
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN', 'CLIENT')")
    public ResponseEntity<ApiResponse<List<RepairOrderSummaryResponse>>> findByClient(
            @PathVariable UUID clientId) {
        List<RepairOrderSummaryResponse> orders = repairOrderService.findByClient(clientId);
        return ResponseEntity.ok(ApiResponse.ok(orders, "Órdenes obtenidas correctamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN', 'CLIENT')")
    public ResponseEntity<ApiResponse<RepairOrderDetailResponse>> findById(
            @PathVariable UUID id) {
        RepairOrderDetailResponse order = repairOrderService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(order, "Orden obtenida correctamente"));
    }

    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN', 'CLIENT')")
    public ResponseEntity<ApiResponse<RepairOrderDetailResponse>> findByOrderNumber(
            @PathVariable Long orderNumber) {
        RepairOrderDetailResponse order = repairOrderService.findByOrderNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.ok(order, "Orden obtenida correctamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<RepairOrderDetailResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody RepairOrderRequest request) {
        RepairOrderDetailResponse order = repairOrderService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok(order, "Orden actualizada correctamente"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<RepairOrderDetailResponse>> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusChangeRequest request) {
        RepairOrderDetailResponse order = repairOrderService.changeStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok(order, "Estado actualizado correctamente"));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<ApiResponse<RepairOrderDetailResponse>> approve(
            @PathVariable UUID id,
            @Valid @RequestBody ApprovalRequest request) {
        RepairOrderDetailResponse order = repairOrderService.approve(id, request);
        return ResponseEntity.ok(ApiResponse.ok(order, "Aprobación registrada correctamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        repairOrderService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Orden eliminada correctamente"));
    }

    // =========================================================
    // ACCESORIOS
    // =========================================================

    @PostMapping("/{orderId}/accessories")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION')")
    public ResponseEntity<ApiResponse<AccessoryResponse>> addAccessory(
            @PathVariable UUID orderId,
            @Valid @RequestBody AccessoryRequest request) {
        AccessoryResponse accessory = repairOrderService.addAccessory(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(accessory, "Accesorio agregado correctamente"));
    }

    @GetMapping("/{orderId}/accessories")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<List<AccessoryResponse>>> findAccessories(
            @PathVariable UUID orderId) {
        List<AccessoryResponse> accessories = repairOrderService.findAccessories(orderId);
        return ResponseEntity.ok(ApiResponse.ok(accessories, "Accesorios obtenidos correctamente"));
    }

    @DeleteMapping("/accessories/{accessoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION')")
    public ResponseEntity<ApiResponse<Void>> deleteAccessory(
            @PathVariable UUID accessoryId) {
        repairOrderService.deleteAccessory(accessoryId);
        return ResponseEntity.ok(ApiResponse.ok("Accesorio eliminado correctamente"));
    }

    // =========================================================
    // ARCHIVOS
    // =========================================================

    @PostMapping("/{orderId}/files")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<RepairOrderFileResponse>> addFile(
            @PathVariable UUID orderId,
            @Valid @RequestBody FileUploadRequest request) {
        RepairOrderFileResponse file = repairOrderService.addFile(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(file, "Archivo agregado correctamente"));
    }

    @GetMapping("/{orderId}/files")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN', 'CLIENT')")
    public ResponseEntity<ApiResponse<List<RepairOrderFileResponse>>> findFiles(
            @PathVariable UUID orderId) {
        List<RepairOrderFileResponse> files = repairOrderService.findFiles(orderId);
        return ResponseEntity.ok(ApiResponse.ok(files, "Archivos obtenidos correctamente"));
    }

    @DeleteMapping("/files/{fileId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable UUID fileId) {
        repairOrderService.deleteFile(fileId);
        return ResponseEntity.ok(ApiResponse.ok("Archivo eliminado correctamente"));
    }

    // =========================================================
    // PARTES
    // =========================================================

    @PostMapping("/{orderId}/parts")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<PartResponse>> addPart(
            @PathVariable UUID orderId,
            @Valid @RequestBody PartRequest request) {
        PartResponse part = repairOrderService.addPart(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(part, "Parte agregada correctamente"));
    }

    @GetMapping("/{orderId}/parts")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<List<PartResponse>>> findParts(
            @PathVariable UUID orderId) {
        List<PartResponse> parts = repairOrderService.findParts(orderId);
        return ResponseEntity.ok(ApiResponse.ok(parts, "Partes obtenidas correctamente"));
    }

    @DeleteMapping("/parts/{partId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<Void>> deletePart(@PathVariable UUID partId) {
        repairOrderService.deletePart(partId);
        return ResponseEntity.ok(ApiResponse.ok("Parte eliminada correctamente"));
    }

    // =========================================================
    // HISTORIAL
    // =========================================================

    @GetMapping("/{orderId}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN', 'CLIENT')")
    public ResponseEntity<ApiResponse<List<StatusHistoryResponse>>> findStatusHistory(
            @PathVariable UUID orderId) {
        List<StatusHistoryResponse> history = repairOrderService.findStatusHistory(orderId);
        return ResponseEntity.ok(ApiResponse.ok(history, "Historial obtenido correctamente"));
    }
}