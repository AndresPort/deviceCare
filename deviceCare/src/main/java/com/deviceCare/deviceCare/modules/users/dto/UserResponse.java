package com.deviceCare.deviceCare.modules.users.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean active;
    private Set<String> roles;
    private Instant createdAt;
}