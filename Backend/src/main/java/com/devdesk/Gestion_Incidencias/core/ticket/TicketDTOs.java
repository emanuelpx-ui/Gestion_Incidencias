package com.devdesk.Gestion_Incidencias.core.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class TicketDTOs {

    public record TicketRequest(
            @NotBlank(message = "El título de la falla es obligatorio.")
            @Size(max = 255) String titulo,
            
            @NotBlank(message = "La descripción de la falla es obligatoria.")
            @Size(max = 5000) String descripcion,
            
            @Size(max = 20) String prioridad,
            @Size(max = 100) String solicitante,
            Long usuarioSoporteId
    ) {}

    public record TicketResponse(
            Long id,
            String titulo,
            String descripcion,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaFin,
            String solicitante,
            String departamento,
            String estatus,
            String estatusEtiqueta,
            String prioridad,
            String prioridadEtiqueta,
            Long usuarioAreaId,
            Long usuarioSoporteId,
            String usuarioSoporteNombre,
            String justificacion,
            String calificacion,
            String calificacionEtiqueta,
            String comentarioEncuesta
    ) {}

    public record EncuestaRequest(
            @NotBlank(message = "Debe indicar cómo fue el servicio recibido.")
            @Pattern(regexp = "(?i)MALO|REGULAR|BUENO", message = "La calificación debe ser MALO, REGULAR o BUENO.")
            String calificacion,
            @Size(max = 500) String comentario
    ) {}

    public record ResolucionRequest(
            @NotBlank(message = "La justificación de la resolución es obligatoria.")
            @Size(min = 10, max = 2000, message = "Describe el trabajo realizado con al menos 10 caracteres.")
            String justificacion
    ) {}

    public record CatalogoResponse(String valor, String etiqueta) {}
}