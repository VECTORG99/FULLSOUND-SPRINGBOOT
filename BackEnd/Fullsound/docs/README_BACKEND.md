# FullSound Backend - Spring Boot

Backend REST API para marketplace de beats musicales.

## Stack Tecnológico

- Java 17
- Spring Boot 3.2.0
- PostgreSQL (migrado desde MySQL)
- Spring Security + JWT
- Swagger/OpenAPI
- MapStruct
- Maven

## Requisitos

```bash
java -version    # Java 17+
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
- GET `/` - Listar beats (paginado)
- GET `/{id}` - Obtener beat
- GET `/slug/{slug}` - Obtener por slug
- GET `/genero/{genero}` - Filtrar por género
- GET `/destacados` - Beats destacados
- POST `/` - Crear beat (Auth)
- PUT `/{id}` - Actualizar beat (Propietario)
- DELETE `/{id}` - Eliminar beat (Propietario)
- POST `/{id}/like` - Dar like (Auth)
- POST `/{id}/reproducir` - Incrementar reproducciones

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
- GET `/perfil` - Perfil actual (Auth)
- PUT `/perfil` - Actualizar perfil (Auth)
- POST `/cambiar-password` - Cambiar contraseña (Auth)
- GET `/{id}` - Obtener usuario (Admin)
- GET `/` - Listar usuarios (Admin)

### Estadísticas - `/api/estadisticas` (Admin)
- GET `/dashboard` - Dashboard general
- GET `/beats/top` - Beats más vendidos
- GET `/ventas` - Reporte de ventas

## Estructura del Proyecto

```
src/main/java/Fullsound/Fullsound/
├── config/          # Configuración (Security, CORS, Swagger)
├── controller/      # REST Controllers (6)
├── dto/             # DTOs Request/Response
├── exception/       # Manejo de excepciones
├── mapper/          # MapStruct mappers
├── model/           # Entidades JPA (6)
├── repository/      # Repositorios Spring Data (6)
├── security/        # JWT + Spring Security
└── service/         # Lógica de negocio (6)
```

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
# Ejecutar tests
./mvnw test

# Tests específicos
./mvnw test -Dtest=BeatServiceTest

# Con cobertura
./mvnw clean test jacoco:report
```

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
