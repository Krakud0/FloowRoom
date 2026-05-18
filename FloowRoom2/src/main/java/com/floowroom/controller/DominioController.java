package com.floowroom.controller;

import com.floowroom.dto.DominioDTO;
import com.floowroom.service.DominioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dominio")
@RequiredArgsConstructor
@Tag(name = "Domínio", description = "Tabelas de domínio: tipos de pessoa e tipos de evento")
public class DominioController {

    private final DominioService dominioService;

    // ---- PessoaTipo ----
    @Operation(summary = "Listar tipos de pessoa")
    @GetMapping("/pessoa-tipos")
    public ResponseEntity<List<DominioDTO.PessoaTipoResponse>> listarPessoaTipos() {
        return ResponseEntity.ok(dominioService.listarPessoaTipos());
    }

    @Operation(summary = "Criar tipo de pessoa")
    @PostMapping("/pessoa-tipos")
    public ResponseEntity<DominioDTO.PessoaTipoResponse> criarPessoaTipo(
            @Valid @RequestBody DominioDTO.PessoaTipoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dominioService.criarPessoaTipo(req));
    }

    @Operation(summary = "Deletar tipo de pessoa")
    @DeleteMapping("/pessoa-tipos/{id}")
    public ResponseEntity<Void> deletarPessoaTipo(@PathVariable Long id) {
        dominioService.deletarPessoaTipo(id);
        return ResponseEntity.noContent().build();
    }

    // ---- EventoTipo ----
    @Operation(summary = "Listar tipos de evento")
    @GetMapping("/evento-tipos")
    public ResponseEntity<List<DominioDTO.EventoTipoResponse>> listarEventoTipos() {
        return ResponseEntity.ok(dominioService.listarEventoTipos());
    }

    @Operation(summary = "Criar tipo de evento")
    @PostMapping("/evento-tipos")
    public ResponseEntity<DominioDTO.EventoTipoResponse> criarEventoTipo(
            @Valid @RequestBody DominioDTO.EventoTipoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dominioService.criarEventoTipo(req));
    }

    @Operation(summary = "Deletar tipo de evento")
    @DeleteMapping("/evento-tipos/{id}")
    public ResponseEntity<Void> deletarEventoTipo(@PathVariable Long id) {
        dominioService.deletarEventoTipo(id);
        return ResponseEntity.noContent().build();
    }
}
