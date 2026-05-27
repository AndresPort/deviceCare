package com.deviceCare.deviceCare.modules.auth;

import com.deviceCare.deviceCare.common.dto.ApiResponse;
import com.deviceCare.deviceCare.modules.auth.dto.LoginRequest;
import com.deviceCare.deviceCare.modules.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        TokenResponse tokens = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(tokens, "Login exitoso"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @RequestHeader("Refresh-Token") String refreshToken) {

        TokenResponse tokens = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.ok(tokens, "Token renovado"));
    }
}