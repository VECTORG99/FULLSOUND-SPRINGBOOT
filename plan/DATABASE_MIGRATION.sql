-- ============================================================
-- FULLSOUND - Script de Mejoras Base de Datos
-- ============================================================
-- Descripción: Optimiza la BD actual eliminando campos innecesarios
--              y agregando campos faltantes para Spring Boot
-- Base de datos: Fullsound_Base
-- Fecha: 2025-11-13
-- ============================================================

USE Fullsound_Base;

-- ============================================================
-- PASO 1: BACKUP DE SEGURIDAD (ejecutar antes de modificar)
-- ============================================================
-- CREATE DATABASE Fullsound_Base_Backup;
-- mysqldump -u root -p Fullsound_Base > backup_fullsound_$(date +%Y%m%d_%H%M%S).sql

-- ============================================================
-- PASO 2: MEJORAS EN TABLA tipo_usuario
-- ============================================================

-- Agregar descripción para los roles
ALTER TABLE tipo_usuario 
ADD COLUMN descripcion VARCHAR(100) AFTER nombre_tipo;

-- Actualizar descripciones
UPDATE tipo_usuario SET descripcion = 'Usuario cliente estándar' WHERE nombre_tipo = 'cliente';
UPDATE tipo_usuario SET descripcion = 'Usuario administrador del sistema' WHERE nombre_tipo = 'administrador';

-- Agregar campo activo para soft deletes
ALTER TABLE tipo_usuario 
ADD COLUMN activo BOOLEAN DEFAULT TRUE AFTER descripcion;

-- Agregar auditoría
ALTER TABLE tipo_usuario 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER activo,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- ============================================================
-- PASO 3: MEJORAS EN TABLA usuario
-- ============================================================

-- Agregar campos adicionales de perfil
ALTER TABLE usuario 
ADD COLUMN nombre_completo VARCHAR(100) AFTER contrasena,
ADD COLUMN telefono VARCHAR(20) AFTER nombre_completo,
ADD COLUMN biografia TEXT AFTER telefono;

-- Agregar URLs de imágenes
ALTER TABLE usuario 
ADD COLUMN url_avatar VARCHAR(255) AFTER biografia,
ADD COLUMN url_portada VARCHAR(255) AFTER url_avatar;

-- Agregar verificación de email
ALTER TABLE usuario 
ADD COLUMN email_verificado BOOLEAN DEFAULT FALSE AFTER url_portada,
ADD COLUMN verificacion_token VARCHAR(255) AFTER email_verificado;

-- Agregar recuperación de contraseña
ALTER TABLE usuario 
ADD COLUMN reset_token VARCHAR(255) AFTER verificacion_token,
ADD COLUMN reset_token_expiry BIGINT AFTER reset_token;

-- Agregar campo activo para soft deletes
ALTER TABLE usuario 
ADD COLUMN activo BOOLEAN DEFAULT TRUE AFTER reset_token_expiry;

-- Agregar auditoría (updated_at, created_at ya existe como fecha_registro)
ALTER TABLE usuario 
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER fecha_registro;

-- Crear índices para mejorar rendimiento
CREATE INDEX idx_email ON usuario(correo);
CREATE INDEX idx_username ON usuario(nombre_usuario);
CREATE INDEX idx_tipo_usuario ON usuario(id_tipo_usuario);

-- ============================================================
-- PASO 4: MEJORAS EN TABLA beat
-- ============================================================

-- ELIMINAR campos calculados innecesarios
-- NOTA: Estos campos se calcularán en el backend/frontend
ALTER TABLE beat 
DROP COLUMN precio_formateado,
DROP COLUMN enlace_producto;

-- Agregar slug para URLs amigables
ALTER TABLE beat 
ADD COLUMN slug VARCHAR(200) UNIQUE AFTER titulo;

-- Generar slugs automáticamente para beats existentes
UPDATE beat 
SET slug = LOWER(CONCAT(
    REPLACE(REPLACE(REPLACE(titulo, ' ', '-'), 'á', 'a'), 'é', 'e'),
    '-', id_beat
));

-- Agregar características musicales
ALTER TABLE beat 
ADD COLUMN bpm INT AFTER genero,
ADD COLUMN tonalidad VARCHAR(20) AFTER bpm,
ADD COLUMN mood VARCHAR(50) AFTER tonalidad,
ADD COLUMN tags TEXT AFTER mood;

-- Agregar estado del beat
ALTER TABLE beat 
ADD COLUMN estado VARCHAR(20) DEFAULT 'DISPONIBLE' AFTER tags,
ADD CONSTRAINT chk_estado CHECK (estado IN ('DISPONIBLE', 'VENDIDO', 'RESERVADO', 'INACTIVO'));

-- Agregar archivos (renombrar fuente_audio a url_audio_preview)
ALTER TABLE beat 
CHANGE COLUMN fuente_audio url_audio_preview VARCHAR(255);

ALTER TABLE beat 
ADD COLUMN url_audio_full VARCHAR(255) AFTER url_audio_preview,
ADD COLUMN url_stems VARCHAR(255) AFTER url_audio_full,
ADD COLUMN duracion_segundos INT AFTER url_stems;

-- Renombrar imagen a url_imagen
ALTER TABLE beat 
CHANGE COLUMN imagen url_imagen VARCHAR(255);

-- Agregar estadísticas
ALTER TABLE beat 
ADD COLUMN reproducciones INT DEFAULT 0 AFTER url_imagen,
ADD COLUMN descargas INT DEFAULT 0 AFTER reproducciones,
ADD COLUMN likes INT DEFAULT 0 AFTER descargas,
ADD COLUMN destacado BOOLEAN DEFAULT FALSE AFTER likes;

-- Agregar campo activo para soft deletes
ALTER TABLE beat 
ADD COLUMN activo BOOLEAN DEFAULT TRUE AFTER destacado;

-- Renombrar fecha_subida a created_at y agregar updated_at
ALTER TABLE beat 
CHANGE COLUMN fecha_subida created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE beat 
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- Crear índices para búsquedas
CREATE INDEX idx_genero ON beat(genero);
CREATE INDEX idx_estado ON beat(estado);
CREATE INDEX idx_bpm ON beat(bpm);
CREATE INDEX idx_usuario ON beat(usuario_id);
CREATE INDEX idx_slug ON beat(slug);

-- ============================================================
-- PASO 5: MEJORAS EN TABLA compra (PEDIDOS)
-- ============================================================

-- Agregar número de pedido único
ALTER TABLE compra 
ADD COLUMN numero_pedido VARCHAR(50) UNIQUE AFTER id_compra;

-- Generar números de pedido para compras existentes
UPDATE compra 
SET numero_pedido = CONCAT('FS-', YEAR(fecha), '-', LPAD(id_compra, 6, '0'));

-- Hacer campo obligatorio después de generar valores
ALTER TABLE compra 
MODIFY COLUMN numero_pedido VARCHAR(50) NOT NULL;

-- Agregar estado del pedido
ALTER TABLE compra 
ADD COLUMN estado VARCHAR(20) DEFAULT 'COMPLETADO' AFTER numero_pedido,
ADD CONSTRAINT chk_estado_pedido CHECK (estado IN ('PENDIENTE', 'PROCESANDO', 'COMPLETADO', 'CANCELADO', 'REEMBOLSADO'));

-- Agregar fecha de completado
ALTER TABLE compra 
ADD COLUMN fecha_completado TIMESTAMP AFTER fecha;

-- Para compras existentes, asumir que fecha_completado = fecha
UPDATE compra SET fecha_completado = fecha WHERE fecha_completado IS NULL;

-- Agregar notas
ALTER TABLE compra 
ADD COLUMN notas TEXT AFTER fecha_completado;

-- Agregar campo activo
ALTER TABLE compra 
ADD COLUMN activo BOOLEAN DEFAULT TRUE AFTER notas;

-- Renombrar fecha a created_at y agregar updated_at
ALTER TABLE compra 
CHANGE COLUMN fecha created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE compra 
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- Crear índices
CREATE INDEX idx_usuario_compra ON compra(usuario_id);
CREATE INDEX idx_estado_compra ON compra(estado);
CREATE INDEX idx_fecha_compra ON compra(created_at);
CREATE INDEX idx_numero_pedido ON compra(numero_pedido);

-- ============================================================
-- PASO 6: MEJORAS EN TABLA compra_detalle
-- ============================================================

-- Agregar cantidad (por defecto 1 para beats existentes)
ALTER TABLE compra_detalle 
ADD COLUMN cantidad INT DEFAULT 1 AFTER beat_id;

-- Agregar snapshot del nombre del beat
ALTER TABLE compra_detalle 
ADD COLUMN nombre_item VARCHAR(150) AFTER cantidad;

-- Actualizar nombre_item con títulos de beats existentes
UPDATE compra_detalle cd
JOIN beat b ON cd.beat_id = b.id_beat
SET cd.nombre_item = b.titulo;

-- Agregar campo activo
ALTER TABLE compra_detalle 
ADD COLUMN activo BOOLEAN DEFAULT TRUE AFTER precio_con_iva;

-- Agregar auditoría
ALTER TABLE compra_detalle 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER activo,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- Crear índices
CREATE INDEX idx_compra ON compra_detalle(compra_id);
CREATE INDEX idx_beat ON compra_detalle(beat_id);

-- ============================================================
-- PASO 7: AGREGAR TABLA DE PAGOS (OPCIONAL - para Stripe)
-- ============================================================

CREATE TABLE IF NOT EXISTS pago (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    compra_id INT NOT NULL UNIQUE,
    metodo_pago VARCHAR(20) NOT NULL DEFAULT 'STRIPE',
    estado_pago VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    monto DECIMAL(10,2) NOT NULL,
    moneda VARCHAR(10) DEFAULT 'USD',
    stripe_payment_intent_id VARCHAR(255) UNIQUE,
    stripe_charge_id VARCHAR(255),
    fecha_pago TIMESTAMP NULL,
    detalles TEXT,
    mensaje_error TEXT,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pago_compra FOREIGN KEY (compra_id) REFERENCES compra(id_compra) ON DELETE CASCADE,
    CONSTRAINT chk_metodo_pago CHECK (metodo_pago IN ('STRIPE', 'PAYPAL', 'TRANSFERENCIA')),
    CONSTRAINT chk_estado_pago CHECK (estado_pago IN ('PENDIENTE', 'PROCESANDO', 'EXITOSO', 'FALLIDO', 'REEMBOLSADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Crear índices para tabla pago
CREATE INDEX idx_compra_pago ON pago(compra_id);
CREATE INDEX idx_stripe_payment_intent ON pago(stripe_payment_intent_id);
CREATE INDEX idx_estado_pago ON pago(estado_pago);

-- Insertar pagos para compras existentes (todas exitosas)
INSERT INTO pago (compra_id, metodo_pago, estado_pago, monto, fecha_pago)
SELECT id_compra, 'STRIPE', 'EXITOSO', total_con_iva, created_at
FROM compra
WHERE NOT EXISTS (SELECT 1 FROM pago WHERE pago.compra_id = compra.id_compra);

-- ============================================================
-- PASO 8: TABLA DE RELACIÓN USUARIO-ROLES (Many-to-Many)
-- ============================================================

CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id INT NOT NULL,
    rol_id INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_roles_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_roles_rol FOREIGN KEY (rol_id) REFERENCES tipo_usuario(id_tipo_usuario) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Migrar relaciones existentes
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT id_usuario, id_tipo_usuario
FROM usuario
WHERE NOT EXISTS (
    SELECT 1 FROM usuario_roles 
    WHERE usuario_roles.usuario_id = usuario.id_usuario 
    AND usuario_roles.rol_id = usuario.id_tipo_usuario
);

-- NOTA: Mantener id_tipo_usuario en tabla usuario por compatibilidad
-- pero Spring Boot usará la tabla usuario_roles

-- ============================================================
-- PASO 9: VERIFICACIÓN FINAL
-- ============================================================

-- Mostrar estructura actualizada
SHOW TABLES;

SELECT 
    TABLE_NAME, 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE, 
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'Fullsound_Base'
ORDER BY TABLE_NAME, ORDINAL_POSITION;

-- Contar registros
SELECT 'tipo_usuario' as tabla, COUNT(*) as registros FROM tipo_usuario
UNION ALL
SELECT 'usuario', COUNT(*) FROM usuario
UNION ALL
SELECT 'beat', COUNT(*) FROM beat
UNION ALL
SELECT 'compra', COUNT(*) FROM compra
UNION ALL
SELECT 'compra_detalle', COUNT(*) FROM compra_detalle
UNION ALL
SELECT 'pago', COUNT(*) FROM pago
UNION ALL
SELECT 'usuario_roles', COUNT(*) FROM usuario_roles;

-- ============================================================
-- PASO 10: DATOS DE PRUEBA ADICIONALES (OPCIONAL)
-- ============================================================

-- Actualizar datos de prueba existentes
UPDATE usuario SET 
    nombre_completo = 'Juan Pérez',
    telefono = '+57 300 1234567',
    email_verificado = TRUE
WHERE nombre_usuario = 'juan123';

UPDATE usuario SET 
    nombre_completo = 'Administrador del Sistema',
    telefono = '+57 300 9999999',
    email_verificado = TRUE
WHERE nombre_usuario = 'amoraga' OR nombre_usuario = 'scanchaya';

-- Actualizar beats con datos adicionales
UPDATE beat SET 
    bpm = 160,
    tonalidad = 'A Minor',
    mood = 'Dark',
    tags = 'electronica,lampa,dark'
WHERE id_beat = 1;

UPDATE beat SET 
    bpm = 120,
    tonalidad = 'C Major',
    mood = 'Happy',
    tags = 'pop,commercial,upbeat'
WHERE id_beat = 2;

UPDATE beat SET 
    bpm = 85,
    tonalidad = 'G Minor',
    mood = 'Chill',
    tags = 'chill,relax,ambient'
WHERE id_beat = 3;

UPDATE beat SET 
    bpm = 110,
    tonalidad = 'D Major',
    mood = 'Funky',
    tags = 'funk,groove,party'
WHERE id_beat = 4;

UPDATE beat SET 
    bpm = 100,
    tonalidad = 'F Minor',
    mood = 'Smooth',
    tags = 'jazz,smooth,relaxed'
WHERE id_beat = 5;

UPDATE beat SET 
    bpm = 140,
    tonalidad = 'E Minor',
    mood = 'Energetic',
    tags = 'rock,energy,power'
WHERE id_beat = 6;

UPDATE beat SET 
    bpm = 90,
    tonalidad = 'C Minor',
    mood = 'Majestic',
    tags = 'classical,symphony,orchestra'
WHERE id_beat = 7;

UPDATE beat SET 
    bpm = 95,
    tonalidad = 'D Minor',
    mood = 'Urban',
    tags = 'hiphop,rap,urban'
WHERE id_beat = 8;

UPDATE beat SET 
    bpm = 80,
    tonalidad = 'A Major',
    mood = 'Relaxed',
    tags = 'reggae,roots,island'
WHERE id_beat = 9;

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================

-- NOTA: Ejecutar este script en un entorno de desarrollo primero
-- NOTA: Hacer backup antes de ejecutar en producción
-- NOTA: Algunos ALTER TABLE pueden tardar si hay muchos datos

SELECT 'Script de migración completado exitosamente' as mensaje;
