# ⚠️ PASO 62-63: Manejo Global de Excepciones

## 🎯 Objetivo
Implementar un sistema robusto de manejo de excepciones personalizadas y un GlobalExceptionHandler para respuestas consistentes.

---

## 📁 Ubicación Base
```
Fullsound/src/main/java/Fullsound/Fullsound/exception/
```

---

## ✅ PASO 62: Excepciones Personalizadas

### 62.1 - ResourceNotFoundException

**Archivo:** `ResourceNotFoundException.java`

```java
package Fullsound.Fullsound.exception;

/**
 * Excepción lanzada cuando un recurso no se encuentra en la BD.
 * Código HTTP: 404 Not Found
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

### 62.2 - BadRequestException

**Archivo:** `BadRequestException.java`

```java
package Fullsound.Fullsound.exception;

/**
 * Excepción lanzada cuando la petición es inválida.
 * Código HTTP: 400 Bad Request
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

### 62.3 - ForbiddenException

**Archivo:** `ForbiddenException.java`

```java
package Fullsound.Fullsound.exception;

/**
 * Excepción lanzada cuando el usuario no tiene permisos.
 * Código HTTP: 403 Forbidden
 */
public class ForbiddenException extends RuntimeException {
    
    public ForbiddenException(String message) {
        super(message);
    }
    
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

### 62.4 - UnauthorizedException

**Archivo:** `UnauthorizedException.java`

```java
package Fullsound.Fullsound.exception;

/**
 * Excepción lanzada cuando la autenticación falla.
 * Código HTTP: 401 Unauthorized
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

### 62.5 - ConflictException

**Archivo:** `ConflictException.java`

```java
package Fullsound.Fullsound.exception;

/**
 * Excepción lanzada cuando hay conflicto (ej: username duplicado).
 * Código HTTP: 409 Conflict
 */
public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

### 62.6 - FileStorageException

**Archivo:** `FileStorageException.java`

```java
package Fullsound.Fullsound.exception;

/**
 * Excepción lanzada cuando falla el almacenamiento de archivos.
 * Código HTTP: 500 Internal Server Error
 */
public class FileStorageException extends RuntimeException {
    
    public FileStorageException(String message) {
        super(message);
    }
    
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

## ✅ PASO 63: GlobalExceptionHandler

**Archivo:** `GlobalExceptionHandler.java`

```java
package Fullsound.Fullsound.exception;

import Fullsound.Fullsound.model.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la aplicación.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Maneja ResourceNotFoundException (404).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Maneja BadRequestException (400).
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(
            BadRequestException ex,
            HttpServletRequest request) {
        
        log.warn("Petición inválida: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja ForbiddenException (403).
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(
            ForbiddenException ex,
            HttpServletRequest request) {
        
        log.warn("Acceso prohibido: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Maneja UnauthorizedException (401).
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex,
            HttpServletRequest request) {
        
        log.warn("No autorizado: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    /**
     * Maneja ConflictException (409).
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException ex,
            HttpServletRequest request) {
        
        log.warn("Conflicto: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * Maneja errores de validación de @Valid (400).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        log.warn("Error de validación en: {}", request.getRequestURI());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message("Los datos enviados no son válidos")
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja AccessDeniedException de Spring Security (403).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        
        log.warn("Acceso denegado: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("Access Denied")
                .message("No tienes permisos para realizar esta acción")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Maneja BadCredentialsException (401).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {
        
        log.warn("Credenciales inválidas en: {}", request.getRequestURI());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Email o contraseña incorrectos")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    /**
     * Maneja errores de tipo de argumento incorrecto (400).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        
        log.warn("Tipo de argumento incorrecto: {}", ex.getMessage());
        
        String message = String.format("El parámetro '%s' debe ser de tipo %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido");
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(message)
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja FileStorageException (500).
     */
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> handleFileStorageException(
            FileStorageException ex,
            HttpServletRequest request) {
        
        log.error("Error de almacenamiento de archivos: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Error al procesar el archivo: " + ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    /**
     * Maneja cualquier otra excepción no capturada (500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Error no manejado: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ha ocurrido un error inesperado. Por favor, contacta al soporte.")
                .path(request.getRequestURI())
                .build();
        
        // Solo en desarrollo
        if (isDevelopmentMode()) {
            error.setTrace(getStackTrace(ex));
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    /**
     * Verifica si está en modo desarrollo.
     */
    private boolean isDevelopmentMode() {
        String env = System.getProperty("spring.profiles.active");
        return env != null && (env.contains("dev") || env.contains("local"));
    }
    
    /**
     * Obtiene el stack trace como string.
     */
    private String getStackTrace(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
```

---

## 🧪 Verificación

### Test de GlobalExceptionHandler

```java
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldReturn404WhenResourceNotFound() throws Exception {
        mockMvc.perform(get("/api/beats/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    void shouldReturn400OnValidationError() throws Exception {
        String invalidRequest = "{\"username\":\"\",\"email\":\"invalid\",\"password\":\"\"}";
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }
    
    @Test
    void shouldReturn401OnBadCredentials() throws Exception {
        String invalidLogin = "{\"emailOrUsername\":\"user\",\"password\":\"wrong\"}";
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidLogin))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
    
    @Test
    void shouldReturn403OnAccessDenied() throws Exception {
        // Usuario sin rol ADMIN intenta acceder a endpoint de admin
        mockMvc.perform(get("/api/usuarios")
                .header("Authorization", "Bearer " + getNonAdminToken()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }
}
```

---

## 📋 Checklist PASO 62-63

**Excepciones Personalizadas:**
- [ ] ResourceNotFoundException.java creada (404)
- [ ] BadRequestException.java creada (400)
- [ ] ForbiddenException.java creada (403)
- [ ] UnauthorizedException.java creada (401)
- [ ] ConflictException.java creada (409)
- [ ] FileStorageException.java creada (500)

**GlobalExceptionHandler:**
- [ ] GlobalExceptionHandler.java creado
- [ ] @RestControllerAdvice configurado
- [ ] Manejo de ResourceNotFoundException
- [ ] Manejo de validaciones (@Valid)
- [ ] Manejo de AccessDeniedException
- [ ] Manejo de BadCredentialsException
- [ ] Manejo de excepciones genéricas
- [ ] ErrorResponse con fieldErrors para validaciones
- [ ] Stack trace solo en desarrollo

**Validación:**
- [ ] Tests de excepciones pasando
- [ ] Respuestas JSON consistentes
- [ ] Códigos HTTP correctos
- [ ] Mensajes de error claros

---

## 📊 Resumen de Excepciones

| Excepción | HTTP Status | Uso |
|-----------|-------------|-----|
| **ResourceNotFoundException** | 404 | Recurso no encontrado en BD |
| **BadRequestException** | 400 | Petición inválida |
| **ForbiddenException** | 403 | Sin permisos |
| **UnauthorizedException** | 401 | Autenticación fallida |
| **ConflictException** | 409 | Conflicto (ej: username duplicado) |
| **FileStorageException** | 500 | Error en archivos |
| **MethodArgumentNotValidException** | 400 | Validación @Valid fallida |
| **AccessDeniedException** | 403 | Spring Security access denied |
| **BadCredentialsException** | 401 | Credenciales incorrectas |
| **Exception** | 500 | Cualquier error no manejado |

---

## 📝 Ejemplo de Respuestas de Error

### 404 - Not Found
```json
{
  "timestamp": "2024-11-13T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Beat no encontrado",
  "path": "/api/beats/999"
}
```

### 400 - Validation Error
```json
{
  "timestamp": "2024-11-13T10:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Los datos enviados no son válidos",
  "path": "/api/auth/register",
  "fieldErrors": {
    "username": "El username es requerido",
    "email": "El email debe ser válido",
    "password": "La contraseña debe tener al menos 6 caracteres"
  }
}
```

### 401 - Unauthorized
```json
{
  "timestamp": "2024-11-13T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Email o contraseña incorrectos",
  "path": "/api/auth/login"
}
```

### 403 - Forbidden
```json
{
  "timestamp": "2024-11-13T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "No tienes permiso para modificar este beat",
  "path": "/api/beats/123"
}
```

---

## ⏭️ Siguiente Paso

Continuar con **[13_TESTING.md](13_TESTING.md)** - PASO 64-67 (Testing Unitario e Integración)
