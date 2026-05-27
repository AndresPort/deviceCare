package com.deviceCare.deviceCare.modules.auth;

import com.deviceCare.deviceCare.common.exception.BusinessException;
import com.deviceCare.deviceCare.modules.auth.dto.LoginRequest;
import com.deviceCare.deviceCare.modules.auth.dto.TokenResponse;
import com.deviceCare.deviceCare.modules.users.model.User;
import com.deviceCare.deviceCare.modules.users.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> BusinessException.notFound("Usuario no encontrado"));

        if (!user.isActive()) {
            throw BusinessException.forbidden("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw BusinessException.badRequest("Credenciales incorrectas");
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        String accessToken = buildToken(user.getId().toString(), roles, expiration);
        String refreshToken = buildToken(user.getId().toString(), roles, refreshExpiration);

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refresh(String refreshToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            String userId = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload()
                    .getSubject();

            User user = userRepository.findByIdAndDeletedAtIsNull(java.util.UUID.fromString(userId))
                    .orElseThrow(() -> BusinessException.notFound("Usuario no encontrado"));

            List<String> roles = user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList());

            String newAccessToken = buildToken(userId, roles, expiration);

            return new TokenResponse(newAccessToken, refreshToken);

        } catch (Exception e) {
            throw BusinessException.badRequest("Token inválido o expirado");
        }
    }

    private String buildToken(String userId, List<String> roles, long expirationMs) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(userId)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }
}