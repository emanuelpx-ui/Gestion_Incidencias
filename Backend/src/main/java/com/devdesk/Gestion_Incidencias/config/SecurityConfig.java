package com.devdesk.Gestion_Incidencias.config;

import com.devdesk.Gestion_Incidencias.exception.MessageConstants;
import com.devdesk.Gestion_Incidencias.security.JwtAuthFilter;
import com.devdesk.Gestion_Incidencias.security.RateLimitingFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final RateLimitingFilter rateLimitingFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .headers(headers -> headers
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                    .contentTypeOptions(HeadersConfigurer.ContentTypeOptionsConfig::disable)
                    .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                    .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; frame-ancestors 'none'"))
                    .referrerPolicy(referrer -> referrer.policy(
                            org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
            )

            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> 
                            escribirError(response, HttpStatus.UNAUTHORIZED, MessageConstants.AUTENTICACION_REQUERIDA))
                    .accessDeniedHandler((request, response, accessDeniedException) -> 
                            escribirError(response, HttpStatus.FORBIDDEN, MessageConstants.ACCESO_DENEGADO))
            )

            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .requestMatchers("/api/salud").permitAll()
                    
                    // Rutas reservadas para administradores
                    .requestMatchers("/api/v1/dashboard/**").hasRole("ADMINISTRADOR")
                    .requestMatchers("/api/v1/areas/**").hasRole("ADMINISTRADOR")
                    
                    // Cualquier otra petición necesita token
                    .anyRequest().authenticated()
            )

            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void escribirError(HttpServletResponse response, HttpStatus estado, String mensaje) throws java.io.IOException {
        response.setStatus(estado.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"success\":false,\"message\":\"" + mensaje + "\",\"data\":null}");
    }
}