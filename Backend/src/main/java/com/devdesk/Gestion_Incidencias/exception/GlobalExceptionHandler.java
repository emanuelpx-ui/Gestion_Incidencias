package com.devdesk.Gestion_Incidencias.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> manejarValidacion(
            MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(MessageConstants.DATOS_INVALIDOS, errores));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> manejarEstadoInvalido(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> manejarCuerpoIlegible(HttpMessageNotReadableException ex) {
        log.debug("Cuerpo de peticion ilegible: {}", ex.getMostSpecificCause().getClass().getSimpleName());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(MessageConstants.PETICION_MAL_FORMADA));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> manejarParametroFaltante(
            MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Falta el parametro obligatorio: " + ex.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("El valor de '" + ex.getName() + "' no tiene el formato esperado."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> manejarAccesoDenegado(AccessDeniedException ex) {
        log.warn("Acceso denegado a un recurso protegido.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(MessageConstants.ACCESO_DENEGADO));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Void>> manejarViolacionDePermisos(SecurityException ex) {
        log.warn("Intento de operar sobre un recurso fuera del alcance del usuario: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage() != null
                        ? ex.getMessage()
                        : MessageConstants.ACCESO_DENEGADO));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> manejarAutenticacion(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(MessageConstants.AUTENTICACION_REQUERIDA));
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> manejarRecursoNoEncontrado(
            org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        log.debug("Ruta no encontrada: {}", ex.getResourcePath());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(MessageConstants.RECURSO_NO_ENCONTRADO));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> manejarRutaNoEncontrada(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(MessageConstants.RECURSO_NO_ENCONTRADO));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> manejarErrorInesperado(Exception ex) {
        log.error("Error no controlado en la aplicacion", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(MessageConstants.ERROR_INTERNO));
    }
}