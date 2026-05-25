package com.floowroom.controller;

import com.floowroom.dto.SalaDTO;
import com.floowroom.entity.Usuario;
import com.floowroom.service.SalaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor
@Tag(name = "Salas", description = "Gestão de salas disponíveis")
public class SalaController {

    private final SalaService salaService;

    @Operation(summary = "Listar todas as salas")
    @GetMapping
    public ResponseEntity<List<SalaDTO.Response>> listar() {
        return ResponseEntity.ok(salaService.listarTodas());
    }

    @Operation(summary = "Buscar sala por ID")
    @GetMapping("/{id}")
    public ResponseEntity<SalaDTO.Response> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.buscarPorId(id));
    }

    @Operation(summary = "Criar sala")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalaDTO.Response> criar(
            @Valid @RequestBody SalaDTO.Request req,
            @AuthenticationPrincipal Usuario logado) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(salaService.criar(req, logado.getUsuarioId()));
    }

    @Operation(summary = "Atualizar sala")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalaDTO.Response> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody SalaDTO.Request req,
            @AuthenticationPrincipal Usuario logado) {
        return ResponseEntity.ok(salaService.atualizar(id, req, logado.getUsuarioId()));
    }

    @Operation(summary = "Deletar sala")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        salaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
