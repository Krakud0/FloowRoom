package com.floowroom.controller;

import com.floowroom.dto.AuthDTO;
import com.floowroom.entity.Usuario;
import com.floowroom.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Registro e login de usuários")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar novo usuário")
    @PostMapping("/register")
    public ResponseEntity<AuthDTO.AuthResponse> registrar(
            @Valid @RequestBody AuthDTO.RegisterRequest req,
            @AuthenticationPrincipal Usuario logado) {
        Long executorId = logado != null ? logado.getUsuarioId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(req, executorId));
    }

    @Operation(summary = "Login (retorna token JWT)")
    @PostMapping("/login")
    public ResponseEntity<AuthDTO.AuthResponse> login(@Valid @RequestBody AuthDTO.LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @Operation(summary = "Health check — usado pelo Render para verificar o serviço")
    @GetMapping("/health")
    public ResponseEntity<java.util.Map<String, String>> health() {
        return ResponseEntity.ok(java.util.Map.of("status", "UP", "service", "FloowRoom API"));
    }
}
