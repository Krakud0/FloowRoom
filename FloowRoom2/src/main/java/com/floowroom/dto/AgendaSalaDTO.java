package com.floowroom.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

// ============================================================
//  AgendaSala DTOs  (agendamento.tbAgendaSala)
// ============================================================

public class AgendaSalaDTO {

    @Data
    public static class Request {
        @NotNull(message = "ID da sala é obrigatório")
        private Long salaId;

        private Long pessoaId;

        private Long eventoTipoId;

        @NotNull(message = "Data/hora de início é obrigatória")
        private LocalDateTime datahoraInicio;

        @NotNull(message = "Data/hora de fim é obrigatória")
        private LocalDateTime datahoraFim;

        @Size(max = 255, message = "Observação deve ter no máximo 255 caracteres")
        private String observacao;
    }

    @Data
    public static class Response {
        private Long agendaSalaId;
        private Long salaId;
        private Integer salaNumero;
        private Long pessoaId;
        private String pessoaNome;
        private Long eventoTipoId;
        private String eventoTipoNome;
        private LocalDateTime datahoraInicio;
        private LocalDateTime datahoraFim;
        private String observacao;
        private String atualizadoPorNome;
        private LocalDateTime atualizadoEm;
    }

    /** Representa um slot livre de disponibilidade */
    @Data
    public static class SlotLivre {
        private LocalDateTime de;
        private LocalDateTime ate;
    }
}
