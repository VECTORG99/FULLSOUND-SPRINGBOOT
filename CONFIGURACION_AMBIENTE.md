# ⚙️ CONFIGURACIÓN DEL AMBIENTE DE DESARROLLO

## 📋 REQUISITOS PREVIOS

Para compilar y ejecutar el backend de FullSound, necesitas instalar:

### **1. Java Development Kit (JDK) 17**

#### **Verificar si está instalado:**

```powershell
java -version
```

Debe mostrar algo como: `java version "17.0.x"`

#### **Instalar JDK 17:**

**Opción A: Eclipse Temurin (Recomendado)**

1. Descarga desde: https://adoptium.net/
2. Selecciona: JDK 17 (LTS) para Windows x64
3. Instala con las opciones por defecto
4. Marca la opción: "Add to PATH"

**Opción B: Oracle JDK**

1. Descarga desde: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
2. Instala y configura la variable de entorno JAVA_HOME

#### **Configurar JAVA_HOME (si es necesario):**

```powershell
# Abrir PowerShell como administrador
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot", "Machine")

# Agregar a PATH
$path = [System.Environment]::GetEnvironmentVariable("PATH", "Machine")
[System.Environment]::SetEnvironmentVariable("PATH", "$path;%JAVA_HOME%\bin", "Machine")

# Reiniciar PowerShell y verificar
java -version
```

---

### **2. Apache Maven 3.8+**

#### **Verificar si está instalado:**

```powershell
mvn -version
```

#### **Instalar Maven:**

**Método 1: Chocolatey (Más fácil)**

```powershell
# Instalar Chocolatey si no lo tienes
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Instalar Maven
choco install maven -y

# Verificar
mvn -version
```

**Método 2: Manual**

1. Descarga desde: https://maven.apache.org/download.cgi
2. Descarga: `apache-maven-3.9.x-bin.zip`
3. Extrae en: `C:\Program Files\Apache\Maven\apache-maven-3.9.x`
4. Configura variables de entorno:

```powershell
# Como administrador
[System.Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\Program Files\Apache\Maven\apache-maven-3.9.x", "Machine")

$path = [System.Environment]::GetEnvironmentVariable("PATH", "Machine")
[System.Environment]::SetEnvironmentVariable("PATH", "$path;%MAVEN_HOME%\bin", "Machine")

# Reiniciar PowerShell
mvn -version
```

---

### **3. MySQL 8.0**

#### **Verificar si está instalado:**

```powershell
mysql --version
```

#### **Instalar MySQL:**

**Opción A: MySQL Installer**

1. Descarga desde: https://dev.mysql.com/downloads/installer/
2. Ejecuta: `mysql-installer-community-8.0.x.msi`
3. Selecciona: "Developer Default"
4. Configura contraseña de root (o déjala vacía si ya lo hiciste)
5. Marca: "Add MySQL to PATH"

**Opción B: Chocolatey**

```powershell
choco install mysql -y
```

#### **Configurar MySQL:**

```powershell
# Iniciar servicio
net start MySQL80

# Conectar
mysql -u root -p

# Crear base de datos
CREATE DATABASE Fullsound_Base CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

---

### **4. Git (Opcional, pero recomendado)**

```powershell
# Con Chocolatey
choco install git -y

# O descargar desde: https://git-scm.com/download/win
```

---

## 🔧 CONFIGURAR MAVEN WRAPPER (Opcional)

Si prefieres usar el Maven Wrapper en lugar de instalar Maven globalmente:

```powershell
cd c:\Users\dh893\Documents\GitHub\FULLSOUND-SPRINGBOOT\Fullsound

# Instalar wrapper
mvn -N wrapper:wrapper

# Ahora puedes usar
.\mvnw.cmd clean install
```

---

## ✅ VERIFICACIÓN COMPLETA

Ejecuta todos estos comandos y verifica que funcionen:

```powershell
# Java
java -version
# Debe mostrar: java version "17.0.x"

# Maven
mvn -version
# Debe mostrar: Apache Maven 3.x.x

# MySQL
mysql --version
# Debe mostrar: mysql Ver 8.0.x

# Git (opcional)
git --version
# Debe mostrar: git version 2.x.x
```

---

## 🚀 COMPILAR EL PROYECTO

Una vez instalado todo:

```powershell
# Navegar al proyecto
cd c:\Users\dh893\Documents\GitHub\FULLSOUND-SPRINGBOOT\Fullsound

# Limpiar
mvn clean

# Compilar (sin tests)
mvn compile -DskipTests

# Compilar y empaquetar (con tests)
mvn clean install

# Ejecutar
mvn spring-boot:run
```

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### **Error: "JAVA_HOME not set"**

```powershell
# Verificar JAVA_HOME
$env:JAVA_HOME

# Si está vacío, configurar:
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot", "Machine")
```

### **Error: "mvn command not found"**

```powershell
# Verificar PATH
$env:PATH

# Agregar Maven al PATH
$path = [System.Environment]::GetEnvironmentVariable("PATH", "Machine")
[System.Environment]::SetEnvironmentVariable("PATH", "$path;C:\Program Files\Apache\Maven\apache-maven-3.9.x\bin", "Machine")

# Reiniciar PowerShell
```

### **Error: "Cannot connect to MySQL"**

```powershell
# Verificar que el servicio está corriendo
Get-Service -Name MySQL80

# Si está detenido, iniciar
Start-Service MySQL80

# O con net
net start MySQL80
```

### **Error de permisos en PowerShell**

```powershell
# Ejecutar como administrador
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
```

---

## 📦 ALTERNATIVA: USAR DOCKER (Avanzado)

Si prefieres no instalar nada localmente:

```yaml
# docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: Fullsound_Base
    ports:
      - "3306:3306"
  
  backend:
    build: ./Fullsound
    ports:
      - "8080:8080"
    depends_on:
      - mysql
```

```powershell
# Ejecutar
docker-compose up
```

---

## 📝 NOTAS IMPORTANTES

1. **Java 17 es obligatorio** - Java 11 o 21 no funcionarán correctamente
2. **Maven 3.8+** es necesario para las dependencias modernas
3. **MySQL 8.0** es la versión probada (5.7 podría funcionar pero no está garantizado)
4. **Reinicia PowerShell** después de instalar cada herramienta
5. **Ejecuta como administrador** cuando configures variables de entorno

---

## ✨ SIGUIENTES PASOS

Una vez que hayas verificado que todo funciona:

1. Ejecutar el script de migración de base de datos:
   ```powershell
   mysql -u root -p Fullsound_Base < plan/DATABASE_MIGRATION.sql
   ```

2. Compilar el proyecto:
   ```powershell
   mvn clean install
   ```

3. Ejecutar el backend:
   ```powershell
   mvn spring-boot:run
   ```

4. Verificar en: http://localhost:8080/api/auth/health

---

**¡Listo para desarrollar! 🎉**
