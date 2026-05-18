package com.floowroom.repository;

import com.floowroom.entity.EventoTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoTipoRepository extends JpaRepository<EventoTipo, Long> {
    boolean existsByNome(String nome);
}
