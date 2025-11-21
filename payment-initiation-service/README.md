# Payment Initiation Service

Microservicio Spring Boot 3 para la gestión de órdenes de pago según el estándar **BIAN Payment Initiation / PaymentOrder**, implementado mediante una migración de **SOAP a REST** usando arquitectura hexagonal.

El proyecto **Payment Initiation Service** fue desarrollado con **Java 21, Spring Boot 3.2.0, Spring Data JPA (Hibernate), H2 Database, Docker, OpenAPI 3.0, JUnit 5, JaCoCo, Checkstyle, SpotBugs y WebTestClient**.

Se aplicaron prácticas de **TDD (Test-Driven Development), Clean Code, principios SOLID, Domain-Driven Design y Arquitectura Hexagonal**.

## 📋 Descripción del Proyecto

Este microservicio implementa la funcionalidad de iniciación de pagos bancarios siguiendo el estándar **BIAN (Banking Industry Architecture Network)** para Payment Initiation. El proyecto representa una migración de un servicio SOAP legacy a una arquitectura REST moderna, utilizando:

- **Contract-First Development**: El contrato OpenAPI 3.0 define la API antes de la implementación
- **Arquitectura Hexagonal**: Separación clara entre dominio, aplicación y adaptadores
- **Domain-Driven Design**: Modelo de dominio rico con agregados, value objects y servicios de dominio
- **TDD**: Desarrollo guiado por tests con cobertura mínima del 85%
- **Clean Code**: Código limpio y mantenible siguiendo principios SOLID

### Funcionalidades Principales

- **Iniciar orden de pago**: Crear una nueva orden de pago con validaciones de negocio y generación automática de referencia
- **Consultar orden de pago**: Obtener los detalles completos de una orden de pago por su ID
- **Consultar estado de orden**: Obtener únicamente el estado actual y última actualización de una orden de pago

## 🏗️ Arquitectura Hexagonal

El proyecto sigue los principios de **Arquitectura Hexagonal (Ports & Adapters)**, separando el dominio del negocio de los detalles técnicos de implementación.

### Estructura de Paquetes

```
com.bank.paymentinitiation/
├── domain/                          # Capa de Dominio (Núcleo)
│   ├── model/                       # Agregados y Value Objects
│   │   ├── PaymentOrder            # Agregado raíz
│   │   ├── PaymentStatus            # Enum de estados
│   │   ├── PaymentAmount            # Value Object
│   │   ├── ExternalReference       # Value Object
│   │   ├── PayerReference          # Value Object
│   │   └── PayeeReference          # Value Object
│   ├── port/
│   │   ├── in/                      # Puertos de entrada (Use Cases)
│   │   │   ├── InitiatePaymentOrderUseCase
│   │   │   ├── RetrievePaymentOrderUseCase
│   │   │   └── RetrievePaymentOrderStatusUseCase
│   │   └── out/                     # Puertos de salida (Repositorios)
│   │       └── PaymentOrderRepository
│   ├── service/                     # Servicios de Dominio
│   │   └── PaymentOrderDomainService
│   └── exception/                   # Excepciones de Dominio
│       ├── PaymentOrderNotFoundException
│       └── InvalidPaymentException
├── application/                      # Capa de Aplicación
│   └── service/                     # Servicios de Aplicación (Orquestación)
│       ├── InitiatePaymentOrderService
│       ├── RetrievePaymentOrderService
│       ├── RetrievePaymentOrderStatusService
│       └── PaymentOrderReferenceGenerator
├── adapter/                         # Capa de Adaptadores
│   ├── in/                          # Adaptadores de Entrada
│   │   └── rest/                    # REST API
│   │       ├── PaymentOrdersController
│   │       ├── GlobalExceptionHandler
│   │       └── mapper/
│   │           └── PaymentOrderRestMapper
│   └── out/                         # Adaptadores de Salida
│       └── persistence/            # Persistencia JPA
│           ├── entity/
│           │   └── PaymentOrderEntity
│           ├── jpa/
│           │   └── PaymentOrderJpaRepository
│           ├── mapper/
│           │   └── PaymentOrderPersistenceMapper
│           └── PaymentOrderRepositoryAdapter
└── config/                          # Configuración
    └── ApplicationConfig
```

### Principios de la Arquitectura

- **Dominio Independiente**: El dominio no depende de frameworks ni tecnologías externas
- **Puertos y Adaptadores**: Interfaces claras entre capas (ports) y su implementación (adapters)
- **Inversión de Dependencias**: Las capas externas dependen de las internas, no al revés
- **Separación de Responsabilidades**: Cada capa tiene una responsabilidad específica

## 🛠️ Stack Técnico

### Lenguaje y Framework
- **Java 21**: Última versión LTS con características modernas (records, pattern matching, virtual threads)
- **Spring Boot 3.2.0**: Framework de aplicación empresarial
- **Spring MVC**: Para la capa REST
- **Spring Data JPA (Hibernate)**: Para la persistencia y ORM

### Base de Datos
- **H2 Database**: Base de datos en memoria para desarrollo y testing
- **JPA/Hibernate**: ORM para mapeo objeto-relacional

### Build y Gestión de Dependencias
- **Gradle 8.5**: Sistema de build con Groovy DSL
- **Gradle Wrapper**: Para builds reproducibles

### Mapeo y Validación
- **MapStruct 1.5.5**: Mapeo entre objetos (DTO ↔ Domain ↔ Entity)
- **Bean Validation (Jakarta)**: Validación de datos de entrada con anotaciones

### API y Contratos
- **OpenAPI 3.0**: Especificación del contrato REST
- **OpenAPI Generator 7.0.1**: Generación automática de DTOs desde el contrato

### Testing
- **JUnit 5**: Framework de testing
- **Mockito**: Mocking para tests unitarios
- **AssertJ**: Assertions fluidas y expresivas
- **WebTestClient**: Tests de integración para REST API (end-to-end)
- **Spring Boot Test**: Contexto de testing integrado

### Calidad de Código
- **JaCoCo 0.8.11**: Cobertura de código (mínimo 85%, actual: 91%)
- **Checkstyle 10.12.5**: Verificación de estilo de código
- **SpotBugs 4.8.3**: Análisis estático de bugs potenciales

### Containerización
- **Docker**: Containerización del microservicio (multi-stage build)
- **Docker Compose**: Orquestación del servicio

### Utilidades
- **Lombok 1.18.30**: Reducción de boilerplate (getters, setters, builders, @Value)
- **Spring Actuator**: Endpoints de monitoreo y salud (`/actuator/health`, `/actuator/info`)

## 🚀 Cómo Ejecutar

### Prerrequisitos

- **Java 21** o superior
- **Gradle 8.5** (incluido via wrapper)
- **Docker** y **Docker Compose** (opcional, para ejecución en contenedor)

### Ejecución Local

#### 1. Clonar el Repositorio (si aplica)

```bash
git clone <repository-url>
cd payment-initiation-service
```

#### 2. Compilar y Ejecutar Quality Gates

```bash
# Compilar, ejecutar tests y verificar quality gates
./gradlew clean check
```

Este comando ejecuta:
- Compilación del código
- Tests unitarios e integración (106 tests)
- Verificación de cobertura (JaCoCo) - mínimo 85% (actual: 91%)
- Análisis de estilo (Checkstyle)
- Análisis estático (SpotBugs)

#### 3. Ejecutar la Aplicación

```bash
# Ejecutar la aplicación Spring Boot
./gradlew bootRun
```

La aplicación estará disponible en: `http://localhost:8080`

#### 4. Verificar Salud de la Aplicación

```bash
# Verificar que la aplicación está funcionando
curl http://localhost:8080/actuator/health
```

Respuesta esperada:
```json
{
  "status": "UP"
}
```

#### 5. Acceso a la Consola H2 (Solo Desarrollo)

- **URL**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **JDBC URL**: `jdbc:h2:mem:paymentdb`
- **Usuario**: `sa`
- **Password**: *(vacío)*

**Nota**: La consola H2 está deshabilitada en el perfil Docker por seguridad.

### Ejecución con Docker

#### 1. Construir la Imagen Docker

```bash
# Construir la imagen Docker (multi-stage build)
docker build -t payment-initiation-service .
```

#### 2. Ejecutar el Contenedor

```bash
# Ejecutar el contenedor
docker run -p 8080:8080 payment-initiation-service
```

#### 3. Construir y Ejecutar con Docker Compose

```bash
# Construir la imagen y levantar el contenedor
docker compose up --build
```

#### 4. Verificar el Contenedor

```bash
# Ver el estado del contenedor
docker compose ps

# Ver los logs
docker compose logs -f payment-initiation-service

# Verificar salud
docker compose exec payment-initiation-service wget -q -O - http://localhost:8080/actuator/health
```

#### 5. Detener el Contenedor

```bash
# Detener y eliminar el contenedor
docker compose down
```

## 📑 API Reference

### Endpoints Disponibles

| Método | Endpoint | Descripción | Código de Respuesta |
|--------|----------|-------------|---------------------|
| POST | `/payment-initiation/payment-orders` | Crear una nueva orden de pago | 201 Created |
| GET | `/payment-initiation/payment-orders/{id}` | Obtener detalles completos de una orden | 200 OK |
| GET | `/payment-initiation/payment-orders/{id}/status` | Obtener solo el estado de una orden | 200 OK |

### Endpoints de Actuator

| Endpoint | Descripción |
|----------|-------------|
| `/actuator/health` | Estado de salud de la aplicación |
| `/actuator/info` | Información de la aplicación |

---

## 🧪 Cómo Probar con Postman

El proyecto incluye una colección de Postman con ejemplos de todas las operaciones disponibles.

### Importar la Colección

1. Abre Postman
2. Importa el archivo: `Prueba-tecnica-Java-migracion/postman_collection.json`
3. La colección incluye requests para:
   - `POST /payment-initiation/payment-orders` - Crear orden de pago
   - `GET /payment-initiation/payment-orders/{id}` - Consultar orden completa
   - `GET /payment-initiation/payment-orders/{id}/status` - Consultar solo estado

### Ejemplos de Uso

#### Crear una Orden de Pago

```http
POST http://localhost:8080/payment-initiation/payment-orders
Content-Type: application/json
```

**Request Body:**
```json
{
  "externalReference": "EXT-001",
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
  "requestedExecutionDate": "2024-12-25"
}
```

**Response (201 Created):**
```json
{
  "paymentOrderId": "PO-1234567890123456",
  "status": "INITIATED"
}
```

#### Consultar una Orden de Pago

```http
GET http://localhost:8080/payment-initiation/payment-orders/PO-1234567890123456
```

**Response (200 OK):**
```json
{
  "paymentOrderId": "PO-1234567890123456",
  "externalReference": "EXT-001",
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
  "requestedExecutionDate": "2024-12-25",
  "status": "INITIATED",
  "lastUpdate": "2024-11-20T21:30:00Z"
}
```

#### Consultar Estado de una Orden

```http
GET http://localhost:8080/payment-initiation/payment-orders/PO-1234567890123456/status
```

**Response (200 OK):**
```json
{
  "paymentOrderId": "PO-1234567890123456",
  "status": "INITIATED",
  "lastUpdate": "2024-11-20T21:30:00Z"
}
```

#### Errores Comunes

**404 Not Found** - Orden de pago no encontrada:
```json
{
  "type": "https://bank.com/errors/not-found",
  "title": "Payment Order Not Found",
  "status": 404,
  "detail": "Payment order not found with reference: PO-9999999999999999",
  "timestamp": "2024-11-20T21:30:00Z"
}
```

**400 Bad Request** - Validación fallida:
```json
{
  "type": "https://bank.com/errors/bad-request",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed: debtorAccount: must not be null, instructedAmount: must not be null",
  "timestamp": "2024-11-20T21:30:00Z"
}
```

## 📦 DTOs Principales

### Request DTOs (Generados por OpenAPI)

- **InitiatePaymentOrderRequest** → Datos de entrada para crear una orden de pago
  - `externalReference` (String, requerido)
  - `debtorAccount` (DebtorAccount, requerido)
  - `creditorAccount` (CreditorAccount, requerido)
  - `instructedAmount` (PaymentAmount, requerido)
  - `remittanceInformation` (String, opcional)
  - `requestedExecutionDate` (LocalDate, requerido)

### Response DTOs (Generados por OpenAPI)

- **InitiatePaymentOrderResponse** → Respuesta al crear una orden
  - `paymentOrderId` (String) - Formato: `PO-{numericId}`
  - `status` (PaymentStatus) - Estado inicial: `INITIATED`

- **RetrievePaymentOrderResponse** → Respuesta al consultar una orden completa
  - `paymentOrderId` (String)
  - `externalReference` (String)
  - `debtorAccount` (DebtorAccount)
  - `creditorAccount` (CreditorAccount)
  - `instructedAmount` (PaymentAmount)
  - `remittanceInformation` (String)
  - `requestedExecutionDate` (LocalDate)
  - `status` (PaymentStatus)
  - `lastUpdate` (OffsetDateTime)

- **PaymentOrderStatusResponse** → Respuesta al consultar solo el estado
  - `paymentOrderId` (String)
  - `status` (PaymentStatus)
  - `lastUpdate` (OffsetDateTime)

### Value Objects del Dominio

- **PaymentOrder** → Agregado raíz con lógica de negocio
- **PaymentStatus** → Enum: `INITIATED`, `PENDING`, `PROCESSED`, `COMPLETED`, `FAILED`, `CANCELLED`
- **PaymentAmount** → Value Object con validación (valor > 0)
- **ExternalReference** → Value Object para referencia externa
- **PayerReference** → Value Object para referencia del pagador (IBAN)
- **PayeeReference** → Value Object para referencia del beneficiario (IBAN)

---

## 📊 Cobertura de Código

El proyecto mantiene una cobertura mínima del **85%** verificada automáticamente con JaCoCo.

### Ejecutar Pruebas y Generar Reporte de Cobertura

```bash
# Ejecutar todas las pruebas y generar reporte de cobertura
./gradlew test jacocoTestReport jacocoTestCoverageVerification
```

El reporte HTML queda en: `build/reports/jacoco/test/html/index.html`

### Cobertura Actual

- **Cobertura Total**: **91%** ✅ (supera el mínimo del 85%)
- **Cobertura por Capa**:
  - **Domain**: 90% (423 instrucciones cubiertas de 1,052)
  - **Application**: 100% (121 instrucciones cubiertas)
  - **Adapters**: 100% (180 instrucciones cubiertas en REST, 60 en Persistence)
  - **Domain Service**: 100% (54 instrucciones cubiertas)
  - **Domain Exception**: 100% (18 instrucciones cubiertas)

### Ver Reporte de Cobertura

```bash
# Abrir reporte HTML en el navegador (macOS)
open build/reports/jacoco/test/html/index.html

# O en Linux
xdg-open build/reports/jacoco/test/html/index.html
```

## 🔍 Quality Gates

El proyecto incluye verificaciones automáticas de calidad que se ejecutan en cada build:

### Checkstyle

Verifica el estilo de código según reglas configuradas.

```bash
# Verificar estilo de código
./gradlew checkstyleMain checkstyleTest
```

**Configuración:**
- Archivo: `config/checkstyle/checkstyle.xml`
- Max warnings: 100 (permite warnings menores aceptables)
- Exclusiones: Código generado, implementaciones MapStruct

### SpotBugs

Análisis estático de bugs potenciales y problemas de código.

```bash
# Análisis estático de bugs
./gradlew spotbugsMain spotbugsTest
```

**Configuración:**
- Effort: MAX
- Confidence: HIGH
- Exclusiones: Código generado, entidades JPA, clase principal

### JaCoCo

Verificación de cobertura mínima de código.

```bash
# Verificar cobertura mínima (debe ser >= 85%)
./gradlew jacocoTestCoverageVerification
```

**Configuración:**
- Cobertura mínima: 85%
- Cobertura actual: 91%
- Exclusiones: Código generado, entidades JPA, implementaciones MapStruct, clase principal, configuración

### Ejecutar Todos los Quality Gates

```bash
# Ejecutar todos los quality gates (tests + cobertura + checkstyle + spotbugs)
./gradlew clean check
```

---

## ✅ Pruebas

El proyecto incluye una suite completa de pruebas siguiendo principios de **TDD (Test-Driven Development)**.

### Pruebas Unitarias (JUnit 5 + Mockito + AssertJ)

- **106 tests unitarios** cubriendo:
  - Modelo de dominio (PaymentOrder, Value Objects, PaymentStatus)
  - Servicios de aplicación (InitiatePaymentOrderService, RetrievePaymentOrderService, etc.)
  - Servicios de dominio (PaymentOrderDomainService)
  - Mappers (PaymentOrderRestMapper, PaymentOrderPersistenceMapper)
  - Excepciones y constructores

**Ejecutar pruebas unitarias:**
```bash
./gradlew test
```

### Pruebas de Integración (WebTestClient)

- **9 tests de integración** validando:
  - Flujo completo: **crear → consultar → consultar estado**
  - Validaciones de entrada (campos requeridos, formatos, rangos)
  - Manejo de errores (404, 400 con Problem Details)
  - Persistencia en H2 real
  - Serialización/deserialización JSON

**Ejecutar pruebas de integración:**
```bash
./gradlew test --tests "*IntegrationTest"
```

### Cobertura de Código (JaCoCo)

- **Cobertura mínima**: 85% (verificación automática en el build)
- **Cobertura actual**: 91%
- **Reportes HTML**: `build/reports/jacoco/test/html/index.html`

**Ejecutar verificación de cobertura:**
```bash
./gradlew test jacocoTestReport jacocoTestCoverageVerification
```

### Estrategia de Testing

- **Unit Tests**: Aislamiento completo, sin contexto Spring (Mockito)
- **Integration Tests**: Contexto Spring completo, H2 real, WebTestClient
- **Test Coverage**: Verificación automática en cada build
- **AAA Pattern**: Arrange-Act-Assert en todos los tests
- **AssertJ**: Assertions fluidas y expresivas

---

## 🤖 Uso de IA en el Desarrollo

Este proyecto ha sido desarrollado con asistencia de IA, y toda la documentación relacionada se encuentra en la carpeta `ai/`.

### Estructura de Documentación IA

```
ai/
├── prompts.md           # Todos los prompts utilizados durante el desarrollo
├── decisions.md         # Decisiones de diseño y correcciones manuales
└── generations/         # Fragmentos de código generados relevantes
    ├── openapi-initial.yaml
    ├── wsdl-analysis.md
    └── README.md
```

### Contenido Documentado

- **Prompts**: Registro completo de todos los prompts utilizados en cada paso del desarrollo
- **Decisiones**: Documentación de decisiones de diseño, correcciones manuales y trade-offs
- **Generaciones**: Fragmentos de código generados especialmente relevantes

### Trazabilidad

Cada paso del desarrollo está documentado con:
- El prompt utilizado
- La respuesta de la IA
- Las correcciones manuales aplicadas
- Las decisiones de diseño tomadas

Esto permite mantener trazabilidad completa del proceso de desarrollo y entender qué fue generado por IA y qué fue ajustado manualmente.

## 📁 Estructura del Proyecto

```
payment-initiation-service/
├── src/
│   ├── main/
│   │   ├── java/                    # Código fuente
│   │   └── resources/
│   │       ├── application.yml      # Configuración principal
│   │       └── application-docker.yml # Configuración Docker
│   └── test/                        # Tests
├── openapi/
│   └── openapi.yaml                 # Contrato OpenAPI 3.0
├── config/
│   ├── checkstyle/                  # Configuración Checkstyle
│   └── spotbugs/                    # Configuración SpotBugs
├── build.gradle                     # Configuración Gradle
├── settings.gradle                  # Configuración del proyecto
├── Dockerfile                       # Dockerfile multi-stage
├── docker-compose.yml               # Orquestación Docker
├── .dockerignore                    # Exclusiones para Docker
├── .gitignore                       # Exclusiones para Git
├── README.md                        # Este archivo
└── ai/                              # Documentación de IA
```

## 🔐 Seguridad

- **Usuario no-root en Docker**: El contenedor se ejecuta con usuario `spring:spring` (principio de menor privilegio)
- **Validación de entrada**: Validaciones con Bean Validation (Jakarta) en todos los endpoints
- **Manejo de errores**: Respuestas RFC 7807 (Problem Details) sin exponer información sensible
- **Health checks**: Monitoreo de salud del servicio con Docker HEALTHCHECK
- **H2 Console deshabilitada en producción**: Solo disponible en desarrollo local
- **Validación de reglas de negocio**: Validaciones de dominio (fechas futuras, montos positivos, etc.)

## 🧩 Diagrama de Arquitectura

El proyecto sigue una **Arquitectura Hexagonal (Ports & Adapters)** con las siguientes capas:

```
┌─────────────────────────────────────────────────────────────┐
│                    ADAPTERS (Infraestructura)                │
├─────────────────────────────────────────────────────────────┤
│  REST (in)          │  Persistence (out)                      │
│  - Controller      │  - JPA Entity                          │
│  - Mapper REST     │  - JPA Repository                       │
│  - Exception Handler│  - Mapper Persistence                  │
└────────────────────┼─────────────────────────────────────────┘
                     │
┌────────────────────┼─────────────────────────────────────────┐
│              APPLICATION (Casos de Uso)                       │
│  - InitiatePaymentOrderService                                │
│  - RetrievePaymentOrderService                                │
│  - RetrievePaymentOrderStatusService                           │
└────────────────────┼─────────────────────────────────────────┘
                     │
┌────────────────────┼─────────────────────────────────────────┐
│                  DOMAIN (Núcleo de Negocio)                   │
│  - PaymentOrder (Aggregate Root)                               │
│  - Value Objects (PaymentAmount, References)                    │
│  - PaymentStatus (Enum)                                        │
│  - PaymentOrderDomainService                                   │
│  - Ports (Use Cases, Repository)                              │
│  - Exceptions                                                  │
└───────────────────────────────────────────────────────────────┘
```

**Principios:**
- El dominio es independiente de frameworks
- Las dependencias apuntan hacia adentro (hacia el dominio)
- Los adaptadores implementan los puertos definidos en el dominio

## 📝 Licencia

Este proyecto es parte de una prueba técnica y está destinado únicamente para fines de evaluación.

## 📚 Recursos Adicionales

### Documentación del Contrato OpenAPI

El contrato OpenAPI 3.0 está disponible en: `openapi/openapi.yaml`

Este contrato define:
- Todos los endpoints disponibles
- Esquemas de request/response
- Validaciones y restricciones
- Códigos de respuesta HTTP
- Ejemplos de uso

### Colección Postman

La colección de Postman incluye:
- Requests pre-configurados para todos los endpoints
- Ejemplos de request/response
- Variables de entorno
- Tests automatizados

**Ubicación**: `Prueba-tecnica-Java-migracion/postman_collection.json`

### Artefactos Legacy (Referencia)

El proyecto incluye artefactos del sistema legacy para referencia:
- **WSDL**: `Prueba-tecnica-Java-migracion/legacy/PaymentOrderService.wsdl`
- **Ejemplos XML**: `Prueba-tecnica-Java-migracion/legacy/samples/`

Estos archivos fueron utilizados durante el análisis y diseño del contrato REST.

## 👥 Autor

Desarrollado como parte de una prueba técnica de migración SOAP a REST.

**Tecnologías y Prácticas Aplicadas:**
- ✅ Java 21 + Spring Boot 3.2.0
- ✅ Arquitectura Hexagonal (Ports & Adapters)
- ✅ Domain-Driven Design
- ✅ Contract-First Development (OpenAPI 3.0)
- ✅ TDD con cobertura > 85%
- ✅ Clean Code y principios SOLID
- ✅ Docker multi-stage build
- ✅ Quality Gates (JaCoCo, Checkstyle, SpotBugs)

---

**Nota**: Este microservicio utiliza H2 en memoria para desarrollo y testing. Para producción, se recomienda configurar una base de datos persistente (PostgreSQL, MySQL, etc.) y ajustar la configuración de JPA según sea necesario.

