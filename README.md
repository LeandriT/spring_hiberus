# Payment Initiation Service

Microservicio Spring Boot 3 para la gestión de órdenes de pago según el estándar **BIAN Payment Initiation / PaymentOrder**, implementado mediante una migración de **SOAP a REST** usando arquitectura hexagonal.

## 📋 Descripción del Proyecto

Este microservicio implementa la funcionalidad de iniciación de pagos bancarios siguiendo el estándar **BIAN (Banking Industry Architecture Network)** para Payment Initiation. El proyecto representa una migración de un servicio SOAP legacy a una arquitectura REST moderna, utilizando:

- **Contract-First Development**: El contrato OpenAPI 3.0 define la API antes de la implementación
- **Arquitectura Hexagonal**: Separación clara entre dominio, aplicación y adaptadores
- **Domain-Driven Design**: Modelo de dominio rico con agregados, value objects y servicios de dominio

### Funcionalidades Principales

- **Iniciar orden de pago**: Crear una nueva orden de pago con validaciones de negocio
- **Consultar orden de pago**: Obtener los detalles completos de una orden de pago
- **Consultar estado de orden**: Obtener únicamente el estado actual de una orden de pago

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
- **Java 21**: Última versión LTS con características modernas
- **Spring Boot 3.2.0**: Framework de aplicación empresarial
- **Spring MVC**: Para la capa REST
- **Spring Data JPA**: Para la persistencia

### Base de Datos
- **H2 Database**: Base de datos en memoria para desarrollo y testing
- **JPA/Hibernate**: ORM para mapeo objeto-relacional

### Build y Gestión de Dependencias
- **Gradle 8.5**: Sistema de build con Groovy DSL
- **Gradle Wrapper**: Para builds reproducibles

### Mapeo y Validación
- **MapStruct 1.5.5**: Mapeo entre objetos (DTO ↔ Domain ↔ Entity)
- **Bean Validation (Jakarta)**: Validación de datos de entrada

### API y Contratos
- **OpenAPI 3.0**: Especificación del contrato REST
- **OpenAPI Generator 7.0.1**: Generación automática de DTOs desde el contrato

### Testing
- **JUnit 5**: Framework de testing
- **Mockito**: Mocking para tests unitarios
- **AssertJ**: Assertions fluidas
- **WebTestClient**: Tests de integración para REST API
- **Spring Boot Test**: Contexto de testing integrado

### Calidad de Código
- **JaCoCo 0.8.11**: Cobertura de código (mínimo 85%)
- **Checkstyle 10.12.5**: Verificación de estilo de código
- **SpotBugs 4.8.3**: Análisis estático de bugs potenciales

### Containerización
- **Docker**: Containerización del microservicio
- **Docker Compose**: Orquestación del servicio

### Utilidades
- **Lombok**: Reducción de boilerplate (getters, setters, builders)
- **Spring Actuator**: Endpoints de monitoreo y salud

## 🚀 Cómo Ejecutar

### Prerrequisitos

- **Java 21** o superior
- **Gradle 8.5** (incluido via wrapper)
- **Docker** y **Docker Compose** (opcional, para ejecución en contenedor)

### Ejecución Local

#### 1. Compilar y Ejecutar Quality Gates

```bash
# Compilar, ejecutar tests y verificar quality gates
./gradlew clean check
```

Este comando ejecuta:
- Compilación del código
- Tests unitarios e integración
- Verificación de cobertura (JaCoCo) - mínimo 85%
- Análisis de estilo (Checkstyle)
- Análisis estático (SpotBugs)

#### 2. Ejecutar la Aplicación

```bash
# Ejecutar la aplicación Spring Boot
./gradlew bootRun
```

La aplicación estará disponible en: `http://localhost:8080`

#### 3. Verificar Salud de la Aplicación

```bash
# Verificar que la aplicación está funcionando
curl http://localhost:8080/actuator/health
```

### Ejecución con Docker

#### 1. Construir y Ejecutar con Docker Compose

```bash
# Construir la imagen y levantar el contenedor
docker compose up --build
```

#### 2. Verificar el Contenedor

```bash
# Ver el estado del contenedor
docker compose ps

# Ver los logs
docker compose logs -f payment-initiation-service

# Verificar salud
docker compose exec payment-initiation-service wget -q -O - http://localhost:8080/actuator/health
```

#### 3. Detener el Contenedor

```bash
# Detener y eliminar el contenedor
docker compose down
```

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

```bash
POST http://localhost:8080/payment-initiation/payment-orders
Content-Type: application/json

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

#### Consultar una Orden de Pago

```bash
GET http://localhost:8080/payment-initiation/payment-orders/PO-1234567890123456
```

#### Consultar Estado de una Orden

```bash
GET http://localhost:8080/payment-initiation/payment-orders/PO-1234567890123456/status
```

### Endpoints Disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/payment-initiation/payment-orders` | Crear una nueva orden de pago |
| GET | `/payment-initiation/payment-orders/{id}` | Obtener detalles completos de una orden |
| GET | `/payment-initiation/payment-orders/{id}/status` | Obtener solo el estado de una orden |

### Endpoints de Actuator

| Endpoint | Descripción |
|----------|-------------|
| `/actuator/health` | Estado de salud de la aplicación |
| `/actuator/info` | Información de la aplicación |

## 📊 Cobertura de Código

El proyecto mantiene una cobertura mínima del **85%** verificada automáticamente con JaCoCo.

### Ver Cobertura

```bash
# Generar reporte de cobertura
./gradlew test jacocoTestReport

# Ver reporte HTML
open build/reports/jacoco/test/html/index.html
```

### Cobertura Actual

- **Cobertura Total**: 91%
- **Cobertura por Capa**:
  - Domain: 90%
  - Application: 100%
  - Adapters: 100%

## 🔍 Quality Gates

El proyecto incluye verificaciones automáticas de calidad:

### Checkstyle

```bash
# Verificar estilo de código
./gradlew checkstyleMain checkstyleTest
```

### SpotBugs

```bash
# Análisis estático de bugs
./gradlew spotbugsMain spotbugsTest
```

### JaCoCo

```bash
# Verificar cobertura mínima
./gradlew jacocoTestCoverageVerification
```

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

- **Usuario no-root en Docker**: El contenedor se ejecuta con usuario `spring:spring`
- **Validación de entrada**: Validaciones con Bean Validation
- **Manejo de errores**: Respuestas RFC 7807 (Problem Details)
- **Health checks**: Monitoreo de salud del servicio

## 📝 Licencia

Este proyecto es parte de una prueba técnica y está destinado únicamente para fines de evaluación.

## 👥 Autor

Desarrollado como parte de una prueba técnica de migración SOAP a REST.

---

**Nota**: Este microservicio utiliza H2 en memoria para desarrollo y testing. Para producción, se recomienda configurar una base de datos persistente (PostgreSQL, MySQL, etc.).

