<div align="center">

# 🎵 FullSound

API REST para marketplace de beats musicales.

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Spring Boot 3.5.7](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/Auth-JWT-red.svg)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203.0-85EA2D.svg)](https://swagger.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

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
JWT_SECRET=tu_secret_de_512_bits
SUPABASE_KEY=tu_supabase_anon_key
```

Configura `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://tu-host:6543/postgres
spring.datasource.username=postgres.tu_project
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
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
| POST | `/api/beats` | Sí (admin) |
| GET | `/api/pedidos` | Sí (admin) |
| POST | `/api/pedidos` | Sí |

## Usuarios de Prueba

- admin@fullsound.com / password123
- productor@fullsound.com / password123
- cliente@fullsound.com / password123

## Estructura del Proyecto

```
FULLSOUND-SPRINGBOOT/
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.md
│   │   └── feature_request.md
│   └── workflows/
│       ├── deploy-backend-aws.yml
│       └── test.yml
├── BackEnd/
│   └── Fullsound/
│       ├── .mvn/
│       ├── docs/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/Fullsound/Fullsound/
│       │   │   │   ├── config/          # Configuración Spring
│       │   │   │   ├── controller/      # Controladores REST
│       │   │   │   ├── dto/             # Objetos de transferencia
│       │   │   │   │   ├── request/
│       │   │   │   │   └── response/
│       │   │   │   ├── enums/           # Enumeraciones
│       │   │   │   ├── exception/       # Manejo de excepciones
│       │   │   │   ├── mapper/          # MapStruct mappers
│       │   │   │   ├── model/           # Entidades JPA
│       │   │   │   ├── repository/      # Repositorios Spring Data
│       │   │   │   ├── security/        # Seguridad y JWT
│       │   │   │   ├── service/         # Lógica de negocio
│       │   │   │   │   └── impl/
│       │   │   │   └── validation/      # Validaciones
│       │   │   └── resources/
│       │   │       ├── application.properties
│       │   │       ├── application-production.properties
│       │   │       ├── application-prod.properties
│       │   │       └── application-docker.properties
│       │   └── test/
│       └── pom.xml
├── scripts/
│   ├── deploy-ec2.sh    # Despliegue en instancia EC2
│   └── setup-ec2.sh     # Configuración inicial del servidor EC2
├── dev.ps1
├── LICENSE
├── PLAN_MIGRACION_BACKEND_KOTLIN.md
└── README.md
```

## Despliegue

El proyecto incluye scripts para desplegar en una instancia **AWS EC2**:

- **`scripts/setup-ec2.sh`** — Configura el entorno del servidor (Java, dependencias y estructura de directorios).
- **`scripts/deploy-ec2.sh`** — Compila, empaqueta y despliega la aplicación en la instancia EC2.

El flujo de CI/CD de GitHub Actions (`.github/workflows/deploy-backend-aws.yml`) automatiza el despliegue a AWS, mientras que `test.yml` ejecuta los tests en cada push/PR.

## Proyectos Relacionados

- **[FullSound-KOTLIN](https://github.com/VECTORG99/FullSound-KOTLIN)** — Cliente Android (Kotlin) que consume esta API.

## Licencia

Este proyecto está bajo la licencia MIT — ver el archivo [LICENSE](LICENSE) para más detalles.
