package com.devdesk.Gestion_Incidencias.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig {

    private final List<String> origenesPermitidos;

    // Se asume que en tu application.properties o .env tienes: app.cors.allowed-origins=http://localhost:5173
    public WebConfig(@Value("${app.cors.allowed-origins:*}") String origenes) {
        this.origenesPermitidos = Arrays.stream(origenes.split(","))
                .map(String::trim)
                .filter(o -> !o.isEmpty())
                .toList();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        if (origenesPermitidos.contains("*")) {
            config.addAllowedOriginPattern("*");
        } else {
            config.setAllowedOrigins(origenesPermitidos);
        }

        config.setAllowedMethods(List.of("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        config.setExposedHeaders(List.of("Content-Disposition"));
        
        if (!origenesPermitidos.contains("*")) {
            config.setAllowCredentials(true);
        }

        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}