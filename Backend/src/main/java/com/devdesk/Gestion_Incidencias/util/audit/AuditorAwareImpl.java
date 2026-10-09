package com.devdesk.Gestion_Incidencias.util.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Si no hay sesión (ej. comandos en terminal o procesos batch), firmamos como SISTEMA
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("SISTEMA");
        }

        // Si hay un usuario logueado, Spring Security nos da su nombre de usuario/correo
        return Optional.of(authentication.getName());
    }
}