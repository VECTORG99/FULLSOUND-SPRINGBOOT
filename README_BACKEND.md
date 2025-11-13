# 🎵 FULLSOUND - Plataforma de Beats (Spring Boot Backend)

## 📋 Descripción

Backend REST API para la plataforma FULLSOUND, un marketplace de beats musicales desarrollado con Spring Boot y adaptado a una base de datos MySQL existente.

---

## 🏗️ Arquitectura

- **Framework:** Spring Boot 3.2.0
- **Java:** 17
- **Base de Datos:** MySQL 8.0+ (`Fullsound_Base`)
- **Autenticación:** JWT (JSON Web Tokens)
- **Pagos:** Stripe API
- **Documentación API:** SpringDoc OpenAPI (Swagger)
- **Build Tool:** Maven

---

## 🗄️ Estructura de Base de Datos

### Tablas Principales:

| Tabla | Descripción | Registros Actuales |
|-------|-------------|-------------------|
| `tipo_usuario` | Roles (cliente, administrador) | 2 |
| `usuario` | Usuarios del sistema | 12 |
| `beat` | Beats musicales | 9 |
| `compra` | Pedidos/Compras | 5 |
| `compra_detalle` | Líneas de pedido | 5 |
| `pago` | Pagos (Stripe) | 5 |
| `usuario_roles` | Relación usuario-rol | 12 |

---

## 🚀 Inicio Rápido

### 1. Requisitos Previos

```bash
# Java 17
java -version

# Maven 3.8+
mvn -version

# MySQL 8.0+
mysql --version
```

### 2. Configurar Base de Datos

#### Opción A: Usar BD Existente (Recomendado)
```bash
# Ejecutar script de mejoras
cd plan
mysql -u root -p < DATABASE_MIGRATION.sql
```

#### Opción B: Crear BD desde Cero
```bash
mysql -u root -p
CREATE DATABASE Fullsound_Base;
USE Fullsound_Base;
source plan/DATABASE_MIGRATION.sql;
```

### 3. Configurar Application Properties

Editar `Fullsound/src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/Fullsound_Base
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_AQUI

# JWT Secret (cambiar en producción)
jwt.secret=MySecretKeyForJWTTokenGenerationShouldBeLongEnoughForHS512Algorithm

# Stripe (obtener en https://dashboard.stripe.com/test/apikeys)
stripe.api.key=sk_test_YOUR_STRIPE_KEY_HERE
```

### 4. Compilar y Ejecutar

```bash
cd Fullsound
mvn clean install
mvn spring-boot:run
```

### 5. Verificar

```bash
# Health check
curl http://localhost:8080/actuator/health

# Swagger UI
open http://localhost:8080/swagger-ui.html
```

---

## 📁 Estructura del Proyecto

```
Fullsound/
├── src/main/java/Fullsound/Fullsound/
│   ├── config/          # Configuración (JPA, Security, CORS)
│   ├── controller/      # REST Controllers (6)
│   ├── service/         # Lógica de negocio (6)
│   ├── repository/      # Acceso a datos (6)
│   ├── model/
│   │   ├── entity/      # Entidades JPA (6)
│   │   ├── enums/       # Enumeraciones (5)
│   │   └── dto/         # DTOs Request/Response
│   ├── mapper/          # MapStruct Mappers (3)
│   ├── security/        # JWT + Spring Security
│   └── exception/       # Manejo de excepciones
├── src/main/resources/
│   ├── application.properties
│   └── static/          # Archivos estáticos
└── pom.xml
```

---

## 🔐 Autenticación

### Registrar Usuario

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevo_usuario",
    "email": "usuario@example.com",
    "password": "password123",
    "nombreCompleto": "Usuario Nuevo"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan123",
    "password": "hash1"
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "usuario": {
      "id": 1,
      "username": "juan123",
      "email": "juan@example.com",
      "roles": ["cliente"]
    }
  }
}
```

### Usar Token

```bash
curl http://localhost:8080/api/beats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 📌 Endpoints Principales

### Auth (`/api/auth`)
| Método | Endpoint | Descripción | Público |
|--------|----------|-------------|---------|
| POST | `/register` | Registrar usuario | ✅ |
| POST | `/login` | Iniciar sesión | ✅ |
| POST | `/forgot-password` | Recuperar contraseña | ✅ |
| POST | `/reset-password` | Resetear contraseña | ✅ |
| GET | `/check-username/{username}` | Verificar disponibilidad | ✅ |
| GET | `/check-email/{email}` | Verificar disponibilidad | ✅ |

### Beats (`/api/beats`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/` | Listar beats (paginado) | ✅ |
| GET | `/{id}` | Obtener beat por ID | ✅ |
| GET | `/slug/{slug}` | Obtener beat por slug | ✅ |
| GET | `/genero/{genero}` | Filtrar por género | ✅ |
| GET | `/destacados` | Beats destacados | ✅ |
| POST | `/` | Crear beat | 🔒 Auth |
| PUT | `/{id}` | Actualizar beat | 🔒 Propietario |
| DELETE | `/{id}` | Eliminar beat | 🔒 Propietario |
| POST | `/{id}/like` | Dar like | 🔒 Auth |
| POST | `/{id}/reproducir` | Incrementar reproducciones | ✅ |
| GET | `/mis-beats` | Beats del usuario | 🔒 Auth |

### Usuarios (`/api/usuarios`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/perfil` | Perfil actual | 🔒 Auth |
| PUT | `/perfil` | Actualizar perfil | 🔒 Auth |
| POST | `/cambiar-password` | Cambiar contraseña | 🔒 Auth |
| GET | `/{id}` | Obtener usuario | 🔒 Admin |
| GET | `/` | Listar usuarios | 🔒 Admin |

### Pedidos (`/api/pedidos`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/mis-pedidos` | Pedidos del usuario | 🔒 Auth |
| GET | `/{id}` | Detalle de pedido | 🔒 Auth |
| GET | `/numero/{numero}` | Buscar por número | 🔒 Auth |
| GET | `/` | Listar todos | 🔒 Admin |

### Pagos (`/api/pagos`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/create-payment-intent` | Crear intento de pago | 🔒 Auth |
| POST | `/confirm` | Confirmar pago | 🔒 Auth |
| POST | `/webhook` | Webhook de Stripe | ✅ |
| GET | `/{id}` | Detalle de pago | 🔒 Auth |

### Estadísticas (`/api/estadisticas`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/dashboard` | Dashboard general | 🔒 Admin |
| GET | `/beats/top` | Beats más vendidos | 🔒 Admin |
| GET | `/ventas` | Reporte de ventas | 🔒 Admin |

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=BeatServiceTest
mvn test -Dtest=AuthControllerTest

# Con cobertura
mvn clean test jacoco:report
```

### Tipos de Tests

1. **Unit Tests:** Servicios con Mockito
2. **Integration Tests:** @SpringBootTest con BD H2
3. **Controller Tests:** @WebMvcTest con MockMvc
4. **Repository Tests:** @DataJpaTest

---

## 📦 Dependencias Principales

```xml
<!-- Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Spring Security + JWT -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<!-- MySQL -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>

<!-- Stripe -->
<dependency>
    <groupId>com.stripe</groupId>
    <artifactId>stripe-java</artifactId>
    <version>24.3.0</version>
</dependency>

<!-- MapStruct -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>

<!-- Swagger/OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

---

## 🔧 Configuración de Producción

### application-prod.properties

```properties
# Database
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# JWT
jwt.secret=${JWT_SECRET}

# Stripe
stripe.api.key=${STRIPE_LIVE_KEY}

# Logging
logging.level.root=WARN
logging.level.Fullsound.Fullsound=INFO
```

### Variables de Entorno

```bash
export DATABASE_URL=jdbc:mysql://production-host:3306/Fullsound_Base
export DB_USERNAME=fullsound_user
export DB_PASSWORD=secure_password
export JWT_SECRET=production_secret_256_bits
export STRIPE_LIVE_KEY=sk_live_your_live_key
```

---

## 🐳 Docker

### Dockerfile

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
version: '3.8'
services:
  backend:
    build: ./Fullsound
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/Fullsound_Base
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=rootpassword
    depends_on:
      - mysql
  
  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=rootpassword
      - MYSQL_DATABASE=Fullsound_Base
    ports:
      - "3306:3306"
```

---

## 📚 Documentación Adicional

- **Plan de Implementación:** Ver `plan/00_IMPLEMENTACION_FINAL.md`
- **Mapeo de BD:** Ver `plan/15_MAPEO_BASE_DATOS.md`
- **Script de Migración:** Ver `plan/DATABASE_MIGRATION.sql`
- **Entidades JPA:** Ver `plan/03_ENTIDADES_JPA.md`
- **Enumeraciones:** Ver `plan/02_ENUMERACIONES.md`

---

## 🤝 Contribución

### Branching Strategy

- `main` - Producción
- `develop` - Desarrollo
- `feature/*` - Nuevas características
- `hotfix/*` - Correcciones urgentes

### Commit Messages

```
feat: Agregar endpoint de estadísticas
fix: Corregir validación de email
docs: Actualizar README
refactor: Mejorar servicio de pagos
test: Agregar tests de BeatService
```

---

## 📝 Licencia

Copyright © 2025 FULLSOUND. Todos los derechos reservados.

---

## 📞 Contacto

- **Desarrollador:** VECTORG99
- **Email:** fullsound@example.com
- **GitHub:** https://github.com/VECTORG99/FULLSOUND-SPRINGBOOT

---

## ⚠️ Notas Importantes

1. **Base de Datos:** El proyecto está adaptado a una BD MySQL existente (`Fullsound_Base`)
2. **Roles:** Los roles en BD son strings: `"cliente"` y `"administrador"` (no `ROLE_*`)
3. **IDs:** Las entidades usan `Integer` (INT en MySQL), no `Long`
4. **Campos Calculados:** `precio_formateado` y `enlace_producto` se calculan en runtime con `@Transient`
5. **Entidades No Implementadas:** Producto, Carrito, Review (no existen en BD actual)

---

## 🎯 Roadmap

- [ ] ✅ Adaptación a BD existente
- [ ] ✅ Implementación de entidades JPA
- [ ] ✅ Configuración de seguridad JWT
- [ ] ✅ Integración con Stripe
- [ ] 🔄 Tests unitarios e integración
- [ ] 🔄 Documentación Swagger completa
- [ ] 📋 Sistema de notificaciones
- [ ] 📋 Carrito de compras (requiere nueva tabla)
- [ ] 📋 Sistema de reviews (requiere nueva tabla)
- [ ] 📋 Productos adicionales (requiere nueva tabla)
- [ ] 📋 CI/CD con GitHub Actions
- [ ] 📋 Deploy en AWS/Heroku

---

**¡Gracias por usar FULLSOUND!** 🎵
