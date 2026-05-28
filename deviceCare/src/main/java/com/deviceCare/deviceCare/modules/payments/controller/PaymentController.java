package com.deviceCare.deviceCare.modules.payments.controller;

import com.deviceCare.deviceCare.common.dto.ApiResponse;
import com.deviceCare.deviceCare.modules.payments.dto.PaymentRequest;
import com.deviceCare.deviceCare.modules.payments.dto.PaymentResponse;
import com.deviceCare.deviceCare.modules.payments.dto.PaymentSummaryResponse;
import com.deviceCare.deviceCare.modules.payments.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION')")
    public ResponseEntity<ApiResponse<PaymentResponse>> create(
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse payment = paymentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(payment, "Pago registrado correctamente"));
    }

    @GetMapping("/order/{repairOrderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN', 'CLIENT')")
    public ResponseEntity<ApiResponse<PaymentSummaryResponse>> findSummaryByOrder(
            @PathVariable UUID repairOrderId) {
        PaymentSummaryResponse summary = paymentService.findSummaryByOrder(repairOrderId);
        return ResponseEntity.ok(ApiResponse.ok(summary, "Resumen de pagos obtenido correctamente"));
    }

    @DeleteMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID paymentId) {
        paymentService.delete(paymentId);
        return ResponseEntity.ok(ApiResponse.ok("Pago eliminado correctamente"));
    }
}