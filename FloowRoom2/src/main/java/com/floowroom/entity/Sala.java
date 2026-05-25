package com.floowroom.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tabela: cadastros.tbSalas
 */
@Entity
@Table(name = "tbSalas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sala {

    @Id
    @Column(name = "sala_id")
    private Long salaId;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atualizado_por")
    private Usuario atualizadoPor;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    public void preSave() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
