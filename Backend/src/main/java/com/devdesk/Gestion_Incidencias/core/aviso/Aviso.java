package com.devdesk.Gestion_Incidencias.core.aviso;

import com.devdesk.Gestion_Incidencias.core.area.Area;
import com.devdesk.Gestion_Incidencias.util.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "aviso")
@Getter @Setter @NoArgsConstructor
public class Aviso extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pn_id")
    private Long id;

    @Column(name = "s_titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "s_mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "b_activo", nullable = false)
    private Boolean activo = true;

    // Relación fuerte hacia el área. Nulo = Global.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pn_area_id", nullable = true)
    private Area area; 

    @Column(name = "b_eliminado", nullable = false)
    private Boolean eliminado = false; 
}