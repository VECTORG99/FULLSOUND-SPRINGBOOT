# 🧪 TESTING E INTEGRACIÓN - FULLSOUND

## 🎯 Objetivo

Implementar tests unitarios, de integración y E2E para asegurar la calidad del sistema migrado.

---

## 📦 Tests Backend (JUnit + Spring Boot Test)

### 1. AuthService Tests

```java
package com.fullsound.service;

import com.fullsound.dto.*;
import com.fullsound.model.*;
import com.fullsound.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Test
    void registroExitoso() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Test User");
        request.setCorreo("test@test.com");
        request.setPassword("password123");
        
        AuthResponse response = authService.register(request);
        
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("test@test.com", response.getUser().getCorreo());
    }
    
    @Test
    void loginExitoso() {
        // Crear usuario primero
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNombre("Test User");
        registerRequest.setCorreo("login@test.com");
        registerRequest.setPassword("password123");
        authService.register(registerRequest);
        
        // Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setCorreo("login@test.com");
        loginRequest.setPassword("password123");
        
        AuthResponse response = authService.login(loginRequest);
        
        assertNotNull(response);
        assertNotNull(response.getToken());
    }
    
    @Test
    void deteccionRolAdmin() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Admin User");
        request.setCorreo("admin@admin.cl");
        request.setPassword("admin123");
        
        AuthResponse response = authService.register(request);
        
        assertEquals("admin", response.getUser().getRol());
    }
}
```

### 2. BeatService Tests

```java
@SpringBootTest
@Transactional
class BeatServiceTest {
    
    @Autowired
    private BeatService beatService;
    
    @Test
    void crearBeat() {
        BeatDTO beat = beatService.crear(
            "Test Beat",
            "Test Artist",
            "Trap",
            15000.0,
            "Beat de prueba",
            null,
            null
        );
        
        assertNotNull(beat);
        assertEquals("Test Beat", beat.getNombre());
    }
    
    @Test
    void listarBeats() {
        List<BeatDTO> beats = beatService.listarTodos();
        assertNotNull(beats);
    }
}
```

### 3. Controller Tests (MockMvc)

```java
package com.fullsound.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void registroEndpoint() throws Exception {
        String json = """
            {
                "nombre": "Test User",
                "correo": "test@test.com",
                "password": "password123"
            }
        """;
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.correo").value("test@test.com"));
    }
}
```

---

## ⚛️ Tests Frontend (Vitest + React Testing Library)

### 1. Component Tests

```javascript
// Login.test.jsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Login from './Login';
import * as authService from '../services/authService';

vi.mock('../services/authService');

describe('Login Component', () => {
  test('renderiza formulario', () => {
    render(
      <BrowserRouter>
        <Login />
      </BrowserRouter>
    );
    
    expect(screen.getByPlaceholderText(/correo/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/contraseña/i)).toBeInTheDocument();
  });
  
  test('login exitoso redirige', async () => {
    authService.login.mockResolvedValue({
      data: {
        user: { correo: 'test@test.com', rol: 'usuario' },
        token: 'fake-token'
      }
    });
    
    render(
      <BrowserRouter>
        <Login />
      </BrowserRouter>
    );
    
    fireEvent.change(screen.getByPlaceholderText(/correo/i), {
      target: { value: 'test@test.com' }
    });
    fireEvent.change(screen.getByPlaceholderText(/contraseña/i), {
      target: { value: 'password123' }
    });
    fireEvent.click(screen.getByText(/iniciar sesión/i));
    
    await waitFor(() => {
      expect(authService.login).toHaveBeenCalled();
    });
  });
});
```

### 2. Service Tests

```javascript
// authService.test.js
import { describe, test, expect, vi } from 'vitest';
import { login, register } from './authService';
import api from './api';

vi.mock('./api');

describe('authService', () => {
  test('login exitoso', async () => {
    api.post.mockResolvedValue({
      data: {
        user: { correo: 'test@test.com' },
        token: 'fake-token'
      }
    });
    
    const result = await login({
      correo: 'test@test.com',
      password: 'password123'
    });
    
    expect(result.data.token).toBe('fake-token');
    expect(localStorage.getItem('token')).toBe('fake-token');
  });
});
```

---

## 🔗 Tests de Integración (API + Frontend)

### Cypress E2E (opcional)

```javascript
// cypress/e2e/auth.cy.js
describe('Flujo de Autenticación', () => {
  it('registro y login completo', () => {
    // Visitar página de registro
    cy.visit('/registro');
    
    // Llenar formulario
    cy.get('input[name="nombre"]').type('Test User');
    cy.get('input[name="correo"]').type('test@test.com');
    cy.get('input[name="password"]').type('password123');
    cy.get('input[type="checkbox"]').check();
    cy.get('button[type="submit"]').click();
    
    // Verificar redirección
    cy.url().should('include', '/beats');
    cy.contains('Test User').should('be.visible');
  });
  
  it('login como admin', () => {
    cy.visit('/login');
    
    cy.get('input[name="correo"]').type('admin@admin.cl');
    cy.get('input[name="password"]').type('admin123');
    cy.get('button[type="submit"]').click();
    
    cy.url().should('include', '/admin');
    cy.contains('Panel de Administración').should('be.visible');
  });
});
```

---

## ✅ Checklist de Testing

### Backend
- [ ] Tests de AuthService
- [ ] Tests de BeatService
- [ ] Tests de CarritoService
- [ ] Tests de Controllers
- [ ] Tests de Repositories
- [ ] Tests de Seguridad JWT
- [ ] Cobertura > 70%

### Frontend
- [ ] Tests de Login
- [ ] Tests de Registro
- [ ] Tests de Beats
- [ ] Tests de Carrito
- [ ] Tests de AdminBeats
- [ ] Tests de servicios
- [ ] Cobertura > 60%

### Integración
- [ ] Login E2E funciona
- [ ] CRUD de beats E2E funciona
- [ ] Carrito E2E funciona
- [ ] Roles y permisos E2E

---

## 🚀 Comandos de Testing

```bash
# Backend
mvn test
mvn test -Dtest=AuthServiceTest
mvn verify  # Incluye tests de integración

# Frontend
cd frontend
npm test
npm run test:ci  # Con coverage
npm run test:watch  # Watch mode

# E2E (si se usa Cypress)
npm run cypress:open
npm run cypress:run
```

---

**Próximo Paso**: [12_DESPLIEGUE_CONFIGURACION.md](12_DESPLIEGUE_CONFIGURACION.md)
