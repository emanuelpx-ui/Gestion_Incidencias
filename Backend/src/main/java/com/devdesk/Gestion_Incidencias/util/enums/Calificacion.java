package com.devdesk.Gestion_Incidencias.util.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Calificacion {
    MALO("Malo"),
    REGULAR("Regular"),
    BUENO("Bueno");

    private final String etiqueta;

    Calificacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static Optional<Calificacion> desde(String nombre) {
        if (nombre == null) return Optional.empty();
        return Arrays.stream(values())
                .filter(c -> c.name().equalsIgnoreCase(nombre.trim()))
                .findFirst();
    }
}