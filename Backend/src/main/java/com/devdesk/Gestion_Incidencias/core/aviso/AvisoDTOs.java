package com.devdesk.Gestion_Incidencias.core.aviso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AvisoDTOs {

    public record Request(
            @NotBlank(message = "El título del aviso es obligatorio.")
            @Size(max = 150) String titulo,
            
            @NotBlank(message = "El mensaje del aviso es obligatorio.")
            @Size(max = 2000) String mensaje,
            
            Boolean activo,
            Long areaId
    ) {}

    public record Response(
            Long id,
            String titulo,
            String mensaje,
            Boolean activo,
            Long areaId,
            String areaNombre
    ) {
        public static Response desdeEntidad(Aviso aviso) {
            return new Response(
                    aviso.getId(),
                    aviso.getTitulo(),
                    aviso.getMensaje(),
                    aviso.getActivo(),
                    aviso.getArea() != null ? aviso.getArea().getId() : null,
                    aviso.getArea() != null ? aviso.getArea().getNombre() : "GLOBAL"
            );
        }
    }
}