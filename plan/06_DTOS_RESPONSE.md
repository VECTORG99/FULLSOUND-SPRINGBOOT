# 📤 PASO 29-30: DTOs de Response (Salida)

## 🎯 Objetivo
Crear todos los DTOs que enviarán datos hacia el frontend, incluyendo wrappers genéricas para respuestas API y paginación.

---

## 📁 Ubicación Base
```
Fullsound/src/main/java/Fullsound/Fullsound/model/dto/response/
```

---

## ✅ PASO 29: DTOs de Response por Módulo

### 29.1 - AuthResponse

**Archivo:** `AuthResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO de respuesta para autenticación exitosa.
 * Incluye JWT token y datos del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Set<String> roles; // ["ROLE_USER", "ROLE_PRODUCTOR"]
    private String nombreCompleto;
    private String avatarUrl;
}
```

---

### 29.2 - UsuarioResponse

**Archivo:** `UsuarioResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO de respuesta para datos de usuario.
 * Excluye contraseña y datos sensibles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    
    private Long id;
    private String username;
    private String email;
    private String nombreCompleto;
    private String telefono;
    private String biografia;
    private String avatarUrl;
    
    // Redes sociales
    private String instagramUrl;
    private String twitterUrl;
    private String youtubeUrl;
    private String spotifyUrl;
    private String soundcloudUrl;
    
    // Roles
    private Set<String> roles;
    
    // Estadísticas (para productores)
    private Integer totalBeats;
    private Integer totalVentas;
    
    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean activo;
}
```

---

### 29.3 - UsuarioPublicResponse

**Archivo:** `UsuarioPublicResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para perfil público de usuario.
 * Muestra solo información pública.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPublicResponse {
    
    private Long id;
    private String username;
    private String nombreCompleto;
    private String biografia;
    private String avatarUrl;
    
    // Redes sociales
    private String instagramUrl;
    private String twitterUrl;
    private String youtubeUrl;
    private String spotifyUrl;
    private String soundcloudUrl;
    
    // Estadísticas públicas
    private Integer totalBeats;
    private Integer totalSeguidores;
}
```

---

### 29.4 - BeatResponse

**Archivo:** `BeatResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import Fullsound.Fullsound.model.enums.EstadoBeat;
import Fullsound.Fullsound.model.enums.TipoLicencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para beats.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeatResponse {
    
    private Long id;
    private String titulo;
    private String descripcion;
    private String genero;
    private Integer bpm;
    private String tonalidad;
    private String mood;
    private String tags;
    
    private BigDecimal precio;
    private BigDecimal precioDescuento;
    private TipoLicencia tipoLicencia;
    
    private String audioUrl;
    private String imagenUrl;
    private String waveformUrl;
    private Integer duracionSegundos;
    
    private EstadoBeat estado;
    private Boolean destacado;
    
    // Estadísticas
    private Integer reproducciones = 0;
    private Integer descargas = 0;
    private Integer likes = 0;
    private Double ratingPromedio = 0.0;
    private Integer totalReviews = 0;
    
    // Productor
    private UsuarioPublicResponse productor;
    
    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

---

### 29.5 - BeatSummaryResponse

**Archivo:** `BeatSummaryResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta resumida para beats (listados, búsquedas).
 * Versión ligera sin todos los detalles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeatSummaryResponse {
    
    private Long id;
    private String titulo;
    private String genero;
    private Integer bpm;
    private String mood;
    
    private BigDecimal precio;
    private BigDecimal precioDescuento;
    
    private String imagenUrl;
    private Integer duracionSegundos;
    private Boolean destacado;
    
    // Estadísticas básicas
    private Integer reproducciones;
    private Integer likes;
    private Double ratingPromedio;
    
    // Productor resumido
    private Long productorId;
    private String productorUsername;
    private String productorAvatar;
}
```

---

### 29.6 - ProductoResponse

**Archivo:** `ProductoResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import Fullsound.Fullsound.model.enums.CategoriaProducto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para productos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private CategoriaProducto categoria;
    
    private BigDecimal precio;
    private BigDecimal precioDescuento;
    
    private Integer stock;
    private Boolean stockIlimitado;
    private Boolean disponible;
    
    private String imagenUrl;
    private String especificaciones; // JSON string
    
    private Boolean destacado;
    
    // Estadísticas
    private Integer totalVendidos = 0;
    private Double ratingPromedio = 0.0;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

---

### 29.7 - CarritoResponse

**Archivo:** `CarritoResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de respuesta para carrito de compras.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoResponse {
    
    private Long id;
    private Long usuarioId;
    
    @Builder.Default
    private List<CarritoItemResponse> items = new ArrayList<>();
    
    private Integer totalItems;
    private BigDecimal subtotal;
    private BigDecimal descuentos;
    private BigDecimal total;
    
    private LocalDateTime updatedAt;
}
```

---

### 29.8 - CarritoItemResponse

**Archivo:** `CarritoItemResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta para items del carrito.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemResponse {
    
    private Long id;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    
    // Beat o Producto (solo uno estará presente)
    private BeatSummaryResponse beat;
    private ProductoResponse producto;
}
```

---

### 29.9 - PedidoResponse

**Archivo:** `PedidoResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import Fullsound.Fullsound.model.enums.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de respuesta para pedidos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {
    
    private Long id;
    private String numeroPedido; // Ej: "FS-2024-001234"
    
    private Long usuarioId;
    private String usuarioEmail;
    
    private EstadoPedido estado;
    
    @Builder.Default
    private List<PedidoItemResponse> items = new ArrayList<>();
    
    private BigDecimal subtotal;
    private BigDecimal descuentos;
    private BigDecimal impuestos;
    private BigDecimal total;
    
    private String notas;
    
    // Datos de envío
    private String direccionEnvio;
    private String ciudadEnvio;
    private String codigoPostal;
    private String pais;
    private String telefonoContacto;
    
    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime fechaEntrega;
}
```

---

### 29.10 - PedidoItemResponse

**Archivo:** `PedidoItemResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta para items de pedido.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItemResponse {
    
    private Long id;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    
    // Snapshot del beat/producto al momento de la compra
    private Long beatId;
    private String beatTitulo;
    private String beatAudioUrl;
    
    private Long productoId;
    private String productoNombre;
}
```

---

### 29.11 - PagoResponse

**Archivo:** `PagoResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import Fullsound.Fullsound.model.enums.EstadoPago;
import Fullsound.Fullsound.model.enums.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para pagos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {
    
    private Long id;
    private Long pedidoId;
    private String numeroPedido;
    
    private MetodoPago metodoPago;
    private EstadoPago estado;
    
    private BigDecimal monto;
    private String moneda = "USD";
    
    // Stripe
    private String stripePaymentIntentId;
    private String stripeClientSecret; // Para confirmar pago en frontend
    
    private String descripcionError;
    
    private LocalDateTime createdAt;
    private LocalDateTime fechaProcesado;
}
```

---

### 29.12 - ReviewResponse

**Archivo:** `ReviewResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para reviews.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    
    private Long id;
    private Integer rating;
    private String comentario;
    
    private Long beatId;
    private String beatTitulo;
    
    // Usuario que hizo la review
    private Long usuarioId;
    private String usuarioUsername;
    private String usuarioAvatar;
    
    private Boolean verificado; // Compró el beat
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

---

### 29.13 - EstadisticasResponse

**Archivo:** `EstadisticasResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta para estadísticas generales.
 * Usado en dashboard de admin/productor.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasResponse {
    
    // Ventas
    private BigDecimal ventasTotales;
    private BigDecimal ventasMesActual;
    private Integer numeroVentas;
    private BigDecimal ticketPromedio;
    
    // Beats
    private Integer totalBeats;
    private Integer beatsActivos;
    private Integer reproducciones;
    private Integer descargas;
    
    // Productos
    private Integer totalProductos;
    private Integer productosDisponibles;
    
    // Usuarios
    private Integer totalUsuarios;
    private Integer usuariosActivos;
    private Integer productores;
    
    // Pedidos
    private Integer pedidosPendientes;
    private Integer pedidosCompletados;
    private Integer pedidosCancelados;
}
```

---

## ✅ PASO 30: Wrappers Genéricas de Respuesta

### 30.1 - ApiResponse (Genérica)

**Archivo:** `ApiResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Wrapper genérica para todas las respuestas de la API.
 * Proporciona estructura consistente.
 * 
 * @param <T> Tipo de datos en la respuesta
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private Boolean success;
    private String message;
    private T data;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    // Para errores
    private String error;
    private String path;
    
    // Métodos estáticos para facilitar creación
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
```

**Ejemplo de uso:**
```java
// En un controller
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<BeatResponse>> getBeat(@PathVariable Long id) {
    BeatResponse beat = beatService.findById(id);
    return ResponseEntity.ok(ApiResponse.success(beat));
}

// Respuesta JSON:
{
    "success": true,
    "data": {
        "id": 1,
        "titulo": "Dark Trap Beat",
        ...
    },
    "timestamp": "2024-11-12T10:30:00"
}
```

---

### 30.2 - PageResponse (Paginación)

**Archivo:** `PageResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper para respuestas paginadas.
 * Incluye metadata de paginación.
 * 
 * @param <T> Tipo de elementos en la lista
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
    private List<T> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private Boolean isFirst;
    private Boolean isLast;
    private Boolean hasNext;
    private Boolean hasPrevious;
    
    // Constructor desde Spring Page
    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
```

**Ejemplo de uso:**
```java
@GetMapping
public ResponseEntity<ApiResponse<PageResponse<BeatSummaryResponse>>> getBeats(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    
    Page<BeatSummaryResponse> beatsPage = beatService.findAll(PageRequest.of(page, size));
    PageResponse<BeatSummaryResponse> response = PageResponse.from(beatsPage);
    
    return ResponseEntity.ok(ApiResponse.success(response));
}

// Respuesta JSON:
{
    "success": true,
    "data": {
        "content": [...],
        "pageNumber": 0,
        "pageSize": 20,
        "totalElements": 156,
        "totalPages": 8,
        "isFirst": true,
        "isLast": false,
        "hasNext": true,
        "hasPrevious": false
    }
}
```

---

### 30.3 - ErrorResponse (Errores detallados)

**Archivo:** `ErrorResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO de respuesta para errores detallados.
 * Usado en GlobalExceptionHandler.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private Integer status;
    private String error;
    private String message;
    private String path;
    
    // Para errores de validación
    private Map<String, String> fieldErrors;
    
    // Para múltiples errores
    private List<String> errors;
    
    // Stack trace (solo en desarrollo)
    private String trace;
}
```

**Ejemplo de uso en GlobalExceptionHandler:**
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex,
        HttpServletRequest request) {
    
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        fieldErrors.put(error.getField(), error.getDefaultMessage())
    );
    
    ErrorResponse error = ErrorResponse.builder()
            .status(400)
            .error("Validation Error")
            .message("Los datos enviados no son válidos")
            .path(request.getRequestURI())
            .fieldErrors(fieldErrors)
            .build();
    
    return ResponseEntity.badRequest().body(error);
}

// Respuesta JSON:
{
    "timestamp": "2024-11-12T10:30:00",
    "status": 400,
    "error": "Validation Error",
    "message": "Los datos enviados no son válidos",
    "path": "/api/beats",
    "fieldErrors": {
        "titulo": "El título es requerido",
        "precio": "El precio debe ser mayor a 0"
    }
}
```

---

### 30.4 - MessageResponse (Mensajes simples)

**Archivo:** `MessageResponse.java`

```java
package Fullsound.Fullsound.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simple para mensajes de confirmación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    
    private String message;
    
    public static MessageResponse of(String message) {
        return new MessageResponse(message);
    }
}
```

**Ejemplo de uso:**
```java
@DeleteMapping("/{id}")
public ResponseEntity<ApiResponse<MessageResponse>> deleteBeat(@PathVariable Long id) {
    beatService.delete(id);
    return ResponseEntity.ok(
        ApiResponse.success("Beat eliminado exitosamente", 
                          MessageResponse.of("El beat ha sido eliminado"))
    );
}
```

---

## 🧪 Verificación

### Compilar

```bash
cd Fullsound
mvn clean compile
```

### Test de Serialización JSON

```java
@Test
void testApiResponseSerialization() throws Exception {
    BeatResponse beat = BeatResponse.builder()
            .id(1L)
            .titulo("Test Beat")
            .build();
    
    ApiResponse<BeatResponse> response = ApiResponse.success(beat);
    
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(response);
    
    assertThat(json).contains("\"success\":true");
    assertThat(json).contains("\"titulo\":\"Test Beat\"");
}
```

---

## 📋 Checklist PASO 29-30

**DTOs Response:**
- [ ] AuthResponse.java creado
- [ ] UsuarioResponse.java creado
- [ ] UsuarioPublicResponse.java creado
- [ ] BeatResponse.java creado
- [ ] BeatSummaryResponse.java creado
- [ ] ProductoResponse.java creado
- [ ] CarritoResponse.java creado
- [ ] CarritoItemResponse.java creado
- [ ] PedidoResponse.java creado
- [ ] PedidoItemResponse.java creado
- [ ] PagoResponse.java creado
- [ ] ReviewResponse.java creado
- [ ] EstadisticasResponse.java creado

**Wrappers Genéricas:**
- [ ] ApiResponse.java creado
- [ ] PageResponse.java creado
- [ ] ErrorResponse.java creado
- [ ] MessageResponse.java creado

**Validación:**
- [ ] Todos los DTOs compilan
- [ ] Serialización JSON funciona
- [ ] Lombok @Builder genera constructores
- [ ] @JsonInclude evita nulls en JSON

---

## 📊 Resumen DTOs Response

| Módulo | DTOs Creados | Campos Promedio |
|--------|--------------|-----------------|
| **Autenticación** | 1 | 8 |
| **Usuario** | 2 | 12 |
| **Beat** | 2 | 15/10 |
| **Producto** | 1 | 13 |
| **Carrito** | 2 | 8/6 |
| **Pedido** | 2 | 15/6 |
| **Pago** | 1 | 10 |
| **Review** | 1 | 9 |
| **Estadísticas** | 1 | 15 |
| **Wrappers** | 4 | Variable |

**Total:** 17 DTOs de Response + 4 Wrappers Genéricas

---

## ⏭️ Siguiente Paso

Continuar con **[07_MAPPERS.md](07_MAPPERS.md)** - PASO 31-37 (Mappers MapStruct)
