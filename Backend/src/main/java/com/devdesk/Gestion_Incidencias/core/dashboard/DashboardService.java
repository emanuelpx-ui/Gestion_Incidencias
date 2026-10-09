package com.devdesk.Gestion_Incidencias.core.dashboard;

import com.devdesk.Gestion_Incidencias.core.aviso.AvisoRepository;
import com.devdesk.Gestion_Incidencias.core.ticket.BitacoraRepository;
import com.devdesk.Gestion_Incidencias.core.ticket.TicketRepository;
import com.devdesk.Gestion_Incidencias.core.usuario.UsuarioRepository;
import com.devdesk.Gestion_Incidencias.util.enums.Calificacion;
import com.devdesk.Gestion_Incidencias.util.enums.EstadoTicket;
import com.devdesk.Gestion_Incidencias.util.enums.Prioridad;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvisoRepository avisoRepository;
    private final BitacoraRepository bitacoraRepository;

    public DashboardDTOs.DashboardResponse obtenerMetricas() {
        return new DashboardDTOs.DashboardResponse(
                ticketRepository.count(),
                usuarioRepository.count(),
                bitacoraRepository.count(), // ¡Ahora es real!
                mapearGenerico(ticketRepository.contarPorEstatus()),
                mapearGenerico(ticketRepository.contarTicketsPorArea()),
                mapearGenerico(ticketRepository.contarPorPrioridad()),
                mapearGenerico(ticketRepository.contarPorIngeniero()),
                mapearFecha(ticketRepository.contarPorFecha()),
                mapearGenerico(avisoRepository.contarAvisosActivosPorArea()),
                mapearGenerico(ticketRepository.contarPorCalificacion()),
                mapearCalificacion(ticketRepository.calificacionPromedioPorTecnico())
        );
    }

    private List<DashboardDTOs.MetricaGenerica> mapearGenerico(List<Object[]> datos) {
        return datos.stream()
                .map(obj -> new DashboardDTOs.MetricaGenerica(nombreLegible(obj[0]), ((Number) obj[1]).longValue()))
                .toList();
    }

    private String nombreLegible(Object valor) {
        if (valor == null) return "Sin asignar";
        if (valor instanceof EstadoTicket estado) return estado.getEtiqueta();
        if (valor instanceof Prioridad prioridad) return prioridad.getEtiqueta();
        if (valor instanceof Calificacion calificacion) return calificacion.getEtiqueta();

        String texto = valor.toString();
        return Prioridad.desde(texto).map(Prioridad::getEtiqueta).orElse(texto);
    }

    private List<DashboardDTOs.MetricaCalificacion> mapearCalificacion(List<Object[]> datos) {
        return datos.stream()
                .map(obj -> new DashboardDTOs.MetricaCalificacion(
                        obj[0] != null ? obj[0].toString() : "Sin asignar",
                        obj[1] != null ? Math.round(((Number) obj[1]).doubleValue() * 10.0) / 10.0 : null,
                        ((Number) obj[2]).longValue()
                ))
                .toList();
    }

    private List<DashboardDTOs.MetricaFecha> mapearFecha(List<Object[]> datos) {
        return datos.stream()
                .map(obj -> new DashboardDTOs.MetricaFecha(
                        obj[0] != null ? obj[0].toString() : "Sin fecha",
                        ((Number) obj[1]).longValue(),
                        0L
                ))
                .toList();
    }
}