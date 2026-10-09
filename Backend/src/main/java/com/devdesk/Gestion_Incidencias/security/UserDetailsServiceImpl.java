package com.devdesk.Gestion_Incidencias.security;

import com.devdesk.Gestion_Incidencias.core.usuario.Usuario;
import com.devdesk.Gestion_Incidencias.core.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String identificador) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(identificador)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas."));

        String rol = usuario.getRol() != null ? usuario.getRol().name() : "SIN_ROL";

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + rol)))
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .build();
    }
}