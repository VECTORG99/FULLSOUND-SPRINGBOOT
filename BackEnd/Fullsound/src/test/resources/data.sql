-- Datos iniciales para tests (H2)
-- Los roles son necesarios para el registro de usuarios
-- El schema se recrea en cada test (ddl-auto=create-drop)
-- created_at es obligatorio (heredado de BaseEntity)
INSERT INTO tipo_usuario (tipo, descripcion, created_at) VALUES
    ('administrador', 'Administrador del sistema', CURRENT_TIMESTAMP),
    ('cliente', 'Usuario cliente', CURRENT_TIMESTAMP);
