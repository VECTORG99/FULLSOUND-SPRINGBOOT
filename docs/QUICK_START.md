# Guía de Inicio Rápido

## Comenzar en 5 Minutos

### Opción 1: Solo Desarrollo Frontend

Iniciar el servidor de desarrollo React:

```bash
cd frontend
npm install
npm run dev
```

Abrir navegador en: http://localhost:3000

### Opción 2: Aplicación Completa

Compilar y ejecutar la aplicación completa:

```bash
# Paso 1: Compilar frontend
cd frontend
npm install
npm run build

# Paso 2: Copiar build a recursos de Spring Boot
# Copiar contenido de dist/ a Fullsound/src/main/resources/static/

# Paso 3: Ejecutar Spring Boot
cd Fullsound
mvn spring-boot:run
```

O ejecutar `FullsoundApplication.java` desde tu IDE.

Abrir navegador en: http://localhost:8080

## Usando Scripts de Desarrollo

Scripts de PowerShell disponibles para tareas comunes:

```powershell
# Mostrar todos los comandos disponibles
.\dev.ps1 help

# Instalar dependencias
.\dev.ps1 install

# Iniciar servidor de desarrollo frontend
.\dev.ps1 dev-frontend

# Compilar frontend
.\dev.ps1 build-frontend

# Copiar build a Spring Boot
.\dev.ps1 copy-build

# Proceso de compilación completo
.\dev.ps1 full-build

# Limpiar artefactos de compilación
.\dev.ps1 clean
```

## Requisitos Previos

Antes de comenzar, asegúrate de tener:

### Para Desarrollo Frontend
- Node.js 20 o superior
- npm 10 o superior

### Para Aplicación Completa
- Node.js 20 o superior
- npm 10 o superior
- Java 17 o superior
- Maven 3.6 o superior
- MySQL 8 o superior
- Base de datos MySQL llamada `fullsound` creada

## Configuración Inicial

1. Instalar dependencias del frontend:
```bash
cd frontend
npm install
```

2. Configurar base de datos en `Fullsound/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fullsound
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

3. Crear base de datos:
```sql
CREATE DATABASE fullsound;
```

4. Probar frontend:
```bash
cd frontend
npm run dev
```

5. Compilar y ejecutar aplicación completa:
```bash
cd frontend
npm run build
# Copiar dist/ a Fullsound/src/main/resources/static/
cd ../Fullsound
mvn spring-boot:run
```

## Problemas Comunes

### Maven no encontrado
Instalar Maven o ejecutar Spring Boot desde tu IDE.

### Puerto 8080 ya está en uso
Cambiar puerto en `application.properties`:
```properties
server.port=8081
```

### No se puede conectar a la base de datos
- Verificar que MySQL esté ejecutándose
- Verificar que la base de datos existe: `CREATE DATABASE fullsound;`
- Verificar credenciales en `application.properties`

### La compilación falla
Limpiar y recompilar:
```bash
cd frontend
rm -rf dist node_modules
npm install
npm run build
```

## Documentación Adicional

Para documentación completa, ver:
- README.md - Visión general del proyecto
- README_MIGRACION.md - Guía de migración
- migracion/ - Documentación detallada de migración
