# 🐳 FULLSOUND - CONFIGURACIÓN CON DOCKER

> **Ejecuta todo el proyecto sin instalar Java, Maven o MySQL**

---

## 📋 REQUISITOS

**Solo necesitas:**

- 🐳 **Docker Desktop** para Windows
  - Descargar: https://www.docker.com/products/docker-desktop/
  - Versión mínima: 20.10+
  - Incluye Docker Compose

---

## 🚀 INICIO RÁPIDO (3 PASOS)

### **Paso 1: Instalar Docker Desktop**

```powershell
# 1. Descargar Docker Desktop desde:
# https://www.docker.com/products/docker-desktop/

# 2. Instalar con las opciones por defecto

# 3. Verificar instalación
docker --version
docker-compose --version
```

### **Paso 2: Configurar Variables de Entorno**

```powershell
# Copiar archivo de ejemplo
cd c:\Users\dh893\Documents\GitHub\FULLSOUND-SPRINGBOOT
cp .env.example .env

# Editar .env con tus claves reales (especialmente Stripe)
notepad .env
```

### **Paso 3: Levantar Todo el Sistema**

```powershell
# Construir y ejecutar todos los contenedores
docker-compose up --build

# O en segundo plano (detached)
docker-compose up -d --build
```

**¡Listo!** 🎉

- **Backend API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Frontend:** http://localhost:5173
- **MySQL:** localhost:3307

---

## 📦 ARQUITECTURA DOCKER

```
┌─────────────────────────────────────────────────────────────┐
│                    FULLSOUND DOCKER STACK                    │
└─────────────────────────────────────────────────────────────┘

┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   FRONTEND      │     │    BACKEND      │     │     MYSQL       │
│   (React)       │────▶│  (Spring Boot)  │────▶│     8.0         │
│  Port: 5173     │     │   Port: 8080    │     │  Port: 3307     │
│  Nginx Alpine   │     │   Java 17 JRE   │     │   8.0-latest    │
└─────────────────┘     └─────────────────┘     └─────────────────┘
        │                       │                        │
        │                       │                        │
        └───────────────────────┴────────────────────────┘
                                │
                        ┌───────▼────────┐
                        │  Docker Network │
                        │ fullsound-net   │
                        └─────────────────┘

VOLUMENES PERSISTENTES:
├─ mysql_data         → /var/lib/mysql (Base de datos)
└─ uploads_data       → /app/uploads (Archivos de beats)
```

---

## 🛠️ COMANDOS DOCKER

### **Gestión de Contenedores**

```powershell
# Ver estado de contenedores
docker-compose ps

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f backend
docker-compose logs -f mysql

# Detener contenedores
docker-compose down

# Detener y eliminar volúmenes (⚠️ BORRA DATOS)
docker-compose down -v

# Reiniciar un servicio
docker-compose restart backend

# Reconstruir imagen y reiniciar
docker-compose up -d --build backend
```

### **Acceso a Contenedores**

```powershell
# Acceder a bash del backend
docker-compose exec backend sh

# Acceder a MySQL
docker-compose exec mysql mysql -u fullsound_user -pfullsound_pass_2025 Fullsound_Base

# Ver logs de MySQL
docker-compose exec mysql tail -f /var/log/mysql/error.log
```

### **Mantenimiento**

```powershell
# Limpiar imágenes no usadas
docker system prune -a

# Ver uso de disco
docker system df

# Eliminar volúmenes huérfanos
docker volume prune

# Backup de base de datos
docker-compose exec mysql mysqldump -u root -pfullsound_root_2025 Fullsound_Base > backup.sql

# Restaurar backup
docker-compose exec -T mysql mysql -u root -pfullsound_root_2025 Fullsound_Base < backup.sql
```

---

## 📁 ESTRUCTURA DE ARCHIVOS DOCKER

```
FULLSOUND-SPRINGBOOT/
│
├── docker-compose.yml              # Orquestación de servicios
├── .env                            # Variables de entorno (crear desde .env.example)
├── .env.example                    # Plantilla de variables
│
├── Fullsound/
│   ├── Dockerfile                  # Imagen del backend
│   ├── .dockerignore              # Archivos excluidos del build
│   └── src/main/resources/
│       └── application-docker.properties  # Config para Docker
│
├── frontend/
│   ├── Dockerfile                  # Imagen del frontend
│   └── docker/
│       └── nginx.conf             # Configuración Nginx
│
└── docker/
    └── mysql/
        └── my.cnf                 # Configuración MySQL
```

---

## 🔧 CONFIGURACIÓN DETALLADA

### **docker-compose.yml**

Define 3 servicios:

#### **1. MySQL**
- **Imagen:** mysql:8.0
- **Puerto:** 3307 (host) → 3306 (container)
- **Volumen:** Datos persistentes en `mysql_data`
- **Init:** Ejecuta `DATABASE_MIGRATION.sql` al crear
- **Health Check:** Verifica disponibilidad antes de backend

#### **2. Backend (Spring Boot)**
- **Build:** Desde `Fullsound/Dockerfile`
- **Puerto:** 8080 → 8080
- **Depende de:** MySQL (espera health check)
- **Volumen:** Uploads persistentes
- **Perfil:** `docker` (usa `application-docker.properties`)

#### **3. Frontend (React)**
- **Build:** Desde `frontend/Dockerfile`
- **Puerto:** 5173 (host) → 3000 (container)
- **Servidor:** Nginx Alpine
- **Proxy:** API requests a backend

### **Variables de Entorno (.env)**

Crea `.env` desde `.env.example`:

```bash
# Database
MYSQL_ROOT_PASSWORD=fullsound_root_2025
MYSQL_DATABASE=Fullsound_Base
MYSQL_USER=fullsound_user
MYSQL_PASSWORD=fullsound_pass_2025

# JWT
JWT_SECRET=MySecretKeyForJWT...
JWT_EXPIRATION=86400000

# Stripe (⚠️ CAMBIAR)
STRIPE_API_KEY=sk_test_TU_CLAVE_REAL
STRIPE_WEBHOOK_SECRET=whsec_TU_WEBHOOK
VITE_STRIPE_PUBLIC_KEY=pk_test_TU_CLAVE_PUBLICA

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
```

---

## 🔍 VERIFICACIÓN

### **1. Verificar que los contenedores están corriendo**

```powershell
docker-compose ps
```

Deberías ver:

```
NAME                    STATUS              PORTS
fullsound-mysql         Up (healthy)        0.0.0.0:3307->3306/tcp
fullsound-backend       Up (healthy)        0.0.0.0:8080->8080/tcp
fullsound-frontend      Up                  0.0.0.0:5173->3000/tcp
```

### **2. Verificar logs del backend**

```powershell
docker-compose logs backend | Select-String "Started"
```

Deberías ver:

```
fullsound-backend | Started FullsoundApplication in X.XXX seconds
```

### **3. Probar endpoints**

```powershell
# Health check
curl http://localhost:8080/api/auth/health

# Swagger UI (abrir en navegador)
start http://localhost:8080/swagger-ui.html
```

### **4. Verificar base de datos**

```powershell
docker-compose exec mysql mysql -u fullsound_user -pfullsound_pass_2025 -e "SHOW TABLES FROM Fullsound_Base;"
```

Deberías ver:

```
+-------------------------+
| Tables_in_Fullsound_Base|
+-------------------------+
| beat                    |
| compra                  |
| compra_detalle          |
| pago                    |
| tipo_usuario            |
| usuario                 |
| usuario_roles           |
+-------------------------+
```

---

## 🧪 PROBAR LA API

### **1. Registrar Usuario**

```powershell
curl -X POST http://localhost:8080/api/auth/register `
  -H "Content-Type: application/json" `
  -d '{
    "nombreUsuario": "testuser",
    "correo": "test@test.com",
    "contraseña": "password123"
  }'
```

### **2. Login**

```powershell
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{
    "nombreUsuario": "testuser",
    "contraseña": "password123"
  }'
```

### **3. Listar Beats (con token)**

```powershell
$token = "TU_TOKEN_JWT_AQUI"
curl http://localhost:8080/api/beats `
  -H "Authorization: Bearer $token"
```

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### **Error: "Docker daemon is not running"**

```powershell
# Abrir Docker Desktop y esperar a que inicie
# O reiniciar servicio Docker
```

### **Error: "Port 8080 is already allocated"**

```powershell
# Detener contenedores
docker-compose down

# Cambiar puerto en docker-compose.yml
# ports:
#   - "8081:8080"  # Usar 8081 en vez de 8080
```

### **Error: "Cannot connect to MySQL"**

```powershell
# Ver logs de MySQL
docker-compose logs mysql

# Verificar health check
docker-compose ps

# Reiniciar solo MySQL
docker-compose restart mysql
```

### **Error: "Backend no arranca"**

```powershell
# Ver logs detallados
docker-compose logs -f backend

# Verificar que MySQL está healthy
docker-compose ps

# Reconstruir imagen
docker-compose up -d --build backend
```

### **Backend arranca pero da error 500**

```powershell
# Verificar que las tablas existen
docker-compose exec mysql mysql -u fullsound_user -pfullsound_pass_2025 Fullsound_Base -e "SHOW TABLES;"

# Si no existen, ejecutar migration manualmente
docker-compose exec -T mysql mysql -u fullsound_user -pfullsound_pass_2025 Fullsound_Base < plan/DATABASE_MIGRATION.sql
```

### **Limpiar todo y empezar de cero**

```powershell
# Detener y eliminar TODO (⚠️ PERDERÁS DATOS)
docker-compose down -v

# Eliminar imágenes
docker-compose down --rmi all

# Limpiar sistema
docker system prune -a --volumes

# Reconstruir desde cero
docker-compose up -d --build
```

---

## 📊 MONITOREO

### **Ver métricas en tiempo real**

```powershell
# CPU y memoria de contenedores
docker stats

# Solo backend
docker stats fullsound-backend
```

### **Health checks**

```powershell
# Backend
curl http://localhost:8080/actuator/health

# MySQL (desde dentro del contenedor)
docker-compose exec mysql mysqladmin ping -h localhost -u root -pfullsound_root_2025
```

---

## 🚀 DESPLIEGUE A PRODUCCIÓN

### **Cambios necesarios:**

1. **Cambiar secretos** en `.env`:
   - JWT_SECRET con clave real de 256+ bits
   - STRIPE_API_KEY con `sk_live_...`
   - MYSQL_PASSWORD segura

2. **Cambiar perfil** Spring:
   ```yaml
   environment:
     SPRING_PROFILES_ACTIVE: prod
   ```

3. **Agregar volumen para logs**:
   ```yaml
   volumes:
     - ./logs:/app/logs
   ```

4. **Configurar reverse proxy** (Nginx/Traefik)

5. **SSL/TLS** con Let's Encrypt

---

## 📚 RECURSOS ADICIONALES

- **Docker Compose Docs:** https://docs.docker.com/compose/
- **Docker Hub - MySQL:** https://hub.docker.com/_/mysql
- **Spring Boot Docker:** https://spring.io/guides/gs/spring-boot-docker/
- **Multi-stage Builds:** https://docs.docker.com/build/building/multi-stage/

---

## ✅ CHECKLIST DE VERIFICACIÓN

Antes de dar por terminado:

- [ ] Docker Desktop instalado y corriendo
- [ ] `.env` creado y configurado
- [ ] `docker-compose up -d --build` ejecutado sin errores
- [ ] `docker-compose ps` muestra todos los contenedores `Up`
- [ ] MySQL está `healthy`
- [ ] Backend está `healthy`
- [ ] http://localhost:8080/swagger-ui.html carga
- [ ] http://localhost:8080/api/auth/health responde 200
- [ ] Puedes registrar un usuario
- [ ] Puedes hacer login y obtener JWT
- [ ] Frontend carga (si lo levantaste)

---

## 🎯 VENTAJAS DE DOCKER

✅ **No necesitas instalar:** Java, Maven, MySQL  
✅ **Mismo ambiente:** Desarrollo = Producción  
✅ **Fácil compartir:** Solo `docker-compose up`  
✅ **Aislamiento:** No contamina tu sistema  
✅ **Portabilidad:** Funciona en Windows, Mac, Linux  
✅ **Escalabilidad:** Fácil agregar réplicas  
✅ **Rollback:** Fácil volver a versión anterior  

---

**¡Todo listo para ejecutar FullSound con Docker! 🐳🎉**
