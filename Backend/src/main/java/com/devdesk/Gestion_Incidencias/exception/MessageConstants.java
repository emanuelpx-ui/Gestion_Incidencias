package com.devdesk.Gestion_Incidencias.exception;

public final class MessageConstants {

    private MessageConstants() {}

    public static final String OPERACION_EXITOSA = "Operacion realizada correctamente.";
    public static final String ERROR_INTERNO = "Ocurrio un error al procesar la solicitud. Intente de nuevo mas tarde.";
    public static final String RECURSO_NO_ENCONTRADO = "El recurso solicitado no existe.";
    public static final String DATOS_INVALIDOS = "Los datos enviados no son validos.";
    public static final String PETICION_MAL_FORMADA = "La solicitud no tiene el formato esperado.";
    public static final String CREDENCIALES_INVALIDAS = "Credenciales invalidas.";
    public static final String AUTENTICACION_REQUERIDA = "Debe iniciar sesion para acceder a este recurso.";
    public static final String ACCESO_DENEGADO = "No cuenta con los permisos necesarios para realizar esta accion.";
    public static final String USUARIO_INACTIVO = "El usuario se encuentra inactivo. Contacte al administrador.";
    public static final String SESION_EXPIRADA = "Su sesion ha expirado. Inicie sesion de nuevo.";
    public static final String DEMASIADOS_INTENTOS = "Demasiados intentos. Espere unos minutos e intente de nuevo.";
    public static final String PASSWORD_ACTUAL_INCORRECTA = "La contrasena actual no es correcta.";
    public static final String PASSWORD_ACTUALIZADA = "Contrasena actualizada correctamente.";
    public static final String PASSWORD_DEBIL = "La contrasena debe tener al menos 8 caracteres e incluir letras y numeros.";
    public static final String PASSWORD_IGUAL_ANTERIOR = "La nueva contrasena debe ser distinta de la actual.";
    public static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado.";
    public static final String CORREO_YA_REGISTRADO = "El correo ya esta registrado.";
    public static final String ROL_NO_ENCONTRADO = "El rol seleccionado no existe.";
    public static final String AREA_NO_ENCONTRADA = "Area no encontrada.";
    public static final String TICKET_NO_ENCONTRADO = "Ticket no encontrado.";
}