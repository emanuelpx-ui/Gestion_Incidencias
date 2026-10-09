package com.devdesk.Gestion_Incidencias.security.auth;

import com.devdesk.Gestion_Incidencias.exception.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDTOs.JwtResponse>> login(@Valid @RequestBody AuthDTOs.LoginRequest request) {
        AuthDTOs.JwtResponse respuesta = authService.iniciarSesion(request);
        return ResponseEntity.ok(ApiResponse.ok(respuesta, respuesta.mensaje()));
    }
}