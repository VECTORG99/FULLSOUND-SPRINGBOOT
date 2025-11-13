# 🎵 FULLSOUND BACKEND - Spring Boot 3.5.7

> **API REST completa para marketplace de beats musicales**

**Estado:** ✅ 100% Implementado | ⏳ Pendiente compilación y pruebas

---

## 🚀 INICIO RÁPIDO

```powershell
# 1. Crear base de datos
mysql -u root -p
CREATE DATABASE Fullsound_Base;
source plan/DATABASE_MIGRATION.sql;

# 2. Configurar (editar application.properties si es necesario)
cd Fullsound

# 3. Compilar y ejecutar
mvn clean install
mvn spring-boot:run

# 4. Verificar
# http://localhost:8080/swagger-ui.html
```

---

## 📚 DOCUMENTACIÓN COMPLETA

| Documento | Descripción |
|-----------|-------------|
| **[BACKEND_COMPLETADO.md](../BACKEND_COMPLETADO.md)** | Guía completa de uso |
| **[CONFIGURACION_AMBIENTE.md](../CONFIGURACION_AMBIENTE.md)** | Instalación de herramientas |
| **[CHECKLIST_FINAL.md](../CHECKLIST_FINAL.md)** | Checklist de verificación |
| **[ESTADO_PROYECTO.md](../ESTADO_PROYECTO.md)** | Estado visual completo |

---

## ✨ CARACTERÍSTICAS

- 🔐 Autenticación JWT con Spring Security 6
- 💳 Integración completa con Stripe
- 🎵 CRUD de beats con búsqueda y filtros
- 🛒 Sistema de pedidos y compras
- 👥 Gestión de usuarios con roles
- 📊 Dashboard de administración
- 📚 Documentación automática con Swagger

---

## 🛠️ STACK TÉCNICO

- **Java 17** (LTS)
- **Spring Boot 3.5.7**
- **MySQL 8.0**
- **JWT** (io.jsonwebtoken 0.11.5)
- **Stripe Java SDK 24.3.0**
- **MapStruct 1.5.5**
- **Maven 3.8+**

---

## 📦 CONTENIDO

```
63 archivos Java
~3,500 líneas de código
34 endpoints REST
6 controladores
5 servicios de negocio
6 entidades de dominio
13 DTOs
```

---

## 📡 ENDPOINTS PRINCIPALES

### Autenticación
- `POST /api/auth/register` - Registro
- `POST /api/auth/login` - Login

### Beats
- `GET /api/beats` - Listar
- `GET /api/beats/search?q=trap` - Buscar
- `POST /api/beats/{id}/play` - Reproducir

### Pedidos
- `POST /api/pedidos` - Crear pedido
- `GET /api/pedidos/mis-pedidos` - Mis compras

### Pagos
- `POST /api/pagos/create-intent` - Iniciar pago
- `POST /api/pagos/confirm` - Confirmar pago

Ver todos: http://localhost:8080/swagger-ui.html

---

## ⚙️ REQUISITOS

- ☕ Java 17
- 🏗️ Maven 3.8+
- 🗄️ MySQL 8.0

Ver guía completa: [CONFIGURACION_AMBIENTE.md](../CONFIGURACION_AMBIENTE.md)

---

**Para más información, ver [BACKEND_COMPLETADO.md](../BACKEND_COMPLETADO.md)**
