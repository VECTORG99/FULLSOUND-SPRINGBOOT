-- ============================================================
-- FULLSOUND - Migracion V3: busqueda de texto completo (FTS)
-- ============================================================
-- Anade una columna tsvector generada a partir de titulo, artista,
-- genero y descripcion, junto con un indice GIN para busquedas
-- full-text eficientes usando to_tsquery / plainto_tsquery.
-- ============================================================

-- ==================== BEAT: columna tsvector + indice GIN ====================
ALTER TABLE beat ADD COLUMN IF NOT EXISTS search_vector tsvector
    GENERATED ALWAYS AS (
        setweight(to_tsvector('spanish', coalesce(titulo, '')), 'A') ||
        setweight(to_tsvector('spanish', coalesce(artista, '')), 'B') ||
        setweight(to_tsvector('spanish', coalesce(genero, '')), 'C') ||
        setweight(to_tsvector('spanish', coalesce(descripcion, '')), 'D')
    ) STORED;

CREATE INDEX IF NOT EXISTS idx_beat_search_vector ON beat USING GIN (search_vector);
