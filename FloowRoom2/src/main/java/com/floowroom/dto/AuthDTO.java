package com.floowroom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// ============================================================
//  Auth DTOs  (login via "login" + senha)
// ============================================================

public class AuthDTO {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Nome é obrigatório")
        private String nome;

        @NotBlank(message = "Login é obrigatório")
        @Size(max = 50, message = "Login deve ter no máximo 50 caracteres")
        private String login;

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter ao menos 6 caracteres")
        private String senha;
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Login é obrigatório")
        private String login;

        @NotBlank(message = "Senha é obrigatória")
        private String senha;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String tipo = "Bearer";
        private Long usuarioId;
        private String nome;
        private String login;
    }
}
