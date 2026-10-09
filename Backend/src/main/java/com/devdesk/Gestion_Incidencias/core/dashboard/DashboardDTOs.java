package com.devdesk.Gestion_Incidencias.core.dashboard;

import java.util.List;

public class DashboardDTOs {

    public record DashboardResponse(
            long totalTickets,
            long totalUsuarios,
            long totalBitacora,
            List<MetricaGenerica> porEstatus,
            List<MetricaGenerica> porArea,
            List<MetricaGenerica> porPrioridad,
            List<MetricaGenerica> porIngeniero,
            List<MetricaFecha> porFecha,
            List<MetricaGenerica> avisosPorArea,
            List<MetricaGenerica> porCalificacion,
            List<MetricaCalificacion> calificacionPorTecnico
    ) {}

    public record MetricaGenerica(String nombre, long cantidad) {}

    public record MetricaFecha(String fecha, long total, long resueltos) {}

    public record MetricaCalificacion(String nombre, Double promedio, Long respuestas) {}
}