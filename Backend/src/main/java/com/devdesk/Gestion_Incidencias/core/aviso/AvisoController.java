package com.devdesk.Gestion_Incidencias.core.aviso;

import com.devdesk.Gestion_Incidencias.exception.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/avisos")
@RequiredArgsConstructor
public class AvisoController {

    private final AvisoService avisoService;

    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<List<AvisoDTOs.Response>>> listarAvisosActivos() {
        return ResponseEntity.ok(ApiResponse.ok(avisoService.obtenerAvisosActivos()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<AvisoDTOs.Response>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(avisoService.obtenerTodos()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<AvisoDTOs.Response>> crearAviso(
            @Valid @RequestBody AvisoDTOs.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(avisoService.crearAviso(request), "Aviso creado correctamente."));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<AvisoDTOs.Response>> actualizarAviso(
            @PathVariable Long id, @Valid @RequestBody AvisoDTOs.Request request) {
        return ResponseEntity.ok(ApiResponse.ok(
                avisoService.actualizarAviso(id, request), "Aviso actualizado correctamente."));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminarAviso(@PathVariable Long id) {
        avisoService.eliminarAvisoFisico(id);
        return ResponseEntity.ok(ApiResponse.ok("Aviso eliminado correctamente."));
    }
}