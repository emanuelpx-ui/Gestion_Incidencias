package com.devdesk.Gestion_Incidencias.core.area;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AreaRequest(
        @NotBlank(message = "El nombre del área es obligatorio.")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres.")
        String nombre,
        Boolean activo
) {}