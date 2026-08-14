# Backend Completado - FullSound

Estado: 100% COMPLETADO

## Componentes Implementados

- Configuración base (pom.xml, application.properties)
- 5 Enumeraciones
- 6 Entidades JPA con repositories
- 6 DTOs Request + 7 DTOs Response
- 4 Mappers MapStruct
- Seguridad JWT completa
- 5 Servicios con implementaciones
- 6 Controladores REST
- Manejo global de excepciones
- Documentación Swagger/OpenAPI

## Pasos para Ejecutar

### 1. Configurar Base de Datos

PostgreSQL:
```sql
CREATE DATABASE fullsound;
-- Ejecutar schema (ver MIGRACION_POSTGRESQL_COMPLETADA.md)
```

### 2. Configurar application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fullsound
spring.datasource.username=postgres
spring.datasource.password=tu_password
jwt.secret=tu_secret_256_bits
```

### 3. Compilar y Ejecutar

```powershell
cd BackEnd\Fullsound
.\mvnw.cmd clean install -DskipTests
.\mvnw.cmd spring-boot:run
```

### 4. Verificar

- API: http://localhost:8080/actuator/health
- Swagger: http://localhost:8080/swagger-ui.html

## Endpoints

### Auth (Público)
- POST /api/auth/register
- POST /api/auth/login

### Beats
- GET /api/beats (listar)
- GET /api/beats/{id}
- GET /api/beats/slug/{slug}
- POST /api/beats/{id}/like (Auth)
- POST /api/beats (Admin)
- PUT /api/beats/{id} (Admin)
- DELETE /api/beats/{id} (Admin)

### Pedidos (Auth)
- POST /api/pedidos
- GET /api/pedidos/mis-pedidos
- GET /api/pedidos/{id}

### Pagos (Auth)
- POST /api/pagos/create-intent
- POST /api/pagos/confirm
- GET /api/pagos/{id}

### Usuarios
- GET /api/usuarios/me (Auth)
- PUT /api/usuarios/me (Auth)
- GET /api/usuarios (Admin)

### Estadísticas (Admin)
- GET /api/estadisticas/dashboard

## Autenticación JWT

1. Registrarse: POST /api/auth/register
2. Login: POST /api/auth/login (obtener token)
3. Usar token: Header `Authorization: Bearer {token}`

Ejemplo:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"usuario1","password":"password123"}'
```

## Estructura

```
src/main/java/Fullsound/Fullsound/
├── config/          # Security, CORS, Swagger
├── controller/      # REST (6 controladores)
├── dto/             # Request/Response DTOs
├── exception/       # Manejo de excepciones
├── mapper/          # MapStruct (4)
├── model/           # Entities JPA (6)
├── repository/      # Spring Data (6)
├── security/        # JWT
└── service/         # Lógica de negocio (5)
```

## Características

- Autenticación JWT con Spring Security
- CRUD completo de Beats con búsqueda y filtros
- Gestión de pedidos
- Integración con Stripe
- Validación de DTOs con Jakarta Validation
- Manejo global de excepciones
- CORS configurado
- Documentación OpenAPI/Swagger

## Testing

```bash
./mvnw test                    # Todos los tests
./mvnw clean test jacoco:report  # Con cobertura
```

## Solución de Problemas

Port 8080 en uso:
```properties
server.port=8081
```

Error de compilación:
```bash
./mvnw clean install -DskipTests
```

Copyright 2025 FULLSOUND. Todos los derechos reservados.
