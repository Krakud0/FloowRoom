package com.floowroom.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabela: dominio.tbEventoTipo
 */
@Entity
@Table(name = "tbEventoTipo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoTipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evento_tipo_id")
    private Long eventoTipoId;

    @Column(nullable = false, unique = true, length = 200)
    private String nome;
}
