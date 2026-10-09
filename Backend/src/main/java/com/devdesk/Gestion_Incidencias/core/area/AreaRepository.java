package com.devdesk.Gestion_Incidencias.core.area;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AreaRepository extends JpaRepository<Area, Long> {

    @Query("""
            SELECT a FROM Area a
            WHERE (:busqueda IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')))
              AND (:activo IS NULL OR a.activo = :activo)
            """)
    Page<Area> buscarPaginado(@Param("busqueda") String busqueda,
                              @Param("activo") Boolean activo,
                              Pageable pageable);

    boolean existsByNombre(String nombre);
    
    boolean existsByNombreAndIdNot(String nombre, Long id);

    
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.area.id = :areaId AND u.activo = true")
    long contarUsuariosActivos(@Param("areaId") Long areaId);
}