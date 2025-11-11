# 🚀 DESPLIEGUE Y CONFIGURACIÓN - FULLSOUND

## 🎯 Objetivo

Configurar el proyecto para desarrollo local y despliegue en producción.

---

## 🛠️ Configuración de Desarrollo

### 1. Variables de Entorno

#### Backend - application-dev.properties
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/fullsound_db
spring.datasource.username=root
spring.datasource.password=root

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT
jwt.secret=dev_secret_key_fullsound_2025
jwt.expiration=86400000

# File Upload
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# CORS
cors.allowed-origins=http://localhost:5173,http://localhost:8080

# Logging
logging.level.com.fullsound=DEBUG
logging.level.org.springframework.web=DEBUG
```

#### Frontend - .env.development
```bash
VITE_API_URL=http://localhost:8080
VITE_ENV=development
```

---

## 🏭 Configuración de Producción

### Backend - application-prod.properties
```properties
# Server
server.port=${PORT:8080}

# Database
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}

# CORS
cors.allowed-origins=${ALLOWED_ORIGINS}

# Logging
logging.level.com.fullsound=INFO
logging.level.root=WARN

# Cache
spring.web.resources.cache.cachecontrol.max-age=31536000
spring.web.resources.cache.cachecontrol.cache-public=true

# Compression
server.compression.enabled=true
server.compression.mime-types=text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json
```

#### Frontend - .env.production
```bash
VITE_API_URL=
VITE_ENV=production
```

---

## 🗄️ Base de Datos

### Script de Creación (MySQL)

```sql
-- schema.sql
CREATE DATABASE IF NOT EXISTS fullsound_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE fullsound_db;

-- Las tablas se crean automáticamente con JPA
-- pero puedes definirlas explícitamente:

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso DATETIME
);

CREATE TABLE IF NOT EXISTS beats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    artista VARCHAR(100) NOT NULL,
    genero VARCHAR(50) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    descripcion TEXT,
    ruta_audio VARCHAR(500),
    ruta_imagen VARCHAR(500),
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS carritos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS carrito_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    carrito_id BIGINT NOT NULL,
    beat_id BIGINT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    fecha_agregado DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (carrito_id) REFERENCES carritos(id) ON DELETE CASCADE,
    FOREIGN KEY (beat_id) REFERENCES beats(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_usuario_correo ON usuarios(correo);
CREATE INDEX idx_beat_genero ON beats(genero);
CREATE INDEX idx_carrito_usuario ON carritos(usuario_id);
```

### Datos de Prueba

```sql
-- data.sql
-- Usuario admin
INSERT INTO usuarios (nombre, correo, password, rol, fecha_registro)
VALUES ('Admin', 'admin@admin.cl', 
        '$2a$10$xxxxxxxxxxxxxxxxxxx',  -- bcrypt hash de 'admin123'
        'ADMIN', NOW());

-- Beats de ejemplo
INSERT INTO beats (nombre, artista, genero, precio, descripcion)
VALUES 
('Trap Beat 1', 'Producer 1', 'Trap', 15000, 'Beat estilo trap moderno'),
('Reggaeton Beat', 'Producer 2', 'Reggaeton', 18000, 'Ritmo urbano latino'),
('Hip Hop Classic', 'Producer 3', 'Hip Hop', 20000, 'Beat clásico de hip hop');
```

---

## 📦 Build y Empaquetado

### Desarrollo
```bash
# Build completo
mvn clean install

# Solo backend (skip frontend)
mvn clean install -Pskip-frontend

# Ejecutar
mvn spring-boot:run
```

### Producción
```bash
# Build optimizado
mvn clean package -Pprod -DskipTests

# Generar JAR
# Resultado: target/fullsound-1.0.0-SNAPSHOT.jar
```

---

## 🐳 Docker

### Dockerfile
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -Pprod -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/fullsound-*.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: fullsound_db
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATABASE_URL: jdbc:mysql://mysql:3306/fullsound_db
      DB_USER: root
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      mysql:
        condition: service_healthy

volumes:
  mysql_data:
```

**Uso:**
```bash
docker-compose up -d
docker-compose logs -f app
docker-compose down
```

---

## ☁️ Despliegue en Servicios Cloud

### Heroku
```bash
# Login
heroku login

# Crear app
heroku create fullsound-app

# Agregar MySQL
heroku addons:create cleardb:ignite

# Variables de entorno
heroku config:set JWT_SECRET=your_secret_key

# Deploy
git push heroku main

# Ver logs
heroku logs --tail
```

### Railway
```bash
# railway.json
{
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "mvn clean package -Pprod -DskipTests"
  },
  "deploy": {
    "startCommand": "java -jar target/fullsound-*.jar",
    "healthcheckPath": "/api/beats",
    "healthcheckTimeout": 100
  }
}
```

---

## 🔐 Variables de Entorno Requeridas

### Desarrollo
```bash
# No requiere variables adicionales
# Usa valores por defecto de application-dev.properties
```

### Producción
```bash
# Obligatorias
DATABASE_URL=jdbc:mysql://host:port/database
DB_USER=username
DB_PASSWORD=password
JWT_SECRET=your-super-secret-key-change-in-production

# Opcionales
PORT=8080
JWT_EXPIRATION=86400000
ALLOWED_ORIGINS=https://yourdomain.com
```

---

## ✅ Checklist de Despliegue

### Pre-Despliegue
- [ ] Tests pasan (backend + frontend)
- [ ] Build de producción genera JAR
- [ ] Variables de entorno configuradas
- [ ] Base de datos creada
- [ ] Scripts SQL ejecutados

### Despliegue
- [ ] JAR ejecuta correctamente
- [ ] Frontend se sirve desde Spring Boot
- [ ] APIs responden
- [ ] Login funciona
- [ ] JWT se valida
- [ ] CORS configurado

### Post-Despliegue
- [ ] Verificar logs
- [ ] Probar funcionalidades críticas
- [ ] Monitorear performance
- [ ] Backup de base de datos

---

## 📊 Monitoreo

### Spring Boot Actuator (opcional)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
# application.properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

**Endpoints:**
- Health: `/actuator/health`
- Info: `/actuator/info`
- Metrics: `/actuator/metrics`

---

**Próximo Paso**: [13_CHECKLIST_FUNCIONALIDADES.md](13_CHECKLIST_FUNCIONALIDADES.md)
