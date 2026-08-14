# Script para gestionar el backend FullSound en Docker
# Uso: .\docker-backend.ps1 [start|stop|restart|logs|status]

param(
    [Parameter(Position=0)]
    [string]$Command = "status"
)

$ContainerName = "fullsound-backend"
$ImageName = "fullsound-backend"
$Port = "8080"

function Show-Help {
    Write-Host "`n=====================================" -ForegroundColor Cyan
    Write-Host "  FullSound Backend - Docker Manager" -ForegroundColor Cyan
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Comandos disponibles:" -ForegroundColor Yellow
    Write-Host "  start    - Iniciar el backend" -ForegroundColor Gray
    Write-Host "  stop     - Detener el backend" -ForegroundColor Gray
    Write-Host "  restart  - Reiniciar el backend" -ForegroundColor Gray
    Write-Host "  logs     - Ver logs del backend" -ForegroundColor Gray
    Write-Host "  status   - Ver estado del backend" -ForegroundColor Gray
    Write-Host "  build    - Reconstruir la imagen" -ForegroundColor Gray
    Write-Host "  clean    - Detener y eliminar contenedor" -ForegroundColor Gray
    Write-Host ""
}

function Start-Backend {
    Write-Host "`n[*] Iniciando backend FullSound..." -ForegroundColor Cyan
    
    # Verificar si ya existe un contenedor con ese nombre
    $existing = docker ps -a --filter "name=$ContainerName" --format "{{.Names}}"
    
    if ($existing) {
        Write-Host "[!] El contenedor ya existe. Iniciándolo..." -ForegroundColor Yellow
        docker start $ContainerName
    } else {
        Write-Host "[*] Creando nuevo contenedor..." -ForegroundColor Cyan
        docker run -d --name $ContainerName -p ${Port}:8080 --env-file .env $ImageName
    }
    
    Write-Host "[✓] Backend iniciado en http://localhost:$Port" -ForegroundColor Green
    Write-Host "[i] Health Check: http://localhost:${Port}/api/auth/health" -ForegroundColor Gray
}

function Stop-Backend {
    Write-Host "`n[*] Deteniendo backend..." -ForegroundColor Cyan
    docker stop $ContainerName
    Write-Host "[✓] Backend detenido" -ForegroundColor Green
}

function Restart-Backend {
    Write-Host "`n[*] Reiniciando backend..." -ForegroundColor Cyan
    docker restart $ContainerName
    Write-Host "[✓] Backend reiniciado" -ForegroundColor Green
}

function Show-Logs {
    Write-Host "`n[*] Mostrando logs del backend (Ctrl+C para salir)..." -ForegroundColor Cyan
    docker logs -f $ContainerName
}

function Show-Status {
    Write-Host "`n=====================================" -ForegroundColor Cyan
    Write-Host "  Estado del Backend" -ForegroundColor Cyan
    Write-Host "=====================================" -ForegroundColor Cyan
    
    $container = docker ps -a --filter "name=$ContainerName" --format "{{.Names}}\t{{.Status}}\t{{.Ports}}"
    
    if ($container) {
        Write-Host "`nContenedor: $ContainerName" -ForegroundColor Green
        Write-Host $container
        
        Write-Host "`n[i] Probando conexión..." -ForegroundColor Gray
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:${Port}/api/auth/health" -TimeoutSec 2 -ErrorAction Stop
            Write-Host "[✓] Backend respondiendo correctamente" -ForegroundColor Green
            Write-Host "    Respuesta: $($response.Content)" -ForegroundColor Gray
        } catch {
            Write-Host "[✗] Backend no responde" -ForegroundColor Red
        }
    } else {
        Write-Host "[!] No hay contenedor con nombre '$ContainerName'" -ForegroundColor Yellow
    }
    Write-Host ""
}

function Build-Image {
    Write-Host "`n[*] Reconstruyendo imagen Docker..." -ForegroundColor Cyan
    docker build -t $ImageName .
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[✓] Imagen construida exitosamente" -ForegroundColor Green
    } else {
        Write-Host "[✗] Error al construir la imagen" -ForegroundColor Red
    }
}

function Clean-Container {
    Write-Host "`n[*] Limpiando contenedor..." -ForegroundColor Cyan
    docker stop $ContainerName 2>$null
    docker rm $ContainerName 2>$null
    Write-Host "[✓] Contenedor eliminado" -ForegroundColor Green
}

# Ejecutar comando
switch ($Command.ToLower()) {
    "start" { Start-Backend }
    "stop" { Stop-Backend }
    "restart" { Restart-Backend }
    "logs" { Show-Logs }
    "status" { Show-Status }
    "build" { Build-Image }
    "clean" { Clean-Container }
    "help" { Show-Help }
    default { Show-Status }
}
