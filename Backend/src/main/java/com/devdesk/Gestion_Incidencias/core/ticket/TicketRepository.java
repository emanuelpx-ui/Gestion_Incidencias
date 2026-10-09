package com.devdesk.Gestion_Incidencias.core.ticket;

import com.devdesk.Gestion_Incidencias.util.enums.EstadoTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @EntityGraph(attributePaths = {"usuarioArea", "usuarioArea.area", "usuarioSoporte"})
    @Query("""
            SELECT t FROM Ticket t
            WHERE (CAST(:busqueda AS string) IS NULL
                   OR LOWER(t.titulo) LIKE :busqueda ESCAPE '!'
                   OR LOWER(t.usuarioArea.nombre) LIKE :busqueda ESCAPE '!')
              AND (:estado IS NULL OR t.estado = :estado)
            """)
    Page<Ticket> buscarTodosPaginado(@Param("busqueda") String busqueda,
                                     @Param("estado") EstadoTicket estado,
                                     Pageable pageable);

    @EntityGraph(attributePaths = {"usuarioArea", "usuarioArea.area", "usuarioSoporte"})
    @Query("""
            SELECT t FROM Ticket t
            WHERE t.usuarioArea.area.id = :areaId
              AND (CAST(:busqueda AS string) IS NULL
                   OR LOWER(t.titulo) LIKE :busqueda ESCAPE '!'
                   OR LOWER(t.usuarioArea.nombre) LIKE :busqueda ESCAPE '!')
              AND (:estado IS NULL OR t.estado = :estado)
            """)
    Page<Ticket> buscarPorAreaPaginado(@Param("areaId") Long areaId,
                                       @Param("busqueda") String busqueda,
                                       @Param("estado") EstadoTicket estado,
                                       Pageable pageable);

    @EntityGraph(attributePaths = {"usuarioArea", "usuarioArea.area", "usuarioSoporte"})
    @Query("""
            SELECT t FROM Ticket t
            WHERE (t.usuarioSoporte.id = :usuarioId OR t.usuarioArea.id = :usuarioId)
              AND (CAST(:busqueda AS string) IS NULL
                   OR LOWER(t.titulo) LIKE :busqueda ESCAPE '!'
                   OR LOWER(t.usuarioArea.nombre) LIKE :busqueda ESCAPE '!')
              AND (:estado IS NULL OR t.estado = :estado)
            """)
    Page<Ticket> buscarPorSoporteOCreadorPaginado(@Param("usuarioId") Long usuarioId,
                                                  @Param("busqueda") String busqueda,
                                                  @Param("estado") EstadoTicket estado,
                                                  Pageable pageable);

        // =====================================================================
    // MÉTRICAS DEL DASHBOARD
    // =====================================================================

    @Query("SELECT t.estado, COUNT(t) FROM Ticket t GROUP BY t.estado")
    List<Object[]> contarPorEstatus();

    @Query("SELECT t.usuarioArea.area.nombre, COUNT(t) FROM Ticket t GROUP BY t.usuarioArea.area.nombre")
    List<Object[]> contarTicketsPorArea();

    @Query("SELECT t.prioridad, COUNT(t) FROM Ticket t GROUP BY t.prioridad")
    List<Object[]> contarPorPrioridad();

    @Query("SELECT t.usuarioSoporte.nombre, COUNT(t) FROM Ticket t WHERE t.usuarioSoporte IS NOT NULL GROUP BY t.usuarioSoporte.nombre")
    List<Object[]> contarPorIngeniero();

    @Query("SELECT CAST(t.fechaCreacion AS date), COUNT(t) FROM Ticket t GROUP BY CAST(t.fechaCreacion AS date) ORDER BY CAST(t.fechaCreacion AS date) ASC")
    List<Object[]> contarPorFecha();

    @Query("SELECT t.calificacion, COUNT(t) FROM Ticket t WHERE t.calificacion IS NOT NULL GROUP BY t.calificacion")
    List<Object[]> contarPorCalificacion();

    @Query("""
            SELECT t.usuarioSoporte.nombre,
                   AVG(CASE t.calificacion
                         WHEN com.devdesk.Gestion_Incidencias.util.enums.Calificacion.BUENO THEN 3.0
                         WHEN com.devdesk.Gestion_Incidencias.util.enums.Calificacion.REGULAR THEN 2.0
                         ELSE 1.0 END),
                   COUNT(t)
            FROM Ticket t
            WHERE t.usuarioSoporte IS NOT NULL AND t.calificacion IS NOT NULL
            GROUP BY t.usuarioSoporte.nombre
            """)
    List<Object[]> calificacionPromedioPorTecnico();
}