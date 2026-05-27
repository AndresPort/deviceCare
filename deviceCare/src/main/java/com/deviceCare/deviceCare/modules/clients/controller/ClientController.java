package com.deviceCare.deviceCare.modules.clients.controller;

import com.deviceCare.deviceCare.common.dto.ApiResponse;
import com.deviceCare.deviceCare.modules.clients.dto.ClientRequest;
import com.deviceCare.deviceCare.modules.clients.dto.ClientResponse;
import com.deviceCare.deviceCare.modules.clients.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION')")
    public ResponseEntity<ApiResponse<ClientResponse>> create(
            @Valid @RequestBody ClientRequest request) {
        ClientResponse client = clientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(client, "Cliente creado correctamente"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> findAll() {
        List<ClientResponse> clients = clientService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(clients, "Clientes obtenidos correctamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<ClientResponse>> findById(@PathVariable UUID id) {
        ClientResponse client = clientService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(client, "Cliente obtenido correctamente"));
    }

    @GetMapping("/document/{documentNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<ClientResponse>> findByDocument(
            @PathVariable String documentNumber) {
        ClientResponse client = clientService.findByDocumentNumber(documentNumber);
        return ResponseEntity.ok(ApiResponse.ok(client, "Cliente obtenido correctamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTION')")
    public ResponseEntity<ApiResponse<ClientResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ClientRequest request) {
        ClientResponse client = clientService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok(client, "Cliente actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        clientService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Cliente eliminado correctamente"));
    }
}