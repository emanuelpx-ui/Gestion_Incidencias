package com.devdesk.Gestion_Incidencias.core.area;

public record AreaResponse(
        Long id,
        String nombre,
        Boolean activo
) {
    public static AreaResponse desdeEntidad(Area area) {
        return new AreaResponse(area.getId(), area.getNombre(), area.getActivo());
    }
}