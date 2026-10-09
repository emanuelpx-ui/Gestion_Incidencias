package com.devdesk.Gestion_Incidencias.core.area;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/areas")
public class AreaController {

    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping
    public ResponseEntity<Page<AreaResponse>> obtenerAreas(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean activo,
            Pageable pageable) {
        return ResponseEntity.ok(areaService.listarAreasPaginado(busqueda, activo, pageable));
    }

    @GetMapping("/todas")
    public ResponseEntity<List<AreaResponse>> obtenerTodas() {
        return ResponseEntity.ok(areaService.listarTodas());
    }

    @PostMapping
    public ResponseEntity<AreaResponse> registrarArea(@Valid @RequestBody AreaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(areaService.crearArea(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaResponse> actualizarArea(@PathVariable Long id, @Valid @RequestBody AreaRequest request) {
        return ResponseEntity.ok(areaService.actualizarArea(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarArea(@PathVariable Long id) {
        areaService.eliminarArea(id);
        return ResponseEntity.noContent().build();
    }
}
