package com.floowroom.service;

import com.floowroom.dto.PessoaDTO;
import com.floowroom.entity.Pessoa;
import com.floowroom.entity.PessoaTipo;
import com.floowroom.entity.Usuario;
import com.floowroom.exception.DadoDuplicadoException;
import com.floowroom.exception.RecursoNaoEncontradoException;
import com.floowroom.repository.PessoaRepository;
import com.floowroom.repository.PessoaTipoRepository;
import com.floowroom.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final PessoaTipoRepository pessoaTipoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<PessoaDTO.Response> listarTodas() {
        return pessoaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PessoaDTO.Response buscarPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public PessoaDTO.Response criar(PessoaDTO.Request req, Long usuarioId) {
        if (pessoaRepository.existsByCpf(req.getCpf())) {
            throw new DadoDuplicadoException("CPF já cadastrado: " + req.getCpf());
        }
        Pessoa p = Pessoa.builder()
                .nome(req.getNome())
                .cpf(req.getCpf())
                .nascimento(req.getNascimento())
                .telefone(req.getTelefone())
                .pessoaTipo(resolverTipo(req.getPessoaTipoId()))
                .atualizadoPor(resolverUsuario(usuarioId))
                .build();
        return toResponse(pessoaRepository.save(p));
    }

    @Transactional
    public PessoaDTO.Response atualizar(Long id, PessoaDTO.Request req, Long usuarioId) {
        Pessoa p = buscar(id);
        if (!p.getCpf().equals(req.getCpf()) && pessoaRepository.existsByCpf(req.getCpf())) {
            throw new DadoDuplicadoException("CPF já cadastrado: " + req.getCpf());
        }
        p.setNome(req.getNome());
        p.setCpf(req.getCpf());
        p.setNascimento(req.getNascimento());
        p.setTelefone(req.getTelefone());
        p.setPessoaTipo(resolverTipo(req.getPessoaTipoId()));
        p.setAtualizadoPor(resolverUsuario(usuarioId));
        return toResponse(pessoaRepository.save(p));
    }

    @Transactional
    public void deletar(Long id) {
        if (!pessoaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Pessoa não encontrada: " + id);
        }
        pessoaRepository.deleteById(id);
    }

    // --- helpers ---
    private Pessoa buscar(Long id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada: " + id));
    }

    private PessoaTipo resolverTipo(Long id) {
        if (id == null) return null;
        return pessoaTipoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("PessoaTipo não encontrado: " + id));
    }

    private Usuario resolverUsuario(Long id) {
        if (id == null) return null;
        return usuarioRepository.findById(id).orElse(null);
    }

    private PessoaDTO.Response toResponse(Pessoa p) {
        PessoaDTO.Response r = new PessoaDTO.Response();
        r.setPessoaId(p.getPessoaId());
        r.setNome(p.getNome());
        r.setCpf(p.getCpf());
        r.setNascimento(p.getNascimento());
        r.setTelefone(p.getTelefone());
        r.setAtualizadoEm(p.getAtualizadoEm());
        if (p.getPessoaTipo() != null) r.setPessoaTipoNome(p.getPessoaTipo().getNome());
        return r;
    }
}
