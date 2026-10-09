package com.devdesk.Gestion_Incidencias.core.aviso;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AvisoRepository extends JpaRepository<Aviso, Long> {

    @EntityGraph(attributePaths = {"area"})
    List<Aviso> findByActivoTrueAndEliminadoFalseOrderByIdDesc();

    @EntityGraph(attributePaths = {"area"})
    List<Aviso> findByEliminadoFalseOrderByIdDesc();

    @Query("SELECT COALESCE(a.area.nombre, 'GLOBAL'), COUNT(a) FROM Aviso a WHERE a.activo = true AND a.eliminado = false GROUP BY a.area.nombre")
    List<Object[]> contarAvisosActivosPorArea();
}