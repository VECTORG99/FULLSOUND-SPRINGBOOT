# 🏷️ PASO 4: Crear Enumeraciones del Sistema# 🏷️ PASO 4: Crear Enumeraciones del Sistema



## 🎯 Objetivo## 🎯 Objetivo

Definir todos los enums necesarios para el sistema adaptados a la base de datos actual.Definir todos los enums necesarios para el sistema antes de crear las entidades.



------



## 📁 Ubicación Base## 📁 Ubicación Base

``````

Fullsound/src/main/java/Fullsound/Fullsound/model/enums/Fullsound/src/main/java/Fullsound/Fullsound/model/enums/

``````



------



## ✅ Enum 1: RolUsuario (ADAPTADO A BD ACTUAL)## ✅ Enum 1: RolUsuario



**Archivo:** `RolUsuario.java`**Archivo:** `RolUsuario.java`



```java```java

package Fullsound.Fullsound.model.enums;package Fullsound.Fullsound.model.enums;



/**public enum RolUsuario {

 * Roles de usuario adaptados a la base de datos actual.    ROLE_USER,      // Usuario regular (comprador)

 * BD tiene: 'cliente' y 'administrador'    ROLE_PRODUCTOR, // Productor de beats (puede vender)

 */    ROLE_ADMIN      // Administrador del sistema

public enum RolUsuario {}

    CLIENTE("cliente", "Cliente"),```

    ADMINISTRADOR("administrador", "Administrador");

    **Uso:** Define los roles de usuarios en el sistema.

    private final String dbValue;

    private final String displayName;---

    

    RolUsuario(String dbValue, String displayName) {## ✅ Enum 2: EstadoBeat

        this.dbValue = dbValue;

        this.displayName = displayName;**Archivo:** `EstadoBeat.java`

    }

    ```java

    public String getDbValue() {package Fullsound.Fullsound.model.enums;

        return dbValue;

    }public enum EstadoBeat {

        DISPONIBLE,  // Beat disponible para compra

    public String getDisplayName() {    VENDIDO,     // Beat vendido con licencia exclusiva

        return displayName;    RESERVADO,   // Beat reservado temporalmente

    }    INACTIVO     // Beat no visible para compra

    }

    /**```

     * Obtiene el enum desde el valor de la base de datos

     */**Uso:** Estados del ciclo de vida de un beat.

    public static RolUsuario fromDbValue(String dbValue) {

        for (RolUsuario rol : values()) {---

            if (rol.dbValue.equalsIgnoreCase(dbValue)) {

                return rol;## ✅ Enum 3: EstadoPedido

            }

        }**Archivo:** `EstadoPedido.java`

        throw new IllegalArgumentException("Rol no válido: " + dbValue);

    }```java

    package Fullsound.Fullsound.model.enums;

    /**

     * Verifica si el rol es ADMINISTRADORpublic enum EstadoPedido {

     */    PENDIENTE,    // Pedido creado, esperando pago

    public boolean isAdmin() {    PROCESANDO,   // Pago confirmado, procesando pedido

        return this == ADMINISTRADOR;    COMPLETADO,   // Pedido entregado/completado

    }    CANCELADO,    // Pedido cancelado

        REEMBOLSADO   // Pedido reembolsado

    /**}

     * Verifica si el rol es CLIENTE```

     */

    public boolean isCliente() {**Uso:** Estados del pedido en el flujo de compra.

        return this == CLIENTE;

    }---

}

```## ✅ Enum 4: EstadoPago



**Uso:** Define los roles de usuarios en el sistema (mapea a tabla `tipo_usuario`).**Archivo:** `EstadoPago.java`



---```java

package Fullsound.Fullsound.model.enums;

## ✅ Enum 2: EstadoBeat

public enum EstadoPago {

**Archivo:** `EstadoBeat.java`    PENDIENTE,    // Pago iniciado pero no confirmado

    PROCESANDO,   // Stripe procesando el pago

```java    EXITOSO,      // Pago confirmado y exitoso

package Fullsound.Fullsound.model.enums;    FALLIDO,      // Pago rechazado o fallido

    REEMBOLSADO   // Pago reembolsado al cliente

/**}

 * Estados del ciclo de vida de un beat```

 */

public enum EstadoBeat {**Uso:** Estados del proceso de pago.

    DISPONIBLE("Disponible para compra"),

    VENDIDO("Vendido con licencia exclusiva"),---

    RESERVADO("Reservado temporalmente"),

    INACTIVO("No visible para compra");## ✅ Enum 5: MetodoPago

    

    private final String descripcion;**Archivo:** `MetodoPago.java`

    

    EstadoBeat(String descripcion) {```java

        this.descripcion = descripcion;package Fullsound.Fullsound.model.enums;

    }

    public enum MetodoPago {

    public String getDescripcion() {    STRIPE,         // Tarjeta de crédito/débito vía Stripe

        return descripcion;    PAYPAL,         // PayPal (futuro)

    }    TRANSFERENCIA,  // Transferencia bancaria (futuro)

        EFECTIVO        // Efectivo (solo para admin)

    /**}

     * Verifica si el beat puede ser comprado```

     */

    public boolean esComprable() {**Uso:** Métodos de pago disponibles.

        return this == DISPONIBLE || this == RESERVADO;

    }---

}

```## ✅ Enum 6: TipoLicencia



**Uso:** Estados del ciclo de vida de un beat (columna `estado` en tabla `beat`).**Archivo:** `TipoLicencia.java`



---```java

package Fullsound.Fullsound.model.enums;

## ✅ Enum 3: EstadoPedido (Compra)

public enum TipoLicencia {

**Archivo:** `EstadoPedido.java`    BASICA,       // Uso personal, sin monetización

    PREMIUM,      // Streaming hasta 500K reproducciones

```java    PROFESIONAL,  // Distribución ilimitada, incluye stems

package Fullsound.Fullsound.model.enums;    EXCLUSIVA     // Derechos completos, sin otros compradores

}

/**```

 * Estados de un pedido (compra)

 */**Uso:** Tipos de licencias para beats.

public enum EstadoPedido {

    PENDIENTE("Pendiente de pago"),**Descripción detallada:**

    PROCESANDO("Procesando pago"),- **BASICA**: Solo uso personal, no comercial

    COMPLETADO("Completado y pagado"),- **PREMIUM**: Permite monetización en YouTube, SoundCloud, streaming limitado

    CANCELADO("Cancelado por el usuario"),- **PROFESIONAL**: Distribución completa, incluye stems, sin límites

    REEMBOLSADO("Reembolsado");- **EXCLUSIVA**: Comprador obtiene derechos exclusivos, beat se marca como VENDIDO

    

    private final String descripcion;---

    

    EstadoPedido(String descripcion) {## ✅ Enum 7: CategoriaProducto

        this.descripcion = descripcion;

    }**Archivo:** `CategoriaProducto.java`

    

    public String getDescripcion() {```java

        return descripcion;package Fullsound.Fullsound.model.enums;

    }

    public enum CategoriaProducto {

    /**    BEAT,           // Beats individuales

     * Verifica si el pedido está finalizado (completado o cancelado)    SAMPLE_PACK,    // Paquetes de samples (drums, melodías, etc.)

     */    PRESET,         // Presets de sintetizadores (Serum, Vital, etc.)

    public boolean esFinal() {    PLUGIN,         // Plugins VST/AU

        return this == COMPLETADO || this == CANCELADO || this == REEMBOLSADO;    CURSO,          // Cursos de producción musical

    }    SERVICIO,       // Servicios (mezcla, mastering, ghost production)

        MERCHANDISING   // Ropa, accesorios, merchandising

    /**}

     * Verifica si el pedido fue exitoso```

     */

    public boolean esExitoso() {**Uso:** Categorías de productos en la tienda.

        return this == COMPLETADO;

    }---

}

```## 🧪 Verificación



**Uso:** Estados de un pedido (columna `estado` en tabla `compra`).### Compilar los enums



---```bash

cd Fullsound

## ✅ Enum 4: MetodoPagomvn clean compile

```

**Archivo:** `MetodoPago.java`

### Verificar en IDE

```java

package Fullsound.Fullsound.model.enums;Verifica que todos los enums:

- No tienen errores de compilación

/**- Están en el paquete correcto: `Fullsound.Fullsound.model.enums`

 * Métodos de pago disponibles- Son accesibles desde otras clases

 */

public enum MetodoPago {---

    STRIPE("Tarjeta de crédito/débito (Stripe)"),

    PAYPAL("PayPal"),## 📋 Resumen de Enums Creados

    TRANSFERENCIA("Transferencia bancaria");

    | Enum | Archivo | Valores | Uso |

    private final String descripcion;|------|---------|---------|-----|

    | **RolUsuario** | `RolUsuario.java` | 3 roles | Autenticación y autorización |

    MetodoPago(String descripcion) {| **EstadoBeat** | `EstadoBeat.java` | 4 estados | Gestión de beats |

        this.descripcion = descripcion;| **EstadoPedido** | `EstadoPedido.java` | 5 estados | Flujo de pedidos |

    }| **EstadoPago** | `EstadoPago.java` | 5 estados | Proceso de pago |

    | **MetodoPago** | `MetodoPago.java` | 4 métodos | Opciones de pago |

    public String getDescripcion() {| **TipoLicencia** | `TipoLicencia.java` | 4 tipos | Licencias de beats |

        return descripcion;| **CategoriaProducto** | `CategoriaProducto.java` | 7 categorías | Clasificación de productos |

    }

}**Total:** 7 enumeraciones con 32 valores

```

---

**Uso:** Método de pago utilizado (tabla `pago`).

## 📋 Checklist PASO 4

---

- [ ] RolUsuario.java creado

## ✅ Enum 5: EstadoPago- [ ] EstadoBeat.java creado

- [ ] EstadoPedido.java creado

**Archivo:** `EstadoPago.java`- [ ] EstadoPago.java creado

- [ ] MetodoPago.java creado

```java- [ ] TipoLicencia.java creado

package Fullsound.Fullsound.model.enums;- [ ] CategoriaProducto.java creado

- [ ] Todos los enums compilan sin errores

/**- [ ] Enums accesibles desde otros paquetes

 * Estados de un pago

 */---

public enum EstadoPago {

    PENDIENTE("Pendiente de procesamiento"),## ⏭️ Siguiente Paso

    PROCESANDO("Procesando con pasarela de pago"),

    EXITOSO("Pago exitoso"),Continuar con **[03_ENTIDADES_JPA.md](03_ENTIDADES_JPA.md)** - PASO 5-12 (Entidades JPA)

    FALLIDO("Pago fallido"),
    REEMBOLSADO("Pago reembolsado");
    
    private final String descripcion;
    
    EstadoPago(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Verifica si el pago fue exitoso
     */
    public boolean esExitoso() {
        return this == EXITOSO;
    }
    
    /**
     * Verifica si el pago está pendiente o procesando
     */
    public boolean estaPendiente() {
        return this == PENDIENTE || this == PROCESANDO;
    }
    
    /**
     * Verifica si el pago falló
     */
    public boolean fallo() {
        return this == FALLIDO;
    }
}
```

**Uso:** Estado de un pago (tabla `pago`).

---

## 🧪 Verificación

### Compilar Enums

```bash
cd Fullsound
mvn clean compile
```

### Verificar que no hay errores de compilación

Los enums deben compilarse sin errores.

---

## 📋 Checklist PASO 4

- [ ] RolUsuario.java creado (ADAPTADO: cliente, administrador)
- [ ] EstadoBeat.java creado
- [ ] EstadoPedido.java creado
- [ ] MetodoPago.java creado
- [ ] EstadoPago.java creado
- [ ] Todos los enums compilan sin errores
- [ ] Métodos helper implementados

---

## ⏭️ Siguiente Paso

Continuar con **[03_ENTIDADES_JPA.md](03_ENTIDADES_JPA.md)** - PASO 5-12 (Entidades JPA adaptadas a BD actual)

---

## 📝 Notas Importantes

### Cambios respecto a la versión original:

1. ✅ **RolUsuario:** Adaptado a BD actual con valores `cliente` y `administrador` (no `ROLE_USER`, `ROLE_PRODUCTOR`, `ROLE_ADMIN`)
2. ❌ **TipoLicencia:** ELIMINADO (no existe en BD actual)
3. ❌ **CategoriaProducto:** ELIMINADO (no hay tabla productos)
4. ✅ **EstadoBeat, EstadoPedido, MetodoPago, EstadoPago:** Mantenidos para funcionalidad completa

### Mapeo a Base de Datos:

| Enum | Tabla | Columna | Tipo |
|------|-------|---------|------|
| RolUsuario | `tipo_usuario` | `nombre_tipo` | VARCHAR(50) |
| EstadoBeat | `beat` | `estado` | VARCHAR(20) |
| EstadoPedido | `compra` | `estado` | VARCHAR(20) |
| MetodoPago | `pago` | `metodo_pago` | VARCHAR(20) |
| EstadoPago | `pago` | `estado_pago` | VARCHAR(20) |
