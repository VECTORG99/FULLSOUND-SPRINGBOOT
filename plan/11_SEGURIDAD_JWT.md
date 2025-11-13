# 🔐 PASO 57-61: Seguridad y JWT

## 🎯 Objetivo
Implementar la configuración de Spring Security con autenticación JWT, filtros personalizados y manejo de roles.

---

## 📁 Ubicación Base
```
Fullsound/src/main/java/Fullsound/Fullsound/security/
```

---

## ✅ PASO 57: JwtTokenProvider

**Archivo:** `JwtTokenProvider.java`

```java
package Fullsound.Fullsound.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Proveedor de tokens JWT.
 * Genera, valida y extrae información de tokens JWT.
 */
@Slf4j
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private long jwtExpirationMs;
    
    /**
     * Genera un token JWT para un username.
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Genera un token JWT desde Authentication.
     */
    public String generateToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return generateToken(userPrincipal.getUsername());
    }
    
    /**
     * Extrae el username del token JWT.
     */
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
    
    /**
     * Valida el token JWT.
     */
    public boolean validateToken(String authToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            
            return true;
            
        } catch (MalformedJwtException e) {
            log.error("Token JWT mal formado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string está vacío: {}", e.getMessage());
        }
        
        return false;
    }
}
```

---

## ✅ PASO 58: JwtAuthenticationFilter

**Archivo:** `JwtAuthenticationFilter.java`

```java
package Fullsound.Fullsound.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta requests para validar JWT tokens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Usuario autenticado: {}", username);
            }
        } catch (Exception ex) {
            log.error("No se pudo establecer autenticación de usuario", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extrae el JWT del header Authorization.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
```

---

## ✅ PASO 59: UserDetailsServiceImpl

**Archivo:** `UserDetailsServiceImpl.java`

```java
package Fullsound.Fullsound.security;

import Fullsound.Fullsound.model.entity.Usuario;
import Fullsound.Fullsound.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de UserDetailsService para cargar usuarios desde la BD.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UsuarioRepository usuarioRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        return UserDetailsImpl.build(usuario);
    }
}
```

---

## ✅ PASO 60: UserDetailsImpl

**Archivo:** `UserDetailsImpl.java`

```java
package Fullsound.Fullsound.security;

import Fullsound.Fullsound.model.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementación de UserDetails para Spring Security.
 */
@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    
    private Long id;
    private String username;
    private String email;
    private String password;
    private boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;
    
    /**
     * Construye UserDetailsImpl desde entidad Usuario.
     */
    public static UserDetailsImpl build(Usuario usuario) {
        Collection<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre().name()))
                .collect(Collectors.toList());
        
        return new UserDetailsImpl(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getActivo(),
                authorities
        );
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
```

---

## ✅ PASO 61: SecurityConfig

**Archivo:** `SecurityConfig.java`

```java
package Fullsound.Fullsound.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de seguridad de Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/beats/**").permitAll()
                .requestMatchers("/api/beats/{id}/**").permitAll()
                .requestMatchers("/api/productos/**").permitAll()
                .requestMatchers("/api/usuarios/{id}/public").permitAll()
                .requestMatchers("/api/usuarios/username/{username}").permitAll()
                .requestMatchers("/api/reviews/beat/**").permitAll()
                .requestMatchers("/api/pagos/webhook").permitAll()
                
                // Swagger/OpenAPI
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                
                // Actuator (opcional)
                .requestMatchers("/actuator/**").permitAll()
                
                // Resto requiere autenticación
                .anyRequest().authenticated()
            );
        
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir orígenes del frontend
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",  // Vite dev
            "http://localhost:3000",  // React dev alternativo
            "http://localhost:4200",  // Angular
            "https://fullsound.com"   // Producción
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
```

---

## ✅ PASO 61.1: JwtAuthenticationEntryPoint

**Archivo:** `JwtAuthenticationEntryPoint.java`

```java
package Fullsound.Fullsound.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import Fullsound.Fullsound.model.dto.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Maneja errores de autenticación.
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        
        log.error("Error de autenticación: {}", authException.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .error("Unauthorized")
                .message("Se requiere autenticación para acceder a este recurso")
                .path(request.getRequestURI())
                .build();
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
```

---

## 🧪 Verificación

### 1. Verificar configuración en application.properties

```properties
# JWT Configuration
jwt.secret=MySecretKeyForJWTTokenGenerationShouldBeLongEnoughForHS512Algorithm
jwt.expiration=86400000

# Security
spring.security.user.name=admin
spring.security.user.password=admin123
```

### 2. Test de autenticación

```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldAllowPublicEndpoints() throws Exception {
        mockMvc.perform(get("/api/beats"))
                .andExpect(status().isOk());
    }
    
    @Test
    void shouldDenyProtectedEndpointsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/usuarios/me"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void shouldAllowProtectedEndpointsWithValidToken() throws Exception {
        // Login para obtener token
        String loginRequest = "{\"emailOrUsername\":\"testuser\",\"password\":\"password123\"}";
        
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        String token = JsonPath.read(response, "$.data.token");
        
        // Usar token para acceder a endpoint protegido
        mockMvc.perform(get("/api/usuarios/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
```

### 3. Test de JWT Token Provider

```java
@SpringBootTest
class JwtTokenProviderTest {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Test
    void shouldGenerateValidToken() {
        String username = "testuser";
        String token = tokenProvider.generateToken(username);
        
        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals(username, tokenProvider.getUsernameFromToken(token));
    }
    
    @Test
    void shouldRejectInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertFalse(tokenProvider.validateToken(invalidToken));
    }
}
```

---

## 📋 Checklist PASO 57-61

- [ ] JwtTokenProvider.java creado
- [ ] JwtAuthenticationFilter.java creado
- [ ] UserDetailsServiceImpl.java creado
- [ ] UserDetailsImpl.java creado
- [ ] SecurityConfig.java creado
- [ ] JwtAuthenticationEntryPoint.java creado
- [ ] jwt.secret configurado en application.properties
- [ ] CORS configurado
- [ ] Endpoints públicos definidos
- [ ] @EnableMethodSecurity activado
- [ ] Tests de seguridad pasando

---

## 📊 Resumen de Seguridad

| Componente | Responsabilidad | Estado |
|------------|-----------------|--------|
| **JwtTokenProvider** | Generar y validar tokens | ✅ |
| **JwtAuthenticationFilter** | Interceptar requests | ✅ |
| **UserDetailsServiceImpl** | Cargar usuarios | ✅ |
| **UserDetailsImpl** | Wrapper de Usuario | ✅ |
| **SecurityConfig** | Configuración Spring Security | ✅ |
| **JwtAuthenticationEntryPoint** | Manejo de errores 401 | ✅ |

---

## 🔐 Flujo de Autenticación

```
1. Usuario → POST /api/auth/login (email/password)
   ↓
2. AuthenticationManager valida credenciales
   ↓
3. UserDetailsServiceImpl carga usuario desde BD
   ↓
4. JwtTokenProvider genera token JWT
   ↓
5. Backend → Retorna token en AuthResponse
   ↓
6. Frontend guarda token en localStorage
   ↓
7. Frontend → Request con header: Authorization: Bearer {token}
   ↓
8. JwtAuthenticationFilter intercepta request
   ↓
9. JwtTokenProvider valida token
   ↓
10. UserDetailsServiceImpl carga usuario
    ↓
11. SecurityContext almacena Authentication
    ↓
12. Controller accede a usuario autenticado
```

---

## 🎯 Endpoints Públicos vs Protegidos

### Públicos (Sin JWT)
- ✅ `/api/auth/**` - Registro y login
- ✅ `/api/beats/**` - Listado de beats
- ✅ `/api/productos/**` - Listado de productos
- ✅ `/api/usuarios/{id}/public` - Perfiles públicos
- ✅ `/api/reviews/beat/**` - Reviews de beats
- ✅ `/api/pagos/webhook` - Webhooks de Stripe
- ✅ `/swagger-ui/**` - Documentación API

### Protegidos (Requieren JWT)
- 🔒 `/api/usuarios/me` - Perfil del usuario
- 🔒 `/api/carrito/**` - Carrito de compras
- 🔒 `/api/pedidos/**` - Gestión de pedidos
- 🔒 `/api/pagos/**` - Pagos (excepto webhook)

### Solo PRODUCTOR
- 🎵 `/api/beats` POST - Crear beat
- 🎵 `/api/beats/{id}` PUT - Editar beat
- 🎵 `/api/beats/my-beats` - Mis beats

### Solo ADMIN
- 👑 `/api/usuarios` GET - Listar usuarios
- 👑 `/api/productos` POST/PUT/DELETE - CRUD productos
- 👑 `/api/pedidos` GET - Ver todos los pedidos
- 👑 `/api/estadisticas/global` - Stats globales

---

## ⏭️ Siguiente Paso

Continuar con **[12_EXCEPCIONES.md](12_EXCEPCIONES.md)** - PASO 62-63 (Manejo Global de Excepciones)
