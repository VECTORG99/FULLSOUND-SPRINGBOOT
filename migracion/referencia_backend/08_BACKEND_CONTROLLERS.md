# 🎮 BACKEND CONTROLLERS - FULLSOUND

## 🎯 Objetivo

Implementar los Controllers REST de Spring Boot que proporcionen las APIs necesarias para el frontend React.

---

## 📋 Controllers a Implementar

1. **AuthController** - Autenticación (Login/Registro/Logout)
2. **BeatController** - CRUD de Beats
3. **CarritoController** - Gestión de Carrito
4. **UsuarioController** - Gestión de Usuarios
5. **GeneroController** - Gestión de Géneros (opcional)

---

## 🔐 1. AuthController

### Endpoints

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/api/auth/register` | No | Registro de usuario |
| POST | `/api/auth/login` | No | Inicio de sesión |
| POST | `/api/auth/logout` | Sí | Cierre de sesión |
| GET | `/api/auth/me` | Sí | Obtener usuario actual |

### Implementación

```java
package com.fullsound.controller;

import com.fullsound.dto.*;
import com.fullsound.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body(Map.of("message", "Logout exitoso"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getCurrentUser(Principal principal) {
        UsuarioDTO usuario = authService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(usuario);
    }
}
```

---

## 🎵 2. BeatController

### Endpoints

| Método | Ruta | Auth | Rol | Descripción |
|--------|------|------|-----|-------------|
| GET | `/api/beats` | No | - | Listar todos los beats |
| GET | `/api/beats/{id}` | No | - | Obtener beat por ID |
| POST | `/api/beats` | Sí | Admin | Crear beat |
| PUT | `/api/beats/{id}` | Sí | Admin | Actualizar beat |
| DELETE | `/api/beats/{id}` | Sí | Admin | Eliminar beat |
| GET | `/api/beats/{id}/audio` | No | - | Descargar audio |
| GET | `/api/beats/{id}/imagen` | No | - | Descargar imagen |
| GET | `/api/beats/generos` | No | - | Listar géneros |

### Implementación

```java
package com.fullsound.controller;

import com.fullsound.dto.BeatDTO;
import com.fullsound.service.BeatService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/beats")
@CrossOrigin(origins = "*")
public class BeatController {
    
    private final BeatService beatService;
    
    public BeatController(BeatService beatService) {
        this.beatService = beatService;
    }
    
    @GetMapping
    public ResponseEntity<List<BeatDTO>> listarBeats() {
        List<BeatDTO> beats = beatService.listarTodos();
        return ResponseEntity.ok(beats);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BeatDTO> obtenerBeat(@PathVariable Long id) {
        BeatDTO beat = beatService.obtenerPorId(id);
        return ResponseEntity.ok(beat);
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BeatDTO> crearBeat(
            @RequestParam("nombre") String nombre,
            @RequestParam("artista") String artista,
            @RequestParam("genero") String genero,
            @RequestParam("precio") Double precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "audio", required = false) MultipartFile audio,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) {
        BeatDTO beat = beatService.crear(nombre, artista, genero, precio, 
                                         descripcion, audio, imagen);
        return ResponseEntity.status(HttpStatus.CREATED).body(beat);
    }
    
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BeatDTO> actualizarBeat(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("artista") String artista,
            @RequestParam("genero") String genero,
            @RequestParam("precio") Double precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "audio", required = false) MultipartFile audio,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) {
        BeatDTO beat = beatService.actualizar(id, nombre, artista, genero, 
                                              precio, descripcion, audio, imagen);
        return ResponseEntity.ok(beat);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarBeat(@PathVariable Long id) {
        beatService.eliminar(id);
        return ResponseEntity.ok().body(Map.of("message", "Beat eliminado"));
    }
    
    @GetMapping("/{id}/audio")
    public ResponseEntity<Resource> descargarAudio(@PathVariable Long id) {
        Resource resource = beatService.obtenerAudio(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                       "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @GetMapping("/{id}/imagen")
    public ResponseEntity<Resource> descargarImagen(@PathVariable Long id) {
        Resource resource = beatService.obtenerImagen(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
    
    @GetMapping("/generos")
    public ResponseEntity<List<String>> listarGeneros() {
        List<String> generos = beatService.listarGeneros();
        return ResponseEntity.ok(generos);
    }
}
```

---

## 🛒 3. CarritoController

### Endpoints

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/api/carrito` | Sí | Obtener carrito del usuario |
| POST | `/api/carrito/items` | Sí | Agregar item al carrito |
| DELETE | `/api/carrito/items/{id}` | Sí | Eliminar item del carrito |
| PUT | `/api/carrito/items/{id}` | Sí | Actualizar cantidad |
| DELETE | `/api/carrito` | Sí | Vaciar carrito |

### Implementación

```java
package com.fullsound.controller;

import com.fullsound.dto.*;
import com.fullsound.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {
    
    private final CarritoService carritoService;
    
    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }
    
    @GetMapping
    public ResponseEntity<CarritoDTO> obtenerCarrito(Principal principal) {
        CarritoDTO carrito = carritoService.obtenerCarrito(principal.getName());
        return ResponseEntity.ok(carrito);
    }
    
    @PostMapping("/items")
    public ResponseEntity<CarritoDTO> agregarItem(
            @RequestBody AgregarItemRequest request,
            Principal principal
    ) {
        CarritoDTO carrito = carritoService.agregarItem(
            principal.getName(), 
            request.getBeatId()
        );
        return ResponseEntity.ok(carrito);
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CarritoDTO> eliminarItem(
            @PathVariable Long itemId,
            Principal principal
    ) {
        CarritoDTO carrito = carritoService.eliminarItem(
            principal.getName(), 
            itemId
        );
        return ResponseEntity.ok(carrito);
    }
    
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CarritoDTO> actualizarCantidad(
            @PathVariable Long itemId,
            @RequestBody ActualizarCantidadRequest request,
            Principal principal
    ) {
        CarritoDTO carrito = carritoService.actualizarCantidad(
            principal.getName(), 
            itemId, 
            request.getCantidad()
        );
        return ResponseEntity.ok(carrito);
    }
    
    @DeleteMapping
    public ResponseEntity<?> vaciarCarrito(Principal principal) {
        carritoService.vaciarCarrito(principal.getName());
        return ResponseEntity.ok().body(Map.of("message", "Carrito vaciado"));
    }
}
```

---

## 👥 4. UsuarioController

### Endpoints

| Método | Ruta | Auth | Rol | Descripción |
|--------|------|------|-----|-------------|
| GET | `/api/usuarios` | Sí | Admin | Listar todos los usuarios |
| GET | `/api/usuarios/{id}` | Sí | - | Obtener usuario por ID |
| PUT | `/api/usuarios/{id}` | Sí | - | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Sí | Admin | Eliminar usuario |

### Implementación

```java
package com.fullsound.controller;

import com.fullsound.dto.UsuarioDTO;
import com.fullsound.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioDTO usuarioDTO
    ) {
        UsuarioDTO usuario = usuarioService.actualizar(id, usuarioDTO);
        return ResponseEntity.ok(usuario);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok().body(Map.of("message", "Usuario eliminado"));
    }
}
```

---

## 🌐 5. StaticResourceController (Fallback para SPA)

```java
package com.fullsound.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StaticResourceController {
    
    /**
     * Redirige todas las rutas no-API a index.html
     * para que React Router funcione correctamente
     */
    @RequestMapping(value = {"/{path:[^\\.]*}", "/**/{path:[^\\.]*}"})
    public String redirect() {
        return "forward:/index.html";
    }
}
```

---

## ✅ Checklist de Implementación

### AuthController
- [ ] Registro funciona
- [ ] Login funciona
- [ ] JWT se genera correctamente
- [ ] Validaciones funcionan
- [ ] Manejo de errores

### BeatController
- [ ] Listar beats funciona
- [ ] Crear beat funciona
- [ ] Upload de archivos funciona
- [ ] Actualizar beat funciona
- [ ] Eliminar beat funciona
- [ ] Descargar audio/imagen funciona

### CarritoController
- [ ] Obtener carrito funciona
- [ ] Agregar item funciona
- [ ] Eliminar item funciona
- [ ] Actualizar cantidad funciona
- [ ] Vaciar carrito funciona

### UsuarioController
- [ ] Listar usuarios (admin)
- [ ] Obtener usuario
- [ ] Actualizar usuario
- [ ] Eliminar usuario (admin)

---

**Próximo Paso**: [09_BACKEND_MODELOS_SERVICIOS.md](09_BACKEND_MODELOS_SERVICIOS.md)
