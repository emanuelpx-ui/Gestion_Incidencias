package com.devdesk.Gestion_Incidencias.core.usuario;

import com.devdesk.Gestion_Incidencias.util.enums.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByCorreoOrUsername(String correo, String username);
    List<Usuario> findByRol(Rol rol);
    boolean existsByCorreo(String correo);
    boolean existsByCorreoAndIdNot(String correo, Long id);
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"area"})
    @Query("""
            SELECT u FROM Usuario u
            WHERE (:busqueda IS NULL 
                   OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                   OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :busqueda, '%')))
              AND (:rol IS NULL OR u.rol = :rol)
              AND (:activo IS NULL OR u.activo = :activo)
            """)
    Page<Usuario> buscarPaginado(@Param("busqueda") String busqueda,
                                 @Param("rol") Rol rol,
                                 @Param("activo") Boolean activo,
                                 Pageable pageable);
}