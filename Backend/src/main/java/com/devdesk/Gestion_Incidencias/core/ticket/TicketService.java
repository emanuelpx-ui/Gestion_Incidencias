package com.devdesk.Gestion_Incidencias.core.ticket;

import com.devdesk.Gestion_Incidencias.core.notificacion.EmailService;
import com.devdesk.Gestion_Incidencias.core.usuario.Usuario;
import com.devdesk.Gestion_Incidencias.core.usuario.UsuarioRepository;
import com.devdesk.Gestion_Incidencias.exception.PageResponse;
import com.devdesk.Gestion_Incidencias.util.enums.Calificacion;
import com.devdesk.Gestion_Incidencias.util.enums.EstadoTicket;
import com.devdesk.Gestion_Incidencias.util.enums.Prioridad;
import com.devdesk.Gestion_Incidencias.util.enums.Rol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;
    private final BitacoraRepository bitacoraRepository;
    private final SimpMessagingTemplate messagingTemplate; // Nota: Si borraste WebSocketConfig, puedes borrar esta inyección y el método emitirEventoTicket.
    private final EmailService emailService; // <-- ¡Nuestro nuevo servicio inyectado!

    @Transactional
    public TicketDTOs.TicketResponse crearTicket(TicketDTOs.TicketRequest request, String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Ticket nuevoTicket = new Ticket();
        nuevoTicket.setTitulo(request.titulo());
        nuevoTicket.setDescripcion(request.descripcion());
        
        if (request.solicitante() != null && !request.solicitante().isBlank()) {
            nuevoTicket.setSolicitanteNombre(request.solicitante());
        }

        nuevoTicket.setUsuarioArea(usuario);
        nuevoTicket.setEstado(EstadoTicket.ABIERTO);
        nuevoTicket.setPrioridad(Prioridad.desde(request.prioridad()).orElse(Prioridad.NORMAL));

        // Asignación
        if (request.usuarioSoporteId() != null) {
            Usuario soporte = usuarioRepository.findById(request.usuarioSoporteId()).orElseThrow();
            nuevoTicket.setUsuarioSoporte(soporte);
        } else if (usuario.getRol() == Rol.AGENTE) {
            nuevoTicket.setUsuarioSoporte(usuario);
        } else {
            Usuario soporteAsignado = resolverAsignacion(usuario);
            if (soporteAsignado != null) nuevoTicket.setUsuarioSoporte(soporteAsignado);
        }

        Ticket ticketGuardado = ticketRepository.save(nuevoTicket);
        
        // 1. Notificación asíncrona de creación de ticket
        emailService.enviarCorreo(
                usuario.getCorreo(),
                "Acuse de recibo: Ticket #" + ticketGuardado.getId() + " registrado",
                "Hola " + usuario.getNombre() + ",\n\n" +
                "Hemos recibido tu reporte exitosamente. Un técnico será asignado pronto para atender tu solicitud.\n\n" +
                "Detalles del problema:\n" + ticketGuardado.getDescripcion() + "\n\n" +
                "Te notificaremos cuando haya actualizaciones."
        );

        emitirEventoTicket(ticketGuardado);
        return mapearATicketResponse(ticketGuardado);
    }

    private Usuario resolverAsignacion(Usuario usuarioArea) {
        // Aquí iría tu lógica de buscarTecnicosDisponiblesOrdenadosPorCarga
        return null; // Implementación simplificada para el ejemplo
    }

    @Transactional
    public TicketDTOs.TicketResponse atenderTicket(Long ticketId, String correoUsuario) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        Usuario tecnico = usuarioRepository.findByCorreo(correoUsuario).orElseThrow();
        verificarPuedeOperarElTicket(ticket, tecnico);

        ticket.setEstado(EstadoTicket.EN_PROCESO);
        Ticket ticketGuardado = ticketRepository.save(ticket);

        registrarBitacora(ticketGuardado, EstadoTicket.EN_PROCESO, "El técnico va en camino.");
        emitirEventoTicket(ticketGuardado);
        return mapearATicketResponse(ticketGuardado);
    }

    @Transactional
    public TicketDTOs.TicketResponse resolverTicket(Long ticketId, String justificacion, String correoUsuario) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        Usuario tecnico = usuarioRepository.findByCorreo(correoUsuario).orElseThrow();
        verificarPuedeOperarElTicket(ticket, tecnico);

        ticket.setEstado(EstadoTicket.CERRADO);
        ticket.setFechaFin(LocalDateTime.now());
        ticket.setJustificacion(justificacion);
        
        Ticket ticketGuardado = ticketRepository.save(ticket);
        registrarBitacora(ticketGuardado, EstadoTicket.CERRADO, justificacion);
        
        // 2. Notificación asíncrona de resolución de ticket
        emailService.enviarCorreo(
                ticket.getUsuarioArea().getCorreo(),
                "Ticket Resuelto: #" + ticketGuardado.getId() + " - " + ticketGuardado.getTitulo(),
                "Hola " + ticket.getUsuarioArea().getNombre() + ",\n\n" +
                "El técnico " + tecnico.getNombre() + " ha marcado tu ticket como RESUELTO.\n\n" +
                "Solución / Notas del técnico:\n" + justificacion + "\n\n" +
                "Por favor, inicia sesión en el sistema de tickets para calificar el servicio brindado.\n" +
                "¡Gracias!"
        );

        emitirEventoTicket(ticketGuardado);
        return mapearATicketResponse(ticketGuardado);
    }

    @Transactional(readOnly = true)
    public PageResponse<TicketDTOs.TicketResponse> obtenerTicketsParaBandeja(
            String correoUsuario, String busqueda, String estatus, Pageable pageable) {

        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario).orElseThrow();
        String textoBusqueda = normalizarBusqueda(busqueda);
        EstadoTicket filtroEstado = normalizarEstado(estatus);

        Page<Ticket> pagina = switch (usuario.getRol()) {
            case ADMINISTRADOR -> ticketRepository.buscarTodosPaginado(textoBusqueda, filtroEstado, pageable);
            case AGENTE -> ticketRepository.buscarPorSoporteOCreadorPaginado(usuario.getId(), textoBusqueda, filtroEstado, pageable);
            case USUARIO -> ticketRepository.buscarPorAreaPaginado(usuario.getArea().getId(), textoBusqueda, filtroEstado, pageable);
        };

        return PageResponse.de(pagina, this::mapearATicketResponse);
    }

    private void verificarPuedeOperarElTicket(Ticket ticket, Usuario usuario) {
        if (usuario.getRol() == Rol.ADMINISTRADOR) return;
        if (ticket.getUsuarioSoporte() == null || !ticket.getUsuarioSoporte().getId().equals(usuario.getId())) {
            throw new SecurityException("No puedes modificar un ticket asignado a otro técnico.");
        }
    }

    private void registrarBitacora(Ticket ticket, EstadoTicket estado, String justificacion) {
        Bitacora bitacora = new Bitacora();
        bitacora.setTicket(ticket);
        bitacora.setEstatusRegistrado(estado.name());
        bitacora.setJustificacion(justificacion);
        bitacoraRepository.save(bitacora);
    }

    private TicketDTOs.TicketResponse mapearATicketResponse(Ticket t) {
        return new TicketDTOs.TicketResponse(
                t.getId(), t.getTitulo(), t.getDescripcion(), t.getFechaCreacion(), t.getFechaFin(),
                t.getSolicitanteNombre(), t.getUsuarioArea().getArea().getNombre(),
                t.getEstado().name(), t.getEstado().getEtiqueta(),
                t.getPrioridad().name(), t.getPrioridad().getEtiqueta(),
                t.getUsuarioArea().getId(), t.getUsuarioSoporte() != null ? t.getUsuarioSoporte().getId() : null,
                t.getUsuarioSoporte() != null ? t.getUsuarioSoporte().getNombre() : null,
                t.getJustificacion(),
                t.getCalificacion() != null ? t.getCalificacion().name() : null,
                t.getCalificacion() != null ? t.getCalificacion().getEtiqueta() : null,
                t.getComentarioEncuesta()
        );
    }

    private void emitirEventoTicket(Ticket ticket) {
        try {
            if (messagingTemplate != null) {
                messagingTemplate.convertAndSend("/topic/tickets-soporte", mapearATicketResponse(ticket));
            }
        } catch (Exception e) {
            log.error("Fallo al emitir evento WS", e);
        }
    }
    
    private String normalizarBusqueda(String valor) { return valor; }
    private EstadoTicket normalizarEstado(String valor) { return null; }
}