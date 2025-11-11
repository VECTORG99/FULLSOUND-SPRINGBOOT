# 📦 Fase 3A: Migración de Componentes

## 🎯 Objetivo

Copiar los **17 componentes React** del proyecto `FullSound_React` al proyecto `FULLSOUND-SPRINGBOOT`, manteniendo exactamente la misma funcionalidad.

---

## 📂 Estructura Destino

```
FULLSOUND-SPRINGBOOT/
└── frontend/                    ← Crear esta carpeta
    ├── public/
    ├── src/
    │   ├── components/         ← Aquí van los componentes
    │   ├── services/
    │   ├── assets/
    │   ├── App.jsx
    │   ├── main.jsx
    │   └── index.css
    ├── package.json
    ├── vite.config.js
    └── index.html
```

---

## 📋 Lista de Componentes a Copiar

### ✅ Componentes Principales (17 total)

| # | Componente | Archivo | Complejidad | Descripción |
|---|------------|---------|-------------|-------------|
| 1 | Inicio | `Inicio.jsx` | 🟡 Media | Página principal |
| 2 | Beats | `Beats.jsx` | 🟡 Media | Catálogo de beats |
| 3 | Carrito | `Carrito.jsx` | 🟡 Media | Carrito de compras |
| 4 | Login | `Login.jsx` | 🟡 Media | Autenticación |
| 5 | Registro | `Registro.jsx` | 🟡 Media | Registro de usuarios |
| 6 | Administracion | `Administracion.jsx` | 🟠 Alta | Panel admin |
| 7 | AdminBeats | `AdminBeats.jsx` | 🟠 Alta | Gestión de beats |
| 8 | Producto | `Producto.jsx` | 🟢 Baja | Detalle de beat |
| 9 | Header | `Header.jsx` | 🟡 Media | Navegación |
| 10 | Footer | `Footer.jsx` | 🟢 Baja | Pie de página |
| 11 | Layout | `Layout.jsx` | 🟢 Baja | Estructura base |
| 12 | Carrusel | `Carrusel.jsx` | 🟢 Baja | Slider de imágenes |
| 13 | Preloader | `Preloader.jsx` | 🟢 Baja | Pantalla de carga |
| 14 | Main | `Main.jsx` | 🟢 Baja | Contenedor principal |
| 15 | Creditos | `Creditos.jsx` | 🟢 Baja | Página de créditos |
| 16 | Terminos | `Terminos.jsx` | 🟢 Baja | Términos y condiciones |
| 17 | ProtectedRoute | `ProtectedRoute.jsx` | 🟡 Media | Rutas protegidas |

---

## 🔧 Proceso de Migración

### Paso 1: Crear estructura de carpetas

```powershell
cd c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT

# Crear carpeta frontend
New-Item -ItemType Directory -Path "frontend"
New-Item -ItemType Directory -Path "frontend\src"
New-Item -ItemType Directory -Path "frontend\src\components"
New-Item -ItemType Directory -Path "frontend\public"
```

### Paso 2: Copiar todos los componentes

```powershell
# Copiar carpeta completa de componentes
Copy-Item -Path "c:\Users\WIN-D4MAG3\Documents\Repos\FullSound_React\src\components\*" `
          -Destination "c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend\src\components\" `
          -Recurse
```

### Paso 3: Copiar archivos raíz de src

```powershell
# Copiar App.jsx, main.jsx, etc.
Copy-Item -Path "c:\Users\WIN-D4MAG3\Documents\Repos\FullSound_React\src\App.jsx" `
          -Destination "c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend\src\"

Copy-Item -Path "c:\Users\WIN-D4MAG3\Documents\Repos\FullSound_React\src\main.jsx" `
          -Destination "c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend\src\"

Copy-Item -Path "c:\Users\WIN-D4MAG3\Documents\Repos\FullSound_React\src\index.css" `
          -Destination "c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend\src\"

Copy-Item -Path "c:\Users\WIN-D4MAG3\Documents\Repos\FullSound_React\src\App.css" `
          -Destination "c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend\src\"
```

---

## ✏️ Ajustes Necesarios

### 1. App.jsx - Quitar basename

**ANTES (FullSound_React):**
```javascript
<BrowserRouter basename="/FullSound_React">
  <Routes>
    {/* rutas */}
  </Routes>
</BrowserRouter>
```

**DESPUÉS (FULLSOUND-SPRINGBOOT):**
```javascript
<BrowserRouter>
  <Routes>
    {/* rutas */}
  </Routes>
</BrowserRouter>
```

### 2. Rutas en componentes - Cambiar a absolutas

**ANTES:**
```javascript
import Header from './components/Header';
```

**DESPUÉS:** (si es necesario)
```javascript
import Header from '@/components/Header'; // o mantener ./components/Header
```

### 3. No cambiar imports relativos

Los imports como `./components/Header` seguirán funcionando, **NO los cambies**.

---

## ✅ Checklist de Migración

### Archivos Principales
- [ ] `App.jsx` copiado
- [ ] `main.jsx` copiado
- [ ] `index.css` copiado
- [ ] `App.css` copiado
- [ ] `setupTests.js` copiado (si existe)

### Componentes (17)
- [ ] `Inicio.jsx`
- [ ] `Beats.jsx`
- [ ] `Carrito.jsx`
- [ ] `Login.jsx`
- [ ] `Registro.jsx`
- [ ] `Administracion.jsx`
- [ ] `AdminBeats.jsx`
- [ ] `Producto.jsx`
- [ ] `Header.jsx`
- [ ] `Footer.jsx`
- [ ] `Layout.jsx`
- [ ] `Carrusel.jsx`
- [ ] `Preloader.jsx`
- [ ] `Main.jsx`
- [ ] `Creditos.jsx`
- [ ] `Terminos.jsx`
- [ ] `ProtectedRoute.jsx`

### Ajustes
- [ ] `basename` eliminado de BrowserRouter
- [ ] Imports revisados (mantener relativos)
- [ ] No hay errores de sintaxis

---

## 🧪 Verificación

Después de copiar, verifica:

```powershell
# Ir a carpeta frontend
cd c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend

# Instalar dependencias (si aún no está hecho)
npm install

# Probar que compile
npm run dev
```

Deberías ver:
```
VITE v7.1.7  ready in XXX ms

➜  Local:   http://localhost:3000/
```

---

## 🎯 Resultado Esperado

✅ **Carpeta `frontend/src/components/` con 17 archivos**  
✅ **Todos los componentes copiados sin modificaciones**  
✅ **App.jsx sin basename**  
✅ **Compilación exitosa con Vite**  
✅ **Aplicación corriendo en desarrollo**

---

## 🔄 Próximo Paso

➡️ **[05_MIGRACION_SERVICIOS.md](05_MIGRACION_SERVICIOS.md)** - Copiar servicios API
