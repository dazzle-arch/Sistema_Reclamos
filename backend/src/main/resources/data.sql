-- 1. Inicialización de Roles del Sistema
INSERT INTO rol (id_rol, nombre, descripcion) VALUES
(1, 'Administrador', 'Control total del sistema, configuración y gestión de usuarios'),
(2, 'Funcionario', 'Gestión, revisión y derivación de reclamos ciudadanos')
ON CONFLICT (id_rol) DO NOTHING;

-- 2. Inicialización de Áreas Municipales
INSERT INTO area (id_area, nombre, descripcion) VALUES
(1, 'Atención al Ciudadano', 'Mesa de partes y recepción de solicitudes vecinales'),
(2, 'Obras Públicas', 'Mantenimiento de pistas, veredas e infraestructura'),
(3, 'Gestión Ambiental', 'Limpieza pública y mantenimiento de áreas verdes')
ON CONFLICT (id_area) DO NOTHING;

-- 3. Usuario 1: Administrador (Rol 1, Área 1)
-- Credenciales para Postman -> Usuario: admin_root | Clave: admin123
INSERT INTO usuario (id_usuario, nombre_usuario, nombres, correo, contrasena, estado, id_rol, id_area) VALUES
(1, 'admin_root', 'Administrador del Sistema', 'sistemas@municipalidad.gob.pe', '$2a$10$MEgE9cmiG7.CwT0JLLSxxew3kRsg2lFnjp9qV8E12gDpvW6ggVCc.', true, 1, 1)
ON CONFLICT (id_usuario) DO NOTHING;

-- 4. Usuario 2: Funcionario (Rol 2, Área 2)
-- Credenciales para Postman -> Usuario: funcionario_juan | Clave: func123
INSERT INTO usuario (id_usuario, nombre_usuario, nombres, correo, contrasena, estado, id_rol, id_area) VALUES
(2, 'funcionario_juan', 'Juan Perez (Atención)', 'juan@municipalidad.gob.pe', '$2a$10$5wwfXufKmjfyWF7oBZMlAOo7izugY64XwAtuQJcaH9F.EEMUmAKTa', true, 2, 2)
ON CONFLICT (id_usuario) DO NOTHING;

-- 5. Inicialización de Categorías Fijas de Reclamo
INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES
(1, 'Limpieza Pública', 'Recojo de residuos sólidos, desmonte o falta de barrido en calles y parques'),
(2, 'Seguridad y Serenazgo', 'Falta de patrullaje, comercio ambulatorio no autorizado o consumo de alcohol en vía pública'),
(3, 'Áreas Verdes', 'Falta de poda, riego o mantenimiento en parques y jardines')
ON CONFLICT (id_categoria) DO NOTHING;

-- 6. Inicialización de un Ciudadano de prueba
INSERT INTO ciudadano (id_ciudadano, dni, nombres, apellidos, correo, telefono, sexo) VALUES
(1, '72345678', 'María', 'López', 'maria.lopez@email.com', '987654321', 'F')
ON CONFLICT (id_ciudadano) DO NOTHING;

-- 7. Inicialización de Reclamos para el Funcionario Juan
INSERT INTO reclamo (id_reclamo, asunto, descripcion, estado, fecha_registro, prioridad, id_ciudadano, id_categoria, id_area, id_usuario_asignado) VALUES
(1, 'Vereda rota en la plaza', 'Hay un bache peligroso en la vereda que dificulta el paso peatonal.', 'EN_PROCESO', CURRENT_TIMESTAMP, 'ALTA', 1, 2, 2, 2),
(2, 'Basura acumulada en parque', 'Hace 3 días no pasa el camión recolector por el parque central.', 'REGISTRADO', CURRENT_TIMESTAMP, 'MEDIA', 1, 1, 2, 2)
ON CONFLICT (id_reclamo) DO NOTHING;