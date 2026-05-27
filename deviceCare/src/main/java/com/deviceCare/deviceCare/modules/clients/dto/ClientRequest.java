package com.deviceCare.deviceCare.modules.clients.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ClientRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @NotBlank(message = "El documento es obligatorio")
    private String documentNumber;

    private String phone;
    private String email;
    private String address;

    // Opcional: vincular a un usuario existente del sistema
    private UUID userId;
}