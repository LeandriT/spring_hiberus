# Payment Initiation Service

Microservicio REST para la gestión de órdenes de pago, alineado con el estándar **BIAN (Banking Industry Architecture Network)** Service Domain **"Payment Initiation"** con Behavior Qualifier **"PaymentOrder"**.

## 📋 Descripción del Proyecto

Este proyecto migra un servicio SOAP legado de órdenes de pago (`PaymentOrderService`) a una arquitectura REST moderna siguiendo estándares BIAN. El servicio permite:

- **Iniciar órdenes de pago** (POST `/payment-initiation/payment-orders`)
- **Recuperar órdenes de pago** (GET `/payment-initiation/payment-orders/{id}`)
- **Consultar estado de órdenes** (GET `/payment-initiation/payment-orders/{id}/status`)

### Migración SOAP → REST

El servicio legacy expone las siguientes operaciones SOAP:
- `SubmitPaymentOrder` → **Initiate** (POST `/payment-initiation/payment-orders`)
- `GetPaymentOrderStatus` → **Retrieve Status** (GET `/payment-initiation/payment-orders/{id}/status`)

Adicionalmente, se ha agregado la operación **Retrieve** (GET `/payment-initiation/payment-orders/{id}`) para cumplir con estándares BIAN completos.

---

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Ports & Adapters)

El proyecto sigue una arquitectura hexagonal que separa claramente la lógica de negocio (dominio) de los detalles de implementación (infraestructura):

```
com.bank.paymentinitiation
├── domain/                          # Capa de Dominio (Framework-agnostic)
│   ├── model/                       # Entidades de dominio, Value Objects, Enums
│   │   ├── PaymentOrder.java       # Aggregate Root
│   │   ├── PaymentAmount.java      # Value Object
│   │   ├── PaymentStatus.java      # Enum
│   │   └── ...
│   ├── port/
│   │   ├── in/                     # Puertos de Entrada (Use Cases)
│   │   │   ├── InitiatePaymentOrderUseCase.java
│   │   │   ├── RetrievePaymentOrderUseCase.java
│   │   │   └── RetrievePaymentOrderStatusUseCase.java
│   │   └── out/                    # Puertos de Salida (Repositories)
│   │       └── PaymentOrderRepository.java
│   ├── exception/                  # Excepciones de Dominio
│   │   ├── PaymentOrderNotFoundException.java
│   │   └── InvalidPaymentException.java
│   └── service/                    # Servicios de Dominio
│       └── PaymentOrderDomainService.java
│
├── application/                     # Capa de Aplicación (Orquestación)
│   └── service/                    # Implementación de Casos de Uso
│       ├── InitiatePaymentOrderService.java
│       ├── RetrievePaymentOrderService.java
│       ├── RetrievePaymentOrderStatusService.java
│       └── PaymentOrderReferenceGenerator.java
│
├── adapter/                         # Adaptadores (Infraestructura)
│   ├── in/
│   │   └── rest/                   # Adaptador REST (Entrada)
│   │       ├── PaymentOrdersController.java
│   │       ├── GlobalExceptionHandler.java
│   │       └── mapper/
│   │           └── PaymentOrderRestMapper.java  # MapStruct: DTO ↔ Domain
│   └── out/
│       └── persistence/            # Adaptador de Persistencia (Salida)
│           ├── PaymentOrderRepositoryAdapter.java
│           ├── entity/
│           │   └── PaymentOrderEntity.java      # Entidad JPA
│           ├── jpa/
│           │   └── PaymentOrderJpaRepository.java
│           └── mapper/
│               └── PaymentOrderPersistenceMapper.java  # MapStruct: Entity ↔ Domain
│
├── config/                          # Configuración Spring
│   └── ApplicationConfig.java
│
└── generated/                       # Código Generado por OpenAPI
    ├── api/
    │   └── PaymentOrdersApi.java
    └── model/
        ├── InitiatePaymentOrderRequest.java
        ├── InitiatePaymentOrderResponse.java
        └── ...
```

### Principios Arquitectónicos

- **Separación de Responsabilidades**: Cada capa tiene una responsabilidad clara
- **Independencia del Dominio**: El dominio no depende de frameworks (Spring, JPA, etc.)
- **Contract-First**: El contrato OpenAPI es la fuente de verdad
- **Dependency Inversion**: Las capas internas definen interfaces, las externas las implementan

---

## 🛠️ Stack Técnico

### Runtime y Framework
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.2.0** - Framework de aplicación
  - Spring Web MVC (no reactive)
  - Spring Data JPA
  - Spring Validation
  - Spring Actuator

### Base de Datos
- **H2 Database** - Base de datos en memoria para desarrollo y testing

### Herramientas de Build
- **Gradle 8.5** (Groovy DSL) - Herramienta de build
- **OpenAPI Generator 7.0.1** - Generación de código desde contrato OpenAPI 3.0

### Librerías
- **MapStruct 1.5.5.Final** - Mapeo entre capas (DTO ↔ Domain ↔ Entity)
- **Lombok 1.18.30** - Reducción de boilerplate
- **Bean Validation (Jakarta)** - Validación de datos

### Testing
- **JUnit 5** - Framework de testing
- **Mockito** - Mocking para tests unitarios
- **AssertJ** - Aserciones fluidas
- **WebTestClient** - Testing de endpoints REST
- **Spring Boot Test** - Testing de integración

### Quality Gates
- **JaCoCo 0.8.11** - Cobertura de código (objetivo: ≥ 85%)
- **Checkstyle 10.12.5** - Análisis de estilo de código (maxWarnings: 10)
- **SpotBugs 6.0.0** - Detección de bugs potenciales (nivel: HIGH)

### Contrato API
- **OpenAPI 3.0.3** - Especificación del contrato REST
- **RFC 7807** - Manejo de errores (Problem Details for HTTP APIs)

### DevOps (Futuro)
- **Docker** - Containerización del servicio
- **Docker Compose** - Orquestación local

---

## 🚀 Cómo Ejecutar

### Prerrequisitos

- **Java 21** o superior
- **Gradle 8.5** (incluido mediante Gradle Wrapper)

### Verificar Calidad del Código

Ejecuta todos los quality gates (tests, Checkstyle, SpotBugs, JaCoCo):

```bash
./gradlew clean check
```

Este comando ejecuta:
1. ✅ Tests unitarios e integración
2. ✅ Checkstyle (análisis de estilo de código)
3. ✅ SpotBugs (detección de bugs)
4. ✅ Verificación de cobertura JaCoCo (≥ 85%)
5. ✅ Generación de reportes HTML

**Reportes generados**:
- Tests: `build/reports/tests/test/index.html`
- JaCoCo: `build/reports/jacoco/test/html/index.html`
- Checkstyle: `build/reports/checkstyle/main.html`
- SpotBugs: `build/reports/spotbugs/main/spotbugs.html`

### Ejecutar la Aplicación Localmente

```bash
./gradlew bootRun
```

La aplicación estará disponible en:
- **API REST**: `http://localhost:8080`
- **H2 Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:paymentdb`
  - Username: `sa`
  - Password: *(vacío)*
- **Health Check**: `http://localhost:8080/actuator/health`

### Ejecutar con Docker (Futuro)

```bash
docker compose up --build
```

> **Nota**: Los archivos Docker (Dockerfile, docker-compose.yml) se configurarán en una fase posterior del proyecto.

### Comandos Gradle Útiles

```bash
# Compilar sin ejecutar tests
./gradlew clean build -x test

# Ejecutar solo tests
./gradlew test

# Generar reporte de cobertura
./gradlew jacocoTestReport

# Verificar cobertura (falla si < 85%)
./gradlew jacocoTestCoverageVerification

# Ejecutar Checkstyle
./gradlew checkstyleMain checkstyleTest

# Ejecutar SpotBugs
./gradlew spotbugsMain spotbugsTest

# Generar código desde OpenAPI
./gradlew openApiGenerate
```

---

## 🧪 Cómo Probar con Postman

El proyecto incluye una colección Postman para probar todos los endpoints. La colección está disponible en:

📁 **`postman_collection.json`** (en la raíz del proyecto workspace)

### Endpoints Disponibles

#### 1. Iniciar Orden de Pago

**POST** `http://localhost:8080/payment-initiation/payment-orders`

**Request Body** (ejemplo):
```json
{
  "externalReference": "EXT-1",
  "debtorAccount": {
    "iban": "EC123456789012345678"
  },
  "creditorAccount": {
    "iban": "EC987654321098765432"
  },
  "instructedAmount": {
    "amount": 150.75,
    "currency": "USD"
  },
  "remittanceInformation": "Factura 001-123",
  "requestedExecutionDate": "2025-10-31"
}
```

**Response** (201 Created):
```json
{
  "paymentOrderReference": "PO-A1B2C3D4E5F6G7H8...",
  "paymentOrderStatus": "INITIATED",
  "payerReference": "EC123456789012345678",
  "payeeReference": "EC987654321098765432",
  "amount": {
    "value": 150.75,
    "currency": "USD"
  },
  "createdAt": "2025-01-27T10:15:30Z"
}
```

#### 2. Recuperar Orden de Pago

**GET** `http://localhost:8080/payment-initiation/payment-orders/{paymentOrderReference}`

**Response** (200 OK):
```json
{
  "paymentOrderReference": "PO-A1B2C3D4E5F6G7H8...",
  "paymentOrderStatus": "INITIATED",
  "payerReference": "EC123456789012345678",
  "payeeReference": "EC987654321098765432",
  "amount": {
    "value": 150.75,
    "currency": "USD"
  },
  "remittanceInformation": "Factura 001-123",
  "requestedExecutionDate": "2025-10-31",
  "createdAt": "2025-01-27T10:15:30Z",
  "updatedAt": "2025-01-27T10:15:30Z"
}
```

#### 3. Consultar Estado de Orden

**GET** `http://localhost:8080/payment-initiation/payment-orders/{paymentOrderReference}/status`

**Response** (200 OK):
```json
{
  "paymentOrderReference": "PO-A1B2C3D4E5F6G7H8...",
  "paymentOrderStatus": "INITIATED",
  "lastUpdated": "2025-01-27T10:15:30Z"
}
```

### Códigos de Estado HTTP

- **201 Created**: Orden creada exitosamente
- **200 OK**: Operación exitosa
- **400 Bad Request**: Error de validación o datos inválidos
- **404 Not Found**: Orden no encontrada
- **500 Internal Server Error**: Error interno del servidor

Todos los errores siguen **RFC 7807** (Problem Details) con content-type `application/problem+json`.

### Importar Colección en Postman

1. Abre Postman
2. Click en **Import**
3. Selecciona el archivo `postman_collection.json`
4. Los 3 endpoints estarán disponibles para probar

---

## 📚 Uso de IA

Este proyecto ha utilizado asistencia de IA (Cursor/ChatGPT) durante su desarrollo. Toda la documentación relacionada se encuentra en la carpeta **`ai/`**.

### Estructura de Documentación IA

```
ai/
├── prompts.md                    # Registro de todos los prompts utilizados
│                                  # Incluye: prompt, respuesta resumida, correcciones manuales
│
├── decisions.md                  # Decisiones arquitectónicas y correcciones manuales
│                                  # Incluye: decisiones técnicas, trade-offs, justificaciones
│
└── generations/                  # Fragmentos clave de código generado por IA
    ├── openapi-initial.yaml      # Borrador inicial del contrato OpenAPI
    ├── wsdl-analysis-summary.md  # Análisis del WSDL legacy
    └── README.md                 # Versión inicial del README
```

### Contenido Documentado

#### **ai/prompts.md**
Registra todos los prompts utilizados durante el desarrollo, incluyendo:
- **Análisis del WSDL legacy** y mapeo a BIAN
- **Generación del contrato OpenAPI 3.0**
- **Creación de estructura hexagonal** de paquetes
- **Implementación del modelo de dominio** (aggregates, value objects)
- **Configuración de MapStruct** y mappers
- **Implementación de controladores REST**
- **Creación de tests unitarios e integración**
- **Configuración de quality gates** (JaCoCo, Checkstyle, SpotBugs)

#### **ai/decisions.md**
Documenta todas las decisiones técnicas y correcciones manuales:
- **Arquitectura hexagonal**: Justificación y estructura de paquetes
- **Modelo de dominio**: Value objects, aggregates, transiciones de estado
- **Mapeo SOAP → BIAN**: Mapeo de campos y operaciones
- **Configuración de herramientas**: JaCoCo, Checkstyle, SpotBugs
- **Exclusiones de código generado**: Rationale para exclusiones en quality gates
- **Estrategia de testing**: Unitarios vs integración, cobertura objetivo

#### **ai/generations/**
Almacena fragmentos clave generados por IA:
- Contrato OpenAPI inicial
- Análisis del WSDL legacy
- Estructura inicial del proyecto

### Práctica de Uso de IA

- ✅ **Todo el código generado por IA ha sido revisado** antes de integrarse
- ✅ **Las correcciones manuales están documentadas** en `ai/decisions.md`
- ✅ **Los prompts utilizados están registrados** en `ai/prompts.md`
- ✅ **El código generado se adaptó** a los estándares del proyecto (BIAN, arquitectura hexagonal)

---

## 📁 Estructura del Proyecto

```
payment-initiation-service/
├── src/
│   ├── main/
│   │   ├── java/com/bank/paymentinitiation/
│   │   │   ├── domain/              # Capa de Dominio
│   │   │   ├── application/         # Capa de Aplicación
│   │   │   ├── adapter/             # Adaptadores (REST, Persistence)
│   │   │   ├── config/              # Configuración Spring
│   │   │   └── PaymentInitiationServiceApplication.java
│   │   └── resources/
│   │       └── application.yml      # Configuración de la aplicación
│   └── test/
│       └── java/com/bank/paymentinitiation/
│           ├── domain/              # Tests del dominio
│           ├── application/         # Tests de servicios
│           └── adapter/             # Tests de integración
│
├── openapi/
│   └── openapi.yaml                 # Contrato OpenAPI 3.0 (Contract-First)
│
├── config/
│   ├── checkstyle/
│   │   ├── checkstyle.xml           # Configuración Checkstyle
│   │   └── suppressions.xml         # Supresiones adicionales
│   └── spotbugs/
│       └── exclude.xml              # Exclusiones SpotBugs
│
├── ai/                               # Documentación de uso de IA
│   ├── prompts.md                   # Prompts utilizados
│   ├── decisions.md                 # Decisiones arquitectónicas
│   └── generations/                 # Fragmentos generados
│
├── build.gradle                     # Configuración Gradle
├── settings.gradle                  # Configuración del proyecto
├── gradlew                          # Gradle Wrapper (Unix)
├── gradlew.bat                      # Gradle Wrapper (Windows)
└── README.md                        # Este archivo
```

---

## 🔧 Configuración

### application.yml

La configuración principal se encuentra en `src/main/resources/application.yml`:

- **Puerto**: 8080
- **Base de datos**: H2 en memoria (`jdbc:h2:mem:paymentdb`)
- **Consola H2**: Habilitada en `/h2-console`
- **JPA**: `ddl-auto: update` (crea/actualiza tablas automáticamente)
- **Actuator**: Health e info endpoints habilitados

### Quality Gates

#### JaCoCo
- **Cobertura mínima**: 85% a nivel de proyecto
- **Cobertura mínima por clase**: 80%
- **Exclusiones**: Código generado, entidades JPA, implementaciones MapStruct, clase principal, configuración

#### Checkstyle
- **maxWarnings**: 10 (permite algunos warnings antes de fallar)
- **Reglas**: Longitud de línea 120, complejidad ciclomática 15, naming estándar Java
- **Exclusiones**: Código generado (`generated/.*`), MapStruct impls (`.*MapperImpl\.java$`)

#### SpotBugs
- **Effort**: MAX (análisis exhaustivo)
- **Report Level**: HIGH (solo problemas de alta confianza)
- **Exclusiones**: Código generado, MapStruct impls, entidades JPA, clase principal

---

## ✅ Estado del Proyecto

### Completado ✅

- [x] Estructura del proyecto con arquitectura hexagonal
- [x] Análisis del WSDL legacy y mapeo a BIAN
- [x] Contrato OpenAPI 3.0 completo
- [x] Generación de código desde OpenAPI (Contract-First)
- [x] Modelo de dominio implementado (aggregates, value objects, enums)
- [x] Puertos y casos de uso definidos
- [x] Servicios de aplicación implementados
- [x] Adaptadores REST implementados (controller, mapper, exception handler)
- [x] Adaptadores de persistencia implementados (JPA, mapper)
- [x] Configuración de H2 en memoria
- [x] Tests unitarios (53 tests)
- [x] Tests de integración (10 tests)
- [x] Configuración de quality gates (JaCoCo, Checkstyle, SpotBugs)
- [x] Documentación IA completa

### Próximos Pasos ⏳

- [ ] Configuración de Docker (Dockerfile, docker-compose.yml)
- [ ] Configuración de perfiles (dev, test, prod)
- [ ] Mejoras en observabilidad (métricas, traces)
- [ ] Documentación de API en Swagger UI

---

## 📖 Referencias

- **BIAN Service Domain**: Payment Initiation
- **Behavior Qualifier**: PaymentOrder
- **Estándar**: OpenAPI 3.0.3
- **Manejo de Errores**: RFC 7807 (Problem Details)

---

## 📝 Licencia

Este proyecto es parte de un ejercicio técnico de migración SOAP a REST.

---

## 👥 Equipo

Desarrollado con asistencia de IA (Cursor/ChatGPT) y documentado según las mejores prácticas de desarrollo asistido por IA.
