-- 1. Inicialización de Roles del Sistema
INSERT INTO rol (id_rol, nombre, descripcion) VALUES
(1, 'Administrador', 'Control total del sistema, configuración y gestión de usuarios y roles'),
(2, 'Funcionario', 'Gestión, revisión, atención y derivación de reclamos ciudadanos')
ON CONFLICT (id_rol) DO NOTHING;

-- 2. Inicialización de Áreas Municipales
INSERT INTO area (id_area, nombre, descripcion) VALUES
(1, 'Atención al Ciudadano', 'Mesa de partes, recepción inicial y derivación de reclamos'),
(2, 'Obras Públicas', 'Mantenimiento de pistas, veredas e infraestructura'),
(3, 'Gestión Ambiental', 'Limpieza pública y mantenimiento de áreas verdes'),
(4, 'Administracion General', 'Admnistracion del departamento pricipal administrativo')
ON CONFLICT (id_area) DO NOTHING;

-- 3. Usuarios del Sistema
-- Admin (Rol 1, sin área fija porque gestiona todo el sistema) -> admin_root | admin123
INSERT INTO usuario (id_usuario, nombre_usuario, nombres, correo, contrasena, estado, id_rol, id_area) VALUES
(1, 'admin_root', 'Administrador del Sistema', 'admin@municipalidad.gob.pe', '$2a$10$MEgE9cmiG7.CwT0JLLSxxew3kRsg2lFnjp9qV8E12gDpvW6ggVCc.', true, 1, 4)
ON CONFLICT (id_usuario) DO NOTHING;

-- Funcionario 1: Atención al Ciudadano (Área 1) -> funcionario_juan | func123
INSERT INTO usuario (id_usuario, nombre_usuario, nombres, correo, contrasena, estado, id_rol, id_area) VALUES
(2, 'funcionario_juan', 'Juan Pérez (Atención al Público)', 'juan@municipalidad.gob.pe', '$2a$10$5wwfXufKmjfyWF7oBZMlAOo7izugY64XwAtuQJcaH9F.EEMUmAKTa', true, 2, 1)
ON CONFLICT (id_usuario) DO NOTHING;

-- Funcionario 2: Obras Públicas (Área 2) -> funcionario_carlos | func123
INSERT INTO usuario (id_usuario, nombre_usuario, nombres, correo, contrasena, estado, id_rol, id_area) VALUES
(3, 'funcionario_carlos', 'Carlos Ramos (Obras Públicas)', 'carlos@municipalidad.gob.pe', '$2a$10$5wwfXufKmjfyWF7oBZMlAOo7izugY64XwAtuQJcaH9F.EEMUmAKTa', true, 2, 2)
ON CONFLICT (id_usuario) DO NOTHING;

-- Funcionario 3: Gestión Ambiental (Área 3) -> funcionario_ana | func123
INSERT INTO usuario (id_usuario, nombre_usuario, nombres, correo, contrasena, estado, id_rol, id_area) VALUES
(4, 'funcionario_ana', 'Ana Torres (Gestión Ambiental)', 'ana@municipalidad.gob.pe', '$2a$10$5wwfXufKmjfyWF7oBZMlAOo7izugY64XwAtuQJcaH9F.EEMUmAKTa', true, 2, 3)
ON CONFLICT (id_usuario) DO NOTHING;

-- 4. Inicialización de Categorías Fijas de Reclamo
INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES
(1, 'Limpieza Pública', 'Recojo de residuos sólidos, desmonte o falta de barrido en calles y parques'),
(2, 'Seguridad y Serenazgo', 'Falta de patrullaje, comercio ambulatorio no autorizado o consumo de alcohol en vía pública'),
(3, 'Áreas Verdes', 'Falta de poda, riego o mantenimiento en parques y jardines')
ON CONFLICT (id_categoria) DO NOTHING;

-- 5. Inicialización de un Ciudadano de prueba
INSERT INTO ciudadano (id_ciudadano, dni, nombres, apellidos, correo, telefono, sexo) VALUES
(1, '72345678', 'María', 'López', 'maria.lopez@email.com', '987654321', 'F')
ON CONFLICT (id_ciudadano) DO NOTHING;

-- 6. Inicialización de Reclamos (Llegan primero a Atención al Ciudadano -> Área 1, Juan)
INSERT INTO reclamo (id_reclamo, asunto, descripcion, estado, fecha_registro, prioridad, id_ciudadano, id_categoria, id_area, id_usuario_asignado) VALUES
(1, 'Vereda rota en la plaza', 'Hay un bache peligroso en la vereda que dificulta el paso peatonal.', 'REGISTRADO', CURRENT_TIMESTAMP, 'ALTA', 1, 2, 1, 2),
(2, 'Basura acumulada en parque', 'Hace 3 días no pasa el camión recolector por el parque central.', 'REGISTRADO', CURRENT_TIMESTAMP, 'MEDIA', 1, 1, 1, 2)
ON CONFLICT (id_reclamo) DO NOTHING;

-- 7. Sincronizar secuencias de PostgreSQL (evita el error de duplicate key al crear nuevos registros)
SELECT setval(pg_get_serial_sequence('rol', 'id_rol'), COALESCE((SELECT MAX(id_rol) FROM rol), 1));
SELECT setval(pg_get_serial_sequence('area', 'id_area'), COALESCE((SELECT MAX(id_area) FROM area), 1));
SELECT setval(pg_get_serial_sequence('usuario', 'id_usuario'), COALESCE((SELECT MAX(id_usuario) FROM usuario), 1));
SELECT setval(pg_get_serial_sequence('categoria', 'id_categoria'), COALESCE((SELECT MAX(id_categoria) FROM categoria), 1));
SELECT setval(pg_get_serial_sequence('ciudadano', 'id_ciudadano'), COALESCE((SELECT MAX(id_ciudadano) FROM ciudadano), 1));
SELECT setval(pg_get_serial_sequence('reclamo', 'id_reclamo'), COALESCE((SELECT MAX(id_reclamo) FROM reclamo), 1));