# Stage 1: Builder
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Gradle wrapper y configuración
COPY gradle/ ./gradle/
COPY gradlew ./
COPY build.gradle ./
COPY settings.gradle ./
RUN chmod +x gradlew

# Código fuente y demás recursos
COPY src/ ./src/
COPY openapi/ ./openapi/
COPY config/ ./config/

# Build del proyecto (sin tests)
RUN ./gradlew clean build -x test --no-daemon


# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy

# Instalar wget para el HEALTHCHECK
RUN apt-get update \
    && apt-get install -y wget \
    && rm -rf /var/lib/apt/lists/*

# Crear usuario 'spring'
RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app

# Copiar el jar generado
COPY --from=builder /app/build/libs/payment-initiation-service-0.0.1-SNAPSHOT.jar app.jar
RUN chown spring:spring app.jar

USER spring:spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]