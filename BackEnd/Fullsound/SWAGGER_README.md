# 📚 Documentación API con Swagger/OpenAPI

## 🚀 Acceso a la Documentación

Una vez que el backend esté ejecutándose, puedes acceder a la documentación interactiva en:

### **Swagger UI (Interfaz Visual)**
```
http://localhost:8080/swagger-ui.html
```
O también:
```
http://localhost:8080/swagger-ui/index.html
```

### **OpenAPI Specification (JSON)**
```
http://localhost:8080/api-docs
```

### **OpenAPI Specification (YAML)**
```
http://localhost:8080/api-docs.yaml
```

---

## 🔐 Autenticación en Swagger

Para probar endpoints protegidos con JWT:

1. **Inicia sesión** usando el endpoint `/api/auth/login`
2. **Copia el token JWT** de la respuesta
3. **Haz clic en el botón "Authorize" 🔓** en la parte superior derecha de Swagger UI
4. **Pega el token** en el campo `Value` (sin agregar "Bearer")
5. **Haz clic en "Authorize"** y luego en "Close"

Ahora puedes probar todos los endpoints protegidos.

---

## 📋 Endpoints Documentados

### **🔐 Autenticación** (`/api/auth`)
- `POST /api/auth/register` - Registrar nuevo usuario
- `POST /api/auth/login` - Iniciar sesión y obtener JWT
- `GET /api/auth/health` - Health check del servicio

### **🎵 Beats** (`/api/beats`)
- `POST /api/beats` - Crear beat (admin)
- `GET /api/beats` - Listar beats activos
- `GET /api/beats/{id}` - Obtener beat por ID
- `GET /api/beats/slug/{slug}` - Obtener beat por slug
- `PUT /api/beats/{id}` - Actualizar beat (admin)
- `DELETE /api/beats/{id}` - Eliminar beat (admin)
- `GET /api/beats/featured` - Listar beats destacados
- `GET /api/beats/genre/{genre}` - Buscar por género

### **🛒 Pedidos** (`/api/pedidos`)
- `POST /api/pedidos` - Crear pedido
- `GET /api/pedidos` - Listar mis pedidos
- `GET /api/pedidos/{id}` - Obtener pedido por ID
- `GET /api/pedidos/numero/{numero}` - Buscar por número

### **💳 Pagos** (`/api/pagos`)
- `POST /api/pagos/stripe/create-payment-intent` - Crear intención de pago
- `POST /api/pagos/stripe/webhook` - Webhook de Stripe
- `GET /api/pagos/{id}` - Obtener pago por ID

### **👤 Usuarios** (`/api/usuarios`)
- `GET /api/usuarios/perfil` - Obtener mi perfil
- `PUT /api/usuarios/perfil` - Actualizar mi perfil
- `GET /api/usuarios` - Listar usuarios (admin)
- `GET /api/usuarios/{id}` - Obtener usuario por ID (admin)

---

## 🎨 Características de Swagger UI

### **Try it out**
Puedes ejecutar requests directamente desde la interfaz:
1. Expande el endpoint que quieres probar
2. Haz clic en "Try it out"
3. Completa los parámetros requeridos
4. Haz clic en "Execute"
5. Ve la respuesta en tiempo real

### **Models**
Al final de la página puedes ver todos los esquemas de datos (DTOs, entidades).

### **Servers**
Puedes cambiar entre diferentes servidores:
- Local (http://localhost:8080)
- Docker (http://localhost:8080)
- Producción (https://api.fullsound.com)

---

## 🛠️ Configuración

La configuración de Swagger está en:
```
BackEnd/Fullsound/src/main/java/Fullsound/Fullsound/config/SwaggerConfig.java
```

Propiedades en `application.properties`:
```properties
# Swagger/OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

---

## 📝 Ejemplos de Uso

### **1. Registrar un usuario**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "johndoe",
    "correo": "john@example.com",
    "contraseña": "Password123!"
  }'
```

### **2. Iniciar sesión**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "john@example.com",
    "contraseña": "Password123!"
  }'
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tipo": "Bearer",
  "nombreUsuario": "johndoe",
  "correo": "john@example.com",
  "roles": ["cliente"]
}
```

### **3. Listar beats (con autenticación)**
```bash
curl -X GET http://localhost:8080/api/beats \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

## 🎯 Ventajas de Swagger

✅ **Documentación automática** - Se genera desde el código  
✅ **Interfaz interactiva** - Prueba endpoints sin Postman  
✅ **Validación en vivo** - Ve qué campos son requeridos  
✅ **Ejemplos incluidos** - Cada endpoint tiene ejemplos  
✅ **Sincronización** - Siempre actualizada con el código  
✅ **Estándares OpenAPI** - Compatible con herramientas externas  

---

## 📚 Recursos

- [Springdoc OpenAPI](https://springdoc.org/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)
- [OpenAPI Specification](https://swagger.io/specification/)

---

**Autor:** VECTORG99  
**Versión:** 2.0.0  
**Última actualización:** 2025-11-30
