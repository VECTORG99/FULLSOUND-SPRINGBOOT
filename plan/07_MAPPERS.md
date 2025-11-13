# 🔄 PASO 31-37: Mappers MapStruct

## 🎯 Objetivo
Crear mappers con MapStruct para convertir automáticamente entre Entities y DTOs, evitando código boilerplate y errores manuales.

---

## 📁 Ubicación Base
```
Fullsound/src/main/java/Fullsound/Fullsound/mapper/
```

---

## 📌 Configuración MapStruct

### Dependencias ya incluidas en pom.xml (Paso 1)

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>

<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

---

## ✅ PASO 31: Mapper de Usuario

**Archivo:** `UsuarioMapper.java`

```java
package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.model.dto.request.RegisterRequest;
import Fullsound.Fullsound.model.dto.request.UsuarioUpdateRequest;
import Fullsound.Fullsound.model.dto.response.UsuarioPublicResponse;
import Fullsound.Fullsound.model.dto.response.UsuarioResponse;
import Fullsound.Fullsound.model.entity.Rol;
import Fullsound.Fullsound.model.entity.Usuario;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper para conversiones de Usuario.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UsuarioMapper {
    
    // Entity -> Response DTO
    @Mapping(target = "roles", expression = "java(mapRolesToStrings(usuario.getRoles()))")
    @Mapping(target = "totalBeats", expression = "java(usuario.getBeats() != null ? usuario.getBeats().size() : 0)")
    @Mapping(target = "totalVentas", ignore = true) // Se calcula en service
    UsuarioResponse toResponse(Usuario usuario);
    
    // Entity -> Public Response
    @Mapping(target = "totalBeats", expression = "java(usuario.getBeats() != null ? usuario.getBeats().size() : 0)")
    @Mapping(target = "totalSeguidores", ignore = true) // Implementar en futuro
    UsuarioPublicResponse toPublicResponse(Usuario usuario);
    
    // Request -> Entity (para registro)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Se encripta en service
    @Mapping(target = "roles", ignore = true) // Se asigna en service
    @Mapping(target = "activo", constant = "true")
    @Mapping(target = "beats", ignore = true)
    @Mapping(target = "carrito", ignore = true)
    @Mapping(target = "pedidos", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Usuario toEntity(RegisterRequest request);
    
    // Update Request -> Entity (actualización parcial)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "beats", ignore = true)
    @Mapping(target = "carrito", ignore = true)
    @Mapping(target = "pedidos", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    void updateEntityFromRequest(UsuarioUpdateRequest request, @MappingTarget Usuario usuario);
    
    // Helper methods
    default Set<String> mapRolesToStrings(Set<Rol> roles) {
        if (roles == null) return Set.of();
        return roles.stream()
                .map(rol -> "ROLE_" + rol.getNombre().name())
                .collect(Collectors.toSet());
    }
}
```

---

## ✅ PASO 32: Mapper de Beat

**Archivo:** `BeatMapper.java`

```java
package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.model.dto.request.BeatCreateRequest;
import Fullsound.Fullsound.model.dto.request.BeatUpdateRequest;
import Fullsound.Fullsound.model.dto.response.BeatResponse;
import Fullsound.Fullsound.model.dto.response.BeatSummaryResponse;
import Fullsound.Fullsound.model.entity.Beat;
import org.mapstruct.*;

/**
 * Mapper para conversiones de Beat.
 */
@Mapper(
    componentModel = "spring",
    uses = {UsuarioMapper.class}, // Usa UsuarioMapper para mapear productor
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BeatMapper {
    
    // Entity -> Response DTO
    @Mapping(target = "productor", source = "productor")
    BeatResponse toResponse(Beat beat);
    
    // Entity -> Summary Response
    @Mapping(target = "productorId", source = "productor.id")
    @Mapping(target = "productorUsername", source = "productor.username")
    @Mapping(target = "productorAvatar", source = "productor.avatarUrl")
    BeatSummaryResponse toSummaryResponse(Beat beat);
    
    // Create Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productor", ignore = true) // Se asigna en service
    @Mapping(target = "estado", constant = "ACTIVO")
    @Mapping(target = "audioUrl", ignore = true) // Se maneja en file upload
    @Mapping(target = "imagenUrl", ignore = true)
    @Mapping(target = "waveformUrl", ignore = true)
    @Mapping(target = "reproducciones", constant = "0")
    @Mapping(target = "descargas", constant = "0")
    @Mapping(target = "likes", constant = "0")
    @Mapping(target = "ratingPromedio", constant = "0.0")
    @Mapping(target = "totalReviews", constant = "0")
    @Mapping(target = "reviews", ignore = true)
    Beat toEntity(BeatCreateRequest request);
    
    // Update Request -> Entity (actualización parcial)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productor", ignore = true)
    @Mapping(target = "audioUrl", ignore = true)
    @Mapping(target = "imagenUrl", ignore = true)
    @Mapping(target = "waveformUrl", ignore = true)
    @Mapping(target = "reproducciones", ignore = true)
    @Mapping(target = "descargas", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "ratingPromedio", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    void updateEntityFromRequest(BeatUpdateRequest request, @MappingTarget Beat beat);
}
```

---

## ✅ PASO 33: Mapper de Producto

**Archivo:** `ProductoMapper.java`

```java
package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.model.dto.request.ProductoCreateRequest;
import Fullsound.Fullsound.model.dto.request.ProductoUpdateRequest;
import Fullsound.Fullsound.model.dto.response.ProductoResponse;
import Fullsound.Fullsound.model.entity.Producto;
import org.mapstruct.*;

/**
 * Mapper para conversiones de Producto.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductoMapper {
    
    // Entity -> Response DTO
    @Mapping(target = "disponible", expression = "java(producto.isDisponible())")
    ProductoResponse toResponse(Producto producto);
    
    // Create Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imagenUrl", ignore = true) // Se maneja en file upload
    @Mapping(target = "totalVendidos", constant = "0")
    @Mapping(target = "ratingPromedio", constant = "0.0")
    Producto toEntity(ProductoCreateRequest request);
    
    // Update Request -> Entity (actualización parcial)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imagenUrl", ignore = true)
    @Mapping(target = "totalVendidos", ignore = true)
    @Mapping(target = "ratingPromedio", ignore = true)
    void updateEntityFromRequest(ProductoUpdateRequest request, @MappingTarget Producto producto);
}
```

---

## ✅ PASO 34: Mapper de Carrito

**Archivo:** `CarritoMapper.java`

```java
package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.model.dto.response.CarritoItemResponse;
import Fullsound.Fullsound.model.dto.response.CarritoResponse;
import Fullsound.Fullsound.model.entity.Carrito;
import Fullsound.Fullsound.model.entity.CarritoItem;
import org.mapstruct.*;

/**
 * Mapper para conversiones de Carrito.
 */
@Mapper(
    componentModel = "spring",
    uses = {BeatMapper.class, ProductoMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CarritoMapper {
    
    // Entity -> Response DTO
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalItems", expression = "java(carrito.getTotalItems())")
    @Mapping(target = "subtotal", expression = "java(carrito.getSubtotal())")
    @Mapping(target = "descuentos", expression = "java(carrito.getDescuentos())")
    @Mapping(target = "total", expression = "java(carrito.getTotal())")
    CarritoResponse toResponse(Carrito carrito);
    
    // CarritoItem Entity -> Response DTO
    @Mapping(target = "beat", source = "beat")
    @Mapping(target = "producto", source = "producto")
    @Mapping(target = "precioUnitario", expression = "java(item.getPrecioUnitario())")
    @Mapping(target = "subtotal", expression = "java(item.getSubtotal())")
    CarritoItemResponse toItemResponse(CarritoItem item);
}
```

---

## ✅ PASO 35: Mapper de Pedido

**Archivo:** `PedidoMapper.java`

```java
package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.model.dto.request.PedidoCreateRequest;
import Fullsound.Fullsound.model.dto.response.PedidoItemResponse;
import Fullsound.Fullsound.model.dto.response.PedidoResponse;
import Fullsound.Fullsound.model.entity.Pedido;
import Fullsound.Fullsound.model.entity.PedidoItem;
import org.mapstruct.*;

/**
 * Mapper para conversiones de Pedido.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PedidoMapper {
    
    // Entity -> Response DTO
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "usuarioEmail", source = "usuario.email")
    @Mapping(target = "items", source = "items")
    PedidoResponse toResponse(Pedido pedido);
    
    // PedidoItem Entity -> Response DTO
    @Mapping(target = "beatId", source = "beat.id")
    @Mapping(target = "beatTitulo", source = "beat.titulo")
    @Mapping(target = "beatAudioUrl", source = "beat.audioUrl")
    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    PedidoItemResponse toItemResponse(PedidoItem item);
    
    // Create Request -> Entity (básico, se completa en service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numeroPedido", ignore = true) // Se genera en service
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "estado", constant = "PENDIENTE")
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "descuentos", ignore = true)
    @Mapping(target = "impuestos", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "fechaEntrega", ignore = true)
    Pedido toEntity(PedidoCreateRequest request);
}
```

---

## ✅ PASO 36: Mapper de Pago

**Archivo:** `PagoMapper.java`

```java
package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.model.dto.response.PagoResponse;
import Fullsound.Fullsound.model.entity.Pago;
import org.mapstruct.*;

/**
 * Mapper para conversiones de Pago.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PagoMapper {
    
    // Entity -> Response DTO
    @Mapping(target = "pedidoId", source = "pedido.id")
    @Mapping(target = "numeroPedido", source = "pedido.numeroPedido")
    PagoResponse toResponse(Pago pago);
}
```

---

## ✅ PASO 37: Mapper de Review

**Archivo:** `ReviewMapper.java`

```java
package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.model.dto.request.ReviewCreateRequest;
import Fullsound.Fullsound.model.dto.request.ReviewUpdateRequest;
import Fullsound.Fullsound.model.dto.response.ReviewResponse;
import Fullsound.Fullsound.model.entity.Review;
import org.mapstruct.*;

/**
 * Mapper para conversiones de Review.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ReviewMapper {
    
    // Entity -> Response DTO
    @Mapping(target = "beatId", source = "beat.id")
    @Mapping(target = "beatTitulo", source = "beat.titulo")
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "usuarioUsername", source = "usuario.username")
    @Mapping(target = "usuarioAvatar", source = "usuario.avatarUrl")
    ReviewResponse toResponse(Review review);
    
    // Create Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "beat", ignore = true) // Se asigna en service
    @Mapping(target = "usuario", ignore = true) // Se obtiene de SecurityContext
    @Mapping(target = "verificado", constant = "false") // Se verifica en service
    Review toEntity(ReviewCreateRequest request);
    
    // Update Request -> Entity (actualización parcial)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "beat", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "verificado", ignore = true)
    void updateEntityFromRequest(ReviewUpdateRequest request, @MappingTarget Review review);
}
```

---

## 🔧 Configuración Adicional de MapStruct

### MapperConfig (Opcional - Configuración compartida)

**Archivo:** `MapperConfig.java`

```java
package Fullsound.Fullsound.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Configuración base compartida para todos los mappers.
 */
@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MapperConfig {
}
```

**Uso en mappers:**
```java
@Mapper(config = MapperConfig.class, uses = {UsuarioMapper.class})
public interface BeatMapper {
    // ...
}
```

---

## 🧪 Verificación

### 1. Compilar para generar implementaciones

```bash
cd Fullsound
mvn clean compile
```

MapStruct generará las implementaciones en:
```
target/generated-sources/annotations/Fullsound/Fullsound/mapper/
```

### 2. Verificar implementaciones generadas

```bash
ls target/generated-sources/annotations/Fullsound/Fullsound/mapper/
```

Deberías ver archivos como:
- `UsuarioMapperImpl.java`
- `BeatMapperImpl.java`
- `ProductoMapperImpl.java`
- etc.

### 3. Test de Mapper (Ejemplo)

```java
package Fullsound.Fullsound.mapper;

import Fullsound.Fullsound.model.dto.request.BeatCreateRequest;
import Fullsound.Fullsound.model.dto.response.BeatResponse;
import Fullsound.Fullsound.model.entity.Beat;
import Fullsound.Fullsound.model.entity.Usuario;
import Fullsound.Fullsound.model.enums.TipoLicencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BeatMapperTest {
    
    @Autowired
    private BeatMapper beatMapper;
    
    @Test
    void shouldMapBeatCreateRequestToEntity() {
        // Given
        BeatCreateRequest request = new BeatCreateRequest();
        request.setTitulo("Test Beat");
        request.setGenero("Trap");
        request.setBpm(140);
        request.setPrecio(new BigDecimal("50.00"));
        request.setTipoLicencia(TipoLicencia.BASICA);
        
        // When
        Beat beat = beatMapper.toEntity(request);
        
        // Then
        assertThat(beat.getTitulo()).isEqualTo("Test Beat");
        assertThat(beat.getGenero()).isEqualTo("Trap");
        assertThat(beat.getBpm()).isEqualTo(140);
        assertThat(beat.getPrecio()).isEqualByComparingTo("50.00");
        assertThat(beat.getReproduciones()).isEqualTo(0);
    }
    
    @Test
    void shouldMapBeatEntityToResponse() {
        // Given
        Usuario productor = new Usuario();
        productor.setId(1L);
        productor.setUsername("producer1");
        
        Beat beat = new Beat();
        beat.setId(1L);
        beat.setTitulo("Dark Trap");
        beat.setGenero("Trap");
        beat.setBpm(140);
        beat.setProductor(productor);
        beat.setReproduciones(1000);
        beat.setLikes(50);
        
        // When
        BeatResponse response = beatMapper.toResponse(beat);
        
        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitulo()).isEqualTo("Dark Trap");
        assertThat(response.getReproduciones()).isEqualTo(1000);
        assertThat(response.getProductor()).isNotNull();
        assertThat(response.getProductor().getUsername()).isEqualTo("producer1");
    }
}
```

---

## 📋 Checklist PASO 31-37

- [ ] UsuarioMapper.java creado
- [ ] BeatMapper.java creado
- [ ] ProductoMapper.java creado
- [ ] CarritoMapper.java creado
- [ ] PedidoMapper.java creado
- [ ] PagoMapper.java creado
- [ ] ReviewMapper.java creado
- [ ] MapperConfig.java creado (opcional)
- [ ] `mvn clean compile` ejecutado exitosamente
- [ ] Implementaciones generadas en target/generated-sources
- [ ] Tests de mappers creados y pasando
- [ ] @Autowired funciona en servicios

---

## 📊 Resumen Mappers

| Mapper | Request DTOs | Response DTOs | Helper Methods |
|--------|--------------|---------------|----------------|
| **UsuarioMapper** | 2 | 2 | mapRolesToStrings |
| **BeatMapper** | 2 | 2 | - |
| **ProductoMapper** | 2 | 1 | - |
| **CarritoMapper** | 0 | 2 | - |
| **PedidoMapper** | 1 | 2 | - |
| **PagoMapper** | 0 | 1 | - |
| **ReviewMapper** | 2 | 1 | - |

**Total:** 7 Mappers con ~50 métodos de conversión automáticos

---

## 🎯 Ventajas de MapStruct

✅ **Código generado en compile-time** (no reflection)
✅ **Type-safe** (errores en compilación, no en runtime)
✅ **Alta performance** (simple assignments)
✅ **Fácil debugging** (código generado es legible)
✅ **Soporte Lombok** (funciona con @Builder, @Data)
✅ **Actualizaciones parciales** con @MappingTarget
✅ **Mapeos complejos** con expressions Java

---

## ⚠️ Consideraciones Importantes

1. **Recompilar después de cambios en DTOs/Entities:**
   ```bash
   mvn clean compile
   ```

2. **IDE puede mostrar errores fantasma** - ignorar si compila OK

3. **Actualización parcial** usa `NullValuePropertyMappingStrategy.IGNORE`

4. **Collections** se mapean automáticamente

5. **Nested objects** requieren especificar mapper con `uses`

---

## ⏭️ Siguiente Paso

Continuar con **[08_SERVICES_INTERFACES.md](08_SERVICES_INTERFACES.md)** - PASO 38 (Interfaces de Servicios)
