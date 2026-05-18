package com.floowroom.service;

import com.floowroom.dto.SalaDTO;
import com.floowroom.entity.Sala;
import com.floowroom.entity.Usuario;
import com.floowroom.exception.DadoDuplicadoException;
import com.floowroom.exception.RecursoNaoEncontradoException;
import com.floowroom.repository.SalaRepository;
import com.floowroom.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<SalaDTO.Response> listarTodas() {
        return salaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public SalaDTO.Response buscarPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public SalaDTO.Response criar(SalaDTO.Request req, Long usuarioId) {
        if (salaRepository.existsByNumero(req.getNumero())) {
            throw new DadoDuplicadoException("Sala " + req.getNumero() + " já cadastrada");
        }
        Sala sala = Sala.builder()
                .numero(req.getNumero())
                .atualizadoPor(resolverUsuario(usuarioId))
                .build();
        return toResponse(salaRepository.save(sala));
    }

    @Transactional
    public SalaDTO.Response atualizar(Long id, SalaDTO.Request req, Long usuarioId) {
        Sala sala = buscar(id);
        if (!sala.getNumero().equals(req.getNumero()) && salaRepository.existsByNumero(req.getNumero())) {
            throw new DadoDuplicadoException("Sala " + req.getNumero() + " já cadastrada");
        }
        sala.setNumero(req.getNumero());
        sala.setAtualizadoPor(resolverUsuario(usuarioId));
        return toResponse(salaRepository.save(sala));
    }

    @Transactional
    public void deletar(Long id) {
        if (!salaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Sala não encontrada: " + id);
        }
        salaRepository.deleteById(id);
    }

    // --- helpers ---
    private Sala buscar(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada: " + id));
    }

    private Usuario resolverUsuario(Long usuarioId) {
        if (usuarioId == null) return null;
        return usuarioRepository.findById(usuarioId).orElse(null);
    }

    private SalaDTO.Response toResponse(Sala s) {
        SalaDTO.Response r = new SalaDTO.Response();
        r.setSalaId(s.getSalaId());
        r.setNumero(s.getNumero());
        r.setAtualizadoEm(s.getAtualizadoEm());
        if (s.getAtualizadoPor() != null) r.setAtualizadoPorNome(s.getAtualizadoPor().getNome());
        return r;
    }
}
