package com.floowroom.repository;

import com.floowroom.entity.PessoaTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaTipoRepository extends JpaRepository<PessoaTipo, Long> {
    boolean existsByNome(String nome);
}
