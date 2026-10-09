package com.devdesk.Gestion_Incidencias.core.usuario;

import com.devdesk.Gestion_Incidencias.util.enums.Rol;
import com.devdesk.Gestion_Incidencias.core.area.Area;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pn_id")
    private Long id;

    @Column(name = "s_nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "s_apellido_paterno")
    private String apellidoPaterno;

    @Column(name = "s_apellido_materno")
    private String apellidoMaterno;

    @Column(name = "s_correo", nullable = false, unique = true, length = 100)
    private String correo;

    @Column(name = "s_username", unique = true, length = 50)
    private String username;

    @JsonIgnore
    @Column(name = "s_password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "s_rol", nullable = false)
    private Rol rol;

    @Column(name = "b_activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "b_disponible_soporte", nullable = false)
    private Boolean disponibleSoporte = false;

    @Column(name = "b_password_temporal", nullable = false)
    private Boolean passwordTemporal = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fn_area_id")
    private Area area;

    // Setters que estandarizan a mayúsculas
    public void setNombre(String nombre) {
        this.nombre = (nombre != null) ? nombre.trim().toUpperCase() : null;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = (apellidoPaterno != null) ? apellidoPaterno.trim().toUpperCase() : null;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = (apellidoMaterno != null) ? apellidoMaterno.trim().toUpperCase() : null;
    }
    
    public void setCorreo(String correo) {
        this.correo = (correo != null) ? correo.trim().toLowerCase() : null;
    }
}