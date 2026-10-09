-- ==============================================================================
-- V1__Init_Schema.sql
-- Creación de la estructura base. Los Enums se mapean como VARCHAR(50).
-- ==============================================================================

CREATE TABLE area (
    pn_id BIGSERIAL PRIMARY KEY,
    s_nombre VARCHAR(150) NOT NULL,
    s_abreviatura VARCHAR(20) NOT NULL,
    b_activo BOOLEAN NOT NULL DEFAULT TRUE,
    
    fecha_creacion TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    creado_por VARCHAR(150),
    modificado_por VARCHAR(150)
);

CREATE TABLE usuario (
    pn_id BIGSERIAL PRIMARY KEY,
    s_nombre VARCHAR(100) NOT NULL,
    s_apellido_paterno VARCHAR(100) NOT NULL,
    s_apellido_materno VARCHAR(100),
    s_correo VARCHAR(150) NOT NULL UNIQUE,
    s_username VARCHAR(100) UNIQUE,
    s_password VARCHAR(255) NOT NULL,
    b_password_temporal BOOLEAN NOT NULL DEFAULT TRUE,
    s_rol VARCHAR(50) NOT NULL, 
    b_activo BOOLEAN NOT NULL DEFAULT TRUE,
    b_disponible_soporte BOOLEAN NOT NULL DEFAULT FALSE,
    pn_area_id BIGINT,
    
    fecha_creacion TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    creado_por VARCHAR(150),
    modificado_por VARCHAR(150),
    
    CONSTRAINT fk_usuario_area FOREIGN KEY (pn_area_id) REFERENCES area(pn_id)
);

CREATE TABLE aviso (
    pn_id BIGSERIAL PRIMARY KEY,
    s_titulo VARCHAR(150) NOT NULL,
    s_mensaje TEXT NOT NULL,
    b_activo BOOLEAN NOT NULL DEFAULT TRUE,
    pn_area_id BIGINT, 
    b_eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    
    fecha_creacion TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    creado_por VARCHAR(150),
    modificado_por VARCHAR(150),
    
    CONSTRAINT fk_aviso_area FOREIGN KEY (pn_area_id) REFERENCES area(pn_id)
);

CREATE TABLE ticket (
    pn_id BIGSERIAL PRIMARY KEY,
    s_titulo VARCHAR(200) NOT NULL,
    s_descripcion TEXT NOT NULL,
    d_fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    d_fecha_fin TIMESTAMP,
    s_solicitante_nombre VARCHAR(150),
    pn_usuario_area_id BIGINT NOT NULL,
    pn_usuario_soporte_id BIGINT,
    s_estado VARCHAR(50) NOT NULL, 
    s_prioridad VARCHAR(50) NOT NULL, 
    s_justificacion TEXT,
    s_calificacion VARCHAR(50), 
    s_comentario_encuesta TEXT,
    
    fecha_creacion TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    creado_por VARCHAR(150),
    modificado_por VARCHAR(150),
    
    CONSTRAINT fk_ticket_usuario_area FOREIGN KEY (pn_usuario_area_id) REFERENCES usuario(pn_id),
    CONSTRAINT fk_ticket_usuario_soporte FOREIGN KEY (pn_usuario_soporte_id) REFERENCES usuario(pn_id)
);

CREATE TABLE bitacora (
    pn_id BIGSERIAL PRIMARY KEY,
    pn_ticket_id BIGINT NOT NULL,
    d_fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    s_estatus_registrado VARCHAR(50) NOT NULL,
    s_justificacion TEXT,
    
    fecha_creacion TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    creado_por VARCHAR(150),
    modificado_por VARCHAR(150),
    
    CONSTRAINT fk_bitacora_ticket FOREIGN KEY (pn_ticket_id) REFERENCES ticket(pn_id)
);