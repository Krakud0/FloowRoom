package com.floowroom.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

// ============================================================
//  Sala DTOs  (cadastros.tbSalas)
// ============================================================

public class SalaDTO {

    @Data
    public static class Request {
        @NotNull(message = "Número da sala é obrigatório")
        @Min(value = 1, message = "Número da sala deve ser maior que 0")
        private Integer numero;
    }

    @Data
    public static class Response {
        private Long salaId;
        private Integer numero;
        private String atualizadoPorNome;
        private LocalDateTime atualizadoEm;
    }
}
