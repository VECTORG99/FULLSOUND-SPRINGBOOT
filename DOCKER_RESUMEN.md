# 🐳 FULLSOUND - CONFIGURACIÓN DOCKER COMPLETADA

## ✅ ARCHIVOS CREADOS

```
FULLSOUND-SPRINGBOOT/
│
├── 🐳 docker-compose.yml              # Orquestación (MySQL + Backend + Frontend)
├── 📄 .env.example                    # Plantilla de variables
├── 📄 .env                            # Variables de entorno (ya creado)
├── 🔧 docker.ps1                      # Script de gestión PowerShell
│
├── 📚 DOCKER_SETUP.md                 # Guía completa paso a paso
├── ⚡ DOCKER_QUICK_START.md           # Inicio rápido
│
├── Fullsound/
│   ├── 🐳 Dockerfile                  # Imagen multi-stage del backend
│   ├── 📄 .dockerignore              # Exclusiones del build
│   └── src/main/resources/
│       └── application-docker.properties  # Config para contenedor
│
├── frontend/
│   ├── 🐳 Dockerfile                  # Imagen del frontend (React + Nginx)
│   └── docker/
│       └── nginx.conf                # Configuración Nginx
│
└── docker/
    └── mysql/
        └── my.cnf                    # Configuración MySQL
```

## 🚀 CÓMO USAR

### **OPCIÓN 1: Script PowerShell (Recomendado)**

```powershell
# Ver ayuda
.\docker.ps1 help

# Iniciar todo
.\docker.ps1 start

# Ver estado
.\docker.ps1 status

# Ver logs en tiempo real
.\docker.ps1 logs

# Detener
.\docker.ps1 stop
```

### **OPCIÓN 2: Docker Compose Manual**

```powershell
# Iniciar
docker-compose up -d --build

# Ver estado
docker-compose ps

# Ver logs
docker-compose logs -f

# Detener
docker-compose down
```

## 📊 SERVICIOS CONFIGURADOS

### 1️⃣ **MySQL 8.0**
- **Puerto:** 3307 (host) → 3306 (container)
- **Base de datos:** Fullsound_Base
- **Usuario:** fullsound_user
- **Password:** fullsound_pass_2025
- **Volumen persistente:** mysql_data
- **Init script:** Ejecuta DATABASE_MIGRATION.sql automáticamente
- **Health check:** Verifica disponibilidad antes de backend

### 2️⃣ **Backend Spring Boot**
- **Puerto:** 8080 → 8080
- **Build:** Multi-stage (Maven builder + JRE runtime)
- **Perfil Spring:** docker
- **Variables:** Desde .env
- **Volumen:** uploads_data para archivos
- **Depende de:** MySQL (espera health check)
- **Health check:** /api/auth/health cada 30s

### 3️⃣ **Frontend React** (Opcional)
- **Puerto:** 5173 (host) → 3000 (container)
- **Build:** Multi-stage (Node builder + Nginx runtime)
- **Servidor:** Nginx Alpine
- **Proxy:** API requests al backend
- **Health check:** Respuesta HTTP

## 🔐 CONFIGURACIÓN DE SEGURIDAD

### Variables en `.env`:

```bash
# Database
MYSQL_ROOT_PASSWORD=fullsound_root_2025
MYSQL_USER=fullsound_user
MYSQL_PASSWORD=fullsound_pass_2025

# JWT (⚠️ Cambiar en producción)
JWT_SECRET=MySecretKeyForJWT...
JWT_EXPIRATION=86400000

# Stripe (⚠️ Usar claves reales)
STRIPE_API_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...
VITE_STRIPE_PUBLIC_KEY=pk_test_...
```

## 🌐 URLS DISPONIBLES

Una vez iniciado:

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Backend API** | http://localhost:8080 | API REST |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Documentación interactiva |
| **API Docs** | http://localhost:8080/api-docs | OpenAPI JSON |
| **Health Check** | http://localhost:8080/api/auth/health | Estado del backend |
| **Actuator** | http://localhost:8080/actuator/health | Métricas Spring |
| **Frontend** | http://localhost:5173 | Aplicación React |
| **MySQL** | localhost:3307 | Base de datos |

## 🔍 VERIFICACIÓN PASO A PASO

### 1. Verificar Docker

```powershell
docker --version
docker-compose --version
```

### 2. Iniciar servicios

```powershell
.\docker.ps1 start
```

Deberías ver:

```
🚀 Iniciando servicios de FullSound...
Creating network "fullsound-network" with driver "bridge"
Creating volume "fullsound-mysql-data" with default driver
Creating volume "fullsound-uploads" with default driver
Creating fullsound-mysql ... done
Creating fullsound-backend ... done
Creating fullsound-frontend ... done

✅ Servicios iniciados correctamente

📡 URLs disponibles:
   - Backend API:  http://localhost:8080
   - Swagger UI:   http://localhost:8080/swagger-ui.html
   - Frontend:     http://localhost:5173
   - MySQL:        localhost:3307
```

### 3. Verificar estado

```powershell
.\docker.ps1 status
```

Deberías ver todos los contenedores **Up** y **Healthy**:

```
📊 Estado de los contenedores:

NAME                    STATUS              PORTS
fullsound-mysql         Up (healthy)        0.0.0.0:3307->3306/tcp
fullsound-backend       Up (healthy)        0.0.0.0:8080->8080/tcp
fullsound-frontend      Up                  0.0.0.0:5173->3000/tcp

🏥 Health Checks:

MySQL: ✅ Healthy
Backend: ✅ Healthy
Frontend: ✅ Healthy
```

### 4. Verificar logs

```powershell
.\docker.ps1 logs
```

Busca líneas como:

```
fullsound-backend | Started FullsoundApplication in 45.123 seconds
fullsound-mysql | ready for connections
```

### 5. Probar API

```powershell
# Health check
curl http://localhost:8080/api/auth/health

# Respuesta esperada
{"message":"FullSound API - Servicio de autenticación activo","success":true}
```

### 6. Abrir Swagger

```powershell
start http://localhost:8080/swagger-ui.html
```

## 🧪 PRUEBAS RÁPIDAS

### Registrar usuario

```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/auth/register `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"nombreUsuario":"testuser","correo":"test@test.com","contraseña":"password123"}'
```

### Login

```powershell
$response = Invoke-RestMethod -Uri http://localhost:8080/api/auth/login `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"nombreUsuario":"testuser","contraseña":"password123"}'

$token = $response.token
Write-Host "Token: $token"
```

### Listar beats (con token)

```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/beats `
  -Method Get `
  -Headers @{Authorization = "Bearer $token"}
```

## 📦 VOLÚMENES PERSISTENTES

Los datos se guardan en volúmenes Docker:

```powershell
# Ver volúmenes
docker volume ls | Select-String fullsound

# Resultado
fullsound-mysql-data
fullsound-uploads
```

**Datos persistentes:**
- ✅ Base de datos MySQL
- ✅ Archivos de beats subidos
- ✅ Imágenes de portadas

## 🛠️ MANTENIMIENTO

### Backup de base de datos

```powershell
# Automático con script
.\docker.ps1 backup

# Manual
docker-compose exec mysql mysqldump -u root -pfullsound_root_2025 Fullsound_Base > backup.sql
```

### Restaurar backup

```powershell
Get-Content backup.sql | docker-compose exec -T mysql mysql -u root -pfullsound_root_2025 Fullsound_Base
```

### Reiniciar un servicio

```powershell
docker-compose restart backend
```

### Ver logs de un servicio específico

```powershell
docker-compose logs -f backend
docker-compose logs -f mysql
```

### Reconstruir imagen

```powershell
.\docker.ps1 rebuild

# O manualmente
docker-compose up -d --build backend
```

### Limpiar todo

```powershell
# Con script (pide confirmación)
.\docker.ps1 clean

# Manual (⚠️ BORRA TODO)
docker-compose down -v --rmi all
```

## 🐛 SOLUCIÓN DE PROBLEMAS

### ❌ "Docker daemon is not running"

**Solución:** Abre Docker Desktop y espera a que inicie

### ❌ "Port already allocated"

**Solución:** Cambia el puerto en `docker-compose.yml`

```yaml
ports:
  - "8081:8080"  # Cambia 8080 por 8081
```

### ❌ Backend no arranca

```powershell
# Ver logs
docker-compose logs backend

# Verificar MySQL
docker-compose ps

# Reintentar
docker-compose restart backend
```

### ❌ Error 500 en API

```powershell
# Verificar tablas en BD
docker-compose exec mysql mysql -u fullsound_user -pfullsound_pass_2025 Fullsound_Base -e "SHOW TABLES;"

# Si no hay tablas, ejecutar migration
Get-Content plan/DATABASE_MIGRATION.sql | docker-compose exec -T mysql mysql -u fullsound_user -pfullsound_pass_2025 Fullsound_Base
```

## 📈 MONITOREO

### CPU y Memoria

```powershell
docker stats
```

### Métricas Spring

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
Invoke-RestMethod http://localhost:8080/actuator/metrics
```

## 🎯 VENTAJAS DE ESTA CONFIGURACIÓN

✅ **Multi-stage builds** → Imágenes optimizadas (backend: ~200MB)  
✅ **Health checks** → Orden de inicio correcto  
✅ **Volúmenes persistentes** → Datos no se pierden  
✅ **Variables de entorno** → Configuración flexible  
✅ **Network aislada** → Comunicación interna segura  
✅ **Usuario no-root** → Seguridad mejorada  
✅ **Init script** → BD lista automáticamente  
✅ **Script de gestión** → Fácil uso  

## 📚 DOCUMENTACIÓN RELACIONADA

- **[DOCKER_SETUP.md](DOCKER_SETUP.md)** - Guía detallada completa
- **[DOCKER_QUICK_START.md](DOCKER_QUICK_START.md)** - Inicio rápido
- **[BACKEND_COMPLETADO.md](BACKEND_COMPLETADO.md)** - Documentación del backend
- **[docker-compose.yml](docker-compose.yml)** - Archivo de configuración

---

**🎉 ¡Configuración Docker completada!**

**Ahora puedes ejecutar todo el proyecto con un solo comando:**

```powershell
.\docker.ps1 start
```

**¡No necesitas instalar Java, Maven ni MySQL! 🐳**
