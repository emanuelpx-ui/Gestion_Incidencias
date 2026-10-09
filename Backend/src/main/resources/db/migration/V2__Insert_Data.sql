-- ==============================================================================
-- V2__Insert_Data.sql
-- Datos iniciales: 3 Áreas, 4 Usuarios, 3 Avisos, 6 Tickets, 5 Bitácoras (21 Reg)
-- ==============================================================================

-- 1. ÁREAS
INSERT INTO area (s_nombre, s_abreviatura, b_activo) VALUES 
('Departamento de Sistemas', 'SISTEMAS', true),
('Recursos Humanos y Nómina', 'RH', true),
('Dirección de Operaciones', 'OPERACIONES', true);

-- 2. USUARIOS (Password para todos: Admin123!)
INSERT INTO usuario (s_nombre, s_apellido_paterno, s_apellido_materno, s_correo, s_username, s_password, b_password_temporal, s_rol, b_activo, b_disponible_soporte, pn_area_id) VALUES 
('Emmanuel', 'Pajarito', 'Xochimitl', 'admin@devdesk.com', 'epajarito', '$2a$10$wT8Kz1s4V/B4Z/qJ7R8pYuXqO3cTbZk3Xp.Nq7n.C8c.u/v.a.z/', false, 'ADMINISTRADOR', true, true, 1),
('Carlos', 'López', 'García', 'clopez@devdesk.com', 'clopez', '$2a$10$wT8Kz1s4V/B4Z/qJ7R8pYuXqO3cTbZk3Xp.Nq7n.C8c.u/v.a.z/', false, 'AGENTE', true, true, 1),
('Ana', 'Martínez', 'Ruiz', 'amartinez@devdesk.com', 'amartinez', '$2a$10$wT8Kz1s4V/B4Z/qJ7R8pYuXqO3cTbZk3Xp.Nq7n.C8c.u/v.a.z/', false, 'USUARIO', true, false, 2),
('Luis', 'Fernández', 'Gómez', 'lfernandez@devdesk.com', 'lfernandez', '$2a$10$wT8Kz1s4V/B4Z/qJ7R8pYuXqO3cTbZk3Xp.Nq7n.C8c.u/v.a.z/', false, 'USUARIO', true, false, 3);

-- 3. AVISOS
INSERT INTO aviso (s_titulo, s_mensaje, b_activo, pn_area_id, b_eliminado) VALUES 
('Mantenimiento Programado', 'El servidor principal de bases de datos se reiniciará el domingo a las 02:00 AM.', true, null, false),
('Actualización de Nómina', 'El sistema de cálculo de nómina estará inactivo por 2 horas hoy en la tarde.', true, 2, false),
('Nuevas Políticas de Seguridad', 'Por favor revisen el nuevo manual de contraseñas seguras enviado a sus correos.', false, null, false);

-- 4. TICKETS
INSERT INTO ticket (s_titulo, s_descripcion, s_solicitante_nombre, pn_usuario_area_id, pn_usuario_soporte_id, s_estado, s_prioridad, s_justificacion, s_calificacion, d_fecha_creacion, d_fecha_fin) VALUES 
('Caída del dashboard Guerra de Territorios', 'Los usuarios no pueden acceder al panel de control interactivo del juego de comportamiento organizacional. Marca error 500.', 'Director de Carrera', 3, 1, 'EN_PROCESO', 'URGENTE', null, null, CURRENT_TIMESTAMP - INTERVAL '2 days', null),
('Fallo ETL en FOFOMI', 'El pipeline de Airflow no está insertando los datos correctamente en PostgreSQL.', 'Analista de Datos', 3, 2, 'ABIERTO', 'ALTA', null, null, CURRENT_TIMESTAMP - INTERVAL '1 day', null),
('Recuperación de credenciales del Sistema de Cafetería', 'El bot de Telegram para restablecer contraseñas en Spring Boot no está respondiendo a los comandos.', 'Encargado de Cafetería', 2, null, 'ABIERTO', 'NORMAL', null, null, CURRENT_TIMESTAMP, null),
('Impresora sin tóner', 'La impresora del segundo piso marca error de consumibles y no deja escanear.', 'Asistente de RH', 2, 2, 'CERRADO', 'BAJA', 'Se reemplazó el cartucho de tóner negro y se recalibró el escáner.', 'BUENO', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
('Acceso a VPN', 'Necesito acceso a la VPN corporativa para trabajar remoto este viernes.', 'Ana Martínez', 2, 1, 'RESUELTO', 'NORMAL', 'Se generaron las credenciales y se enviaron por correo seguro.', null, CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
('Licencia de FluidSIM expirada', 'Al abrir el simulador FluidSIM Pneumatics marca que la licencia de red no está disponible.', 'Ingeniero de Procesos', 3, null, 'ABIERTO', 'ALTA', null, null, CURRENT_TIMESTAMP, null);

-- 5. BITÁCORA
INSERT INTO bitacora (pn_ticket_id, s_estatus_registrado, s_justificacion, d_fecha_registro) VALUES 
(1, 'ABIERTO', 'Ticket registrado por el usuario.', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(1, 'EN_PROCESO', 'Revisando los logs del contenedor Docker Compose.', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(4, 'ABIERTO', 'Reporte de falta de tóner.', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(4, 'EN_PROCESO', 'Técnico asignado, buscando suministros en almacén.', CURRENT_TIMESTAMP - INTERVAL '4 days 12 hours'),
(4, 'CERRADO', 'Se reemplazó el cartucho de tóner negro y se recalibró el escáner.', CURRENT_TIMESTAMP - INTERVAL '4 days');