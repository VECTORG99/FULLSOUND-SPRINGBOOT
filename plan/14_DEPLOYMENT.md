# 🚀 PASO 68-71: Docker y Deployment

## 🎯 Objetivo
Configurar Docker, Docker Compose y preparar la aplicación para deployment en producción.

---

## ✅ PASO 68: Dockerfile para Spring Boot

**Archivo:** `Fullsound/Dockerfile`

```dockerfile
# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear usuario no-root
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# Copiar JAR desde etapa de build
COPY --from=build /app/target/*.jar app.jar

# Crear directorios para uploads
RUN mkdir -p /app/uploads/beats /app/uploads/images && \
    chown -R appuser:appuser /app

USER appuser

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Ejecutar aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

---

## ✅ PASO 69: Docker Compose

**Archivo:** `docker-compose.yml`

```yaml
version: '3.8'

services:
  # MySQL Database
  mysql:
    image: mysql:8.0
    container_name: fullsound-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: fullsound
      MYSQL_USER: fullsound_user
      MYSQL_PASSWORD: fullsound_pass
    ports:
      - "3307:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init-db.sql:/docker-entrypoint-initdb.d/init-db.sql
    networks:
      - fullsound-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Spring Boot Backend
  backend:
    build:
      context: ./Fullsound
      dockerfile: Dockerfile
    container_name: fullsound-backend
    restart: unless-stopped
    depends_on:
      mysql:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/fullsound?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: fullsound_user
      SPRING_DATASOURCE_PASSWORD: fullsound_pass
      JWT_SECRET: ${JWT_SECRET:-MySecretKeyForJWTTokenGenerationShouldBeLongEnoughForHS512Algorithm}
      STRIPE_API_KEY: ${STRIPE_API_KEY}
      JAVA_OPTS: "-Xmx1g -Xms512m"
    ports:
      - "8080:8080"
    volumes:
      - backend_uploads:/app/uploads
    networks:
      - fullsound-network
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  # React Frontend (Vite)
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: fullsound-frontend
    restart: unless-stopped
    depends_on:
      - backend
    environment:
      VITE_API_URL: http://localhost:8080/api
    ports:
      - "5173:80"
    networks:
      - fullsound-network

  # Nginx Reverse Proxy (Opcional)
  nginx:
    image: nginx:alpine
    container_name: fullsound-nginx
    restart: unless-stopped
    depends_on:
      - backend
      - frontend
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
    networks:
      - fullsound-network

volumes:
  mysql_data:
    driver: local
  backend_uploads:
    driver: local

networks:
  fullsound-network:
    driver: bridge
```

---

## ✅ PASO 70: Dockerfile para Frontend (React + Vite)

**Archivo:** `frontend/Dockerfile`

```dockerfile
# Etapa 1: Build
FROM node:20-alpine AS build

WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias
RUN npm ci

# Copiar código fuente
COPY . .

# Build de producción
ARG VITE_API_URL=http://localhost:8080/api
ENV VITE_API_URL=$VITE_API_URL

RUN npm run build

# Etapa 2: Nginx para servir archivos estáticos
FROM nginx:alpine

# Copiar archivos build
COPY --from=build /app/dist /usr/share/nginx/html

# Copiar configuración de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer puerto
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s CMD wget --quiet --tries=1 --spider http://localhost/health || exit 1

# Comando de inicio
CMD ["nginx", "-g", "daemon off;"]
```

**Archivo:** `frontend/nginx.conf`

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Serve static files
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Health check endpoint
    location /health {
        access_log off;
        return 200 "OK\n";
        add_header Content-Type text/plain;
    }
}
```

---

## ✅ PASO 71: Configuraciones de Producción

### 71.1 - application-prod.properties

**Archivo:** `Fullsound/src/main/resources/application-prod.properties`

```properties
# Server Configuration
server.port=8080
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain

# Database
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/fullsound}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:fullsound_user}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:fullsound_pass}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# HikariCP Settings
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Stripe
stripe.api.key=${STRIPE_API_KEY}

# File Upload
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
file.upload.dir=/app/uploads

# Logging
logging.level.root=INFO
logging.level.Fullsound.Fullsound=INFO
logging.level.org.springframework.web=WARN
logging.level.org.hibernate.SQL=WARN
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.file.name=/app/logs/fullsound.log
logging.file.max-size=10MB
logging.file.max-history=30

# Actuator
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true

# CORS (Ajustar según dominio de producción)
cors.allowed.origins=https://fullsound.com,https://www.fullsound.com

# Security
spring.security.user.name=admin
spring.security.user.password=${ADMIN_PASSWORD:changeme}
```

---

### 71.2 - Nginx Reverse Proxy

**Archivo:** `nginx/nginx.conf`

```nginx
events {
    worker_connections 1024;
}

http {
    upstream backend {
        server backend:8080;
    }

    upstream frontend {
        server frontend:80;
    }

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req_zone $binary_remote_addr zone=login:10m rate=5r/m;

    server {
        listen 80;
        server_name fullsound.com www.fullsound.com;

        # Redirect to HTTPS (en producción)
        # return 301 https://$server_name$request_uri;

        client_max_body_size 50M;

        # Frontend
        location / {
            proxy_pass http://frontend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Backend API
        location /api/ {
            limit_req zone=api burst=20 nodelay;

            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;

            # CORS headers
            add_header 'Access-Control-Allow-Origin' '*' always;
            add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
            add_header 'Access-Control-Allow-Headers' 'Authorization, Content-Type' always;

            if ($request_method = 'OPTIONS') {
                return 204;
            }
        }

        # Login endpoint con rate limiting más estricto
        location /api/auth/login {
            limit_req zone=login burst=5 nodelay;
            proxy_pass http://backend;
        }

        # Uploads (servir archivos estáticos)
        location /uploads/ {
            alias /app/uploads/;
            expires 1y;
            add_header Cache-Control "public, immutable";
        }

        # Health checks
        location /health {
            access_log off;
            return 200 "OK\n";
            add_header Content-Type text/plain;
        }
    }

    # HTTPS Configuration (descomentar en producción)
    # server {
    #     listen 443 ssl http2;
    #     server_name fullsound.com www.fullsound.com;
    #
    #     ssl_certificate /etc/nginx/ssl/fullsound.crt;
    #     ssl_certificate_key /etc/nginx/ssl/fullsound.key;
    #     ssl_protocols TLSv1.2 TLSv1.3;
    #     ssl_ciphers HIGH:!aNULL:!MD5;
    #
    #     # ... resto de configuración igual
    # }
}
```

---

### 71.3 - .env Template

**Archivo:** `.env.example`

```bash
# Database
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=fullsound
MYSQL_USER=fullsound_user
MYSQL_PASSWORD=fullsound_pass

# Spring Boot
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/fullsound

# JWT (Generar secret seguro en producción)
JWT_SECRET=MySecretKeyForJWTTokenGenerationShouldBeLongEnoughForHS512Algorithm

# Stripe
STRIPE_API_KEY=sk_test_your_stripe_key_here

# Admin
ADMIN_PASSWORD=secure_admin_password

# Frontend
VITE_API_URL=http://localhost:8080/api
```

---

## 🚀 Comandos de Deployment

### Desarrollo Local

```bash
# Construir y levantar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f backend

# Reconstruir después de cambios
docker-compose up -d --build

# Detener servicios
docker-compose down

# Eliminar volúmenes (resetear BD)
docker-compose down -v
```

### Producción

```bash
# 1. Clonar repositorio
git clone https://github.com/VECTORG99/FULLSOUND-SPRINGBOOT.git
cd FULLSOUND-SPRINGBOOT

# 2. Configurar variables de entorno
cp .env.example .env
nano .env  # Editar con valores de producción

# 3. Construir imágenes
docker-compose -f docker-compose.prod.yml build

# 4. Levantar servicios
docker-compose -f docker-compose.prod.yml up -d

# 5. Verificar estado
docker-compose -f docker-compose.prod.yml ps

# 6. Ver logs
docker-compose -f docker-compose.prod.yml logs -f

# 7. Backups de BD
docker exec fullsound-mysql mysqldump -u root -p fullsound > backup_$(date +%Y%m%d).sql
```

---

## 📋 Checklist PASO 68-71

**Docker:**
- [ ] Dockerfile para Spring Boot creado
- [ ] Multi-stage build implementado
- [ ] Usuario no-root configurado
- [ ] Health checks añadidos
- [ ] Dockerfile para Frontend creado

**Docker Compose:**
- [ ] docker-compose.yml creado
- [ ] Servicios: mysql, backend, frontend, nginx
- [ ] Networks configuradas
- [ ] Volumes para persistencia
- [ ] Health checks en todos los servicios
- [ ] Variables de entorno parametrizadas

**Configuración Producción:**
- [ ] application-prod.properties creado
- [ ] HikariCP optimizado
- [ ] Logging configurado
- [ ] Actuator endpoints expuestos
- [ ] .env.example creado

**Nginx:**
- [ ] nginx.conf creado
- [ ] Reverse proxy configurado
- [ ] Rate limiting implementado
- [ ] CORS headers configurados
- [ ] SSL preparado (comentado)

**Deployment:**
- [ ] Scripts de deployment documentados
- [ ] Backups automatizados
- [ ] Monitoring preparado
- [ ] CI/CD pipeline (opcional)

---

## 📊 Arquitectura de Deployment

```
┌─────────────────────────────────────────────┐
│              Nginx (Port 80/443)             │
│         Reverse Proxy + Load Balancer       │
└────────┬──────────────────────┬──────────────┘
         │                      │
    ┌────▼─────┐          ┌────▼─────┐
    │ Frontend │          │ Backend  │
    │  (React) │          │ (Spring) │
    │ Port 5173│          │ Port 8080│
    └──────────┘          └────┬─────┘
                               │
                          ┌────▼─────┐
                          │  MySQL   │
                          │ Port 3306│
                          └──────────┘
```

---

## 🎯 URLs de Acceso

### Desarrollo
- **Frontend:** http://localhost:5173
- **Backend API:** http://localhost:8080/api
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Actuator Health:** http://localhost:8080/actuator/health
- **MySQL:** localhost:3307

### Producción (con Nginx)
- **Frontend:** http://fullsound.com
- **Backend API:** http://fullsound.com/api
- **Admin Panel:** http://fullsound.com/admin

---

## ✅ Verificación Post-Deployment

```bash
# 1. Verificar que todos los contenedores estén corriendo
docker-compose ps

# 2. Health check del backend
curl http://localhost:8080/actuator/health

# 3. Test de conexión a MySQL
docker exec -it fullsound-mysql mysql -u fullsound_user -p fullsound

# 4. Verificar logs
docker-compose logs backend | tail -100

# 5. Test de endpoints
curl http://localhost:8080/api/beats
```

---

## 🎉 FIN DE LA DOCUMENTACIÓN

¡Felicitaciones! Has completado toda la documentación de implementación del proyecto FULLSOUND Spring Boot + React.

### 📚 Archivos Creados

1. ✅ `00_INDICE_IMPLEMENTACION.md` - Índice maestro
2. ✅ `01_CONFIGURACION_INICIAL.md` - Setup inicial
3. ✅ `02_ENUMERACIONES.md` - 7 Enums
4. ✅ `03_ENTIDADES_JPA.md` - 11 Entities
5. ✅ `04_REPOSITORIES.md` - 10 Repositories
6. ✅ `05_DTOS_REQUEST.md` - 18 DTOs Request
7. ✅ `06_DTOS_RESPONSE.md` - 17 DTOs Response + 4 Wrappers
8. ✅ `07_MAPPERS.md` - 7 MapStruct Mappers
9. ✅ `08_SERVICES_INTERFACES.md` - 9 Service Interfaces
10. ✅ `09_SERVICES_IMPL.md` - 9 Service Implementations
11. ✅ `10_CONTROLLERS.md` - 9 REST Controllers (69 endpoints)
12. ✅ `11_SEGURIDAD_JWT.md` - Security + JWT
13. ✅ `12_EXCEPCIONES.md` - Global Exception Handling
14. ✅ `13_TESTING.md` - Unit & Integration Tests
15. ✅ `14_DEPLOYMENT.md` - Docker & Deployment

### 📊 Estadísticas Totales

- **71 Pasos** de implementación
- **~150 archivos** Java a crear
- **~15,000 líneas** de código documentado
- **69 endpoints** REST API
- **11 entidades** JPA
- **100+ query methods** personalizados
- **Docker-ready** con compose
- **Production-ready** con Nginx

### ⏭️ Próximos Pasos

1. Revisar todos los archivos MD en `/plan`
2. Modificar lo que necesites antes de implementar
3. Comenzar implementación paso a paso
4. Ejecutar tests en cada paso
5. Deploy con Docker Compose

¡Buena suerte con la implementación! 🚀
