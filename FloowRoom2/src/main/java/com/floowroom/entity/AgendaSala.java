package com.floowroom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tabela: agendamento.tbAgendaSala
 */
@Entity
@Table(
    name = "tbAgendaSala",
    indexes = {
        @Index(name = "idx_agenda_sala_horario", columnList = "sala_id, datahora_inicio, datahora_fim")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaSala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agenda_sala_id")
    private Long agendaSalaId;

    @Column(name = "datahora_inicio", nullable = false)
    private LocalDateTime datahoraInicio;

    @Column(name = "datahora_fim", nullable = false)
    private LocalDateTime datahoraFim;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_tipo_id")
    private EventoTipo eventoTipo;

    @Column(length = 255)
    private String observacao;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atualizado_por")
    private Usuario atualizadoPor;

    @PrePersist
    @PreUpdate
    public void preSave() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
