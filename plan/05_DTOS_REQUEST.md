# 📥 PASO 22-28: DTOs de Request (Entrada)

## 🎯 Objetivo
Crear todos los DTOs que recibirán datos desde el frontend, con validaciones incorporadas.

---

## 📁 Ubicación Base
```
Fullsound/src/main/java/Fullsound/Fullsound/model/dto/request/
```

---

## ✅ PASO 22: DTOs de Autenticación

### 22.1 - LoginRequest

**Archivo:** `LoginRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitudes de login.
 * Acepta email o username como identificador.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "El email o username es requerido")
    private String emailOrUsername;
    
    @NotBlank(message = "La contraseña es requerida")
    private String password;
}
```

---

### 22.2 - RegisterRequest

**Archivo:** `RegisterRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registro de nuevos usuarios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "El username es requerido")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    private String username;
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    private String nombreCompleto;
    
    private Boolean esProductor = false; // Si se registra como productor
}
```

---

## ✅ PASO 23: DTOs de Usuario

### 23.1 - UsuarioUpdateRequest

**Archivo:** `UsuarioUpdateRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualización de perfil de usuario.
 * Todos los campos son opcionales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateRequest {
    
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    private String username;
    
    @Email(message = "El email debe ser válido")
    private String email;
    
    private String nombreCompleto;
    private String telefono;
    
    @Size(max = 1000, message = "La biografía no puede exceder 1000 caracteres")
    private String biografia;
    
    // Redes sociales
    private String instagramUrl;
    private String twitterUrl;
    private String youtubeUrl;
    private String spotifyUrl;
    private String soundcloudUrl;
}
```

---

### 23.2 - PasswordChangeRequest

**Archivo:** `PasswordChangeRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para cambio de contraseña.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
    
    @NotBlank(message = "La contraseña actual es requerida")
    private String currentPassword;
    
    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String newPassword;
}
```

---

### 23.3 - PasswordResetRequest

**Archivo:** `PasswordResetRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar reset de contraseña.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;
}
```

---

## ✅ PASO 24: DTOs de Beat

### 24.1 - BeatCreateRequest

**Archivo:** `BeatCreateRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import Fullsound.Fullsound.model.enums.TipoLicencia;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para creación de nuevos beats.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeatCreateRequest {
    
    @NotBlank(message = "El título es requerido")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;
    
    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String descripcion;
    
    @NotBlank(message = "El género es requerido")
    @Size(max = 50)
    private String genero;
    
    @NotNull(message = "El BPM es requerido")
    @Min(value = 60, message = "El BPM debe ser al menos 60")
    @Max(value = 200, message = "El BPM no puede exceder 200")
    private Integer bpm;
    
    @Size(max = 20)
    private String tonalidad; // Ej: "C Minor"
    
    @Size(max = 50)
    private String mood; // Ej: "Dark", "Happy"
    
    @Size(max = 500)
    private String tags; // Separados por comas
    
    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "10000.0", message = "El precio no puede exceder $10,000")
    private BigDecimal precio;
    
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precioDescuento;
    
    @NotNull(message = "El tipo de licencia es requerido")
    private TipoLicencia tipoLicencia;
    
    @Min(value = 1)
    private Integer duracionSegundos;
    
    private Boolean destacado = false;
}
```

---

### 24.2 - BeatUpdateRequest

**Archivo:** `BeatUpdateRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import Fullsound.Fullsound.model.enums.EstadoBeat;
import Fullsound.Fullsound.model.enums.TipoLicencia;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para actualización de beats.
 * Todos los campos son opcionales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeatUpdateRequest {
    
    @Size(max = 200)
    private String titulo;
    
    @Size(max = 2000)
    private String descripcion;
    
    @Size(max = 50)
    private String genero;
    
    @Min(60)
    @Max(200)
    private Integer bpm;
    
    private String tonalidad;
    private String mood;
    private String tags;
    
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "10000.0")
    private BigDecimal precio;
    
    private BigDecimal precioDescuento;
    private TipoLicencia tipoLicencia;
    private EstadoBeat estado;
    private Integer duracionSegundos;
    private Boolean destacado;
}
```

---

### 24.3 - BeatFilterRequest

**Archivo:** `BeatFilterRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para filtros de búsqueda de beats.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeatFilterRequest {
    
    private String genero;
    private Integer bpm;
    private String mood;
    private BigDecimal precioMin;
    private BigDecimal precioMax;
    private String searchTerm; // Búsqueda de texto
    private Long productorId;
    private Boolean destacados;
    
    // Ordenamiento
    private String sortBy = "createdAt"; // createdAt, reproducciones, precio, likes
    private String sortDir = "DESC"; // ASC, DESC
    
    // Paginación (se manejan en Pageable, pero útil para documentar)
    private Integer page = 0;
    private Integer size = 20;
}
```

---

## ✅ PASO 25: DTOs de Producto

### 25.1 - ProductoCreateRequest

**Archivo:** `ProductoCreateRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import Fullsound.Fullsound.model.enums.CategoriaProducto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para creación de productos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCreateRequest {
    
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 200)
    private String nombre;
    
    @Size(max = 2000)
    private String descripcion;
    
    @NotNull(message = "La categoría es requerida")
    private CategoriaProducto categoria;
    
    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "100000.0")
    private BigDecimal precio;
    
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precioDescuento;
    
    @Min(value = 0)
    private Integer stock;
    
    private Boolean stockIlimitado = false;
    private Boolean destacado = false;
    
    @Size(max = 5000)
    private String especificaciones; // JSON string
}
```

---

### 25.2 - ProductoUpdateRequest

**Archivo:** `ProductoUpdateRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import Fullsound.Fullsound.model.enums.CategoriaProducto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para actualización de productos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoUpdateRequest {
    
    @Size(max = 200)
    private String nombre;
    
    @Size(max = 2000)
    private String descripcion;
    
    private CategoriaProducto categoria;
    
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precio;
    
    private BigDecimal precioDescuento;
    
    @Min(value = 0)
    private Integer stock;
    
    private Boolean stockIlimitado;
    private Boolean destacado;
    private String especificaciones;
}
```

---

## ✅ PASO 26: DTOs de Carrito

### 26.1 - CarritoItemRequest

**Archivo:** `CarritoItemRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para agregar items al carrito.
 * Debe incluir beatId O productoId (no ambos).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemRequest {
    
    private Long beatId;
    private Long productoId;
    
    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad = 1;
    
    // Validación personalizada en el service para verificar que solo uno esté presente
}
```

---

### 26.2 - CarritoItemUpdateRequest

**Archivo:** `CarritoItemUpdateRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar cantidad de item en carrito.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemUpdateRequest {
    
    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}
```

---

## ✅ PASO 27: DTOs de Pedido y Pago

### 27.1 - PedidoCreateRequest

**Archivo:** `PedidoCreateRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para creación de pedidos.
 * El pedido se crea desde el carrito del usuario autenticado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCreateRequest {
    
    @Size(max = 500)
    private String notas;
    
    private String codigoDescuento; // Para cupones (implementación futura)
    
    // Datos de envío (opcional, solo para productos físicos)
    private String direccionEnvio;
    private String ciudadEnvio;
    private String codigoPostal;
    private String pais;
    private String telefonoContacto;
}
```

---

### 27.2 - PagoCreateRequest

**Archivo:** `PagoCreateRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import Fullsound.Fullsound.model.enums.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para iniciar un pago.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoCreateRequest {
    
    @NotNull(message = "El ID del pedido es requerido")
    private Long pedidoId;
    
    @NotNull(message = "El método de pago es requerido")
    private MetodoPago metodoPago;
    
    // Para Stripe
    private String paymentMethodId; // ID del método de pago de Stripe
    private String stripeToken; // Token antiguo (deprecated, usar paymentMethodId)
    
    // Email para recibo
    private String emailRecibo;
}
```

---

### 27.3 - StripeWebhookRequest

**Archivo:** `StripeWebhookRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para webhooks de Stripe.
 * Stripe envía eventos al backend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StripeWebhookRequest {
    
    private String id;
    private String type; // Ej: "payment_intent.succeeded"
    private Object data; // Datos del evento
    private Long created;
}
```

---

## ✅ PASO 28: DTOs de Review

### 28.1 - ReviewCreateRequest

**Archivo:** `ReviewCreateRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear una review de un beat.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {
    
    @NotNull(message = "El ID del beat es requerido")
    private Long beatId;
    
    @NotNull(message = "El rating es requerido")
    @Min(value = 1, message = "El rating debe ser al menos 1")
    @Max(value = 5, message = "El rating no puede exceder 5")
    private Integer rating;
    
    @Size(max = 1000, message = "El comentario no puede exceder 1000 caracteres")
    private String comentario;
}
```

---

### 28.2 - ReviewUpdateRequest

**Archivo:** `ReviewUpdateRequest.java`

```java
package Fullsound.Fullsound.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar una review existente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateRequest {
    
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;
    
    @Size(max = 1000)
    private String comentario;
}
```

---

## 🧪 Verificación

### Compilar

```bash
cd Fullsound
mvn clean compile
```

### Test de Validaciones (Ejemplo)

```java
@Test
void testLoginRequestValidation() {
    LoginRequest request = new LoginRequest();
    request.setEmailOrUsername(""); // Inválido
    request.setPassword(""); // Inválido
    
    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
    assertEquals(2, violations.size());
}
```

---

## 📋 Checklist PASO 22-28

- [ ] LoginRequest.java creado
- [ ] RegisterRequest.java creado
- [ ] UsuarioUpdateRequest.java creado
- [ ] PasswordChangeRequest.java creado
- [ ] PasswordResetRequest.java creado
- [ ] BeatCreateRequest.java creado
- [ ] BeatUpdateRequest.java creado
- [ ] BeatFilterRequest.java creado
- [ ] ProductoCreateRequest.java creado
- [ ] ProductoUpdateRequest.java creado
- [ ] CarritoItemRequest.java creado
- [ ] CarritoItemUpdateRequest.java creado
- [ ] PedidoCreateRequest.java creado
- [ ] PagoCreateRequest.java creado
- [ ] StripeWebhookRequest.java creado
- [ ] ReviewCreateRequest.java creado
- [ ] ReviewUpdateRequest.java creado
- [ ] Todos los DTOs compilan sin errores
- [ ] Validaciones funcionando correctamente

---

## 📊 Resumen DTOs Request

| Módulo | DTOs Creados | Validaciones |
|--------|--------------|--------------|
| **Autenticación** | 3 | @NotBlank, @Email, @Size |
| **Usuario** | 3 | @Email, @Size |
| **Beat** | 3 | @NotNull, @Min, @Max, @DecimalMin |
| **Producto** | 2 | @NotNull, @DecimalMin, @Size |
| **Carrito** | 2 | @NotNull, @Min |
| **Pedido/Pago** | 3 | @NotNull, @Size |
| **Review** | 2 | @Min, @Max, @Size |

**Total:** 18 DTOs de Request con validaciones completas

---

## ⏭️ Siguiente Paso

Continuar con **[06_DTOS_RESPONSE.md](06_DTOS_RESPONSE.md)** - PASO 29-30 (DTOs de Response)
