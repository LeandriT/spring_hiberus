# Prompts de IA - Payment Initiation Service

Este archivo documenta todos los prompts utilizados durante el desarrollo del proyecto, incluyendo aquellos usados con Cursor y otras herramientas de IA.

## Estructura de Documentación

Cada entrada de prompt debe incluir:
- **Fecha**: Fecha en que se usó el prompt
- **Contexto**: Situación o necesidad que motivó el prompt
- **Prompt usado**: El texto exacto del prompt enviado a la IA
- **Herramienta**: Cursor, ChatGPT, GitHub Copilot, etc.
- **Respuesta resumida**: Breve descripción de lo que generó la IA
- **Correcciones manuales**: Cambios aplicados manualmente al código generado
- **Archivos generados/modificados**: Lista de archivos afectados

---

## Prompt 1: Creación del Proyecto Base

**Fecha**: 2025-01-27

**Contexto**: Inicialización del proyecto desde cero. Necesidad de crear la estructura base con Spring Boot 3, Java 21 y todas las herramientas de calidad configuradas.

**Herramienta**: Cursor

**Prompt usado**:
```
Crea un proyecto Spring Boot 3 con Java 21 usando Gradle (Groovy DSL).

Actúa como un generador de proyectos y crea lo siguiente:

- Nombre del proyecto (crear carpeta): payment-initiation-service
- Group: com.bank.paymentinitiation
- Paquete base: com.bank.paymentinitiation

En build.gradle configura:
- Plugins: java, spring-boot 3.2.0, dependency-management, openapi-generator, checkstyle, spotbugs, jacoco
- Java 21 (sourceCompatibility y targetCompatibility)
- Dependencias: Spring Web MVC, Spring Data JPA, Validation, Actuator, H2, MapStruct, Lombok
- Tests: Spring Boot Test, Spring WebFlux (para WebTestClient)

Configura Checkstyle con archivos en config/checkstyle/checkstyle.xml y suppressions.xml
Configura JaCoCo básico
Configura SpotBugs sin romper el build
```

**Respuesta de la IA**:
La IA generó:
- Estructura completa del proyecto con directorios src/main/java, src/test/java, src/main/resources
- build.gradle con todos los plugins y dependencias especificadas
- settings.gradle con el nombre del proyecto
- Configuración de Checkstyle con reglas "medias" apropiadas para proyectos BIAN
- Archivos de supresión para código generado
- Clase principal Spring Boot Application
- application.yml básico con configuración H2
- gradle-wrapper.properties
- .gitignore estándar para proyectos Java/Spring

**Correcciones manuales aplicadas**:
- Ninguna en este paso inicial. La configuración fue generada correctamente.

---

## Prompt 2: Análisis del WSDL Legacy y Mapeo a BIAN

**Fecha**: 2025-01-27

**Contexto**: Necesidad de analizar el servicio SOAP legacy (PaymentOrderService.wsdl) y los XML de ejemplo para entender la funcionalidad existente y mapearla correctamente al Service Domain BIAN "Payment Initiation" con Behavior Qualifier "PaymentOrder".

**Herramienta**: Cursor

**Prompt usado**:
```
Analiza el archivo PaymentOrderService.wsdl y los XML de ejemplo (SubmitPaymentOrderRequest/Response, GetPaymentOrderStatusRequest/Response) y dame:

1) Operaciones SOAP disponibles relacionadas con órdenes de pago.
2) Estructuras de datos principales (campos clave) de la orden de pago.
3) Estados posibles de la orden de pago en el servicio legacy.
4) Un mapeo de estos conceptos al Service Domain BIAN Payment Initiation y al BQ PaymentOrder.
```

**Respuesta de la IA**:
La IA realizó un análisis completo que incluyó:

1. **Operaciones SOAP identificadas**:
   - `SubmitPaymentOrder`: Envía una nueva orden de pago
   - `GetPaymentOrderStatus`: Consulta el estado de una orden existente

2. **Estructuras de datos analizadas**:
   - `SubmitPaymentOrderRequest`: 7 campos (externalId, debtorIban, creditorIban, amount, currency, remittanceInfo, requestedExecutionDate)
   - `SubmitPaymentOrderResponse`: 2 campos (paymentOrderId, status)
   - `GetPaymentOrderStatusRequest`: 1 campo (paymentOrderId)
   - `GetPaymentOrderStatusResponse`: 3 campos (paymentOrderId, status, lastUpdate)

3. **Estados identificados**:
   - `ACCEPTED`: Estado inicial en SubmitPaymentOrderResponse
   - `SETTLED`: Estado final en GetPaymentOrderStatusResponse
   - Estados inferidos: PENDING, PROCESSING, REJECTED, FAILED, CANCELLED

4. **Mapeo completo SOAP → BIAN**:
   - Operaciones: SubmitPaymentOrder → Initiate (POST), GetPaymentOrderStatus → Retrieve Status (GET)
   - Campos: Mapeo detallado de cada campo legacy a su equivalente BIAN
   - Estados: Mapeo de estados legacy a estados BIAN estándar
   - Estructuras: Agrupación de campos en objetos BIAN (instructedAmount, debtorAccount, creditorAccount)

**Correcciones manuales aplicadas**:
- Se agregó la decisión de incluir operación `Retrieve` adicional (no presente en SOAP legacy) para mejor alineación BIAN
- Se refinó el mapeo de estados para incluir estados inferidos basados en mejores prácticas de sistemas de pago
- Se documentaron decisiones de diseño sobre nomenclatura BIAN y estructura de objetos anidados

**Archivos generados/modificados**:
- `ai/decisions.md`: Análisis completo agregado en la sección "Fase 2: Análisis del Servicio SOAP Legacy y Mapeo a BIAN"
- `ai/prompts.md`: Esta entrada documentada

---

## Prompt 3: Generación del Contrato OpenAPI 3.0

**Fecha**: 2025-01-27

**Contexto**: Necesidad de crear el contrato OpenAPI 3.0 completo que defina los endpoints REST basados en el análisis del WSDL legacy y la colección Postman. El contrato debe ser contract-first y alineado con BIAN Payment Initiation.

**Herramienta**: Cursor

**Prompt usado**:
```
Basándote en el análisis del WSDL y la colección postman_collection.json (endpoints):

- POST http://localhost:8080/payment-initiation/payment-orders
- GET  http://localhost:8080/payment-initiation/payment-orders/{id}
- GET  http://localhost:8080/payment-initiation/payment-orders/{id}/status

Genera un archivo openapi/openapi.yaml con OpenAPI 3.0 que defina:

- servers: url http://localhost:8080
- paths: POST /payment-initiation/payment-orders (initiatePaymentOrder), GET /payment-initiation/payment-orders/{id} (retrievePaymentOrder), GET /payment-initiation/payment-orders/{id}/status (retrievePaymentOrderStatus)
- requestBody con esquema InitiatePaymentOrderRequest alineado a la colección Postman
- responses: 201, 200, 400, 404, 500 con ProblemDetail (RFC 7807)
- schemas: InitiatePaymentOrderRequest, InitiatePaymentOrderResponse, RetrievePaymentOrderResponse, PaymentOrderStatusResponse, PaymentAmount, Account, PaymentOrderStatus, ProblemDetail
```

**Respuesta de la IA**:
La IA generó un contrato OpenAPI 3.0 completo que incluye:

1. **Información del API**:
   - Título, descripción, versión 1.0.0
   - Información de contacto y licencia

2. **Servers**:
   - Servidor de desarrollo local en http://localhost:8080

3. **Paths completos**:
   - POST `/payment-initiation/payment-orders` con operationId `initiatePaymentOrder`
   - GET `/payment-initiation/payment-orders/{id}` con operationId `retrievePaymentOrder`
   - GET `/payment-initiation/payment-orders/{id}/status` con operationId `retrievePaymentOrderStatus`

4. **Schemas definidos**:
   - `InitiatePaymentOrderRequest`: Con todos los campos requeridos y opcionales
   - `InitiatePaymentOrderResponse`: Respuesta con paymentOrderReference, status, cuentas, amount, timestamps
   - `RetrievePaymentOrderResponse`: Detalle completo de la orden
   - `PaymentOrderStatusResponse`: Solo estado y lastUpdated
   - `Account`: Objeto con iban (validación IBAN)
   - `PaymentAmount`: Objeto con amount y currency (validaciones de monto mínimo y moneda ISO 4217)
   - `PaymentOrderStatus`: Enum con estados BIAN (INITIATED, PENDING, PROCESSED, COMPLETED, FAILED, CANCELLED)
   - `ProblemDetail`: RFC 7807 completo para manejo de errores

5. **Validaciones incluidas**:
   - Patrones regex para paymentOrderReference (PO-xxxx)
   - Validación de IBAN (longitud y patrón)
   - Validación de currency (ISO 4217, enum con monedas comunes)
   - Validación de amount (mínimo 0.01)
   - Longitudes máximas para strings
   - Formatos de fecha y fecha-hora (ISO 8601)

6. **Manejo de errores**:
   - 400 Bad Request con ProblemDetail
   - 404 Not Found con ProblemDetail
   - 500 Internal Server Error con ProblemDetail
   - Todos usando content-type `application/problem+json`

**Correcciones manuales aplicadas**:
- Se agregó extensión `x-mapping` en PaymentOrderStatus para documentar el mapeo de estados legacy a BIAN
- Se refinaron las descripciones para incluir referencias al mapeo SOAP → BIAN
- Se agregaron ejemplos en todos los schemas para facilitar el testing

**Archivos generados/modificados**:
- `openapi/openapi.yaml`: Contrato OpenAPI 3.0 completo creado
- `ai/prompts.md`: Esta entrada documentada
- `ai/decisions.md`: Decisiones de diseño del contrato documentadas
- `ai/generations/openapi-initial.yaml`: Copia del contrato inicial generado

---

## Prompt 4: Configuración de OpenAPI Generator en Gradle

**Fecha**: 2025-01-27

**Contexto**: Necesidad de configurar el plugin OpenAPI Generator en build.gradle para generar automáticamente las interfaces de API y los DTOs desde el contrato OpenAPI 3.0, siguiendo el enfoque contract-first.

**Herramienta**: Cursor

**Prompt usado**:
```
En build.gradle, configura la tarea openApiGenerate:

- generatorName = 'spring'
- inputSpec   = "$rootDir/openapi/openapi.yaml"
- outputDir   = "$buildDir/generated"
- apiPackage  = 'com.bank.paymentinitiation.generated.api'
- modelPackage = 'com.bank.paymentinitiation.generated.model'
- invokerPackage = 'com.bank.paymentinitiation.generated.invoker'
- configOptions:
  - interfaceOnly = 'true'
  - useSpringBoot3 = 'true'
  - useTags = 'true'
  - dateLibrary = 'java8'
  - serializationLibrary = 'jackson'
  - hideGenerationTimestamp = 'true'

Añade la carpeta generada como source set:
- sourceSets.main.java.srcDir("$buildDir/generated/src/main/java")

Y haz que compileJava dependa de openApiGenerate.

Agrega dependencias adicionales para código generado:
- io.swagger.core.v3:swagger-annotations:2.2.21
- org.openapitools:jackson-databind-nullable:0.2.6
- jakarta.validation:jakarta.validation-api:3.0.2
- jakarta.annotation:jakarta.annotation-api:2.1.1
```

**Respuesta de la IA**:
La IA configuró correctamente:

1. **Tarea openApiGenerate**:
   - Generator: 'spring' (genera código Spring Boot)
   - Input: `$rootDir/openapi/openapi.yaml`
   - Output: `$buildDir/generated`
   - Paquetes: api, model, invoker en `com.bank.paymentinitiation.generated.*`
   - ConfigOptions: interfaceOnly, useSpringBoot3, useTags, dateLibrary java8, jackson, hideGenerationTimestamp

2. **Source Sets**:
   - Agregado `$buildDir/generated/src/main/java` como sourceDir del sourceSet main

3. **Dependencias**:
   - Dependencias agregadas en la sección dependencies

4. **Dependencias de tareas**:
   - `compileJava` configurado para depender de `openApiGenerate`

**Resultado de la generación**:
- ✅ `./gradlew openApiGenerate` ejecutado exitosamente
- ✅ Generados archivos en `build/generated/src/main/java/com/bank/paymentinitiation/generated/`:
  - `api/PaymentOrdersApi.java`: Interfaz con los 3 métodos (initiatePaymentOrder, retrievePaymentOrder, retrievePaymentOrderStatus)
  - `model/*.java`: 8 clases de modelo (Account, InitiatePaymentOrderRequest, InitiatePaymentOrderResponse, PaymentAmount, PaymentOrderStatus, PaymentOrderStatusResponse, ProblemDetail, RetrievePaymentOrderResponse)
  - `api/ApiUtil.java`: Utilidades para la API

- ✅ `./gradlew compileJava` ejecutado exitosamente
- ✅ Código generado compila correctamente sin errores

**Correcciones manuales aplicadas**:
- Ninguna. La configuración fue generada correctamente y funcionó desde el primer intento.

**Archivos generados/modificados**:
- `build.gradle`: Configuración de openApiGenerate y dependencias agregadas
- `build/generated/src/main/java/com/bank/paymentinitiation/generated/`: Código generado automáticamente
- `ai/prompts.md`: Esta entrada documentada
- `ai/decisions.md`: Decisiones sobre dependencias documentadas

---

## Prompt 5: Creación de Estructura de Paquetes Hexagonal

**Fecha**: 2025-01-27

**Contexto**: Necesidad de crear la estructura completa de paquetes siguiendo arquitectura hexagonal (Ports & Adapters) para organizar el código del microservicio de manera clara y mantenible.

**Herramienta**: Cursor

**Prompt usado**:
```
Crea la siguiente estructura de paquetes bajo com.bank.paymentinitiation:

- domain
  - model
  - port.in
  - port.out
  - exception
  - service
- application
  - service
  - mapper (opcional)
- adapter.in.rest
  - dto (los modelos generados van en generated.model, aquí solo wrappers si los necesitas)
  - mapper
- adapter.out.persistence
  - entity
  - jpa
  - mapper
  - PaymentOrderRepositoryAdapter
- config

Crea clases vacías (o interfaces) con javadoc describiendo su rol, sin meter aún mucha lógica.
```

**Respuesta de la IA**:
La IA creó la estructura completa de paquetes con clases/interfaces base:

**Domain Layer** (5 clases/interfaces):
- `domain/model/PaymentOrder.java`: Aggregate root
- `domain/model/PaymentOrderReference.java`: Value object
- `domain/model/PaymentOrderStatus.java`: Enum
- `domain/port/in/InitiatePaymentOrderUseCase.java`: Input port
- `domain/port/in/RetrievePaymentOrderUseCase.java`: Input port
- `domain/port/in/RetrievePaymentOrderStatusUseCase.java`: Input port
- `domain/port/out/PaymentOrderRepository.java`: Output port
- `domain/exception/DomainException.java`: Base exception
- `domain/exception/PaymentOrderNotFoundException.java`: Domain exception
- `domain/exception/InvalidPaymentException.java`: Domain exception
- `domain/service/PaymentOrderDomainService.java`: Domain service

**Application Layer** (3 clases):
- `application/service/InitiatePaymentOrderService.java`: Use case implementation
- `application/service/RetrievePaymentOrderService.java`: Use case implementation
- `application/service/RetrievePaymentOrderStatusService.java`: Use case implementation

**Adapter In (REST)** (2 clases):
- `adapter/in/rest/PaymentOrdersController.java`: REST controller
- `adapter/in/rest/mapper/PaymentOrderRestMapper.java`: MapStruct mapper interface

**Adapter Out (Persistence)** (4 clases/interfaces):
- `adapter/out/persistence/entity/PaymentOrderEntity.java`: JPA entity
- `adapter/out/persistence/jpa/PaymentOrderJpaRepository.java`: Spring Data JPA repository
- `adapter/out/persistence/mapper/PaymentOrderPersistenceMapper.java`: MapStruct mapper interface
- `adapter/out/persistence/PaymentOrderRepositoryAdapter.java`: Repository adapter implementation

**Config** (1 clase):
- `config/ApplicationConfig.java`: Spring configuration

**Características**:
- Todas las clases/interfaces incluyen JavaDoc descriptivo
- Estructura clara siguiendo principios de arquitectura hexagonal
- Separación clara de responsabilidades
- Domain layer sin dependencias de framework
- TODO comments indicando qué implementar

**Correcciones manuales aplicadas**:
- Ninguna. La estructura fue generada correctamente según los principios de arquitectura hexagonal.

**Archivos generados/modificados**:
- 20 archivos Java creados con estructura completa
- `ai/prompts.md`: Esta entrada documentada
- `ai/decisions.md`: Decisiones sobre arquitectura hexagonal documentadas

---

## Prompt 6: Implementación del Modelo de Dominio

**Fecha**: 2025-01-27

**Contexto**: Necesidad de implementar el modelo de dominio completo con value objects, enum de estados, y aggregate root PaymentOrder con métodos de negocio.

**Herramienta**: Cursor

**Prompt usado**:
```
En com.bank.paymentinitiation.domain.model crea:

- Enum PaymentStatus con valores: INITIATED, PENDING, PROCESSED, COMPLETED, FAILED, CANCELLED.
- Value object PaymentAmount (BigDecimal value, String currency) con factoría estática que valide que value > 0.
- Value objects PayerReference, PayeeReference, ExternalReference (strings no vacíos).
- Aggregate root PaymentOrder con campos:
  - String paymentOrderReference
  - ExternalReference externalReference
  - PayerReference payerReference
  - PayeeReference payeeReference
  - PaymentAmount instructedAmount
  - String remittanceInformation
  - LocalDate requestedExecutionDate
  - PaymentStatus status
  - LocalDateTime createdAt
  - LocalDateTime updatedAt

Incluye métodos de dominio para:
- validar el agregado (validate())
- iniciar la orden (marcar INITIATED)
- cambiar estado respetando una secuencia razonable (por ejemplo INITIATED → PENDING → PROCESSED → COMPLETED).

No uses anotaciones de Spring en el dominio. Usa Lombok (@Value/@Builder) cuando tenga sentido.
```

**Respuesta de la IA**:
La IA implementó el modelo de dominio completo:

**Enum creado**:
- `PaymentStatus.java`: Enum con 6 valores (INITIATED, PENDING, PROCESSED, COMPLETED, FAILED, CANCELLED)

**Value Objects creados** (5):
- `PaymentAmount.java`: Value object con BigDecimal value y String currency, validación value > 0
- `PayerReference.java`: Value object con validación de string no vacío
- `PayeeReference.java`: Value object con validación de string no vacío
- `ExternalReference.java`: Value object con validación de string no vacío
- `PaymentOrderReference.java`: Value object con validación de patrón PO-[A-Z0-9-]+

**Aggregate Root**:
- `PaymentOrder.java`: Aggregate root completo con:
  - Todos los campos solicitados
  - Método `validate()`: Valida que todos los campos requeridos estén presentes
  - Método `initiate()`: Marca la orden como INITIATED
  - Método `changeStatus()`: Cambia el estado respetando transiciones válidas

**Características implementadas**:
- Todos los value objects usan Lombok `@Value` para inmutabilidad
- PaymentOrder usa Lombok `@Builder` con `toBuilder = true` para permitir copias inmutables
- Validaciones en constructores de value objects
- Validación de transiciones de estado en PaymentOrder
- Sin anotaciones de Spring (framework-agnostic)
- Factory methods estáticos para crear value objects

**Transiciones de estado validadas**:
- INITIATED → PENDING, CANCELLED
- PENDING → PROCESSED, CANCELLED, FAILED
- PROCESSED → COMPLETED, FAILED
- COMPLETED, FAILED, CANCELLED son estados terminales (no permiten transiciones)

**Correcciones manuales aplicadas**:
- Se completó `PaymentOrderReference` con validación de patrón regex
- Se mejoró la documentación de transiciones de estado en el enum PaymentStatus

**Archivos generados/modificados**:
- `domain/model/PaymentStatus.java`: Enum creado
- `domain/model/PaymentAmount.java`: Value object creado
- `domain/model/PayerReference.java`: Value object creado
- `domain/model/PayeeReference.java`: Value object creado
- `domain/model/ExternalReference.java`: Value object creado
- `domain/model/PaymentOrder.java`: Aggregate root implementado completamente
- `domain/model/PaymentOrderReference.java`: Value object completado
- `ai/prompts.md`: Esta entrada documentada
- `ai/decisions.md`: Decisiones sobre modelo de dominio documentadas

---

## Prompt 7: Implementación de Puertos y Servicios de Aplicación

**Fecha**: 2025-01-27

**Contexto**: Necesidad de actualizar las interfaces de puertos (ports in/out) y implementar los servicios de aplicación que orquestan los casos de uso.

**Herramienta**: Cursor

**Prompt usado**:
```
En domain.port.in crea interfaces:

- InitiatePaymentOrderUseCase
  - PaymentOrder initiate(PaymentOrder order);
- RetrievePaymentOrderUseCase
  - PaymentOrder retrieve(String paymentOrderReference);
- RetrievePaymentOrderStatusUseCase
  - PaymentStatus retrieveStatus(String paymentOrderReference);

En domain.port.out crea:
- PaymentOrderRepository
  - PaymentOrder save(PaymentOrder order);
  - Optional<PaymentOrder> findByReference(String paymentOrderReference);

En application.service crea implementaciones de estos casos de uso que:
- Inyectan PaymentOrderRepository (port out).
- Aplican reglas de negocio básicas y delegan en el repositorio.
- Lanzan excepciones de dominio (PaymentOrderNotFoundException, InvalidPaymentException) cuando aplique.
```

**Respuesta de la IA**:
La IA actualizó las interfaces y implementó los servicios de aplicación:

**Puertos actualizados**:
- `domain/port/in/InitiatePaymentOrderUseCase.java`: Ya tenía la firma correcta
- `domain/port/in/RetrievePaymentOrderUseCase.java`: Actualizado para usar String en lugar de PaymentOrderReference
- `domain/port/in/RetrievePaymentOrderStatusUseCase.java`: Actualizado para usar String y PaymentStatus
- `domain/port/out/PaymentOrderRepository.java`: Actualizado para usar String, eliminado existsByReference

**Servicios de aplicación implementados** (3):
- `InitiatePaymentOrderService.java`: 
  - Valida el payment order
  - Genera referencia si no existe
  - Inicia la orden (status INITIATED)
  - Persiste y retorna
- `RetrievePaymentOrderService.java`:
  - Valida que la referencia no sea null/vacía
  - Busca en el repositorio
  - Lanza PaymentOrderNotFoundException si no existe
- `RetrievePaymentOrderStatusService.java`:
  - Valida que la referencia no sea null/vacía
  - Busca en el repositorio
  - Lanza PaymentOrderNotFoundException si no existe
  - Retorna solo el status

**Domain Service actualizado**:
- `PaymentOrderDomainService.java`:
  - Método `validate()`: Valida fecha de ejecución no en el pasado
  - Método `generateReference()`: Genera referencia única con formato PO-UUID

**Excepción actualizada**:
- `PaymentOrderNotFoundException.java`: Actualizada para usar String en lugar de PaymentOrderReference

**Características implementadas**:
- Validaciones de negocio en servicios de aplicación
- Manejo de excepciones de dominio
- Delegación al repositorio para persistencia
- Generación de referencias únicas
- Validación de fechas de ejecución

**Correcciones manuales aplicadas**:
- Se actualizó PaymentOrderRepositoryAdapter para usar String en lugar de PaymentOrderReference
- Se eliminó el método existsByReference del adaptador (ya no está en la interfaz)

**Archivos generados/modificados**:
- `domain/port/in/RetrievePaymentOrderUseCase.java`: Actualizado
- `domain/port/in/RetrievePaymentOrderStatusUseCase.java`: Actualizado
- `domain/port/out/PaymentOrderRepository.java`: Actualizado
- `application/service/InitiatePaymentOrderService.java`: Implementado completamente
- `application/service/RetrievePaymentOrderService.java`: Implementado completamente
- `application/service/RetrievePaymentOrderStatusService.java`: Implementado completamente
- `domain/service/PaymentOrderDomainService.java`: Implementado completamente
- `domain/exception/PaymentOrderNotFoundException.java`: Actualizado
- `adapter/out/persistence/PaymentOrderRepositoryAdapter.java`: Actualizado
- `ai/prompts.md`: Esta entrada documentada
- `ai/decisions.md`: Decisiones sobre puertos y servicios documentadas

---

## Prompt 8: Configuración de H2 y Entidad JPA

**Fecha**: 2025-01-27

**Contexto**: Necesidad de configurar H2 en memoria para persistencia y crear la entidad JPA PaymentOrderEntity que mapee el modelo de dominio a la base de datos.

**Herramienta**: Cursor

**Prompt usado**:
```
Configura application.yml para H2 en memoria:

spring:
  datasource:
    url: jdbc:h2:mem:paymentdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        format_sql: true

management:
  endpoints:
    web:
      exposure:
        include: health,info
      base-path: /actuator
  endpoint:
    health:
      show-details: when_authorized

En adapter.out.persistence.entity crea PaymentOrderEntity con campos equivalentes al dominio, usando @Entity y @Table. Usa UUID como id técnico y paymentOrderReference como identificador de negocio único.

En adapter.out.persistence.jpa crea PaymentOrderJpaRepository extends JpaRepository<PaymentOrderEntity, UUID> con método:
- Optional<PaymentOrderEntity> findByPaymentOrderReference(String paymentOrderReference);
```

**Respuesta de la IA**:
La IA configuró H2 y creó la entidad JPA:

**application.yml actualizado**:
- Configuración de H2 en memoria con DB_CLOSE_DELAY=-1 y DB_CLOSE_ON_EXIT=FALSE
- Consola H2 habilitada en /h2-console
- JPA configurado con ddl-auto: update
- Actuator configurado con health e info endpoints

**PaymentOrderEntity creada**:
- UUID como ID técnico (primary key)
- paymentOrderReference como identificador de negocio único (con índice único)
- Todos los campos equivalentes al dominio:
  - paymentOrderReference, externalReference, payerReference, payeeReference
  - amount, currency (flattened from PaymentAmount)
  - remittanceInformation, requestedExecutionDate
  - status (PaymentOrderStatusEntity enum)
  - createdAt, updatedAt
- Callbacks @PrePersist y @PreUpdate para timestamps automáticos
- Lombok @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor

**PaymentOrderStatusEntity enum creado**:
- Enum separado en el paquete entity
- Valores: INITIATED, PENDING, PROCESSED, COMPLETED, FAILED, CANCELLED
- Mantiene independencia entre capa de persistencia y dominio

**PaymentOrderJpaRepository**:
- Ya tenía el método findByPaymentOrderReference
- Extiende JpaRepository<PaymentOrderEntity, UUID>

**Características implementadas**:
- Campos aplanados de value objects (payerReference.value → payerReference)
- Status como enum separado en persistencia
- UUID para ID técnico
- paymentOrderReference como identificador de negocio único
- Índice único en paymentOrderReference
- Timestamps automáticos con callbacks JPA

**Correcciones manuales aplicadas**:
- Se creó PaymentOrderStatusEntity como enum separado (no dentro de PaymentOrderEntity)
- Se agregó índice único en paymentOrderReference en @Table

**Archivos generados/modificados**:
- `src/main/resources/application.yml`: Configuración H2 y Actuator actualizada
- `adapter/out/persistence/entity/PaymentOrderEntity.java`: Entidad JPA completa creada
- `adapter/out/persistence/entity/PaymentOrderStatusEntity.java`: Enum creado
- `adapter/out/persistence/jpa/PaymentOrderJpaRepository.java`: Ya tenía el método requerido
- `ai/prompts.md`: Esta entrada documentada
- `ai/decisions.md`: Decisiones sobre H2 y entidad JPA documentadas

---

## Prompt 9: Configuración de MapStruct y Creación de Mappers

**Fecha**: 2025-01-27

**Contexto**: Necesidad de configurar MapStruct y crear mappers para transformaciones entre DTOs, dominio y entidades JPA.

**Herramienta**: Cursor

**Prompt usado**:
```
Configura MapStruct usando:
- implementation "org.mapstruct:mapstruct:1.5.5.Final"
- annotationProcessor "org.mapstruct:mapstruct-processor:1.5.5.Final"

Crea en adapter.in.rest.mapper:
- PaymentOrderRestMapper con métodos:
  - PaymentOrder toDomain(InitiatePaymentOrderRequest request);
  - InitiatePaymentOrderResponse toInitiateResponse(PaymentOrder domain);
  - RetrievePaymentOrderResponse toRetrieveResponse(PaymentOrder domain);
  - PaymentOrderStatusResponse toStatusResponse(PaymentOrder domain);

Crea en adapter.out.persistence.mapper:
- PaymentOrderPersistenceMapper con métodos:
  - PaymentOrderEntity toEntity(PaymentOrder domain);
  - PaymentOrder toDomain(PaymentOrderEntity entity);

Usa @Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR).

⚠️ IMPORTANTE - Evitar ambigüedades de tipos:
- En los mappers MapStruct, usa nombres completamente calificados (fully qualified names) para tipos 
  que tienen el mismo nombre en diferentes paquetes (ej: PaymentAmount, PaymentStatus en dominio vs generated.model).
- Para métodos `toDomain(InitiatePaymentOrderRequest request, String paymentOrderReference)`, 
  el mapper debe recibir el `paymentOrderReference` como parámetro adicional porque se genera 
  en el controlador, no en el mapper.

⚠️ Conversión de timestamps:
- Agrega métodos @Named para convertir LocalDateTime → OffsetDateTime en mappers REST, 
  ya que los DTOs de OpenAPI usan OffsetDateTime pero el dominio usa LocalDateTime.
```

**Respuesta de la IA**:
La IA configuró MapStruct y creó los mappers:

**MapStruct configurado**:
- Dependencias ya estaban en build.gradle (verificadas)
- Configuración @Mapper con componentModel = "spring" y unmappedTargetPolicy = ERROR

**PaymentOrderRestMapper creado**:
- Método `toDomain(InitiatePaymentOrderRequest request, String paymentOrderReference)`:
  - Recibe paymentOrderReference como parámetro adicional
  - Mapea Account.iban → PayerReference/PayeeReference (value objects)
  - Mapea PaymentAmount (generated) → PaymentAmount (domain) con conversión Double → BigDecimal
  - Ignora status, createdAt, updatedAt (se establecen en dominio)
- Método `toInitiateResponse(PaymentOrder domain)`:
  - Mapea PaymentStatus (domain) → PaymentOrderStatus (generated)
  - Mapea value objects → Account y PaymentAmount (generated)
  - Convierte LocalDateTime → OffsetDateTime
- Método `toRetrieveResponse(PaymentOrder domain)`: Similar a toInitiateResponse
- Método `toStatusResponse(PaymentOrder domain)`: Solo mapea reference, status y lastUpdated

**PaymentOrderPersistenceMapper creado**:
- Método `toEntity(PaymentOrder domain)`:
  - Aplana value objects a campos simples
  - Mapea PaymentStatus (domain) → PaymentOrderStatusEntity (persistence)
  - Ignora id (generado por JPA)
- Método `toDomain(PaymentOrderEntity entity)`:
  - Reconstruye value objects desde campos simples
  - Mapea PaymentOrderStatusEntity → PaymentStatus (domain)
  - Combina amount y currency en PaymentAmount

**Métodos @Named implementados**:
- Conversiones de value objects (String → ExternalReference, PayerReference, PayeeReference)
- Conversiones de Account ↔ value objects
- Conversiones de PaymentAmount (generated ↔ domain)
- Conversiones de PaymentStatus ↔ PaymentOrderStatus
- Conversión de timestamps: LocalDateTime → OffsetDateTime

**Características implementadas**:
- Nombres completamente calificados para PaymentAmount generado (evita ambigüedad)
- Métodos @Named para todas las conversiones complejas
- Conversión de timestamps con ZoneOffset.UTC
- Mapeo bidireccional completo

**Correcciones manuales aplicadas**:
- Se usaron nombres completamente calificados para `com.bank.paymentinitiation.generated.model.PaymentAmount` en métodos @Named
- Se agregó paymentOrderReference como parámetro adicional en toDomain
- Se implementaron todos los métodos @Named para conversiones

**Archivos generados/modificados**:
- `adapter/in/rest/mapper/PaymentOrderRestMapper.java`: Mapper REST completo creado
- `adapter/out/persistence/mapper/PaymentOrderPersistenceMapper.java`: Mapper de persistencia completo creado
- `build/generated/sources/annotationProcessor/.../PaymentOrderRestMapperImpl.java`: Implementación generada por MapStruct
- `build/generated/sources/annotationProcessor/.../PaymentOrderPersistenceMapperImpl.java`: Implementación generada por MapStruct
- `ai/prompts.md`: Esta entrada documentada
- `ai/decisions.md`: Decisiones sobre mappers MapStruct documentadas

---

## Prompt 10: Implementación de Controlador REST y Componentes de Soporte

**Fecha**: 2025-01-27

**Contexto**: Necesidad de implementar el controlador REST que expone los endpoints de la API y crear los componentes de soporte necesarios (generador de referencias, configuración de beans, adaptador de repositorio).

**Herramienta**: Cursor

**Prompt usado**:
```
Usando las interfaces generadas en com.bank.paymentinitiation.generated.api, crea PaymentOrdersController en adapter.in.rest que:

- Implemente PaymentOrdersApi (el nombre generado por OpenAPI).
- Inyecte los use cases, PaymentOrderRestMapper y PaymentOrderReferenceGenerator.
- Para POST /payment-initiation/payment-orders:
  - Reciba InitiatePaymentOrderRequest con @Valid.
  - Genere paymentOrderReference usando PaymentOrderReferenceGenerator.
  - Mapee DTO → dominio usando `mapper.toDomain(request, paymentOrderReference)`.
  - Llame InitiatePaymentOrderUseCase, mapee dominio → InitiatePaymentOrderResponse.
  - Devuelva ResponseEntity.status(HttpStatus.CREATED).body(response).
- Para GET /payment-initiation/payment-orders/{id}:
  - Llame RetrievePaymentOrderUseCase, mapee dominio → RetrievePaymentOrderResponse.
  - Devuelva ResponseEntity.ok(response).
- Para GET /payment-initiation/payment-orders/{id}/status:
  - Llame RetrievePaymentOrderUseCase (necesita orden completa para mapear a response con paymentOrderReference y lastUpdated).
  - Mapee dominio → PaymentOrderStatusResponse usando mapper.toStatusResponse().
  - Devuelva ResponseEntity.ok(response).

⚠️ IMPORTANTE - Servicios necesarios:

1. Crea PaymentOrderReferenceGenerator en application.service:
   - Componente Spring que genera referencias únicas en formato "PO-{UUID compacto}".

2. Crea ApplicationConfig en config/:
   - Clase de configuración Spring con @Configuration.
   - Define beans para InitiatePaymentOrderService, RetrievePaymentOrderService, RetrievePaymentOrderStatusService.

3. Completa PaymentOrderRepositoryAdapter:
   - Debe tener @Component para ser detectado por Spring.
   - Implementa los métodos save() y findByReference() usando PaymentOrderJpaRepository y PaymentOrderPersistenceMapper.
```

**Respuesta de la IA**:
La IA creó el controlador REST y todos los componentes de soporte:

**PaymentOrdersController creado**:
- Implementa `PaymentOrdersApi` (interfaz generada por OpenAPI)
- Inyecta: `InitiatePaymentOrderUseCase`, `RetrievePaymentOrderUseCase`, `PaymentOrderRestMapper`, `PaymentOrderReferenceGenerator`
- Endpoint POST `/payment-initiation/payment-orders`:
  - Valida request con `@Valid`
  - Genera referencia usando `PaymentOrderReferenceGenerator`
  - Mapea DTO → dominio
  - Llama use case
  - Mapea dominio → response
  - Retorna 201 CREATED
- Endpoint GET `/payment-initiation/payment-orders/{id}`:
  - Llama `RetrievePaymentOrderUseCase`
  - Mapea dominio → response
  - Retorna 200 OK
- Endpoint GET `/payment-initiation/payment-orders/{id}/status`:
  - Usa `RetrievePaymentOrderUseCase` (no `RetrievePaymentOrderStatusUseCase`) porque necesita orden completa
  - Mapea dominio → response
  - Retorna 200 OK
- Logging con `@Slf4j` para trazabilidad

**PaymentOrderReferenceGenerator creado**:
- Componente Spring (`@Component`)
- Método `generate()` que retorna String en formato "PO-{UUID compacto}"
- UUID sin guiones, convertido a mayúsculas
- Ejemplo: "PO-A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6"

**ApplicationConfig actualizado**:
- Clase de configuración Spring (`@Configuration`)
- Define 4 beans:
  - `PaymentOrderDomainService`: Recibe `PaymentOrderRepository`
  - `InitiatePaymentOrderService`: Recibe `PaymentOrderRepository` y `PaymentOrderDomainService`
  - `RetrievePaymentOrderService`: Recibe `PaymentOrderRepository`
  - `RetrievePaymentOrderStatusService`: Recibe `PaymentOrderRepository`
- Todos los beans reciben `PaymentOrderRepository` que es inyectado automáticamente por Spring (PaymentOrderRepositoryAdapter tiene `@Component`)

**PaymentOrderRepositoryAdapter completado**:
- Ya tenía `@Component`
- Método `save()`:
  - Convierte dominio → entidad usando mapper
  - Guarda entidad usando JPA repository
  - Convierte entidad → dominio usando mapper
  - Retorna dominio
- Método `findByReference()`:
  - Busca entidad por referencia usando JPA repository
  - Convierte entidad → dominio si existe
  - Retorna Optional

**PaymentOrderDomainService actualizado**:
- Agregado constructor que recibe `PaymentOrderRepository`
- Necesario para que ApplicationConfig pueda crear el bean

**Características implementadas**:
- Controlador REST completo con 3 endpoints
- Generación de referencias únicas
- Configuración de beans para servicios de aplicación
- Adaptador de repositorio completamente funcional
- Logging para trazabilidad
- Validación de requests con `@Valid`

**Correcciones manuales aplicadas**:
- Se agregó constructor a `PaymentOrderDomainService` para recibir `PaymentOrderRepository`
- Se actualizó `ApplicationConfig` para crear todos los beans necesarios

**Archivos generados/modificados**:
- `adapter/in/rest/PaymentOrdersController.java`: Controlador REST completo creado
- `application/service/PaymentOrderReferenceGenerator.java`: Generador de referencias creado
- `config/ApplicationConfig.java`: Configuración de beans actualizada
- `adapter/out/persistence/PaymentOrderRepositoryAdapter.java`: Adaptador completado
- `domain/service/PaymentOrderDomainService.java`: Constructor agregado
- `ai/prompts.md`: Esta entrada documentada
- `ai/decisions.md`: Decisiones sobre controlador y componentes documentadas

---

## Prompt 11: Implementación de GlobalExceptionHandler

**Fecha**: 2025-01-27

**Contexto**: Necesidad de implementar manejo global de excepciones que convierta excepciones de dominio a respuestas HTTP siguiendo RFC 7807 (Problem Details for HTTP APIs).

**Herramienta**: Cursor

**Prompt usado**:
```
Crea en adapter.in.rest un @RestControllerAdvice GlobalExceptionHandler que maneje:

- PaymentOrderNotFoundException → 404 NOT FOUND, body ProblemDetail con title "Payment Order Not Found".
- InvalidPaymentException → 400 BAD REQUEST, body ProblemDetail con title "Invalid Payment Order".
- Exception genérica → 500 INTERNAL SERVER ERROR.

Usa org.springframework.http.ProblemDetail (Spring 6+). Todos los errores deben tener content-type application/problem+json.
```

**Respuesta de la IA**:
La IA creó el GlobalExceptionHandler:

**GlobalExceptionHandler creado**:
- Anotado con `@RestControllerAdvice` y `@Slf4j`
- 3 métodos de manejo de excepciones:

1. **handlePaymentOrderNotFound(PaymentOrderNotFoundException)**:
   - Mapea a HTTP 404 NOT FOUND
   - Crea ProblemDetail con:
     - title: "Payment Order Not Found"
     - status: 404
     - detail: Mensaje de la excepción
     - property: paymentOrderReference (incluido como propiedad adicional)
   - Logging a nivel WARN

2. **handleInvalidPayment(InvalidPaymentException)**:
   - Mapea a HTTP 400 BAD REQUEST
   - Crea ProblemDetail con:
     - title: "Invalid Payment Order"
     - status: 400
     - detail: Mensaje de la excepción
   - Logging a nivel WARN

3. **handleGenericException(Exception)**:
   - Mapea a HTTP 500 INTERNAL SERVER ERROR
   - Crea ProblemDetail con:
     - title: "Internal Server Error"
     - status: 500
     - detail: Mensaje genérico (no expone detalles internos)
   - Logging a nivel ERROR con stack trace

**Características implementadas**:
- Usa `org.springframework.http.ProblemDetail` (Spring 6+)
- Content-type `application/problem+json` (automático con ProblemDetail en Spring 6+)
- Mapeo de excepciones de dominio a códigos HTTP apropiados
- Logging diferenciado por nivel de severidad
- No expone detalles internos en excepciones genéricas
- Propiedades adicionales en ProblemDetail (paymentOrderReference)

**Correcciones manuales aplicadas**:
- Ninguna. El handler fue creado correctamente según las especificaciones.

**Archivos generados/modificados**:
- `adapter/in/rest/GlobalExceptionHandler.java`: Handler global de excepciones creado
- `ai/prompts.md`: Esta entrada documentada
- `ai/decisions.md`: Decisiones sobre manejo de errores RFC 7807 documentadas

---

## Prompt: Tests Unitarios con Mejores Prácticas

**Fecha**: 2025-11-20
**Contexto**: Crear tests unitarios siguiendo buenas prácticas de testing para el microservicio Payment Initiation.

**Prompt usado**:
```
Crea tests unitarios siguiendo buenas prácticas de testing para el microservicio Payment Initiation.

⚠️ MUY IMPORTANTE – REGLA OBLIGATORIA PARA LEER LOGS
[... prompt completo con especificaciones detalladas ...]
```

**Resumen de tests generados**:

### PaymentOrderTest (34 tests)
- **Validaciones de creación**: 10 tests
  - Creación exitosa con todos los campos válidos
  - Falla cuando paymentOrderReference es null/blank
  - Falla cuando externalReference es null
  - Falla cuando payerReference es null
  - Falla cuando payeeReference es null
  - Falla cuando instructedAmount es null
  - Falla cuando requestedExecutionDate es null
  - Falla cuando createdAt es null
  - Falla cuando status es null
- **Reglas de negocio / transiciones de estado**: 20 tests
  - Transiciones válidas: 7 tests (INITIATED→PENDING/CANCELLED, PENDING→PROCESSED/FAILED/CANCELLED, PROCESSED→COMPLETED/FAILED)
  - Transiciones inválidas: 4 tests (INITIATED→PROCESSED, terminal states)
  - Casos especiales: 3 tests (updatedAt, mismo status, null)
  - Método initiate(): 2 tests
- **Helpers**: createValidPaymentOrder(), createValidPaymentOrderBuilder()

### InitiatePaymentOrderServiceTest (4 tests)
- shouldInitiatePaymentOrderSuccessfully(): Verifica iniciación exitosa
- shouldUseExistingPaymentOrderReference(): Verifica uso de referencia existente
- shouldThrowExceptionWhenValidationFails(): Verifica excepción cuando validación falla
- shouldNotSaveWhenOrderInvalid(): Verifica que no se guarda cuando el order es inválido

### RetrievePaymentOrderServiceTest (4 tests)
- shouldRetrievePaymentOrderSuccessfully(): Verifica recuperación exitosa
- shouldThrowWhenOrderNotFound(): Verifica PaymentOrderNotFoundException
- shouldThrowWhenReferenceIsNullOrBlank(): Verifica IllegalArgumentException para null
- shouldThrowWhenReferenceIsBlank(): Verifica IllegalArgumentException para blank

### RetrievePaymentOrderStatusServiceTest (4 tests)
- shouldRetrievePaymentOrderStatusSuccessfully(): Verifica recuperación de status exitosa
- shouldThrowWhenOrderNotFound(): Verifica PaymentOrderNotFoundException
- shouldThrowWhenReferenceIsNull(): Verifica IllegalArgumentException para null
- shouldThrowWhenReferenceIsBlank(): Verifica IllegalArgumentException para blank

### PaymentOrderRestMapperTest (4 tests)
- shouldMapRequestToDomain(): Verifica mapeo InitiatePaymentOrderRequest → PaymentOrder
- shouldMapDomainToInitiateResponse(): Verifica mapeo PaymentOrder → InitiatePaymentOrderResponse
- shouldMapDomainToRetrieveResponse(): Verifica mapeo PaymentOrder → RetrievePaymentOrderResponse
- shouldMapDomainToStatusResponse(): Verifica mapeo PaymentOrder → PaymentOrderStatusResponse

### PaymentOrderPersistenceMapperTest (3 tests)
- shouldMapDomainToEntity(): Verifica aplanamiento de value objects (payerReference.value → payerReference String)
- shouldMapEntityToDomain(): Verifica reconstrucción de value objects (payerReference String → PayerReference)
- shouldMapAllStatuses(): Verifica conversión bidireccional de todos los valores de PaymentStatus

**Total**: 53 tests unitarios

**Correcciones manuales aplicadas**:
- Todos los tests usan patrón AAA (Arrange-Act-Assert)
- Helpers y builders para evitar código repetido
- @DisplayName con nombres descriptivos en lenguaje natural
- AssertJ para aserciones fluidas
- Mockito con @ExtendWith(MockitoExtension.class) para servicios
- @SpringBootTest solo para mappers que requieren contexto Spring

**Archivos generados/modificados**:
- `test/domain/model/PaymentOrderTest.java`: 34 tests para aggregate
- `test/application/service/InitiatePaymentOrderServiceTest.java`: 4 tests para servicio de iniciación
- `test/application/service/RetrievePaymentOrderServiceTest.java`: 4 tests para servicio de recuperación
- `test/application/service/RetrievePaymentOrderStatusServiceTest.java`: 4 tests para servicio de status
- `test/adapter/in/rest/mapper/PaymentOrderRestMapperTest.java`: 4 tests para mapper REST
- `test/adapter/out/persistence/mapper/PaymentOrderPersistenceMapperTest.java`: 3 tests para mapper de persistencia
- `ai/prompts.md`: Esta entrada documentada
- `ai/decisions.md`: Decisiones sobre estrategia de testing documentadas

---

## Prompt: Tests de Integración con WebTestClient

**Fecha**: 2025-11-20
**Contexto**: Crear tests de integración usando Spring Boot Test + WebTestClient para verificar que los endpoints REST funcionen de extremo a extremo con H2 real, el contrato OpenAPI y la colección Postman.

**Prompt usado**:
```
Configura y crea tests de integración usando Spring Boot Test + WebTestClient, asegurando que los endpoints REST funcionen de extremo a extremo con H2 real, el contrato OpenAPI y la colección Postman.

⚠️ MUY IMPORTANTE – REGLA OBLIGATORIA PARA LEER LOGS (NO GREP / NO TAIL)
[... prompt completo con especificaciones de tests ...]
```

**Resumen de respuesta de IA**:
- Se creó la clase `PaymentInitiationIntegrationTest` con `@SpringBootTest(webEnvironment = RANDOM_PORT)` y `@AutoConfigureWebTestClient`
- Se implementaron 10 tests de integración cubriendo casos exitosos y de error
- Se corrigieron múltiples problemas identificados en los logs completos (sin filtros)

**Correcciones manuales realizadas**:
1. **Error de validación de currency**: Eliminado `pattern` de `currency` en OpenAPI (conflicto con `enum`)
2. **Error de orden de validación**: Cambiado orden en `InitiatePaymentOrderService` (initiate antes de validar)
3. **Error de UUID en PaymentOrderReference**: Corregido `generateReference()` para eliminar guiones
4. **Error de IBANs en tests**: Cambiados a IBANs válidos (≥15 caracteres)
5. **Handlers de excepciones**: Agregados handlers para `MethodArgumentNotValidException` y `HttpMessageNotReadableException`
6. **Corrección de fechas en tests**: Ajustadas aserciones para coincidir con fechas enviadas

**Archivos generados/modificados**:
- `adapter/in/rest/PaymentInitiationIntegrationTest.java`: 10 tests de integración
- `adapter/in/rest/GlobalExceptionHandler.java`: Handlers adicionales para validaciones
- `application/service/InitiatePaymentOrderService.java`: Orden de validación corregido
- `domain/service/PaymentOrderDomainService.java`: UUID sin guiones
- `openapi/openapi.yaml`: Eliminado pattern de currency
- `ai/prompts.md`: Esta entrada documentada
- `ai/decisions.md`: Decisiones sobre tests de integración documentadas
