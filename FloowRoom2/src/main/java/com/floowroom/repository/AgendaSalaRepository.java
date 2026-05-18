package com.floowroom.repository;

import com.floowroom.entity.AgendaSala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendaSalaRepository extends JpaRepository<AgendaSala, Long> {

    List<AgendaSala> findBySalaSalaIdOrderByDatahoraInicioAsc(Long salaId);

    List<AgendaSala> findByPessoapessoaIdOrderByDatahoraInicioDesc(Long pessoaId);

    /**
     * Verifica sobreposição de horário para uma mesma sala.
     * Exclui opcionalmente um agendamento (para edição).
     */
    @Query("""
        SELECT a FROM AgendaSala a
        WHERE a.sala.salaId = :salaId
          AND (:excludeId IS NULL OR a.agendaSalaId <> :excludeId)
          AND a.datahoraInicio < :datahoraFim
          AND a.datahoraFim   > :datahoraInicio
        """)
    List<AgendaSala> findConflitos(
        @Param("salaId")         Long salaId,
        @Param("datahoraInicio") LocalDateTime datahoraInicio,
        @Param("datahoraFim")    LocalDateTime datahoraFim,
        @Param("excludeId")      Long excludeId
    );

    /**
     * Busca agendamentos de uma sala num intervalo (disponibilidade).
     */
    @Query("""
        SELECT a FROM AgendaSala a
        WHERE a.sala.salaId = :salaId
          AND a.datahoraInicio >= :inicio
          AND a.datahoraFim   <= :fim
        ORDER BY a.datahoraInicio ASC
        """)
    List<AgendaSala> findBySalaEPeriodo(
        @Param("salaId") Long salaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim")    LocalDateTime fim
    );
}
