# Etapa 1: Compilar con Maven
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copiar archivos de configuración de Maven
COPY pom.xml .

# Descargar dependencias (esto se cachea)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación (incluyendo tests; si fallan, es señal de un problema)
RUN mvn clean package

# Etapa 2: Imagen final ligera
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR desde la etapa de compilación (usar comodín para capturar cualquier versión)
COPY --from=builder /build/target/*.jar app.jar

# Exponer puerto 8080
EXPOSE 8080

# Variables de entorno
ENV DB_PASSWORD=${DB_PASSWORD}
ENV JWT_SECRET=${JWT_SECRET}
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
