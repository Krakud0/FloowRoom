package com.floowroom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Tabela: cadastro.tbPessoas
 * Representa o locatário/solicitante do agendamento.
 */
@Entity
@Table(schema = "cadastro", name = "tbPessoas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pessoa_id")
    private Long pessoaId;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    private LocalDate nascimento;

    @Column(length = 20)
    private String telefone;

    /** FK para cadastro.tbPessoaTipo (tipo de pessoa: professor, aluno, funcionário, etc.) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_tipo_id")
    private PessoaTipo pessoaTipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atualizado_por")
    private Usuario atualizadoPor;

    @Column(name = "atualizado_em")
    private LocalDate atualizadoEm;

    @PrePersist
    @PreUpdate
    public void preSave() {
        this.atualizadoEm = LocalDate.now();
    }
}
