# Plan de Migracion: Backend Spring Boot a Kotlin/Android

## Estado Actual

### Backend Spring Boot (FULLSOUND-SPRINGBOOT)
- API REST desarrollada en Spring Boot 3.5.7 con Java 17
- Base de datos PostgreSQL en Supabase
- Autenticacion JWT con Spring Security
- Endpoints REST para: Beats, Usuarios, Pedidos, Pagos (Stripe), Autenticacion
- URL actual: `jdbc:postgresql://aws-0-us-west-2.pooler.supabase.com:6543/postgres`
- Credenciales: Usuario `postgres.kivpcepyhfpqjfoycwel` con password en variable de entorno

### Aplicacion Android Kotlin (FullSound-KOTLIN)
- Conexion directa a Supabase mediante cliente Kotlin
- Base de datos local Room para cache
- Repositorios actuales: SupabaseBeatRepository, SupabaseUserRepository, SupabaseCarritoRepository
- Cliente Retrofit configurado pero sin usar (RetrofitClient.kt con URL placeholder)
- Arquitectura MVVM con ViewModels y LiveData

## Objetivo

Migrar la aplicacion Android de acceso directo a Supabase hacia consumo de API REST del backend Spring Boot, manteniendo datos reales de PostgreSQL.

## Analisis de Diferencias Criticas

### 1. Modelos de Datos

#### Beat
**Spring Boot (Beat.java):**
- Campo `id` como `Integer` con nombre de columna `id_beat`
- Campo `precio` como `Integer` (centavos de CLP)
- Campos: imagenUrl, audioUrl, audioDemoUrl

**Kotlin (Beat.kt):**
- Campo `id` como `Int` autoincremental
- Campo `precio` como `Double`
- Campos: imagenPath, mp3Path, audioDemoPath

**ACCION REQUERIDA:** Ajustar modelo Kotlin para coincidir con respuestas del backend.

#### Usuario
**Spring Boot (Usuario.java):**
- Campos: nombreUsuario, correo, contraseña, activo, rol (relacion ManyToOne), nombre, apellido
- ID como `Integer` con nombre `id_usuario`
- Tabla: `usuario`

**Kotlin (User.kt):**
- Campos: id (String UUID), email, username, password, name, rut, role (String), profileImage
- Tabla: `users`

**ACCION REQUERIDA:** Crear DTOs compatibles o adaptar modelo Kotlin.

#### Pedido/Compra
**Spring Boot:** Tiene modelo completo de Pedido con items, pagos, estados
**Kotlin:** No tiene modelo de Pedido implementado

**ACCION REQUERIDA:** Crear modelo de Pedido en Kotlin con campos compatibles.

### 2. Endpoints del Backend

#### Autenticacion (/api/auth)
- POST /api/auth/register - Registro de usuario
- POST /api/auth/login - Login con JWT
- GET /api/auth/health - Health check

#### Beats (/api/beats)
- GET /api/beats - Listar beats activos
- GET /api/beats/{id} - Obtener beat por ID
- GET /api/beats/slug/{slug} - Obtener por slug
- POST /api/beats - Crear beat (requiere admin)
- PUT /api/beats/{id} - Actualizar beat (requiere admin)
- DELETE /api/beats/{id} - Eliminar beat (requiere admin)
- GET /api/beats/search?q={query} - Buscar beats
- GET /api/beats/featured?limit={n} - Beats destacados

#### Usuarios (/api/usuarios)
- GET /api/usuarios/me - Obtener usuario autenticado
- GET /api/usuarios/{id} - Obtener usuario por ID
- GET /api/usuarios - Listar todos (requiere admin)
- POST /api/usuarios/cambiar-password - Cambiar contraseña

#### Pedidos (/api/pedidos)
- POST /api/pedidos - Crear pedido
- GET /api/pedidos/{id} - Obtener pedido por ID
- GET /api/pedidos/numero/{numeroPedido} - Obtener por numero
- GET /api/pedidos/mis-pedidos - Pedidos del usuario autenticado
- GET /api/pedidos - Listar todos (requiere admin)
- PATCH /api/pedidos/{id}/estado?estado={nuevo_estado} - Actualizar estado

#### Pagos (/api/pagos)
- POST /api/pagos/create-intent - Crear intencion de pago
- POST /api/pagos/{pagoId}/process - Procesar pago
- GET /api/pagos/{id} - Obtener pago por ID
- POST /api/pagos/confirm - Confirmar pago

### 3. Autenticacion JWT

#### Backend Spring Boot
- Genera token JWT con claims: subject (username), userId, roles
- Expiracion: 86400000 ms (24 horas)
- Algoritmo: HS512
- Header: `Authorization: Bearer {token}`
- Secret: Variable de entorno `JWT_SECRET`

#### Kotlin Actual
- No tiene manejo de JWT implementado
- Usa credenciales directas de Supabase (SUPABASE_ANON_KEY)

**ACCION REQUERIDA:** Implementar interceptor de Retrofit para agregar header JWT en todas las peticiones.

## Plan de Implementacion Paso a Paso

### FASE 1: Preparacion y Configuracion (Sin cambios de codigo)

#### 1.1 Verificar Backend Activo
- Confirmar que el backend Spring Boot esta desplegado y accesible
- Obtener URL base del backend (produccion o desarrollo)
- Verificar que los endpoints responden correctamente
- Documentar URL en `local.properties`

**Archivo a modificar:** `FullSound-KOTLIN.git/local.properties`
```properties
# Backend Spring Boot
BACKEND_BASE_URL=https://tu-backend-url.com/api
# o en desarrollo: http://10.0.2.2:8080/api para emulador Android
```

#### 1.2 Analizar Respuestas del Backend
- Documentar estructura JSON de cada endpoint
- Identificar nombres de campos exactos
- Verificar tipos de datos en respuestas
- Documentar codigos de error HTTP

**Herramientas:** Postman, Swagger UI (http://backend-url/swagger-ui.html)

#### 1.3 Inventariar Clases Kotlin Existentes
- Listar todos los Fragments que consumen datos
- Listar ViewModels y sus dependencias
- Listar Repositorios actuales (Room y Supabase)
- Documentar flujo actual de datos

**Resultado esperado:** Documento con mapeo de clases y dependencias.

### FASE 2: Crear Capa de Red (Retrofit)

#### 2.1 Crear Modelos de Request/Response (DTOs)

**Ubicacion:** `app/src/main/java/com/grupo8/fullsound/data/remote/dto/`

**Archivos a crear:**

```kotlin
// AuthDTOs.kt
data class LoginRequestDto(
    val correo: String,  // Backend usa "correo" no "email"
    val contraseña: String  // Backend usa "contraseña" con ñ
)

data class RegisterRequestDto(
    val nombreUsuario: String,
    val correo: String,
    val contraseña: String,
    val nombre: String?,
    val apellido: String?
)

data class AuthResponseDto(
    val token: String,
    val usuario: UsuarioDto
)

data class UsuarioDto(
    val id: Int,
    val nombreUsuario: String,
    val correo: String,
    val activo: Boolean,
    val rol: RolDto
)

data class RolDto(
    val id: Int,
    val nombre: String
)
```

```kotlin
// BeatDTOs.kt
data class BeatResponseDto(
    val id: Int,
    val titulo: String,
    val slug: String?,
    val artista: String?,
    val precio: Int,  // Integer en backend, no Double
    val bpm: Int?,
    val tonalidad: String?,
    val duracion: Int?,
    val genero: String?,
    val etiquetas: String?,
    val descripcion: String?,
    val imagenUrl: String?,
    val audioUrl: String?,
    val audioDemoUrl: String?,
    val reproducciones: Int,
    val estado: String,
    val createdAt: String?,
    val updatedAt: String?
)

data class BeatRequestDto(
    val titulo: String,
    val artista: String?,
    val precio: Int,
    val bpm: Int?,
    val tonalidad: String?,
    val duracion: Int?,
    val genero: String?,
    val descripcion: String?,
    val imagenUrl: String?,
    val audioUrl: String?
)
```

```kotlin
// PedidoDTOs.kt
data class PedidoRequestDto(
    val items: List<PedidoItemRequestDto>,
    val total: Int,
    val metodoPago: String
)

data class PedidoItemRequestDto(
    val idBeat: Int,
    val cantidad: Int,
    val precioUnitario: Int
)

data class PedidoResponseDto(
    val id: Int,
    val numeroPedido: String,
    val fechaCompra: String,
    val total: Int,
    val estado: String,
    val metodoPago: String?,
    val items: List<PedidoItemResponseDto>
)

data class PedidoItemResponseDto(
    val id: Int,
    val beat: BeatResponseDto,
    val cantidad: Int,
    val precioUnitario: Int,
    val subtotal: Int
)
```

```kotlin
// ErrorResponse.kt
data class ErrorResponseDto(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)
```

#### 2.2 Crear Interfaces Retrofit

**Ubicacion:** `app/src/main/java/com/grupo8/fullsound/data/remote/api/`

**Archivos a crear:**

```kotlin
// AuthApiService.kt
interface AuthApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): MessageResponseDto
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto
    
    @GET("auth/health")
    suspend fun healthCheck(): MessageResponseDto
}
```

```kotlin
// BeatApiService.kt
interface BeatApiService {
    @GET("beats")
    suspend fun getAllBeats(): List<BeatResponseDto>
    
    @GET("beats/{id}")
    suspend fun getBeatById(@Path("id") id: Int): BeatResponseDto
    
    @GET("beats/slug/{slug}")
    suspend fun getBeatBySlug(@Path("slug") slug: String): BeatResponseDto
    
    @GET("beats/search")
    suspend fun searchBeats(@Query("q") query: String): List<BeatResponseDto>
    
    @GET("beats/featured")
    suspend fun getFeaturedBeats(@Query("limit") limit: Int = 10): List<BeatResponseDto>
    
    @POST("beats")
    suspend fun createBeat(@Body request: BeatRequestDto): BeatResponseDto
    
    @PUT("beats/{id}")
    suspend fun updateBeat(@Path("id") id: Int, @Body request: BeatRequestDto): BeatResponseDto
    
    @DELETE("beats/{id}")
    suspend fun deleteBeat(@Path("id") id: Int): MessageResponseDto
}
```

```kotlin
// PedidoApiService.kt
interface PedidoApiService {
    @POST("pedidos")
    suspend fun createPedido(@Body request: PedidoRequestDto): PedidoResponseDto
    
    @GET("pedidos/{id}")
    suspend fun getPedidoById(@Path("id") id: Int): PedidoResponseDto
    
    @GET("pedidos/numero/{numeroPedido}")
    suspend fun getPedidoByNumero(@Path("numeroPedido") numero: String): PedidoResponseDto
    
    @GET("pedidos/mis-pedidos")
    suspend fun getMisPedidos(): List<PedidoResponseDto>
    
    @GET("pedidos")
    suspend fun getAllPedidos(): List<PedidoResponseDto>
    
    @PATCH("pedidos/{id}/estado")
    suspend fun updateEstadoPedido(
        @Path("id") id: Int,
        @Query("estado") estado: String
    ): PedidoResponseDto
}
```

```kotlin
// UsuarioApiService.kt
interface UsuarioApiService {
    @GET("usuarios/me")
    suspend fun getCurrentUser(): UsuarioDto
    
    @GET("usuarios/{id}")
    suspend fun getUserById(@Path("id") id: Int): UsuarioDto
    
    @GET("usuarios")
    suspend fun getAllUsers(): List<UsuarioDto>
    
    @POST("usuarios/cambiar-password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): MessageResponseDto
}
```

#### 2.3 Crear Interceptor JWT

**Ubicacion:** `app/src/main/java/com/grupo8/fullsound/data/remote/interceptor/`

**Archivo a crear:**

```kotlin
// AuthInterceptor.kt
class AuthInterceptor(private val context: Context) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Obtener token JWT almacenado
        val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val token = sharedPrefs.getString("jwt_token", null)
        
        // Si no hay token o la ruta es /auth/login o /auth/register, no agregar header
        val isAuthEndpoint = originalRequest.url.encodedPath.contains("/auth/login") ||
                            originalRequest.url.encodedPath.contains("/auth/register")
        
        val newRequest = if (token != null && !isAuthEndpoint) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}
```

#### 2.4 Configurar RetrofitClient

**Ubicacion:** `app/src/main/java/com/grupo8/fullsound/data/remote/`

**Archivo a modificar:** `RetrofitClient.kt`

```kotlin
object RetrofitClient {
    private const val BASE_URL = BuildConfig.BACKEND_BASE_URL
    
    private lateinit var context: Context
    
    fun init(applicationContext: Context) {
        context = applicationContext
    }
    
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
    
    val beatApi: BeatApiService by lazy {
        retrofit.create(BeatApiService::class.java)
    }
    
    val pedidoApi: PedidoApiService by lazy {
        retrofit.create(PedidoApiService::class.java)
    }
    
    val usuarioApi: UsuarioApiService by lazy {
        retrofit.create(UsuarioApiService::class.java)
    }
}
```

#### 2.5 Actualizar build.gradle.kts

**Ubicacion:** `app/build.gradle.kts`

**Agregar dependencias si faltan:**

```kotlin
dependencies {
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
}
```

**Agregar campo de BuildConfig:**

```kotlin
android {
    defaultConfig {
        // ... configuracion existente
        
        // Leer BACKEND_BASE_URL del local.properties
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { properties.load(it) }
        }
        
        val backendUrl = properties.getProperty("BACKEND_BASE_URL", "http://10.0.2.2:8080/api/")
        buildConfigField("String", "BACKEND_BASE_URL", "\"$backendUrl\"")
    }
}
```

### FASE 3: Adaptar Repositorios

#### 3.1 Crear Repositorio de Backend para Autenticacion

**Ubicacion:** `app/src/main/java/com/grupo8/fullsound/repository/`

**Archivo a crear:** `BackendAuthRepository.kt`

```kotlin
class BackendAuthRepository(
    private val authApi: AuthApiService,
    private val context: Context
) {
    private val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    
    suspend fun login(email: String, password: String): Resource<UsuarioDto> {
        return try {
            val request = LoginRequestDto(correo = email, contraseña = password)
            val response = authApi.login(request)
            
            // Guardar token JWT
            sharedPrefs.edit()
                .putString("jwt_token", response.token)
                .apply()
            
            Resource.Success(response.usuario)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Resource.Error("Error de autenticacion: ${e.code()}")
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }
    
    suspend fun register(
        username: String,
        email: String,
        password: String,
        name: String?,
        apellido: String?
    ): Resource<String> {
        return try {
            val request = RegisterRequestDto(
                nombreUsuario = username,
                correo = email,
                contraseña = password,
                nombre = name,
                apellido = apellido
            )
            val response = authApi.register(request)
            Resource.Success(response.message)
        } catch (e: HttpException) {
            Resource.Error("Error de registro: ${e.code()}")
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }
    
    fun logout() {
        sharedPrefs.edit()
            .remove("jwt_token")
            .apply()
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPrefs.getString("jwt_token", null) != null
    }
}
```

#### 3.2 Crear Repositorio de Backend para Beats

**Ubicacion:** `app/src/main/java/com/grupo8/fullsound/repository/`

**Archivo a crear:** `BackendBeatRepository.kt`

```kotlin
class BackendBeatRepository(
    private val beatApi: BeatApiService,
    private val beatDao: BeatDao
) {
    
    suspend fun getAllBeats(): Resource<List<Beat>> {
        return try {
            // Obtener desde backend
            val dtos = beatApi.getAllBeats()
            val beats = dtos.map { dto -> dtoToModel(dto) }
            
            // Guardar en cache local
            beats.forEach { beatDao.insertBeat(it) }
            
            Resource.Success(beats)
        } catch (e: HttpException) {
            // Fallback a cache local en caso de error
            val cachedBeats = beatDao.getAllBeats()
            if (cachedBeats.isNotEmpty()) {
                Resource.Success(cachedBeats)
            } else {
                Resource.Error("Error al obtener beats: ${e.code()}")
            }
        } catch (e: Exception) {
            val cachedBeats = beatDao.getAllBeats()
            if (cachedBeats.isNotEmpty()) {
                Resource.Success(cachedBeats)
            } else {
                Resource.Error("Error de red: ${e.message}")
            }
        }
    }
    
    suspend fun getBeatById(id: Int): Resource<Beat> {
        return try {
            val dto = beatApi.getBeatById(id)
            val beat = dtoToModel(dto)
            beatDao.insertBeat(beat)
            Resource.Success(beat)
        } catch (e: Exception) {
            val cachedBeat = beatDao.getBeatById(id)
            if (cachedBeat != null) {
                Resource.Success(cachedBeat)
            } else {
                Resource.Error("Beat no encontrado: ${e.message}")
            }
        }
    }
    
    suspend fun searchBeats(query: String): Resource<List<Beat>> {
        return try {
            val dtos = beatApi.searchBeats(query)
            val beats = dtos.map { dtoToModel(it) }
            Resource.Success(beats)
        } catch (e: Exception) {
            Resource.Error("Error en busqueda: ${e.message}")
        }
    }
    
    suspend fun createBeat(beat: Beat): Resource<Beat> {
        return try {
            val request = modelToRequestDto(beat)
            val dto = beatApi.createBeat(request)
            val newBeat = dtoToModel(dto)
            beatDao.insertBeat(newBeat)
            Resource.Success(newBeat)
        } catch (e: HttpException) {
            Resource.Error("Error al crear beat: ${e.code()}")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }
    
    suspend fun updateBeat(id: Int, beat: Beat): Resource<Beat> {
        return try {
            val request = modelToRequestDto(beat)
            val dto = beatApi.updateBeat(id, request)
            val updatedBeat = dtoToModel(dto)
            beatDao.insertBeat(updatedBeat)
            Resource.Success(updatedBeat)
        } catch (e: Exception) {
            Resource.Error("Error al actualizar: ${e.message}")
        }
    }
    
    suspend fun deleteBeat(id: Int): Resource<String> {
        return try {
            val response = beatApi.deleteBeat(id)
            beatDao.deleteBeatById(id)
            Resource.Success(response.message)
        } catch (e: Exception) {
            Resource.Error("Error al eliminar: ${e.message}")
        }
    }
    
    private fun dtoToModel(dto: BeatResponseDto): Beat {
        return Beat(
            id = dto.id,
            titulo = dto.titulo,
            slug = dto.slug,
            artista = dto.artista,
            precio = dto.precio.toDouble(),  // Convertir Int a Double
            bpm = dto.bpm,
            tonalidad = dto.tonalidad,
            duracion = dto.duracion,
            genero = dto.genero,
            etiquetas = dto.etiquetas,
            descripcion = dto.descripcion,
            imagenPath = dto.imagenUrl,
            mp3Path = dto.audioUrl,
            audioDemoPath = dto.audioDemoUrl,
            reproducciones = dto.reproducciones,
            estado = dto.estado,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }
    
    private fun modelToRequestDto(beat: Beat): BeatRequestDto {
        return BeatRequestDto(
            titulo = beat.titulo,
            artista = beat.artista,
            precio = beat.precio.toInt(),  // Convertir Double a Int
            bpm = beat.bpm,
            tonalidad = beat.tonalidad,
            duracion = beat.duracion,
            genero = beat.genero,
            descripcion = beat.descripcion,
            imagenUrl = beat.imagenPath,
            audioUrl = beat.mp3Path
        )
    }
}
```

#### 3.3 Crear Repositorio de Backend para Pedidos

**Ubicacion:** `app/src/main/java/com/grupo8/fullsound/repository/`

**Archivo a crear:** `BackendPedidoRepository.kt`

```kotlin
class BackendPedidoRepository(
    private val pedidoApi: PedidoApiService
) {
    
    suspend fun createPedido(
        items: List<CarritoItem>,
        total: Int,
        metodoPago: String
    ): Resource<PedidoResponseDto> {
        return try {
            val pedidoItems = items.map { item ->
                PedidoItemRequestDto(
                    idBeat = item.beatId,
                    cantidad = item.quantity,
                    precioUnitario = item.beat.precio.toInt()
                )
            }
            
            val request = PedidoRequestDto(
                items = pedidoItems,
                total = total,
                metodoPago = metodoPago
            )
            
            val response = pedidoApi.createPedido(request)
            Resource.Success(response)
        } catch (e: HttpException) {
            Resource.Error("Error al crear pedido: ${e.code()}")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }
    
    suspend fun getMisPedidos(): Resource<List<PedidoResponseDto>> {
        return try {
            val response = pedidoApi.getMisPedidos()
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error("Error al obtener pedidos: ${e.message}")
        }
    }
    
    suspend fun getPedidoById(id: Int): Resource<PedidoResponseDto> {
        return try {
            val response = pedidoApi.getPedidoById(id)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }
}
```

### FASE 4: Actualizar ViewModels

#### 4.1 Modificar LoginViewModel

**Ubicacion:** `app/src/main/java/com/grupo8/fullsound/viewmodel/`

**Archivo a modificar:** `LoginViewModel.kt`

**Cambios:**
- Reemplazar dependencia de `UserRepository` por `BackendAuthRepository`
- Actualizar metodo `login()` para usar el nuevo repositorio
- Guardar datos del usuario autenticado en UserSession

```kotlin
class LoginViewModel(
    private val backendAuthRepository: BackendAuthRepository
) : ViewModel() {
    
    private val _loginResult = MutableLiveData<Resource<UsuarioDto>>()
    val loginResult: LiveData<Resource<UsuarioDto>> = _loginResult
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Resource.Loading()
            val result = backendAuthRepository.login(email, password)
            _loginResult.value = result
            
            if (result is Resource.Success) {
                // Guardar sesion del usuario
                UserSession.setUser(result.data.nombreUsuario)
                UserSession.setRole(result.data.rol.nombre)
            }
        }
    }
}
```

#### 4.2 Modificar RegisterViewModel

**Archivo a modificar:** `RegisterViewModel.kt`

**Cambios similares a LoginViewModel:**

```kotlin
class RegisterViewModel(
    private val backendAuthRepository: BackendAuthRepository
) : ViewModel() {
    
    private val _registerResult = MutableLiveData<Resource<String>>()
    val registerResult: LiveData<Resource<String>> = _registerResult
    
    fun register(
        username: String,
        email: String,
        password: String,
        name: String,
        rut: String
    ) {
        viewModelScope.launch {
            _registerResult.value = Resource.Loading()
            val result = backendAuthRepository.register(
                username = username,
                email = email,
                password = password,
                name = name,
                apellido = null  // Backend acepta apellido opcional
            )
            _registerResult.value = result
        }
    }
}
```

#### 4.3 Modificar BeatsViewModel

**Archivo a modificar:** `BeatsViewModel.kt`

**Cambios:**
- Reemplazar `BeatRepository` (que usa Supabase) por `BackendBeatRepository`
- Mantener logica de ViewModels, solo cambiar origen de datos

```kotlin
class BeatsViewModel(
    private val backendBeatRepository: BackendBeatRepository
) : ViewModel() {
    
    private val _beatsResult = MutableLiveData<Resource<List<Beat>>>()
    val beatsResult: LiveData<Resource<List<Beat>>> = _beatsResult
    
    fun getAllBeats() {
        viewModelScope.launch {
            _beatsResult.value = Resource.Loading()
            val result = backendBeatRepository.getAllBeats()
            _beatsResult.value = result
        }
    }
    
    fun searchBeats(query: String) {
        viewModelScope.launch {
            _beatsResult.value = Resource.Loading()
            val result = backendBeatRepository.searchBeats(query)
            _beatsResult.value = result
        }
    }
    
    fun createBeat(beat: Beat) {
        viewModelScope.launch {
            val result = backendBeatRepository.createBeat(beat)
            // Notificar resultado
        }
    }
    
    fun updateBeat(id: Int, beat: Beat) {
        viewModelScope.launch {
            val result = backendBeatRepository.updateBeat(id, beat)
            // Notificar resultado
        }
    }
    
    fun deleteBeat(id: Int) {
        viewModelScope.launch {
            val result = backendBeatRepository.deleteBeat(id)
            // Notificar resultado
        }
    }
}
```

#### 4.4 Crear PedidoViewModel

**Ubicacion:** `app/src/main/java/com/grupo8/fullsound/viewmodel/`

**Archivo a crear:** `PedidoViewModel.kt`

```kotlin
class PedidoViewModel(
    private val backendPedidoRepository: BackendPedidoRepository
) : ViewModel() {
    
    private val _createPedidoResult = MutableLiveData<Resource<PedidoResponseDto>>()
    val createPedidoResult: LiveData<Resource<PedidoResponseDto>> = _createPedidoResult
    
    private val _misPedidosResult = MutableLiveData<Resource<List<PedidoResponseDto>>>()
    val misPedidosResult: LiveData<Resource<List<PedidoResponseDto>>> = _misPedidosResult
    
    fun createPedido(items: List<CarritoItem>, total: Int, metodoPago: String) {
        viewModelScope.launch {
            _createPedidoResult.value = Resource.Loading()
            val result = backendPedidoRepository.createPedido(items, total, metodoPago)
            _createPedidoResult.value = result
        }
    }
    
    fun getMisPedidos() {
        viewModelScope.launch {
            _misPedidosResult.value = Resource.Loading()
            val result = backendPedidoRepository.getMisPedidos()
            _misPedidosResult.value = result
        }
    }
}
```

### FASE 5: Actualizar Fragments y UI

#### 5.1 Modificar LoginFragment

**Archivo a modificar:** `LoginFragment.kt`

**Cambios:**
- Actualizar inicializacion de ViewModel para usar `BackendAuthRepository`
- Ajustar observadores para trabajar con `UsuarioDto` en lugar de `User`

```kotlin
private val viewModel: LoginViewModel by viewModels {
    val authRepo = BackendAuthRepository(
        authApi = RetrofitClient.authApi,
        context = requireContext()
    )
    LoginViewModelFactory(authRepo)
}
```

#### 5.2 Modificar RegisterFragment

**Cambios similares a LoginFragment**

#### 5.3 Modificar BeatsFragment y BeatsListaFragment

**Cambios:**
- Actualizar inicializacion de ViewModel para usar `BackendBeatRepository`
- Mantener logica de UI, solo cambiar fuente de datos

```kotlin
private val viewModel: BeatsViewModel by viewModels {
    val database = AppDatabase.getInstance(requireContext())
    val backendBeatRepo = BackendBeatRepository(
        beatApi = RetrofitClient.beatApi,
        beatDao = database.beatDao()
    )
    BeatsViewModelFactory(backendBeatRepo)
}
```

#### 5.4 Modificar CarritoFragment

**Cambios:**
- Agregar funcionalidad para crear pedido
- Integrar `PedidoViewModel`
- Al finalizar compra, llamar a `createPedido()`

```kotlin
private val pedidoViewModel: PedidoViewModel by viewModels {
    val pedidoRepo = BackendPedidoRepository(RetrofitClient.pedidoApi)
    PedidoViewModelFactory(pedidoRepo)
}

private fun finalizarCompra() {
    // Obtener items del carrito
    val items = carritoViewModel.carritoItems.value ?: emptyList()
    val total = calcularTotal(items)
    
    // Crear pedido
    pedidoViewModel.createPedido(items, total, "EFECTIVO")
    
    // Observar resultado
    pedidoViewModel.createPedidoResult.observe(viewLifecycleOwner) { result ->
        when (result) {
            is Resource.Success -> {
                // Mostrar confirmacion
                Toast.makeText(context, "Pedido creado: ${result.data.numeroPedido}", Toast.LENGTH_LONG).show()
                // Limpiar carrito
                carritoViewModel.clearCarrito()
            }
            is Resource.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
            }
            is Resource.Loading -> {
                // Mostrar loading
            }
        }
    }
}
```

### FASE 6: Inicializacion y Configuracion Global

#### 6.1 Inicializar RetrofitClient en Application

**Ubicacion:** `app/src/main/java/com/grupo8/fullsound/`

**Archivo a crear (si no existe):** `FullSoundApplication.kt`

```kotlin
class FullSoundApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar RetrofitClient con contexto
        RetrofitClient.init(applicationContext)
    }
}
```

**Registrar en AndroidManifest.xml:**

```xml
<application
    android:name=".FullSoundApplication"
    ...>
```

#### 6.2 Actualizar network_security_config.xml

**Ubicacion:** `app/src/main/res/xml/network_security_config.xml`

**Agregar dominio del backend:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
        <!-- Agregar dominio de produccion si usa HTTP -->
        <domain includeSubdomains="true">tu-backend-url.com</domain>
    </domain-config>
</network-security-config>
```

### FASE 7: Testing y Validacion

#### 7.1 Pruebas de Autenticacion
- Registrar nuevo usuario y verificar token JWT guardado
- Login con usuario existente
- Verificar que token se envia en peticiones posteriores
- Logout y verificar que token se elimina

#### 7.2 Pruebas de Beats
- Listar beats desde backend
- Buscar beats
- Crear beat (como admin)
- Actualizar beat
- Eliminar beat
- Verificar cache local funciona en modo offline

#### 7.3 Pruebas de Pedidos
- Agregar items al carrito
- Crear pedido desde carrito
- Listar mis pedidos
- Verificar numero de pedido generado

#### 7.4 Pruebas de Errores
- Simular error 401 (Unauthorized) y verificar logout automatico
- Simular error 403 (Forbidden) para operaciones de admin
- Simular error 404 (Not Found)
- Simular timeout de red
- Verificar que cache local se usa como fallback

### FASE 8: Migracion Gradual (Opcional)

#### 8.1 Estrategia de Coexistencia
- Mantener repositorios de Supabase como fallback temporal
- Crear flag de configuracion para alternar entre backend y Supabase
- Migrar feature por feature

**Archivo a crear:** `app/src/main/java/com/grupo8/fullsound/config/DataSourceConfig.kt`

```kotlin
object DataSourceConfig {
    enum class DataSource {
        BACKEND,
        SUPABASE,
        HYBRID
    }
    
    var currentDataSource = DataSource.BACKEND
    
    fun useBackend() = currentDataSource == DataSource.BACKEND
    fun useSupabase() = currentDataSource == DataSource.SUPABASE
    fun useHybrid() = currentDataSource == DataSource.HYBRID
}
```

#### 8.2 Adaptador de Repositorio Hibrido

```kotlin
class HybridBeatRepository(
    private val backendRepo: BackendBeatRepository,
    private val supabaseRepo: SupabaseBeatRepository
) {
    suspend fun getAllBeats(): Resource<List<Beat>> {
        return when (DataSourceConfig.currentDataSource) {
            DataSource.BACKEND -> backendRepo.getAllBeats()
            DataSource.SUPABASE -> supabaseRepo.getAllBeats()
            DataSource.HYBRID -> {
                val backendResult = backendRepo.getAllBeats()
                if (backendResult is Resource.Error) {
                    supabaseRepo.getAllBeats()
                } else {
                    backendResult
                }
            }
        }
    }
}
```

## Consideraciones de Seguridad

### 1. Almacenamiento de Token JWT
- Usar SharedPreferences en modo privado
- Considerar EncryptedSharedPreferences para mayor seguridad
- No almacenar password en local

### 2. Validacion de Certificados SSL
- En produccion, usar HTTPS exclusivamente
- Implementar Certificate Pinning para mayor seguridad

### 3. Ofuscacion de Codigo
- Habilitar ProGuard en modo release
- Ofuscar clases de red y autenticacion

## Consideraciones de Rendimiento

### 1. Cache y Offline-First
- Mantener Room Database como cache
- Implementar estrategia de invalidacion de cache
- Sincronizar cambios locales cuando hay conexion

### 2. Paginacion
- Implementar paginacion en listas grandes
- Usar LiveData con PagedList si es necesario

### 3. Imagenes
- Continuar usando Coil para carga de imagenes
- Implementar cache de imagenes en disco

## Checklist de Verificacion Final

### Codigo
- [ ] Todos los DTOs creados y mapeados correctamente
- [ ] Interfaces Retrofit definidas con anotaciones correctas
- [ ] AuthInterceptor implementado y agregando header JWT
- [ ] RetrofitClient configurado con base URL correcta
- [ ] Repositorios de backend implementados
- [ ] ViewModels actualizados con nuevos repositorios
- [ ] Fragments actualizados con nuevos ViewModels
- [ ] ViewModelFactories actualizadas

### Configuracion
- [ ] BACKEND_BASE_URL agregado en local.properties
- [ ] BuildConfig generando campo BACKEND_BASE_URL
- [ ] network_security_config.xml actualizado
- [ ] FullSoundApplication registrado en manifest
- [ ] Dependencias de Retrofit y OkHttp agregadas

### Testing
- [ ] Login funcional con token JWT
- [ ] Registro funcional
- [ ] Listar beats desde backend
- [ ] Cache local funcionando como fallback
- [ ] Crear pedido funcional
- [ ] Listar pedidos del usuario
- [ ] Manejo de errores HTTP probado
- [ ] Timeout de red manejado

### Backend
- [ ] Backend desplegado y accesible
- [ ] CORS configurado para permitir peticiones desde Android
- [ ] Endpoints respondiendo correctamente
- [ ] JWT generandose correctamente
- [ ] Base de datos PostgreSQL operativa

## Posibles Problemas y Soluciones

### Problema 1: Error 401 en todas las peticiones
**Causa:** Token JWT no se esta enviando o es invalido
**Solucion:**
- Verificar que AuthInterceptor esta agregado al OkHttpClient
- Verificar que token se guarda correctamente en SharedPreferences
- Verificar formato del header: `Authorization: Bearer {token}`

### Problema 2: Error de Certificado SSL
**Causa:** Backend usa certificado autofirmado o invalido
**Solucion:**
- En desarrollo, agregar dominio a network_security_config.xml
- En produccion, usar certificado valido

### Problema 3: Timeout de red
**Causa:** Backend lento o timeouts muy cortos
**Solucion:**
- Aumentar timeouts en OkHttpClient (connectTimeout, readTimeout)
- Optimizar queries en backend

### Problema 4: Diferencias en nombres de campos
**Causa:** Backend usa nombres diferentes (ej: "correo" vs "email")
**Solucion:**
- Usar anotacion @SerializedName de Gson en DTOs
- Ejemplo: `@SerializedName("correo") val email: String`

### Problema 5: Tipos de datos incompatibles
**Causa:** Backend usa Integer, Kotlin usa Double
**Solucion:**
- Hacer conversion explicita en mappers: `dto.precio.toDouble()`
- Mantener coherencia en toda la app

### Problema 6: Error al crear pedido sin autenticacion
**Causa:** Backend requiere JWT para crear pedidos
**Solucion:**
- Verificar que usuario esta logueado antes de permitir compra
- Redirigir a login si no hay token

## Documentacion de Referencia

### Endpoints del Backend
Consultar Swagger UI: `http://backend-url/swagger-ui.html`

### Modelos de Datos
- Ver archivos `.java` en `BackEnd/Fullsound/src/main/java/Fullsound/Fullsound/model/`
- Ver DTOs en `BackEnd/Fullsound/src/main/java/Fullsound/Fullsound/dto/`

### Codigo de Errores HTTP
- 200: OK
- 201: Created
- 400: Bad Request (datos invalidos)
- 401: Unauthorized (sin autenticacion)
- 403: Forbidden (sin permisos)
- 404: Not Found
- 500: Internal Server Error

## Conclusion

Este plan proporciona una ruta detallada para migrar la aplicacion Android de acceso directo a Supabase hacia consumo del backend Spring Boot mediante API REST.

La migracion se realiza en fases incrementales, manteniendo la funcionalidad existente y permitiendo testing continuo. Se preserva Room Database como cache local para soporte offline.

El backend Spring Boot proporciona una capa de logica de negocio centralizada, autenticacion JWT, y facilita futuras integraciones (pagos, notificaciones, etc.).

Todos los cambios son no destructivos y permiten rollback a Supabase si es necesario mediante la estrategia hibrida propuesta en Fase 8.
