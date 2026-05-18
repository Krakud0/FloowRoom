package com.floowroom.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// ============================================================
//  PessoaTipo e EventoTipo DTOs  (tabelas de domínio)
// ============================================================

public class DominioDTO {

    @Data
    public static class PessoaTipoRequest {
        @NotBlank(message = "Nome é obrigatório")
        private String nome;
    }

    @Data
    public static class PessoaTipoResponse {
        private Long pessoaTipoId;
        private String nome;
    }

    @Data
    public static class EventoTipoRequest {
        @NotBlank(message = "Nome é obrigatório")
        private String nome;
    }

    @Data
    public static class EventoTipoResponse {
        private Long eventoTipoId;
        private String nome;
    }
}
