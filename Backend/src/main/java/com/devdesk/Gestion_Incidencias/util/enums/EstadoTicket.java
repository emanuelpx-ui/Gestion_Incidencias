package com.devdesk.Gestion_Incidencias.util.enums;

import java.util.Arrays;
import java.util.Optional;

public enum EstadoTicket {
    ABIERTO("Abierto"),
    EN_PROCESO("En proceso"),
    CERRADO("Cerrado");

    private final String etiqueta;

    EstadoTicket(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean esFinal() {
        return this == CERRADO;
    }

    public static Optional<EstadoTicket> desde(String nombre) {
        if (nombre == null) return Optional.empty();
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(nombre.trim()))
                .findFirst();
    }
}