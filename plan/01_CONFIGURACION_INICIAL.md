# 📦 PASO 1-3: Configuración Inicial del Proyecto

## 🎯 Objetivo
Configurar el proyecto Spring Boot con todas las dependencias necesarias y estructura base.

---

## ✅ PASO 1: Actualizar pom.xml

### Ubicación
```
Fullsound/pom.xml
```

### Código Completo

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>Fullsound</groupId>
    <artifactId>Fullsound</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>Fullsound</name>
    <description>Fullsound - Plataforma de venta de beats</description>
    
    <properties>
        <java.version>17</java.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <lombok.version>1.18.30</lombok.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.3</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <optional>true</optional>
        </dependency>

        <!-- MapStruct -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${mapstruct.version}</version>
        </dependency>

        <!-- OpenAPI/Swagger -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.3.0</version>
        </dependency>

        <!-- Stripe -->
        <dependency>
            <groupId>com.stripe</groupId>
            <artifactId>stripe-java</artifactId>
            <version>24.3.0</version>
        </dependency>

        <!-- Commons IO (para file upload) -->
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.15.1</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            
            <!-- Maven Compiler Plugin con soporte para Lombok y MapStruct -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>${mapstruct.version}</version>
                        </path>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok-mapstruct-binding</artifactId>
                            <version>0.2.0</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>

            <!-- Frontend Maven Plugin -->
            <plugin>
                <groupId>com.github.eirslett</groupId>
                <artifactId>frontend-maven-plugin</artifactId>
                <version>1.15.0</version>
                <configuration>
                    <workingDirectory>../frontend</workingDirectory>
                    <installDirectory>target</installDirectory>
                </configuration>
                <executions>
                    <execution>
                        <id>install node and npm</id>
                        <goals>
                            <goal>install-node-and-npm</goal>
                        </goals>
                        <configuration>
                            <nodeVersion>v20.10.0</nodeVersion>
                            <npmVersion>10.2.3</npmVersion>
                        </configuration>
                    </execution>
                    <execution>
                        <id>npm install</id>
                        <goals>
                            <goal>npm</goal>
                        </goals>
                        <configuration>
                            <arguments>install</arguments>
                        </configuration>
                    </execution>
                    <execution>
                        <id>npm run build</id>
                        <goals>
                            <goal>npm</goal>
                        </goals>
                        <configuration>
                            <arguments>run build</arguments>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- Maven Resources Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <version>3.3.1</version>
                <executions>
                    <execution>
                        <id>copy-frontend-build</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.directory}/classes/static</outputDirectory>
                            <resources>
                                <resource>
                                    <directory>../frontend/dist</directory>
                                    <filtering>false</filtering>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Verificación
```bash
cd Fullsound
mvn clean install -DskipTests
```

✅ **Criterio de aceptación:** BUILD SUCCESS

---

## ✅ PASO 2: Configurar application.properties

### application.properties (Desarrollo)

**Ubicación:** `Fullsound/src/main/resources/application.properties`

```properties
# ============================================
# APPLICATION CONFIGURATION
# ============================================
spring.application.name=fullsound
server.port=8080

# Active Profile
spring.profiles.active=dev

# ============================================
# DATABASE CONFIGURATION
# ============================================
spring.datasource.url=jdbc:mysql://localhost:3306/fullsound?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ============================================
# JPA / HIBERNATE CONFIGURATION
# ============================================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
spring.jpa.open-in-view=false

# ============================================
# JACKSON CONFIGURATION
# ============================================
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.time-zone=UTC
spring.jackson.default-property-inclusion=non_null

# ============================================
# FILE UPLOAD CONFIGURATION
# ============================================
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
spring.servlet.multipart.file-size-threshold=2KB

# Upload directories
app.upload.dir=uploads
app.upload.beats=${app.upload.dir}/beats
app.upload.images=${app.upload.dir}/images
app.upload.avatars=${app.upload.dir}/avatars

# ============================================
# STATIC RESOURCES
# ============================================
spring.web.resources.static-locations=classpath:/static/
spring.mvc.static-path-pattern=/**

# ============================================
# ACTUATOR CONFIGURATION
# ============================================
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true

# ============================================
# LOGGING CONFIGURATION
# ============================================
logging.level.root=INFO
logging.level.Fullsound.Fullsound=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# ============================================
# JWT CONFIGURATION
# ============================================
jwt.secret=fullsound-secret-key-2024-change-this-in-production-must-be-at-least-256-bits-long-for-hs256-algorithm
jwt.expiration=86400000
jwt.refresh-expiration=604800000

# ============================================
# STRIPE CONFIGURATION
# ============================================
stripe.api.key=sk_test_your_stripe_key_here
stripe.webhook.secret=whsec_your_webhook_secret_here

# ============================================
# CORS CONFIGURATION
# ============================================
app.cors.allowed-origins=http://localhost:5173,http://localhost:8080
app.cors.allowed-methods=GET,POST,PUT,DELETE,PATCH,OPTIONS
app.cors.allowed-headers=*
app.cors.allow-credentials=true

# ============================================
# API DOCUMENTATION
# ============================================
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha
```

### application-dev.properties

**Ubicación:** `Fullsound/src/main/resources/application-dev.properties`

```properties
# Development Profile
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
logging.level.Fullsound.Fullsound=DEBUG
```

### application-prod.properties

**Ubicación:** `Fullsound/src/main/resources/application-prod.properties`

```properties
# Production Profile
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Logging
logging.level.root=WARN
logging.level.Fullsound.Fullsound=INFO
logging.level.org.hibernate.SQL=WARN

# Database (usar variables de entorno)
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/fullsound}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}

# JWT
jwt.secret=${JWT_SECRET}

# Stripe
stripe.api.key=${STRIPE_API_KEY}
stripe.webhook.secret=${STRIPE_WEBHOOK_SECRET}
```

### Verificación
```bash
# Crear base de datos
mysql -u root -p
CREATE DATABASE IF NOT EXISTS fullsound;
exit;

# Ejecutar aplicación
mvn spring-boot:run
```

✅ **Criterio de aceptación:** Aplicación inicia sin errores

---

## ✅ PASO 3: Crear Estructura de Paquetes

### Comandos (Windows PowerShell)

```powershell
cd Fullsound\src\main\java\Fullsound\Fullsound

# Crear estructura de directorios
New-Item -ItemType Directory -Force -Path model\entity
New-Item -ItemType Directory -Force -Path model\dto\request
New-Item -ItemType Directory -Force -Path model\dto\response
New-Item -ItemType Directory -Force -Path model\enums
New-Item -ItemType Directory -Force -Path repository
New-Item -ItemType Directory -Force -Path service\interfaces
New-Item -ItemType Directory -Force -Path service\impl
New-Item -ItemType Directory -Force -Path service\mapper
New-Item -ItemType Directory -Force -Path security
New-Item -ItemType Directory -Force -Path exception
New-Item -ItemType Directory -Force -Path util
```

### Estructura Final

```
Fullsound/src/main/java/Fullsound/Fullsound/
├── config/
├── controller/
├── exception/
├── model/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   ├── entity/
│   └── enums/
├── repository/
├── security/
├── service/
│   ├── impl/
│   ├── interfaces/
│   └── mapper/
└── util/
```

### Verificación
```bash
# Listar estructura
tree /F src\main\java\Fullsound\Fullsound
```

✅ **Criterio de aceptación:** Todos los directorios creados

---

## 📋 Checklist PASO 1-3

- [ ] pom.xml actualizado con todas las dependencias
- [ ] application.properties configurado
- [ ] application-dev.properties creado
- [ ] application-prod.properties creado
- [ ] Base de datos MySQL creada
- [ ] Estructura de paquetes completa
- [ ] Aplicación compila: `mvn clean compile`
- [ ] Aplicación ejecuta: `mvn spring-boot:run`

---

## ⏭️ Siguiente Paso

Continuar con **[02_ENUMERACIONES.md](02_ENUMERACIONES.md)** - PASO 4
