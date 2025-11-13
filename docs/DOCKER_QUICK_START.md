# 🐳 INICIO RÁPIDO CON DOCKER

## ⚡ 3 PASOS PARA EJECUTAR

### 1️⃣ Instalar Docker Desktop

Descarga e instala: https://www.docker.com/products/docker-desktop/

### 2️⃣ Ejecutar Script

```powershell
# Iniciar todo
.\docker.ps1 start

# Ver estado
.\docker.ps1 status

# Ver logs
.\docker.ps1 logs
```

### 3️⃣ Verificar

- 🌐 **Backend:** http://localhost:8080/swagger-ui.html
- 🎵 **Frontend:** http://localhost:5173
- 🗄️ **MySQL:** localhost:3307

## 📚 Documentación Completa

Ver: [DOCKER_SETUP.md](DOCKER_SETUP.md)

## 🛠️ Comandos Disponibles

```powershell
.\docker.ps1 start     # Iniciar
.\docker.ps1 stop      # Detener
.\docker.ps1 restart   # Reiniciar
.\docker.ps1 logs      # Ver logs
.\docker.ps1 status    # Estado
.\docker.ps1 rebuild   # Reconstruir
.\docker.ps1 backup    # Backup BD
.\docker.ps1 clean     # Limpiar todo
```

## ⚙️ Configuración

Edita `.env` para cambiar:
- Contraseñas de MySQL
- Claves de Stripe
- JWT secret

**¡Listo! 🎉**
