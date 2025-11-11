# 📦 Fase 3B: Migración de Servicios API

## 🎯 Objetivo

Copiar los **6 servicios API** del proyecto `FullSound_React` al proyecto `FULLSOUND-SPRINGBOOT`, manteniendo la funcionalidad y el modo local.

---

## 📋 Lista de Servicios a Copiar

| # | Servicio | Archivo | Descripción |
|---|----------|---------|-------------|
| 1 | API Base | `api.js` | Configuración Axios, interceptores |
| 2 | Auth | `authService.js` | Login, registro, logout |
| 3 | Beats | `beatsService.js` | CRUD de beats |
| 4 | Carrito | `carritoService.js` | Gestión del carrito |
| 5 | Usuarios | `usuariosService.js` | Gestión de usuarios |
| 6 | Index | `index.js` | Exportaciones centralizadas |

---

## 🔧 Proceso de Migración

### Paso 1: Copiar carpeta de servicios

```powershell
# Copiar toda la carpeta services
Copy-Item -Path "c:\Users\WIN-D4MAG3\Documents\Repos\FullSound_React\src\services\*" `
          -Destination "c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend\src\services\" `
          -Recurse
```

---

## ✏️ Ajustes Necesarios

### 1. api.js - Actualizar BASE_URL

**ANTES:**
```javascript
const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
```

**DESPUÉS:**
```javascript
// Mismo código, pero ahora apuntará a Spring Boot local
const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
```

✅ **No necesita cambios** - ya está configurado correctamente.

### 2. authService.js - Mantener modo local

El archivo ya tiene modo local implementado:

```javascript
const isLocalMode = () => {
  const apiUrl = import.meta.env.VITE_API_URL || '';
  return apiUrl.includes('localhost') || apiUrl === '';
};
```

✅ **No necesita cambios** - funciona para desarrollo local.

### 3. Verificar que todos los servicios usan `api.js`

Todos los servicios deben importar la instancia de axios:

```javascript
import api from './api';
```

✅ **Si ya lo hacen, no cambiar nada**.

---

## 📝 Resumen de Archivos

### api.js
```javascript
import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
});

// Interceptores (ya existentes)
api.interceptors.request.use(/* ... */);
api.interceptors.response.use(/* ... */);

export default api;
```

### authService.js
```javascript
import api from './api';

// Modo local para desarrollo
const isLocalMode = () => {
  const apiUrl = import.meta.env.VITE_API_URL || '';
  return apiUrl.includes('localhost') || apiUrl === '';
};

export const login = async (email, password) => {
  if (isLocalMode()) {
    // Simulación local
    // ...
  }
  const response = await api.post('/auth/login', { email, password });
  return response.data;
};

// ... resto de funciones
```

### beatsService.js
```javascript
import api from './api';

export const getAllBeats = async () => {
  const response = await api.get('/beats');
  return response.data;
};

// ... resto de funciones
```

### carritoService.js
```javascript
import api from './api';

export const getCarrito = async () => {
  const response = await api.get('/carrito');
  return response.data;
};

// ... resto de funciones
```

### usuariosService.js
```javascript
import api from './api';

export const getUsuarios = async () => {
  const response = await api.get('/usuarios');
  return response.data;
};

// ... resto de funciones
```

### index.js
```javascript
export * from './authService';
export * from './beatsService';
export * from './carritoService';
export * from './usuariosService';
export { default as api } from './api';
```

---

## ✅ Checklist de Migración

### Archivos Copiados
- [ ] `api.js` copiado
- [ ] `authService.js` copiado
- [ ] `beatsService.js` copiado
- [ ] `carritoService.js` copiado
- [ ] `usuariosService.js` copiado
- [ ] `index.js` copiado

### Verificaciones
- [ ] Todos los servicios importan `api.js`
- [ ] `BASE_URL` apunta a `http://localhost:8080/api`
- [ ] Modo local está activo en `authService.js`
- [ ] No hay errores de sintaxis
- [ ] Imports funcionan correctamente

---

## 🧪 Verificación

Verifica que los imports funcionan:

```javascript
// En cualquier componente
import { login, getAllBeats, getCarrito } from '@/services';
// o
import { login } from '@/services/authService';
```

---

## 🎯 Resultado Esperado

✅ **Carpeta `frontend/src/services/` con 6 archivos**  
✅ **Todos los servicios copiados sin cambios mayores**  
✅ **BASE_URL configurada para Spring Boot local**  
✅ **Modo local activo para desarrollo**  
✅ **Importaciones funcionando**

---

## 🔄 Próximo Paso

➡️ **[06_MIGRACION_ASSETS.md](06_MIGRACION_ASSETS.md)** - Copiar CSS, imágenes, audio, fuentes
