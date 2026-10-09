package com.devdesk.Gestion_Incidencias.core.area;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AreaService {

    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    @Transactional(readOnly = true)
    public Page<AreaResponse> listarAreasPaginado(String busqueda, Boolean activo, Pageable pageable) {
        return areaRepository.buscarPaginado(busqueda, activo, pageable)
                .map(AreaResponse::desdeEntidad);
    }

    @Transactional(readOnly = true)
    public List<AreaResponse> listarTodas() {
        return areaRepository.findAll().stream()
                .map(AreaResponse::desdeEntidad)
                .toList();
    }

    @Transactional
    public AreaResponse crearArea(AreaRequest request) {
        String nombreUpper = request.nombre().trim().toUpperCase();
        if (areaRepository.existsByNombre(nombreUpper)) {
            throw new IllegalArgumentException("Ya existe un área con el nombre " + nombreUpper);
        }

        Area area = new Area();
        area.setNombre(request.nombre()); // El setter lo hará mayúscula automáticamente
        if (request.activo() != null) area.setActivo(request.activo());

        return AreaResponse.desdeEntidad(areaRepository.save(area));
    }

    @Transactional
    public AreaResponse actualizarArea(Long id, AreaRequest request) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El área indicada no existe."));

        String nombreUpper = request.nombre().trim().toUpperCase();
        if (areaRepository.existsByNombreAndIdNot(nombreUpper, id)) {
            throw new IllegalArgumentException("Ya existe otra área con el nombre " + nombreUpper);
        }

        area.setNombre(request.nombre());
        if (request.activo() != null) area.setActivo(request.activo());

        return AreaResponse.desdeEntidad(areaRepository.save(area));
    }

    @Transactional
    public void eliminarArea(Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El área indicada no existe."));

        
         long personal = areaRepository.contarUsuariosActivos(id);
         if (personal > 0) {
            throw new IllegalArgumentException("No se puede desactivar el área: tiene usuarios activos.");
         }

        area.setActivo(false); // Baja lógica
        areaRepository.save(area);
    }
}
