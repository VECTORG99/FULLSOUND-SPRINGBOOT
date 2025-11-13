# 🗄️ PASO 5-9: Crear Entidades JPA (ADAPTADAS A BD ACTUAL)# 🗄️ PASO 5-12: Crear Entidades JPA



## 🎯 Objetivo## 🎯 Objetivo

Implementar las entidades JPA adaptadas a la estructura de base de datos MySQL existente `Fullsound_Base`.Implementar todas las entidades JPA que representan el modelo de datos del sistema.



------



## 📁 Ubicación Base## 📁 Ubicación Base

``````

Fullsound/src/main/java/Fullsound/Fullsound/model/entity/Fullsound/src/main/java/Fullsound/Fullsound/model/entity/

``````



------



## ⚠️ IMPORTANTE - Adaptaciones a BD Actual## ✅ PASO 5: BaseEntity (Clase Abstracta)



Este documento está **ADAPTADO** a la base de datos MySQL existente con las siguientes tablas:**Archivo:** `BaseEntity.java`

- ✅ `tipo_usuario` (roles)

- ✅ `usuario` (usuarios)```java

- ✅ `beat` (beats)package Fullsound.Fullsound.model.entity;

- ✅ `compra` (pedidos)

- ✅ `compra_detalle` (items de pedidos)import jakarta.persistence.*;

- ✅ `pago` (pagos - será creada por script de migración)import lombok.Getter;

- ✅ `usuario_roles` (relación many-to-many)import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;

**NO se implementarán:**import org.springframework.data.annotation.LastModifiedDate;

- ❌ Productoimport org.springframework.data.jpa.domain.support.AuditingEntityListener;

- ❌ Carrito

- ❌ CarritoItemimport java.time.LocalDateTime;

- ❌ Review

/**

--- * Entidad base que proporciona campos comunes a todas las entidades.

 * Incluye auditoría automática de creación y actualización.

## ✅ PASO 5: Configuración JPA */

@Getter

### 5.1 - JpaConfig.java@Setter

@MappedSuperclass

**Archivo:** `Fullsound/src/main/java/Fullsound/Fullsound/config/JpaConfig.java`@EntityListeners(AuditingEntityListener.class)

public abstract class BaseEntity {

```java    

package Fullsound.Fullsound.config;    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

import org.springframework.context.annotation.Bean;    private Long id;

import org.springframework.context.annotation.Configuration;    

import org.springframework.data.domain.AuditorAware;    @CreatedDate

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;    @Column(name = "created_at", nullable = false, updatable = false)

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;    private LocalDateTime createdAt;

import org.springframework.security.core.Authentication;    

import org.springframework.security.core.context.SecurityContextHolder;    @LastModifiedDate

import org.springframework.transaction.annotation.EnableTransactionManagement;    @Column(name = "updated_at")

    private LocalDateTime updatedAt;

import java.util.Optional;    

    @Column(name = "activo")

@Configuration    private Boolean activo = true;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")    

@EnableJpaRepositories(basePackages = "Fullsound.Fullsound.repository")    @PrePersist

@EnableTransactionManagement    protected void onCreate() {

public class JpaConfig {        if (activo == null) {

                activo = true;

    /**        }

     * Proveedor de auditor para JPA Auditing    }

     * Captura el usuario actual del SecurityContext}

     */```

    @Bean

    public AuditorAware<String> auditorProvider() {**Características:**

        return () -> {- Todas las entidades heredan de esta clase

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();- Auditoría automática con `@CreatedDate` y `@LastModifiedDate`

            if (authentication == null || !authentication.isAuthenticated()) {- Campo `activo` para soft deletes

                return Optional.of("system");- ID autogenerado con estrategia IDENTITY

            }

            return Optional.of(authentication.getName());**⚠️ IMPORTANTE:** Crear también `JpaConfig.java` en el paquete `config`:

        };

    }```java

}package Fullsound.Fullsound.config;

```

import org.springframework.context.annotation.Configuration;

---import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

## ✅ PASO 6: Entidad Rol (tipo_usuario)import org.springframework.transaction.annotation.EnableTransactionManagement;



**Archivo:** `Rol.java`@Configuration

@EnableJpaAuditing

```java@EnableJpaRepositories(basePackages = "Fullsound.Fullsound.repository")

package Fullsound.Fullsound.model.entity;@EnableTransactionManagement

public class JpaConfig {

import jakarta.persistence.*;    // Habilita auditoría automática para BaseEntity

import lombok.AllArgsConstructor;}

import lombok.Getter;```

import lombok.NoArgsConstructor;

import lombok.Setter;---

import org.springframework.data.annotation.CreatedDate;

import org.springframework.data.annotation.LastModifiedDate;## ✅ PASO 6: Entidades de Autenticación (Rol y Usuario)

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

### 6.1 - Entidad Rol

import java.time.LocalDateTime;

**Archivo:** `Rol.java`

/**

 * Entidad ROL - Mapea a tabla tipo_usuario```java

 * Tabla en BD: tipo_usuariopackage Fullsound.Fullsound.model.entity;

 */

@Entityimport Fullsound.Fullsound.model.enums.RolUsuario;

@Table(name = "tipo_usuario")import jakarta.persistence.*;

@EntityListeners(AuditingEntityListener.class)import lombok.AllArgsConstructor;

@Getterimport lombok.Getter;

@Setterimport lombok.NoArgsConstructor;

@NoArgsConstructorimport lombok.Setter;

@AllArgsConstructor

public class Rol {@Entity

    @Table(name = "roles")

    @Id@Getter

    @GeneratedValue(strategy = GenerationType.IDENTITY)@Setter

    @Column(name = "id_tipo_usuario")@NoArgsConstructor

    private Integer id;@AllArgsConstructor

    public class Rol extends BaseEntity {

    @Column(name = "nombre_tipo", nullable = false, unique = true, length = 50)    

    private String nombre; // 'cliente' o 'administrador'    @Enumerated(EnumType.STRING)

        @Column(nullable = false, unique = true, length = 20)

    @Column(length = 100)    private RolUsuario nombre;

    private String descripcion;    

        @Column(length = 100)

    @Column(nullable = false)    private String descripcion;

    private Boolean activo = true;    

        public Rol(RolUsuario nombre) {

    @CreatedDate        this.nombre = nombre;

    @Column(name = "created_at", updatable = false)    }

    private LocalDateTime createdAt;}

    ```

    @LastModifiedDate

    @Column(name = "updated_at")**Tabla generada:**

    private LocalDateTime updatedAt;```sql

    CREATE TABLE roles (

    // Constructor helper    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    public Rol(String nombre) {    nombre VARCHAR(20) NOT NULL UNIQUE,

        this.nombre = nombre;    descripcion VARCHAR(100),

        this.activo = true;    activo BOOLEAN DEFAULT TRUE,

    }    created_at TIMESTAMP NOT NULL,

        updated_at TIMESTAMP

    public Rol(String nombre, String descripcion) {);

        this.nombre = nombre;```

        this.descripcion = descripcion;

        this.activo = true;---

    }

}### 6.2 - Entidad Usuario

```

**Archivo:** `Usuario.java`

---

```java

## ✅ PASO 7: Entidad Usuariopackage Fullsound.Fullsound.model.entity;



**Archivo:** `Usuario.java`import jakarta.persistence.*;

import lombok.AllArgsConstructor;

```javaimport lombok.Getter;

package Fullsound.Fullsound.model.entity;import lombok.NoArgsConstructor;

import lombok.Setter;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;import java.util.HashSet;

import lombok.Getter;import java.util.Set;

import lombok.NoArgsConstructor;

import lombok.Setter;@Entity

import org.springframework.data.annotation.CreatedDate;@Table(name = "usuarios", indexes = {

import org.springframework.data.annotation.LastModifiedDate;    @Index(name = "idx_email", columnList = "email"),

import org.springframework.data.jpa.domain.support.AuditingEntityListener;    @Index(name = "idx_username", columnList = "username")

})

import java.time.LocalDateTime;@Getter

import java.util.HashSet;@Setter

import java.util.Set;@NoArgsConstructor

@AllArgsConstructor

/**public class Usuario extends BaseEntity {

 * Entidad USUARIO - Mapea a tabla usuario    

 * Tabla en BD: usuario    // ==================== INFORMACIÓN BÁSICA ====================

 */    

@Entity    @Column(nullable = false, unique = true, length = 50)

@Table(name = "usuario", indexes = {    private String username;

    @Index(name = "idx_email", columnList = "correo"),    

    @Index(name = "idx_username", columnList = "nombre_usuario"),    @Column(nullable = false, unique = true, length = 100)

    @Index(name = "idx_tipo_usuario", columnList = "id_tipo_usuario")    private String email;

})    

@EntityListeners(AuditingEntityListener.class)    @Column(nullable = false)

@Getter    private String password; // Hash BCrypt

@Setter    

@NoArgsConstructor    @Column(name = "nombre_completo", length = 100)

@AllArgsConstructor    private String nombreCompleto;

public class Usuario {    

        @Column(length = 20)

    @Id    private String telefono;

    @GeneratedValue(strategy = GenerationType.IDENTITY)    

    @Column(name = "id_usuario")    @Column(columnDefinition = "TEXT")

    private Integer id;    private String biografia;

        

    // ==================== INFORMACIÓN BÁSICA ====================    // ==================== IMÁGENES ====================

        

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)    @Column(name = "url_avatar")

    private String username;    private String urlAvatar;

        

    @Column(name = "correo", nullable = false, unique = true, length = 100)    @Column(name = "url_portada")

    private String email;    private String urlPortada;

        

    @Column(name = "contrasena", nullable = false, length = 255)    // ==================== REDES SOCIALES ====================

    private String password; // Hash BCrypt    

        @Column(name = "instagram_url")

    @Column(name = "nombre_completo", length = 100)    private String instagramUrl;

    private String nombreCompleto;    

        @Column(name = "twitter_url")

    @Column(length = 20)    private String twitterUrl;

    private String telefono;    

        @Column(name = "youtube_url")

    @Column(columnDefinition = "TEXT")    private String youtubeUrl;

    private String biografia;    

        @Column(name = "spotify_url")

    // ==================== IMÁGENES ====================    private String spotifyUrl;

        

    @Column(name = "url_avatar")    @Column(name = "soundcloud_url")

    private String urlAvatar;    private String soundcloudUrl;

        

    @Column(name = "url_portada")    // ==================== VERIFICACIÓN Y SEGURIDAD ====================

    private String urlPortada;    

        @Column(name = "email_verificado")

    // ==================== VERIFICACIÓN Y SEGURIDAD ====================    private Boolean emailVerificado = false;

        

    @Column(name = "email_verificado")    @Column(name = "verificacion_token")

    private Boolean emailVerificado = false;    private String verificacionToken;

        

    @Column(name = "verificacion_token")    @Column(name = "reset_token")

    private String verificacionToken;    private String resetToken;

        

    @Column(name = "reset_token")    @Column(name = "reset_token_expiry")

    private String resetToken;    private Long resetTokenExpiry;

        

    @Column(name = "reset_token_expiry")    // ==================== RELACIONES ====================

    private Long resetTokenExpiry;    

        @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})

    // ==================== SOFT DELETE Y AUDITORÍA ====================    @JoinTable(

            name = "usuario_roles",

    @Column(nullable = false)        joinColumns = @JoinColumn(name = "usuario_id"),

    private Boolean activo = true;        inverseJoinColumns = @JoinColumn(name = "rol_id")

        )

    @CreatedDate    private Set<Rol> roles = new HashSet<>();

    @Column(name = "fecha_registro", nullable = false, updatable = false)    

    private LocalDateTime fechaRegistro;    @OneToMany(mappedBy = "productor", cascade = CascadeType.ALL, orphanRemoval = true)

        private Set<Beat> beats = new HashSet<>();

    @LastModifiedDate    

    @Column(name = "updated_at")    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)

    private LocalDateTime updatedAt;    private Carrito carrito;

        

    // ==================== RELACIONES ====================    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)

        private Set<Pedido> pedidos = new HashSet<>();

    /**    

     * Relación Many-to-One con Rol (para compatibilidad con BD actual)    // ==================== MÉTODOS HELPER ====================

     * NOTA: Esta columna se mantiene pero Spring Security usará usuario_roles    

     */    public void addRol(Rol rol) {

    @ManyToOne(fetch = FetchType.EAGER)        this.roles.add(rol);

    @JoinColumn(name = "id_tipo_usuario")    }

    private Rol rol;    

        public void removeRol(Rol rol) {

    /**        this.roles.remove(rol);

     * Relación Many-to-Many con Rol (tabla usuario_roles)    }

     * Esta es la relación principal que usará Spring Security    

     */    public void addBeat(Beat beat) {

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})        beats.add(beat);

    @JoinTable(        beat.setProductor(this);

        name = "usuario_roles",    }

        joinColumns = @JoinColumn(name = "usuario_id"),    

        inverseJoinColumns = @JoinColumn(name = "rol_id")    public void removeBeat(Beat beat) {

    )        beats.remove(beat);

    private Set<Rol> roles = new HashSet<>();        beat.setProductor(null);

        }

    /**}

     * Beats creados por este usuario (si es productor)```

     */

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)**Índices creados:**

    private Set<Beat> beats = new HashSet<>();- `idx_email`: Acelera búsquedas por email (login)

    - `idx_username`: Acelera búsquedas por username

    /**

     * Compras realizadas por este usuario---

     */

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)## ✅ PASO 7: Entidad Beat

    private Set<Pedido> pedidos = new HashSet<>();

    **Archivo:** `Beat.java`

    // ==================== MÉTODOS HELPER ====================

    ```java

    public void addRol(Rol rol) {package Fullsound.Fullsound.model.entity;

        this.roles.add(rol);

    }import Fullsound.Fullsound.model.enums.EstadoBeat;

    import Fullsound.Fullsound.model.enums.TipoLicencia;

    public void removeRol(Rol rol) {import jakarta.persistence.*;

        this.roles.remove(rol);import lombok.AllArgsConstructor;

    }import lombok.Getter;

    import lombok.NoArgsConstructor;

    public void addBeat(Beat beat) {import lombok.Setter;

        beats.add(beat);

        beat.setUsuario(this);import java.math.BigDecimal;

    }import java.util.HashSet;

    import java.util.Set;

    public void removeBeat(Beat beat) {

        beats.remove(beat);@Entity

        beat.setUsuario(null);@Table(name = "beats", indexes = {

    }    @Index(name = "idx_genero", columnList = "genero"),

        @Index(name = "idx_estado", columnList = "estado"),

    /**    @Index(name = "idx_bpm", columnList = "bpm"),

     * Verifica si el usuario tiene rol de administrador    @Index(name = "idx_productor", columnList = "productor_id")

     */})

    public boolean isAdmin() {@Getter

        return roles.stream()@Setter

            .anyMatch(r -> "administrador".equalsIgnoreCase(r.getNombre()));@NoArgsConstructor

    }@AllArgsConstructor

    public class Beat extends BaseEntity {

    /**    

     * Verifica si el usuario es cliente    // ==================== INFORMACIÓN BÁSICA ====================

     */    

    public boolean isCliente() {    @Column(nullable = false, length = 200)

        return roles.stream()    private String titulo;

            .anyMatch(r -> "cliente".equalsIgnoreCase(r.getNombre()));    

    }    @Column(unique = true, nullable = false)

}    private String slug; // URL-friendly: "dark-trap-beat-160bpm"

```    

    @Column(columnDefinition = "TEXT")

---    private String descripcion;

    

## ✅ PASO 8: Entidad Beat    // ==================== PRODUCTOR ====================

    

**Archivo:** `Beat.java`    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "productor_id", nullable = false)

```java    private Usuario productor;

package Fullsound.Fullsound.model.entity;    

    // ==================== CARACTERÍSTICAS MUSICALES ====================

import Fullsound.Fullsound.model.enums.EstadoBeat;    

import jakarta.persistence.*;    @Column(nullable = false, length = 50)

import lombok.AllArgsConstructor;    private String genero; // Trap, Hip-Hop, R&B, etc.

import lombok.Getter;    

import lombok.NoArgsConstructor;    @Column

import lombok.Setter;    private Integer bpm; // Beats por minuto

import org.springframework.data.annotation.CreatedDate;    

import org.springframework.data.annotation.LastModifiedDate;    @Column(length = 20)

import org.springframework.data.jpa.domain.support.AuditingEntityListener;    private String tonalidad; // Ej: "C Minor", "G Major"

    

import java.math.BigDecimal;    @Column(length = 50)

import java.text.NumberFormat;    private String mood; // Ej: "Dark", "Happy", "Energetic"

import java.time.LocalDateTime;    

import java.util.Locale;    @Column(columnDefinition = "TEXT")

    private String tags; // Separados por comas: "dark,trap,808"

/**    

 * Entidad BEAT - Mapea a tabla beat    // ==================== PRECIO Y LICENCIA ====================

 * Tabla en BD: beat    

 */    @Column(nullable = false, precision = 10, scale = 2)

@Entity    private BigDecimal precio;

@Table(name = "beat", indexes = {    

    @Index(name = "idx_genero", columnList = "genero"),    @Column(name = "precio_descuento", precision = 10, scale = 2)

    @Index(name = "idx_estado", columnList = "estado"),    private BigDecimal precioDescuento;

    @Index(name = "idx_bpm", columnList = "bpm"),    

    @Index(name = "idx_usuario", columnList = "usuario_id"),    @Enumerated(EnumType.STRING)

    @Index(name = "idx_slug", columnList = "slug")    @Column(nullable = false, length = 20)

})    private TipoLicencia tipoLicencia = TipoLicencia.BASICA;

@EntityListeners(AuditingEntityListener.class)    

@Getter    @Enumerated(EnumType.STRING)

@Setter    @Column(nullable = false, length = 20)

@NoArgsConstructor    private EstadoBeat estado = EstadoBeat.DISPONIBLE;

@AllArgsConstructor    

public class Beat {    // ==================== ARCHIVOS ====================

        

    @Id    @Column(name = "url_audio_preview", nullable = false)

    @GeneratedValue(strategy = GenerationType.IDENTITY)    private String urlAudioPreview; // MP3 con tag de agua

    @Column(name = "id_beat")    

    private Integer id;    @Column(name = "url_audio_full")

        private String urlAudioFull; // WAV sin tag (descarga después de compra)

    // ==================== INFORMACIÓN BÁSICA ====================    

        @Column(name = "url_stems")

    @Column(nullable = false, length = 150)    private String urlStems; // ZIP con stems (licencia premium/exclusiva)

    private String titulo;    

        @Column(name = "url_imagen")

    @Column(length = 100)    private String urlImagen; // Cover art

    private String artista;    

        @Column(name = "duracion_segundos")

    @Column(unique = true, nullable = false, length = 200)    private Integer duracionSegundos;

    private String slug; // URL-friendly: "dark-trap-beat-160bpm"    

        // ==================== ESTADÍSTICAS ====================

    @Column(columnDefinition = "TEXT")    

    private String descripcion;    @Column(name = "reproducciones")

        private Integer reproducciones = 0;

    // ==================== PRODUCTOR (USUARIO) ====================    

        @Column(name = "descargas")

    @ManyToOne(fetch = FetchType.LAZY)    private Integer descargas = 0;

    @JoinColumn(name = "usuario_id", nullable = false)    

    private Usuario usuario;    @Column(name = "likes")

        private Integer likes = 0;

    // ==================== CARACTERÍSTICAS MUSICALES ====================    

        @Column(name = "destacado")

    @Column(nullable = false, length = 100)    private Boolean destacado = false;

    private String genero; // Trap, Hip-Hop, R&B, etc.    

        // ==================== RELACIONES ====================

    @Column    

    private Integer bpm; // Beats por minuto    @OneToMany(mappedBy = "beat", cascade = CascadeType.ALL, orphanRemoval = true)

        private Set<Review> reviews = new HashSet<>();

    @Column(length = 20)    

    private String tonalidad; // Ej: "C Minor", "G Major"    // ==================== MÉTODOS HELPER ====================

        

    @Column(length = 50)    public void incrementarReproducciones() {

    private String mood; // Ej: "Dark", "Happy", "Energetic"        this.reproducciones = (this.reproducciones == null ? 0 : this.reproducciones) + 1;

        }

    @Column(columnDefinition = "TEXT")    

    private String tags; // Separados por comas: "dark,trap,808"    public void incrementarDescargas() {

            this.descargas = (this.descargas == null ? 0 : this.descargas) + 1;

    // ==================== PRECIO ====================    }

        

    @Column(nullable = false, precision = 10, scale = 2)    public void incrementarLikes() {

    private BigDecimal precio;        this.likes = (this.likes == null ? 0 : this.likes) + 1;

        }

    // ==================== ESTADO ====================    

        public void decrementarLikes() {

    @Enumerated(EnumType.STRING)        this.likes = (this.likes == null ? 0 : this.likes) - 1;

    @Column(nullable = false, length = 20)        if (this.likes < 0) this.likes = 0;

    private EstadoBeat estado = EstadoBeat.DISPONIBLE;    }

        

    // ==================== ARCHIVOS ====================    public BigDecimal getPrecioFinal() {

            return precioDescuento != null ? precioDescuento : precio;

    @Column(name = "url_audio_preview", length = 255)    }

    private String urlAudioPreview; // MP3 con tag de agua (antes: fuente_audio)    

        public boolean tieneDescuento() {

    @Column(name = "url_audio_full", length = 255)        return precioDescuento != null && precioDescuento.compareTo(precio) < 0;

    private String urlAudioFull; // WAV sin tag (descarga después de compra)    }

    }

    @Column(name = "url_stems", length = 255)```

    private String urlStems; // ZIP con stems (licencia premium/exclusiva)

    **Índices para búsqueda rápida:**

    @Column(name = "url_imagen", length = 255)- `idx_genero`: Filtrar por género

    private String urlImagen; // Cover art (antes: imagen)- `idx_estado`: Filtrar por estado

    - `idx_bpm`: Filtrar por BPM

    @Column(name = "duracion_segundos")- `idx_productor`: Listar beats de un productor

    private Integer duracionSegundos;

    ---

    // ==================== ESTADÍSTICAS ====================

    ## ✅ PASO 8: Entidad Producto

    @Column(name = "reproducciones")

    private Integer reproducciones = 0;**Archivo:** `Producto.java`

    

    @Column(name = "descargas")```java

    private Integer descargas = 0;package Fullsound.Fullsound.model.entity;

    

    @Column(name = "likes")import Fullsound.Fullsound.model.enums.CategoriaProducto;

    private Integer likes = 0;import jakarta.persistence.*;

    import lombok.AllArgsConstructor;

    @Column(name = "destacado")import lombok.Getter;

    private Boolean destacado = false;import lombok.NoArgsConstructor;

    import lombok.Setter;

    // ==================== SOFT DELETE Y AUDITORÍA ====================

    import java.math.BigDecimal;

    @Column(nullable = false)

    private Boolean activo = true;@Entity

    @Table(name = "productos", indexes = {

    @CreatedDate    @Index(name = "idx_categoria", columnList = "categoria"),

    @Column(name = "created_at", updatable = false)    @Index(name = "idx_precio", columnList = "precio")

    private LocalDateTime createdAt;})

    @Getter

    @LastModifiedDate@Setter

    @Column(name = "updated_at")@NoArgsConstructor

    private LocalDateTime updatedAt;@AllArgsConstructor

    public class Producto extends BaseEntity {

    // ==================== MÉTODOS HELPER ====================    

        // ==================== INFORMACIÓN BÁSICA ====================

    /**    

     * Calcula el precio formateado (reemplaza columna eliminada)    @Column(nullable = false, length = 200)

     */    private String nombre;

    @Transient    

    public String getPrecioFormateado() {    @Column(unique = true, nullable = false)

        if (precio == null) return "$0";    private String slug;

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));    

        return format.format(precio).replace(",00", "");    @Column(columnDefinition = "TEXT")

    }    private String descripcion;

        

    /**    // ==================== CLASIFICACIÓN ====================

     * Calcula el enlace del producto (reemplaza columna eliminada)    

     */    @Enumerated(EnumType.STRING)

    @Transient    @Column(nullable = false, length = 30)

    public String getEnlaceProducto() {    private CategoriaProducto categoria;

        return "/producto/" + id;    

    }    // ==================== PRECIO ====================

        

    /**    @Column(nullable = false, precision = 10, scale = 2)

     * Incrementa el contador de reproducciones    private BigDecimal precio;

     */    

    public void incrementarReproducciones() {    @Column(name = "precio_descuento", precision = 10, scale = 2)

        this.reproducciones = (this.reproducciones == null ? 0 : this.reproducciones) + 1;    private BigDecimal precioDescuento;

    }    

        // ==================== INVENTARIO ====================

    /**    

     * Incrementa el contador de descargas    @Column

     */    private Integer stock;

    public void incrementarDescargas() {    

        this.descargas = (this.descargas == null ? 0 : this.descargas) + 1;    @Column(name = "stock_ilimitado")

    }    private Boolean stockIlimitado = false; // Para productos digitales

        

    /**    // ==================== ARCHIVOS ====================

     * Incrementa el contador de likes    

     */    @Column(name = "url_imagen")

    public void incrementarLikes() {    private String urlImagen;

        this.likes = (this.likes == null ? 0 : this.likes) + 1;    

    }    @Column(name = "url_archivo")

        private String urlArchivo; // Archivo descargable (productos digitales)

    /**    

     * Decrementa el contador de likes    @Column(name = "url_demo")

     */    private String urlDemo; // Preview o demo

    public void decrementarLikes() {    

        this.likes = (this.likes == null ? 0 : this.likes) - 1;    // ==================== ESTADÍSTICAS ====================

        if (this.likes < 0) this.likes = 0;    

    }    @Column(name = "destacado")

        private Boolean destacado = false;

    /**    

     * Verifica si el beat puede ser comprado    @Column(name = "ventas_totales")

     */    private Integer ventasTotales = 0;

    public boolean esComprable() {    

        return activo && estado != null && estado.esComprable();    // ==================== METADATA ====================

    }    

}    @Column(columnDefinition = "TEXT")

```    private String especificaciones; // JSON con specs del producto

    

---    // ==================== MÉTODOS HELPER ====================

    

## ✅ PASO 9: Entidades de Pedido (Compra)    public BigDecimal getPrecioFinal() {

        return precioDescuento != null ? precioDescuento : precio;

### 9.1 - Entidad Pedido (Compra)    }

    

**Archivo:** `Pedido.java`    public boolean tieneDescuento() {

        return precioDescuento != null && precioDescuento.compareTo(precio) < 0;

```java    }

package Fullsound.Fullsound.model.entity;    

    public boolean hayStock() {

import Fullsound.Fullsound.model.enums.EstadoPedido;        return stockIlimitado || (stock != null && stock > 0);

import jakarta.persistence.*;    }

import lombok.AllArgsConstructor;    

import lombok.Getter;    public void decrementarStock(int cantidad) {

import lombok.NoArgsConstructor;        if (!stockIlimitado && stock != null) {

import lombok.Setter;            stock -= cantidad;

import org.springframework.data.annotation.CreatedDate;            if (stock < 0) stock = 0;

import org.springframework.data.annotation.LastModifiedDate;        }

import org.springframework.data.jpa.domain.support.AuditingEntityListener;    }

    

import java.math.BigDecimal;    public void incrementarVentas() {

import java.time.LocalDateTime;        this.ventasTotales = (this.ventasTotales == null ? 0 : this.ventasTotales) + 1;

import java.util.ArrayList;    }

import java.util.List;}

```

/**

 * Entidad PEDIDO - Mapea a tabla compra---

 * Tabla en BD: compra

 */## ✅ PASO 9: Entidades de Carrito

@Entity

@Table(name = "compra", indexes = {### 9.1 - Entidad Carrito

    @Index(name = "idx_usuario_compra", columnList = "usuario_id"),

    @Index(name = "idx_estado_compra", columnList = "estado"),**Archivo:** `Carrito.java`

    @Index(name = "idx_fecha_compra", columnList = "created_at"),

    @Index(name = "idx_numero_pedido", columnList = "numero_pedido")```java

})package Fullsound.Fullsound.model.entity;

@EntityListeners(AuditingEntityListener.class)

@Getterimport jakarta.persistence.*;

@Setterimport lombok.AllArgsConstructor;

@NoArgsConstructorimport lombok.Getter;

@AllArgsConstructorimport lombok.NoArgsConstructor;

public class Pedido {import lombok.Setter;

    

    @Idimport java.math.BigDecimal;

    @GeneratedValue(strategy = GenerationType.IDENTITY)import java.util.ArrayList;

    @Column(name = "id_compra")import java.util.List;

    private Integer id;

    @Entity

    @Column(name = "numero_pedido", nullable = false, unique = true, length = 50)@Table(name = "carritos")

    private String numeroPedido; // Ej: "FS-2025-000001"@Getter

    @Setter

    // ==================== USUARIO ====================@NoArgsConstructor

    @AllArgsConstructor

    @ManyToOne(fetch = FetchType.LAZY)public class Carrito extends BaseEntity {

    @JoinColumn(name = "usuario_id", nullable = false)    

    private Usuario usuario;    @OneToOne(fetch = FetchType.LAZY)

        @JoinColumn(name = "usuario_id", nullable = false, unique = true)

    // ==================== ESTADO ====================    private Usuario usuario;

        

    @Enumerated(EnumType.STRING)    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)

    @Column(nullable = false, length = 20)    private List<CarritoItem> items = new ArrayList<>();

    private EstadoPedido estado = EstadoPedido.COMPLETADO;    

        @Column(nullable = false, precision = 10, scale = 2)

    // ==================== MONTOS ====================    private BigDecimal total = BigDecimal.ZERO;

        

    @Column(nullable = false, precision = 10, scale = 2)    // ==================== MÉTODOS HELPER ====================

    private BigDecimal subtotal;    

        public void addItem(CarritoItem item) {

    @Column(name = "iva_total", nullable = false, precision = 10, scale = 2)        items.add(item);

    private BigDecimal ivaTotal;        item.setCarrito(this);

            calcularTotal();

    @Column(name = "total_con_iva", nullable = false, precision = 10, scale = 2)    }

    private BigDecimal totalConIva;    

        public void removeItem(CarritoItem item) {

    // ==================== FECHAS ====================        items.remove(item);

            item.setCarrito(null);

    @CreatedDate        calcularTotal();

    @Column(name = "created_at", nullable = false, updatable = false)    }

    private LocalDateTime createdAt;    

        public void limpiar() {

    @Column(name = "fecha_completado")        items.clear();

    private LocalDateTime fechaCompletado;        total = BigDecimal.ZERO;

        }

    @LastModifiedDate    

    @Column(name = "updated_at")    public void calcularTotal() {

    private LocalDateTime updatedAt;        total = items.stream()

                .map(CarritoItem::getSubtotal)

    // ==================== NOTAS Y ESTADO ====================            .reduce(BigDecimal.ZERO, BigDecimal::add);

        }

    @Column(columnDefinition = "TEXT")    

    private String notas;    public int getCantidadItems() {

            return items.stream()

    @Column(nullable = false)            .mapToInt(CarritoItem::getCantidad)

    private Boolean activo = true;            .sum();

        }

    // ==================== RELACIONES ====================}

    ```

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<PedidoItem> items = new ArrayList<>();---

    

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)### 9.2 - Entidad CarritoItem

    private Pago pago;

    **Archivo:** `CarritoItem.java`

    // ==================== LIFECYCLE HOOKS ====================

    ```java

    @PrePersistpackage Fullsound.Fullsound.model.entity;

    private void generarNumeroPedido() {

        if (numeroPedido == null) {import jakarta.persistence.*;

            // Generar formato: FS-YYYY-NNNNNNimport lombok.AllArgsConstructor;

            int year = LocalDateTime.now().getYear();import lombok.Getter;

            numeroPedido = String.format("FS-%d-%06d", year, (id != null ? id : 1));import lombok.NoArgsConstructor;

        }import lombok.Setter;

    }

    import java.math.BigDecimal;

    // ==================== MÉTODOS HELPER ====================

    @Entity

    public void addItem(PedidoItem item) {@Table(name = "carrito_items")

        items.add(item);@Getter

        item.setPedido(this);@Setter

    }@NoArgsConstructor

    @AllArgsConstructor

    public void removeItem(PedidoItem item) {public class CarritoItem extends BaseEntity {

        items.remove(item);    

        item.setPedido(null);    @ManyToOne(fetch = FetchType.LAZY)

    }    @JoinColumn(name = "carrito_id", nullable = false)

        private Carrito carrito;

    public void marcarComoCompletado() {    

        this.estado = EstadoPedido.COMPLETADO;    // IMPORTANTE: Un item puede ser Beat O Producto (no ambos)

        this.fechaCompletado = LocalDateTime.now();    @ManyToOne(fetch = FetchType.LAZY)

    }    @JoinColumn(name = "beat_id")

        private Beat beat;

    public boolean estaPagado() {    

        return pago != null && pago.isExitoso();    @ManyToOne(fetch = FetchType.LAZY)

    }    @JoinColumn(name = "producto_id")

        private Producto producto;

    public int getCantidadItems() {    

        return items.stream()    @Column(nullable = false)

            .mapToInt(PedidoItem::getCantidad)    private Integer cantidad = 1;

            .sum();    

    }    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)

}    private BigDecimal precioUnitario;

```    

    @Column(nullable = false, precision = 10, scale = 2)

---    private BigDecimal subtotal;

    

### 9.2 - Entidad PedidoItem (CompraDetalle)    // ==================== LIFECYCLE HOOKS ====================

    

**Archivo:** `PedidoItem.java`    @PrePersist

    @PreUpdate

```java    private void calcularSubtotal() {

package Fullsound.Fullsound.model.entity;        if (precioUnitario != null && cantidad != null) {

            subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

import jakarta.persistence.*;        }

import lombok.AllArgsConstructor;    }

import lombok.Getter;    

import lombok.NoArgsConstructor;    // ==================== MÉTODOS HELPER ====================

import lombok.Setter;    

import org.springframework.data.annotation.CreatedDate;    public void incrementarCantidad() {

import org.springframework.data.annotation.LastModifiedDate;        this.cantidad++;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;        calcularSubtotal();

    }

import java.math.BigDecimal;    

import java.time.LocalDateTime;    public void decrementarCantidad() {

        if (this.cantidad > 1) {

/**            this.cantidad--;

 * Entidad PEDIDO ITEM - Mapea a tabla compra_detalle            calcularSubtotal();

 * Tabla en BD: compra_detalle        }

 */    }

@Entity    

@Table(name = "compra_detalle", indexes = {    public String getNombreItem() {

    @Index(name = "idx_compra", columnList = "compra_id"),        if (beat != null) return beat.getTitulo();

    @Index(name = "idx_beat", columnList = "beat_id")        if (producto != null) return producto.getNombre();

})        return "Item desconocido";

@EntityListeners(AuditingEntityListener.class)    }

@Getter}

@Setter```

@NoArgsConstructor

@AllArgsConstructor---

public class PedidoItem {

    ## ✅ PASO 10: Entidades de Pedido

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)### 10.1 - Entidad Pedido

    @Column(name = "id_detalle")

    private Integer id;**Archivo:** `Pedido.java`

    

    // ==================== RELACIONES ====================```java

    package Fullsound.Fullsound.model.entity;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "compra_id", nullable = false)import Fullsound.Fullsound.model.enums.EstadoPedido;

    private Pedido pedido;import jakarta.persistence.*;

    import lombok.AllArgsConstructor;

    @ManyToOne(fetch = FetchType.LAZY)import lombok.Getter;

    @JoinColumn(name = "beat_id", nullable = false)import lombok.NoArgsConstructor;

    private Beat beat;import lombok.Setter;

    

    // ==================== INFORMACIÓN DE COMPRA ====================import java.math.BigDecimal;

    import java.time.LocalDateTime;

    @Column(nullable = false)import java.util.ArrayList;

    private Integer cantidad = 1;import java.util.List;

    import java.util.UUID;

    @Column(name = "nombre_item", length = 150)

    private String nombreItem; // Snapshot del nombre del beat@Entity

    @Table(name = "pedidos", indexes = {

    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)    @Index(name = "idx_usuario", columnList = "usuario_id"),

    private BigDecimal precioBase;    @Index(name = "idx_estado", columnList = "estado"),

        @Index(name = "idx_fecha", columnList = "fecha_pedido")

    @Column(name = "iva_monto", nullable = false, precision = 10, scale = 2)})

    private BigDecimal ivaMonto;@Getter

    @Setter

    @Column(name = "precio_con_iva", nullable = false, precision = 10, scale = 2)@NoArgsConstructor

    private BigDecimal precioConIva;@AllArgsConstructor

    public class Pedido extends BaseEntity {

    // ==================== SOFT DELETE Y AUDITORÍA ====================    

        @Column(name = "numero_pedido", nullable = false, unique = true)

    @Column(nullable = false)    private String numeroPedido; // Ej: "FS-A1B2C3D4"

    private Boolean activo = true;    

        @ManyToOne(fetch = FetchType.LAZY)

    @CreatedDate    @JoinColumn(name = "usuario_id", nullable = false)

    @Column(name = "created_at", updatable = false)    private Usuario usuario;

    private LocalDateTime createdAt;    

        @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)

    @LastModifiedDate    private List<PedidoItem> items = new ArrayList<>();

    @Column(name = "updated_at")    

    private LocalDateTime updatedAt;    @Enumerated(EnumType.STRING)

        @Column(nullable = false, length = 20)

    // ==================== LIFECYCLE HOOKS ====================    private EstadoPedido estado = EstadoPedido.PENDIENTE;

        

    @PrePersist    // ==================== MONTOS ====================

    @PreUpdate    

    private void calcularMontos() {    @Column(nullable = false, precision = 10, scale = 2)

        // Guardar snapshot del nombre    private BigDecimal subtotal;

        if (nombreItem == null && beat != null) {    

            nombreItem = beat.getTitulo();    @Column(precision = 10, scale = 2)

        }    private BigDecimal impuestos = BigDecimal.ZERO;

            

        // Calcular precio con IVA si no está seteado    @Column(precision = 10, scale = 2)

        if (precioConIva == null && precioBase != null && ivaMonto != null) {    private BigDecimal descuento = BigDecimal.ZERO;

            precioConIva = precioBase.add(ivaMonto);    

        }    @Column(nullable = false, precision = 10, scale = 2)

    }    private BigDecimal total;

        

    // ==================== MÉTODOS HELPER ====================    // ==================== FECHAS ====================

        

    /**    @Column(name = "fecha_pedido", nullable = false)

     * Calcula el subtotal de este item    private LocalDateTime fechaPedido;

     */    

    @Transient    @Column(name = "fecha_completado")

    public BigDecimal getSubtotal() {    private LocalDateTime fechaCompletado;

        if (precioBase != null && cantidad != null) {    

            return precioBase.multiply(BigDecimal.valueOf(cantidad));    // ==================== RELACIONES ====================

        }    

        return BigDecimal.ZERO;    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)

    }    private Pago pago;

}    

```    @Column(columnDefinition = "TEXT")

    private String notas;

---    

    // ==================== LIFECYCLE HOOKS ====================

## ✅ PASO 10: Entidad Pago    

    @PrePersist

**Archivo:** `Pago.java`    private void generarNumeroPedido() {

        if (numeroPedido == null) {

```java            numeroPedido = "FS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

package Fullsound.Fullsound.model.entity;        }

        if (fechaPedido == null) {

import Fullsound.Fullsound.model.enums.EstadoPago;            fechaPedido = LocalDateTime.now();

import Fullsound.Fullsound.model.enums.MetodoPago;        }

import jakarta.persistence.*;    }

import lombok.AllArgsConstructor;    

import lombok.Getter;    // ==================== MÉTODOS HELPER ====================

import lombok.NoArgsConstructor;    

import lombok.Setter;    public void addItem(PedidoItem item) {

import org.springframework.data.annotation.CreatedDate;        items.add(item);

import org.springframework.data.annotation.LastModifiedDate;        item.setPedido(this);

import org.springframework.data.jpa.domain.support.AuditingEntityListener;    }

    

import java.math.BigDecimal;    public void removeItem(PedidoItem item) {

import java.time.LocalDateTime;        items.remove(item);

        item.setPedido(null);

/**    }

 * Entidad PAGO - Mapea a tabla pago    

 * Tabla en BD: pago (creada por script de migración)    public void calcularTotal() {

 */        subtotal = items.stream()

@Entity            .map(PedidoItem::getSubtotal)

@Table(name = "pago", indexes = {            .reduce(BigDecimal.ZERO, BigDecimal::add);

    @Index(name = "idx_compra_pago", columnList = "compra_id"),        

    @Index(name = "idx_stripe_payment_intent", columnList = "stripe_payment_intent_id"),        total = subtotal

    @Index(name = "idx_estado_pago", columnList = "estado_pago")            .add(impuestos != null ? impuestos : BigDecimal.ZERO)

})            .subtract(descuento != null ? descuento : BigDecimal.ZERO);

@EntityListeners(AuditingEntityListener.class)    }

@Getter    

@Setter    public void marcarComoCompletado() {

@NoArgsConstructor        this.estado = EstadoPedido.COMPLETADO;

@AllArgsConstructor        this.fechaCompletado = LocalDateTime.now();

public class Pago {    }

        

    @Id    public boolean estaPagado() {

    @GeneratedValue(strategy = GenerationType.IDENTITY)        return pago != null && pago.isExitoso();

    @Column(name = "id_pago")    }

    private Integer id;}

    ```

    // ==================== RELACIÓN CON PEDIDO ====================

    ---

    @OneToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "compra_id", nullable = false, unique = true)### 10.2 - Entidad PedidoItem

    private Pedido pedido;

    **Archivo:** `PedidoItem.java`

    // ==================== MÉTODO Y ESTADO ====================

    ```java

    @Enumerated(EnumType.STRING)package Fullsound.Fullsound.model.entity;

    @Column(name = "metodo_pago", nullable = false, length = 20)

    private MetodoPago metodoPago = MetodoPago.STRIPE;import jakarta.persistence.*;

    import lombok.AllArgsConstructor;

    @Enumerated(EnumType.STRING)import lombok.Getter;

    @Column(name = "estado_pago", nullable = false, length = 20)import lombok.NoArgsConstructor;

    private EstadoPago estadoPago = EstadoPago.PENDIENTE;import lombok.Setter;

    

    // ==================== MONTOS ====================import java.math.BigDecimal;

    

    @Column(nullable = false, precision = 10, scale = 2)@Entity

    private BigDecimal monto;@Table(name = "pedido_items")

    @Getter

    @Column(length = 10)@Setter

    private String moneda = "USD";@NoArgsConstructor

    @AllArgsConstructor

    // ==================== STRIPE ====================public class PedidoItem extends BaseEntity {

        

    @Column(name = "stripe_payment_intent_id", unique = true)    @ManyToOne(fetch = FetchType.LAZY)

    private String stripePaymentIntentId;    @JoinColumn(name = "pedido_id", nullable = false)

        private Pedido pedido;

    @Column(name = "stripe_charge_id")    

    private String stripeChargeId;    @ManyToOne(fetch = FetchType.LAZY)

        @JoinColumn(name = "beat_id")

    // ==================== FECHAS Y DETALLES ====================    private Beat beat;

        

    @Column(name = "fecha_pago")    @ManyToOne(fetch = FetchType.LAZY)

    private LocalDateTime fechaPago;    @JoinColumn(name = "producto_id")

        private Producto producto;

    @Column(columnDefinition = "TEXT")    

    private String detalles; // JSON con detalles adicionales    @Column(nullable = false)

        private Integer cantidad;

    @Column(name = "mensaje_error", columnDefinition = "TEXT")    

    private String mensajeError;    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)

        private BigDecimal precioUnitario;

    // ==================== SOFT DELETE Y AUDITORÍA ====================    

        @Column(nullable = false, precision = 10, scale = 2)

    @Column(nullable = false)    private BigDecimal subtotal;

    private Boolean activo = true;    

        // Snapshot del nombre por si se elimina el beat/producto

    @CreatedDate    @Column(name = "nombre_item", nullable = false)

    @Column(name = "created_at", updatable = false)    private String nombreItem;

    private LocalDateTime createdAt;    

        @PrePersist

    @LastModifiedDate    @PreUpdate

    @Column(name = "updated_at")    private void calcularSubtotal() {

    private LocalDateTime updatedAt;        if (precioUnitario != null && cantidad != null) {

                subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

    // ==================== MÉTODOS HELPER ====================        }

        }

    public boolean isExitoso() {}

        return estadoPago == EstadoPago.EXITOSO;```

    }

    ---

    public boolean isPendiente() {

        return estadoPago == EstadoPago.PENDIENTE || estadoPago == EstadoPago.PROCESANDO;## ✅ PASO 11: Entidad Pago

    }

    **Archivo:** `Pago.java`

    public boolean isFallido() {

        return estadoPago == EstadoPago.FALLIDO;```java

    }package Fullsound.Fullsound.model.entity;

    

    public void marcarComoExitoso() {import Fullsound.Fullsound.model.enums.EstadoPago;

        this.estadoPago = EstadoPago.EXITOSO;import Fullsound.Fullsound.model.enums.MetodoPago;

        this.fechaPago = LocalDateTime.now();import jakarta.persistence.*;

    }import lombok.AllArgsConstructor;

    import lombok.Getter;

    public void marcarComoFallido(String error) {import lombok.NoArgsConstructor;

        this.estadoPago = EstadoPago.FALLIDO;import lombok.Setter;

        this.mensajeError = error;

    }import java.math.BigDecimal;

}import java.time.LocalDateTime;

```

@Entity

---@Table(name = "pagos", indexes = {

    @Index(name = "idx_stripe_payment_intent", columnList = "stripe_payment_intent_id"),

## 🧪 Verificación    @Index(name = "idx_estado_pago", columnList = "estado_pago")

})

### Compilar Entidades@Getter

@Setter

```bash@NoArgsConstructor

cd Fullsound@AllArgsConstructor

mvn clean compilepublic class Pago extends BaseEntity {

```    

    @OneToOne(fetch = FetchType.LAZY)

### Verificar Tablas en MySQL    @JoinColumn(name = "pedido_id", nullable = false, unique = true)

    private Pedido pedido;

```sql    

mysql -u root -p    @Enumerated(EnumType.STRING)

USE Fullsound_Base;    @Column(name = "metodo_pago", nullable = false, length = 20)

SHOW TABLES;    private MetodoPago metodoPago;

```    

    @Enumerated(EnumType.STRING)

**Tablas esperadas:**    @Column(name = "estado_pago", nullable = false, length = 20)

```    private EstadoPago estadoPago = EstadoPago.PENDIENTE;

+------------------------+    

| Tables_in_Fullsound_Base |    @Column(nullable = false, precision = 10, scale = 2)

+------------------------+    private BigDecimal monto;

| beat                   |    

| compra                 |    @Column(length = 10)

| compra_detalle         |    private String moneda = "USD";

| pago                   |    

| tipo_usuario           |    // ==================== STRIPE ====================

| usuario                |    

| usuario_roles          |    @Column(name = "stripe_payment_intent_id", unique = true)

+------------------------+    private String stripePaymentIntentId;

```    

    @Column(name = "stripe_charge_id")

### Verificar Estructura de Tabla    private String stripeChargeId;

    

```sql    // ==================== FECHAS Y DETALLES ====================

DESCRIBE usuario;    

DESCRIBE beat;    @Column(name = "fecha_pago")

DESCRIBE compra;    private LocalDateTime fechaPago;

DESCRIBE pago;    

```    @Column(columnDefinition = "TEXT")

    private String detalles; // JSON con detalles adicionales

---    

    @Column(name = "mensaje_error", columnDefinition = "TEXT")

## 📋 Checklist PASO 5-10    private String mensajeError;

    

- [ ] JpaConfig.java creado en config/    // ==================== MÉTODOS HELPER ====================

- [ ] Rol.java creado (mapea a tipo_usuario)    

- [ ] Usuario.java creado (mapea a usuario)    public boolean isExitoso() {

- [ ] Beat.java creado (mapea a beat)        return estadoPago == EstadoPago.EXITOSO;

- [ ] Pedido.java creado (mapea a compra)    }

- [ ] PedidoItem.java creado (mapea a compra_detalle)    

- [ ] Pago.java creado (mapea a pago)    public boolean isPendiente() {

- [ ] Todas las entidades compilan sin errores        return estadoPago == EstadoPago.PENDIENTE || estadoPago == EstadoPago.PROCESANDO;

- [ ] Relaciones funcionando correctamente    }

- [ ] Índices creados en BD    

    public boolean isFallido() {

---        return estadoPago == EstadoPago.FALLIDO;

    }

## ⏭️ Siguiente Paso    

    public void marcarComoExitoso() {

Continuar con **[04_REPOSITORIES.md](04_REPOSITORIES.md)** - PASO 11-15 (Repositories adaptados)        this.estadoPago = EstadoPago.EXITOSO;

        this.fechaPago = LocalDateTime.now();

---    }

    

## 📝 Cambios vs Documentación Original    public void marcarComoFallido(String error) {

        this.estadoPago = EstadoPago.FALLIDO;

| Aspecto | Original | Adaptado | Razón |        this.mensajeError = error;

|---------|----------|----------|-------|    }

| **Tabla Rol** | `roles` | `tipo_usuario` | Adaptar a BD actual |}

| **Tabla Usuario** | `usuarios` | `usuario` | Adaptar a BD actual |```

| **Tabla Beat** | `beats` | `beat` | Adaptar a BD actual |

| **Tabla Pedido** | `pedidos` | `compra` | Adaptar a BD actual |---

| **Tabla PedidoItem** | `pedido_items` | `compra_detalle` | Adaptar a BD actual |

| **Producto** | ✅ Incluido | ❌ Eliminado | No existe en BD |## ✅ PASO 12: Entidad Review

| **Carrito** | ✅ Incluido | ❌ Eliminado | No existe en BD |

| **Review** | ✅ Incluido | ❌ Eliminado | No existe en BD |**Archivo:** `Review.java`

| **precio_formateado** | Columna BD | @Transient method | Cálculo en runtime |

| **enlace_producto** | Columna BD | @Transient method | Cálculo en runtime |```java

package Fullsound.Fullsound.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_beat", columnList = "beat_id"),
    @Index(name = "idx_usuario", columnList = "usuario_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beat_id", nullable = false)
    private Beat beat;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false)
    private Integer rating; // 1-5 estrellas
    
    @Column(columnDefinition = "TEXT")
    private String comentario;
    
    @Column(name = "verificado")
    private Boolean verificado = false; // Si el usuario compró el beat
    
    @PrePersist
    @PreUpdate
    private void validarRating() {
        if (rating < 1) rating = 1;
        if (rating > 5) rating = 5;
    }
}
```

---

## 🧪 Verificación

### Compilar y Ejecutar

```bash
cd Fullsound
mvn clean compile
mvn spring-boot:run
```

### Verificar Tablas en MySQL

```sql
mysql -u root -p
USE fullsound;
SHOW TABLES;
```

**Tablas esperadas (11 tablas):**
```
+----------------------+
| Tables_in_fullsound  |
+----------------------+
| beats                |
| carrito_items        |
| carritos             |
| pagos                |
| pedido_items         |
| pedidos              |
| productos            |
| reviews              |
| roles                |
| usuario_roles        |
| usuarios             |
+----------------------+
```

### Verificar Estructura de Tabla

```sql
DESCRIBE usuarios;
DESCRIBE beats;
DESCRIBE pedidos;
```

---

## 📋 Checklist PASO 5-12

- [ ] BaseEntity.java creado
- [ ] JpaConfig.java creado en config/
- [ ] Rol.java creado
- [ ] Usuario.java creado
- [ ] Beat.java creado
- [ ] Producto.java creado
- [ ] Carrito.java creado
- [ ] CarritoItem.java creado
- [ ] Pedido.java creado
- [ ] PedidoItem.java creado
- [ ] Pago.java creado
- [ ] Review.java creado
- [ ] Todas las entidades compilan sin errores
- [ ] 11 tablas creadas en MySQL
- [ ] Relaciones funcionando correctamente

---

## ⏭️ Siguiente Paso

Continuar con **[04_REPOSITORIES.md](04_REPOSITORIES.md)** - PASO 13-21 (Repositories)
