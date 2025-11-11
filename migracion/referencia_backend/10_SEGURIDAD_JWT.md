# 🔐 SEGURIDAD Y JWT - FULLSOUND

## 🎯 Objetivo

Implementar Spring Security con autenticación JWT compatible con el frontend React.

---

## 📦 Dependencias JWT (ya en pom.xml)

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
```

---

## ⚙️ Configuración de Seguridad

### 1. SecurityConfig.java

```java
package com.fullsound.config;

import com.fullsound.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors()
            .and()
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos
                .requestMatchers("/", "/index.html", "/assets/**", 
                               "/static/**", "/favicon.ico").permitAll()
                
                // Rutas del frontend
                .requestMatchers("/login", "/registro", "/beats", 
                               "/carrito", "/producto/**", "/creditos",
                               "/terminos", "/main").permitAll()
                
                // Auth endpoints
                .requestMatchers("/api/auth/**").permitAll()
                
                // Beats públicos (GET)
                .requestMatchers(HttpMethod.GET, "/api/beats/**").permitAll()
                
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/beats").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/beats/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/beats/**").hasRole("ADMIN")
                
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthenticationFilter, 
                           UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 🎫 JWT Token Provider

```java
package com.fullsound.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    public String generateToken(String correo) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String getCorreoFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

---

## 🔒 JWT Authentication Filter

```java
package com.fullsound.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                  CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws IOException, ServletException {
        try {
            String jwt = getJwtFromRequest(request);
            
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                String correo = jwtTokenProvider.getCorreoFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(correo);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("No se pudo establecer autenticación", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

---

## 👤 Custom UserDetailsService

```java
package com.fullsound.security;

import com.fullsound.model.Usuario;
import com.fullsound.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UsuarioRepository usuarioRepository;
    
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Usuario no encontrado: " + correo)
                );
        
        return new User(
            usuario.getCorreo(),
            usuario.getPassword(),
            Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
            )
        );
    }
}
```

---

## 🌐 CORS Configuration

```java
package com.fullsound.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Orígenes permitidos
        config.addAllowedOrigin("http://localhost:5173");  // Vite
        config.addAllowedOrigin("http://localhost:8080");  // Spring Boot
        config.addAllowedOrigin("https://fullsound.com");  // Producción
        
        // Métodos HTTP
        config.addAllowedMethod("*");
        
        // Headers
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.addExposedHeader("Authorization");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
```

---

## ⚙️ application.properties

```properties
# JWT Configuration
jwt.secret=fullsound_secret_key_super_secure_2025_change_this_in_production
jwt.expiration=86400000
# 86400000ms = 24 horas
```

---

## ✅ Checklist

- [ ] Spring Security configurado
- [ ] JWT generación funciona
- [ ] JWT validación funciona
- [ ] Filtro de autenticación aplicado
- [ ] CORS configurado
- [ ] Roles y permisos funcionan
- [ ] Login retorna token válido
- [ ] Frontend puede usar token

---

**Próximo Paso**: [11_TESTING_INTEGRACION.md](11_TESTING_INTEGRACION.md)
