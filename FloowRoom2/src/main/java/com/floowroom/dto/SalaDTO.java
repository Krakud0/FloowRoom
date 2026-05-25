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
        @NotNull(message = "ID da sala é obrigatório")
        @Min(value = 1000, message = "ID da sala deve possuir 4 dígitos (entre 1000 e 9999)")
        @Max(value = 9999, message = "ID da sala deve possuir 4 dígitos (entre 1000 e 9999)")
        private Long salaId;

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
