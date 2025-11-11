# 📚 ARCHIVOS DE REFERENCIA - Backend y AWS

## ℹ️ Sobre esta carpeta

Estos archivos contienen información sobre **backend completo, seguridad, testing y deployment**. 

**NO son parte de la migración actual de frontend**, pero están disponibles para cuando decidas implementar:
- Backend Spring Boot
- Base de datos
- Spring Security + JWT
- Deployment en AWS

---

## 📋 Contenido

### Backend
- **08_BACKEND_CONTROLLERS.md** - Controllers REST (AuthController, BeatController, CarritoController, etc.)
- **09_BACKEND_MODELOS_SERVICIOS.md** - JPA Entities, Repositories, Services
- **10_SEGURIDAD_JWT.md** - Spring Security + JWT implementation

### Testing y Deployment
- **11_TESTING_INTEGRACION.md** - Testing backend con JUnit + Spring Boot Test
- **12_DESPLIEGUE_CONFIGURACION.md** - Docker, docker-compose, deployment

### Otros
- **13_CHECKLIST_FUNCIONALIDADES.md** - Checklist exhaustivo con backend incluido
- **00_INDICE_GENERAL.md** - Índice del plan original (backend + frontend)
- **RESUMEN_VISUAL.md** - Diagramas y visualizaciones

---

## 🔮 Cuándo usar estos archivos

### Después de completar la migración frontend:

1. **Paso 1**: Implementar backend Spring Boot
   - Usa `08_BACKEND_CONTROLLERS.md`
   - Usa `09_BACKEND_MODELOS_SERVICIOS.md`
   - Usa `10_SEGURIDAD_JWT.md`

2. **Paso 2**: Testing
   - Usa `11_TESTING_INTEGRACION.md`

3. **Paso 3**: Deployment
   - Usa `12_DESPLIEGUE_CONFIGURACION.md`

---

## ⚠️ Importante

Estos archivos asumen que ya completaste la **migración del frontend**. No los uses hasta que el frontend esté funcionando correctamente.

---

## 🔙 Volver a la migración

➡️ [../README.md](../README.md)
