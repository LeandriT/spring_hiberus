# Payment Initiation Service

Microservicio de Payment Initiation basado en el estándar BIAN (Banking Industry Architecture Network), migrado de SOAP a REST.

## Tecnologías

- Java 21
- Spring Boot 3.2.0
- Spring MVC (NO WebFlux)
- H2 Database
- Gradle (Groovy DSL)
- MapStruct
- Lombok
- Checkstyle
- SpotBugs
- JaCoCo

## Arquitectura

- Arquitectura Hexagonal (Ports & Adapters)
- Enfoque Contract-First con OpenAPI 3.0
- Principios SOLID y Clean Code

## Estructura del Proyecto

```
payment-initiation-service/
├── src/
│   ├── main/
│   │   ├── java/com/bank/paymentinitiation/
│   │   └── resources/
│   └── test/
├── config/
│   └── checkstyle/
└── build.gradle
```

## Compilación y Ejecución

### Compilar y ejecutar tests
```bash
./gradlew clean build
```

### Ejecutar la aplicación
```bash
./gradlew bootRun
```

### Ejecutar Checkstyle
```bash
./gradlew checkstyleMain --no-daemon
./gradlew checkstyleTest --no-daemon
```

### Ejecutar SpotBugs
```bash
./gradlew spotbugsMain
./gradlew spotbugsTest
```

### Generar reporte JaCoCo
```bash
./gradlew jacocoTestReport
```

## Configuración

- **Puerto**: 8080
- **Base de datos H2**: Memoria, console habilitada en `/h2-console`
- **Checkstyle**: Configuración en `config/checkstyle/checkstyle.xml`
- **SpotBugs**: Configuración en `build.gradle`

## Endpoints (a implementar)

- `POST /payment-initiation/payment-orders` - Iniciar orden de pago
- `GET /payment-initiation/payment-orders/{id}` - Recuperar orden de pago
- `GET /payment-initiation/payment-orders/{id}/status` - Obtener estado

## Documentación

- Documentación de decisiones: `../ai/decisions.md`
- Pasos de implementación: `../ai/steps.md`

