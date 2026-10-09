package com.devdesk.Gestion_Incidencias.util.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Prioridad {
    BAJA("Baja"),
    NORMAL("Normal"),
    ALTA("Alta"),
    URGENTE("Urgente");

    private final String etiqueta;

    Prioridad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static Prioridad porDefecto() {
        return NORMAL;
    }

    public static Optional<Prioridad> desde(String nombre) {
        if (nombre == null) return Optional.empty();
        return Arrays.stream(values())
                .filter(p -> p.name().equalsIgnoreCase(nombre.trim()))
                .findFirst();
    }
}