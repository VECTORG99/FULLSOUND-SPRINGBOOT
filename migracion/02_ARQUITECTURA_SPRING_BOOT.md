# 🏗️ ARQUITECTURA SPRING BOOT - FULLSOUND

## 🎯 Objetivo de Arquitectura

Crear una aplicación Spring Boot que:
1. Sirva la SPA de React como recursos estáticos
2. Proporcione APIs REST para el frontend
3. Gestione autenticación JWT
4. Administre la base de datos MySQL
5. Compile automáticamente el frontend durante el build

---

## 📁 Estructura de Directorios Propuesta

```
FULLSOUND-SPRINGBOOT/
│
├── Fullsound/                          # Proyecto Maven principal
│   ├── pom.xml                         # Configuración Maven + Frontend Plugin
│   │
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/fullsound/
│   │   │   │       ├── FullsoundApplication.java
│   │   │   │       │
│   │   │   │       ├── config/         # Configuraciones
│   │   │   │       │   ├── WebConfig.java
│   │   │   │       │   ├── SecurityConfig.java
│   │   │   │       │   ├── CorsConfig.java
│   │   │   │       │   └── JwtConfig.java
│   │   │   │       │
│   │   │   │       ├── controller/     # REST Controllers
│   │   │   │       │   ├── AuthController.java
│   │   │   │       │   ├── BeatController.java
│   │   │   │       │   ├── CarritoController.java
│   │   │   │       │   ├── UsuarioController.java
│   │   │   │       │   └── StaticResourceController.java
│   │   │   │       │
│   │   │   │       ├── model/          # Entidades JPA
│   │   │   │       │   ├── Usuario.java
│   │   │   │       │   ├── Beat.java
│   │   │   │       │   ├── Carrito.java
│   │   │   │       │   ├── CarritoItem.java
│   │   │   │       │   ├── Genero.java
│   │   │   │       │   └── Rol.java
│   │   │   │       │
│   │   │   │       ├── repository/     # JPA Repositories
│   │   │   │       │   ├── UsuarioRepository.java
│   │   │   │       │   ├── BeatRepository.java
│   │   │   │       │   ├── CarritoRepository.java
│   │   │   │       │   └── GeneroRepository.java
│   │   │   │       │
│   │   │   │       ├── service/        # Lógica de negocio
│   │   │   │       │   ├── AuthService.java
│   │   │   │       │   ├── BeatService.java
│   │   │   │       │   ├── CarritoService.java
│   │   │   │       │   ├── UsuarioService.java
│   │   │   │       │   └── JwtService.java
│   │   │   │       │
│   │   │   │       ├── dto/            # Data Transfer Objects
│   │   │   │       │   ├── LoginRequest.java
│   │   │   │       │   ├── LoginResponse.java
│   │   │   │       │   ├── RegisterRequest.java
│   │   │   │       │   ├── BeatDTO.java
│   │   │   │       │   ├── CarritoDTO.java
│   │   │   │       │   └── UsuarioDTO.java
│   │   │   │       │
│   │   │   │       ├── security/       # Seguridad JWT
│   │   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │   │       │   ├── JwtTokenProvider.java
│   │   │   │       │   └── CustomUserDetailsService.java
│   │   │   │       │
│   │   │   │       ├── exception/      # Manejo de errores
│   │   │   │       │   ├── GlobalExceptionHandler.java
│   │   │   │       │   ├── ResourceNotFoundException.java
│   │   │   │       │   ├── UnauthorizedException.java
│   │   │   │       │   └── ValidationException.java
│   │   │   │       │
│   │   │   │       └── util/           # Utilidades
│   │   │   │           ├── FileUploadUtil.java
│   │   │   │           ├── ValidationUtil.java
│   │   │   │           └── ResponseUtil.java
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── application.properties          # Config general
│   │   │       ├── application-dev.properties      # Config desarrollo
│   │   │       ├── application-prod.properties     # Config producción
│   │   │       │
│   │   │       ├── static/              # AQUÍ VA EL BUILD DE REACT
│   │   │       │   ├── index.html       # Generado por Vite
│   │   │       │   ├── assets/          # JS, CSS, imgs compilados
│   │   │       │   │   ├── index-[hash].js
│   │   │       │   │   ├── index-[hash].css
│   │   │       │   │   ├── img/
│   │   │       │   │   ├── audio/
│   │   │       │   │   └── fonts/
│   │   │       │   └── favicon.ico
│   │   │       │
│   │   │       ├── db/                  # Scripts SQL
│   │   │       │   ├── schema.sql
│   │   │       │   └── data.sql
│   │   │       │
│   │   │       └── templates/           # (Opcional) Thymeleaf
│   │   │
│   │   └── test/
│   │       └── java/
│   │           └── com/fullsound/
│   │               ├── controller/      # Tests de controllers
│   │               ├── service/         # Tests de servicios
│   │               └── integration/     # Tests de integración
│   │
│   ├── frontend/                        # CÓDIGO FUENTE REACT
│   │   ├── package.json
│   │   ├── vite.config.js
│   │   ├── index.html
│   │   ├── public/
│   │   └── src/
│   │       ├── App.jsx
│   │       ├── main.jsx
│   │       ├── components/
│   │       ├── services/
│   │       ├── assets/
│   │       └── utils/
│   │
│   └── target/                          # Generado por Maven
│       ├── classes/
│       ├── frontend/                    # Build de React
│       └── fullsound-0.0.1-SNAPSHOT.jar
│
└── migracion/                           # Documentación (ya creada)
```

---

## 🔄 Flujo de Build Integrado

### Proceso de Compilación

```mermaid
┌─────────────────────────────────────────────────────────────┐
│  1. MAVEN CLEAN                                             │
│  mvn clean                                                  │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│  2. FRONTEND-MAVEN-PLUGIN                                   │
│  - Instala Node.js (local)                                 │
│  - Ejecuta npm install                                     │
│  - Ejecuta npm run build                                   │
│  - Genera: frontend/dist/                                  │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│  3. MAVEN-RESOURCES-PLUGIN                                  │
│  - Copia frontend/dist/ → src/main/resources/static/      │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│  4. MAVEN COMPILE                                           │
│  - Compila código Java                                     │
│  - Procesa resources (incluyendo static/)                  │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│  5. MAVEN PACKAGE                                           │
│  - Crea JAR/WAR con:                                       │
│    • Clases Java                                           │
│    • Resources estáticos (React)                           │
│    • Dependencies                                          │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│  6. RESULTADO FINAL                                         │
│  fullsound-0.0.1-SNAPSHOT.jar                              │
│  • Backend Spring Boot                                     │
│  • Frontend React (pre-compilado)                          │
│  • Todo en un solo archivo ejecutable                     │
└─────────────────────────────────────────────────────────────┘
```

---

## ⚙️ Configuración de Recursos Estáticos

### WebConfig.java
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos estáticos de React
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0); // Sin cache en desarrollo
        
        // Archivos multimedia (audio, imágenes)
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCachePeriod(3600); // Cache de 1 hora
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirigir todas las rutas del frontend a index.html
        // Esto permite que React Router funcione correctamente
        registry.addViewController("/{spring:\\w+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/**/{spring:\\w+}")
                .setViewName("forward:/index.html");
    }
}
```

### StaticResourceController.java
```java
@Controller
public class StaticResourceController {
    
    /**
     * Fallback para rutas del frontend
     * Redirige todas las rutas no-API a index.html
     */
    @RequestMapping(value = "/{path:[^\\.]*}", method = RequestMethod.GET)
    public String redirect() {
        return "forward:/index.html";
    }
}
```

---

## 🔐 Configuración de Seguridad

### SecurityConfig.java
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Desactivar CSRF para APIs REST
            .cors() // Habilitar CORS
            .and()
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos (React) - públicos
                .requestMatchers("/", "/index.html", "/assets/**", 
                                "/static/**", "/favicon.ico").permitAll()
                
                // Rutas del frontend - públicas
                .requestMatchers("/login", "/registro", "/beats", 
                                "/carrito", "/producto/**", "/creditos",
                                "/terminos", "/main").permitAll()
                
                // Endpoints de autenticación - públicos
                .requestMatchers("/api/auth/**").permitAll()
                
                // Endpoints de lectura - públicos
                .requestMatchers(HttpMethod.GET, "/api/beats/**").permitAll()
                
                // Endpoints de administración - solo admin
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // CRUD de beats - solo admin
                .requestMatchers(HttpMethod.POST, "/api/beats").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/beats/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/beats/**").hasRole("ADMIN")
                
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sin sesiones
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), 
                           UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### CorsConfig.java
```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Orígenes permitidos
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",  // Vite dev server
            "http://localhost:8080",  // Spring Boot
            "https://fullsound.com"   // Producción
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With"
        ));
        
        // Exponer headers en respuesta
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization"
        ));
        
        // Permitir credenciales
        configuration.setAllowCredentials(true);
        
        // Tiempo de cache para preflight
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

## 🗄️ Configuración de Base de Datos

### application.properties
```properties
# ==========================================
# CONFIGURACIÓN GENERAL
# ==========================================
spring.application.name=Fullsound
server.port=8080

# ==========================================
# BASE DE DATOS MySQL
# ==========================================
spring.datasource.url=jdbc:mysql://localhost:3306/fullsound_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# ==========================================
# RECURSOS ESTÁTICOS
# ==========================================
spring.web.resources.static-locations=classpath:/static/
spring.mvc.static-path-pattern=/**

# Cache de recursos estáticos
spring.web.resources.cache.cachecontrol.max-age=3600
spring.web.resources.cache.cachecontrol.cache-public=true

# ==========================================
# MULTIPART - Archivos de audio/imagen
# ==========================================
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# ==========================================
# JWT
# ==========================================
jwt.secret=fullsound_secret_key_super_secure_2025
jwt.expiration=86400000
# 86400000ms = 24 horas

# ==========================================
# LOGGING
# ==========================================
logging.level.com.fullsound=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### application-dev.properties
```properties
# DESARROLLO
server.port=8080
spring.jpa.hibernate.ddl-auto=create-drop
logging.level.root=DEBUG

# CORS permisivo
cors.allowed-origins=http://localhost:5173,http://localhost:8080
```

### application-prod.properties
```properties
# PRODUCCIÓN
server.port=8080
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=WARN

# CORS restrictivo
cors.allowed-origins=https://fullsound.com

# Cache agresivo
spring.web.resources.cache.cachecontrol.max-age=31536000
```

---

## 🚀 Estrategia de Despliegue

### Desarrollo Local

**Opción 1: Frontend y Backend separados (recomendado en desarrollo)**
```bash
# Terminal 1: Spring Boot
cd Fullsound
mvn spring-boot:run

# Terminal 2: React con Vite
cd Fullsound/frontend
npm run dev

# Frontend: http://localhost:5173
# Backend: http://localhost:8080
# Frontend hace proxy a backend
```

**Opción 2: Todo integrado**
```bash
cd Fullsound
mvn clean package
java -jar target/fullsound-0.0.1-SNAPSHOT.jar

# Todo en: http://localhost:8080
```

### Producción

**Construcción:**
```bash
mvn clean package -Pprod
```

**Ejecución:**
```bash
java -jar target/fullsound-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

**Docker (opcional):**
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/fullsound-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

---

## 📊 Diagrama de Arquitectura Completa

```
┌────────────────────────────────────────────────────────────────┐
│                        NAVEGADOR                               │
│  http://localhost:8080                                         │
└────────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────────┐
│                     SPRING BOOT (Puerto 8080)                  │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │  RECURSOS ESTÁTICOS (React compilado)                    │ │
│  │  /index.html, /assets/*, /favicon.ico                    │ │
│  │  Servidos desde: classpath:/static/                      │ │
│  └──────────────────────────────────────────────────────────┘ │
│                          ↕                                     │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │  REST API                                                 │ │
│  │  /api/auth/* - Autenticación                            │ │
│  │  /api/beats/* - CRUD Beats                              │ │
│  │  /api/carrito/* - Gestión Carrito                       │ │
│  │  /api/usuarios/* - Gestión Usuarios                     │ │
│  └──────────────────────────────────────────────────────────┘ │
│                          ↕                                     │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │  SEGURIDAD                                                │ │
│  │  JWT Filter → Validación de token                       │ │
│  │  CORS → Configuración de orígenes                       │ │
│  └──────────────────────────────────────────────────────────┘ │
│                          ↕                                     │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │  CAPA DE SERVICIO                                         │ │
│  │  Lógica de negocio                                       │ │
│  └──────────────────────────────────────────────────────────┘ │
│                          ↕                                     │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │  REPOSITORIOS JPA                                         │ │
│  │  Acceso a datos                                          │ │
│  └──────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────┘
                          ↕
┌────────────────────────────────────────────────────────────────┐
│                     MySQL DATABASE                             │
│  fullsound_db                                                  │
│  • usuarios                                                    │
│  • beats                                                       │
│  • carritos                                                    │
│  • carrito_items                                               │
│  • generos                                                     │
└────────────────────────────────────────────────────────────────┘
```

---

## 🎯 Ventajas de Esta Arquitectura

✅ **Un solo servidor**: Frontend y backend en un solo proceso  
✅ **Un solo puerto**: Todo en 8080 (o configurable)  
✅ **Un solo JAR**: Deployment simplificado  
✅ **Sin problemas de CORS**: En producción no hay cross-origin  
✅ **Build automatizado**: Maven se encarga de todo  
✅ **Hot reload en desarrollo**: Ambos proyectos pueden ejecutarse por separado  

---

## ⚠️ Consideraciones Importantes

### Routing del Frontend
React Router maneja rutas en el cliente, pero Spring Boot necesita saber qué hacer con URLs como `/beats`, `/login`, etc.

**Solución**: Todas las rutas que no sean `/api/*` deben servir `index.html`, y React Router se encarga del resto.

### Assets Pesados
Archivos de audio pueden inflar el JAR.

**Soluciones:**
- Servir desde CDN externo
- Storage separado (AWS S3, Google Cloud Storage)
- Streaming desde servidor de archivos dedicado

### Cache
En producción, los assets de React tienen hash en el nombre (`index-abc123.js`), permitiendo cache infinito.

---

**Próximo Paso**: [03_DEPENDENCIAS_MAVEN.md](03_DEPENDENCIAS_MAVEN.md)
