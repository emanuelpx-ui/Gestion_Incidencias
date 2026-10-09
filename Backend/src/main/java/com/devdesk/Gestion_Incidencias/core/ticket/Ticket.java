package com.devdesk.Gestion_Incidencias.core.ticket;

import com.devdesk.Gestion_Incidencias.core.usuario.Usuario;
import com.devdesk.Gestion_Incidencias.util.audit.Auditable;
import com.devdesk.Gestion_Incidencias.util.enums.Calificacion;
import com.devdesk.Gestion_Incidencias.util.enums.EstadoTicket;
import com.devdesk.Gestion_Incidencias.util.enums.Prioridad;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
@Getter @Setter @NoArgsConstructor
public class Ticket extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pn_id")
    private Long id;

    @Column(name = "s_titulo", nullable = false)
    private String titulo;

    @Column(name = "s_descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "s_solicitante_nombre", length = 100)
    private String solicitanteNombre;

    @Column(name = "s_justificacion", columnDefinition = "TEXT")
    private String justificacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "s_estado", nullable = false, length = 20)
    private EstadoTicket estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fn_usuario_area_id", nullable = false)
    private Usuario usuarioArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fn_usuario_soporte_id")
    private Usuario usuarioSoporte;

    @Enumerated(EnumType.STRING)
    @Column(name = "s_prioridad", nullable = false, length = 20)
    private Prioridad prioridad;

    @Column(name = "d_fecha_fin")
    private LocalDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "s_calificacion", length = 20)
    private Calificacion calificacion;

    @Column(name = "s_comentario_encuesta", length = 500)
    private String comentarioEncuesta;

    @Column(name = "d_fecha_encuesta")
    private LocalDateTime fechaEncuesta;
}