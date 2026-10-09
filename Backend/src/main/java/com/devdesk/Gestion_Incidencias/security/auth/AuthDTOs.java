package com.devdesk.Gestion_Incidencias.security.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDTOs {

    public record LoginRequest(
            @NotBlank(message = "El identificador es obligatorio.")
            @Size(max = 150) String identificador,
            
            @NotBlank(message = "La contraseña es obligatoria.")
            @Size(max = 100) String password
    ) {}

    public record JwtResponse(
            Long usuarioId,
            String nombre,
            String rol,
            String token,
            String mensaje,
            Long areaId
    ) {}
}