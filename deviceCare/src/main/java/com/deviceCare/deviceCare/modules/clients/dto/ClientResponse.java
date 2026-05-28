package com.deviceCare.deviceCare.modules.clients.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ClientResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String documentNumber;
    private String phone;
    private String email;
    private String address;
    private UUID userId;
    private Instant createdAt;
}