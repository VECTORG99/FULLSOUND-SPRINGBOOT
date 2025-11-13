# ✅ CHECKLIST DE IMPLEMENTACIÓN - FULLSOUND SPRING BOOT

## 📋 ANTES DE EMPEZAR

- [ ] Java 17 instalado y configurado
- [ ] Maven 3.8+ instalado
- [ ] MySQL 8.0+ instalado y corriendo
- [ ] IDE configurado (IntelliJ IDEA / Eclipse / VS Code)
- [ ] Cuenta de Stripe (para API keys de testing)
- [ ] Git configurado

---

## 🗄️ FASE 1: BASE DE DATOS (30 minutos)

### Backup y Preparación
- [ ] Crear backup de la BD actual
  ```bash
  mysqldump -u root -p Fullsound_Base > backup_fullsound_$(date +%Y%m%d).sql
  ```

### Ejecutar Migración
- [ ] Ejecutar `DATABASE_MIGRATION.sql`
  ```bash
  mysql -u root -p < plan/DATABASE_MIGRATION.sql
  ```

### Verificación
- [ ] Verificar que todas las tablas existen
  ```sql
  USE Fullsound_Base;
  SHOW TABLES;
  -- Debe mostrar: tipo_usuario, usuario, beat, compra, compra_detalle, pago, usuario_roles
  ```

- [ ] Verificar estructura de tabla `usuario`
  ```sql
  DESCRIBE usuario;
  -- Verificar: nombre_completo, telefono, biografia, url_avatar, email_verificado, etc.
  ```

- [ ] Verificar estructura de tabla `beat`
  ```sql
  DESCRIBE beat;
  -- Verificar: slug, bpm, tonalidad, mood, tags, estado, estadísticas, etc.
  -- NO debe tener: precio_formateado, enlace_producto
  ```

- [ ] Verificar tabla `pago` fue creada
  ```sql
  DESCRIBE pago;
  ```

- [ ] Verificar datos de prueba
  ```sql
  SELECT COUNT(*) FROM usuario;  -- Debe ser 12
  SELECT COUNT(*) FROM beat;     -- Debe ser 9
  SELECT COUNT(*) FROM compra;   -- Debe ser 5
  ```

---

## ⚙️ FASE 2: CONFIGURACIÓN (15 minutos)

### Application Properties
- [ ] Abrir `Fullsound/src/main/resources/application.properties`
- [ ] Verificar URL de BD: `jdbc:mysql://localhost:3306/Fullsound_Base`
- [ ] Configurar password de MySQL
- [ ] Configurar JWT secret (cambiar en producción)
- [ ] Configurar Stripe API key (obtener de dashboard)
- [ ] Verificar `spring.jpa.hibernate.ddl-auto=validate`

### POM.xml
- [ ] Verificar dependencias en `Fullsound/pom.xml`
- [ ] Spring Boot 3.2.0
- [ ] MySQL Connector
- [ ] Spring Security
- [ ] JWT (jjwt 0.12.3)
- [ ] MapStruct 1.5.5
- [ ] Stripe Java SDK 24.3.0
- [ ] SpringDoc OpenAPI 2.3.0
- [ ] Lombok

---

## 🏷️ FASE 3: ENUMERACIONES (15 minutos)

Crear en `Fullsound/src/main/java/Fullsound/Fullsound/model/enums/`:

- [ ] `RolUsuario.java`
  - CLIENTE("cliente"), ADMINISTRADOR("administrador")
  - Método `fromDbValue()`
  
- [ ] `EstadoBeat.java`
  - DISPONIBLE, VENDIDO, RESERVADO, INACTIVO
  
- [ ] `EstadoPedido.java`
  - PENDIENTE, PROCESANDO, COMPLETADO, CANCELADO, REEMBOLSADO
  
- [ ] `MetodoPago.java`
  - STRIPE, PAYPAL, TRANSFERENCIA
  
- [ ] `EstadoPago.java`
  - PENDIENTE, PROCESANDO, EXITOSO, FALLIDO, REEMBOLSADO

### Verificación
- [ ] Compilar enums
  ```bash
  mvn clean compile
  ```

---

## 🗃️ FASE 4: ENTIDADES JPA (45 minutos)

### Configuración
- [ ] Crear `config/JpaConfig.java`
  - @EnableJpaAuditing
  - @EnableJpaRepositories
  - AuditorAware bean

### Entidades
Crear en `Fullsound/src/main/java/Fullsound/Fullsound/model/entity/`:

- [ ] `Rol.java`
  - @Table(name = "tipo_usuario")
  - @Column(name = "id_tipo_usuario") Integer id
  - @Column(name = "nombre_tipo") String nombre
  
- [ ] `Usuario.java`
  - @Table(name = "usuario")
  - @Column(name = "id_usuario") Integer id
  - @Column(name = "nombre_usuario") String username
  - @Column(name = "correo") String email
  - @ManyToMany con roles (tabla usuario_roles)
  
- [ ] `Beat.java`
  - @Table(name = "beat")
  - @Column(name = "id_beat") Integer id
  - @Transient getPrecioFormateado()
  - @Transient getEnlaceProducto()
  
- [ ] `Pedido.java`
  - @Table(name = "compra")
  - @Column(name = "id_compra") Integer id
  - @Column(name = "numero_pedido") String numeroPedido
  
- [ ] `PedidoItem.java`
  - @Table(name = "compra_detalle")
  - @Column(name = "id_detalle") Integer id
  
- [ ] `Pago.java`
  - @Table(name = "pago")
  - @Column(name = "id_pago") Integer id
  - Stripe integration fields

### Verificación
- [ ] Compilar entidades
  ```bash
  mvn clean compile
  ```
- [ ] Verificar en logs que Hibernate mapea correctamente
- [ ] No debe haber errores de mapeo

---

## 📚 FASE 5: REPOSITORIES (30 minutos)

Crear en `Fullsound/src/main/java/Fullsound/Fullsound/repository/`:

- [ ] `RolRepository.java`
  - extends JpaRepository<Rol, Integer>
  - findByNombre(String nombre)
  
- [ ] `UsuarioRepository.java`
  - extends JpaRepository<Usuario, Integer>
  - findByUsername(String username)
  - findByEmail(String email)
  - existsByUsername(String username)
  - existsByEmail(String email)
  
- [ ] `BeatRepository.java`
  - extends JpaRepository<Beat, Integer>
  - findByGenero(String genero)
  - findByEstado(EstadoBeat estado)
  - findBySlug(String slug)
  - findByActivoTrueAndEstado(EstadoBeat estado, Pageable pageable)
  
- [ ] `PedidoRepository.java`
  - extends JpaRepository<Pedido, Integer>
  - findByUsuarioId(Integer usuarioId)
  - findByNumeroPedido(String numeroPedido)
  
- [ ] `PedidoItemRepository.java`
  - extends JpaRepository<PedidoItem, Integer>
  - findByPedidoId(Integer pedidoId)
  
- [ ] `PagoRepository.java`
  - extends JpaRepository<Pago, Integer>
  - findByStripePaymentIntentId(String paymentIntentId)
  - findByPedidoId(Integer pedidoId)

### Verificación
- [ ] Compilar repositories
- [ ] Verificar que Spring detecta los repositories en logs

---

## 📦 FASE 6: DTOs (1 hora)

### Request DTOs
Crear en `Fullsound/src/main/java/Fullsound/Fullsound/model/dto/request/`:

#### auth/
- [ ] `LoginRequest.java` (@NotBlank username, password)
- [ ] `RegisterRequest.java` (validaciones @Email, @Size)
- [ ] `PasswordResetRequest.java`
- [ ] `CambiarPasswordRequest.java`

#### usuario/
- [ ] `UsuarioUpdateRequest.java`

#### beat/
- [ ] `BeatCreateRequest.java` (validaciones @Min, @DecimalMin)
- [ ] `BeatUpdateRequest.java`
- [ ] `BeatFilterRequest.java` (genero, precioMin, precioMax, etc.)

#### pago/
- [ ] `PagoCreateRequest.java`
- [ ] `ConfirmarPagoRequest.java`

### Response DTOs
Crear en `Fullsound/src/main/java/Fullsound/Fullsound/model/dto/response/`:

#### common/
- [ ] `ApiResponse<T>.java` (success, message, data)
- [ ] `PageResponse<T>.java` (content, pageNumber, totalPages, etc.)
- [ ] `ErrorResponse.java` (timestamp, status, error, message, path)
- [ ] `MessageResponse.java` (message)

#### Específicos
- [ ] `AuthResponse.java` (token, type, usuario)
- [ ] `UsuarioResponse.java`
- [ ] `BeatResponse.java`
- [ ] `PedidoResponse.java`
- [ ] `PedidoItemResponse.java`
- [ ] `PagoResponse.java`

### Verificación
- [ ] Compilar DTOs
- [ ] Verificar validaciones Jakarta

---

## 🗺️ FASE 7: MAPPERS (30 minutos)

Crear en `Fullsound/src/main/java/Fullsound/Fullsound/mapper/`:

- [ ] `UsuarioMapper.java`
  - @Mapper(componentModel = "spring")
  - toResponse(Usuario usuario)
  - toEntity(UsuarioUpdateRequest request)
  
- [ ] `BeatMapper.java`
  - @Mapper(componentModel = "spring")
  - toResponse(Beat beat)
  - toEntity(BeatCreateRequest request)
  - toEntity(BeatUpdateRequest request)
  
- [ ] `PedidoMapper.java`
  - @Mapper(componentModel = "spring")
  - toResponse(Pedido pedido)
  - toItemResponse(PedidoItem item)

### Verificación
- [ ] Compilar mappers
- [ ] Verificar que MapStruct genera implementaciones
  ```bash
  mvn clean compile
  # Ver en target/generated-sources/annotations
  ```

---

## ⚙️ FASE 8: SERVICIOS (3 horas)

Crear interfaces en `Fullsound/src/main/java/Fullsound/Fullsound/service/`:

- [ ] `AuthService.java`
- [ ] `UsuarioService.java`
- [ ] `BeatService.java`
- [ ] `PedidoService.java`
- [ ] `PagoService.java`
- [ ] `EstadisticasService.java`

Crear implementaciones en `Fullsound/src/main/java/Fullsound/Fullsound/service/impl/`:

- [ ] `AuthServiceImpl.java`
  - register() - BCrypt password, crear rol default
  - login() - validar credenciales, generar JWT
  - forgotPassword()
  - resetPassword()
  
- [ ] `UsuarioServiceImpl.java`
  - findById(), findByUsername(), findByEmail()
  - update(), cambiarPassword()
  - findAll() (admin only)
  
- [ ] `BeatServiceImpl.java`
  - create() - generar slug, validar usuario
  - update() - verificar ownership
  - delete() - soft delete
  - findAll(), findById(), findByGenero()
  - incrementarReproducciones(), darLike()
  
- [ ] `PedidoServiceImpl.java`
  - crearPedido() - calcular totales, generar numero_pedido
  - findByUsuarioId()
  - findByNumeroPedido()
  
- [ ] `PagoServiceImpl.java`
  - createPaymentIntent() - Stripe API
  - confirmarPago() - actualizar estado
  - webhookStripe() - manejar eventos
  
- [ ] `EstadisticasServiceImpl.java`
  - getDashboard()
  - getTopBeats()
  - getVentas()

### Verificación
- [ ] Compilar servicios
- [ ] Verificar inyección de dependencias
- [ ] @Service, @Transactional correctos

---

## 🌐 FASE 9: CONTROLLERS (2 horas)

Crear en `Fullsound/src/main/java/Fullsound/Fullsound/controller/`:

- [ ] `AuthController.java` (@RestController, @RequestMapping("/api/auth"))
  - POST /register (público)
  - POST /login (público)
  - POST /forgot-password (público)
  - POST /reset-password (público)
  - GET /check-username/{username} (público)
  - GET /check-email/{email} (público)
  
- [ ] `UsuarioController.java` (@RequestMapping("/api/usuarios"))
  - GET /perfil (autenticado)
  - PUT /perfil (autenticado)
  - POST /cambiar-password (autenticado)
  - GET /{id} (admin)
  - GET / (admin)
  
- [ ] `BeatController.java` (@RequestMapping("/api/beats"))
  - GET / (público, paginado)
  - GET /{id} (público)
  - GET /slug/{slug} (público)
  - GET /genero/{genero} (público)
  - GET /destacados (público)
  - POST / (autenticado)
  - PUT /{id} (propietario)
  - DELETE /{id} (propietario)
  - POST /{id}/like (autenticado)
  - POST /{id}/reproducir (público)
  - GET /mis-beats (autenticado)
  
- [ ] `PedidoController.java` (@RequestMapping("/api/pedidos"))
  - GET /mis-pedidos (autenticado)
  - GET /{id} (autenticado)
  - GET /numero/{numero} (autenticado)
  - GET / (admin)
  
- [ ] `PagoController.java` (@RequestMapping("/api/pagos"))
  - POST /create-payment-intent (autenticado)
  - POST /confirm (autenticado)
  - POST /webhook (público, Stripe webhook)
  - GET /{id} (autenticado)
  
- [ ] `EstadisticasController.java` (@RequestMapping("/api/estadisticas"))
  - GET /dashboard (admin)
  - GET /beats/top (admin)
  - GET /ventas (admin)

### Verificación
- [ ] Compilar controllers
- [ ] Verificar endpoints en logs al iniciar
- [ ] Swagger UI accesible

---

## 🔐 FASE 10: SEGURIDAD (1 hora)

Crear en `Fullsound/src/main/java/Fullsound/Fullsound/security/`:

- [ ] `JwtTokenProvider.java`
  - generateToken(Authentication)
  - getUsernameFromToken(String token)
  - validateToken(String token)
  - Keys.hmacShaKeyFor para HS512
  
- [ ] `JwtAuthenticationFilter.java`
  - extends OncePerRequestFilter
  - doFilterInternal() - extraer token de Authorization header
  
- [ ] `UserDetailsServiceImpl.java`
  - implements UserDetailsService
  - loadUserByUsername() - cargar desde BD
  
- [ ] `UserDetailsImpl.java`
  - implements UserDetails
  - mapear roles correctamente
  
- [ ] `JwtAuthenticationEntryPoint.java`
  - implements AuthenticationEntryPoint
  - handle 401 Unauthorized

Crear en `Fullsound/src/main/java/Fullsound/Fullsound/config/`:

- [ ] `SecurityConfig.java`
  - @Configuration, @EnableWebSecurity
  - filterChain() - configurar endpoints públicos
  - passwordEncoder() - BCryptPasswordEncoder bean
  - authenticationManager() bean
  - CORS configuration
  
- [ ] `CorsConfig.java`
  - @Configuration
  - WebMvcConfigurer para CORS global

### Endpoints Públicos a Configurar:
- [ ] /api/auth/** (todos)
- [ ] /api/beats (GET sin auth)
- [ ] /api/beats/{id} (GET sin auth)
- [ ] /api/beats/slug/** (GET sin auth)
- [ ] /api/beats/genero/** (GET sin auth)
- [ ] /api/beats/destacados (GET sin auth)
- [ ] /api/beats/*/reproducir (POST sin auth)
- [ ] /api/pagos/webhook (POST sin auth - Stripe)
- [ ] /swagger-ui/** (sin auth)
- [ ] /api-docs/** (sin auth)
- [ ] /actuator/health (sin auth)

### Verificación
- [ ] Compilar security
- [ ] Iniciar aplicación
- [ ] Verificar que endpoints públicos funcionan sin token
- [ ] Verificar que endpoints protegidos requieren token
- [ ] Test login y obtener token

---

## ⚠️ FASE 11: EXCEPCIONES (30 minutos)

Crear en `Fullsound/src/main/java/Fullsound/Fullsound/exception/`:

- [ ] `ResourceNotFoundException.java` (extends RuntimeException)
- [ ] `BadRequestException.java`
- [ ] `UnauthorizedException.java`
- [ ] `ForbiddenException.java`
- [ ] `ConflictException.java`

- [ ] `GlobalExceptionHandler.java`
  - @RestControllerAdvice
  - @ExceptionHandler para cada excepción
  - @ExceptionHandler(MethodArgumentNotValidException.class)
  - Retornar ErrorResponse consistente

### Verificación
- [ ] Compilar excepciones
- [ ] Probar endpoint con validación inválida
- [ ] Verificar formato de error JSON

---

## 🧪 FASE 12: TESTING (2 horas)

### Tests de Repository
Crear en `Fullsound/src/test/java/.../repository/`:

- [ ] `BeatRepositoryTest.java` (@DataJpaTest)
  - testFindByGenero()
  - testFindByEstado()
  - testFindBySlug()
  
- [ ] `UsuarioRepositoryTest.java`
  - testFindByUsername()
  - testExistsByEmail()

### Tests de Service
Crear en `Fullsound/src/test/java/.../service/`:

- [ ] `BeatServiceTest.java` (@ExtendWith(MockitoExtension.class))
  - @Mock repositories
  - @InjectMocks service
  - testCreate(), testUpdate(), testDelete()
  
- [ ] `AuthServiceTest.java`
  - testRegister(), testLogin()

### Tests de Controller
Crear en `Fullsound/src/test/java/.../controller/`:

- [ ] `BeatControllerTest.java` (@WebMvcTest(BeatController.class))
  - @MockBean service
  - @Autowired MockMvc
  - testGetBeats(), testCreateBeat()

### Tests de Integración
Crear en `Fullsound/src/test/java/.../integration/`:

- [ ] `AuthIntegrationTest.java` (@SpringBootTest)
  - @AutoConfigureMockMvc
  - testRegisterAndLogin()

### Verificación
- [ ] Ejecutar todos los tests
  ```bash
  mvn test
  ```
- [ ] Verificar cobertura
  ```bash
  mvn test jacoco:report
  ```

---

## ✅ FASE 13: VERIFICACIÓN FINAL

### Compilación
- [ ] Limpiar y compilar proyecto completo
  ```bash
  mvn clean install
  ```
- [ ] No debe haber errores de compilación
- [ ] No debe haber warnings críticos

### Ejecución
- [ ] Iniciar aplicación
  ```bash
  mvn spring-boot:run
  ```
- [ ] Verificar logs de inicio
  - [ ] Spring Boot banner
  - [ ] Hibernate mapeo de entidades
  - [ ] Spring Security filterChain
  - [ ] Tomcat started on port 8080
  - [ ] No errores de autowiring

### Endpoints de Prueba

#### Health Check
- [ ] ```bash
  curl http://localhost:8080/actuator/health
  # Respuesta: {"status":"UP"}
  ```

#### Swagger UI
- [ ] Abrir http://localhost:8080/swagger-ui.html
- [ ] Verificar que muestra todos los controllers
- [ ] Verificar que muestra todos los endpoints

#### Register
- [ ] ```bash
  curl -X POST http://localhost:8080/api/auth/register \
    -H "Content-Type: application/json" \
    -d '{
      "username": "testuser",
      "email": "test@example.com",
      "password": "Test123!",
      "nombreCompleto": "Usuario Test"
    }'
  ```
- [ ] Debe retornar 201 Created con token JWT

#### Login
- [ ] ```bash
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{
      "username": "juan123",
      "password": "hash1"
    }'
  ```
- [ ] Debe retornar 200 OK con token JWT

#### Listar Beats (Público)
- [ ] ```bash
  curl http://localhost:8080/api/beats
  ```
- [ ] Debe retornar lista de beats con paginación

#### Crear Beat (Autenticado)
- [ ] ```bash
  TOKEN="tu_token_jwt_aqui"
  curl -X POST http://localhost:8080/api/beats \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "titulo": "Nuevo Beat Test",
      "genero": "Trap",
      "precio": 50000,
      "descripcion": "Beat de prueba"
    }'
  ```
- [ ] Debe retornar 201 Created con beat creado

#### Endpoint Protegido sin Token
- [ ] ```bash
  curl http://localhost:8080/api/usuarios/perfil
  ```
- [ ] Debe retornar 401 Unauthorized

#### Endpoint Admin
- [ ] ```bash
  # Login como admin
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"amoraga","password":"hash21"}'
  
  # Usar token de admin
  curl http://localhost:8080/api/estadisticas/dashboard \
    -H "Authorization: Bearer $ADMIN_TOKEN"
  ```
- [ ] Debe retornar dashboard stats

---

## 📊 ESTADÍSTICAS FINALES

Al completar todas las fases, debes tener:

- [x] **6 Entidades JPA** mapeadas a BD
- [x] **5 Enumeraciones** Java
- [x] **6 Repositories** con queries personalizados
- [x] **~12 DTOs Request** con validaciones
- [x] **~8 DTOs Response** 
- [x] **3 MapStruct Mappers**
- [x] **6 Services** con lógica de negocio
- [x] **6 REST Controllers**
- [x] **~48 Endpoints REST**
- [x] **5 Security Components** (JWT + Spring Security)
- [x] **5 Exception Handlers**
- [x] **Tests** (Repository, Service, Controller, Integration)

---

## 🎉 ¡IMPLEMENTACIÓN COMPLETADA!

Si todas las casillas están marcadas:

✅ **¡Felicitaciones! Has completado la implementación de FULLSOUND Backend.**

### Próximos Pasos:
1. [ ] Integrar con frontend React (puerto 5173)
2. [ ] Configurar CI/CD (GitHub Actions)
3. [ ] Deploy a producción (AWS, Heroku, Railway)
4. [ ] Implementar funcionalidades adicionales (Carrito, Reviews)
5. [ ] Optimizar queries con índices adicionales
6. [ ] Agregar caching (Redis)
7. [ ] Implementar rate limiting
8. [ ] Agregar logging avanzado (ELK Stack)

---

**Tiempo total estimado:** 8-12 horas  
**Última actualización:** 2025-11-13  
**Proyecto:** FULLSOUND-SPRINGBOOT  
**Desarrollador:** VECTORG99
