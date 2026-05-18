package com.floowroom.repository;

import com.floowroom.entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    Optional<Sala> findByNumero(Integer numero);
    boolean existsByNumero(Integer numero);
}
