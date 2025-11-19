# Payment Initiation Service

Microservicio REST para gestión de órdenes de pago, migrado desde un servicio SOAP legacy y alineado con los estándares BIAN (Banking Industry Architecture Network).

## 📋 Descripción del Proyecto

Este proyecto implementa el **Service Domain BIAN: Payment Initiation** con el **Behavior Qualifier: PaymentOrder**. Se trata de una migración completa de un servicio SOAP legacy a una API REST moderna y alineada con BIAN.

### Características Principales

- **Migración SOAP → REST**: Transformación completa desde servicio SOAP hacia API REST
- **Alineación BIAN**: Cumplimiento con estándares BIAN para Payment Initiation
- **Arquitectura Hexagonal**: Separación clara entre dominio de negocio y tecnologías
- **Contract-First**: Desarrollo basado en contrato OpenAPI 3.0
- **Domain-Driven Design (DDD)**: Modelo de dominio rico con value objects y agregados

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Ports & Adapters)

El proyecto sigue los principios de arquitectura hexagonal para mantener el dominio de negocio independiente de frameworks y tecnologías:

```
┌─────────────────────────────────────────────────────────┐
│                    Adapter In (REST)                    │
│  PaymentOrdersController → PaymentOrderRestMapper       │
└────────────────┬────────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────────┐
│              Application Layer                           │
│  InitiatePaymentOrderService                            │
│  RetrievePaymentOrderService                            │
│  RetrievePaymentOrderStatusService                      │
└────────────────┬────────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────────┐
│                  Domain Layer                            │
│  ├── model (PaymentOrder, PaymentAmount, etc.)          │
│  ├── port.in (Use Cases Interfaces)                     │
│  ├── port.out (Repository Interfaces)                   │
│  └── exception (Domain Exceptions)                      │
└────────────────┬────────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────────┐
│              Adapter Out (Persistence)                   │
│  PaymentOrderRepositoryAdapter →                        │
│  PaymentOrderPersistenceMapper →                        │
│  PaymentOrderJpaRepository                              │
└──────────────────────────────────────────────────────────┘
```

### Estructura de Paquetes

```
com.bank.paymentinitiation
├── domain                    # Capa de dominio (sin dependencias de frameworks)
│   ├── model                 # Agregados, value objects, enums
│   ├── port.in               # Interfaces de casos de uso
│   ├── port.out              # Interfaces de repositorios
│   ├── exception             # Excepciones de dominio
│   └── service               # Servicios de dominio (si aplica)
├── application               # Capa de aplicación
│   ├── service               # Implementaciones de casos de uso
│   └── mapper                # Mappers opcionales
├── adapter.in.rest           # Adaptador de entrada (REST)
│   ├── PaymentOrdersController
│   └── mapper                # Mappers REST ↔ Domain
├── adapter.out.persistence   # Adaptador de salida (JPA)
│   ├── entity                # Entidades JPA
│   ├── jpa                   # Repositorios JPA
│   ├── mapper                # Mappers Domain ↔ Entity
│   └── PaymentOrderRepositoryAdapter
├── config                    # Configuración de Spring
└── generated                 # Código generado por OpenAPI Generator
    ├── api                   # Interfaces de API
    └── model                 # DTOs de OpenAPI
```

## 🛠️ Stack Técnico

### Core
- **Java 21**: Última versión LTS de Java
- **Spring Boot 3.2.0**: Framework para microservicios
- **Spring MVC**: Framework web (no WebFlux)
- **Spring Data JPA**: Persistencia de datos

### Base de Datos
- **H2 Database**: Base de datos en memoria para desarrollo y testing

### Build & Dependencies
- **Gradle (Groovy DSL)**: Build automation tool
- **MapStruct 1.5.5**: Code generation para mapeos type-safe
- **Lombok 1.18.30**: Reducción de boilerplate code

### API & Contract
- **OpenAPI 3.0**: Especificación de contrato API
- **OpenAPI Generator 7.0.1**: Generación de código desde OpenAPI

### Testing
- **JUnit 5**: Framework de testing
- **AssertJ**: Fluent assertions
- **Mockito**: Mocking framework
- **WebTestClient**: Testing de integración (Spring MVC)

### Quality Gates
- **JaCoCo**: Code coverage (mínimo 75%)
- **Checkstyle**: Code style checking
- **SpotBugs**: Static analysis (nivel HIGH)

### Containerización
- **Docker**: Multi-stage Dockerfile
- **Docker Compose**: Orquestación de contenedores

## 🚀 Cómo Ejecutar

### Requisitos Previos

- Java 21 o superior
- Gradle 8.5 o superior (incluido en el proyecto via Gradle Wrapper)
- Docker y Docker Compose (opcional, para ejecución en contenedor)

### Ejecución Local

#### 1. Verificar y Ejecutar Quality Checks

```bash
./gradlew clean check
```

Este comando ejecuta:
- ✅ Tests unitarios e integración
- ✅ Checkstyle (verificación de estilo)
- ✅ SpotBugs (análisis estático)
- ✅ JaCoCo (verificación de cobertura ≥75%)

#### 2. Ejecutar la Aplicación

```bash
./gradlew bootRun
```

La aplicación estará disponible en:
- **API REST**: http://localhost:8080
- **Actuator Health**: http://localhost:8080/actuator/health
- **H2 Console**: http://localhost:8080/h2-console

#### 3. Ejecutar con Docker Compose

```bash
docker compose up --build
```

Este comando:
- Construye la imagen Docker (multi-stage build)
- Levanta el contenedor con el servicio
- Expone el puerto 8080

Para ejecutar en segundo plano:
```bash
docker compose up -d
```

Para ver los logs:
```bash
docker compose logs -f payment-initiation-service
```

Para detener:
```bash
docker compose down
```

## 🧪 Pruebas con Postman

El proyecto incluye una colección de Postman para validar los endpoints REST. La colección se encuentra en:

```
postman_collection.json
```

### Endpoints Disponibles

#### 1. POST /payment-initiation/payment-orders
Inicia una nueva orden de pago.

**Request Body:**
```json
{
  "externalReference": "EXT-1",
  "debtorAccount": { "iban": "EC12DEBTOR" },
  "creditorAccount": { "iban": "EC98CREDITOR" },
  "instructedAmount": { "amount": 150.75, "currency": "USD" },
  "remittanceInformation": "Factura 001-123",
  "requestedExecutionDate": "2025-10-31"
}
```

**Response:** `201 Created`

#### 2. GET /payment-initiation/payment-orders/{id}
Recupera una orden de pago completa por su referencia.

**Response:** `200 OK`

#### 3. GET /payment-initiation/payment-orders/{id}/status
Recupera solo el estado de una orden de pago.

**Response:** `200 OK`

### Importar Colección en Postman

1. Abre Postman
2. Click en **Import**
3. Selecciona el archivo `postman_collection.json`
4. La colección quedará disponible con todos los endpoints configurados

### Ejemplo de Uso

1. Ejecuta primero `POST Initiate PaymentOrder` para crear una orden
2. Copia el `paymentOrderReference` de la respuesta
3. Usa ese ID en `GET Retrieve PaymentOrder` y `GET Retrieve PaymentOrder Status`

## 📊 Quality Gates

### Cobertura de Código (JaCoCo)

- **Mínimo requerido**: 75%
- **Cobertura actual**: ~77%
- **Reporte HTML**: `build/reports/jacoco/html/index.html`

### Checkstyle

- **Configuración**: `config/checkstyle/checkstyle.xml`
- **Máximo de warnings**: 10
- **Reporte HTML**: `build/reports/checkstyle/`

### SpotBugs

- **Nivel de confianza**: HIGH (solo reporta problemas de alta confianza)
- **Effort**: MAX
- **Excluye**: Código generado, entidades JPA, mappers implementados
- **Reporte HTML**: `build/reports/spotbugs/`

## 🤖 Uso de IA

Este proyecto documenta el uso de herramientas de IA (principalmente Cursor Pro) durante su desarrollo. La documentación se encuentra en la carpeta `ai/`:

### `ai/prompts.md`
Lista todos los prompts utilizados con IA, incluyendo:
- Fecha y contexto de cada prompt
- Prompt completo enviado
- Respuesta resumida de la IA
- Resultado: qué se mantuvo, qué se modificó manualmente, y qué se descartó

### `ai/decisions.md`
Documenta decisiones arquitectónicas y técnicas, incluyendo:
- Contexto de cada decisión
- Opciones consideradas
- Decisión tomada y justificación
- Impacto en el proyecto
- Correcciones manuales sobre código generado por IA

### `ai/generations/`
Almacena fragmentos relevantes generados por IA, como:
- Especificaciones OpenAPI iniciales
- Ejemplos de controladores
- Cualquier fragmento significativo que requiera referencia futura

**Importante**: Todo el código generado por IA ha sido revisado, adaptado y probado antes de ser considerado producción-ready.

## 📁 Estructura del Proyecto

```
payment-initiation-service/
├── ai/                          # Documentación de uso de IA
│   ├── prompts.md              # Lista de prompts usados
│   ├── decisions.md            # Decisiones arquitectónicas
│   └── generations/            # Fragmentos generados por IA
├── config/                     # Configuración de herramientas
│   ├── checkstyle/            # Configuración Checkstyle
│   └── spotbugs/              # Configuración SpotBugs
├── openapi/                    # Especificación OpenAPI
│   └── openapi.yaml           # Contrato API (source of truth)
├── src/
│   ├── main/
│   │   ├── java/              # Código fuente Java
│   │   └── resources/
│   │       └── application.yml # Configuración Spring Boot
│   └── test/
│       └── java/              # Tests unitarios e integración
├── build.gradle               # Configuración Gradle
├── docker-compose.yml         # Configuración Docker Compose
├── Dockerfile                 # Dockerfile multi-stage
└── postman_collection.json    # Colección Postman para testing
```

## 🔍 Reportes

Después de ejecutar `./gradlew check`, los siguientes reportes estarán disponibles:

- **Tests**: `build/reports/tests/test/index.html`
- **JaCoCo**: `build/reports/jacoco/html/index.html`
- **Checkstyle**: `build/reports/checkstyle/main.html`
- **SpotBugs**: `build/reports/spotbugs/main.html`

## 📝 Notas Adicionales

### Base de Datos H2

La aplicación usa H2 en memoria por defecto. Todos los datos se pierden al reiniciar la aplicación.

**Configuración en `application.yml`:**
- URL: `jdbc:h2:mem:paymentdb`
- Console habilitada en: `/h2-console`
- Usuario: `sa`
- Password: (vacío)

### Endpoints del Actuator

Spring Actuator está habilitado y proporciona información sobre el estado de la aplicación:

- **Health**: `/actuator/health`
- **Info**: `/actuator/info`

### Código Generado

El código generado por OpenAPI Generator se encuentra en:
- `build/generated/src/main/java/com/bank/paymentinitiation/generated/`

Este código es generado automáticamente desde `openapi/openapi.yaml` y no debe modificarse manualmente.

## 👥 Contribución

Este proyecto sigue las mejores prácticas de arquitectura hexagonal, DDD y contract-first development. Cualquier contribución debe:

1. Mantener la separación de capas
2. No introducir dependencias de frameworks en el dominio
3. Incluir tests unitarios e integración
4. Pasar todos los quality gates
5. Documentar decisiones significativas en `ai/decisions.md`

## 📄 Licencia

[Especificar licencia si aplica]

---

**Última actualización**: Noviembre 2024

