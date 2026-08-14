<div align="center">

# 🎵 FullSound Backend API

API REST para el marketplace de beats musicales de la aplicación Android **FullSound**.

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Spring Boot 3.4.1](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/Auth-JWT-red.svg)](https://jwt.io/)
[![Stripe](https://img.shields.io/badge/Payments-Stripe-635BFF.svg)](https://stripe.com/)
[![Swagger](https://img.shields.io/badge/Docs-OpenAPI%203.0-85EA2D.svg)](https://swagger.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

## 📖 Descripción

**FullSound** es el backend Spring Boot que da servicio a la app Android
[FullSound-KOTLIN](https://github.com/VECTORG99/FullSound-KOTLIN). Expone una API
REST para la gestión del catálogo de beats musicales, autenticación de usuarios,
procesamiento de pedidos y pagos con Stripe.

El proyecto mantiene la nomenclatura de dominio en español (Usuario, Pedido,
Beat, Pago, etc.) y usa **PostgreSQL** como base de datos, con migraciones
gestionadas por **Flyway**.

## 🛠️ Stack Tecnológico

| Componente        | Tecnología                                   |
|-------------------|----------------------------------------------|
| Lenguaje          | Java 21                                      |
| Framework         | Spring Boot 3.4.1                            |
| Base de datos     | PostgreSQL 15+ (compatible con Supabase)     |
| Migraciones       | Flyway                                       |
| ORM               | Spring Data JPA / Hibernate                  |
| Seguridad         | Spring Security + JWT (jjwt)                 |
| Pagos             | Stripe                                       |
| Documentación API | springdoc-openapi (Swagger UI)               |
| Mapeo DTO         | MapStruct                                    |
| Build             | Maven                                        |

## 📁 Estructura del Proyecto

```
FULLSOUND-SPRINGBOOT/
└── BackEnd/
    └── Fullsound/                 # Proyecto Maven (Spring Boot)
        ├── src/
        │   ├── main/
        │   │   ├── java/Fullsound/Fullsound/
        │   │   │   ├── config/        # Configuración Spring
        │   │   │   ├── controller/    # Controladores REST
        │   │   │   ├── dto/           # Objetos de transferencia
        │   │   │   ├── enums/         # Enumeraciones
        │   │   │   ├── exception/     # Manejo de excepciones
        │   │   │   ├── mapper/        # Mappers MapStruct
        │   │   │   ├── model/         # Entidades JPA
        │   │   │   ├── repository/    # Repositorios Spring Data
        │   │   │   ├── security/      # Seguridad y JWT
        │   │   │   ├── service/       # Lógica de negocio
        │   │   │   └── validation/    # Validaciones
        │   │   └── resources/
        │   │       ├── application.properties
        │   │       ├── application-dev.properties
        │   │       ├── application-prod.properties
        │   │       ├── application-docker.properties
        │   │       └── db/migration/  # Migraciones Flyway
        │   └── test/
        ├── Dockerfile
        └── pom.xml
```

## ✅ Requisitos

- **Java 21** (JDK)
- **Maven 3.8+** (incluido vía `mvnw`)
- **PostgreSQL 15+** (local, Supabase o Docker)

## 🚀 Instalación y Ejecución Local

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/VECTORG99/FULLSOUND-SPRINGBOOT.git
   cd FULLSOUND-SPRINGBOOT/BackEnd/Fullsound
   ```

2. **Configurar variables de entorno** (crear un archivo `.env` o exportarlas):

   ```properties
   DATABASE_URL=jdbc:postgresql://localhost:5432/fullsound
   DB_USERNAME=postgres
   DB_PASSWORD=tu_password
   JWT_SECRET=tu_secret_de_512_bits_para_hs512
   STRIPE_API_KEY=sk_test_tu_stripe_key
   STRIPE_WEBHOOK_SECRET=whsec_tu_webhook_secret
   ```

3. **Crear la base de datos** (Flyway creará las tablas automáticamente al iniciar):

   ```sql
   CREATE DATABASE fullsound;
   ```

4. **Ejecutar la aplicación** (perfil `dev` por defecto):

   ```bash
   ./mvnw spring-boot:run
   ```

5. **Acceder a los servicios**:

   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - OpenAPI JSON: http://localhost:8080/api-docs

## 🐳 Docker

El proyecto incluye un `Dockerfile` multi-etapa y un perfil `docker`.

### Construir la imagen

```bash
cd BackEnd/Fullsound
docker build -t fullsound-backend .
```

### Ejecutar con un contenedor PostgreSQL

```bash
# Red
docker network create fullsound-net

# Base de datos PostgreSQL
docker run -d --name fullsound-db --network fullsound-net \
  -e POSTGRES_DB=fullsound \
  -e POSTGRES_USER=fullsound \
  -e POSTGRES_PASSWORD=fullsound_pass \
  -p 5432:5432 \
  postgres:15

# Backend
docker run -d --name fullsound-api --network fullsound-net \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/fullsound \
  -e SPRING_DATASOURCE_USERNAME=fullsound \
  -e SPRING_DATASOURCE_PASSWORD=fullsound_pass \
  -e JWT_SECRET=tu_secret_de_512_bits_para_hs512 \
  -p 8080:8080 \
  fullsound-backend
```

> **Nota:** En el perfil `docker` el host de la base de datos por defecto es `db`
> (nombre del servicio en docker-compose). Ajusta `SPRING_DATASOURCE_URL` según tu entorno.

### docker-compose (ejemplo)

```yaml
version: "3.8"
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: fullsound
      POSTGRES_USER: fullsound
      POSTGRES_PASSWORD: fullsound_pass
    ports:
      - "5432:5432"
    volumes:
      - fullsound-db:/var/lib/postgresql/data

  api:
    build: ./BackEnd/Fullsound
    depends_on:
      - db
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/fullsound
      SPRING_DATASOURCE_USERNAME: fullsound
      SPRING_DATASOURCE_PASSWORD: fullsound_pass
      JWT_SECRET: tu_secret_de_512_bits_para_hs512
    ports:
      - "8080:8080"

volumes:
  fullsound-db:
```

## 🔐 Autenticación

La API usa JWT (Bearer token). Ejemplo:

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario":"admin@fullsound.com","contraseña":"password123"}'

# Usar el token en peticiones protegidas
curl -H "Authorization: Bearer {token}" http://localhost:8080/api/beats
```

## 📚 Documentación de la API

La documentación interactiva está disponible vía **Swagger UI** cuando la
aplicación está en ejecución:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Especificación OpenAPI:** http://localhost:8080/api-docs

### Endpoints principales

| Método | Endpoint                  | Descripción                    | Auth        |
|--------|---------------------------|--------------------------------|-------------|
| POST   | `/api/auth/register`      | Registrar nuevo usuario        | No          |
| POST   | `/api/auth/login`         | Iniciar sesión                 | No          |
| GET    | `/api/beats`              | Listar beats disponibles       | No          |
| GET    | `/api/beats/{id}`         | Obtener beat por ID            | No          |
| GET    | `/api/beats/slug/{slug}`  | Obtener beat por slug          | No          |
| POST   | `/api/beats`              | Crear beat                     | Sí (admin)  |
| PUT    | `/api/beats/{id}`         | Actualizar beat                | Sí (admin)  |
| DELETE | `/api/beats/{id}`         | Eliminar beat                  | Sí (admin)  |
| POST   | `/api/pedidos`            | Crear pedido                   | Sí          |
| GET    | `/api/pedidos/mis-pedidos`| Listar mis pedidos             | Sí          |
| GET    | `/api/pedidos`            | Listar todos los pedidos       | Sí (admin)  |
| POST   | `/api/pagos/pedido/{id}`  | Crear intención de pago Stripe | Sí          |
| GET    | `/api/estadisticas/*`     | Estadísticas del dashboard     | Sí (admin)  |

## 🗄️ Migraciones de Base de Datos

Las migraciones se gestionan con **Flyway** y se encuentran en
`src/main/resources/db/migration/`. La migración inicial
`V1__initial_schema.sql` crea el esquema completo de PostgreSQL.

`spring.jpa.hibernate.ddl-auto=validate` en todos los perfiles: Hibernate solo
valida que el esquema coincida con las entidades, nunca lo modifica. La creación
y evolución del esquema es responsabilidad exclusiva de Flyway.

## 🔧 Perfiles de Spring

| Perfil    | Archivo                         | Uso                                   |
|-----------|---------------------------------|---------------------------------------|
| `dev`     | `application-dev.properties`    | Desarrollo local (logs DEBUG)         |
| `prod`    | `application-prod.properties`   | Producción (AWS / Supabase)           |
| `docker`  | `application-docker.properties` | Contenedores Docker                   |

Activar un perfil:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# o
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

## 📦 Compilar y Probar

```bash
# Compilar
./mvnw compile

# Ejecutar tests
./mvnw test

# Empaquetar (sin frontend)
./mvnw clean package -Pskip-frontend
```

## 📱 Proyecto Relacionado

- **[FullSound-KOTLIN](https://github.com/VECTORG99/FullSound-KOTLIN)** — Cliente
  Android (Kotlin) que consume esta API.

## 📚 Aviso educativo

> **Proyecto educativo** — Este repositorio forma parte de mi formación temprana
> en **DUOC UC** (Instituto Profesional). Las prácticas aquí reflejadas
> corresponden al momento de desarrollo y pueden no representar estándares
> actuales de la industria. Se conserva con fines de portafolio y aprendizaje.

## 📄 Licencia

El proyecto está bajo la licencia MIT — ver el archivo [LICENSE](LICENSE) para
más detalles.
