# Stage 1: Builder - Compila la aplicación
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY gradle/ gradle/
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .

# Copiar código fuente
COPY src/ src/
COPY openapi/ openapi/
COPY config/ config/

# Dar permisos de ejecución a gradlew
RUN chmod +x gradlew

# Compilar la aplicación (sin tests para acelerar el build)
RUN ./gradlew clean build -x test --no-daemon

# Verificar que se generó el JAR
RUN ls -la build/libs/

# Stage 2: Runtime - Imagen final de ejecución
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Instalar wget para health checks
RUN apk add --no-cache wget

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el JAR desde el stage builder
# El JAR ejecutable de Spring Boot tiene el nombre: payment-initiation-service-0.0.1-SNAPSHOT.jar
COPY --from=builder /app/build/libs/payment-initiation-service-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto 8080
EXPOSE 8080

# Health check usando actuator
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

