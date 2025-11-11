# ⚙️ Fase 3D: Configuración de Build

## 🎯 Objetivo

Configurar correctamente `vite.config.js`, `package.json`, y archivos de configuración para que el proyecto funcione en la nueva ubicación.

---

## 📋 Archivos a Configurar

1. `package.json` - Dependencias y scripts
2. `vite.config.js` - Configuración de Vite
3. `index.html` - HTML principal
4. `.env` - Variables de entorno (opcional)

---

## 🔧 Paso 1: Copiar y Ajustar package.json

### Copiar archivo

```powershell
Copy-Item -Path "c:\Users\WIN-D4MAG3\Documents\Repos\FullSound_React\package.json" `
          -Destination "c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend\package.json"
```

### Ajustes necesarios

**ANTES:**
```json
{
  "name": "fullsound_react",
  "version": "0.0.0",
  // ...
}
```

**DESPUÉS:**
```json
{
  "name": "fullsound-frontend",
  "version": "1.0.0",
  // ... resto igual
}
```

✅ **Solo cambiar nombre y versión, todo lo demás mantener igual**.

---

## 🔧 Paso 2: Ajustar vite.config.js

### Copiar archivo

```powershell
Copy-Item -Path "c:\Users\WIN-D4MAG3\Documents\Repos\FullSound_React\vite.config.js" `
          -Destination "c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend\vite.config.js"
```

### Ajuste CRÍTICO: Quitar basename

**ANTES:**
```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  base: '/FullSound_React/',  // ← QUITAR ESTO
  server: {
    port: 3000
  }
})
```

**DESPUÉS:**
```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  base: '/',  // ← Cambiar a raíz
  server: {
    port: 3000
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets'
  }
})
```

---

## 🔧 Paso 3: Copiar index.html

```powershell
Copy-Item -Path "c:\Users\WIN-D4MAG3\Documents\Repos\FullSound_React\index.html" `
          -Destination "c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend\index.html"
```

✅ **No necesita cambios**.

---

## 🔧 Paso 4: Instalar Dependencias

```powershell
cd c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend
npm install
```

Esto instalará todas las dependencias del `package.json`.

---

## 🔧 Paso 5: Crear .gitignore

```powershell
# Crear .gitignore en frontend
New-Item -Path "frontend\.gitignore" -ItemType File
```

Contenido:
```
node_modules/
dist/
.env
.env.local
*.log
```

---

## ✅ Checklist de Configuración

### Archivos Copiados
- [ ] `package.json` copiado y nombre actualizado
- [ ] `vite.config.js` copiado y `base` ajustado a `/`
- [ ] `index.html` copiado
- [ ] `.gitignore` creado

### Configuración de Vite
- [ ] `base: '/'` configurado (en lugar de `/FullSound_React/`)
- [ ] `server.port: 3000` configurado
- [ ] `build.outDir: 'dist'` configurado

### Dependencias
- [ ] `npm install` ejecutado exitosamente
- [ ] `node_modules/` creado
- [ ] Sin errores de dependencias

---

## 🧪 Verificación

### 1. Probar desarrollo

```powershell
cd c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend
npm run dev
```

Deberías ver:
```
VITE v7.1.7  ready in XXX ms

➜  Local:   http://localhost:3000/
➜  Network: use --host to expose
```

### 2. Abrir navegador

Abre `http://localhost:3000` y verifica:
- [ ] Aplicación carga sin errores
- [ ] No hay errores 404 en consola
- [ ] Estilos se aplican correctamente
- [ ] Navegación funciona
- [ ] Imágenes cargan

### 3. Probar build

```powershell
npm run build
```

Deberías ver:
```
vite v7.1.7 building for production...
✓ XXX modules transformed.
dist/index.html                   X.XX kB
dist/assets/index-XXXX.css        XXX kB │ gzip: XX kB
dist/assets/index-XXXX.js         XXX kB │ gzip: XX kB
✓ built in XXXms
```

Verifica que se creó la carpeta `dist/` con:
- [ ] `index.html`
- [ ] `assets/` con archivos JS y CSS
- [ ] Carpeta de imágenes/audio

---

## 📝 Archivos de Configuración Finales

### package.json (resumen)
```json
{
  "name": "fullsound-frontend",
  "version": "1.0.0",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "test": "vitest"
  },
  "dependencies": {
    "react": "^19.1.1",
    "react-dom": "^19.1.1",
    "react-router-dom": "^7.9.4",
    "axios": "^1.12.2",
    "react-slick": "^0.30.2",
    "slick-carousel": "^1.8.1"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.3.4",
    "vite": "^7.1.7"
  }
}
```

### vite.config.js (completo)
```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
  plugins: [react()],
  base: '/',
  
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  
  server: {
    port: 3000,
    open: true
  },
  
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false
  }
})
```

---

## 🎯 Resultado Esperado

✅ **`npm run dev` ejecuta sin errores**  
✅ **Aplicación abre en `http://localhost:3000`**  
✅ **No hay errores en consola del navegador**  
✅ **Rutas funcionan sin `/FullSound_React/`**  
✅ **`npm run build` genera carpeta `dist/` correctamente**  
✅ **Frontend idéntico al proyecto original**

---

## ⚠️ Problemas Comunes y Soluciones

### Error: "Cannot find module 'vite'"
```powershell
npm install vite --save-dev
```

### Error: "Failed to resolve import"
Verifica que las rutas en imports sean correctas:
```javascript
import Component from './components/Component'  // ✅
import Component from '../components/Component' // ✅
```

### Error 404 en assets
Verifica que `base: '/'` esté configurado en `vite.config.js`.

---

## 🔄 Próximo Paso

➡️ **[08_CHECKLIST_MIGRACION.md](08_CHECKLIST_MIGRACION.md)** - Validar que todo funciona correctamente
