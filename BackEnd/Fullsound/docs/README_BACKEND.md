# FullSound Backend - Spring Boot

Backend REST API para marketplace de beats musicales.

## Stack Tecnológico

- Java 21
- Spring Boot 3.4.1
- PostgreSQL (con Flyway para migraciones)
- Spring Security + JWT
- WebSocket/STOMP (notificaciones en tiempo real)
- Caffeine (caché en memoria)
- Swagger/OpenAPI
- MapStruct
- Maven

## Requisitos

```bash
java -version    # Java 21+
mvn -version     # Maven 3.8+
psql --version   # PostgreSQL 12+
```

## Configuración

### application.properties

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/fullsound
spring.datasource.username=postgres
spring.datasource.password=tu_password

# JWT
jwt.secret=tu_secret_key_256_bits
jwt.expiration=86400000
```

### Ejecutar

```bash
cd BackEnd/Fullsound
./mvnw spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui.html

## Autenticación

### Registrar Usuario

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario1",
    "email": "usuario1@test.com",
    "password": "password123",
    "nombreCompleto": "Usuario Uno"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario1",
    "password": "password123"
  }'
```

Respuesta incluye token JWT:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "usuario": {
    "id": 1,
    "username": "usuario1",
    "roles": ["cliente"]
  }
}
```

### Usar Token

```bash
curl http://localhost:8080/api/beats \
  -H "Authorization: Bearer {token}"
```

## Endpoints Principales

### Auth - `/api/auth`
- POST `/register` - Registrar usuario
- POST `/login` - Iniciar sesión
- POST `/forgot-password` - Recuperar contraseña
- GET `/check-username/{username}` - Verificar disponibilidad
- GET `/check-email/{email}` - Verificar disponibilidad

### Beats - `/api/beats`
- GET `/` - Listar beats activos (paginado)
- GET `/{id}` - Obtener beat (incluye `calificacionPromedio`, `totalResenas`, `totalFavoritos`)
- GET `/slug/{slug}` - Obtener por slug
- GET `/featured` - Beats destacados
- GET `/search?q={texto}` - **Buscar beats** por título, artista, género, etiquetas o descripción (paginado). Usa full-text PostgreSQL (`tsvector`) con ranking; degrada a `ILIKE`/`LIKE` si FTS no está disponible.
- GET `/filter/price?min={min}&max={max}` - Filtrar por rango de precio (paginado)
- GET `/filter/bpm?min={min}&max={max}` - Filtrar por rango de BPM (paginado)
- POST `/` - Crear beat (Admin)
- PUT `/{id}` - Actualizar beat (Admin)
- DELETE `/{id}` - Eliminar beat (Admin)
- POST `/{id}/play` - Incrementar reproducciones
- POST `/{id}/reviews` - **Crear/actualizar reseña** de un beat (Auth, rating 1-5 + comentario)
- GET `/{id}/reviews` - **Listar reseñas** de un beat (público)

### Reseñas - `/api/reviews`
- DELETE `/{id}` - **Eliminar reseña propia** (Auth; solo el autor puede eliminarla)

### Pedidos - `/api/pedidos` (Auth requerido)
- GET `/mis-pedidos` - Pedidos del usuario
- GET `/{id}` - Detalle de pedido
- GET `/numero/{numero}` - Buscar por número
- GET `/` - Listar todos (Admin)

### Pagos - `/api/pagos` (Auth requerido)
- POST `/create-payment-intent` - Crear intento de pago
- POST `/confirm` - Confirmar pago
- POST `/webhook` - Webhook de Stripe
- GET `/{id}` - Detalle de pago

### Usuarios - `/api/usuarios`
- GET `/me` - Perfil propio (Auth)
- PUT `/me` - Actualizar perfil propio (Auth)
- POST `/cambiar-password` - Cambiar contraseña (Auth)
- GET `/{id}` - Obtener usuario (Admin)
- GET `/` - Listar usuarios (Admin, paginado)
- DELETE `/{id}` - Desactivar usuario (Admin)
- PATCH `/{id}/activate` - Reactivar usuario (Admin)

#### Favoritos / Wishlist - `/api/usuarios/me/favorites` (Auth requerido)
- POST `/{beatId}` - **Añadir beat a favoritos** (idempotente: no duplica)
- DELETE `/{beatId}` - **Quitar beat de favoritos** (no falla si no existía)
- GET `/` - **Listar beats favoritos** del usuario (paginado)

#### Notificaciones del usuario - `/api/usuarios/me/notifications` (Auth requerido)
- GET `/` - **Listar mis notificaciones** (paginado)
- GET `/unread-count` - **Conteo de notificaciones no leídas** (`{ "unreadCount": n }`)

### Notificaciones (admin/sistema) - `/api/notifications`
- POST `/` - Crear notificación para un usuario (Admin; la envía también en tiempo real vía WebSocket a `/user/{id}/queue/notifications`)
- PUT `/{id}/read` - Marcar notificación como leída (Auth)
- PUT `/read-all` - Marcar todas como leídas (Auth)

> **WebSocket/STOMP:** endpoint de conexión `/ws` (fallback SockJS en `/ws-sockjs`). Las notificaciones en tiempo real se publican en `/user/queue/notifications`. El handshake STOMP acepta el header `Authorization: Bearer {token}`.

### Estadísticas - `/api/estadisticas` (Admin)
- GET `/dashboard` - Dashboard general
- GET `/beats/top` - Beats más vendidos
- GET `/ventas` - Reporte de ventas

## Estructura del Proyecto

```
src/main/java/Fullsound/Fullsound/
├── config/          # Configuración (Security, CORS, Swagger, WebSocket, Caché)
├── controller/      # REST Controllers (Auth, Beat, Review, Usuario, Notificacion, Pago, Pedido, ...)
├── dto/             # DTOs Request/Response
├── exception/       # Manejo de excepciones global
├── mapper/          # MapStruct mappers
├── model/           # Entidades JPA (Beat, Usuario, Rol, Pedido, PedidoItem, Pago, Review, UsuarioFavorito, Notificacion)
├── repository/      # Repositorios Spring Data
├── security/        # JWT + Spring Security + Rate Limiting
├── service/         # Lógica de negocio (Beat, Review, Favorito, Notificacion, Usuario, Auth, Pago, Pedido)
├── validation/      # Validadores personalizados (RUT)
└── websocket/       # NotificationPublisher (STOMP)
```

## Funcionalidades Avanzadas (Round 3)

### Búsqueda de beats (full-text)
- Endpoint `GET /api/beats/search?q={texto}` (paginado).
- Migración `V3__add_beat_fulltext_search.sql`: columna `tsvector` generada (`search_vector`) con pesos por campo (título A, artista B, género C, descripción D) + índice GIN.
- Query nativa con `plainto_tsquery('spanish', ...)` y `ts_rank`. Degradación graceful a `LIKE` cuando FTS no está disponible (p.ej. H2 en tests).

### Favoritos / Wishlist
- Entidad `UsuarioFavorito` (join table `usuario_favorito`), migración `V4`.
- Endpoints bajo `/api/usuarios/me/favorites`. Idempotente.
- El contador `totalFavoritos` se incluye en la respuesta de `Beat`.

### Reseñas / Valoraciones
- Entidad `Review` (tabla `review`), migración `V5`. Rating 1-5 con `CHECK`, único por `(beat, usuario)`.
- Endpoints `POST/GET /api/beats/{id}/reviews` y `DELETE /api/reviews/{id}` (solo el autor).
- La respuesta de `Beat` incluye `calificacionPromedio` y `totalResenas`.

### Notificaciones in-app
- Entidad `Notificacion` (tabla `notificacion`), migración `V6`.
- Endpoints `GET /api/usuarios/me/notifications`, `GET /api/usuarios/me/notifications/unread-count`, `PUT /api/notifications/{id}/read`, `POST /api/notifications` (admin).
- Publicación en tiempo real vía WebSocket/STOMP (`NotificationPublisher`).

## Migraciones Flyway

```
src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__add_audit_columns.sql
├── V3__add_beat_fulltext_search.sql
├── V4__add_usuario_favoritos.sql
├── V5__add_reviews.sql
└── V6__add_notifications.sql
```

> En tests se usa H2 en memoria con `spring.flyway.enabled=false` y `ddl-auto=create-drop`, por lo que las migraciones PostgreSQL no se ejecutan en el entorno de test.

## Schema PostgreSQL

```sql
-- Roles
CREATE TABLE tipo_usuario (
    id_tipo_usuario INTEGER PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- Usuarios
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Relación usuario-roles
CREATE TABLE usuario_roles (
    id_usuario INTEGER REFERENCES usuario(id_usuario),
    id_tipo_usuario INTEGER REFERENCES tipo_usuario(id_tipo_usuario),
    PRIMARY KEY (id_usuario, id_tipo_usuario)
);

-- Beats
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

-- Pedidos
CREATE TABLE compra (
    id_compra SERIAL PRIMARY KEY,
    numero_pedido VARCHAR(50) UNIQUE NOT NULL,
    id_usuario INTEGER REFERENCES usuario(id_usuario),
    fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total INTEGER NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    metodo_pago VARCHAR(20)
);

-- Items de pedido
CREATE TABLE compra_detalle (
    id_detalle SERIAL PRIMARY KEY,
    id_compra INTEGER REFERENCES compra(id_compra),
    id_beat INTEGER REFERENCES beat(id_beat),
    nombre_item VARCHAR(255),
    cantidad INTEGER DEFAULT 1,
    precio_unitario INTEGER NOT NULL
);

-- Pagos
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

## Migración desde MySQL

El proyecto fue migrado desde MySQL a PostgreSQL. Cambios principales:

- Enums eliminados, reemplazados por VARCHAR con validaciones @Pattern
- Campos eliminados de Beat: mood, tags, archivoAudio, imagenPortada, descargas, likes, destacado, activo
- Campos agregados a Beat: duracion, genero, etiquetas, descripcion, imagenUrl, audioUrl, audioDemoUrl
- IDs tipo INTEGER con SERIAL para auto-incremento
- Join table usuario_roles con columnas id_usuario, id_tipo_usuario

Ver detalles en MIGRACION_POSTGRESQL_COMPLETADA.md

## Testing

```bash
# Ejecutar todos los tests (H2 en memoria, sin PostgreSQL)
mvn test -Pskip-frontend

# Tests específicos
mvn test -Pskip-frontend -Dtest=BeatServiceTest
mvn test -Pskip-frontend -Dtest=AdvancedFeaturesIntegrationTest

# Con cobertura
mvn clean test jacoco:report
```

Suite actual: **137 tests** (unitarios con Mockito + integración con MockMvc/H2), cubriendo
auth, beats, pedidos, pagos, búsqueda, favoritos, reseñas y notificaciones.

## Docker

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
docker build -t fullsound-backend .
docker run -p 8080:8080 --env-file .env fullsound-backend
```

## Notas Importantes

1. Roles en BD son strings: "cliente" y "administrador" (no ROLE_*)
2. IDs usan Integer (INT en BD), no Long
3. Estados son VARCHAR: DISPONIBLE, VENDIDO, RESERVADO, INACTIVO para beats
4. Estados pedido: PENDIENTE, PROCESANDO, COMPLETADO, CANCELADO, REEMBOLSADO
5. Estados pago: PENDIENTE, PROCESANDO, COMPLETADO, FALLIDO, REEMBOLSADO

## Licencia

Copyright 2025 FULLSOUND. Todos los derechos reservados.
