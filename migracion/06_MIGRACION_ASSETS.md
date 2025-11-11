# 📦 Fase 3C: Migración de Assets

## 🎯 Objetivo

Copiar todos los **assets** (CSS, imágenes, audio, fuentes) del proyecto `FullSound_React` al proyecto `FULLSOUND-SPRINGBOOT`.

---

## 📂 Estructura de Assets

```
FullSound_React/src/assets/
├── css/
│   ├── bootstrap.min.css
│   ├── font-awesome.min.css
│   ├── owl.carousel.min.css
│   └── style.css
├── fonts/
│   └── fontawesome/
├── img/
│   ├── logo.png
│   ├── banner1.jpg
│   └── ... (más imágenes)
└── audio/
    └── ... (archivos de audio)
```

---

## 🔧 Proceso de Migración

### Paso 1: Copiar carpeta completa de assets

```powershell
# Copiar toda la carpeta assets
Copy-Item -Path "c:\Users\WIN-D4MAG3\Documents\Repos\FullSound_React\src\assets\*" `
          -Destination "c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend\src\assets\" `
          -Recurse -Force
```

### Paso 2: Copiar carpeta public (si tiene contenido)

```powershell
# Copiar carpeta public
Copy-Item -Path "c:\Users\WIN-D4MAG3\Documents\Repos\FullSound_React\public\*" `
          -Destination "c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend\public\" `
          -Recurse -Force
```

---

## 📋 Assets a Copiar

### 1. CSS (~320 KB total)

| Archivo | Tamaño | Descripción |
|---------|--------|-------------|
| `bootstrap.min.css` | ~150 KB | Framework CSS |
| `font-awesome.min.css` | ~30 KB | Iconos |
| `owl.carousel.min.css` | ~5 KB | Carrusel |
| `style.css` | ~135 KB | Estilos personalizados |

✅ **Copiar todo sin modificaciones**

### 2. Fuentes (~500 KB total)

```
fonts/
└── fontawesome/
    ├── fontawesome-webfont.eot
    ├── fontawesome-webfont.svg
    ├── fontawesome-webfont.ttf
    ├── fontawesome-webfont.woff
    └── fontawesome-webfont.woff2
```

✅ **Copiar todo sin modificaciones**

### 3. Imágenes (estimado 5-10 MB)

```
img/
├── logo.png
├── banner1.jpg
├── banner2.jpg
├── beat-placeholder.png
└── ... (más imágenes)
```

✅ **Copiar todas las imágenes**

### 4. Audio (estimado 50-100 MB)

```
audio/
└── ... (archivos .mp3, .wav, etc.)
```

✅ **Copiar todos los archivos de audio**

---

## ✏️ Ajustes en Código

### 1. Imports de CSS en componentes

**Verificar que los imports de CSS sigan funcionando:**

```javascript
// En main.jsx o App.jsx
import './assets/css/bootstrap.min.css';
import './assets/css/font-awesome.min.css';
import './assets/css/owl.carousel.min.css';
import './assets/css/style.css';
```

✅ **Si ya existen estos imports, no cambiar nada**.

### 2. Imports de imágenes

**Verificar imports de imágenes en componentes:**

```javascript
// Ejemplo en Header.jsx
import logo from '@/assets/img/logo.png';

// Uso
<img src={logo} alt="Logo" />
```

✅ **Mantener imports existentes**.

### 3. Rutas de audio

**Si hay componentes que reproducen audio:**

```javascript
// Ejemplo
import beatAudio from '@/assets/audio/beat-sample.mp3';

<audio src={beatAudio} controls />
```

✅ **Mantener como está**.

---

## ✅ Checklist de Migración

### CSS
- [ ] `bootstrap.min.css` copiado
- [ ] `font-awesome.min.css` copiado
- [ ] `owl.carousel.min.css` copiado
- [ ] `style.css` copiado
- [ ] Imports de CSS funcionan en componentes

### Fuentes
- [ ] Carpeta `fonts/fontawesome/` copiada completamente
- [ ] Archivos .woff, .woff2, .ttf, .eot, .svg presentes
- [ ] Font Awesome iconos se ven correctamente

### Imágenes
- [ ] Todas las imágenes .png, .jpg, .jpeg copiadas
- [ ] Logo visible
- [ ] Banners visibles
- [ ] Placeholders disponibles

### Audio
- [ ] Archivos .mp3 copiados
- [ ] Archivos .wav copiados (si hay)
- [ ] Reproductores de audio funcionan

---

## 🧪 Verificación

### 1. Verificar CSS

```powershell
cd c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend
npm run dev
```

Abre `http://localhost:3000` y verifica:
- [ ] Bootstrap estilos aplicados
- [ ] Iconos Font Awesome visibles
- [ ] Carrusel funciona
- [ ] Estilos personalizados aplicados

### 2. Verificar Imágenes

Navega por la aplicación y verifica:
- [ ] Logo del header visible
- [ ] Imágenes de beats/productos visibles
- [ ] Banners en inicio visibles
- [ ] No hay imágenes rotas (404)

### 3. Verificar Audio

Si hay reproductores de audio:
- [ ] Audio carga correctamente
- [ ] Controles de reproducción funcionan
- [ ] No hay errores 404 en consola

---

## 🎯 Resultado Esperado

✅ **Carpeta `frontend/src/assets/` completa**  
✅ **CSS: 4 archivos principales**  
✅ **Fuentes: Font Awesome completo**  
✅ **Imágenes: todas copiadas**  
✅ **Audio: todos los archivos**  
✅ **Aplicación se ve idéntica al proyecto original**  
✅ **No hay errores 404 en consola del navegador**

---

## 📊 Verificación de Tamaño

Verifica que el tamaño de la carpeta assets sea similar:

```powershell
# Tamaño de assets en proyecto original
cd c:\Users\WIN-D4MAG3\Documents\Repos\FullSound_React\src\assets
Get-ChildItem -Recurse | Measure-Object -Property Length -Sum

# Tamaño de assets en nuevo proyecto
cd c:\Users\WIN-D4MAG3\Documents\Repos\FULLSOUND-SPRINGBOOT\frontend\src\assets
Get-ChildItem -Recurse | Measure-Object -Property Length -Sum
```

Deberían ser similares (~55-110 MB).

---

## 🔄 Próximo Paso

➡️ **[07_CONFIGURACION_BUILD.md](07_CONFIGURACION_BUILD.md)** - Configurar vite.config.js y package.json
