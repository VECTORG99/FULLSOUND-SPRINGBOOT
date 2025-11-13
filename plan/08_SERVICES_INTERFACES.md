# 🎯 PASO 38: Interfaces de Servicios

## 🎯 Objetivo
Definir las interfaces (contratos) de todos los servicios de negocio. Estas interfaces establecen los métodos públicos que expondrá cada servicio.

---

## 📁 Ubicación Base
```
Fullsound/src/main/java/Fullsound/Fullsound/service/
```

---

## ✅ PASO 38.1: AuthService Interface

**Archivo:** `AuthService.java`

```java
package Fullsound.Fullsound.service;

import Fullsound.Fullsound.model.dto.request.LoginRequest;
import Fullsound.Fullsound.model.dto.request.RegisterRequest;
import Fullsound.Fullsound.model.dto.request.PasswordResetRequest;
import Fullsound.Fullsound.model.dto.response.AuthResponse;
import Fullsound.Fullsound.model.dto.response.MessageResponse;

/**
 * Servicio de autenticación y gestión de tokens JWT.
 */
public interface AuthService {
    
    /**
     * Registra un nuevo usuario en el sistema.
     * Asigna rol ROLE_USER por defecto.
     * Si esProductor=true, asigna también ROLE_PRODUCTOR.
     */
    AuthResponse register(RegisterRequest request);
    
    /**
     * Autentica un usuario y genera un JWT token.
     * Acepta email o username.
     */
    AuthResponse login(LoginRequest request);
    
    /**
     * Verifica si un username ya existe.
     */
    boolean existsByUsername(String username);
    
    /**
     * Verifica si un email ya existe.
     */
    boolean existsByEmail(String email);
    
    /**
     * Solicita un reset de contraseña.
     * Genera token y envía email (implementación futura).
     */
    MessageResponse requestPasswordReset(PasswordResetRequest request);
    
    /**
     * Valida un token JWT.
     * Retorna username si es válido.
     */
    String validateToken(String token);
}
```

---

## ✅ PASO 38.2: UsuarioService Interface

**Archivo:** `UsuarioService.java`

```java
package Fullsound.Fullsound.service;

import Fullsound.Fullsound.model.dto.request.PasswordChangeRequest;
import Fullsound.Fullsound.model.dto.request.UsuarioUpdateRequest;
import Fullsound.Fullsound.model.dto.response.MessageResponse;
import Fullsound.Fullsound.model.dto.response.UsuarioPublicResponse;
import Fullsound.Fullsound.model.dto.response.UsuarioResponse;
import Fullsound.Fullsound.model.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio de gestión de usuarios.
 */
public interface UsuarioService {
    
    /**
     * Obtiene el perfil del usuario autenticado.
     */
    UsuarioResponse getCurrentUser();
    
    /**
     * Obtiene un usuario por ID (respuesta completa).
     */
    UsuarioResponse findById(Long id);
    
    /**
     * Obtiene el perfil público de un usuario.
     */
    UsuarioPublicResponse findPublicProfileById(Long id);
    
    /**
     * Obtiene usuario por username (perfil público).
     */
    UsuarioPublicResponse findByUsername(String username);
    
    /**
     * Lista todos los usuarios (admin).
     */
    Page<UsuarioResponse> findAll(Pageable pageable);
    
    /**
     * Busca usuarios por término (admin).
     */
    Page<UsuarioResponse> searchUsuarios(String searchTerm, Pageable pageable);
    
    /**
     * Actualiza el perfil del usuario autenticado.
     */
    UsuarioResponse updateCurrentUser(UsuarioUpdateRequest request);
    
    /**
     * Cambia la contraseña del usuario autenticado.
     */
    MessageResponse changePassword(PasswordChangeRequest request);
    
    /**
     * Desactiva el usuario autenticado (soft delete).
     */
    MessageResponse deactivateAccount();
    
    /**
     * Activa/Desactiva un usuario (admin).
     */
    MessageResponse toggleUserStatus(Long id);
    
    /**
     * Obtiene la entidad Usuario autenticado actual (uso interno).
     */
    Usuario getCurrentUsuarioEntity();
    
    /**
     * Verifica si el usuario autenticado es productor.
     */
    boolean isCurrentUserProductor();
}
```

---

## ✅ PASO 38.3: BeatService Interface

**Archivo:** `BeatService.java`

```java
package Fullsound.Fullsound.service;

import Fullsound.Fullsound.model.dto.request.BeatCreateRequest;
import Fullsound.Fullsound.model.dto.request.BeatFilterRequest;
import Fullsound.Fullsound.model.dto.request.BeatUpdateRequest;
import Fullsound.Fullsound.model.dto.response.BeatResponse;
import Fullsound.Fullsound.model.dto.response.BeatSummaryResponse;
import Fullsound.Fullsound.model.dto.response.MessageResponse;
import Fullsound.Fullsound.model.enums.EstadoBeat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio de gestión de beats.
 */
public interface BeatService {
    
    /**
     * Crea un nuevo beat (solo productores).
     */
    BeatResponse create(BeatCreateRequest request);
    
    /**
     * Obtiene un beat por ID.
     */
    BeatResponse findById(Long id);
    
    /**
     * Lista todos los beats activos.
     */
    Page<BeatSummaryResponse> findAll(Pageable pageable);
    
    /**
     * Busca beats con filtros avanzados.
     */
    Page<BeatSummaryResponse> searchBeats(BeatFilterRequest filters, Pageable pageable);
    
    /**
     * Obtiene beats por género.
     */
    Page<BeatSummaryResponse> findByGenero(String genero, Pageable pageable);
    
    /**
     * Obtiene beats destacados.
     */
    List<BeatSummaryResponse> findDestacados(int limit);
    
    /**
     * Obtiene beats similares a uno dado.
     */
    List<BeatSummaryResponse> findSimilares(Long beatId, int limit);
    
    /**
     * Obtiene beats del productor autenticado.
     */
    Page<BeatSummaryResponse> findMyBeats(Pageable pageable);
    
    /**
     * Obtiene beats de un productor específico.
     */
    Page<BeatSummaryResponse> findByProductorId(Long productorId, Pageable pageable);
    
    /**
     * Actualiza un beat (solo el productor propietario).
     */
    BeatResponse update(Long id, BeatUpdateRequest request);
    
    /**
     * Cambia el estado de un beat (solo propietario).
     */
    BeatResponse changeEstado(Long id, EstadoBeat nuevoEstado);
    
    /**
     * Elimina un beat (soft delete - cambia estado a INACTIVO).
     */
    MessageResponse delete(Long id);
    
    /**
     * Incrementa reproducciones del beat.
     */
    void incrementarReproducciones(Long id);
    
    /**
     * Incrementa likes del beat.
     */
    MessageResponse toggleLike(Long id);
    
    /**
     * Incrementa descargas del beat (post-compra).
     */
    void incrementarDescargas(Long id);
    
    /**
     * Actualiza rating promedio del beat.
     */
    void updateRatingPromedio(Long id);
}
```

---

## ✅ PASO 38.4: ProductoService Interface

**Archivo:** `ProductoService.java`

```java
package Fullsound.Fullsound.service;

import Fullsound.Fullsound.model.dto.request.ProductoCreateRequest;
import Fullsound.Fullsound.model.dto.request.ProductoUpdateRequest;
import Fullsound.Fullsound.model.dto.response.MessageResponse;
import Fullsound.Fullsound.model.dto.response.ProductoResponse;
import Fullsound.Fullsound.model.enums.CategoriaProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio de gestión de productos.
 */
public interface ProductoService {
    
    /**
     * Crea un nuevo producto (admin).
     */
    ProductoResponse create(ProductoCreateRequest request);
    
    /**
     * Obtiene un producto por ID.
     */
    ProductoResponse findById(Long id);
    
    /**
     * Lista todos los productos disponibles.
     */
    Page<ProductoResponse> findAll(Pageable pageable);
    
    /**
     * Busca productos por término.
     */
    Page<ProductoResponse> searchProductos(String searchTerm, Pageable pageable);
    
    /**
     * Obtiene productos por categoría.
     */
    Page<ProductoResponse> findByCategoria(CategoriaProducto categoria, Pageable pageable);
    
    /**
     * Obtiene productos destacados.
     */
    List<ProductoResponse> findDestacados(int limit);
    
    /**
     * Actualiza un producto (admin).
     */
    ProductoResponse update(Long id, ProductoUpdateRequest request);
    
    /**
     * Elimina un producto (admin).
     */
    MessageResponse delete(Long id);
    
    /**
     * Verifica disponibilidad de stock.
     */
    boolean hasStock(Long productoId, int cantidad);
    
    /**
     * Reduce stock tras compra (transaccional).
     */
    void reducirStock(Long productoId, int cantidad);
    
    /**
     * Incrementa stock (devolución, admin).
     */
    void incrementarStock(Long productoId, int cantidad);
}
```

---

## ✅ PASO 38.5: CarritoService Interface

**Archivo:** `CarritoService.java`

```java
package Fullsound.Fullsound.service;

import Fullsound.Fullsound.model.dto.request.CarritoItemRequest;
import Fullsound.Fullsound.model.dto.request.CarritoItemUpdateRequest;
import Fullsound.Fullsound.model.dto.response.CarritoResponse;
import Fullsound.Fullsound.model.dto.response.MessageResponse;

/**
 * Servicio de gestión del carrito de compras.
 */
public interface CarritoService {
    
    /**
     * Obtiene el carrito del usuario autenticado.
     * Si no existe, lo crea.
     */
    CarritoResponse getMyCarrito();
    
    /**
     * Agrega un item al carrito.
     * Puede ser un beat o un producto.
     */
    CarritoResponse addItem(CarritoItemRequest request);
    
    /**
     * Actualiza la cantidad de un item.
     */
    CarritoResponse updateItem(Long itemId, CarritoItemUpdateRequest request);
    
    /**
     * Elimina un item del carrito.
     */
    CarritoResponse removeItem(Long itemId);
    
    /**
     * Limpia todo el carrito.
     */
    MessageResponse clearCarrito();
    
    /**
     * Obtiene el número de items en el carrito.
     */
    Integer getItemCount();
}
```

---

## ✅ PASO 38.6: PedidoService Interface

**Archivo:** `PedidoService.java`

```java
package Fullsound.Fullsound.service;

import Fullsound.Fullsound.model.dto.request.PedidoCreateRequest;
import Fullsound.Fullsound.model.dto.response.PedidoResponse;
import Fullsound.Fullsound.model.enums.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio de gestión de pedidos.
 */
public interface PedidoService {
    
    /**
     * Crea un pedido desde el carrito del usuario.
     * Genera número de pedido único.
     */
    PedidoResponse createFromCarrito(PedidoCreateRequest request);
    
    /**
     * Obtiene un pedido por ID (solo propietario o admin).
     */
    PedidoResponse findById(Long id);
    
    /**
     * Obtiene un pedido por número de pedido.
     */
    PedidoResponse findByNumeroPedido(String numeroPedido);
    
    /**
     * Lista pedidos del usuario autenticado.
     */
    Page<PedidoResponse> getMyPedidos(Pageable pageable);
    
    /**
     * Lista todos los pedidos (admin).
     */
    Page<PedidoResponse> findAll(Pageable pageable);
    
    /**
     * Filtra pedidos por estado (admin).
     */
    Page<PedidoResponse> findByEstado(EstadoPedido estado, Pageable pageable);
    
    /**
     * Actualiza el estado de un pedido (admin).
     */
    PedidoResponse updateEstado(Long id, EstadoPedido nuevoEstado);
    
    /**
     * Cancela un pedido (solo si está PENDIENTE).
     */
    PedidoResponse cancelarPedido(Long id);
}
```

---

## ✅ PASO 38.7: PagoService Interface

**Archivo:** `PagoService.java`

```java
package Fullsound.Fullsound.service;

import Fullsound.Fullsound.model.dto.request.PagoCreateRequest;
import Fullsound.Fullsound.model.dto.response.MessageResponse;
import Fullsound.Fullsound.model.dto.response.PagoResponse;

/**
 * Servicio de gestión de pagos con integración Stripe.
 */
public interface PagoService {
    
    /**
     * Crea un PaymentIntent en Stripe.
     * Retorna clientSecret para confirmar en frontend.
     */
    PagoResponse createPayment(PagoCreateRequest request);
    
    /**
     * Confirma un pago exitoso.
     * Actualiza pedido y libera productos.
     */
    PagoResponse confirmPayment(String paymentIntentId);
    
    /**
     * Cancela un pago pendiente.
     */
    MessageResponse cancelPayment(Long pagoId);
    
    /**
     * Procesa webhook de Stripe.
     */
    void handleStripeWebhook(String payload, String sigHeader);
    
    /**
     * Obtiene un pago por ID.
     */
    PagoResponse findById(Long id);
    
    /**
     * Obtiene pagos de un pedido.
     */
    PagoResponse findByPedidoId(Long pedidoId);
}
```

---

## ✅ PASO 38.8: ReviewService Interface

**Archivo:** `ReviewService.java`

```java
package Fullsound.Fullsound.service;

import Fullsound.Fullsound.model.dto.request.ReviewCreateRequest;
import Fullsound.Fullsound.model.dto.request.ReviewUpdateRequest;
import Fullsound.Fullsound.model.dto.response.MessageResponse;
import Fullsound.Fullsound.model.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio de gestión de reviews de beats.
 */
public interface ReviewService {
    
    /**
     * Crea una review para un beat.
     * Verifica que el usuario haya comprado el beat.
     */
    ReviewResponse create(ReviewCreateRequest request);
    
    /**
     * Obtiene una review por ID.
     */
    ReviewResponse findById(Long id);
    
    /**
     * Lista reviews de un beat.
     */
    Page<ReviewResponse> findByBeatId(Long beatId, Pageable pageable);
    
    /**
     * Lista reviews del usuario autenticado.
     */
    Page<ReviewResponse> getMyReviews(Pageable pageable);
    
    /**
     * Actualiza una review (solo propietario).
     */
    ReviewResponse update(Long id, ReviewUpdateRequest request);
    
    /**
     * Elimina una review (solo propietario o admin).
     */
    MessageResponse delete(Long id);
    
    /**
     * Verifica si el usuario compró un beat.
     */
    boolean hasUserPurchasedBeat(Long usuarioId, Long beatId);
}
```

---

## ✅ PASO 38.9: EstadisticasService Interface (Bonus)

**Archivo:** `EstadisticasService.java`

```java
package Fullsound.Fullsound.service;

import Fullsound.Fullsound.model.dto.response.EstadisticasResponse;

/**
 * Servicio de generación de estadísticas y analytics.
 */
public interface EstadisticasService {
    
    /**
     * Obtiene estadísticas generales del sistema (admin).
     */
    EstadisticasResponse getEstadisticasGlobales();
    
    /**
     * Obtiene estadísticas del productor autenticado.
     */
    EstadisticasResponse getMyEstadisticas();
    
    /**
     * Obtiene estadísticas de un productor específico (admin).
     */
    EstadisticasResponse getEstadisticasProductor(Long productorId);
}
```

---

## 📋 Checklist PASO 38

- [ ] AuthService.java creada (6 métodos)
- [ ] UsuarioService.java creada (13 métodos)
- [ ] BeatService.java creada (17 métodos)
- [ ] ProductoService.java creada (12 métodos)
- [ ] CarritoService.java creada (6 métodos)
- [ ] PedidoService.java creada (9 métodos)
- [ ] PagoService.java creada (6 métodos)
- [ ] ReviewService.java creada (7 métodos)
- [ ] EstadisticasService.java creada (3 métodos)
- [ ] Todas las interfaces compilan
- [ ] JavaDoc completo en todos los métodos

---

## 📊 Resumen Interfaces

| Service | Métodos | Responsabilidad Principal |
|---------|---------|---------------------------|
| **AuthService** | 6 | Autenticación, JWT, Registro |
| **UsuarioService** | 13 | Gestión de usuarios, perfiles |
| **BeatService** | 17 | CRUD beats, búsqueda, estadísticas |
| **ProductoService** | 12 | CRUD productos, stock |
| **CarritoService** | 6 | Gestión de carrito |
| **PedidoService** | 9 | Creación y gestión de pedidos |
| **PagoService** | 6 | Stripe integration, webhooks |
| **ReviewService** | 7 | Reviews y ratings |
| **EstadisticasService** | 3 | Analytics y dashboards |

**Total:** 9 Servicios con 79 métodos públicos

---

## 🎯 Principios Aplicados

✅ **Interface Segregation** - Interfaces específicas por dominio
✅ **Single Responsibility** - Cada servicio tiene una responsabilidad clara
✅ **Dependency Inversion** - Dependencias en abstracciones, no implementaciones
✅ **Open/Closed** - Extensible sin modificar interfaces
✅ **Documentation First** - JavaDoc en todos los métodos

---

## ⏭️ Siguiente Paso

Continuar con **[09_SERVICES_IMPL.md](09_SERVICES_IMPL.md)** - PASO 39-45 (Implementaciones de Servicios)
