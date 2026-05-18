package com.floowroom.service;

import com.floowroom.dto.DominioDTO;
import com.floowroom.entity.EventoTipo;
import com.floowroom.entity.PessoaTipo;
import com.floowroom.exception.DadoDuplicadoException;
import com.floowroom.exception.RecursoNaoEncontradoException;
import com.floowroom.repository.EventoTipoRepository;
import com.floowroom.repository.PessoaTipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DominioService {

    private final PessoaTipoRepository pessoaTipoRepository;
    private final EventoTipoRepository eventoTipoRepository;

    // ---- PessoaTipo ----
    public List<DominioDTO.PessoaTipoResponse> listarPessoaTipos() {
        return pessoaTipoRepository.findAll().stream().map(this::toPessoaTipoResp).toList();
    }

    @Transactional
    public DominioDTO.PessoaTipoResponse criarPessoaTipo(DominioDTO.PessoaTipoRequest req) {
        if (pessoaTipoRepository.existsByNome(req.getNome())) {
            throw new DadoDuplicadoException("Tipo já cadastrado: " + req.getNome());
        }
        PessoaTipo pt = pessoaTipoRepository.save(PessoaTipo.builder().nome(req.getNome()).build());
        return toPessoaTipoResp(pt);
    }

    @Transactional
    public void deletarPessoaTipo(Long id) {
        if (!pessoaTipoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("PessoaTipo não encontrado: " + id);
        }
        pessoaTipoRepository.deleteById(id);
    }

    // ---- EventoTipo ----
    public List<DominioDTO.EventoTipoResponse> listarEventoTipos() {
        return eventoTipoRepository.findAll().stream().map(this::toEventoTipoResp).toList();
    }

    @Transactional
    public DominioDTO.EventoTipoResponse criarEventoTipo(DominioDTO.EventoTipoRequest req) {
        if (eventoTipoRepository.existsByNome(req.getNome())) {
            throw new DadoDuplicadoException("Tipo já cadastrado: " + req.getNome());
        }
        EventoTipo et = eventoTipoRepository.save(EventoTipo.builder().nome(req.getNome()).build());
        return toEventoTipoResp(et);
    }

    @Transactional
    public void deletarEventoTipo(Long id) {
        if (!eventoTipoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("EventoTipo não encontrado: " + id);
        }
        eventoTipoRepository.deleteById(id);
    }

    // --- helpers ---
    private DominioDTO.PessoaTipoResponse toPessoaTipoResp(PessoaTipo p) {
        DominioDTO.PessoaTipoResponse r = new DominioDTO.PessoaTipoResponse();
        r.setPessoaTipoId(p.getPessoaTipoId());
        r.setNome(p.getNome());
        return r;
    }

    private DominioDTO.EventoTipoResponse toEventoTipoResp(EventoTipo e) {
        DominioDTO.EventoTipoResponse r = new DominioDTO.EventoTipoResponse();
        r.setEventoTipoId(e.getEventoTipoId());
        r.setNome(e.getNome());
        return r;
    }
}
