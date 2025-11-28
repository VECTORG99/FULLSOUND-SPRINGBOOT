# ========================================
# FULLSOUND BACKEND - Dockerfile
# ========================================
# Multi-stage build para optimizar tamaño

# ========================================
# STAGE 1: Build con Maven
# ========================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Metadatos
LABEL maintainer="VECTORG99"
LABEL description="FullSound Backend API - Build Stage"

# Directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (cacheado si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación (saltando tests para build más rápido)
RUN mvn clean package -DskipTests

# ========================================
# STAGE 2: Runtime con JRE
# ========================================
FROM eclipse-temurin:17-jre-alpine

# Metadatos
LABEL maintainer="VECTORG99"
LABEL description="FullSound Backend API - Runtime"
LABEL version="1.0.0"

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8080
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring

# Directorio de trabajo
WORKDIR /app

# Copiar JAR desde stage builder
COPY --from=builder /app/target/*.jar app.jar

# Crear directorio para uploads
RUN mkdir -p /app/uploads/beats /app/uploads/images && \
    chown -R spring:spring /app

# Cambiar a usuario no-root
USER spring:spring

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/auth/health || exit 1

# Comando de ejecución
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
