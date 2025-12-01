# FullSound Spring Boot

Marketplace de beats musicales con backend en Spring Boot y frontend en React.

## Documentación

Toda la documentación del proyecto está centralizada en la carpeta `docs`.

Consulta los siguientes archivos según lo que necesites:

### Inicio Rápido
- [QUICK_START.md](docs/QUICK_START.md) - Guía rápida para iniciar el proyecto
- [QUICK_START_IMPLEMENTACION.md](docs/QUICK_START_IMPLEMENTACION.md) - Pasos esenciales de implementación

### Documentación Técnica
- [README_BACKEND.md](docs/README_BACKEND.md) - Documentación completa del backend y endpoints
- [CONFIGURACION_AMBIENTE.md](docs/CONFIGURACION_AMBIENTE.md) - Instrucciones para configurar el entorno
- [CHECKLIST_FINAL.md](docs/CHECKLIST_FINAL.md) - Checklist de implementación

### Estado del Proyecto
- [STATUS_PROYECTO.md](docs/STATUS_PROYECTO.md) - Estado actual y próximos pasos
- [ESTADO_PROYECTO.md](docs/ESTADO_PROYECTO.md) - Resumen del progreso
- [RESUMEN_FINAL.md](docs/RESUMEN_FINAL.md) - Resumen ejecutivo
- [BACKEND_COMPLETADO.md](docs/BACKEND_COMPLETADO.md) - Detalles de la implementación completada
- [ESTRUCTURA_VISUAL.md](docs/ESTRUCTURA_VISUAL.md) - Estructura visual del proyecto

### Docker
- [DOCKER_QUICK_START.md](docs/DOCKER_QUICK_START.md) - Inicio rápido con Docker
- [DOCKER_RESUMEN.md](docs/DOCKER_RESUMEN.md) - Resumen de comandos Docker
- [DOCKER_SETUP.md](docs/DOCKER_SETUP.md) - Configuración detallada de Docker

### Índice Completo
Para ver el índice y descripción de cada archivo, revisa [docs/README.md](docs/README.md).

## Estructura del Proyecto

```
FULLSOUND-SPRINGBOOT/
├── Fullsound/          # Backend Spring Boot
├── frontend/           # Frontend React
├── docs/               # Toda la documentación
├── docker/             # Configuraciones Docker
└── docker-compose.yml  # Orquestación de servicios
```

# 🎵 FULLSOUND - Backend API REST

API REST para plataforma de compra y venta de beats musicales.

## 🚀 Stack Tecnológico

- **Java 21** - LTS
- **Spring Boot 3.5.7** - Framework principal
- **PostgreSQL 17** - Base de datos (Supabase)
- **Spring Security + JWT** - Autenticación
- **Spring Data JPA** - ORM
- **Swagger/OpenAPI 3.0** - Documentación API
- **MapStruct** - Mapeo de objetos
- **Maven** - Gestión de dependencias

---

## 📋 Requisitos

- Java JDK 21+
- PostgreSQL 12+ (o cuenta Supabase)
- Maven 3.8+ (o usar wrapper incluido)

---

## ⚙️ Configuración

### 1. Clonar repositorio
```bash
git clone https://github.com/tu-usuario/FULLSOUND-SPRINGBOOT.git
cd FULLSOUND-SPRINGBOOT/BackEnd/Fullsound
```

### 2. Configurar base de datos

Crea un archivo `.env` en `BackEnd/Fullsound/.env`:

```properties
DB_PASSWORD=tu_password_supabase
```

### 3. Configurar `application.properties`

Edita `src/main/resources/application.properties`:

```properties
# PostgreSQL/Supabase
spring.datasource.url=jdbc:postgresql://aws-0-us-west-2.pooler.supabase.com:6543/postgres
spring.datasource.username=postgres.tu_project_ref
spring.datasource.password=${DB_PASSWORD}

# JWT
jwt.secret=tu_secret_key_seguro_minimo_256_bits
jwt.expiration=86400000
```

### 4. Ejecutar script SQL

Ejecuta el script `database/fullsound-schema.sql` en tu base de datos Supabase para crear las tablas.

---

## 🏃 Ejecutar

### Con Maven Wrapper (recomendado)
```bash
./mvnw spring-boot:run
```

### Con Maven instalado
```bash
mvn spring-boot:run
```

### Con Java directamente
```bash
mvn clean package
java -jar target/fullsound-frontend-2.0.0.jar
```

---

## 📚 Documentación API

Una vez iniciada la aplicación, accede a:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health

---

## 🔐 Autenticación

La API usa **JWT (JSON Web Tokens)**. Para acceder a endpoints protegidos:

1. **Registrarse**: `POST /api/auth/register`
2. **Login**: `POST /api/auth/login` → Obtienes `token`
3. **Usar token**: Agrega header `Authorization: Bearer {token}`

### Ejemplo con curl:
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@fullsound.com","password":"password123"}'

# Usar token
curl http://localhost:8080/api/beats \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 📁 Estructura del Proyecto

```
BackEnd/Fullsound/
├── src/main/java/Fullsound/Fullsound/
│   ├── config/           # Configuraciones (Security, CORS, Swagger)
│   ├── controller/       # Controladores REST
│   ├── dto/             # DTOs (Request/Response)
│   ├── exception/       # Manejo de excepciones
│   ├── mapper/          # MapStruct mappers
│   ├── model/           # Entidades JPA
│   ├── repository/      # Repositorios Spring Data
│   ├── security/        # JWT, Filtros, UserDetails
│   └── service/         # Lógica de negocio
└── src/main/resources/
    ├── application.properties
    └── application-docker.properties
```

---

## 🐳 Docker (Opcional)

```bash
# Build
docker build -t fullsound-backend .

# Run
docker run -p 8080:8080 --env-file .env fullsound-backend
```

---

## 🧪 Testing

```bash
# Ejecutar tests
./mvnw test

# Con cobertura
./mvnw clean verify
```

---

## 📊 Endpoints Principales

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Registrar usuario | ❌ |
| POST | `/api/auth/login` | Iniciar sesión | ❌ |
| GET | `/api/beats` | Listar beats | ❌ |
| GET | `/api/beats/{id}` | Obtener beat | ❌ |
| POST | `/api/beats` | Crear beat | ✅ |
| PUT | `/api/beats/{id}` | Actualizar beat | ✅ |
| DELETE | `/api/beats/{id}` | Eliminar beat | ✅ |
| GET | `/api/pedidos` | Mis pedidos | ✅ |
| POST | `/api/pedidos` | Crear pedido | ✅ |

---

## 👥 Usuarios de Prueba

| Usuario | Correo | Password | Rol |
|---------|--------|----------|-----|
| admin | admin@fullsound.com | password123 | Administrador |
| productor1 | productor@fullsound.com | password123 | Productor |
| cliente1 | cliente@fullsound.com | password123 | Cliente |

---

## 🔧 Configuración CORS

CORS está habilitado para desarrollo local. Para producción, actualiza `WebConfig.java`:

```java
.allowedOrigins("https://tu-frontend.com")
```

---

## 📝 Variables de Entorno

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_PASSWORD` | Contraseña PostgreSQL | `tu_password` |
| `JWT_SECRET` | Clave secreta JWT | `min_256_bits_key` |
| `SPRING_PROFILES_ACTIVE` | Perfil activo | `dev` / `prod` |

---

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama: `git checkout -b feature/nueva-funcionalidad`
3. Commit: `git commit -m 'Add: nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto está bajo licencia MIT. Ver archivo `LICENSE` para más detalles.

---

## 📞 Contacto

- **Autor**: VECTORG99
- **Email**: contacto@fullsound.com
- **GitHub**: [@vectorg99](https://github.com/vectorg99)

---

## 🔄 Changelog

### v2.0.0 (2025-11-30)
- ✅ Migración completa a PostgreSQL
- ✅ Integración con Supabase
- ✅ Documentación Swagger completa
- ✅ Arquitectura REST API pura
- ✅ Separación frontend/backend

---

**Made with ❤️ by VECTORG99**