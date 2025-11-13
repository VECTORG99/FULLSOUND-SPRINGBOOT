# FullSound - Aplicación Spring Boot

## Descripción

FullSound es una aplicación web que combina un frontend React con un backend Spring Boot. La aplicación sirve como plataforma para explorar, comprar y gestionar beats musicales.

## Stack Tecnológico

### Frontend
- React 19.1.1
- React Router 7.9.4
- Vite 7.2.2
- Axios
- Vitest

### Backend
- Spring Boot 3.5.7
- Spring Data JPA
- Spring Boot Actuator
- MySQL
- Maven

## Requisitos Previos

- Node.js 20+
- npm 10+
- Java 17+
- Maven 3.6+
- MySQL 8+

## Instalación

### Configuración del Frontend

```bash
cd frontend
npm install
```

### Configuración del Backend

1. Crear base de datos MySQL:
```sql
CREATE DATABASE fullsound;
```

2. Configurar conexión a la base de datos en `Fullsound/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fullsound
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

## Desarrollo

### Servidor de Desarrollo Frontend

```bash
cd frontend
npm run dev
```

Acceder en: http://localhost:3000

### Servidor de Desarrollo Backend

```bash
cd Fullsound
mvn spring-boot:run
```

O ejecutar `FullsoundApplication.java` desde tu IDE.

Acceder en: http://localhost:8080

## Compilación para Producción

### Compilar Frontend

```bash
cd frontend
npm run build
```

### Copiar Build a Spring Boot

```bash
# Copiar frontend/dist/ a Fullsound/src/main/resources/static/
```

### Compilar Aplicación Completa

```bash
cd Fullsound
mvn clean package
```

Esto genera `target/fullsound-frontend.jar`

### Ejecutar JAR de Producción

```bash
java -jar target/fullsound-frontend.jar
```

## Scripts de Desarrollo

Se proporciona un script de PowerShell para tareas comunes:

```powershell
.\dev.ps1 help           # Mostrar comandos disponibles
.\dev.ps1 dev-frontend   # Iniciar servidor de desarrollo frontend
.\dev.ps1 build-frontend # Compilar frontend
.\dev.ps1 copy-build     # Copiar build a Spring Boot
.\dev.ps1 full-build     # Proceso de compilación completo
.\dev.ps1 clean          # Limpiar artefactos de compilación
.\dev.ps1 install        # Instalar dependencias del frontend
```

## Endpoints de la API

- Health Check: `/api/health`
- Actuator: `/actuator/health`

## Configuración

### Propiedades de la Aplicación

Configuración clave en `application.properties`:

- Puerto del servidor: `server.port=8080`
- Configuración de base de datos
- Configuración JPA/Hibernate
- Configuración de recursos estáticos
- Configuración CORS

### Configuración del Frontend

Configuración clave en `vite.config.js`:

- Ruta base
- Directorio de salida de compilación
- Puerto del servidor

## Pruebas

### Pruebas del Frontend

```bash
cd frontend
npm test
```

### Pruebas del Backend

```bash
cd Fullsound
mvn test
```

## Documentación

Documentación adicional disponible en:

- `QUICK_START.md` - Guía de inicio rápido
- `migracion/` - Documentación de migración

## Licencia

Añadir licencia aqui