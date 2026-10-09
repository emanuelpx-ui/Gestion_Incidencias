package com.devdesk.Gestion_Incidencias.core.area;

import org.springframework.data.repository.query.Param;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "area")
@Getter
@Setter // Lombok genera los setters normales, pero podemos sobrescribir específicos
@NoArgsConstructor
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pn_id")
    private Long id;

    @Column(name = "s_nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(name = "b_activo", nullable = false)
    private Boolean activo = true;

    // Interceptamos el setNombre para estandarizar SIEMPRE a mayúsculas
    public void setNombre(String nombre) {
        this.nombre = (nombre != null) ? nombre.trim().toUpperCase() : null;

    }
    
}