# 🗄️ MAPEO BASE DE DATOS - MySQL Actual vs Spring Boot

## 🎯 Objetivo
Documentar las diferencias entre la base de datos MySQL actual y las entidades JPA documentadas, y definir la estrategia de adaptación.

---

## 📊 Comparación de Estructuras

### ❌ DIFERENCIAS CRÍTICAS ENCONTRADAS

| Aspecto | MySQL Actual | Spring Boot Documentado | Acción Requerida |
|---------|-------------|-------------------------|-------------------|
| **Nombre BD** | `Fullsound_Base` | `fullsound` | ✅ Ajustar application.properties |
| **Tabla Roles** | `tipo_usuario` | `roles` | ⚠️ RENOMBRAR o adaptar entity |
| **Tabla Usuario** | `usuario` | `usuarios` | ⚠️ RENOMBRAR o adaptar entity |
| **Tabla Beat** | `beat` | `beats` | ⚠️ RENOMBRAR o adaptar entity |
| **Tabla Compra** | `compra` | `pedidos` | ⚠️ RENOMBRAR o adaptar entity |
| **Tabla Compra Detalle** | `compra_detalle` | `pedido_items` | ⚠️ RENOMBRAR o adaptar entity |
| **Tabla Producto** | ❌ NO EXISTE | `productos` | ❌ NO implementar (no requerido) |
| **Tabla Carrito** | ❌ NO EXISTE | `carritos`, `carrito_items` | ❌ NO implementar (no requerido) |
| **Tabla Pagos** | ❌ NO EXISTE | `pagos` | ❌ NO implementar (no requerido) |
| **Tabla Reviews** | ❌ NO EXISTE | `reviews` | ❌ NO implementar (no requerido) |

---

## 🔍 Análisis Detallado por Tabla

### 1️⃣ TIPO_USUARIO vs ROLES

**MySQL Actual:**
```sql
CREATE TABLE tipo_usuario (
    id_tipo_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO tipo_usuario (nombre_tipo) VALUES ('cliente');
INSERT INTO tipo_usuario (nombre_tipo) VALUES ('administrador');
```

**Spring Boot Documentado:**
```java
@Entity
@Table(name = "roles")
public class Rol extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private RolUsuario nombre; // Enum: CLIENTE, PRODUCTOR, ADMIN
}
```

**✅ SOLUCIÓN - Opción 1 (Recomendada): Adaptar Entity a BD Existente**
```java
@Entity
@Table(name = "tipo_usuario") // ✅ Usar tabla existente
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_usuario") // ✅ Coincidir con BD
    private Integer id;
    
    @Column(name = "nombre_tipo", nullable = false, unique = true, length = 50)
    private String nombre; // ✅ String en vez de Enum
    
    @CreatedDate
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
}
```

**✅ SOLUCIÓN - Opción 2: Renombrar Tabla en MySQL**
```sql
ALTER TABLE tipo_usuario RENAME TO roles;
ALTER TABLE tipo_usuario CHANGE id_tipo_usuario id BIGINT;
ALTER TABLE tipo_usuario CHANGE nombre_tipo nombre VARCHAR(20);
```

---

### 2️⃣ USUARIO vs USUARIOS

**MySQL Actual:**
```sql
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    id_tipo_usuario INT DEFAULT 1,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_tipo FOREIGN KEY (id_tipo_usuario) 
        REFERENCES tipo_usuario(id_tipo_usuario)
);
```

**Spring Boot Documentado:**
```java
@Entity
@Table(name = "usuarios")
public class Usuario extends BaseEntity {
    private String username;
    private String email;
    private String password;
    private String nombreCompleto;
    private String telefono;
    // + muchos campos adicionales (biografia, avatar, redes sociales, etc.)
}
```

**✅ SOLUCIÓN ADAPTADA - Mantener BD Actual:**
```java
@Entity
@Table(name = "usuario") // ✅ Tabla existente
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") // ✅ Coincidir con BD
    private Integer id;
    
    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "contrasena", nullable = false, length = 255)
    private String password;
    
    @ManyToOne
    @JoinColumn(name = "id_tipo_usuario") // ✅ FK existente
    private Rol rol;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    // ⚠️ NOTA: Campos adicionales documentados (biografia, avatar, redes) 
    // NO están en BD actual. Opciones:
    // 1. NO agregarlos (mantener BD simple)
    // 2. Agregarlos con ALTER TABLE después
}
```

---

### 3️⃣ BEAT vs BEATS

**MySQL Actual:**
```sql
CREATE TABLE beat (
    id_beat INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    artista VARCHAR(100) NOT NULL,
    genero VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    precio_formateado VARCHAR(50) NOT NULL, -- ⚠️ Campo calculado innecesario
    fuente_audio VARCHAR(255),
    imagen VARCHAR(255),
    enlace_producto VARCHAR(255) NOT NULL, -- ⚠️ Campo calculado innecesario
    usuario_id INT NOT NULL,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_beat_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuario(id_usuario) ON DELETE CASCADE
);
```

**Spring Boot Documentado:**
```java
@Entity
@Table(name = "beats")
public class Beat extends BaseEntity {
    private String titulo;
    private String slug;
    private String descripcion;
    private String genero;
    private Integer bpm;
    private String tonalidad;
    private BigDecimal precio;
    private String urlAudioPreview;
    private String urlImagen;
    // + muchos campos adicionales
}
```

**✅ SOLUCIÓN ADAPTADA - Mantener BD Actual:**
```java
@Entity
@Table(name = "beat") // ✅ Tabla existente (singular)
public class Beat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_beat")
    private Integer id;
    
    @Column(nullable = false, length = 150)
    private String titulo;
    
    @Column(length = 100)
    private String artista; // ✅ Campo existente en BD
    
    @Column(length = 100)
    private String genero;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    // ❌ ELIMINAR precio_formateado - calcularlo en DTO/Mapper
    // @Transient
    // public String getPrecioFormateado() {
    //     return NumberFormat.getCurrencyInstance(new Locale("es", "CO"))
    //         .format(precio);
    // }
    
    @Column(name = "fuente_audio", length = 255)
    private String fuenteAudio; // ✅ Nombre en BD
    
    @Column(length = 255)
    private String imagen;
    
    // ❌ ELIMINAR enlace_producto - calcularlo en @Transient
    @Transient
    public String getEnlaceProducto() {
        return "/producto/" + id;
    }
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario productor; // ✅ Relación existente
    
    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;
    
    // ⚠️ CAMPOS NO PRESENTES EN BD ACTUAL (no agregar):
    // - slug
    // - bpm
    // - tonalidad
    // - mood
    // - tags
    // - precioDescuento
    // - tipoLicencia
    // - estado
    // - urlAudioFull
    // - urlStems
    // - duracionSegundos
    // - reproducciones
    // - descargas
    // - likes
    // - destacado
}
```

---

### 4️⃣ COMPRA vs PEDIDOS

**MySQL Actual:**
```sql
CREATE TABLE compra (
    id_compra INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL,
    iva_total DECIMAL(10,2) NOT NULL,
    total_con_iva DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_compra_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuario(id_usuario) ON DELETE CASCADE
);
```

**Spring Boot Documentado:**
```java
@Entity
@Table(name = "pedidos")
public class Pedido extends BaseEntity {
    private String numeroPedido;
    private Usuario usuario;
    private EstadoPedido estado;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private LocalDateTime fechaPedido;
}
```

**✅ SOLUCIÓN ADAPTADA:**
```java
@Entity
@Table(name = "compra") // ✅ Tabla existente
public class Pedido { // Mantener nombre clase "Pedido"
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "fecha")
    private LocalDateTime fecha;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "iva_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal ivaTotal;
    
    @Column(name = "total_con_iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalConIva;
    
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<PedidoItem> items = new ArrayList<>();
    
    // ⚠️ CAMPOS NO PRESENTES EN BD (no agregar):
    // - numeroPedido
    // - estado (EstadoPedido enum)
    // - descuento
    // - fechaCompletado
    // - notas
}
```

---

### 5️⃣ COMPRA_DETALLE vs PEDIDO_ITEMS

**MySQL Actual:**
```sql
CREATE TABLE compra_detalle (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    compra_id INT NOT NULL,
    beat_id INT NOT NULL,
    precio_base DECIMAL(10,2) NOT NULL,
    iva_monto DECIMAL(10,2) NOT NULL,
    precio_con_iva DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_compra FOREIGN KEY (compra_id) 
        REFERENCES compra(id_compra) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_beat FOREIGN KEY (beat_id) 
        REFERENCES beat(id_beat) ON DELETE CASCADE
);
```

**✅ SOLUCIÓN ADAPTADA:**
```java
@Entity
@Table(name = "compra_detalle") // ✅ Tabla existente
public class PedidoItem { // Mantener nombre clase "PedidoItem"
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "compra_id", nullable = false)
    private Pedido compra; // ⚠️ Variable "compra" pero tipo "Pedido"
    
    @ManyToOne
    @JoinColumn(name = "beat_id", nullable = false)
    private Beat beat;
    
    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;
    
    @Column(name = "iva_monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal ivaMonto;
    
    @Column(name = "precio_con_iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioConIva;
    
    // ⚠️ NOTA: BD actual NO tiene campo "cantidad"
    // Asumiendo cantidad = 1 siempre (1 beat por línea)
    
    @Transient
    public int getCantidad() {
        return 1;
    }
    
    // ⚠️ CAMPOS NO PRESENTES:
    // - producto_id (solo beats, no productos)
    // - cantidad
    // - nombreItem (snapshot)
}
```

---

## 🚨 TABLAS NO PRESENTES EN BD ACTUAL

### ❌ NO Implementar estas Entidades (por ahora):

| Entidad Documentada | Razón | Acción |
|---------------------|-------|---------|
| **Producto** | No existe en BD actual | ⛔ Comentar/eliminar de documentación |
| **Carrito** | No existe en BD actual | ⛔ Comentar/eliminar de documentación |
| **CarritoItem** | No existe en BD actual | ⛔ Comentar/eliminar de documentación |
| **Pago** | No existe en BD actual | ⛔ Comentar/eliminar de documentación |
| **Review** | No existe en BD actual | ⛔ Comentar/eliminar de documentación |

---

## ✅ ESTRATEGIA DE IMPLEMENTACIÓN

### 🎯 Opción A: Adaptar Spring Boot a BD Actual (RECOMENDADO)

**Ventajas:**
- ✅ No modifica BD existente
- ✅ Mantiene datos actuales
- ✅ Migración más rápida
- ✅ Menos riesgo de pérdida de datos

**Archivos a Modificar:**
```
plan/
  ├── 03_ENTIDADES_JPA.md ➜ Actualizar con nombres de tabla reales
  ├── 04_REPOSITORIES.md ➜ Adaptar queries a columnas reales
  ├── 05_DTOS_REQUEST.md ➜ Eliminar DTOs de entidades no existentes
  ├── 06_DTOS_RESPONSE.md ➜ Eliminar DTOs de entidades no existentes
  ├── 07_MAPPERS.md ➜ Adaptar mapeo a campos reales
  ├── 08-09_SERVICES.md ➜ Eliminar servicios de entidades no existentes
  ├── 10_CONTROLLERS.md ➜ Eliminar controllers de entidades no existentes
```

---

### 🎯 Opción B: Migrar BD MySQL a Nuevo Esquema

**Ventajas:**
- ✅ BD más normalizada
- ✅ Soporta funcionalidades avanzadas (carrito, pagos, reviews)
- ✅ Mejor para crecimiento futuro

**Desventajas:**
- ❌ Requiere script de migración
- ❌ Riesgo de pérdida de datos
- ❌ Tiempo de downtime

**Script de Migración:**
```sql
-- 1. Backup
CREATE DATABASE Fullsound_Base_Backup;
mysqldump -u root -p Fullsound_Base > backup_$(date +%Y%m%d).sql

-- 2. Crear nueva BD
CREATE DATABASE fullsound;
USE fullsound;

-- 3. Renombrar tablas
RENAME TABLE Fullsound_Base.tipo_usuario TO fullsound.roles;
RENAME TABLE Fullsound_Base.usuario TO fullsound.usuarios;
RENAME TABLE Fullsound_Base.beat TO fullsound.beats;
RENAME TABLE Fullsound_Base.compra TO fullsound.pedidos;
RENAME TABLE Fullsound_Base.compra_detalle TO fullsound.pedido_items;

-- 4. Renombrar columnas
ALTER TABLE fullsound.roles 
  CHANGE id_tipo_usuario id BIGINT AUTO_INCREMENT,
  CHANGE nombre_tipo nombre VARCHAR(20);

ALTER TABLE fullsound.usuarios
  CHANGE id_usuario id BIGINT AUTO_INCREMENT,
  CHANGE nombre_usuario username VARCHAR(50),
  CHANGE correo email VARCHAR(100),
  CHANGE contrasena password VARCHAR(255),
  CHANGE id_tipo_usuario rol_id BIGINT;

-- ... continuar con todas las tablas
```

---

## 📋 DECISION FINAL RECOMENDADA

### ✅ **OPCIÓN A - Adaptar Spring Boot a BD Actual**

**Motivos:**
1. **Preserva datos existentes** sin riesgo
2. **Migración más rápida** (2-3 días vs 1-2 semanas)
3. **Menos complejidad** inicial
4. **Permite evolución gradual** (agregar tablas después)

### 📝 Próximos Pasos:

1. **PASO 16: Crear documento actualizado `16_ENTIDADES_JPA_ADAPTADAS.md`**
   - ✅ Entidades adaptadas a tabla `tipo_usuario`
   - ✅ Entidades adaptadas a tabla `usuario`
   - ✅ Entidades adaptadas a tabla `beat`
   - ✅ Entidades adaptadas a tabla `compra`
   - ✅ Entidades adaptadas a tabla `compra_detalle`

2. **PASO 17: Actualizar `application.properties`**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/Fullsound_Base
   spring.jpa.hibernate.ddl-auto=validate  # ⚠️ NO crear tablas, solo validar
   ```

3. **PASO 18: Adaptar Repositories**
   - Solo 5 repositories: `RolRepository`, `UsuarioRepository`, `BeatRepository`, `PedidoRepository`, `PedidoItemRepository`

4. **PASO 19: Adaptar DTOs y Services**
   - Eliminar referencias a Producto, Carrito, Pago, Review
   - Mantener solo Beat, Usuario, Pedido (Compra)

---

## ❓ ¿Qué decides?

**A) Adaptar Spring Boot a BD actual** ➜ Continúo creando `16_ENTIDADES_JPA_ADAPTADAS.md`  
**B) Migrar BD a nuevo esquema** ➜ Creo script de migración completo  
**C) Otro enfoque** ➜ Dime qué prefieres  

---

## 📊 Resumen de Cambios

| Elemento | BD Actual | Spring Boot Doc | Solución |
|----------|-----------|----------------|----------|
| **Base de Datos** | `Fullsound_Base` | `fullsound` | Cambiar properties |
| **Tabla Roles** | `tipo_usuario` | `roles` | Usar `@Table(name="tipo_usuario")` |
| **Tabla Usuarios** | `usuario` | `usuarios` | Usar `@Table(name="usuario")` |
| **Tabla Beats** | `beat` | `beats` | Usar `@Table(name="beat")` |
| **Tabla Pedidos** | `compra` | `pedidos` | Usar `@Table(name="compra")` |
| **Tabla Items** | `compra_detalle` | `pedido_items` | Usar `@Table(name="compra_detalle")` |
| **Productos** | ❌ No existe | ✅ Documentado | ⛔ No implementar |
| **Carrito** | ❌ No existe | ✅ Documentado | ⛔ No implementar |
| **Pagos** | ❌ No existe | ✅ Documentado | ⛔ No implementar |
| **Reviews** | ❌ No existe | ✅ Documentado | ⛔ No implementar |

---

**🎯 Estado:** Esperando decisión para continuar con adaptación correspondiente.
