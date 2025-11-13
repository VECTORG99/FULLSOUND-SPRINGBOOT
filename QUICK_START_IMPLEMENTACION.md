# ⚡ QUICK START - FULLSOUND

> **5 minutos para empezar la implementación**

---

## 🎯 ACCIÓN INMEDIATA

### 1️⃣ Ejecutar Migración SQL (OBLIGATORIO) 🔥

```bash
# Windows PowerShell
cd c:\Users\dh893\Documents\GitHub\FULLSOUND-SPRINGBOOT\plan
Get-Content DATABASE_MIGRATION.sql | mysql -u root -p Fullsound_Base
```

**Verificar:**
```sql
mysql -u root -p
USE Fullsound_Base;
SHOW TABLES;  -- Debe mostrar 7 tablas
DESC beat;    -- Debe tener columna 'slug'
```

---

### 2️⃣ Leer Guía Principal (10 min) 📖

```bash
cat plan/00_RESUMEN_EJECUTIVO.md
```

**Qué encontrarás:**
- Archivos clave priorizados
- Cambios principales
- Próximos pasos
- Puntos críticos

---

### 3️⃣ Comenzar Implementación (sigue el orden) ✅

#### Fase 1: Enumeraciones (15 min)
```bash
# 1. Leer código
cat plan/02_ENUMERACIONES.md

# 2. Crear directorio
cd Fullsound/src/main/java/com/fullsound
mkdir enums

# 3. Copiar 5 archivos:
# - RolUsuario.java
# - EstadoBeat.java
# - EstadoPedido.java
# - MetodoPago.java
# - EstadoPago.java
```

#### Fase 2: Entidades (45 min)
```bash
# 1. Leer código
cat plan/03_ENTIDADES_JPA.md

# 2. Crear directorio
mkdir model

# 3. Copiar 6 archivos:
# - Rol.java
# - Usuario.java
# - Beat.java
# - Pedido.java
# - PedidoItem.java
# - Pago.java
```

#### Fase 3: Repositories (30 min)
```bash
mkdir repository

# Crear 6 interfaces JpaRepository
# Ver ejemplos en plan/00_IMPLEMENTACION_FINAL.md
```

#### Fase 4: Seguir Checklist
```bash
cat plan/CHECKLIST_IMPLEMENTACION.md
# Marcar cada fase completada
```

---

## ⚠️ PUNTOS CRÍTICOS (MEMORIZAR)

### 1. Base de Datos
```
✅ Nombre: Fullsound_Base (con mayúscula)
✅ Ejecutar DATABASE_MIGRATION.sql PRIMERO
✅ Usar spring.jpa.hibernate.ddl-auto=validate
```

### 2. Roles
```java
// ❌ INCORRECTO
@PreAuthorize("hasRole('ROLE_administrador')")

// ✅ CORRECTO
@PreAuthorize("hasRole('administrador')")
```

### 3. IDs
```java
// ❌ INCORRECTO
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

// ✅ CORRECTO
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;
```

### 4. Campos Calculados
```java
// ❌ INCORRECTO (crear columna en BD)
@Column(name = "precio_formateado")
private String precioFormateado;

// ✅ CORRECTO (método @Transient)
@Transient
public String getPrecioFormateado() {
    return String.format("$%,.2f", this.precio);
}
```

---

## 📂 Archivos Esenciales (en orden de uso)

| # | Archivo | Para qué | Cuándo |
|---|---------|----------|--------|
| 🔥 | `plan/DATABASE_MIGRATION.sql` | Migrar BD | **AHORA MISMO** |
| 📖 | `plan/00_RESUMEN_EJECUTIVO.md` | Overview | Después de migrar |
| ✅ | `plan/CHECKLIST_IMPLEMENTACION.md` | Tracking | Durante todo |
| 💻 | `plan/00_IMPLEMENTACION_FINAL.md` | Código | Al implementar |
| 📋 | `plan/02_ENUMERACIONES.md` | Copiar enums | Fase 1 |
| 📋 | `plan/03_ENTIDADES_JPA.md` | Copiar entidades | Fase 2 |
| 🌐 | `README_BACKEND.md` | Ver endpoints | Testing |
| 🗂️ | `plan/15_MAPEO_BASE_DATOS.md` | Entender BD | Si hay dudas |

---

## 🚀 Comandos Frecuentes

### Backend
```bash
cd Fullsound

# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# Tests
mvn test

# Ver logs
tail -f logs/application.log
```

### Frontend
```bash
cd frontend

# Instalar
npm install

# Ejecutar
npm run dev

# Tests
npm test
```

### Base de Datos
```bash
# Conectar
mysql -u root -p

# Ver tablas
USE Fullsound_Base;
SHOW TABLES;

# Ver estructura
DESC beat;
DESC usuario;
DESC pago;

# Ver datos
SELECT * FROM tipo_usuario;
SELECT * FROM beat LIMIT 5;
```

---

## 📊 Checklist Express

### Pre-Implementación
- [ ] MySQL corriendo en puerto 3306
- [ ] BD `Fullsound_Base` existe
- [ ] Java 17+ instalado
- [ ] Maven 3.8+ instalado
- [ ] IDE configurado

### Migración BD
- [ ] Backup creado
- [ ] `DATABASE_MIGRATION.sql` ejecutado
- [ ] 7 tablas verificadas
- [ ] Columna `slug` existe en `beat`
- [ ] Tabla `pago` existe

### Implementación Backend
- [ ] Enumeraciones creadas (5 archivos)
- [ ] Entidades creadas (6 archivos)
- [ ] Repositories creados (6 archivos)
- [ ] Services creados
- [ ] Controllers creados
- [ ] Security configurada
- [ ] Tests pasan

### Verificación
- [ ] `mvn clean compile` sin errores
- [ ] `mvn test` todos pasan
- [ ] Backend arranca sin errores
- [ ] Endpoint `/actuator/health` responde
- [ ] Login funciona
- [ ] Endpoints protegidos requieren JWT

---

## 🆘 Troubleshooting

### Error: "Access denied for user 'root'"
```bash
# Verificar password en application.properties
spring.datasource.password=TU_PASSWORD_AQUI
```

### Error: "Table 'tipo_usuario' doesn't exist"
```bash
# No ejecutaste DATABASE_MIGRATION.sql
mysql -u root -p < plan/DATABASE_MIGRATION.sql
```

### Error: "Port 8080 already in use"
```bash
# Cambiar puerto en application.properties
server.port=8081
```

### Error: "Cannot find symbol: class MapStruct"
```bash
# Instalar dependencias
mvn clean install
```

---

## ⏱️ Timeline Express

| Fase | Tiempo | Acción |
|------|--------|--------|
| **AHORA** | 30 min | Migrar BD |
| Después | 15 min | Leer docs |
| Día 1 | 4 horas | Enums, Entidades, Repos |
| Día 2 | 4 horas | DTOs, Services |
| Día 3 | 4 horas | Controllers, Security |
| Día 4 | 4 horas | Tests, Frontend |
| **TOTAL** | **~17h** | Backend completo |

---

## 🎓 Recursos Útiles

- **Código de ejemplo:** `plan/00_IMPLEMENTACION_FINAL.md`
- **Endpoints API:** `README_BACKEND.md`
- **Mapeo BD:** `plan/15_MAPEO_BASE_DATOS.md`
- **Checklist completo:** `plan/CHECKLIST_IMPLEMENTACION.md`

---

## 📞 ¿Dudas?

1. Revisa `plan/00_RESUMEN_EJECUTIVO.md`
2. Busca en `plan/00_IMPLEMENTACION_FINAL.md`
3. Consulta `README_BACKEND.md`
4. Lee `STATUS_PROYECTO.md`

---

**¡Listo para empezar! 🚀**

```bash
# 1. Migra la BD
mysql -u root -p < plan/DATABASE_MIGRATION.sql

# 2. Lee la guía
cat plan/00_RESUMEN_EJECUTIVO.md

# 3. Comienza
cat plan/CHECKLIST_IMPLEMENTACION.md
```

---

**Última actualización:** 2025-11-13  
**Versión:** 1.0.0
