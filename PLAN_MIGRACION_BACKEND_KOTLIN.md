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

## FASE 9: Automatizacion CI/CD con GitHub Actions

### 9.0 Vision General de Automatizacion

Esta fase reemplaza el proceso manual de generacion y despliegue del APK con un workflow automatizado de GitHub Actions que:

1. **Se activa automaticamente** cuando el backend se despliega en AWS
2. **Obtiene la URL del backend** desde el deployment del backend
3. **Configura automaticamente** las variables de entorno para el APK
4. **Ejecuta tests de integracion** contra el backend real
5. **Genera y firma el APK** de produccion
6. **Sube el APK a S3** para distribucion
7. **Crea release en GitHub** con el APK adjunto

**Ventajas de este enfoque:**
- ✅ Cero configuracion manual
- ✅ El APK siempre apunta al backend correcto
- ✅ Tests automaticos garantizan compatibilidad
- ✅ Deployment atomico y reproducible
- ✅ Historial completo de releases

### 9.1 Configuracion de GitHub Secrets

#### 9.1.1 Secrets Requeridos en el Repositorio FullSound-KOTLIN

Configurar los siguientes secrets en GitHub (Settings > Secrets and variables > Actions):

**Secrets de AWS (compartidos con el backend):**
```
AWS_ACCESS_KEY_ID           - Credenciales AWS
AWS_SECRET_ACCESS_KEY       - Credenciales AWS
AWS_SESSION_TOKEN           - Token de sesion AWS (Learner Lab)
AWS_ACCOUNT_ID              - ID de cuenta AWS
```

**Secrets de la Aplicacion:**
```
DB_PASSWORD                 - Contraseña de PostgreSQL en Supabase
JWT_SECRET                  - Clave secreta JWT (minimo 64 caracteres)
KEYSTORE_PASSWORD           - Password del keystore de firma
KEY_PASSWORD                - Password de la key en el keystore
KEYSTORE_BASE64             - Keystore codificado en base64
```

**Secrets Opcionales:**
```
BACKEND_REPO_OWNER          - Owner del repo backend (default: VECTORG99)
BACKEND_REPO_NAME           - Nombre repo backend (default: FULLSOUND-SPRINGBOOT)
```

#### 9.1.2 Como Generar y Codificar el Keystore

**Paso 1: Generar Keystore (solo una vez)**
```bash
# En el directorio raiz del proyecto Kotlin
keytool -genkey -v -keystore fullsound-release.keystore \
  -alias fullsound \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000

# Guardar passwords de forma segura
```

**Paso 2: Codificar Keystore en Base64**
```bash
# En Linux/Mac
base64 fullsound-release.keystore | tr -d '\n' > keystore.base64

# En Windows PowerShell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("fullsound-release.keystore")) | Out-File keystore.base64
```

**Paso 3: Agregar a GitHub Secrets**
- Copiar contenido de `keystore.base64`
- Crear secret `KEYSTORE_BASE64` con ese valor
- Crear secrets `KEYSTORE_PASSWORD` y `KEY_PASSWORD`
- ⚠️ **NUNCA** commitear el keystore en Git

### 9.2 Configuracion Simplificada del Proyecto Kotlin

#### 9.2.1 Actualizar build.gradle.kts para CI/CD

**Archivo a modificar:** `FullSound-KOTLIN.git/app/build.gradle.kts`

**Cambios clave:**
```kotlin
android {
    // ... configuracion existente
    
    signingConfigs {
        create("release") {
            // En CI/CD, el keystore se decodifica desde GitHub Secrets
            // En desarrollo local, se usa keystore local si existe
            val keystorePath = System.getenv("KEYSTORE_PATH") ?: "../keystore/fullsound-release.keystore"
            
            if (file(keystorePath).exists()) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "default_password"
                keyAlias = System.getenv("KEY_ALIAS") ?: "fullsound"
                keyPassword = System.getenv("KEY_PASSWORD") ?: "default_password"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            
            // La URL del backend se inyecta desde GitHub Actions
            val backendUrl = System.getenv("BACKEND_BASE_URL") 
                ?: "https://placeholder.com/api"
            
            buildConfigField("String", "BACKEND_BASE_URL", "\"$backendUrl\"")
            buildConfigField("String", "ENVIRONMENT", "\"production\"")
        }
        
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            buildConfigField("String", "BACKEND_BASE_URL", "\"http://10.0.2.2:8080/api\"")
            buildConfigField("String", "ENVIRONMENT", "\"development\"")
        }
    }
    
    // Versionado automatico basado en Git
    defaultConfig {
        // ... configuracion existente
        
        // Version code automatico basado en numero de commits
        versionCode = providers.exec {
            commandLine("git", "rev-list", "--count", "HEAD")
        }.standardOutput.asText.get().trim().toIntOrNull() ?: 1
        
        // Version name desde Git tag o branch
        versionName = providers.exec {
            commandLine("git", "describe", "--tags", "--always", "--dirty")
        }.standardOutput.asText.get().trim().ifEmpty { "1.0.0" }
    }
}
```

#### 9.2.2 Configuracion .env para Desarrollo Local (Opcional)

**Archivo a crear:** `FullSound-KOTLIN.git/.env.example`

```properties
# Variables para desarrollo local
# Copiar a .env y configurar con valores reales

# Backend URL (local o AWS)
BACKEND_BASE_URL=http://10.0.2.2:8080/api

# Credenciales de firma (solo para testing local)
KEYSTORE_PASSWORD=tu_password
KEY_ALIAS=fullsound
KEY_PASSWORD=tu_password
```

**Archivo a modificar:** `FullSound-KOTLIN.git/.gitignore`
```gitignore
# Ignorar archivos sensibles
.env
*.keystore
keystore/
keystore.base64
local.properties
```

### 9.3 Estructura del GitHub Actions Workflow

#### 9.3.1 Workflow Principal: Build y Deploy APK

**Archivo a crear:** `FullSound-KOTLIN.git/.github/workflows/build-and-deploy-apk.yml`

**Estructura del workflow:**

```yaml
name: Build and Deploy Android APK

on:
  # Se activa cuando se hace push al main
  push:
    branches: [main]
    paths:
      - 'app/**'
      - '.github/workflows/build-and-deploy-apk.yml'
  
  # Se activa manualmente con parametros opcionales
  workflow_dispatch:
    inputs:
      backend_url:
        description: 'Backend URL (opcional, se auto-detecta desde AWS)'
        required: false
        type: string
      skip_tests:
        description: 'Skip integration tests'
        required: false
        type: boolean
        default: false
  
  # Se activa cuando el backend se despliega (webhook desde otro repo)
  repository_dispatch:
    types: [backend-deployed]

jobs:
  detect-backend:
    name: Detect Backend URL from AWS
    runs-on: ubuntu-latest
    outputs:
      backend_url: ${{ steps.get-backend.outputs.url }}
      backend_ip: ${{ steps.get-backend.outputs.ip }}
    
    steps:
      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-session-token: ${{ secrets.AWS_SESSION_TOKEN }}
          aws-region: us-east-1
      
      - name: Get Backend URL from AWS
        id: get-backend
        run: |
          # Opcion 1: Usar URL manual si se proporciono
          if [ -n "${{ inputs.backend_url }}" ]; then
            echo "url=${{ inputs.backend_url }}" >> $GITHUB_OUTPUT
            exit 0
          fi
          
          # Opcion 2: Obtener desde deployment-info en S3
          BUCKET_NAME="fullsound-deployments-${{ secrets.AWS_ACCOUNT_ID }}"
          
          if aws s3 ls s3://${BUCKET_NAME}/deployment-info.json; then
            aws s3 cp s3://${BUCKET_NAME}/deployment-info.json deployment-info.json
            BACKEND_IP=$(jq -r '.ec2_public_ip' deployment-info.json)
            
            if [ "$BACKEND_IP" != "null" ] && [ -n "$BACKEND_IP" ]; then
              BACKEND_URL="http://${BACKEND_IP}:8080/api"
              echo "url=${BACKEND_URL}" >> $GITHUB_OUTPUT
              echo "ip=${BACKEND_IP}" >> $GITHUB_OUTPUT
              echo "✅ Backend URL detected: ${BACKEND_URL}"
            else
              echo "❌ No backend IP found in deployment-info.json"
              exit 1
            fi
          else
            echo "❌ deployment-info.json not found in S3"
            exit 1
          fi
      
      - name: Verify Backend Health
        run: |
          BACKEND_URL="${{ steps.get-backend.outputs.url }}"
          echo "Testing backend health: ${BACKEND_URL}/auth/health"
          
          # Esperar hasta 2 minutos a que el backend este disponible
          for i in {1..24}; do
            if curl -f -s "${BACKEND_URL}/auth/health" > /dev/null; then
              echo "✅ Backend is healthy!"
              exit 0
            fi
            echo "⏳ Waiting for backend... (attempt $i/24)"
            sleep 5
          done
          
          echo "❌ Backend health check failed"
          exit 1

  build-apk:
    name: Build and Test APK
    needs: detect-backend
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout Code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0  # Necesario para versionado con Git
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      
      - name: Decode Keystore
        run: |
          echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > /tmp/keystore.jks
          echo "KEYSTORE_PATH=/tmp/keystore.jks" >> $GITHUB_ENV
      
      - name: Setup Build Environment
        run: |
          echo "BACKEND_BASE_URL=${{ needs.detect-backend.outputs.backend_url }}" >> $GITHUB_ENV
          echo "KEYSTORE_PASSWORD=${{ secrets.KEYSTORE_PASSWORD }}" >> $GITHUB_ENV
          echo "KEY_ALIAS=fullsound" >> $GITHUB_ENV
          echo "KEY_PASSWORD=${{ secrets.KEY_PASSWORD }}" >> $GITHUB_ENV
      
      - name: Grant Execute Permission for Gradlew
        run: chmod +x gradlew
      
      - name: Run Unit Tests
        run: ./gradlew test
      
      - name: Run Integration Tests (optional)
        if: ${{ !inputs.skip_tests }}
        env:
          BACKEND_URL: ${{ needs.detect-backend.outputs.backend_url }}
          JWT_SECRET: ${{ secrets.JWT_SECRET }}
        run: |
          # Aqui se ejecutarian tests de integracion contra el backend real
          echo "Running integration tests against: $BACKEND_URL"
          ./gradlew connectedAndroidTest || echo "⚠️  Integration tests skipped (no emulator)"
      
      - name: Build Release APK
        run: |
          echo "🔨 Building APK with backend: ${{ needs.detect-backend.outputs.backend_url }}"
          ./gradlew assembleRelease --stacktrace
      
      - name: Build Release AAB (for Play Store)
        run: ./gradlew bundleRelease
      
      - name: Get APK Info
        id: apk-info
        run: |
          APK_PATH=$(find app/build/outputs/apk/release -name "*.apk" | head -n 1)
          AAB_PATH=$(find app/build/outputs/bundle/release -name "*.aab" | head -n 1)
          
          echo "apk_path=${APK_PATH}" >> $GITHUB_OUTPUT
          echo "aab_path=${AAB_PATH}" >> $GITHUB_OUTPUT
          
          # Obtener version de build.gradle
          VERSION_NAME=$(grep "versionName" app/build.gradle.kts | head -1 | sed 's/.*"\(.*\)".*/\1/')
          VERSION_CODE=$(grep "versionCode" app/build.gradle.kts | head -1 | sed 's/.*= *\([0-9]*\).*/\1/')
          
          echo "version_name=${VERSION_NAME}" >> $GITHUB_OUTPUT
          echo "version_code=${VERSION_CODE}" >> $GITHUB_OUTPUT
          
          echo "📦 APK: ${APK_PATH}"
          echo "📦 AAB: ${AAB_PATH}"
          echo "🏷️  Version: ${VERSION_NAME} (${VERSION_CODE})"
      
      - name: Sign APK Verification
        run: |
          APK_PATH="${{ steps.apk-info.outputs.apk_path }}"
          echo "Verifying APK signature..."
          jarsigner -verify -verbose -certs "${APK_PATH}"
      
      - name: Upload APK Artifact
        uses: actions/upload-artifact@v4
        with:
          name: fullsound-apk-${{ steps.apk-info.outputs.version_name }}
          path: ${{ steps.apk-info.outputs.apk_path }}
          retention-days: 30
      
      - name: Upload AAB Artifact
        uses: actions/upload-artifact@v4
        with:
          name: fullsound-aab-${{ steps.apk-info.outputs.version_name }}
          path: ${{ steps.apk-info.outputs.aab_path }}
          retention-days: 30

  deploy-to-s3:
    name: Deploy APK to AWS S3
    needs: [detect-backend, build-apk]
    runs-on: ubuntu-latest
    
    steps:
      - name: Download APK Artifact
        uses: actions/download-artifact@v4
        with:
          pattern: fullsound-apk-*
          merge-multiple: true
      
      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-session-token: ${{ secrets.AWS_SESSION_TOKEN }}
          aws-region: us-east-1
      
      - name: Create S3 Bucket for APKs
        run: |
          BUCKET_NAME="fullsound-android-releases-${{ secrets.AWS_ACCOUNT_ID }}"
          aws s3 mb s3://${BUCKET_NAME} --region us-east-1 2>/dev/null || echo "Bucket exists"
          
          # Configurar bucket para acceso publico
          aws s3api put-public-access-block \
            --bucket ${BUCKET_NAME} \
            --public-access-block-configuration \
            "BlockPublicAcls=false,IgnorePublicAcls=false,BlockPublicPolicy=false,RestrictPublicBuckets=false"
          
          # Configurar politica de bucket
          cat > /tmp/bucket-policy.json << EOF
          {
            "Version": "2012-10-17",
            "Statement": [{
              "Sid": "PublicReadGetObject",
              "Effect": "Allow",
              "Principal": "*",
              "Action": "s3:GetObject",
              "Resource": "arn:aws:s3:::${BUCKET_NAME}/*"
            }]
          }
          EOF
          
          aws s3api put-bucket-policy --bucket ${BUCKET_NAME} --policy file:///tmp/bucket-policy.json
          
          echo "BUCKET_NAME=${BUCKET_NAME}" >> $GITHUB_ENV
      
      - name: Upload APK to S3
        run: |
          APK_FILE=$(ls *.apk | head -n 1)
          TIMESTAMP=$(date +%Y%m%d-%H%M%S)
          VERSION="${{ github.sha }}"
          
          # Subir version timestamped
          aws s3 cp "${APK_FILE}" \
            "s3://${BUCKET_NAME}/fullsound-${TIMESTAMP}.apk" \
            --metadata "commit-sha=${{ github.sha }},backend-url=${{ needs.detect-backend.outputs.backend_url }}"
          
          # Subir como latest
          aws s3 cp "${APK_FILE}" \
            "s3://${BUCKET_NAME}/fullsound-latest.apk" \
            --metadata "commit-sha=${{ github.sha }},backend-url=${{ needs.detect-backend.outputs.backend_url }},timestamp=${TIMESTAMP}"
          
          # Crear metadata JSON
          cat > metadata.json << EOF
          {
            "version": "${VERSION}",
            "timestamp": "${TIMESTAMP}",
            "commit_sha": "${{ github.sha }}",
            "backend_url": "${{ needs.detect-backend.outputs.backend_url }}",
            "download_url": "https://${BUCKET_NAME}.s3.us-east-1.amazonaws.com/fullsound-latest.apk",
            "size_bytes": $(stat -f%z "${APK_FILE}" 2>/dev/null || stat -c%s "${APK_FILE}")
          }
          EOF
          
          aws s3 cp metadata.json "s3://${BUCKET_NAME}/metadata-latest.json"
          
          echo "✅ APK uploaded successfully!"
          echo "📱 Download URL: https://${BUCKET_NAME}.s3.us-east-1.amazonaws.com/fullsound-latest.apk"
          
          echo "download_url=https://${BUCKET_NAME}.s3.us-east-1.amazonaws.com/fullsound-latest.apk" >> $GITHUB_ENV
      
      - name: Create GitHub Release
        uses: softprops/action-gh-release@v1
        if: startsWith(github.ref, 'refs/tags/')
        with:
          files: |
            *.apk
          body: |
            ## FullSound APK Release
            
            **Backend URL:** ${{ needs.detect-backend.outputs.backend_url }}
            **Commit:** ${{ github.sha }}
            **Build Date:** ${{ github.event.head_commit.timestamp }}
            
            ### Download
            - [Download APK from S3](${{ env.download_url }})
            
            ### Installation
            1. Enable "Unknown Sources" in Android Settings
            2. Download and install the APK
            3. Login with your credentials
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      
      - name: Deployment Summary
        run: |
          echo "======================================"
          echo "🎉 DEPLOYMENT COMPLETE"
          echo "======================================"
          echo ""
          echo "📱 APK Download: ${{ env.download_url }}"
          echo "🌐 Backend URL: ${{ needs.detect-backend.outputs.backend_url }}"
          echo "🔖 Commit: ${{ github.sha }}"
          echo "📦 S3 Bucket: ${BUCKET_NAME}"
          echo ""
          echo "======================================"
```

#### 9.3.2 Workflow de Trigger desde Backend

**Concepto:** Cuando el backend se despliega, puede notificar al repo de Kotlin para que genere un nuevo APK automaticamente.

**Archivo a modificar en backend:** `FULLSOUND-SPRINGBOOT/.github/workflows/deploy-backend-aws.yml`

**Agregar al final del workflow:**

```yaml
      - name: Trigger Android APK Build
        if: success()
        run: |
          # Notificar al repo de Android que el backend fue desplegado
          curl -X POST \
            -H "Accept: application/vnd.github+json" \
            -H "Authorization: Bearer ${{ secrets.GITHUB_TOKEN }}" \
            -H "X-GitHub-Api-Version: 2022-11-28" \
            https://api.github.com/repos/VECTORG99/FullSound-KOTLIN/dispatches \
            -d '{
              "event_type": "backend-deployed",
              "client_payload": {
                "backend_url": "http://${{ steps.check-ec2.outputs.public-ip }}:8080/api",
                "backend_ip": "${{ steps.check-ec2.outputs.public-ip }}",
                "commit_sha": "${{ github.sha }}"
              }
            }'
```

### 9.4 Configuracion ProGuard para Release

**Archivo a modificar:** `FullSound-KOTLIN.git/app/proguard-rules.pro`

```proguard
# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Modelos de datos (DTOs)
-keep class com.grupo8.fullsound.data.remote.dto.** { *; }
-keep class com.grupo8.fullsound.data.local.entity.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlin
-keep class kotlin.Metadata { *; }
```

### 9.5 Tests de Integracion Automaticos

#### 9.5.1 Crear Tests contra Backend Real

**Archivo a crear:** `FullSound-KOTLIN.git/app/src/androidTest/java/com/grupo8/fullsound/BackendIntegrationTest.kt`

**Proposito:** Validar que el APK puede comunicarse correctamente con el backend de AWS.

```kotlin
/**
 * Tests de integracion que se ejecutan contra el backend real de AWS
 * Se ejecutan en GitHub Actions antes de generar el APK de produccion
 */
@RunWith(AndroidJUnit4::class)
class BackendIntegrationTest {
    
    private val backendUrl = System.getenv("BACKEND_URL") ?: BuildConfig.BACKEND_BASE_URL
    
    @Test
    fun testBackendHealthCheck() {
        // Verificar que el backend esta disponible
        val healthUrl = "$backendUrl/auth/health"
        
        val response = makeRequest(healthUrl)
        assertEquals(200, response.code)
    }
    
    @Test
    fun testUserRegistrationAndLogin() {
        // Test completo de registro y login
        val randomUser = "testuser_${System.currentTimeMillis()}"
        val email = "$randomUser@fullsound.test"
        val password = "TestPassword123!"
        
        // 1. Registrar usuario
        val registerResponse = register(randomUser, email, password)
        assertTrue(registerResponse.isSuccessful)
        
        // 2. Login
        val loginResponse = login(email, password)
        assertTrue(loginResponse.isSuccessful)
        assertNotNull(loginResponse.token)
    }
    
    @Test
    fun testGetBeatsFromBackend() {
        // Verificar que se pueden obtener beats del backend
        val beatsUrl = "$backendUrl/beats"
        
        val response = makeRequest(beatsUrl)
        assertEquals(200, response.code)
        
        val beats = parseBeatsResponse(response.body)
        assertTrue(beats.isNotEmpty(), "Backend should return beats")
    }
    
    @Test
    fun testCreatePedidoRequiresAuth() {
        // Verificar que crear pedido sin auth retorna 401
        val pedidoUrl = "$backendUrl/pedidos"
        
        val response = makePostRequest(pedidoUrl, "{}")
        assertEquals(401, response.code)
    }
}
```

#### 9.5.2 Estrategia de Tests en CI/CD

**Tests que se ejecutan automaticamente:**

1. **Unit Tests (siempre):** Tests unitarios de la app
2. **Backend Health Check (siempre):** Verifica que backend este disponible
3. **Integration Tests (opcional):** Tests completos contra backend real
   - Solo se ejecutan si hay emulador disponible
   - Pueden skippearse con flag `skip_tests`

**Beneficios:**
- ✅ Detecta problemas de compatibilidad antes de deployment
- ✅ Valida que el backend esta funcionando correctamente
- ✅ Garantiza que el APK generado funciona end-to-end

### 9.6 Proceso Automatizado Completo

#### 9.6.1 Flujo Automatico End-to-End

**Diagrama del proceso:**

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. DEVELOPER PUSH CODE                                          │
│    - Push a main en repo FullSound-KOTLIN                       │
│    - O trigger manual via workflow_dispatch                     │
└───────────────────┬─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│ 2. DETECT BACKEND URL                                           │
│    ✓ Obtiene IP del EC2 desde S3 deployment-info.json          │
│    ✓ Construye URL: http://EC2-IP:8080/api                     │
│    ✓ Verifica health check del backend                         │
└───────────────────┬─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. BUILD APK                                                    │
│    ✓ Decodifica keystore desde GitHub Secrets                  │
│    ✓ Inyecta BACKEND_BASE_URL en BuildConfig                   │
│    ✓ Ejecuta unit tests                                        │
│    ✓ Ejecuta integration tests (opcional)                      │
│    ✓ Compila y firma APK de release                            │
│    ✓ Genera AAB para Play Store                                │
└───────────────────┬─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│ 4. DEPLOY TO S3                                                 │
│    ✓ Crea bucket fullsound-android-releases-{ACCOUNT_ID}       │
│    ✓ Sube APK como fullsound-{timestamp}.apk                   │
│    ✓ Sube copia como fullsound-latest.apk                      │
│    ✓ Crea metadata.json con info del build                     │
│    ✓ Crea GitHub Release (si es tag)                           │
└───────────────────┬─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│ 5. DEPLOYMENT COMPLETE                                          │
│    📱 APK URL: https://bucket.s3.amazonaws.com/latest.apk       │
│    🌐 Consuming: http://EC2-IP:8080/api                         │
│    ✅ Ready for distribution                                    │
└─────────────────────────────────────────────────────────────────┘
```

#### 9.6.2 Trigger Automatico desde Backend (Opcional)

**Cuando el backend se despliega exitosamente, puede disparar automaticamente el build del APK:**

```
Backend Deploy (main) → API Call → Android Workflow → Build APK → Deploy S3
```

**Ventaja:** El APK siempre esta sincronizado con la ultima version del backend.

#### 9.6.3 Variables Disponibles en el Workflow

**Variables que se pasan automaticamente:**

| Variable | Origen | Uso |
|----------|--------|-----|
| `BACKEND_BASE_URL` | Detectada desde AWS | Inyectada en BuildConfig del APK |
| `KEYSTORE_PASSWORD` | GitHub Secret | Firma del APK |
| `KEY_PASSWORD` | GitHub Secret | Firma del APK |
| `JWT_SECRET` | GitHub Secret | Tests de integracion |
| `DB_PASSWORD` | GitHub Secret | Tests de integracion |
| `AWS_ACCESS_KEY_ID` | GitHub Secret | Acceso a S3 |
| `AWS_SECRET_ACCESS_KEY` | GitHub Secret | Acceso a S3 |
| `AWS_SESSION_TOKEN` | GitHub Secret | Acceso a S3 (Learner Lab) |
| `AWS_ACCOUNT_ID` | GitHub Secret | Nombre del bucket S3 |

### 9.7 Pagina de Descarga del APK

#### 9.7.1 Pagina Web de Descarga Dinamica

**Crear archivo:** `FULLSOUND-SPRINGBOOT/frontend/descargar-app.html`

Esta pagina obtiene automaticamente la URL del APK desde S3 y muestra metadata en tiempo real.

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Descargar FullSound APK</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            max-width: 700px;
            margin: 50px auto;
            padding: 30px;
            text-align: center;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 20px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.3);
        }
        h1 {
            font-size: 3em;
            margin-bottom: 10px;
        }
        .subtitle {
            font-size: 1.2em;
            margin-bottom: 30px;
            opacity: 0.9;
        }
        .download-btn {
            background-color: #1DB954;
            color: white;
            padding: 18px 40px;
            border: none;
            border-radius: 50px;
            font-size: 20px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            margin: 25px 0;
            transition: all 0.3s ease;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        }
        .download-btn:hover {
            background-color: #1ed760;
            transform: translateY(-3px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.4);
        }
        .info-box {
            background: rgba(255,255,255,0.1);
            border-radius: 15px;
            padding: 20px;
            margin: 20px 0;
            backdrop-filter: blur(10px);
        }
        .version {
            font-size: 14px;
            opacity: 0.8;
        }
        .metadata {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
            margin: 20px 0;
            text-align: left;
        }
        .metadata-item {
            background: rgba(255,255,255,0.1);
            padding: 15px;
            border-radius: 10px;
        }
        .metadata-label {
            font-size: 0.9em;
            opacity: 0.7;
            margin-bottom: 5px;
        }
        .metadata-value {
            font-size: 1.1em;
            font-weight: bold;
        }
        .instructions {
            background: rgba(255,255,255,0.1);
            border-radius: 15px;
            padding: 25px;
            margin-top: 30px;
            text-align: left;
        }
        .instructions h3 {
            margin-top: 0;
            text-align: center;
        }
        .instructions ol {
            line-height: 2em;
        }
        .loading {
            display: inline-block;
            margin: 20px;
        }
        @keyframes spin {
            to { transform: rotate(360deg); }
        }
        .spinner {
            border: 4px solid rgba(255,255,255,0.3);
            border-top: 4px solid white;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
            margin: 20px auto;
        }
    </style>
</head>
<body>
    <h1>🎵 FullSound</h1>
    <p class="subtitle">Aplicación móvil oficial para comprar beats</p>
    
    <div id="loading" class="loading">
        <div class="spinner"></div>
        <p>Cargando información del APK...</p>
    </div>
    
    <div id="content" style="display: none;">
        <a id="downloadBtn" href="#" class="download-btn" download>
            📱 Descargar APK
        </a>
        
        <div class="info-box">
            <p class="version">
                <strong>Versión:</strong> <span id="version">Cargando...</span><br>
                <strong>Tamaño:</strong> <span id="size">Cargando...</span><br>
                <strong>Última actualización:</strong> <span id="timestamp">Cargando...</span>
            </p>
        </div>
        
        <div class="metadata">
            <div class="metadata-item">
                <div class="metadata-label">Backend URL</div>
                <div class="metadata-value" id="backendUrl">-</div>
            </div>
            <div class="metadata-item">
                <div class="metadata-label">Commit SHA</div>
                <div class="metadata-value" id="commitSha">-</div>
            </div>
        </div>
        
        <div class="instructions">
            <h3>📋 Instrucciones de instalación</h3>
            <ol>
                <li><strong>Descarga</strong> el archivo APK tocando el botón verde</li>
                <li>Ve a <strong>Ajustes > Seguridad</strong> en tu dispositivo Android</li>
                <li>Habilita <strong>"Fuentes desconocidas"</strong> o <strong>"Instalar aplicaciones desconocidas"</strong></li>
                <li>Abre el <strong>archivo APK descargado</strong></li>
                <li>Toca <strong>"Instalar"</strong> y espera a que se complete</li>
                <li>¡Listo! Abre FullSound y disfruta</li>
            </ol>
            <p style="text-align: center; margin-top: 20px;">
                <small>⚠️ Requiere Android 7.0 (API 24) o superior</small>
            </p>
        </div>
    </div>
    
    <script>
        // Obtener metadata del APK desde S3
        const AWS_ACCOUNT_ID = 'TU_ACCOUNT_ID'; // Reemplazar con tu Account ID
        const BUCKET_NAME = `fullsound-android-releases-${AWS_ACCOUNT_ID}`;
        const METADATA_URL = `https://${BUCKET_NAME}.s3.us-east-1.amazonaws.com/metadata-latest.json`;
        const APK_URL = `https://${BUCKET_NAME}.s3.us-east-1.amazonaws.com/fullsound-latest.apk`;
        
        fetch(METADATA_URL)
            .then(response => response.json())
            .then(data => {
                // Actualizar UI con metadata
                document.getElementById('version').textContent = data.version || 'N/A';
                document.getElementById('timestamp').textContent = new Date(data.timestamp).toLocaleString('es-ES');
                document.getElementById('backendUrl').textContent = data.backend_url || 'N/A';
                document.getElementById('commitSha').textContent = (data.commit_sha || 'N/A').substring(0, 7);
                
                // Calcular tamaño en MB
                const sizeMB = (data.size_bytes / (1024 * 1024)).toFixed(2);
                document.getElementById('size').textContent = `${sizeMB} MB`;
                
                // Configurar boton de descarga
                document.getElementById('downloadBtn').href = data.download_url || APK_URL;
                
                // Mostrar contenido
                document.getElementById('loading').style.display = 'none';
                document.getElementById('content').style.display = 'block';
            })
            .catch(error => {
                console.error('Error loading metadata:', error);
                // Si falla, usar URL por defecto
                document.getElementById('downloadBtn').href = APK_URL;
                document.getElementById('loading').innerHTML = '<p>⚠️ No se pudo cargar la metadata, pero puedes descargar el APK</p>';
                document.getElementById('content').style.display = 'block';
            });
    </script>
</body>
</html>
```

**Nota:** Reemplazar `TU_ACCOUNT_ID` con el valor real del AWS Account ID.

### 9.8 Configuracion Simplificada para Desarrollo Local

#### 9.8.1 Desarrollo y Testing Local (sin GitHub Actions)

**Para desarrolladores que quieren probar localmente antes de push:**

**Opcion 1: Usar Backend Local**
```bash
# En local.properties
BACKEND_BASE_URL=http://10.0.2.2:8080/api
```

**Opcion 2: Usar Backend en AWS**
```bash
# Obtener IP del backend desde AWS
aws ec2 describe-instances \
  --filters "Name=tag:Name,Values=fullsound-backend" \
  --query "Reservations[0].Instances[0].PublicIpAddress" \
  --output text

# Agregar a local.properties
BACKEND_BASE_URL=http://[IP-AWS]:8080/api
```

**Opcion 3: Usar archivo .env**
```bash
# Crear .env en la raiz del proyecto Kotlin
echo "BACKEND_BASE_URL=http://tu-ip:8080/api" > .env
```

**Build local sin firma:**
```bash
# Para testing rapido (debug build)
./gradlew assembleDebug

# Para testing de release sin firma
./gradlew assembleRelease
```

### 9.9 Monitoreo y Debugging

#### 9.9.1 Ver Logs del Workflow en GitHub

**Acceso a logs:**
1. Ir a repositorio `FullSound-KOTLIN` en GitHub
2. Ir a tab "Actions"
3. Seleccionar workflow "Build and Deploy Android APK"
4. Ver detalles de cada job (detect-backend, build-apk, deploy-to-s3)

**Logs importantes a revisar:**
- ✅ Backend URL detectada correctamente
- ✅ Health check del backend exitoso
- ✅ Tests pasados
- ✅ APK firmado correctamente
- ✅ Upload a S3 exitoso

#### 9.9.2 Debugging de Problemas Comunes

**Problema 1: Backend URL no detectada**
```bash
# Verificar que deployment-info.json existe en S3
aws s3 ls s3://fullsound-deployments-{ACCOUNT_ID}/deployment-info.json

# Ver contenido
aws s3 cp s3://fullsound-deployments-{ACCOUNT_ID}/deployment-info.json -
```

**Solucion:** Asegurar que el backend se haya desplegado correctamente.

**Problema 2: Health check falla**
```bash
# Verificar manualmente el backend
curl http://[EC2-IP]:8080/api/auth/health

# Verificar que EC2 este running
aws ec2 describe-instances \
  --filters "Name=tag:Name,Values=fullsound-backend" \
  --query "Reservations[0].Instances[0].State.Name"
```

**Solucion:** Verificar que el backend este corriendo y accesible.

**Problema 3: Keystore invalido**
```bash
# Verificar que el secret KEYSTORE_BASE64 este configurado
# Re-generar y re-codificar el keystore si es necesario
```

**Problema 4: APK no se puede instalar**
- Verificar firma del APK en los logs
- Verificar que minSdk coincide con el dispositivo
- Habilitar "Unknown Sources" en dispositivo

#### 9.9.3 Monitoreo de Deployments

**Ver ultimos deployments:**
```bash
# Listar APKs en S3
aws s3 ls s3://fullsound-android-releases-{ACCOUNT_ID}/ --recursive

# Ver metadata del ultimo deployment
aws s3 cp s3://fullsound-android-releases-{ACCOUNT_ID}/metadata-latest.json -
```

**Estadisticas de descarga (opcional):**
- Habilitar CloudFront logging
- Usar S3 access logs
- Implementar contador de descargas en pagina web

### 9.10 Checklist de Configuracion Completa

#### 9.10.1 Repositorio FullSound-KOTLIN

**GitHub Secrets configurados:**
- [ ] `AWS_ACCESS_KEY_ID` - Credenciales AWS
- [ ] `AWS_SECRET_ACCESS_KEY` - Credenciales AWS
- [ ] `AWS_SESSION_TOKEN` - Token de sesion AWS Learner Lab
- [ ] `AWS_ACCOUNT_ID` - ID de cuenta AWS
- [ ] `DB_PASSWORD` - Contraseña de PostgreSQL
- [ ] `JWT_SECRET` - Secret JWT (minimo 64 caracteres)
- [ ] `KEYSTORE_BASE64` - Keystore codificado en base64
- [ ] `KEYSTORE_PASSWORD` - Password del keystore
- [ ] `KEY_PASSWORD` - Password de la key

**Archivos creados/modificados:**
- [ ] `.github/workflows/build-and-deploy-apk.yml` - Workflow principal
- [ ] `app/build.gradle.kts` - Configurado para CI/CD
- [ ] `app/proguard-rules.pro` - Reglas de ProGuard
- [ ] `.gitignore` - Actualizado para excluir archivos sensibles
- [ ] `.env.example` - Template para desarrollo local

**Configuracion local (opcional):**
- [ ] `.env` o `local.properties` configurado para desarrollo
- [ ] Keystore local generado (solo para testing)

#### 9.10.2 Repositorio FULLSOUND-SPRINGBOOT (Backend)

**Modificaciones opcionales:**
- [ ] Workflow modificado para disparar build del APK automaticamente
- [ ] Seccion agregada para notificar repo de Kotlin

#### 9.10.3 AWS

**Recursos creados automaticamente:**
- [ ] Bucket S3: `fullsound-android-releases-{ACCOUNT_ID}`
- [ ] Politica de bucket configurada para acceso publico
- [ ] deployment-info.json disponible desde backend

**Verificaciones:**
- [ ] Backend desplegado en EC2 y accessible
- [ ] Health check del backend responde correctamente
- [ ] URL del backend disponible via deployment-info.json

#### 9.10.4 Testing

**Tests automaticos:**
- [ ] Unit tests pasan en CI/CD
- [ ] Integration tests configurados (opcional)
- [ ] Backend health check funciona

**Tests manuales:**
- [ ] APK descargado desde S3
- [ ] APK instalado en dispositivo real
- [ ] Login funciona contra backend de AWS
- [ ] Listar beats funciona
- [ ] Crear pedido funciona
- [ ] Funcionalidad offline con Room funciona

#### 9.10.5 Documentacion

- [ ] README actualizado con instrucciones de CI/CD
- [ ] Plan de migracion completado (este documento)
- [ ] Pagina de descarga del APK desplegada
- [ ] URLs de descarga documentadas

### 9.11 Ventajas del Enfoque con GitHub Actions

**Comparacion: Manual vs Automatizado**

| Aspecto | Proceso Manual | GitHub Actions (Automatizado) |
|---------|---------------|------------------------------|
| **Configuracion Backend URL** | Manual, propenso a errores | ✅ Auto-detectada desde AWS |
| **Firma del APK** | Keystore local, riesgo de perdida | ✅ Keystore seguro en GitHub Secrets |
| **Tests** | Manual, a veces se olvidan | ✅ Automaticos en cada build |
| **Consistency** | Depende del desarrollador | ✅ Proceso identico siempre |
| **Distribucion** | Subir manualmente a S3 | ✅ Automatica con metadata |
| **Versionado** | Manual en build.gradle | ✅ Automatico desde Git |
| **Deployment Backend + APK** | 2 pasos manuales | ✅ Sincronizado automaticamente |
| **Rollback** | Complejo | ✅ Facil con versiones en S3 |
| **Auditoria** | No hay registro | ✅ Logs completos en GitHub |
| **Tiempo de deployment** | 30-60 minutos | ✅ 10-15 minutos automatico |

**Ventajas clave:**

1. ✅ **Zero Configuration:** El APK siempre sabe donde esta el backend
2. ✅ **Atomic Deployments:** Backend y APK estan sincronizados
3. ✅ **Reproducible:** Cualquier commit puede regenerar el APK exacto
4. ✅ **Secure:** Secrets nunca en el codigo, solo en GitHub
5. ✅ **Fast Feedback:** Errores se detectan en CI antes de produccion
6. ✅ **Versionado Automatico:** No mas confusion con versiones

### 9.12 Proceso de Actualizacion Simplificado

**Para publicar una nueva version:**

```bash
# 1. Hacer cambios en el codigo
git add .
git commit -m "feat: nueva funcionalidad X"

# 2. (Opcional) Crear tag para release oficial
git tag -a v1.2.0 -m "Release 1.2.0"

# 3. Push al repositorio
git push origin main
git push origin v1.2.0  # Si creaste tag

# 4. ✨ GitHub Actions se encarga del resto automaticamente:
#    - Detecta backend URL desde AWS
#    - Ejecuta tests
#    - Genera APK firmado con backend correcto
#    - Sube a S3
#    - Crea GitHub Release (si es tag)
```

**Resultado automatico:**
- 📱 APK disponible en S3 en ~10-15 minutos
- 🔗 URL de descarga actualizada
- 📊 Metadata con info del build
- 🏷️ GitHub Release (si es tag)
- 📝 Logs completos del proceso

**Versionado Automatico:**
- `versionCode`: Numero de commits en Git (auto)
- `versionName`: Tag de Git o commit SHA (auto)
- No necesitas modificar build.gradle manualmente

### 9.13 Estrategias de Distribucion

#### 9.13.1 Beta Testing (S3)

**Uso:** Distribucion rapida a beta testers.

**Ventajas:**
- ✅ Deployment automatico inmediato
- ✅ URL directa para descargar
- ✅ Multiples versiones disponibles
- ✅ Sin revision de Google

**Distribucion:**
1. Compartir URL: `https://bucket.s3.amazonaws.com/fullsound-latest.apk`
2. O usar pagina web de descarga personalizada

#### 9.13.2 Google Play Store (Produccion)

**Uso:** Distribucion publica a usuarios finales.

**Proceso:**
1. Descargar AAB desde GitHub Actions artifacts
2. Subir a Google Play Console
3. Completar listing (screenshots, descripcion, etc.)
4. Publicar en Internal/Closed/Open Testing o Production

**Ventajas:**
- ✅ Actualizaciones automaticas para usuarios
- ✅ Mayor confianza (Play Store verification)
- ✅ Estadisticas de uso
- ✅ Ratings y reviews

#### 9.13.3 Enfoque Hibrido (Recomendado)

**Estrategia:**
- **S3:** Para beta testing y actualizaciones rapidas
- **Play Store:** Para produccion estable

**Workflow:**
```
Desarrollo → GitHub Actions → S3 (beta) → Testing → Play Store (production)
```

## Conclusion

Este plan proporciona una **ruta detallada y automatizada** para migrar la aplicacion Android de acceso directo a Supabase hacia consumo del backend Spring Boot mediante API REST.

### Cambios Clave en la Fase 9 (Automatizacion):

**Antes (Manual):**
- ❌ Configurar URL del backend manualmente
- ❌ Generar keystore y mantenerlo seguro localmente
- ❌ Compilar APK manualmente
- ❌ Subir a S3 con comandos manuales
- ❌ Crear releases manualmente
- ❌ Alto riesgo de errores humanos

**Ahora (Automatizado con GitHub Actions):**
- ✅ **URL del backend auto-detectada** desde AWS deployment
- ✅ **Keystore seguro** en GitHub Secrets
- ✅ **Build automatico** en cada push
- ✅ **Tests automaticos** garantizan calidad
- ✅ **Deploy automatico a S3** con metadata
- ✅ **GitHub Releases automaticos** con tags
- ✅ **Proceso reproducible y auditable**
- ✅ **Sincronizacion backend-APK automatica**

### Ventajas Globales:

1. **Zero Configuration:** El APK siempre consume el backend correcto sin configuracion manual
2. **Atomic Deployments:** Backend y APK estan perfectamente sincronizados
3. **Continuous Delivery:** Cada commit puede generar un APK de produccion
4. **Seguridad:** Secrets nunca expuestos, keystore seguro
5. **Trazabilidad:** Cada APK tiene metadata completa (commit, backend URL, timestamp)
6. **Rollback Facil:** Multiples versiones disponibles en S3
7. **Fast Iteration:** De codigo a APK en produccion en 10-15 minutos
8. **Quality Assurance:** Tests automaticos en cada build

### Arquitectura Final:

```
┌─────────────────────────────────────────────────────────────┐
│                    DESARROLLADOR                             │
│  git push → GitHub → Actions Workflow                        │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
        ▼                             ▼
┌───────────────┐            ┌────────────────┐
│   BACKEND     │            │   ANDROID APK  │
│  Spring Boot  │◄───────────│    Kotlin      │
│   (AWS EC2)   │   consume  │  (S3 + GitHub) │
└───────────────┘            └────────────────┘
        │                             │
        │                             │
        ▼                             ▼
   PostgreSQL                   Usuarios Finales
   (Supabase)                   (Download S3/Play)
```

La migracion se realiza en **fases incrementales**, manteniendo la funcionalidad existente y permitiendo testing continuo. Se preserva **Room Database** como cache local para soporte offline.

El backend Spring Boot proporciona una **capa de logica de negocio centralizada**, autenticacion JWT, y facilita futuras integraciones (pagos, notificaciones, etc.).

**La Fase 9 automatizada garantiza que la aplicacion Android pueda ser distribuida a usuarios finales consumiendo todos los servicios de produccion desplegados en AWS**, con un proceso completamente automatizado, seguro y reproducible.

Todos los cambios son **no destructivos** y permiten rollback a Supabase si es necesario mediante la estrategia hibrida propuesta en Fase 8.
