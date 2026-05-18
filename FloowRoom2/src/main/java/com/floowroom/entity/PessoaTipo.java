package com.floowroom.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabela: cadastro.tbPessoaTipo
 * Exemplos: Aluno, Professor, Funcionário, Visitante, etc.
 */
@Entity
@Table(schema = "cadastro", name = "tbPessoaTipo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PessoaTipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pessoa_tipo_id")
    private Long pessoaTipoId;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;
}
