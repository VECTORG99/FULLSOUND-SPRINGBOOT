# 📊 BACKEND - MODELOS Y SERVICIOS - FULLSOUND

## 🎯 Objetivo

Implementar las entidades JPA, repositorios y servicios de negocio que soportan los controllers.

---

## 📦 Estructura de Paquetes

```
com.fullsound/
├── model/              # Entidades JPA
├── repository/         # Repositorios Spring Data
├── service/            # Lógica de negocio
└── dto/                # Data Transfer Objects
```

---

## 🗄️ MODELOS (Entidades JPA)

### 1. Usuario

```java
package com.fullsound.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, unique = true, length = 100)
    private String correo;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;
    
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Carrito carrito;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
```

### 2. Beat

```java
package com.fullsound.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "beats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String artista;
    
    @Column(nullable = false, length = 50)
    private String genero;
    
    @Column(nullable = false)
    private Double precio;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "ruta_audio")
    private String rutaAudio;
    
    @Column(name = "ruta_imagen")
    private String rutaImagen;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
```

### 3. Carrito

```java
package com.fullsound.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoItem> items = new ArrayList<>();
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    public Double calcularTotal() {
        return items.stream()
                .mapToDouble(item -> item.getBeat().getPrecio() * item.getCantidad())
                .sum();
    }
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
```

### 4. CarritoItem

```java
package com.fullsound.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "carrito_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;
    
    @ManyToOne
    @JoinColumn(name = "beat_id", nullable = false)
    private Beat beat;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "fecha_agregado")
    private LocalDateTime fechaAgregado;
    
    @PrePersist
    protected void onCreate() {
        fechaAgregado = LocalDateTime.now();
    }
}
```

### 5. Rol (Enum)

```java
package com.fullsound.model;

public enum Rol {
    ADMIN,
    USUARIO
}
```

---

## 📚 REPOSITORIOS

```java
package com.fullsound.repository;

import com.fullsound.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}

@Repository
public interface BeatRepository extends JpaRepository<Beat, Long> {
    List<Beat> findByGenero(String genero);
    List<Beat> findByArtista(String artista);
}

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuarioId(Long usuarioId);
}

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    Optional<CarritoItem> findByCarritoIdAndBeatId(Long carritoId, Long beatId);
}
```

---

## 🎨 DTOs (Data Transfer Objects)

```java
package com.fullsound.dto;

import lombok.*;

// LoginRequest
@Data
public class LoginRequest {
    private String correo;
    private String password;
}

// RegisterRequest
@Data
public class RegisterRequest {
    private String nombre;
    private String correo;
    private String password;
}

// AuthResponse
@Data
@Builder
public class AuthResponse {
    private UsuarioDTO user;
    private String token;
}

// UsuarioDTO
@Data
@Builder
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String rol;
}

// BeatDTO
@Data
@Builder
public class BeatDTO {
    private Long id;
    private String nombre;
    private String artista;
    private String genero;
    private Double precio;
    private String descripcion;
    private String audioUrl;
    private String imagenUrl;
}

// CarritoDTO
@Data
@Builder
public class CarritoDTO {
    private Long id;
    private List<CarritoItemDTO> items;
    private Double total;
}

// CarritoItemDTO
@Data
@Builder
public class CarritoItemDTO {
    private Long id;
    private BeatDTO beat;
    private Integer cantidad;
}
```

---

## ⚙️ SERVICIOS

### AuthService

```java
package com.fullsound.service;

import com.fullsound.dto.*;
import com.fullsound.model.*;
import com.fullsound.repository.UsuarioRepository;
import com.fullsound.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    public AuthService(UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        Rol rol = request.getCorreo().endsWith("@admin.cl") 
                ? Rol.ADMIN 
                : Rol.USUARIO;
        
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(rol)
                .build();
        
        usuario = usuarioRepository.save(usuario);
        
        String token = jwtTokenProvider.generateToken(usuario.getCorreo());
        
        return AuthResponse.builder()
                .user(convertToDTO(usuario))
                .token(token)
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        
        String token = jwtTokenProvider.generateToken(usuario.getCorreo());
        
        return AuthResponse.builder()
                .user(convertToDTO(usuario))
                .token(token)
                .build();
    }
    
    private UsuarioDTO convertToDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol().name().toLowerCase())
                .build();
    }
}
```

### BeatService

```java
package com.fullsound.service;

import com.fullsound.dto.BeatDTO;
import com.fullsound.model.Beat;
import com.fullsound.repository.BeatRepository;
import com.fullsound.util.FileUploadUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeatService {
    
    private final BeatRepository beatRepository;
    private final FileUploadUtil fileUploadUtil;
    
    public BeatService(BeatRepository beatRepository, 
                      FileUploadUtil fileUploadUtil) {
        this.beatRepository = beatRepository;
        this.fileUploadUtil = fileUploadUtil;
    }
    
    public List<BeatDTO> listarTodos() {
        return beatRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public BeatDTO obtenerPorId(Long id) {
        Beat beat = beatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beat no encontrado"));
        return convertToDTO(beat);
    }
    
    public BeatDTO crear(String nombre, String artista, String genero,
                        Double precio, String descripcion,
                        MultipartFile audio, MultipartFile imagen) {
        String rutaAudio = fileUploadUtil.saveFile(audio, "beats/audio");
        String rutaImagen = fileUploadUtil.saveFile(imagen, "beats/imagenes");
        
        Beat beat = Beat.builder()
                .nombre(nombre)
                .artista(artista)
                .genero(genero)
                .precio(precio)
                .descripcion(descripcion)
                .rutaAudio(rutaAudio)
                .rutaImagen(rutaImagen)
                .build();
        
        beat = beatRepository.save(beat);
        return convertToDTO(beat);
    }
    
    private BeatDTO convertToDTO(Beat beat) {
        return BeatDTO.builder()
                .id(beat.getId())
                .nombre(beat.getNombre())
                .artista(beat.getArtista())
                .genero(beat.getGenero())
                .precio(beat.getPrecio())
                .descripcion(beat.getDescripcion())
                .audioUrl("/api/beats/" + beat.getId() + "/audio")
                .imagenUrl("/api/beats/" + beat.getId() + "/imagen")
                .build();
    }
}
```

---

## ✅ Checklist

- [ ] Todas las entidades creadas
- [ ] Relaciones configuradas
- [ ] Repositorios creados
- [ ] DTOs definidos
- [ ] Servicios implementados
- [ ] Validaciones agregadas
- [ ] Tests unitarios

---

**Próximo Paso**: [10_SEGURIDAD_JWT.md](10_SEGURIDAD_JWT.md)
