# Script de desarrollo para FullSound
# Uso: .\dev.ps1 [comando]

param(
    [Parameter(Position=0)]
    [string]$Command = "help"
)

$ProjectRoot = "c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT"
$FrontendDir = "$ProjectRoot\frontend"
$SpringBootDir = "$ProjectRoot\Fullsound"
$StaticDir = "$SpringBootDir\src\main\resources\static"

function Show-Help {
    Write-Host "`n==================================" -ForegroundColor Cyan
    Write-Host "  FullSound - Scripts de Desarrollo" -ForegroundColor Cyan
    Write-Host "==================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Comandos disponibles:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "  dev-frontend    " -NoNewline
    Write-Host "- Iniciar servidor de desarrollo React (puerto 3000)" -ForegroundColor Gray
    Write-Host "  build-frontend  " -NoNewline
    Write-Host "- Construir frontend para producción" -ForegroundColor Gray
    Write-Host "  copy-build      " -NoNewline
    Write-Host "- Copiar build del frontend a Spring Boot" -ForegroundColor Gray
    Write-Host "  full-build      " -NoNewline
    Write-Host "- Build completo (frontend + copia a Spring Boot)" -ForegroundColor Gray
    Write-Host "  clean           " -NoNewline
    Write-Host "- Limpiar archivos de build" -ForegroundColor Gray
    Write-Host "  install         " -NoNewline
    Write-Host "- Instalar dependencias del frontend" -ForegroundColor Gray
    Write-Host "  help            " -NoNewline
    Write-Host "- Mostrar esta ayuda" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Ejemplos:" -ForegroundColor Yellow
    Write-Host "  .\dev.ps1 dev-frontend" -ForegroundColor Green
    Write-Host "  .\dev.ps1 full-build" -ForegroundColor Green
    Write-Host ""
}

function Start-DevFrontend {
    Write-Host "`n[*] Iniciando servidor de desarrollo React..." -ForegroundColor Cyan
    Set-Location $FrontendDir
    npm run dev
}

function Build-Frontend {
    Write-Host "`n[*] Construyendo frontend para producción..." -ForegroundColor Cyan
    Set-Location $FrontendDir
    npm run build
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[✓] Build del frontend completado exitosamente" -ForegroundColor Green
        return $true
    } else {
        Write-Host "[✗] Error en el build del frontend" -ForegroundColor Red
        return $false
    }
}

function Copy-BuildToSpringBoot {
    Write-Host "`n[*] Copiando build a Spring Boot..." -ForegroundColor Cyan
    
    $distDir = "$FrontendDir\dist"
    
    if (!(Test-Path $distDir)) {
        Write-Host "[✗] Directorio dist/ no existe. Ejecuta 'build-frontend' primero." -ForegroundColor Red
        return $false
    }
    
    # Limpiar directorio static
    if (Test-Path $StaticDir) {
        Write-Host "    Limpiando directorio static..." -ForegroundColor Gray
        Remove-Item -Path "$StaticDir\*" -Recurse -Force
    } else {
        New-Item -ItemType Directory -Path $StaticDir -Force | Out-Null
    }
    
    # Copiar archivos
    Write-Host "    Copiando archivos..." -ForegroundColor Gray
    Copy-Item -Path "$distDir\*" -Destination $StaticDir -Recurse -Force
    
    Write-Host "[✓] Archivos copiados exitosamente" -ForegroundColor Green
    Write-Host "    Ubicación: $StaticDir" -ForegroundColor Gray
    return $true
}

function Invoke-FullBuild {
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "  Build Completo de FullSound" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    
    # Build frontend
    if (Build-Frontend) {
        # Copiar a Spring Boot
        if (Copy-BuildToSpringBoot) {
            Write-Host "`n[✓] Build completo exitoso!" -ForegroundColor Green
            Write-Host ""
            Write-Host "Siguiente paso:" -ForegroundColor Yellow
            Write-Host "  1. Ejecutar FullsoundApplication.java desde tu IDE" -ForegroundColor Gray
            Write-Host "  2. Acceder a http://localhost:8080" -ForegroundColor Gray
            Write-Host ""
        }
    }
}

function Clear-BuildFiles {
    Write-Host "`n[*] Limpiando archivos de build..." -ForegroundColor Cyan
    
    # Limpiar dist
    $distDir = "$FrontendDir\dist"
    if (Test-Path $distDir) {
        Write-Host "    Eliminando frontend/dist..." -ForegroundColor Gray
        Remove-Item -Path $distDir -Recurse -Force
    }
    
    # Limpiar static
    if (Test-Path $StaticDir) {
        Write-Host "    Eliminando resources/static..." -ForegroundColor Gray
        Remove-Item -Path "$StaticDir\*" -Recurse -Force
    }
    
    # Limpiar node_modules/.vite
    $viteCache = "$FrontendDir\node_modules\.vite"
    if (Test-Path $viteCache) {
        Write-Host "    Eliminando cache de Vite..." -ForegroundColor Gray
        Remove-Item -Path $viteCache -Recurse -Force
    }
    
    Write-Host "[✓] Limpieza completada" -ForegroundColor Green
}

function Install-Dependencies {
    Write-Host "`n[*] Instalando dependencias del frontend..." -ForegroundColor Cyan
    Set-Location $FrontendDir
    npm install
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[✓] Dependencias instaladas exitosamente" -ForegroundColor Green
    } else {
        Write-Host "[✗] Error instalando dependencias" -ForegroundColor Red
    }
}

# Ejecutar comando
switch ($Command.ToLower()) {
    "dev-frontend" { Start-DevFrontend }
    "build-frontend" { Build-Frontend }
    "copy-build" { Copy-BuildToSpringBoot }
    "full-build" { Invoke-FullBuild }
    "clean" { Clear-BuildFiles }
    "install" { Install-Dependencies }
    "help" { Show-Help }
    default {
        Write-Host "[✗] Comando no reconocido: $Command" -ForegroundColor Red
        Show-Help
    }
}
