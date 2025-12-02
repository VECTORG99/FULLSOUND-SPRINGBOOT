# Migración a PostgreSQL - Completada

Fecha: 30 de Noviembre, 2025
Estado: COMPLETO - Compilación exitosa

## Resumen

Migración completa del backend de FullSound desde MySQL a PostgreSQL, sincronizando todas las capas MVC con el schema definitivo de la base de datos.

## Cambios Principales

### Entities

**Usuario:**
- Join table `usuario_roles` con columnas: `id_usuario`, `id_tipo_usuario`
- Campo `updatedAt` tipo `LocalDateTime`

**Rol:**
- Campo `descripcion` tipo `VARCHAR(255)`

**Beat - Refactorización Completa:**

Campos eliminados:
- mood, tags, archivoAudio, imagenPortada
- descargas, likes, destacado, activo

Campos agregados:
- duracion (Integer) - segundos
- genero (String) - Trap, Lo-Fi, Hip Hop
- etiquetas (String) - reemplaza tags
- descripcion (String)
- imagenUrl, audioUrl, audioDemoUrl (String)

**Pedido:**
- estado: enum → String (PENDIENTE, PROCESANDO, COMPLETADO, CANCELADO, REEMBOLSADO)
- metodoPago: enum → String (TARJETA, TRANSFERENCIA, PAYPAL)

**Pago:**
- estado: enum → String (PENDIENTE, PROCESANDO, COMPLETADO, FALLIDO, REEMBOLSADO)
- Campo metadata eliminado

### DTOs

Sincronizados con cambios de entities. Estados validados con @Pattern en lugar de enums.

### Repositories

**BeatRepository - Limpieza:**

Métodos eliminados (campos inexistentes):
- findByActivo, findByDestacadoTrueAndActivoTrue
- findTopByOrderByDescargasDesc, findTopByOrderByLikesDesc

Métodos actualizados:
- findByEstado(String estado) - antes usaba enum
- search(@Param("query")) - busca en etiquetas y genero

Métodos nuevos:
- findAllAvailable() - reemplaza búsqueda por activo=true
- findByGeneroContainingIgnoreCase(String genero)

**PedidoRepository y PagoRepository:**
- Parámetros String en lugar de enums

### Services

**PedidoServiceImpl:**
- Eliminada validación beat.getActivo()
- Estados como String: "PENDIENTE", "COMPLETADO", etc.
- Sin conversión EstadoPedido.valueOf()

**PagoServiceImpl:**
- Estados Stripe mapeados a String
- Comparaciones: "COMPLETADO".equals(estado)

### Controllers

**PedidoController:**
- updateEstado(@RequestParam String estado) - antes usaba enum

### Mappers

**BeatMapper:**
- unmappedTargetPolicy = ReportingPolicy.IGNORE
- Soluciona warnings sobre campos eliminados

## Schema PostgreSQL Aplicado

```sql
-- Tabla: tipo_usuario (equivalente a Rol)
CREATE TABLE tipo_usuario (
    id_tipo_usuario INTEGER PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- Tabla: usuario
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla: usuario_roles (join table)
CREATE TABLE usuario_roles (
    id_usuario INTEGER REFERENCES usuario(id_usuario),
    id_tipo_usuario INTEGER REFERENCES tipo_usuario(id_tipo_usuario),
    PRIMARY KEY (id_usuario, id_tipo_usuario)
);

-- Tabla: beat
CREATE TABLE beat (
    id_beat SERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    artista VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    precio INTEGER NOT NULL,
    bpm INTEGER NOT NULL,
    tonalidad VARCHAR(10) NOT NULL,
    duracion INTEGER NOT NULL,
    genero VARCHAR(100),
    etiquetas VARCHAR(500),
    descripcion TEXT,
    estado VARCHAR(20) DEFAULT 'DISPONIBLE',
    reproducciones INTEGER DEFAULT 0,
    imagen_url VARCHAR(500),
    audio_url VARCHAR(500),
    audio_demo_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla: compra (equivalente a Pedido)
CREATE TABLE compra (
    id_compra SERIAL PRIMARY KEY,
    numero_pedido VARCHAR(50) UNIQUE NOT NULL,
    id_usuario INTEGER REFERENCES usuario(id_usuario),
    fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total INTEGER NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    metodo_pago VARCHAR(20)
);

-- Tabla: compra_detalle (equivalente a PedidoItem)
CREATE TABLE compra_detalle (
    id_detalle SERIAL PRIMARY KEY,
    id_compra INTEGER REFERENCES compra(id_compra),
    id_beat INTEGER REFERENCES beat(id_beat),
    nombre_item VARCHAR(255),
    cantidad INTEGER DEFAULT 1,
    precio_unitario INTEGER NOT NULL
);

-- Tabla: pago
CREATE TABLE pago (
    id_pago SERIAL PRIMARY KEY,
    id_compra INTEGER REFERENCES compra(id_compra),
    stripe_payment_intent_id VARCHAR(255) UNIQUE,
    stripe_charge_id VARCHAR(255) UNIQUE,
    monto INTEGER NOT NULL,
    moneda VARCHAR(3) DEFAULT 'USD',
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP
);
```

## Validación

Compilación exitosa:
```powershell
.\mvnw.cmd clean compile
# BUILD SUCCESS (43.268 s)
```

## Archivos Modificados

- 6 Entities (Usuario, Rol, Beat, Pedido, PedidoItem, Pago)
- 6 DTOs (BeatRequest/Response, PedidoRequest/Response, PagoRequest/Response)
- 3 Repositories (BeatRepository, PedidoRepository, PagoRepository)
- 2 Services (PedidoServiceImpl, PagoServiceImpl)
- 1 Controller (PedidoController)
- 1 Mapper (BeatMapper)

Total: 19 archivos

## Notas Técnicas

- IDs: INTEGER con SERIAL para auto-incremento
- Estados: VARCHAR validados con @Pattern en DTOs
- Join table: id_usuario, id_tipo_usuario (no camelCase)
- Campos de auditoría: created_at, updated_at (LocalDateTime)

## Próximos Pasos

1. Ejecutar scripts SQL en PostgreSQL
2. Configurar credenciales en application.properties
3. Verificar logs al ejecutar
4. Probar endpoints con Swagger UI
5. Implementar tests unitarios

Copyright 2025 FULLSOUND. Todos los derechos reservados.
