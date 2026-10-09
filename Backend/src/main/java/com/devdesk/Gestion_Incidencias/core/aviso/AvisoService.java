package com.devdesk.Gestion_Incidencias.core.aviso;

import com.devdesk.Gestion_Incidencias.core.area.Area;
import com.devdesk.Gestion_Incidencias.core.area.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvisoService {

    private final AvisoRepository avisoRepository;
    private final AreaRepository areaRepository;

    @Transactional
    public AvisoDTOs.Response crearAviso(AvisoDTOs.Request request) {
        Aviso aviso = new Aviso();
        aviso.setTitulo(request.titulo().trim());
        aviso.setMensaje(request.mensaje().trim());
        aviso.setActivo(request.activo() != null ? request.activo() : true);
        
        if (request.areaId() != null) {
            Area area = areaRepository.findById(request.areaId())
                    .orElseThrow(() -> new IllegalArgumentException("El área de destino no existe."));
            aviso.setArea(area);
        }

        return AvisoDTOs.Response.desdeEntidad(avisoRepository.save(aviso));
    }

    @Transactional(readOnly = true)
    public List<AvisoDTOs.Response> obtenerAvisosActivos() {
        return avisoRepository.findByActivoTrueAndEliminadoFalseOrderByIdDesc()
                .stream().map(AvisoDTOs.Response::desdeEntidad).toList();
    }

    @Transactional(readOnly = true)
    public List<AvisoDTOs.Response> obtenerTodos() {
        return avisoRepository.findByEliminadoFalseOrderByIdDesc()
                .stream().map(AvisoDTOs.Response::desdeEntidad).toList();
    }

    @Transactional
    public AvisoDTOs.Response actualizarAviso(Long id, AvisoDTOs.Request request) {
        Aviso aviso = avisoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Aviso no encontrado"));
        
        aviso.setTitulo(request.titulo().trim());
        aviso.setMensaje(request.mensaje().trim());
        if (request.activo() != null) aviso.setActivo(request.activo());
        
        if (request.areaId() != null) {
            Area area = areaRepository.findById(request.areaId())
                    .orElseThrow(() -> new IllegalArgumentException("El área de destino no existe."));
            aviso.setArea(area);
        } else {
            aviso.setArea(null); // Lo vuelve global
        }
        
        return AvisoDTOs.Response.desdeEntidad(avisoRepository.save(aviso));
    }

    @Transactional
    public void eliminarAvisoFisico(Long id) {
        Aviso aviso = avisoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Aviso no encontrado"));
        
        aviso.setEliminado(true);
        aviso.setActivo(false);
        avisoRepository.save(aviso);
    }
}