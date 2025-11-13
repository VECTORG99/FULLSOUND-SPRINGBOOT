# ========================================
# FULLSOUND - Docker Management Script
# ========================================
# Script de PowerShell para gestionar contenedores Docker

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet('start', 'stop', 'restart', 'logs', 'status', 'clean', 'rebuild', 'backup', 'help')]
    [string]$Action = 'help'
)

$ErrorActionPreference = "Stop"

function Write-Banner {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  🎵 FULLSOUND - Docker Manager" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
}

function Test-DockerRunning {
    try {
        docker info | Out-Null
        return $true
    } catch {
        Write-Host "❌ ERROR: Docker no está corriendo" -ForegroundColor Red
        Write-Host "   Inicia Docker Desktop y vuelve a intentar" -ForegroundColor Yellow
        exit 1
    }
}

function Start-Services {
    Write-Host "🚀 Iniciando servicios de FullSound..." -ForegroundColor Green
    
    # Verificar si existe .env
    if (-not (Test-Path ".env")) {
        Write-Host "⚠️  No se encontró .env, copiando desde .env.example..." -ForegroundColor Yellow
        Copy-Item ".env.example" ".env"
        Write-Host "✅ Archivo .env creado. Edítalo si necesitas cambiar configuraciones." -ForegroundColor Green
    }
    
    # Iniciar contenedores
    docker-compose up -d --build
    
    Write-Host ""
    Write-Host "✅ Servicios iniciados correctamente" -ForegroundColor Green
    Write-Host ""
    Write-Host "📡 URLs disponibles:" -ForegroundColor Cyan
    Write-Host "   - Backend API:  http://localhost:8080" -ForegroundColor White
    Write-Host "   - Swagger UI:   http://localhost:8080/swagger-ui.html" -ForegroundColor White
    Write-Host "   - Frontend:     http://localhost:5173" -ForegroundColor White
    Write-Host "   - MySQL:        localhost:3307" -ForegroundColor White
    Write-Host ""
    Write-Host "📊 Ver estado: .\docker.ps1 status" -ForegroundColor Yellow
    Write-Host "📋 Ver logs:   .\docker.ps1 logs" -ForegroundColor Yellow
}

function Stop-Services {
    Write-Host "🛑 Deteniendo servicios de FullSound..." -ForegroundColor Yellow
    docker-compose down
    Write-Host "✅ Servicios detenidos" -ForegroundColor Green
}

function Restart-Services {
    Write-Host "🔄 Reiniciando servicios de FullSound..." -ForegroundColor Yellow
    docker-compose restart
    Write-Host "✅ Servicios reiniciados" -ForegroundColor Green
}

function Show-Logs {
    Write-Host "📋 Mostrando logs (Ctrl+C para salir)..." -ForegroundColor Cyan
    docker-compose logs -f
}

function Show-Status {
    Write-Host "📊 Estado de los contenedores:" -ForegroundColor Cyan
    Write-Host ""
    docker-compose ps
    Write-Host ""
    
    # Health checks
    Write-Host "🏥 Health Checks:" -ForegroundColor Cyan
    Write-Host ""
    
    Write-Host "MySQL: " -NoNewline
    try {
        docker-compose exec -T mysql mysqladmin ping -h localhost -u root -pfullsound_root_2025 2>$null | Out-Null
        Write-Host "✅ Healthy" -ForegroundColor Green
    } catch {
        Write-Host "❌ Unhealthy" -ForegroundColor Red
    }
    
    Write-Host "Backend: " -NoNewline
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/health" -UseBasicParsing -TimeoutSec 5 2>$null
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ Healthy" -ForegroundColor Green
        } else {
            Write-Host "❌ Unhealthy" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ Not responding" -ForegroundColor Red
    }
    
    Write-Host "Frontend: " -NoNewline
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:5173" -UseBasicParsing -TimeoutSec 5 2>$null
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ Healthy" -ForegroundColor Green
        } else {
            Write-Host "❌ Unhealthy" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ Not responding" -ForegroundColor Red
    }
}

function Clean-All {
    Write-Host "🧹 Limpiando contenedores, imágenes y volúmenes..." -ForegroundColor Yellow
    Write-Host "⚠️  ADVERTENCIA: Esto eliminará todos los datos!" -ForegroundColor Red
    
    $confirmation = Read-Host "¿Estás seguro? (y/N)"
    if ($confirmation -eq 'y' -or $confirmation -eq 'Y') {
        docker-compose down -v --rmi all
        Write-Host "✅ Limpieza completada" -ForegroundColor Green
    } else {
        Write-Host "❌ Operación cancelada" -ForegroundColor Yellow
    }
}

function Rebuild-Services {
    Write-Host "🔨 Reconstruyendo servicios..." -ForegroundColor Yellow
    docker-compose up -d --build --force-recreate
    Write-Host "✅ Servicios reconstruidos" -ForegroundColor Green
}

function Backup-Database {
    Write-Host "💾 Creando backup de la base de datos..." -ForegroundColor Cyan
    
    $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $backupFile = "backup_fullsound_$timestamp.sql"
    
    docker-compose exec -T mysql mysqldump -u root -pfullsound_root_2025 Fullsound_Base > $backupFile
    
    if (Test-Path $backupFile) {
        Write-Host "✅ Backup creado: $backupFile" -ForegroundColor Green
    } else {
        Write-Host "❌ Error al crear backup" -ForegroundColor Red
    }
}

function Show-Help {
    Write-Host "USO: .\docker.ps1 [ACCIÓN]" -ForegroundColor White
    Write-Host ""
    Write-Host "ACCIONES DISPONIBLES:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  start     - Iniciar todos los servicios" -ForegroundColor Green
    Write-Host "  stop      - Detener todos los servicios" -ForegroundColor Yellow
    Write-Host "  restart   - Reiniciar todos los servicios" -ForegroundColor Yellow
    Write-Host "  logs      - Ver logs en tiempo real" -ForegroundColor Cyan
    Write-Host "  status    - Ver estado de contenedores" -ForegroundColor Cyan
    Write-Host "  rebuild   - Reconstruir servicios" -ForegroundColor Magenta
    Write-Host "  backup    - Crear backup de base de datos" -ForegroundColor Blue
    Write-Host "  clean     - Limpiar todo (⚠️ elimina datos)" -ForegroundColor Red
    Write-Host "  help      - Mostrar esta ayuda" -ForegroundColor White
    Write-Host ""
    Write-Host "EJEMPLOS:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  .\docker.ps1 start      # Iniciar proyecto" -ForegroundColor Gray
    Write-Host "  .\docker.ps1 status     # Ver estado" -ForegroundColor Gray
    Write-Host "  .\docker.ps1 logs       # Ver logs" -ForegroundColor Gray
    Write-Host "  .\docker.ps1 stop       # Detener todo" -ForegroundColor Gray
    Write-Host ""
}

# Main
Write-Banner
Test-DockerRunning

switch ($Action) {
    'start'   { Start-Services }
    'stop'    { Stop-Services }
    'restart' { Restart-Services }
    'logs'    { Show-Logs }
    'status'  { Show-Status }
    'clean'   { Clean-All }
    'rebuild' { Rebuild-Services }
    'backup'  { Backup-Database }
    'help'    { Show-Help }
    default   { Show-Help }
}
