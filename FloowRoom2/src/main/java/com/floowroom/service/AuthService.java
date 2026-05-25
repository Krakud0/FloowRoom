package com.floowroom.service;

import com.floowroom.dto.AuthDTO;
import com.floowroom.entity.Usuario;
import com.floowroom.exception.DadoDuplicadoException;
import com.floowroom.exception.RecursoNaoEncontradoException;
import com.floowroom.repository.UsuarioRepository;
import com.floowroom.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthDTO.AuthResponse registrar(AuthDTO.RegisterRequest req, Long executorId) {
        if (usuarioRepository.existsByLogin(req.getLogin())) {
            throw new DadoDuplicadoException("Login já cadastrado: " + req.getLogin());
        }

        Usuario executor = null;
        if (executorId != null) {
            executor = usuarioRepository.findById(executorId).orElse(null);
        }

        Usuario usuario = Usuario.builder()
                .nome(req.getNome())
                .login(req.getLogin())
                .senha(passwordEncoder.encode(req.getSenha()))
                .admin(Boolean.TRUE.equals(req.getAdmin()))
                .atualizadoPor(executor)
                .build();

        usuario = usuarioRepository.save(usuario);
        return toResponse(usuario, jwtUtil.gerarToken(usuario));
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getLogin(), req.getSenha()));

        UserDetails userDetails = usuarioRepository.findByLogin(req.getLogin())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        String token = jwtUtil.gerarToken(userDetails);
        Usuario usuario = (Usuario) userDetails;
        return toResponse(usuario, token);
    }

    private AuthDTO.AuthResponse toResponse(Usuario u, String token) {
        AuthDTO.AuthResponse resp = new AuthDTO.AuthResponse();
        resp.setToken(token);
        resp.setUsuarioId(u.getUsuarioId());
        resp.setNome(u.getNome());
        resp.setLogin(u.getLogin());
        resp.setAdmin(u.getAdmin());
        return resp;
    }
}
