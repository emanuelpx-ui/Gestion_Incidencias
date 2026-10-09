package com.devdesk.Gestion_Incidencias.core.ticket;

import com.devdesk.Gestion_Incidencias.exception.ApiResponse;
import com.devdesk.Gestion_Incidencias.exception.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<ApiResponse<TicketDTOs.TicketResponse>> crearTicket(
            @Valid @RequestBody TicketDTOs.TicketRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(ticketService.crearTicket(request, auth.getName()), "Ticket creado."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TicketDTOs.TicketResponse>>> obtenerTodos(
            Authentication auth,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estatus,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                ticketService.obtenerTicketsParaBandeja(auth.getName(), busqueda, estatus, pageable)));
    }

    @PutMapping("/{id}/atender")
    @PreAuthorize("hasAnyRole('AGENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<TicketDTOs.TicketResponse>> atenderTicket(
            @PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                ticketService.atenderTicket(id, auth.getName()), "Técnico en camino."));
    }

    @PutMapping("/{id}/resolver")
    @PreAuthorize("hasAnyRole('AGENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<TicketDTOs.TicketResponse>> resolverTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketDTOs.ResolucionRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                ticketService.resolverTicket(id, request.justificacion(), auth.getName()), "Ticket resuelto."));
    }
}