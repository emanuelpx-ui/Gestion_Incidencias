package com.devdesk.Gestion_Incidencias.security.auth;

import com.devdesk.Gestion_Incidencias.core.usuario.Usuario;
import com.devdesk.Gestion_Incidencias.core.usuario.UsuarioRepository;
import com.devdesk.Gestion_Incidencias.exception.MessageConstants;
import com.devdesk.Gestion_Incidencias.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AuthDTOs.JwtResponse iniciarSesion(AuthDTOs.LoginRequest request) {
        Usuario usuario = usuarioRepository
                .findByCorreo(request.identificador()) // Ajustado a tu UsuarioRepository actual
                .orElseThrow(() -> new IllegalArgumentException(MessageConstants.CREDENCIALES_INVALIDAS));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new IllegalArgumentException(MessageConstants.CREDENCIALES_INVALIDAS);
        }

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new IllegalArgumentException(MessageConstants.USUARIO_INACTIVO);
        }

        String token = tokenProvider.generarToken(usuario);
        Long areaId = usuario.getArea() != null ? usuario.getArea().getId() : null;

        return new AuthDTOs.JwtResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getRol() != null ? usuario.getRol().name() : null,
                token,
                "Autenticación exitosa.",
                areaId
        );
    }
}