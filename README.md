# FullSound

API REST para marketplace de beats musicales.

## Stack

- Java 21 + Spring Boot 3.5.7
- PostgreSQL 17 (Supabase)
- Spring Security + JWT
- Swagger/OpenAPI 3.0

## Requisitos

- Java 21+
- PostgreSQL 12+
- Maven 3.8+

## Instalación

```bash
git clone https://github.com/VECTORG99/FULLSOUND-SPRINGBOOT.git
cd FULLSOUND-SPRINGBOOT/BackEnd/Fullsound
```

Crea `.env`:
```properties
DB_PASSWORD=tu_password
```

Configura `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://tu-host:6543/postgres
spring.datasource.username=postgres.tu_project
spring.datasource.password=${DB_PASSWORD}
jwt.secret=tu_secret_256_bits
```

## Ejecutar

```bash
./mvnw spring-boot:run
```

Accede a:
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html

## Autenticación

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@fullsound.com","password":"password123"}'

# Usar token en requests
Authorization: Bearer {token}
```

## Endpoints

| Método | Endpoint | Auth |
|--------|----------|------|
| POST | `/api/auth/register` | No |
| POST | `/api/auth/login` | No |
| GET | `/api/beats` | No |
| POST | `/api/beats` | Sí |
| GET | `/api/pedidos` | Sí |
| POST | `/api/pedidos` | Sí |

## Usuarios de Prueba

- admin@fullsound.com / password123
- productor@fullsound.com / password123
- cliente@fullsound.com / password123