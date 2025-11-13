# 🧪 PASO 64-67: Testing Unitario e Integración

## 🎯 Objetivo
Implementar tests unitarios y de integración para garantizar la calidad y funcionalidad del código.

---

## 📁 Ubicación Base
```
Fullsound/src/test/java/Fullsound/Fullsound/
```

---

## ✅ PASO 64: Tests de Repositories

### 64.1 - BeatRepositoryTest

**Archivo:** `repository/BeatRepositoryTest.java`

```java
package Fullsound.Fullsound.repository;

import Fullsound.Fullsound.model.entity.Beat;
import Fullsound.Fullsound.model.entity.Usuario;
import Fullsound.Fullsound.model.enums.EstadoBeat;
import Fullsound.Fullsound.model.enums.TipoLicencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BeatRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private BeatRepository beatRepository;
    
    private Usuario productor;
    
    @BeforeEach
    void setUp() {
        productor = new Usuario();
        productor.setUsername("producer1");
        productor.setEmail("producer@test.com");
        productor.setPassword("password");
        productor.setActivo(true);
        productor = entityManager.persist(productor);
    }
    
    @Test
    void shouldFindByEstado() {
        // Given
        Beat beat1 = createBeat("Beat1", EstadoBeat.ACTIVO);
        Beat beat2 = createBeat("Beat2", EstadoBeat.INACTIVO);
        entityManager.persist(beat1);
        entityManager.persist(beat2);
        entityManager.flush();
        
        // When
        Page<Beat> result = beatRepository.findByEstado(EstadoBeat.ACTIVO, PageRequest.of(0, 10));
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitulo()).isEqualTo("Beat1");
    }
    
    @Test
    void shouldFindByGeneroAndEstado() {
        // Given
        Beat trapBeat = createBeat("Trap Beat", EstadoBeat.ACTIVO);
        trapBeat.setGenero("Trap");
        
        Beat hiphopBeat = createBeat("HipHop Beat", EstadoBeat.ACTIVO);
        hiphopBeat.setGenero("HipHop");
        
        entityManager.persist(trapBeat);
        entityManager.persist(hiphopBeat);
        entityManager.flush();
        
        // When
        Page<Beat> result = beatRepository.findByGeneroAndEstado("Trap", EstadoBeat.ACTIVO, PageRequest.of(0, 10));
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getGenero()).isEqualTo("Trap");
    }
    
    @Test
    void shouldFindDestacados() {
        // Given
        Beat beat1 = createBeat("Normal Beat", EstadoBeat.ACTIVO);
        beat1.setDestacado(false);
        
        Beat beat2 = createBeat("Featured Beat", EstadoBeat.ACTIVO);
        beat2.setDestacado(true);
        
        entityManager.persist(beat1);
        entityManager.persist(beat2);
        entityManager.flush();
        
        // When
        List<Beat> result = beatRepository.findDestacados(PageRequest.of(0, 10));
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitulo()).isEqualTo("Featured Beat");
    }
    
    @Test
    void shouldIncrementarReproducciones() {
        // Given
        Beat beat = createBeat("Test Beat", EstadoBeat.ACTIVO);
        beat = entityManager.persist(beat);
        Long beatId = beat.getId();
        entityManager.flush();
        
        // When
        beatRepository.incrementarReproducciones(beatId);
        entityManager.clear();
        
        // Then
        Beat updated = beatRepository.findById(beatId).orElseThrow();
        assertThat(updated.getReproduciones()).isEqualTo(1);
    }
    
    private Beat createBeat(String titulo, EstadoBeat estado) {
        Beat beat = new Beat();
        beat.setTitulo(titulo);
        beat.setProductor(productor);
        beat.setEstado(estado);
        beat.setGenero("Test");
        beat.setBpm(140);
        beat.setPrecio(new BigDecimal("50.00"));
        beat.setTipoLicencia(TipoLicencia.BASICA);
        return beat;
    }
}
```

---

## ✅ PASO 65: Tests de Services

### 65.1 - BeatServiceImplTest

**Archivo:** `service/BeatServiceImplTest.java`

```java
package Fullsound.Fullsound.service;

import Fullsound.Fullsound.exception.ForbiddenException;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.BeatMapper;
import Fullsound.Fullsound.model.dto.request.BeatCreateRequest;
import Fullsound.Fullsound.model.dto.response.BeatResponse;
import Fullsound.Fullsound.model.entity.Beat;
import Fullsound.Fullsound.model.entity.Usuario;
import Fullsound.Fullsound.model.enums.EstadoBeat;
import Fullsound.Fullsound.model.enums.TipoLicencia;
import Fullsound.Fullsound.repository.BeatRepository;
import Fullsound.Fullsound.service.impl.BeatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeatServiceImplTest {
    
    @Mock
    private BeatRepository beatRepository;
    
    @Mock
    private BeatMapper beatMapper;
    
    @Mock
    private UsuarioService usuarioService;
    
    @InjectMocks
    private BeatServiceImpl beatService;
    
    private Usuario productor;
    private Beat beat;
    private BeatCreateRequest createRequest;
    
    @BeforeEach
    void setUp() {
        productor = new Usuario();
        productor.setId(1L);
        productor.setUsername("producer1");
        
        beat = new Beat();
        beat.setId(1L);
        beat.setTitulo("Test Beat");
        beat.setProductor(productor);
        beat.setEstado(EstadoBeat.ACTIVO);
        
        createRequest = new BeatCreateRequest();
        createRequest.setTitulo("New Beat");
        createRequest.setGenero("Trap");
        createRequest.setBpm(140);
        createRequest.setPrecio(new BigDecimal("50.00"));
        createRequest.setTipoLicencia(TipoLicencia.BASICA);
    }
    
    @Test
    void shouldCreateBeatWhenUserIsProductor() {
        // Given
        when(usuarioService.isCurrentUserProductor()).thenReturn(true);
        when(usuarioService.getCurrentUsuarioEntity()).thenReturn(productor);
        when(beatMapper.toEntity(any())).thenReturn(beat);
        when(beatRepository.save(any())).thenReturn(beat);
        when(beatMapper.toResponse(any())).thenReturn(new BeatResponse());
        
        // When
        BeatResponse result = beatService.create(createRequest);
        
        // Then
        assertThat(result).isNotNull();
        verify(beatRepository).save(any(Beat.class));
    }
    
    @Test
    void shouldThrowForbiddenExceptionWhenUserIsNotProductor() {
        // Given
        when(usuarioService.isCurrentUserProductor()).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> beatService.create(createRequest))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Solo los productores pueden crear beats");
        
        verify(beatRepository, never()).save(any());
    }
    
    @Test
    void shouldFindBeatById() {
        // Given
        when(beatRepository.findByIdWithProductor(1L)).thenReturn(Optional.of(beat));
        when(beatMapper.toResponse(beat)).thenReturn(new BeatResponse());
        
        // When
        BeatResponse result = beatService.findById(1L);
        
        // Then
        assertThat(result).isNotNull();
        verify(beatRepository).findByIdWithProductor(1L);
    }
    
    @Test
    void shouldThrowResourceNotFoundExceptionWhenBeatNotExists() {
        // Given
        when(beatRepository.findByIdWithProductor(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> beatService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Beat no encontrado");
    }
    
    @Test
    void shouldIncrementarReproducciones() {
        // Given
        doNothing().when(beatRepository).incrementarReproducciones(1L);
        
        // When
        beatService.incrementarReproducciones(1L);
        
        // Then
        verify(beatRepository).incrementarReproducciones(1L);
    }
}
```

---

## ✅ PASO 66: Tests de Controllers

### 66.1 - BeatControllerTest

**Archivo:** `controller/BeatControllerTest.java`

```java
package Fullsound.Fullsound.controller;

import Fullsound.Fullsound.model.dto.response.BeatResponse;
import Fullsound.Fullsound.model.dto.response.BeatSummaryResponse;
import Fullsound.Fullsound.service.BeatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeatController.class)
class BeatControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private BeatService beatService;
    
    @Test
    void shouldGetBeatById() throws Exception {
        // Given
        BeatResponse beatResponse = BeatResponse.builder()
                .id(1L)
                .titulo("Test Beat")
                .build();
        
        when(beatService.findById(1L)).thenReturn(beatResponse);
        
        // When & Then
        mockMvc.perform(get("/api/beats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.titulo").value("Test Beat"));
    }
    
    @Test
    void shouldGetAllBeats() throws Exception {
        // Given
        List<BeatSummaryResponse> beats = Arrays.asList(
                BeatSummaryResponse.builder().id(1L).titulo("Beat 1").build(),
                BeatSummaryResponse.builder().id(2L).titulo("Beat 2").build()
        );
        Page<BeatSummaryResponse> page = new PageImpl<>(beats);
        
        when(beatService.findAll(any(Pageable.class))).thenReturn(page);
        
        // When & Then
        mockMvc.perform(get("/api/beats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }
    
    @Test
    @WithMockUser(roles = "PRODUCTOR")
    void shouldCreateBeatWhenUserIsProductor() throws Exception {
        // Given
        String createRequest = objectMapper.writeValueAsString(
                Map.of(
                        "titulo", "New Beat",
                        "genero", "Trap",
                        "bpm", 140,
                        "precio", 50.00,
                        "tipoLicencia", "BASICA"
                )
        );
        
        BeatResponse beatResponse = BeatResponse.builder()
                .id(1L)
                .titulo("New Beat")
                .build();
        
        when(beatService.create(any())).thenReturn(beatResponse);
        
        // When & Then
        mockMvc.perform(post("/api/beats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.titulo").value("New Beat"));
    }
}
```

---

## ✅ PASO 67: Tests de Integración

### 67.1 - AuthIntegrationTest

**Archivo:** `integration/AuthIntegrationTest.java`

```java
package Fullsound.Fullsound.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        String registerRequest = objectMapper.writeValueAsString(
                Map.of(
                        "username", "newuser",
                        "email", "newuser@test.com",
                        "password", "password123"
                )
        );
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.email").value("newuser@test.com"));
    }
    
    @Test
    void shouldFailRegisterWithDuplicateUsername() throws Exception {
        // Given
        String registerRequest = objectMapper.writeValueAsString(
                Map.of(
                        "username", "existinguser",
                        "email", "new@test.com",
                        "password", "password123"
                )
        );
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    void shouldLoginSuccessfully() throws Exception {
        // Given
        String loginRequest = objectMapper.writeValueAsString(
                Map.of(
                        "emailOrUsername", "testuser",
                        "password", "password123"
                )
        );
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists());
    }
}
```

---

## 🧪 Test Data SQL

**Archivo:** `src/test/resources/test-data.sql`

```sql
-- Roles
INSERT INTO roles (id, nombre) VALUES (1, 'USER');
INSERT INTO roles (id, nombre) VALUES (2, 'PRODUCTOR');
INSERT INTO roles (id, nombre) VALUES (3, 'ADMIN');

-- Usuario de prueba
INSERT INTO usuarios (id, username, email, password, activo, created_at, updated_at)
VALUES (1, 'testuser', 'test@test.com', '$2a$10$encrypted...', true, NOW(), NOW());

-- Relación usuario-rol
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (1, 1);

-- Usuario existente para tests de duplicados
INSERT INTO usuarios (id, username, email, password, activo, created_at, updated_at)
VALUES (2, 'existinguser', 'existing@test.com', '$2a$10$encrypted...', true, NOW(), NOW());
```

---

## 📋 Checklist PASO 64-67

**Repository Tests:**
- [ ] BeatRepositoryTest.java creado
- [ ] Tests de queries personalizadas
- [ ] Tests de @Modifying operations
- [ ] Tests de JOIN FETCH

**Service Tests:**
- [ ] BeatServiceImplTest.java creado
- [ ] AuthServiceImplTest.java creado
- [ ] Mocks de repositories y mappers
- [ ] Tests de casos de éxito
- [ ] Tests de excepciones
- [ ] Cobertura > 80%

**Controller Tests:**
- [ ] BeatControllerTest.java creado
- [ ] @WebMvcTest configurado
- [ ] Tests con @WithMockUser
- [ ] Tests de validación
- [ ] Tests de respuestas JSON

**Integration Tests:**
- [ ] AuthIntegrationTest.java creado
- [ ] @SpringBootTest configurado
- [ ] @Transactional para rollback
- [ ] test-data.sql creado
- [ ] Tests end-to-end

**Configuración:**
- [ ] application-test.properties creado
- [ ] H2 database para tests
- [ ] Maven Surefire configurado

---

## 📊 Resumen de Testing

| Tipo de Test | Archivos | Cobertura Objetivo |
|--------------|----------|-------------------|
| **Repository** | 10 | 90% |
| **Service** | 9 | 85% |
| **Controller** | 9 | 80% |
| **Integration** | 5 | 70% |

**Total:** ~33 test files

---

## ⏭️ Siguiente Paso

Continuar con **[14_DEPLOYMENT.md](14_DEPLOYMENT.md)** - PASO 68-71 (Docker y Deployment)
