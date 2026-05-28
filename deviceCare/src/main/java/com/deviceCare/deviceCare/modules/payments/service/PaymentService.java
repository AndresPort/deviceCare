package com.deviceCare.deviceCare.modules.payments.service;

import com.deviceCare.deviceCare.common.exception.BusinessException;
import com.deviceCare.deviceCare.modules.payments.dto.PaymentRequest;
import com.deviceCare.deviceCare.modules.payments.dto.PaymentResponse;
import com.deviceCare.deviceCare.modules.payments.dto.PaymentSummaryResponse;
import com.deviceCare.deviceCare.modules.payments.model.Payment;
import com.deviceCare.deviceCare.modules.payments.model.enums.PaymentMethod;
import com.deviceCare.deviceCare.modules.payments.model.enums.PaymentStatus;
import com.deviceCare.deviceCare.modules.payments.repository.PaymentRepository;
import com.deviceCare.deviceCare.modules.repairs.model.RepairOrder;
import com.deviceCare.deviceCare.modules.repairs.model.enums.RepairStatus;
import com.deviceCare.deviceCare.modules.repairs.repository.RepairOrderPartRepository;
import com.deviceCare.deviceCare.modules.repairs.repository.RepairOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RepairOrderRepository repairOrderRepository;
    private final RepairOrderPartRepository partRepository;

    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        RepairOrder order = repairOrderRepository
                .findByIdAndDeletedAtIsNull(request.getRepairOrderId())
                .orElseThrow(() -> BusinessException.notFound("Orden no encontrada"));

        if (order.getStatus() == RepairStatus.CANCELLED) {
            throw BusinessException.badRequest("No se puede registrar un pago en una orden cancelada");
        }

        if (order.getStatus() == RepairStatus.DELIVERED) {
            throw BusinessException.badRequest("La orden ya fue entregada y pagada");
        }

        Payment payment = new Payment();
        payment.setRepairOrder(order);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setTransactionReference(request.getTransactionReference());
        payment.setPaidAt(Instant.now());

        return toResponse(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public PaymentSummaryResponse findSummaryByOrder(UUID repairOrderId) {
        RepairOrder order = repairOrderRepository
                .findByIdAndDeletedAtIsNull(repairOrderId)
                .orElseThrow(() -> BusinessException.notFound("Orden no encontrada"));

        List<Payment> payments = paymentRepository
                .findAllByRepairOrderIdAndDeletedAtIsNull(repairOrderId);

        BigDecimal totalPaid = paymentRepository.sumTotalByRepairOrderId(repairOrderId);

        BigDecimal partsCost = partRepository.calculatePartsCost(repairOrderId);
        BigDecimal laborCost = order.getLaborCost() != null
                ? order.getLaborCost() : BigDecimal.ZERO;
        BigDecimal totalCost = laborCost.add(partsCost);

        BigDecimal balance = totalCost.subtract(totalPaid);

        PaymentStatus globalStatus;
        if (totalPaid.compareTo(BigDecimal.ZERO) == 0) {
            globalStatus = PaymentStatus.PENDING;
        } else if (totalPaid.compareTo(totalCost) >= 0) {
            globalStatus = PaymentStatus.PAID;
        } else {
            globalStatus = PaymentStatus.PARTIAL;
        }

        PaymentSummaryResponse summary = new PaymentSummaryResponse();
        summary.setRepairOrderId(repairOrderId);
        summary.setOrderNumber(order.getOrderNumber());
        summary.setTotalCost(totalCost);
        summary.setTotalPaid(totalPaid);
        summary.setBalance(balance);
        summary.setGlobalStatus(globalStatus);
        summary.setPayments(payments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList()));

        return summary;
    }

    @Transactional
    public void delete(UUID paymentId) {
        Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(paymentId)
                .orElseThrow(() -> BusinessException.notFound("Pago no encontrado"));
        payment.setDeletedAt(OffsetDateTime.now());
        paymentRepository.save(payment);
    }

    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setTransactionReference(payment.getTransactionReference());
        response.setPaidAt(payment.getPaidAt());
        response.setCreatedAt(payment.getCreatedAt());
        response.setRepairOrderId(payment.getRepairOrder().getId());
        // Evita acceder a la relación lazy con una query directa
        Long orderNumber = repairOrderRepository
                .findOrderNumberById(payment.getRepairOrder().getId());
        response.setOrderNumber(orderNumber);
        return response;
    }
}