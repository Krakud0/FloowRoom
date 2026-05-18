package com.floowroom.controller;

import com.floowroom.dto.PessoaDTO;
import com.floowroom.entity.Usuario;
import com.floowroom.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pessoas")
@RequiredArgsConstructor
@Tag(name = "Pessoas", description = "Gestão de pessoas / locatários")
public class PessoaController {

    private final PessoaService pessoaService;

    @Operation(summary = "Listar todas as pessoas")
    @GetMapping
    public ResponseEntity<List<PessoaDTO.Response>> listar() {
        return ResponseEntity.ok(pessoaService.listarTodas());
    }

    @Operation(summary = "Buscar pessoa por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PessoaDTO.Response> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(pessoaService.buscarPorId(id));
    }

    @Operation(summary = "Criar pessoa")
    @PostMapping
    public ResponseEntity<PessoaDTO.Response> criar(
            @Valid @RequestBody PessoaDTO.Request req,
            @AuthenticationPrincipal Usuario logado) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pessoaService.criar(req, logado.getUsuarioId()));
    }

    @Operation(summary = "Atualizar pessoa")
    @PutMapping("/{id}")
    public ResponseEntity<PessoaDTO.Response> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PessoaDTO.Request req,
            @AuthenticationPrincipal Usuario logado) {
        return ResponseEntity.ok(pessoaService.atualizar(id, req, logado.getUsuarioId()));
    }

    @Operation(summary = "Deletar pessoa")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pessoaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
