package com.floowroom.controller;

import com.floowroom.dto.AgendaSalaDTO;
import com.floowroom.entity.Usuario;
import com.floowroom.service.AgendaSalaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/agenda")
@RequiredArgsConstructor
@Tag(name = "Agenda de Salas", description = "Agendamento e controle de disponibilidade de salas")
public class AgendaSalaController {

    private final AgendaSalaService agendaSalaService;

    @Operation(summary = "Listar todos os agendamentos")
    @GetMapping
    public ResponseEntity<List<AgendaSalaDTO.Response>> listar() {
        return ResponseEntity.ok(agendaSalaService.listarTodos());
    }

    @Operation(summary = "Buscar agendamento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<AgendaSalaDTO.Response> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(agendaSalaService.buscarPorId(id));
    }

    @Operation(summary = "Listar agendamentos de uma sala")
    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<AgendaSalaDTO.Response>> listarPorSala(@PathVariable Long salaId) {
        return ResponseEntity.ok(agendaSalaService.listarPorSala(salaId));
    }

    @Operation(summary = "Consultar disponibilidade de uma sala num período",
               description = "Retorna os horários OCUPADOS. Períodos ausentes = disponíveis.")
    @GetMapping("/disponibilidade/{salaId}")
    public ResponseEntity<List<AgendaSalaDTO.Response>> disponibilidade(
            @PathVariable Long salaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(agendaSalaService.consultarDisponibilidade(salaId, inicio, fim));
    }

    @Operation(summary = "Criar agendamento")
    @PostMapping
    public ResponseEntity<AgendaSalaDTO.Response> criar(
            @Valid @RequestBody AgendaSalaDTO.Request req,
            @AuthenticationPrincipal Usuario logado) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(agendaSalaService.criar(req, logado.getUsuarioId()));
    }

    @Operation(summary = "Atualizar agendamento")
    @PutMapping("/{id}")
    public ResponseEntity<AgendaSalaDTO.Response> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AgendaSalaDTO.Request req,
            @AuthenticationPrincipal Usuario logado) {
        return ResponseEntity.ok(agendaSalaService.atualizar(id, req, logado.getUsuarioId()));
    }

    @Operation(summary = "Cancelar (excluir) agendamento")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        agendaSalaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
