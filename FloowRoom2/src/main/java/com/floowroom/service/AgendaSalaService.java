package com.floowroom.service;

import com.floowroom.dto.AgendaSalaDTO;
import com.floowroom.entity.*;
import com.floowroom.exception.ConflitoPeriodoException;
import com.floowroom.exception.RecursoNaoEncontradoException;
import com.floowroom.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaSalaService {

    private final AgendaSalaRepository agendaSalaRepository;
    private final SalaRepository salaRepository;
    private final PessoaRepository pessoaRepository;
    private final EventoTipoRepository eventoTipoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<AgendaSalaDTO.Response> listarTodos() {
        return agendaSalaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public AgendaSalaDTO.Response buscarPorId(Long id) {
        return toResponse(buscar(id));
    }

    public List<AgendaSalaDTO.Response> listarPorSala(Long salaId) {
        return agendaSalaRepository.findBySalaSalaIdOrderByDatahoraInicioAsc(salaId)
                .stream().map(this::toResponse).toList();
    }

    /**
     * Retorna os slots OCUPADOS de uma sala num intervalo.
     * O front pode calcular os livres a partir disso.
     */
    public List<AgendaSalaDTO.Response> consultarDisponibilidade(Long salaId,
                                                                 LocalDateTime inicio,
                                                                 LocalDateTime fim) {
        return agendaSalaRepository.findBySalaEPeriodo(salaId, inicio, fim)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public AgendaSalaDTO.Response criar(AgendaSalaDTO.Request req, Long usuarioId) {
        validarPeriodo(req.getDatahoraInicio(), req.getDatahoraFim());
        verificarConflito(req.getSalaId(), req.getDatahoraInicio(), req.getDatahoraFim(), null);

        AgendaSala agenda = AgendaSala.builder()
                .sala(resolverSala(req.getSalaId()))
                .pessoa(resolverPessoa(req.getPessoaId()))
                .eventoTipo(resolverEventoTipo(req.getEventoTipoId()))
                .datahoraInicio(req.getDatahoraInicio())
                .datahoraFim(req.getDatahoraFim())
                .observacao(req.getObservacao())
                .atualizadoPor(resolverUsuario(usuarioId))
                .build();

        return toResponse(agendaSalaRepository.save(agenda));
    }

    @Transactional
    public AgendaSalaDTO.Response atualizar(Long id, AgendaSalaDTO.Request req, Long usuarioId) {
        AgendaSala agenda = buscar(id);
        validarPeriodo(req.getDatahoraInicio(), req.getDatahoraFim());
        verificarConflito(req.getSalaId(), req.getDatahoraInicio(), req.getDatahoraFim(), id);

        agenda.setSala(resolverSala(req.getSalaId()));
        agenda.setPessoa(resolverPessoa(req.getPessoaId()));
        agenda.setEventoTipo(resolverEventoTipo(req.getEventoTipoId()));
        agenda.setDatahoraInicio(req.getDatahoraInicio());
        agenda.setDatahoraFim(req.getDatahoraFim());
        agenda.setObservacao(req.getObservacao());
        agenda.setAtualizadoPor(resolverUsuario(usuarioId));

        return toResponse(agendaSalaRepository.save(agenda));
    }

    @Transactional
    public void cancelar(Long id) {
        if (!agendaSalaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Agendamento não encontrado: " + id);
        }
        agendaSalaRepository.deleteById(id);
    }

    // --- validações ---
    private void validarPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("A data/hora de fim deve ser após a de início");
        }
    }

    private void verificarConflito(Long salaId, LocalDateTime inicio, LocalDateTime fim, Long excludeId) {
        List<AgendaSala> conflitos = agendaSalaRepository.findConflitos(salaId, inicio, fim, excludeId);
        if (!conflitos.isEmpty()) {
            AgendaSala c = conflitos.get(0);
            throw new ConflitoPeriodoException(
                "Conflito de horário: sala já agendada de %s até %s".formatted(
                    c.getDatahoraInicio(), c.getDatahoraFim()));
        }
    }

    // --- helpers ---
    private AgendaSala buscar(Long id) {
        return agendaSalaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado: " + id));
    }

    private Sala resolverSala(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada: " + id));
    }

    private Pessoa resolverPessoa(Long id) {
        if (id == null) return null;
        return pessoaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada: " + id));
    }

    private EventoTipo resolverEventoTipo(Long id) {
        if (id == null) return null;
        return eventoTipoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("EventoTipo não encontrado: " + id));
    }

    private Usuario resolverUsuario(Long id) {
        if (id == null) return null;
        return usuarioRepository.findById(id).orElse(null);
    }

    private AgendaSalaDTO.Response toResponse(AgendaSala a) {
        AgendaSalaDTO.Response r = new AgendaSalaDTO.Response();
        r.setAgendaSalaId(a.getAgendaSalaId());
        r.setDatahoraInicio(a.getDatahoraInicio());
        r.setDatahoraFim(a.getDatahoraFim());
        r.setObservacao(a.getObservacao());
        r.setAtualizadoEm(a.getAtualizadoEm());
        if (a.getSala() != null) {
            r.setSalaId(a.getSala().getSalaId());
            r.setSalaNumero(a.getSala().getNumero());
        }
        if (a.getPessoa() != null) {
            r.setPessoaId(a.getPessoa().getPessoaId());
            r.setPessoaNome(a.getPessoa().getNome());
        }
        if (a.getEventoTipo() != null) {
            r.setEventoTipoId(a.getEventoTipo().getEventoTipoId());
            r.setEventoTipoNome(a.getEventoTipo().getNome());
        }
        if (a.getAtualizadoPor() != null) {
            r.setAtualizadoPorNome(a.getAtualizadoPor().getNome());
        }
        return r;
    }
}
