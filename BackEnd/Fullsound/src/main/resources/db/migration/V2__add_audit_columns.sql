-- ============================================================
-- FULLSOUND - Migracion V2: añadir columnas de auditoria
-- ============================================================
-- Añade las columnas created_at y updated_at a las tablas que
-- aun no las tienen, estandarizando los campos de auditoria
-- heredados desde BaseEntity en todas las entidades del dominio.
-- ============================================================

-- ==================== COMPRA (Pedido) ====================
ALTER TABLE compra ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE compra ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- ==================== COMPRA_DETALLE (PedidoItem) ====================
ALTER TABLE compra_detalle ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE compra_detalle ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- ==================== PAGO ====================
-- pago ya tiene created_at; solo falta updated_at
ALTER TABLE pago ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- ==================== TIPO_USUARIO (Rol) ====================
ALTER TABLE tipo_usuario ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE tipo_usuario ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;
