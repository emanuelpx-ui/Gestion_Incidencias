package com.devdesk.Gestion_Incidencias.core.dashboard;

import com.devdesk.Gestion_Incidencias.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metricas")
    public ResponseEntity<ApiResponse<DashboardDTOs.DashboardResponse>> obtenerMetricas() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.obtenerMetricas()));
    }
}