# Payment Initiation Service

Microservicio REST para la gestión de órdenes de pago, alineado con el estándar BIAN (Banking Industry Architecture Network) Service Domain "Payment Initiation".

## Descripción

Este proyecto migra un servicio SOAP legado de órdenes de pago a una arquitectura REST moderna basada en:
- **Spring Boot 3.2.0** con **Java 21**
- **Arquitectura Hexagonal** (Ports & Adapters)
- **Contract-First** con OpenAPI 3.0
- **Gradle** (Groovy DSL) como herramienta de build

## Requisitos

- Java 21 o superior
- Gradle 8.5 (incluido en el proyecto mediante Gradle Wrapper)

## Estructura del Proyecto

```
payment-initiation-service/
├── src/
│   ├── main/
│   │   ├── java/com/bank/paymentinitiation/
│   │   └── resources/
│   └── test/
├── config/
│   ├── checkstyle/
│   └── spotbugs/
├── build.gradle
├── settings.gradle
└── gradlew
```

## Compilación y Ejecución

### Compilar el proyecto

```bash
./gradlew clean build
```

### Ejecutar la aplicación

```bash
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8080`

### Ejecutar tests

```bash
./gradlew test
```

### Verificar calidad de código

```bash
# Checkstyle
./gradlew checkstyleMain

# SpotBugs
./gradlew spotbugsMain

# Cobertura de código (JaCoCo)
./gradlew jacocoTestReport
```

## Configuración

La configuración principal se encuentra en `src/main/resources/application.yml`:

- **Puerto**: 8080
- **Base de datos**: H2 en memoria
- **Consola H2**: Disponible en `/h2-console`

## Herramientas de Calidad

### Checkstyle

Configuración en `config/checkstyle/checkstyle.xml`:
- Longitud de línea máxima: 120 caracteres
- Complejidad ciclomática máxima: 15
- Reglas de naming estándar de Java
- Exclusiones para código generado

### SpotBugs

Configuración básica con exclusiones para código generado en `config/spotbugs/exclude.xml`.

### JaCoCo

Configuración para reportes de cobertura de código (objetivo: ≥ 80%).

## Documentación de IA

Este proyecto utiliza asistencia de IA para el desarrollo. La documentación se encuentra en:

- `ai/prompts.md`: Prompts utilizados y respuestas generadas
- `ai/decisions.md`: Decisiones arquitectónicas y correcciones manuales aplicadas

## Estado del Proyecto

**Fase actual**: Configuración inicial del proyecto
- ✅ Estructura del proyecto creada
- ✅ Build.gradle configurado con todas las dependencias
- ✅ Checkstyle configurado y funcionando
- ✅ SpotBugs configurado (básico)
- ✅ JaCoCo configurado
- ⏳ Próximo: Diseño del contrato OpenAPI

## Licencia

Este proyecto es parte de un ejercicio técnico de migración SOAP a REST.

