package com.devdesk.Gestion_Incidencias.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final Set<String> RUTAS_PROTEGIDAS = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/registro"
    );

    private final Map<String, Bucket> cubetas = new ConcurrentHashMap<>();

    private final boolean habilitado;
    private final long capacidad;
    private final Duration periodoRecarga;

    public RateLimitingFilter(
            @Value("${app.rate-limit.enabled:true}") boolean habilitado,
            @Value("${app.rate-limit.capacity:5}") long capacidad,
            @Value("${app.rate-limit.refill-minutes:1}") long minutosRecarga) {
        this.habilitado = habilitado;
        this.capacidad = capacidad;
        this.periodoRecarga = Duration.ofMinutes(minutosRecarga);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String clave = obtenerIpCliente(request) + "|" + request.getServletPath();
        Bucket cubeta = cubetas.computeIfAbsent(clave, k -> crearCubeta());

        if (cubeta.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }

        log.warn("Límite de peticiones superado en {}", request.getServletPath());
        responderDemasiadasPeticiones(response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if (!habilitado || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        return !RUTAS_PROTEGIDAS.contains(request.getServletPath());
    }

    private Bucket crearCubeta() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacidad)
                        .refillIntervally(capacidad, periodoRecarga)
                        .build())
                .build();
    }

    private String obtenerIpCliente(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private void responderDemasiadasPeticiones(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {"success":false,"message":"Demasiados intentos. Espere unos minutos e intente de nuevo.","data":null}\
                """);
    }
}