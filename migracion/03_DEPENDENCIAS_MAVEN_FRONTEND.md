# 📦 DEPENDENCIAS MAVEN - FRONTEND ONLY (ACTUALIZADO)

## 🎯 Objetivo ACTUALIZADO

Configurar el `pom.xml` **MINIMALISTA** para:
1. ✅ Servir frontend React como recursos estáticos
2. ✅ Build automático del frontend con frontend-maven-plugin
3. ✅ CORS configurado para APIs en AWS
4. ✅ Health check endpoint para AWS Load Balancer
5. ❌ **SIN backend** (sin JPA, sin Security, sin JWT, sin MySQL)

---

## 📄 pom.xml Completo (SIMPLIFICADO)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <!-- Parent Spring Boot -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.7</version>
        <relativePath/>
    </parent>
    
    <!-- Identificación del proyecto -->
    <groupId>com.fullsound</groupId>
    <artifactId>fullsound-frontend</artifactId>
    <version>2.0.0</version>
    <name>FullSound Frontend Server</name>
    <description>Frontend React servido por Spring Boot (AWS Ready)</description>
    <packaging>jar</packaging>
    
    <!-- Propiedades -->
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        
        <!-- Frontend configuration -->
        <frontend.directory>${project.basedir}/../../FullSound_React</frontend.directory>
        <node.version>v20.11.0</node.version>
        <npm.version>10.2.4</npm.version>
    </properties>
    
    <!-- Dependencias MINIMALISTAS (solo lo necesario) -->
    <dependencies>
        <!-- Spring Boot Web Starter (para servir archivos estáticos) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Spring Boot DevTools (hot reload en desarrollo) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        
        <!-- Spring Boot Actuator (para health checks de AWS) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <!-- Build Configuration -->
    <build>
        <finalName>fullsound-frontend</finalName>
        
        <plugins>
            <!-- Spring Boot Maven Plugin -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <mainClass>Fullsound.Fullsound.FullsoundApplication</mainClass>
                </configuration>
            </plugin>
            
            <!-- Frontend Maven Plugin (para compilar React) -->
            <plugin>
                <groupId>com.github.eirslett</groupId>
                <artifactId>frontend-maven-plugin</artifactId>
                <version>1.15.0</version>
                
                <configuration>
                    <workingDirectory>${frontend.directory}</workingDirectory>
                    <installDirectory>${project.build.directory}</installDirectory>
                    <nodeVersion>${node.version}</nodeVersion>
                    <npmVersion>${npm.version}</npmVersion>
                </configuration>
                
                <executions>
                    <!-- Instalar Node y NPM -->
                    <execution>
                        <id>install-node-and-npm</id>
                        <goals>
                            <goal>install-node-and-npm</goal>
                        </goals>
                        <phase>generate-resources</phase>
                    </execution>
                    
                    <!-- NPM Install -->
                    <execution>
                        <id>npm-install</id>
                        <goals>
                            <goal>npm</goal>
                        </goals>
                        <phase>generate-resources</phase>
                        <configuration>
                            <arguments>install</arguments>
                        </configuration>
                    </execution>
                    
                    <!-- NPM Build (producción) -->
                    <execution>
                        <id>npm-build</id>
                        <goals>
                            <goal>npm</goal>
                        </goals>
                        <phase>generate-resources</phase>
                        <configuration>
                            <arguments>run build</arguments>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            
            <!-- Maven Resources Plugin (copiar archivos React al JAR) -->
            <plugin>
                <artifactId>maven-resources-plugin</artifactId>
                <version>3.3.1</version>
                <executions>
                    <execution>
                        <id>copy-react-build</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.outputDirectory}/static</outputDirectory>
                            <resources>
                                <resource>
                                    <directory>${frontend.directory}/dist</directory>
                                    <filtering>false</filtering>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            
            <!-- Maven Clean Plugin (limpiar archivos generados) -->
            <plugin>
                <artifactId>maven-clean-plugin</artifactId>
                <version>3.3.2</version>
                <configuration>
                    <filesets>
                        <fileset>
                            <directory>${frontend.directory}/dist</directory>
                        </fileset>
                        <fileset>
                            <directory>${frontend.directory}/node_modules</directory>
                        </fileset>
                    </filesets>
                </configuration>
            </plugin>
        </plugins>
    </build>
    
    <!-- Profiles para diferentes entornos -->
    <profiles>
        <!-- Perfil de Desarrollo (sin compilar frontend) -->
        <profile>
            <id>dev</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <spring.profiles.active>development</spring.profiles.active>
            </properties>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.github.eirslett</groupId>
                        <artifactId>frontend-maven-plugin</artifactId>
                        <executions>
                            <!-- Solo npm install, no build -->
                            <execution>
                                <id>npm-build</id>
                                <phase>none</phase>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
        
        <!-- Perfil de Producción (compilar frontend) -->
        <profile>
            <id>prod</id>
            <properties>
                <spring.profiles.active>production</spring.profiles.active>
            </properties>
            <!-- Usa todas las configuraciones de build por defecto -->
        </profile>
        
        <!-- Perfil Skip Frontend (solo backend) -->
        <profile>
            <id>skip-frontend</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.github.eirslett</groupId>
                        <artifactId>frontend-maven-plugin</artifactId>
                        <executions>
                            <execution>
                                <id>install-node-and-npm</id>
                                <phase>none</phase>
                            </execution>
                            <execution>
                                <id>npm-install</id>
                                <phase>none</phase>
                            </execution>
                            <execution>
                                <id>npm-build</id>
                                <phase>none</phase>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
    
</project>
```

---

## 🔍 Explicación de Componentes

### 📦 Dependencias

| Dependencia | Propósito | Necesaria |
|-------------|-----------|-----------|
| `spring-boot-starter-web` | Servidor web + recursos estáticos | ✅ Sí |
| `spring-boot-devtools` | Hot reload en desarrollo | ⚠️ Opcional |
| `spring-boot-starter-actuator` | Health checks para AWS | ✅ Sí |
| `spring-boot-starter-test` | Testing | ⚠️ Opcional |

### 🔌 Plugins

| Plugin | Propósito | Fase |
|--------|-----------|------|
| `spring-boot-maven-plugin` | Crear JAR ejecutable | package |
| `frontend-maven-plugin` | Compilar React | generate-resources |
| `maven-resources-plugin` | Copiar dist/ a static/ | process-resources |
| `maven-clean-plugin` | Limpiar node_modules y dist | clean |

### 🎭 Profiles

| Profile | Uso | Comando |
|---------|-----|---------|
| `dev` | Desarrollo (sin build de React) | `mvn spring-boot:run` |
| `prod` | Producción (con build completo) | `mvn clean package -P prod` |
| `skip-frontend` | Solo backend (sin React) | `mvn clean package -P skip-frontend` |

---

## ⚙️ Variables de Configuración

### Modificables en `pom.xml`

```xml
<properties>
    <!-- Versión de Java -->
    <java.version>17</java.version>
    
    <!-- Directorio del frontend React -->
    <frontend.directory>${project.basedir}/../../FullSound_React</frontend.directory>
    
    <!-- Versiones de Node y NPM -->
    <node.version>v20.11.0</node.version>
    <npm.version>10.2.4</npm.version>
</properties>
```

---

## 🚀 Comandos de Build

### 1. Desarrollo (solo backend, sin compilar React)

```powershell
mvn spring-boot:run
```

### 2. Build completo para producción

```powershell
mvn clean package -P prod
```

Genera: `target/fullsound-frontend.jar`

### 3. Ejecutar JAR

```powershell
java -jar target/fullsound-frontend.jar
```

### 4. Build solo backend (sin frontend)

```powershell
mvn clean package -P skip-frontend
```

### 5. Limpiar todo

```powershell
mvn clean
```

---

## 📊 Tamaño Esperado del JAR

| Componente | Tamaño |
|------------|--------|
| Spring Boot + Tomcat | ~20 MB |
| Frontend React compilado | ~5-10 MB |
| Assets (CSS, imágenes, audio) | ~50-100 MB |
| **TOTAL** | **~75-130 MB** |

---

## 🔗 Integración con AWS

### Variables de Entorno para AWS

En AWS Elastic Beanstalk o ECS, configurar:

```bash
SPRING_PROFILES_ACTIVE=production
VITE_API_URL=https://api.fullsound.com/api
SERVER_PORT=8080
```

### Health Check URL para Load Balancer

```
/actuator/health
```

---

## ✅ Checklist de Configuración

- [ ] `pom.xml` copiado y adaptado
- [ ] Versión de Java correcta (17)
- [ ] `frontend.directory` apunta a FullSound_React
- [ ] Node version v20.11.0 o superior
- [ ] NPM version 10.2.4 o superior
- [ ] Profiles configurados (dev, prod, skip-frontend)
- [ ] frontend-maven-plugin correctamente configurado
- [ ] maven-resources-plugin copia dist/ a static/
- [ ] Spring Boot Actuator incluido para health checks
- [ ] CORS configuration lista (ver 08_AWS_PREPARACION.md)

---

## 🎯 Resultado Esperado

✅ **pom.xml minimalista**  
✅ **Solo dependencias para servir frontend**  
✅ **Build automático de React con Maven**  
✅ **JAR listo para desplegar en AWS**  
✅ **Health checks para Load Balancer**  
✅ **Sin backend local** (APIs apuntarán a AWS)

---

## 📝 Notas Importantes

### ⚠️ Lo que NO incluye este pom.xml:

- ❌ Spring Data JPA
- ❌ Spring Security
- ❌ JWT dependencies
- ❌ MySQL driver
- ❌ Lombok
- ❌ Validation

### ✅ Lo que SÍ incluye:

- ✅ Spring Boot Web (servidor estático)
- ✅ Spring Boot Actuator (health checks)
- ✅ frontend-maven-plugin (build de React)
- ✅ Configuración CORS (manual en código)

### 🔮 Cuando integres el backend AWS:

1. El frontend seguirá sirviendo desde Spring Boot
2. Las APIs llamarán a AWS via `VITE_API_URL`
3. CORS en Spring Boot permitirá requests de cualquier origen
4. No necesitas modificar este pom.xml (a menos que quieras agregar backend)

---

## 🔄 Próximos Pasos

1. ✅ Copiar este pom.xml a `FULLSOUND-SPRINGBOOT/Fullsound/`
2. ➡️ Configurar `application.yml` (ver siguiente fase)
3. ➡️ Crear configuración CORS (ver 08_AWS_PREPARACION.md)
4. ➡️ Crear HealthController (ver 08_AWS_PREPARACION.md)
5. ➡️ Build de prueba: `mvn clean package -P prod`
