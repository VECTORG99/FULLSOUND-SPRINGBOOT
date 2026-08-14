# Backend FullSound - Docker

## 🚀 Inicio Rápido

### 1. Configurar Variables de Entorno
Edita el archivo `.env` y agrega tu password de Supabase:
```bash
DB_PASSWORD=tu_password_real_aqui
```

### 2. Iniciar el Backend
```powershell
.\docker-backend.ps1 start
```

El backend estará disponible en:
- **API Base**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/auth/health

---

## 📋 Comandos Disponibles

### Gestión del Contenedor
```powershell
# Ver estado
.\docker-backend.ps1 status

# Iniciar backend
.\docker-backend.ps1 start

# Detener backend
.\docker-backend.ps1 stop

# Reiniciar backend
.\docker-backend.ps1 restart

# Ver logs en tiempo real
.\docker-backend.ps1 logs

# Reconstruir imagen
.\docker-backend.ps1 build

# Limpiar contenedor
.\docker-backend.ps1 clean
```

### Comandos Docker Directos
```powershell
# Ver logs
docker logs fullsound-backend

# Entrar al contenedor
docker exec -it fullsound-backend sh

# Ver estado
docker ps

# Detener
docker stop fullsound-backend

# Iniciar
docker start fullsound-backend

# Eliminar
docker rm fullsound-backend
```

---

## 🔧 Configuración

### Variables de Entorno (.env)
```bash
# Password de PostgreSQL (Supabase)
DB_PASSWORD=tu_password_aqui

# Secret para JWT
JWT_SECRET=MySecretKeyForJWT...

# Perfil de Spring Boot
SPRING_PROFILES_ACTIVE=prod
```

### Puertos
- **8080**: Puerto del backend Spring Boot

### Base de Datos
- **PostgreSQL en Supabase**
- URL: `aws-0-us-west-2.pooler.supabase.com:6543`
- Usuario: `postgres.kivpcepyhfpqjfoycwel`

---

## 📡 Endpoints Disponibles

### Autenticación
- `POST /api/auth/register` - Registro
- `POST /api/auth/login` - Login (devuelve JWT)
- `GET /api/auth/health` - Health check

### Beats
- `GET /api/beats` - Listar todos
- `GET /api/beats/{id}` - Por ID
- `GET /api/beats/search?q=` - Búsqueda
- `POST /api/beats` - Crear (ADMIN)

### Pedidos
- `POST /api/pedidos` - Crear pedido
- `GET /api/pedidos/mis-pedidos` - Mis pedidos
- `GET /api/pedidos/{id}` - Por ID

### Usuarios
- `GET /api/usuarios/me` - Usuario actual
- `POST /api/usuarios/cambiar-password` - Cambiar contraseña

---

## 🧪 Probar el Backend

### Health Check
```powershell
curl http://localhost:8080/api/auth/health
```

Respuesta esperada:
```json
{
  "message": "FullSound API - Servicio de autenticación activo",
  "success": true
}
```

### Login
```powershell
$body = @{
    correo = "usuario@test.com"
    contraseña = "password123"
} | ConvertTo-Json

curl -Method POST `
     -Uri "http://localhost:8080/api/auth/login" `
     -ContentType "application/json" `
     -Body $body
```

---

## 📱 Configurar App Android

### Emulador Android
En `local.properties` del proyecto Kotlin:
```properties
BACKEND_BASE_URL=http://10.0.2.2:8080/api/
```

### Dispositivo Físico
Encuentra tu IP local:
```powershell
ipconfig
```

Luego usa tu IP en `local.properties`:
```properties
BACKEND_BASE_URL=http://192.168.1.X:8080/api/
```

---

## 🐛 Troubleshooting

### El contenedor no inicia
```powershell
# Ver logs completos
docker logs fullsound-backend

# Verificar variables de entorno
docker inspect fullsound-backend
```

### Error de conexión a la base de datos
- Verificar que `DB_PASSWORD` en `.env` sea correcto
- Verificar conectividad a Supabase

### Puerto 8080 ocupado
```powershell
# Ver qué proceso usa el puerto
netstat -ano | findstr :8080

# Cambiar puerto en docker run
docker run -d --name fullsound-backend -p 9090:8080 --env-file .env fullsound-backend
```

### Reconstruir desde cero
```powershell
# Limpiar todo
.\docker-backend.ps1 clean
docker rmi fullsound-backend

# Reconstruir
.\docker-backend.ps1 build

# Iniciar
.\docker-backend.ps1 start
```

---

## 📦 Contenido de la Imagen

- **Base**: Eclipse Temurin 17 JRE Alpine
- **Tamaño**: ~400MB
- **Build**: Multi-stage (Maven + Runtime)
- **Puerto**: 8080

---

## 🔒 Seguridad

- JWT con expiración de 24 horas
- CORS configurado
- Variables sensibles en `.env` (no commitear)
- HTTPS recomendado en producción

---

## 📝 Notas

- El backend compila automáticamente dentro de Docker
- No necesitas Maven o Java instalados localmente
- Los cambios en código requieren reconstruir: `.\docker-backend.ps1 build`
- El archivo `.env` no debe estar en Git
