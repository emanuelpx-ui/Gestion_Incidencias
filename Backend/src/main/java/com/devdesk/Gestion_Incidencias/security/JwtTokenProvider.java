package com.devdesk.Gestion_Incidencias.security;

import com.devdesk.Gestion_Incidencias.core.usuario.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final SecretKey claveFirma;
    private final long vigenciaMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration:3600000}") long vigenciaMs) {
        this.claveFirma = construirClave(secret);
        this.vigenciaMs = vigenciaMs;
    }

    public String generarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol() != null ? usuario.getRol().name() : null);
        claims.put("nombre", usuario.getNombre());

        Date ahora = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getCorreo())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + vigenciaMs))
                .signWith(claveFirma)
                .compact();
    }

    public String extraerIdentificador(String token) {
        Claims claims = parsear(token);
        return claims != null ? claims.getSubject() : null;
    }

    private Claims parsear(String token) {
        try {
            return Jwts.parser().verifyWith(claveFirma).build().parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token rechazado durante la validación.");
        }
        return null;
    }

    private SecretKey construirClave(String secret) {
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(secret.trim());
        } catch (IllegalArgumentException e) {
            bytes = secret.trim().getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(bytes);
    }
}