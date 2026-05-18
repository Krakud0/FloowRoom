package com.floowroom.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

// ============================================================
//  Pessoa DTOs  (cadastro.tbPessoas)
// ============================================================

public class PessoaDTO {

    @Data
    public static class Request {
        @NotBlank(message = "Nome é obrigatório")
        private String nome;

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF inválido (formato: 000.000.000-00)")
        private String cpf;

        private LocalDate nascimento;

        @Pattern(regexp = "^[\\d\\s()+-]{8,20}$", message = "Telefone inválido")
        private String telefone;

        private Long pessoaTipoId;
    }

    @Data
    public static class Response {
        private Long pessoaId;
        private String nome;
        private String cpf;
        private LocalDate nascimento;
        private String telefone;
        private String pessoaTipoNome;
        private LocalDate atualizadoEm;
    }
}
