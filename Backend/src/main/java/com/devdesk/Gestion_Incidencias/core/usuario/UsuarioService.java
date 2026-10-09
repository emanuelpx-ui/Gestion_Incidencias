package com.devdesk.Gestion_Incidencias.core.usuario;

import com.devdesk.Gestion_Incidencias.core.area.Area;
import com.devdesk.Gestion_Incidencias.core.area.AreaRepository;
import com.devdesk.Gestion_Incidencias.core.notificacion.EmailService;
import com.devdesk.Gestion_Incidencias.core.usuario.dto.UsuarioDTOs.*;
import com.devdesk.Gestion_Incidencias.util.enums.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final UsuarioRepository usuarioRepository;
    private final AreaRepository areaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // <-- Servicio de correo inyectado

    @Transactional
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        if (usuarioRepository.existsByCorreo(request.correo())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
        }
        if (request.username() != null && usuarioRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }

        String passwordTemporal = generarPasswordAleatoria();

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setApellidoPaterno(request.apellidoPaterno());
        usuario.setApellidoMaterno(request.apellidoMaterno());
        usuario.setCorreo(request.correo());
        usuario.setUsername(request.username());
        
        // Guardamos el hash, no el texto plano
        usuario.setPassword(passwordEncoder.encode(passwordTemporal));
        usuario.setPasswordTemporal(true); 
        usuario.setRol(request.rol());
        usuario.setDisponibleSoporte(request.disponibleSoporte() != null ? request.disponibleSoporte() : false);

        if (request.areaId() != null) {
            Area area = areaRepository.findById(request.areaId())
                    .orElseThrow(() -> new IllegalArgumentException("Área no encontrada."));
            usuario.setArea(area);
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Envío asíncrono de credenciales por correo
        emailService.enviarCorreo(
                usuarioGuardado.getCorreo(),
                "Bienvenido al Sistema de Gestión de Incidencias",
                "Hola " + usuarioGuardado.getNombre() + ",\n\n" +
                "Tu cuenta ha sido creada exitosamente. Aquí tienes tus credenciales de acceso:\n\n" +
                "Usuario/Correo: " + usuarioGuardado.getCorreo() + "\n" +
                "Contraseña temporal: " + passwordTemporal + "\n\n" +
                "Por seguridad, el sistema te pedirá cambiar esta contraseña en tu primer inicio de sesión.\n" +
                "¡Bienvenido!"
        );

        return UsuarioResponse.desdeEntidad(usuarioGuardado, passwordTemporal);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarUsuariosPaginado(String busqueda, Rol rol, Boolean activo, Pageable pageable) {
        return usuarioRepository.buscarPaginado(busqueda, rol, activo, pageable)
                .map(u -> UsuarioResponse.desdeEntidad(u, null));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarAgentesSoporte() {
        return usuarioRepository.findByRol(Rol.AGENTE).stream()
                .map(u -> UsuarioResponse.desdeEntidad(u, null))
                .toList();
    }

    @Transactional
    public UsuarioResponse actualizarUsuario(Long id, ActualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (usuarioRepository.existsByCorreoAndIdNot(request.correo(), id)) {
            throw new IllegalArgumentException("Ya existe otro usuario con ese correo.");
        }

        usuario.setNombre(request.nombre());
        usuario.setApellidoPaterno(request.apellidoPaterno());
        usuario.setApellidoMaterno(request.apellidoMaterno());
        usuario.setCorreo(request.correo());
        if (request.rol() != null) usuario.setRol(request.rol());

        if (request.areaId() != null) {
            Area area = areaRepository.findById(request.areaId())
                    .orElseThrow(() -> new IllegalArgumentException("Área no encontrada."));
            usuario.setArea(area);
        }

        return UsuarioResponse.desdeEntidad(usuarioRepository.save(usuario), null);
    }

    @Transactional
    public UsuarioResponse resetearPassword(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        String nuevaTemporal = generarPasswordAleatoria();
        usuario.setPassword(passwordEncoder.encode(nuevaTemporal));
        usuario.setPasswordTemporal(true);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Envío asíncrono del reseteo de contraseña
        emailService.enviarCorreo(
                usuarioGuardado.getCorreo(),
                "Restablecimiento de Contraseña",
                "Hola " + usuarioGuardado.getNombre() + ",\n\n" +
                "El administrador ha restablecido tu contraseña.\n" +
                "Tu nueva contraseña temporal es: " + nuevaTemporal + "\n\n" +
                "Por favor, inicia sesión y cámbiala de inmediato."
        );
        
        return UsuarioResponse.desdeEntidad(usuarioGuardado, nuevaTemporal);
    }

    @Transactional
    public void inactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        usuario.setActivo(false);
        if (usuario.getRol() == Rol.AGENTE) {
            usuario.setDisponibleSoporte(false);
        }
        usuarioRepository.save(usuario);
    }

    private String generarPasswordAleatoria() {
        final String alfabeto = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#$%&*";
        StringBuilder sb = new StringBuilder(14);
        for (int i = 0; i < 14; i++) {
            sb.append(alfabeto.charAt(RANDOM.nextInt(alfabeto.length())));
        }
        return sb.toString();
    }
}