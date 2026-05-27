package com.deviceCare.deviceCare.modules.repairs.service;

import com.deviceCare.deviceCare.common.exception.BusinessException;
import com.deviceCare.deviceCare.modules.devices.model.Device;
import com.deviceCare.deviceCare.modules.devices.repository.DeviceRepository;
import com.deviceCare.deviceCare.modules.repairs.dto.request.*;
import com.deviceCare.deviceCare.modules.repairs.dto.response.*;
import com.deviceCare.deviceCare.modules.repairs.model.*;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairStatus;
import com.deviceCare.deviceCare.modules.repairs.repository.*;
import com.deviceCare.deviceCare.modules.users.model.User;
import com.deviceCare.deviceCare.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;
    private final RepairOrderStatusHistoryRepository statusHistoryRepository;
    private final RepairOrderFileRepository fileRepository;
    private final RepairOrderAccessoryRepository accessoryRepository;
    private final RepairOrderPartRepository partRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    // =========================================================
    // ÓRDENES
    // =========================================================

    @Transactional
    public RepairOrderDetailResponse create(RepairOrderRequest request) {
        Device device = deviceRepository.findByIdAndDeletedAtIsNull(request.getDeviceId())
                .orElseThrow(() -> BusinessException.notFound("Dispositivo no encontrado"));

        RepairOrder order = new RepairOrder();
        order.setDevice(device);
        order.setClientProblem(request.getClientProblem());
        order.setPriority(request.getPriority());
        order.setDamageLevel(request.getDamageLevel());
        order.setEstimatedCost(request.getEstimatedCost());
        order.setLaborCost(request.getLaborCost() != null
                ? request.getLaborCost() : BigDecimal.ZERO);
        order.setEstimatedDeliveryDate(request.getEstimatedDeliveryDate());
        order.setStatus(RepairStatus.RECEIVED);

        if (request.getAssignedTechnicianId() != null) {
            User technician = userRepository
                    .findByIdAndDeletedAtIsNull(request.getAssignedTechnicianId())
                    .orElseThrow(() -> BusinessException.notFound("Técnico no encontrado"));
            order.setAssignedTechnician(technician);
        }

        RepairOrder saved = repairOrderRepository.save(order);
        recordStatusChange(saved, null, RepairStatus.RECEIVED, "Orden creada");

        return toDetailResponse(saved);
    }

    public List<RepairOrderSummaryResponse> findAll() {
        return repairOrderRepository.findAllOrderedByCreatedAt()
                .stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<RepairOrderSummaryResponse> findByStatus(RepairStatus status) {
        return repairOrderRepository.findAllByStatusAndDeletedAtIsNull(status)
                .stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<RepairOrderSummaryResponse> findByClient(UUID clientId) {
        return repairOrderRepository.findAllByDevice_Client_IdAndDeletedAtIsNull(clientId)
                .stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    public RepairOrderDetailResponse findById(UUID id) {
        RepairOrder order = repairOrderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Orden no encontrada"));
        return toDetailResponse(order);
    }

    public RepairOrderDetailResponse findByOrderNumber(Long orderNumber) {
        RepairOrder order = repairOrderRepository
                .findByOrderNumberAndDeletedAtIsNull(orderNumber)
                .orElseThrow(() -> BusinessException.notFound("Orden no encontrada"));
        return toDetailResponse(order);
    }

    @Transactional
    public RepairOrderDetailResponse update(UUID id, RepairOrderRequest request) {
        RepairOrder order = repairOrderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Orden no encontrada"));

        order.setClientProblem(request.getClientProblem());
        order.setTechnicalDiagnosis(request.getTechnicalDiagnosis());
        order.setPriority(request.getPriority());
        order.setDamageLevel(request.getDamageLevel());
        order.setEstimatedCost(request.getEstimatedCost());
        order.setEstimatedDeliveryDate(request.getEstimatedDeliveryDate());

        if (request.getLaborCost() != null) {
            order.setLaborCost(request.getLaborCost());
        }

        if (request.getAssignedTechnicianId() != null) {
            User technician = userRepository
                    .findByIdAndDeletedAtIsNull(request.getAssignedTechnicianId())
                    .orElseThrow(() -> BusinessException.notFound("Técnico no encontrado"));
            order.setAssignedTechnician(technician);
        }

        return toDetailResponse(repairOrderRepository.save(order));
    }

    @Transactional
    public RepairOrderDetailResponse changeStatus(UUID id, StatusChangeRequest request) {
        RepairOrder order = repairOrderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Orden no encontrada"));

        if (order.getStatus() == RepairStatus.DELIVERED ||
                order.getStatus() == RepairStatus.CANCELLED) {
            throw BusinessException.badRequest(
                    "No se puede cambiar el estado de una orden entregada o cancelada");
        }

        RepairStatus oldStatus = order.getStatus();
        order.setStatus(request.getNewStatus());

        if (request.getNewStatus() == RepairStatus.DELIVERED) {
            order.setDeliveredAt(OffsetDateTime.now());
        }

        repairOrderRepository.save(order);
        recordStatusChange(order, oldStatus, request.getNewStatus(), request.getNotes());

        return toDetailResponse(order);
    }

    @Transactional
    public RepairOrderDetailResponse approve(UUID id, ApprovalRequest request) {
        RepairOrder order = repairOrderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Orden no encontrada"));

        if (order.getStatus() != RepairStatus.WAITING_APPROVAL) {
            throw BusinessException.badRequest(
                    "La orden no está en estado de espera de aprobación");
        }

        order.setClientApproved(request.isApproved());
        order.setClientApprovedAt(OffsetDateTime.now());
        order.setClientApprovalNotes(request.getNotes());

        if (request.isApproved()) {
            order.setStatus(RepairStatus.IN_REPAIR);
            recordStatusChange(order, RepairStatus.WAITING_APPROVAL,
                    RepairStatus.IN_REPAIR, "Cliente aprobó el presupuesto");
        } else {
            order.setStatus(RepairStatus.CANCELLED);
            recordStatusChange(order, RepairStatus.WAITING_APPROVAL,
                    RepairStatus.CANCELLED, "Cliente rechazó el presupuesto");
        }

        return toDetailResponse(repairOrderRepository.save(order));
    }

    @Transactional
    public void delete(UUID id) {
        RepairOrder order = repairOrderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Orden no encontrada"));
        order.setDeletedAt(OffsetDateTime.now());
        repairOrderRepository.save(order);
    }

    // =========================================================
    // ACCESORIOS
    // =========================================================

    @Transactional
    public AccessoryResponse addAccessory(UUID orderId, AccessoryRequest request) {
        RepairOrder order = repairOrderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> BusinessException.notFound("Orden no encontrada"));

        RepairOrderAccessory accessory = new RepairOrderAccessory();
        accessory.setRepairOrder(order);
        accessory.setAccessoryName(request.getAccessoryName());
        accessory.setQuantity(request.getQuantity());
        accessory.setReceived(request.isReceived());
        accessory.setConditionNotes(request.getConditionNotes());

        return toAccessoryResponse(accessoryRepository.save(accessory));
    }

    public List<AccessoryResponse> findAccessories(UUID orderId) {
        return accessoryRepository.findAllByRepairOrderIdAndDeletedAtIsNull(orderId)
                .stream()
                .map(this::toAccessoryResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAccessory(UUID accessoryId) {
        RepairOrderAccessory accessory = accessoryRepository.findById(accessoryId)
                .orElseThrow(() -> BusinessException.notFound("Accesorio no encontrado"));
        accessory.setDeletedAt(OffsetDateTime.now());
        accessoryRepository.save(accessory);
    }

    // =========================================================
    // ARCHIVOS
    // =========================================================

    @Transactional
    public RepairOrderFileResponse addFile(UUID orderId, FileUploadRequest request) {
        RepairOrder order = repairOrderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> BusinessException.notFound("Orden no encontrada"));

        RepairOrderFile file = new RepairOrderFile();
        file.setRepairOrder(order);
        file.setFileUrl(request.getFileUrl());
        file.setStage(request.getStage());
        file.setDescription(request.getDescription());

        return toFileResponse(fileRepository.save(file));
    }

    public List<RepairOrderFileResponse> findFiles(UUID orderId) {
        return fileRepository.findAllByRepairOrderIdAndDeletedAtIsNull(orderId)
                .stream()
                .map(this::toFileResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFile(UUID fileId) {
        RepairOrderFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> BusinessException.notFound("Archivo no encontrado"));
        file.setDeletedAt(OffsetDateTime.now());
        fileRepository.save(file);
    }

    // =========================================================
    // PARTES
    // =========================================================

    @Transactional
    public PartResponse addPart(UUID orderId, PartRequest request) {
        RepairOrder order = repairOrderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> BusinessException.notFound("Orden no encontrada"));

        RepairOrderPart part = new RepairOrderPart();
        part.setRepairOrder(order);
        part.setSourceType(request.getSourceType());
        part.setCustomPartName(request.getCustomPartName());
        part.setQuantity(request.getQuantity());
        part.setUnitPrice(request.getUnitPrice());
        part.setNotes(request.getNotes());

        return toPartResponse(partRepository.save(part));
    }

    public List<PartResponse> findParts(UUID orderId) {
        return partRepository.findAllByRepairOrderIdAndDeletedAtIsNull(orderId)
                .stream()
                .map(this::toPartResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePart(UUID partId) {
        RepairOrderPart part = partRepository.findById(partId)
                .orElseThrow(() -> BusinessException.notFound("Parte no encontrada"));
        part.setDeletedAt(OffsetDateTime.now());
        partRepository.save(part);
    }

    // =========================================================
    // HISTORIAL
    // =========================================================

    public List<StatusHistoryResponse> findStatusHistory(UUID orderId) {
        return statusHistoryRepository
                .findAllByRepairOrderIdOrderByChangedAtAsc(orderId)
                .stream()
                .map(this::toStatusHistoryResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // MÉTODOS PRIVADOS
    // =========================================================

    private void recordStatusChange(RepairOrder order, RepairStatus oldStatus,
                                    RepairStatus newStatus, String notes) {
        RepairOrderStatusHistory history = new RepairOrderStatusHistory();
        history.setRepairOrder(order);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setNotes(notes);
        statusHistoryRepository.save(history);
    }

    private BigDecimal calculateTotalCost(UUID orderId, BigDecimal laborCost) {
        BigDecimal partsCost = partRepository.calculatePartsCost(orderId);
        BigDecimal labor = laborCost != null ? laborCost : BigDecimal.ZERO;
        return labor.add(partsCost);
    }

    private RepairOrderSummaryResponse toSummaryResponse(RepairOrder order) {
        RepairOrderSummaryResponse response = new RepairOrderSummaryResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setStatus(order.getStatus());
        response.setPriority(order.getPriority());
        response.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
        response.setCreatedAt(order.getCreatedAt());
        response.setTotalCost(calculateTotalCost(order.getId(), order.getLaborCost()));

        if (order.getDevice() != null) {
            response.setDeviceModel(order.getDevice().getModel());
            if (order.getDevice().getBrand() != null) {
                response.setDeviceBrand(order.getDevice().getBrand().getName());
            }
            if (order.getDevice().getClient() != null &&
                    order.getDevice().getClient().getUser() != null) {
                User user = order.getDevice().getClient().getUser();
                response.setClientName(user.getFirstName() + " " + user.getLastName());
            }
        }

        if (order.getAssignedTechnician() != null) {
            response.setAssignedTechnicianName(
                    order.getAssignedTechnician().getFirstName() + " " +
                            order.getAssignedTechnician().getLastName()
            );
        }

        return response;
    }

    private RepairOrderDetailResponse toDetailResponse(RepairOrder order) {
        RepairOrderDetailResponse response = new RepairOrderDetailResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setStatus(order.getStatus());
        response.setPriority(order.getPriority());
        response.setDamageLevel(order.getDamageLevel());
        response.setClientProblem(order.getClientProblem());
        response.setTechnicalDiagnosis(order.getTechnicalDiagnosis());
        response.setClientApproved(order.isClientApproved());
        response.setClientApprovedAt(order.getClientApprovedAt());
        response.setClientApprovalNotes(order.getClientApprovalNotes());
        response.setEstimatedCost(order.getEstimatedCost());
        response.setLaborCost(order.getLaborCost());
        response.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
        response.setDeliveredAt(order.getDeliveredAt());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        BigDecimal partsCost = partRepository.calculatePartsCost(order.getId());
        response.setPartsCost(partsCost);
        response.setTotalCost(
                (order.getLaborCost() != null ? order.getLaborCost() : BigDecimal.ZERO)
                        .add(partsCost)
        );

        if (order.getDevice() != null) {
            Device device = order.getDevice();
            response.setDeviceId(device.getId());
            response.setDeviceModel(device.getModel());
            response.setDeviceSerialNumber(device.getSerialNumber());
            response.setDeviceColor(device.getColor());
            if (device.getBrand() != null) {
                response.setDeviceBrand(device.getBrand().getName());
            }
            if (device.getClient() != null) {
                response.setClientId(device.getClient().getId());
                response.setClientDocument(device.getClient().getDocumentNumber());
                if (device.getClient().getUser() != null) {
                    User user = device.getClient().getUser();
                    response.setClientName(user.getFirstName() + " " + user.getLastName());
                }
            }
        }

        if (order.getAssignedTechnician() != null) {
            response.setAssignedTechnicianId(order.getAssignedTechnician().getId());
            response.setAssignedTechnicianName(
                    order.getAssignedTechnician().getFirstName() + " " +
                            order.getAssignedTechnician().getLastName()
            );
        }

        response.setStatusHistory(findStatusHistory(order.getId()));
        response.setFiles(findFiles(order.getId()));
        response.setAccessories(findAccessories(order.getId()));
        response.setParts(findParts(order.getId()));

        return response;
    }

    private AccessoryResponse toAccessoryResponse(RepairOrderAccessory accessory) {
        AccessoryResponse response = new AccessoryResponse();
        response.setId(accessory.getId());
        response.setAccessoryName(accessory.getAccessoryName());
        response.setQuantity(accessory.getQuantity());
        response.setReceived(accessory.isReceived());
        response.setConditionNotes(accessory.getConditionNotes());
        response.setCreatedAt(accessory.getCreatedAt());
        return response;
    }

    private RepairOrderFileResponse toFileResponse(RepairOrderFile file) {
        RepairOrderFileResponse response = new RepairOrderFileResponse();
        response.setId(file.getId());
        response.setFileUrl(file.getFileUrl());
        response.setStage(file.getStage());
        response.setDescription(file.getDescription());
        response.setUploadedAt(file.getUploadedAt());
        if (file.getUploadedBy() != null) {
            response.setUploadedByName(
                    file.getUploadedBy().getFirstName() + " " +
                            file.getUploadedBy().getLastName()
            );
        }
        return response;
    }

    private PartResponse toPartResponse(RepairOrderPart part) {
        PartResponse response = new PartResponse();
        response.setId(part.getId());
        response.setSourceType(part.getSourceType());
        response.setCustomPartName(part.getCustomPartName());
        response.setQuantity(part.getQuantity());
        response.setUnitPrice(part.getUnitPrice());
        response.setSubtotal(part.getUnitPrice().multiply(
                BigDecimal.valueOf(part.getQuantity())));
        response.setNotes(part.getNotes());
        response.setCreatedAt(part.getCreatedAt());
        return response;
    }

    private StatusHistoryResponse toStatusHistoryResponse(RepairOrderStatusHistory history) {
        StatusHistoryResponse response = new StatusHistoryResponse();
        response.setId(history.getId());
        response.setOldStatus(history.getOldStatus());
        response.setNewStatus(history.getNewStatus());
        response.setNotes(history.getNotes());
        response.setChangedAt(history.getChangedAt());
        if (history.getChangedBy() != null) {
            response.setChangedByName(
                    history.getChangedBy().getFirstName() + " " +
                            history.getChangedBy().getLastName()
            );
        }
        return response;
    }
}