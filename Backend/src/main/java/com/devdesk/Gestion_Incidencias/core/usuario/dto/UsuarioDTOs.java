package com.devdesk.Gestion_Incidencias.core.usuario.dto;

import com.devdesk.Gestion_Incidencias.util.enums.Rol;
import com.devdesk.Gestion_Incidencias.core.usuario.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioDTOs {

    public record UsuarioRequest(
            @NotBlank String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            @NotBlank @Email String correo,
            String username,
            @NotNull Rol rol,
            Long areaId,
            Boolean disponibleSoporte
    ) {}

    public record ActualizarUsuarioRequest(
            @NotBlank String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            @NotBlank @Email String correo,
            Rol rol,
            Long areaId
    ) {}

    public record CambioPasswordRequest(
            @NotBlank String passwordActual,
            @NotBlank 
            @Size(min = 8, message = "Mínimo 8 caracteres.")
            @Pattern(regexp = ".*[a-zA-Z].*", message = "Debe incluir una letra.")
            @Pattern(regexp = ".*\\d.*", message = "Debe incluir un número.")
            String nuevaPassword
    ) {}

    public record UsuarioResponse(
            Long id,
            String nombreCompleto,
            String username,
            String correo,
            Rol rol,
            Boolean activo,
            Boolean disponibleSoporte,
            Long areaId,
            String areaNombre,
            Boolean passwordTemporal,
            String passwordTemporalTexto
    ) {
        public static UsuarioResponse desdeEntidad(Usuario usuario, String passwordTemporalTexto) {
            String fullName = String.format("%s %s %s", 
                    usuario.getNombre() != null ? usuario.getNombre() : "",
                    usuario.getApellidoPaterno() != null ? usuario.getApellidoPaterno() : "",
                    usuario.getApellidoMaterno() != null ? usuario.getApellidoMaterno() : ""
            ).trim().replaceAll(" +", " ");

            return new UsuarioResponse(
                    usuario.getId(),
                    fullName,
                    usuario.getUsername(),
                    usuario.getCorreo(),
                    usuario.getRol(),
                    usuario.getActivo(),
                    usuario.getDisponibleSoporte(),
                    usuario.getArea() != null ? usuario.getArea().getId() : null,
                    usuario.getArea() != null ? usuario.getArea().getNombre() : "Sin Área",
                    usuario.getPasswordTemporal(),
                    passwordTemporalTexto
            );
        }
    }
}
