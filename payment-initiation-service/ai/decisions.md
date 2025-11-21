# Decisiones de Diseño y Correcciones Manuales

Este archivo documenta las decisiones de diseño, correcciones manuales aplicadas, trade-offs y razonamientos técnicos durante el desarrollo del microservicio Payment Initiation.

## Formato de Documentación

Para cada decisión o corrección, se documentará:
- **Fecha/Etapa**: Cuándo se tomó la decisión
- **Paso del Playbook**: A qué paso corresponde (si aplica)
- **Decisión/Problema**: Qué se decidió o qué problema se corrigió
- **Razón/Trade-off**: Por qué se tomó esa decisión o qué alternativas se consideraron
- **Impacto**: Qué impacto tiene en el proyecto

---

## PASO 0 - Creación del Proyecto Base

### Decisión: Configuración de Checkstyle

**Fecha**: Inicio del proyecto  
**Paso**: PASO 0

**Decisión**:
- Usar configuración "media" de Checkstyle (no extremadamente restrictiva)
- Longitud de línea: 120 caracteres
- Orden de imports: java.* → jakarta.* → org.* → com.*
- LineLength fuera de TreeWalker (requisito técnico)

**Razón**:
- Una configuración muy restrictiva puede frenar el desarrollo
- 120 caracteres es un balance razonable entre legibilidad y flexibilidad
- El orden de imports debe seguir estándares Java y ser compatible con Checkstyle 10.12.5

**Impacto**:
- El código debe seguir estas convenciones para pasar las validaciones
- Facilita la consistencia del código en el equipo

---

### Corrección: Propiedad `sortStaticImports` en ImportOrder

**Fecha**: Inicio del proyecto  
**Paso**: PASO 0

**Problema**:
- Checkstyle fallaba con el error: "Property 'sortStaticImports' does not exist"

**Corrección aplicada**:
- Se eliminó la propiedad `sortStaticImports` del módulo ImportOrder en `checkstyle.xml`

**Razón**:
- La versión 10.12.5 de Checkstyle no soporta esta propiedad
- Los static imports se ordenan automáticamente según su grupo base (org.*, com.*, etc.)

**Impacto**:
- Los static imports seguirán siendo validados, pero sin la propiedad específica
- No afecta la funcionalidad, solo la configuración

---

### Corrección: Ubicación de `BeforeExecutionExclusionFileFilter`

**Fecha**: Inicio del proyecto  
**Paso**: PASO 0

**Problema**:
- Checkstyle fallaba con el error: "TreeWalker is not allowed as a parent of BeforeExecutionExclusionFileFilter"

**Corrección aplicada**:
- Se movieron los módulos `BeforeExecutionExclusionFileFilter` fuera de `TreeWalker`
- Se mantuvieron en el módulo `Checker` (nivel raíz)

**Razón**:
- `BeforeExecutionExclusionFileFilter` debe estar en el nivel de `Checker`, no dentro de `TreeWalker`
- Esto permite excluir archivos antes de que TreeWalker los procese

**Impacto**:
- Los archivos generados (código OpenAPI, MapStruct) se excluyen correctamente de las verificaciones

---

### Decisión: Configuración de OpenAPI Generator

**Fecha**: Inicio del proyecto  
**Paso**: PASO 0

**Decisión**:
- Configurar OpenAPI Generator en `build.gradle` desde el inicio
- Hacer que la tarea `openApiGenerate` solo se ejecute si `openapi.yaml` existe (`onlyIf`)
- No hacer que `compileJava` dependa de `openApiGenerate` hasta que el archivo exista

**Razón**:
- Permite tener la configuración lista desde el inicio
- Evita errores de compilación antes de crear el contrato OpenAPI (PASO 3)
- Facilita la transición cuando se cree el archivo

**Impacto**:
- El proyecto compila correctamente sin `openapi.yaml`
- Cuando se cree el archivo en el PASO 3, la generación funcionará automáticamente

---

### Decisión: Supresiones de Checkstyle para Clase Principal

**Fecha**: Inicio del proyecto  
**Paso**: PASO 0

**Decisión**:
- Agregar supresión de `HideUtilityClassConstructor` para clases `*Application.java`

**Razón**:
- Spring Boot requiere que la clase principal tenga un constructor público o por defecto
- Es un patrón estándar de Spring Boot, no una violación real de buenas prácticas
- El warning no bloquea el build (maxWarnings = 100), pero es mejor suprimirlo explícitamente

**Impacto**:
- Elimina warnings innecesarios en la clase principal
- Mantiene el código limpio y sin warnings evitables

---

## PASO 2 - Análisis del WSDL y XML Legacy

### Análisis del Servicio SOAP Legacy

**Fecha**: Análisis inicial  
**Paso**: PASO 2

**Archivos analizados**:
- `PaymentOrderService.wsdl`
- `SubmitPaymentOrderRequest.xml` / `SubmitPaymentOrderResponse.xml`
- `GetPaymentOrderStatusRequest.xml` / `GetPaymentOrderStatusResponse.xml`

---

#### 1) Operaciones SOAP Disponibles

El servicio legacy expone dos operaciones principales:

**a) SubmitPaymentOrder**
- **SOAP Action**: `submit`
- **Propósito**: Crear/iniciar una nueva orden de pago
- **Request**: `SubmitPaymentOrderRequest`
- **Response**: `SubmitPaymentOrderResponse`
- **Endpoint legacy**: `http://soap-mock:8081/legacy/payments`

**b) GetPaymentOrderStatus**
- **SOAP Action**: `status`
- **Propósito**: Consultar el estado actual de una orden de pago existente
- **Request**: `GetPaymentOrderStatusRequest`
- **Response**: `GetPaymentOrderStatusResponse`

---

#### 2) Estructuras de Datos Principales

**SubmitPaymentOrderRequest**:
- `externalId` (string, requerido): Identificador externo de la orden
- `debtorIban` (string, requerido): IBAN de la cuenta deudora
- `creditorIban` (string, requerido): IBAN de la cuenta acreedora
- `amount` (decimal, requerido): Monto de la orden de pago
- `currency` (string, requerido): Código de moneda (ej: USD, EUR)
- `remittanceInfo` (string, opcional): Información de remesa/referencia
- `requestedExecutionDate` (date, requerido): Fecha solicitada de ejecución

**SubmitPaymentOrderResponse**:
- `paymentOrderId` (string): Identificador único de la orden generado por el sistema
- `status` (string): Estado inicial de la orden (ej: "ACCEPTED")

**GetPaymentOrderStatusRequest**:
- `paymentOrderId` (string, requerido): Identificador de la orden a consultar

**GetPaymentOrderStatusResponse**:
- `paymentOrderId` (string): Identificador de la orden
- `status` (string): Estado actual de la orden (ej: "SETTLED")
- `lastUpdate` (dateTime): Fecha y hora de la última actualización

---

#### 3) Estados Posibles de la Orden de Pago

Basado en los ejemplos XML analizados, se identifican los siguientes estados:

- **ACCEPTED**: Estado inicial cuando se crea la orden (SubmitPaymentOrderResponse)
- **SETTLED**: Estado final cuando la orden ha sido liquidada/completada (GetPaymentOrderStatusResponse)

**Nota**: Estos son los únicos estados visibles en los ejemplos. El dominio BIAN Payment Initiation define un conjunto más completo de estados que se implementará en el modelo de dominio.

---

#### 4) Mapeo a BIAN Payment Initiation / PaymentOrder

**Mapeo de Campos**:

| Campo Legacy (SOAP) | Campo BIAN (REST) | Tipo BIAN | Notas |
|---------------------|-------------------|-----------|-------|
| `externalId` | `externalReference` | String | Identificador externo proporcionado por el cliente |
| `debtorIban` | `debtorAccount.iban` | String (objeto anidado) | IBAN de la cuenta deudora, estructurado como objeto Account |
| `creditorIban` | `creditorAccount.iban` | String (objeto anidado) | IBAN de la cuenta acreedora, estructurado como objeto Account |
| `amount` + `currency` | `instructedAmount.amount` + `instructedAmount.currency` | PaymentAmount (value object) | Monto y moneda combinados en un objeto estructurado |
| `remittanceInfo` | `remittanceInformation` | String | Información de remesa (opcional) |
| `requestedExecutionDate` | `requestedExecutionDate` | LocalDate | Fecha solicitada de ejecución (sin cambios) |
| `paymentOrderId` | `paymentOrderReference` / `paymentOrderId` | String | Identificador de negocio de la orden (en dominio: `paymentOrderReference`, en API: `paymentOrderId`) |
| `status` | `status` | PaymentStatus (enum) | Estado de la orden, mapeado a enum según BIAN |
| `lastUpdate` | `lastUpdate` / `updatedAt` | OffsetDateTime / LocalDateTime | Timestamp de última actualización |

**Mapeo de Operaciones**:

| Operación SOAP | Endpoint REST BIAN | Método HTTP | Notas |
|----------------|-------------------|-------------|-------|
| `SubmitPaymentOrder` | `POST /payment-initiation/payment-orders` | POST | Iniciar una nueva orden de pago |
| `GetPaymentOrderStatus` | `GET /payment-initiation/payment-orders/{id}/status` | GET | Consultar solo el estado |
| - | `GET /payment-initiation/payment-orders/{id}` | GET | **Nuevo**: Recuperar orden completa (no existía en SOAP) |

**Mapeo de Estados**:

| Estado Legacy | Estado BIAN (PaymentStatus) | Descripción |
|--------------|----------------------------|-------------|
| `ACCEPTED` | `INITIATED` | Orden creada y aceptada inicialmente |
| `SETTLED` | `COMPLETED` | Orden liquidada/completada exitosamente |

**Estados adicionales BIAN** (no presentes en legacy, pero necesarios para el dominio):
- `PENDING`: Orden pendiente de procesamiento
- `PROCESSED`: Orden procesada pero no completada
- `FAILED`: Orden fallida
- `CANCELLED`: Orden cancelada

---

#### Decisiones de Diseño Basadas en el Análisis

**1. Estructura de Account Objects**
- **Decisión**: Usar objetos anidados `debtorAccount` y `creditorAccount` con propiedad `iban`
- **Razón**: Alinea con el estándar BIAN y permite extensibilidad futura (puede agregarse más información de cuenta)
- **Impacto**: El contrato OpenAPI debe definir schemas `DebtorAccount` y `CreditorAccount`

**2. PaymentAmount como Value Object**
- **Decisión**: Combinar `amount` y `currency` en un objeto `instructedAmount`
- **Razón**: Sigue el patrón BIAN y permite validaciones conjuntas (ej: validar que amount > 0)
- **Impacto**: El dominio debe tener un value object `PaymentAmount` con validaciones

**3. PaymentOrderReference vs PaymentOrderId**
- **Decisión**: Usar `paymentOrderReference` en el dominio y `paymentOrderId` en la API REST
- **Razón**: El dominio debe usar terminología de negocio, mientras que la API puede usar términos más técnicos
- **Impacto**: Los mappers deben convertir entre ambos nombres

**4. Estados del Dominio**
- **Decisión**: Implementar el enum completo `PaymentStatus` con todos los estados BIAN, no solo los del legacy
- **Razón**: Permite transiciones de estado completas y alineación con BIAN
- **Impacto**: El modelo de dominio debe soportar: INITIATED, PENDING, PROCESSED, COMPLETED, FAILED, CANCELLED

**5. Endpoint Adicional de Recuperación Completa**
- **Decisión**: Agregar `GET /payment-initiation/payment-orders/{id}` además del endpoint de status
- **Razón**: El servicio SOAP solo permitía consultar status, pero REST debe permitir recuperar la orden completa
- **Impacto**: Se implementarán dos endpoints GET diferentes con diferentes respuestas

---

## PASO 3 - Diseño del Contrato OpenAPI 3.0

### Decisión: Estructura del Contrato OpenAPI

**Fecha**: Diseño del contrato  
**Paso**: PASO 3

**Decisión**:
- Usar OpenAPI 3.0.3 como versión del contrato
- Definir 3 endpoints: POST (initiate), GET /{id} (retrieve), GET /{id}/status (status)
- Usar operationIds descriptivos: `initiatePaymentOrder`, `retrievePaymentOrder`, `retrievePaymentOrderStatus`
- Incluir ejemplos en todos los schemas para facilitar testing

**Razón**:
- OpenAPI 3.0.3 es la versión estable y ampliamente soportada
- Los operationIds se usan para generar nombres de métodos en las interfaces
- Los ejemplos facilitan la comprensión y testing del API

**Impacto**:
- El código generado usará estos operationIds para nombrar métodos
- Los ejemplos servirán como referencia para tests y documentación

---

### Decisión: Validación de IBANs

**Fecha**: Diseño del contrato  
**Paso**: PASO 3

**Decisión**:
- IBANs con `minLength: 15` y `maxLength: 34`
- No usar `pattern` para validar formato IBAN (solo longitud)

**Razón**:
- Los IBANs reales tienen entre 15 y 34 caracteres según ISO 13616
- El Postman collection usa IBANs cortos de ejemplo, pero el contrato debe validar IBANs reales
- Validar el formato completo de IBAN con regex sería muy complejo y puede causar problemas

**Impacto**:
- Todos los tests y ejemplos deben usar IBANs de al menos 15 caracteres
- El Postman collection deberá actualizarse con IBANs válidos para testing

---

### Decisión: Currency como Enum sin Pattern

**Fecha**: Diseño del contrato  
**Paso**: PASO 3

**Decisión**:
- `currency` definido como `enum` con lista extensa de códigos ISO 4217
- NO usar `pattern` junto con `enum`

**Razón**:
- OpenAPI Generator crea un Java enum cuando se usa `enum`, y `@Pattern` no puede aplicarse a enums
- Usar solo `enum` evita errores de validación en el código generado
- La lista extensa de monedas permite soportar múltiples países

**Impacto**:
- El código generado tendrá un enum `Currency` con todos los valores definidos
- Agregar nuevas monedas requiere modificar el OpenAPI y regenerar código

---

### Decisión: PaymentOrderId con Pattern

**Fecha**: Diseño del contrato  
**Paso**: PASO 3

**Decisión**:
- `paymentOrderId` con `pattern: '^PO-[0-9]+$'`
- Aplicado en path parameters y en schemas de response

**Razón**:
- El análisis del WSDL mostró que los IDs siguen el formato `PO-{número}` (ej: PO-0001)
- El pattern asegura que solo se acepten IDs válidos
- Alinea con el formato esperado del servicio legacy

**Impacto**:
- El código generado validará automáticamente el formato del ID
- Los tests deben usar IDs que cumplan el pattern (ej: "PO-1234567890")

---

### Decisión: PaymentStatus Enum Completo

**Fecha**: Diseño del contrato  
**Paso**: PASO 3

**Decisión**:
- Enum `PaymentStatus` con todos los estados BIAN: INITIATED, PENDING, PROCESSED, COMPLETED, FAILED, CANCELLED
- No limitarse solo a los estados del legacy (ACCEPTED, SETTLED)

**Razón**:
- Permite transiciones de estado completas según BIAN
- Facilita la implementación del dominio con todas las transiciones necesarias
- Alinea completamente con el estándar BIAN Payment Initiation

**Impacto**:
- El modelo de dominio debe soportar todos estos estados
- Los mappers deben convertir entre estados legacy y estados BIAN

---

### Decisión: ProblemDetail según RFC 7807

**Fecha**: Diseño del contrato  
**Paso**: PASO 3

**Decisión**:
- Usar schema `ProblemDetail` según RFC 7807 para todos los errores
- Content-Type: `application/problem+json`
- Campos requeridos: `type`, `title`, `status`
- Campos opcionales: `detail`, `instance`

**Razón**:
- RFC 7807 es el estándar para manejo de errores HTTP
- Spring Boot 6+ tiene soporte nativo para ProblemDetail
- Facilita el manejo consistente de errores en toda la API

**Impacto**:
- El GlobalExceptionHandler debe retornar ProblemDetail
- Todos los errores (400, 404, 500) deben usar este formato

---

### Decisión: PaymentAmount con Minimum

**Fecha**: Diseño del contrato  
**Paso**: PASO 3

**Decisión**:
- `amount` con `minimum: 0.01` (no puede ser cero ni negativo)
- Tipo `number` (no `integer`) para permitir decimales

**Razón**:
- Las órdenes de pago deben tener un monto positivo
- Permite montos decimales (ej: 150.75)
- La validación en el contrato se refleja automáticamente en el código generado

**Impacto**:
- El código generado validará que amount >= 0.01
- Los tests deben usar montos válidos

---

### Decisión: Formato de Fechas

**Fecha**: Diseño del contrato  
**Paso**: PASO 3

**Decisión**:
- `requestedExecutionDate`: `type: string, format: date` (ISO 8601, ej: "2025-12-31")
- `lastUpdate`: `type: string, format: date-time` (ISO 8601, ej: "2025-10-30T16:25:30Z")

**Razón**:
- ISO 8601 es el estándar para fechas en APIs REST
- `date` para fechas sin hora, `date-time` para timestamps
- OpenAPI Generator mapea estos a `LocalDate` y `OffsetDateTime` en Java

**Impacto**:
- El código generado usará `LocalDate` y `OffsetDateTime`
- Los mappers deben convertir entre estos tipos y `LocalDateTime` del dominio

---

### Nota: Warning sobre Formato "decimal"

**Fecha**: Diseño del contrato  
**Paso**: PASO 3

**Observación**:
- OpenAPI Generator muestra warning: "Unknown `format` decimal detected for type `number`"
- El formato "decimal" no es estándar en OpenAPI, pero no causa errores

**Decisión**:
- Mantener `format: decimal` para claridad semántica
- El warning no afecta la funcionalidad, OpenAPI Generator lo maneja correctamente

**Impacto**:
- El código generado funciona correctamente
- El warning puede ignorarse o corregirse en el futuro si es necesario

---

## PASO 4 - Configuración de OpenAPI Generator

### Decisión: Consolidación de Configuración de OpenAPI Generator

**Fecha**: Configuración de generación de código  
**Paso**: PASO 4

**Problema**:
- Había dos bloques `tasks.named('openApiGenerate')` duplicados en `build.gradle`
- Uno tenía la configuración completa, otro tenía solo `onlyIf`
- `compileJava` no dependía explícitamente de `openApiGenerate`

**Corrección aplicada**:
- Se consolidaron ambos bloques en uno solo con toda la configuración
- Se eliminó el `onlyIf` (ya no es necesario, el archivo `openapi.yaml` existe)
- Se agregó `dependsOn 'openApiGenerate'` a `compileJava`

**Razón**:
- Evita duplicación y confusión en la configuración
- Asegura que el código se genere antes de compilar
- Simplifica el mantenimiento

**Impacto**:
- Cada vez que se ejecute `compileJava`, se generará automáticamente el código OpenAPI
- El código generado está disponible en `build/generated/src/main/java/`

---

### Decisión: Dependencias Adicionales para Código Generado

**Fecha**: Configuración de generación de código  
**Paso**: PASO 4

**Decisión**:
- Las dependencias adicionales ya estaban agregadas desde el PASO 0:
  - `io.swagger.core.v3:swagger-annotations:2.2.21`
  - `org.openapitools:jackson-databind-nullable:0.2.6`
  - `jakarta.validation:jakarta.validation-api:3.0.2`
  - `jakarta.annotation:jakarta.annotation-api:2.1.1`

**Razón**:
- Estas dependencias son requeridas por el código generado por OpenAPI Generator
- `swagger-annotations`: Anotaciones para documentación de API
- `jackson-databind-nullable`: Soporte para campos nullable en Jackson
- `jakarta.validation-api`: Anotaciones de validación (@NotNull, @Size, etc.)
- `jakarta.annotation-api`: Anotaciones básicas de Jakarta (@Nullable, etc.)

**Impacto**:
- El código generado compila correctamente sin errores de dependencias faltantes
- Las validaciones y anotaciones funcionan como se espera

---

### Decisión: Configuración de Source Sets

**Fecha**: Configuración de generación de código  
**Paso**: PASO 4

**Decisión**:
- Configurar `sourceSets.main.java.srcDir("$buildDir/generated/src/main/java")`
- Esto ya estaba configurado desde el PASO 0

**Razón**:
- Permite que Gradle reconozca el código generado como parte del source set principal
- El código generado se compila junto con el código fuente del proyecto

**Impacto**:
- El código generado está disponible para importar en el código del proyecto
- No es necesario agregar manualmente el directorio generado al classpath

---

### Decisión: ConfigOptions de OpenAPI Generator

**Fecha**: Configuración de generación de código  
**Paso**: PASO 4

**Decisión**:
- `interfaceOnly: 'true'`: Genera solo interfaces, no implementaciones
- `useSpringBoot3: 'true'`: Usa anotaciones y configuraciones de Spring Boot 3
- `useTags: 'true'`: Usa tags del OpenAPI para organizar endpoints
- `dateLibrary: 'java8'`: Usa tipos de fecha de Java 8+ (LocalDate, OffsetDateTime)
- `serializationLibrary: 'jackson'`: Usa Jackson para serialización JSON
- `hideGenerationTimestamp: 'true'`: No incluye timestamp en código generado (evita cambios innecesarios)

**Razón**:
- `interfaceOnly` permite implementar los controladores manualmente (arquitectura hexagonal)
- `useSpringBoot3` asegura compatibilidad con Spring Boot 3.x
- `dateLibrary: 'java8'` alinea con el uso de LocalDate/LocalDateTime en el dominio
- `hideGenerationTimestamp` evita cambios en git por regeneración

**Impacto**:
- Se generan interfaces que deben implementarse en los adaptadores REST
- Los tipos de fecha son compatibles con el dominio (LocalDate, OffsetDateTime)
- El código generado es estable y no cambia por regeneración

---

## PASO 5 - Estructura de Paquetes Hexagonal

### Decisión: Arquitectura Hexagonal (Ports & Adapters)

**Fecha**: Estructuración del proyecto  
**Paso**: PASO 5

**Decisión**:
- Usar arquitectura hexagonal (Ports & Adapters) para organizar el código
- Separar claramente dominio, aplicación y adaptadores
- El dominio es independiente de frameworks y tecnologías

**Razón**:
- La arquitectura hexagonal permite:
  - **Testabilidad**: El dominio puede probarse sin frameworks
  - **Mantenibilidad**: Separación clara de responsabilidades
  - **Flexibilidad**: Fácil cambiar adaptadores (REST → GraphQL, JPA → MongoDB, etc.)
  - **Alineación con BIAN**: El dominio representa el modelo de negocio puro

**Impacto**:
- El código está organizado en capas claramente definidas
- Los cambios en infraestructura no afectan el dominio
- Los tests del dominio no requieren Spring ni JPA

---

### Decisión: Organización de Paquetes

**Fecha**: Estructuración del proyecto  
**Paso**: PASO 5

**Decisión**:
- **Domain**: Contiene el modelo de negocio puro (agregados, value objects, puertos, excepciones)
- **Application**: Contiene los casos de uso (servicios que orquestan el dominio)
- **Adapter.in.rest**: Adaptadores de entrada (REST controllers, mappers REST)
- **Adapter.out.persistence**: Adaptadores de salida (JPA entities, repositorios, mappers)
- **Config**: Configuración de Spring

**Razón**:
- Sigue el patrón Ports & Adapters estándar
- Facilita la navegación del código
- Separa claramente las responsabilidades

**Impacto**:
- Los desarrolladores saben dónde encontrar cada componente
- El dominio está claramente separado de la infraestructura
- Los adaptadores pueden cambiarse sin afectar el dominio

---

### Decisión: Uso de package-info.java

**Fecha**: Estructuración del proyecto  
**Paso**: PASO 5

**Decisión**:
- Crear `package-info.java` para cada paquete con javadoc descriptivo
- Documentar el propósito de cada paquete y sus componentes principales

**Razón**:
- Facilita la comprensión de la estructura para nuevos desarrolladores
- Documenta el propósito de cada paquete sin necesidad de leer código
- Mejora la navegación en IDEs

**Impacto**:
- Cada paquete tiene documentación clara de su propósito
- Los desarrolladores entienden rápidamente dónde colocar nuevo código
- La documentación está integrada en el código fuente

---

### Decisión: Separación de Mappers

**Fecha**: Estructuración del proyecto  
**Paso**: PASO 5

**Decisión**:
- Mappers REST en `adapter.in.rest.mapper`
- Mappers de persistencia en `adapter.out.persistence.mapper`
- Mappers de aplicación opcionales en `application.mapper`

**Razón**:
- Cada adaptador tiene sus propios mappers para convertir entre su formato y el dominio
- Los mappers REST convierten DTOs ↔ Dominio
- Los mappers de persistencia convierten Dominio ↔ Entidades JPA

**Impacto**:
- Los mappers están cerca de donde se usan
- Facilita el mantenimiento y la comprensión
- Evita acoplamiento entre adaptadores

---

### Decisión: DTOs Opcionales en adapter.in.rest.dto

**Fecha**: Estructuración del proyecto  
**Paso**: PASO 5

**Decisión**:
- Los DTOs principales están en `generated.model` (generados por OpenAPI)
- `adapter.in.rest.dto` solo se usa para wrappers o DTOs adicionales no definidos en OpenAPI

**Razón**:
- El contrato OpenAPI es la fuente de verdad para los DTOs REST
- Solo se necesitan DTOs adicionales en casos especiales (wrappers, DTOs internos, etc.)

**Impacto**:
- La mayoría de los DTOs están en `generated.model`
- Solo se crean DTOs adicionales cuando es realmente necesario

---

## PASO 6 - Modelo de Dominio BIAN

### Decisión: Value Objects Inmutables

**Fecha**: Implementación del dominio  
**Paso**: PASO 6

**Decisión**:
- Todos los value objects (`PaymentAmount`, `ExternalReference`, `PayerReference`, `PayeeReference`) son inmutables
- Validación de invariantes en el constructor
- Implementación de `equals()`, `hashCode()` y `toString()`

**Razón**:
- Los value objects deben ser inmutables para garantizar la integridad del dominio
- La validación en el constructor asegura que nunca se creen objetos inválidos
- `equals()` y `hashCode()` permiten comparación por valor, no por referencia

**Impacto**:
- Los value objects no pueden modificarse después de crearse
- Cualquier cambio requiere crear una nueva instancia
- Garantiza la integridad de los datos en el dominio

---

### Decisión: PaymentAmount con Factoría Estática

**Fecha**: Implementación del dominio  
**Paso**: PASO 6

**Decisión**:
- `PaymentAmount` usa factoría estática `of()` en lugar de constructor público
- El constructor es privado
- La validación (value > 0) se hace en la factoría

**Razón**:
- La factoría estática es más expresiva y clara
- Permite validación centralizada
- Facilita futuras extensiones (ej: validación de moneda)

**Impacto**:
- Se debe usar `PaymentAmount.of(value, currency)` para crear instancias
- No se puede crear un `PaymentAmount` inválido (value <= 0)

---

### Decisión: PaymentOrder con Lombok @Value y @Builder

**Fecha**: Implementación del dominio  
**Paso**: PASO 6

**Decisión**:
- Usar Lombok `@Value` para inmutabilidad y generación automática de getters, equals, hashCode
- Usar `@Builder(toBuilder = true)` para permitir creación y modificación inmutables

**Razón**:
- `@Value` hace el agregado inmutable y genera código boilerplate automáticamente
- `toBuilder = true` permite crear nuevas instancias modificadas (patrón inmutabilidad)
- Reduce código boilerplate manteniendo la inmutabilidad

**Impacto**:
- PaymentOrder es inmutable (todos los campos son final)
- Los cambios de estado crean nuevas instancias usando `toBuilder()`
- Warnings de Checkstyle sobre VisibilityModifier (aceptables, son campos finales generados por Lombok)

---

### Decisión: Método initiate() Antes de validate()

**Fecha**: Implementación del dominio  
**Paso**: PASO 6

**Decisión**:
- El método `initiate()` establece `status = INITIATED` y `createdAt = LocalDateTime.now()`
- El método `validate()` requiere que `status` y `createdAt` sean no-null
- El orden correcto es: generar reference → `initiate()` → `validate()` → guardar

**Razón**:
- `validate()` verifica todas las invariantes, incluyendo status y createdAt
- Estos campos se establecen en `initiate()`, no en el constructor
- Esto permite crear PaymentOrder sin estado inicial y luego iniciarlo

**Impacto**:
- Los servicios de aplicación deben seguir el orden correcto
- No se puede validar un PaymentOrder antes de iniciarlo
- Facilita la creación de órdenes en dos pasos (crear → iniciar → validar → guardar)

---

### Decisión: Transiciones de Estado Validadas

**Fecha**: Implementación del dominio  
**Paso**: PASO 6

**Decisión**:
- El método `changeStatus()` valida las transiciones de estado
- Transiciones válidas:
  - INITIATED → PENDING, CANCELLED
  - PENDING → PROCESSED, FAILED, CANCELLED
  - PROCESSED → COMPLETED, FAILED
- Estados finales (COMPLETED, FAILED, CANCELLED) no permiten más cambios

**Razón**:
- Las transiciones de estado deben seguir reglas de negocio claras
- Los estados finales no deben cambiar (integridad del dominio)
- La validación previene estados inválidos

**Impacto**:
- No se pueden hacer transiciones inválidas (ej: INITIATED → COMPLETED)
- Los estados finales son realmente finales
- Cualquier intento de transición inválida lanza `IllegalStateException`

---

### Decisión: Enum PaymentStatus Completo

**Fecha**: Implementación del dominio  
**Paso**: PASO 6

**Decisión**:
- Enum `PaymentStatus` con todos los estados BIAN: INITIATED, PENDING, PROCESSED, COMPLETED, FAILED, CANCELLED
- No limitarse solo a los estados del legacy (ACCEPTED, SETTLED)

**Razón**:
- Alinea completamente con el estándar BIAN Payment Initiation
- Permite transiciones de estado completas
- Facilita la implementación de reglas de negocio complejas

**Impacto**:
- El dominio soporta todos los estados BIAN
- Los mappers deben convertir entre estados legacy y estados BIAN
- Facilita futuras extensiones del dominio

---

### Corrección: OperatorWrap en toString()

**Fecha**: Implementación del dominio  
**Paso**: PASO 6

**Problema**:
- Checkstyle mostraba warnings sobre OperatorWrap en métodos `toString()`
- El operador '+' debe estar en una nueva línea según la configuración

**Corrección aplicada**:
- Se ajustaron los métodos `toString()` en todos los value objects
- El operador '+' ahora está al inicio de la nueva línea

**Razón**:
- Cumple con las reglas de Checkstyle configuradas
- Mantiene la consistencia del código

**Impacto**:
- Los warnings de OperatorWrap fueron corregidos
- El código cumple con las reglas de estilo

---

## PASO 7 - Puertos de Dominio y Servicios de Aplicación

### Decisión: Separación de Puertos In/Out

**Fecha**: Implementación de casos de uso  
**Paso**: PASO 7

**Decisión**:
- **Puertos de entrada (port.in)**: Interfaces que definen los casos de uso (qué puede hacer la aplicación)
- **Puertos de salida (port.out)**: Interfaces que definen cómo el dominio persiste datos (repositorios)

**Razón**:
- La separación de puertos in/out es fundamental en arquitectura hexagonal
- Permite que el dominio defina sus necesidades sin depender de implementaciones
- Facilita el testing (mocks de repositorios) y el cambio de implementaciones

**Impacto**:
- El dominio no conoce detalles de implementación (JPA, REST, etc.)
- Los adaptadores implementan los puertos según la tecnología elegida
- Los servicios de aplicación dependen de interfaces, no de implementaciones

---

### Decisión: PaymentOrderDomainService

**Fecha**: Implementación de casos de uso  
**Paso**: PASO 7

**Decisión**:
- Crear `PaymentOrderDomainService` para lógica de negocio que no pertenece al agregado
- Métodos: `generateReference()` y `validate()`

**Razón**:
- La generación de referencias es lógica de dominio pero no pertenece al agregado
- Las validaciones de negocio complejas pueden estar en el servicio de dominio
- Facilita la reutilización de lógica entre diferentes servicios de aplicación

**Impacto**:
- El servicio de dominio puede ser inyectado en servicios de aplicación
- La lógica de generación de referencias está centralizada
- Las validaciones de negocio están separadas de las invariantes del agregado

---

### Decisión: Orden de Operaciones en InitiatePaymentOrderService

**Fecha**: Implementación de casos de uso  
**Paso**: PASO 7

**Decisión**:
- Orden estricto: generar reference → `initiate()` → `validate()` → `save()`
- NO llamar a `validate()` antes de `initiate()`

**Razón**:
- `validate()` requiere que `status` y `createdAt` sean no-null
- Estos campos se establecen en `initiate()`
- Llamar a `validate()` antes causaría NullPointerException

**Impacto**:
- Los servicios de aplicación deben seguir este orden estrictamente
- Facilita la creación de órdenes en pasos claros
- Previene errores en tiempo de ejecución

---

### Decisión: RetrievePaymentOrderStatusService Reutiliza RetrievePaymentOrderUseCase

**Fecha**: Implementación de casos de uso  
**Paso**: PASO 7

**Decisión**:
- `RetrievePaymentOrderStatusService` inyecta `RetrievePaymentOrderUseCase`
- Llama a `retrieve()` y extrae solo el `status`

**Razón**:
- Evita duplicación de lógica (validación de referencia, búsqueda, manejo de excepciones)
- Reutiliza el caso de uso existente
- Mantiene la consistencia (mismo comportamiento para buscar orden completa o solo status)

**Impacto**:
- Menos código duplicado
- Cambios en `RetrievePaymentOrderService` se reflejan automáticamente
- Facilita el mantenimiento

---

### Decisión: Excepciones de Dominio

**Fecha**: Implementación de casos de uso  
**Paso**: PASO 7

**Decisión**:
- `PaymentOrderNotFoundException`: Cuando una orden no se encuentra
- `InvalidPaymentException`: Cuando una orden es inválida según reglas de negocio

**Razón**:
- Las excepciones de dominio expresan conceptos del negocio
- Son independientes de frameworks (no extienden excepciones de Spring)
- Facilitan el manejo de errores en los adaptadores

**Impacto**:
- Los adaptadores (REST) deben mapear estas excepciones a códigos HTTP apropiados
- Las excepciones son parte del contrato del dominio
- Facilitan el testing (se pueden verificar excepciones específicas)

---

### Decisión: PaymentOrderDomainService.generateReference()

**Fecha**: Implementación de casos de uso  
**Paso**: PASO 7

**Decisión**:
- Genera referencias en formato "PO-{número}" usando timestamp + aleatorio
- El número cumple el patrón `^PO-[0-9]+$` del OpenAPI

**Razón**:
- El formato debe cumplir con la validación del OpenAPI
- Timestamp + aleatorio garantiza unicidad
- Solo números (no UUIDs) para cumplir el pattern

**Impacto**:
- Las referencias generadas son válidas según el contrato OpenAPI
- Garantiza unicidad en la mayoría de casos
- Facilita la validación en los adaptadores REST

---

### Decisión: PaymentOrderDomainService.validate()

**Fecha**: Implementación de casos de uso  
**Paso**: PASO 7

**Decisión**:
- Valida reglas de negocio adicionales (ej: fecha no en el pasado)
- Complementa `PaymentOrder.validate()` que valida invariantes

**Razón**:
- Las validaciones de negocio pueden ser más complejas que las invariantes
- Separar validaciones permite diferentes niveles de validación
- Facilita la extensión con nuevas reglas de negocio

**Impacto**:
- Se validan tanto invariantes como reglas de negocio
- Las validaciones están centralizadas en el servicio de dominio
- Facilita el mantenimiento y extensión

---

### Corrección: Orden de Imports en Checkstyle

**Fecha**: Implementación de casos de uso  
**Paso**: PASO 7

**Problema**:
- Checkstyle mostraba warnings sobre orden de imports
- `java.time.LocalDate` y `java.util.Optional` deben ir antes de imports de `com.*`

**Corrección aplicada**:
- Se reordenaron los imports según el orden: java.* → jakarta.* → org.* → com.*
- Se eliminó import no usado `java.time.LocalDateTime` en PaymentOrderDomainService

**Razón**:
- Cumple con las reglas de Checkstyle configuradas
- Mantiene la consistencia del código

**Impacto**:
- Los warnings de ImportOrder fueron corregidos
- El código cumple con las reglas de estilo

---

## PASO 8 - H2 + JPA (Persistencia)

### Decisión: UUID como Clave Primaria Técnica

**Fecha**: Implementación de persistencia  
**Paso**: PASO 8

**Decisión**:
- Usar `UUID` como clave primaria técnica (`id`) en `PaymentOrderEntity`
- Usar `paymentOrderReference` como identificador de negocio único

**Razón**:
- UUID proporciona unicidad global sin necesidad de secuencias
- Separación clara entre ID técnico (UUID) e identificador de negocio (paymentOrderReference)
- Facilita la migración y replicación de datos

**Impacto**:
- Las búsquedas se hacen por `paymentOrderReference`, no por `id`
- El adaptador debe preservar el `id` al actualizar entidades existentes
- El dominio no conoce el UUID, solo usa `paymentOrderReference`

---

### Decisión: Mapeo de Value Objects a Campos Primitivos

**Fecha**: Implementación de persistencia  
**Paso**: PASO 8

**Decisión**:
- Los value objects del dominio se mapean a campos primitivos en la entidad JPA
- `ExternalReference.getValue()` → `externalReference` (String)
- `PaymentAmount.getValue()` → `amount` (BigDecimal)
- `PaymentAmount.getCurrency()` → `currency` (String)
- `PaymentStatus.name()` → `status` (String)

**Razón**:
- JPA no soporta directamente value objects complejos
- Mapear a campos primitivos simplifica la persistencia
- El mapper reconstruye los value objects al leer de la base de datos

**Impacto**:
- El mapper debe usar expresiones Java para extraer valores de value objects
- Al leer, el mapper debe reconstruir los value objects usando sus constructores/factorías

---

### Decisión: Búsqueda por paymentOrderReference, NO por UUID

**Fecha**: Implementación de persistencia  
**Paso**: PASO 8

**Decisión**:
- `PaymentOrderJpaRepository.findByPaymentOrderReference()` busca por `paymentOrderReference`
- NO se busca por `id` (UUID) porque el dominio usa `paymentOrderReference`

**Razón**:
- El dominio identifica órdenes por `paymentOrderReference` (identificador de negocio)
- El UUID es solo un detalle técnico de persistencia
- Mantiene la separación entre dominio e infraestructura

**Impacto**:
- El método usa `@Query` para buscar por `paymentOrderReference`
- El adaptador siempre busca por `paymentOrderReference`, nunca por UUID

---

### Decisión: Preservación del ID al Actualizar

**Fecha**: Implementación de persistencia  
**Paso**: PASO 8

**Decisión**:
- En `PaymentOrderRepositoryAdapter.save()`, si la entidad existe, se preserva el `id` (UUID)
- Se actualizan todos los demás campos

**Razón**:
- JPA necesita el `id` para hacer update en lugar de insert
- Preservar el `id` evita crear duplicados
- Mantiene la integridad referencial

**Impacto**:
- El adaptador verifica si existe la entidad por `paymentOrderReference`
- Si existe, preserva el `id` antes de guardar
- Si no existe, crea una nueva entidad (JPA generará el UUID)

---

### Decisión: PaymentOrderPersistenceMapper con Expresiones Java

**Fecha**: Implementación de persistencia  
**Paso**: PASO 8

**Decisión**:
- Usar `@Mapping` con `expression = "java(...)"` para mapear value objects
- Usar nombres completamente calificados en las expresiones para evitar ambigüedades

**Razón**:
- MapStruct no puede mapear automáticamente value objects a campos primitivos
- Las expresiones Java permiten extraer valores de value objects
- Nombres completamente calificados evitan conflictos con tipos generados por OpenAPI

**Impacto**:
- El código generado por MapStruct usa las expresiones Java directamente
- Facilita el mapeo complejo entre dominio y persistencia

---

### Decisión: PaymentOrderEntity con Lombok

**Fecha**: Implementación de persistencia  
**Paso**: PASO 8

**Decisión**:
- Usar Lombok `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- Constructor sin argumentos requerido por JPA
- Builder para facilitar creación en tests

**Razón**:
- Reduce código boilerplate
- JPA requiere constructor sin argumentos
- El builder facilita la creación de entidades en tests

**Impacto**:
- La entidad tiene getters/setters generados automáticamente
- El constructor sin argumentos está disponible para JPA
- El builder está disponible para tests

---

### Corrección: Nombres Completamente Calificados en MapStruct

**Fecha**: Implementación de persistencia  
**Paso**: PASO 8

**Problema**:
- MapStruct no podía resolver tipos como `PaymentStatus`, `PaymentAmount`, etc. en expresiones
- Error de compilación: "cannot find symbol"

**Corrección aplicada**:
- Se usaron nombres completamente calificados en las expresiones:
  - `com.bank.paymentinitiation.domain.model.PaymentStatus`
  - `com.bank.paymentinitiation.domain.model.PaymentAmount`
  - etc.

**Razón**:
- Evita ambigüedades con tipos generados por OpenAPI (que tienen nombres similares)
- MapStruct necesita nombres completamente calificados en expresiones Java

**Impacto**:
- El código compila correctamente
- No hay conflictos entre tipos del dominio y tipos generados

---

### Corrección: @Component en PaymentOrderDomainService

**Fecha**: Implementación de persistencia  
**Paso**: PASO 8

**Problema**:
- El test `contextLoads()` fallaba con `NoSuchBeanDefinitionException`
- Spring no podía encontrar `PaymentOrderDomainService`

**Corrección aplicada**:
- Se agregó `@Component` a `PaymentOrderDomainService`

**Razón**:
- Spring necesita que los servicios sean componentes para inyectarlos
- `InitiatePaymentOrderService` inyecta `PaymentOrderDomainService`

**Impacto**:
- Spring puede detectar y inyectar `PaymentOrderDomainService`
- Los tests pasan correctamente

---

## PASO 9 - MapStruct para Mapeos REST

### Decisión: PaymentOrderRestMapper con Parámetro Adicional

**Fecha**: Implementación de mappers  
**Paso**: PASO 9

**Decisión**:
- `toDomain(InitiatePaymentOrderRequest request, String paymentOrderReference)` recibe `paymentOrderReference` como parámetro adicional
- El `paymentOrderReference` se genera en el controlador, no en el mapper

**Razón**:
- El `paymentOrderReference` no viene en el request, se genera en el controlador
- El mapper necesita este valor para crear el PaymentOrder completo
- Separar la generación del mapeo mantiene responsabilidades claras

**Impacto**:
- El controlador debe generar el `paymentOrderReference` antes de llamar al mapper
- El mapper recibe todos los datos necesarios para crear el PaymentOrder

---

### Decisión: Conversión de PaymentStatus entre Dominio y DTOs

**Fecha**: Implementación de mappers  
**Paso**: PASO 9

**Decisión**:
- Usar `PaymentStatus.fromValue(domain.getStatus().name())` para convertir del dominio al DTO generado
- Ambos enums tienen los mismos valores, solo difieren en el paquete

**Razón**:
- Los enums tienen el mismo nombre pero están en paquetes diferentes
- `fromValue()` es el método estático generado por OpenAPI para crear el enum desde String
- `name()` retorna el nombre del enum como String

**Impacto**:
- La conversión es directa ya que ambos enums tienen los mismos valores
- No hay pérdida de información en la conversión

---

### Decisión: Conversión de PaymentAmount entre Dominio y DTOs

**Fecha**: Implementación de mappers  
**Paso**: PASO 9

**Decisión**:
- Del dominio al DTO: `new PaymentAmount(domain.getInstructedAmount().getValue(), CurrencyEnum.fromValue(domain.getInstructedAmount().getCurrency()))`
- Del DTO al dominio: `PaymentAmount.of(request.getInstructedAmount().getAmount(), request.getInstructedAmount().getCurrency().getValue())`

**Razón**:
- El dominio usa `PaymentAmount` (value object) con `String currency`
- El DTO generado usa `PaymentAmount` (clase) con `CurrencyEnum currency`
- Necesita conversión entre String y CurrencyEnum

**Impacto**:
- El mapper maneja la conversión entre String y CurrencyEnum
- El dominio mantiene su independencia (no conoce CurrencyEnum)

---

### Decisión: Conversión de Account Objects a Reference Value Objects

**Fecha**: Implementación de mappers  
**Paso**: PASO 9

**Decisión**:
- Del DTO al dominio: `debtorAccount.iban` → `PayerReference`, `creditorAccount.iban` → `PayeeReference`
- Del dominio al DTO: `PayerReference.getValue()` → `debtorAccount.iban`, `PayeeReference.getValue()` → `creditorAccount.iban`

**Razón**:
- El dominio usa value objects `PayerReference` y `PayeeReference`
- El DTO REST usa objetos anidados `DebtorAccount` y `CreditorAccount` con propiedad `iban`
- El mapper extrae el IBAN y crea los value objects

**Impacto**:
- El dominio no conoce la estructura de Account objects del REST
- El mapper maneja la conversión entre estructuras diferentes

---

### Decisión: Conversión LocalDateTime → OffsetDateTime

**Fecha**: Implementación de mappers  
**Paso**: PASO 9

**Decisión**:
- Crear método `@Named("localDateTimeToOffsetDateTime")` que convierte usando `ZoneOffset.UTC`
- Usar `qualifiedByName` en los mapeos que requieren esta conversión

**Razón**:
- El dominio usa `LocalDateTime` (sin zona horaria)
- Los DTOs de OpenAPI usan `OffsetDateTime` (con zona horaria)
- UTC es una elección razonable para APIs REST

**Impacto**:
- Los timestamps se convierten a UTC al exponerse en la API REST
- El dominio mantiene su simplicidad (sin zona horaria)

---

### Decisión: Nombres Completamente Calificados en Expresiones

**Fecha**: Implementación de mappers  
**Paso**: PASO 9

**Decisión**:
- Usar nombres completamente calificados en todas las expresiones Java de MapStruct
- Ejemplo: `com.bank.paymentinitiation.domain.model.PaymentAmount` en lugar de solo `PaymentAmount`

**Razón**:
- Hay ambigüedades entre tipos del dominio y tipos generados (mismo nombre, diferente paquete)
- MapStruct necesita nombres completamente calificados en expresiones Java para resolver correctamente

**Impacto**:
- Evita errores de compilación por ambigüedades
- El código es más explícito sobre qué tipo se está usando

---

### Corrección: Aliases en Imports

**Fecha**: Implementación de mappers  
**Paso**: PASO 9

**Problema**:
- Intenté usar `import ... as ...` para crear aliases (sintaxis de Kotlin/Python)
- Java no soporta aliases en imports

**Corrección aplicada**:
- Se eliminaron los aliases
- Se usaron nombres completamente calificados directamente en las expresiones

**Razón**:
- Java no tiene sintaxis para aliases en imports
- Las expresiones Java en MapStruct pueden usar nombres completamente calificados directamente

**Impacto**:
- El código compila correctamente
- Las expresiones son más largas pero más explícitas

---

### Corrección: Source Completo en @Mapping con Múltiples Parámetros

**Fecha**: Implementación de mappers  
**Paso**: PASO 9

**Problema**:
- MapStruct mostraba error: "Method has no source parameter named 'remittanceInformation'"
- Cuando hay múltiples parámetros, MapStruct necesita el source completo

**Corrección aplicada**:
- Se cambió `source = "remittanceInformation"` a `source = "request.remittanceInformation"`
- Se cambió `source = "requestedExecutionDate"` a `source = "request.requestedExecutionDate"`

**Razón**:
- MapStruct necesita saber de qué parámetro viene cada campo cuando hay múltiples parámetros
- El formato es `"parameterName.fieldName"`

**Impacto**:
- El mapper compila correctamente
- MapStruct puede resolver correctamente los campos fuente

---

### Corrección: Orden de Imports en PaymentOrderDomainService

**Fecha**: Implementación de mappers  
**Paso**: PASO 9

**Problema**:
- Checkstyle mostraba warning: "Orden incorrecto para el import 'org.springframework.stereotype.Component'"
- El import de Spring debe ir después de java.* pero antes de com.*

**Corrección aplicada**:
- Se reordenó: java.* → org.* → com.*

**Razón**:
- Cumple con las reglas de Checkstyle configuradas
- Mantiene la consistencia del código

**Impacto**:
- El warning de ImportOrder fue corregido
- El código cumple con las reglas de estilo

---

## PASO 10 - Adaptador REST (Controlador)

### Decisión: PaymentOrderReferenceGenerator Separado de PaymentOrderDomainService

**Fecha**: Implementación del controlador REST  
**Paso**: PASO 10

**Decisión**:
- Crear `PaymentOrderReferenceGenerator` en `application.service` separado de `PaymentOrderDomainService`
- `PaymentOrderDomainService.generateReference()` ya existe pero está en `domain.service`

**Razón**:
- El controlador necesita un componente Spring para generar referencias
- Separar la generación de referencias en `application.service` mantiene la separación de capas
- `PaymentOrderDomainService` puede seguir siendo usado por los servicios de aplicación

**Impacto**:
- El controlador inyecta `PaymentOrderReferenceGenerator` directamente
- Hay dos lugares donde se genera la referencia (domain service y application service)
- Ambos usan el mismo algoritmo, garantizando consistencia

---

### Decisión: PaymentOrderReferenceGenerator con Formato Numérico

**Fecha**: Implementación del controlador REST  
**Paso**: PASO 10

**Decisión**:
- Generar referencias en formato "PO-{número}" usando solo números
- NO usar UUIDs con letras (no cumplen el patrón `^PO-[0-9]+$`)

**Razón**:
- El OpenAPI define `pattern: '^PO-[0-9]+$'` para `paymentOrderId`
- UUIDs contienen letras y no cumplirían el patrón
- Usar timestamp + aleatorio garantiza unicidad mientras cumple el patrón

**Impacto**:
- Las referencias generadas cumplen con la validación del OpenAPI
- Evita errores de `ConstraintViolationException` en los endpoints
- El formato es consistente con el contrato

---

### Decisión: retrievePaymentOrderStatus Usa RetrievePaymentOrderUseCase

**Fecha**: Implementación del controlador REST  
**Paso**: PASO 10

**Decisión**:
- `retrievePaymentOrderStatus()` usa `RetrievePaymentOrderUseCase` (no `RetrievePaymentOrderStatusUseCase`)
- Necesita la orden completa para mapear `paymentOrderReference` y `lastUpdated`

**Razón**:
- `PaymentOrderStatusResponse` requiere `paymentOrderId` (paymentOrderReference) y `lastUpdate` (updatedAt)
- `RetrievePaymentOrderStatusUseCase` solo retorna `PaymentStatus`, no la orden completa
- Reutilizar `RetrievePaymentOrderUseCase` evita duplicación y proporciona todos los datos necesarios

**Impacto**:
- El controlador obtiene la orden completa aunque solo necesite el status
- El mapper puede extraer todos los campos necesarios para la respuesta
- Mantiene la consistencia (mismo comportamiento para buscar orden completa o solo status)

---

### Decisión: ApplicationConfig Vacía

**Fecha**: Implementación del controlador REST  
**Paso**: PASO 10

**Decisión**:
- Crear `ApplicationConfig` como clase de configuración Spring vacía
- Los servicios ya tienen `@Service` y son detectados automáticamente

**Razón**:
- Preparar la estructura para futuras configuraciones
- Los servicios de aplicación ya están anotados con `@Service`
- Spring Boot detecta automáticamente los componentes con anotaciones

**Impacto**:
- La configuración está lista para futuras extensiones
- No hay configuración adicional necesaria por ahora
- Mantiene la estructura organizada

---

### Decisión: PaymentOrdersController Implementa PaymentOrdersApi Directamente

**Fecha**: Implementación del controlador REST  
**Paso**: PASO 10

**Decisión**:
- `PaymentOrdersController` implementa `PaymentOrdersApi` directamente
- No crear una capa intermedia

**Razón**:
- La interfaz generada por OpenAPI ya define los contratos
- Implementar directamente mantiene la simplicidad
- El controlador es el adaptador REST (capa de infraestructura)

**Impacto**:
- El controlador está acoplado a la interfaz generada
- Cambios en el OpenAPI requieren regenerar y actualizar el controlador
- Mantiene la separación: REST (adaptador) → Use Cases (aplicación) → Domain

---

### Decisión: Códigos HTTP en PaymentOrdersController

**Fecha**: Implementación del controlador REST  
**Paso**: PASO 10

**Decisión**:
- `initiatePaymentOrder()` retorna `201 CREATED`
- `retrievePaymentOrder()` y `retrievePaymentOrderStatus()` retornan `200 OK`

**Razón**:
- `201 CREATED` es el código apropiado para recursos creados (POST)
- `200 OK` es el código apropiado para recursos recuperados (GET)
- Sigue las convenciones REST estándar

**Impacto**:
- Los clientes reciben códigos HTTP apropiados
- Facilita el manejo de respuestas en el cliente
- Cumple con las mejores prácticas REST

---

### Decisión: Validación con @Valid en Controlador

**Fecha**: Implementación del controlador REST  
**Paso**: PASO 10

**Decisión**:
- Usar `@Valid` en el parámetro `InitiatePaymentOrderRequest` del controlador
- La validación se realiza antes de llegar al dominio

**Razón**:
- `@Valid` activa las validaciones de Bean Validation definidas en el DTO
- Valida a nivel de REST antes de procesar en el dominio
- Proporciona respuestas HTTP 400 apropiadas para requests inválidos

**Impacto**:
- Los requests inválidos se rechazan antes de llegar al dominio
- Las respuestas de error son más claras para el cliente
- Reduce la carga en el dominio (no procesa datos inválidos)

---

## PASO 11 - Manejo de Excepciones y Validaciones

### Decisión: Usar ProblemDetail (RFC 7807)

**Fecha**: Implementación del manejo de excepciones  
**Paso**: PASO 11

**Decisión**:
- Usar `org.springframework.http.ProblemDetail` de Spring 6+ para todas las respuestas de error
- Seguir el estándar RFC 7807 (Problem Details for HTTP APIs)

**Razón**:
- RFC 7807 es el estándar moderno para errores HTTP
- `ProblemDetail` proporciona estructura consistente para errores
- Facilita el manejo de errores en el cliente
- Spring Boot 3+ incluye soporte nativo para ProblemDetail

**Impacto**:
- Todas las respuestas de error tienen content-type `application/problem+json`
- Los errores tienen estructura consistente (title, detail, status)
- Los clientes pueden manejar errores de forma estándar

---

### Decisión: Mapeo de Excepciones de Dominio a HTTP

**Fecha**: Implementación del manejo de excepciones  
**Paso**: PASO 11

**Decisión**:
- `PaymentOrderNotFoundException` → 404 NOT FOUND
- `InvalidPaymentException` → 400 BAD REQUEST

**Razón**:
- 404 es el código apropiado para recursos no encontrados
- 400 es el código apropiado para requests inválidos según reglas de negocio
- Sigue las convenciones REST estándar

**Impacto**:
- Los clientes reciben códigos HTTP apropiados
- Facilita el manejo de errores en el cliente
- Cumple con las mejores prácticas REST

---

### Decisión: Handlers Obligatorios para Validaciones

**Fecha**: Implementación del manejo de excepciones  
**Paso**: PASO 11

**Decisión**:
- Implementar handlers para `MethodArgumentNotValidException` y `HttpMessageNotReadableException`
- Estos handlers son OBLIGATORIOS para tests de integración

**Razón**:
- Sin estos handlers, Spring Boot retorna 500 INTERNAL SERVER ERROR en lugar de 400 BAD REQUEST
- Los tests de integración esperan 400 BAD REQUEST para requests inválidos
- Proporciona mejor experiencia al cliente (errores claros)

**Impacto**:
- Los errores de validación se mapean correctamente a 400 BAD REQUEST
- Los tests de integración pueden verificar respuestas apropiadas
- Los clientes reciben mensajes de error claros

---

### Decisión: Detalles de Validación en MethodArgumentNotValidException

**Fecha**: Implementación del manejo de excepciones  
**Paso**: PASO 11

**Decisión**:
- Extraer detalles de validación de `ex.getBindingResult().getFieldErrors()`
- Formatear como "campo: mensaje" separados por comas
- Incluir en el `detail` del ProblemDetail

**Razón**:
- Proporciona información específica sobre qué campos fallaron
- Facilita la corrección de errores en el cliente
- Sigue las mejores prácticas de validación

**Impacto**:
- Los clientes reciben detalles específicos de validación
- Facilita el debugging y corrección de requests
- Mejora la experiencia del desarrollador

---

### Decisión: Detalles de Error en HttpMessageNotReadableException

**Fecha**: Implementación del manejo de excepciones  
**Paso**: PASO 11

**Decisión**:
- Analizar el mensaje de la excepción para identificar el tipo de error
- Formatear mensajes específicos para "JSON parse error" y "Cannot deserialize"
- Proporcionar mensaje genérico si no se puede identificar el tipo

**Razón**:
- Diferentes tipos de errores requieren diferentes mensajes
- Proporciona información útil sobre el problema específico
- Facilita la corrección de errores en el cliente

**Impacto**:
- Los clientes reciben mensajes de error más específicos
- Facilita el debugging de requests malformados
- Mejora la experiencia del desarrollador

---

### Decisión: Handler Genérico para Excepciones No Manejadas

**Fecha**: Implementación del manejo de excepciones  
**Paso**: PASO 11

**Decisión**:
- Implementar handler para `Exception` genérica como último recurso
- Mapear a 500 INTERNAL SERVER ERROR
- Incluir mensaje de la excepción en el detail

**Razón**:
- Captura excepciones inesperadas que no fueron manejadas específicamente
- Proporciona respuesta HTTP apropiada en lugar de error sin manejar
- Facilita el debugging (el mensaje de error está disponible)

**Impacto**:
- Las excepciones inesperadas se manejan apropiadamente
- Los clientes reciben respuestas HTTP apropiadas
- Facilita el debugging de errores inesperados

---

### Decisión: Orden de Handlers en GlobalExceptionHandler

**Fecha**: Implementación del manejo de excepciones  
**Paso**: PASO 11

**Decisión**:
- Ordenar handlers de más específico a más genérico
- Handler genérico (`Exception`) al final

**Razón**:
- Spring evalúa handlers en orden de especificidad
- Handlers más específicos deben ejecutarse antes que los genéricos
- Evita que el handler genérico capture excepciones que deberían ser manejadas específicamente

**Impacto**:
- Las excepciones se manejan por el handler más apropiado
- No hay conflictos entre handlers
- El comportamiento es predecible

---

## PASO 12 - Tests Unitarios

### Decisión: Estrategia de Testing por Capas

**Fecha**: Implementación de tests unitarios  
**Paso**: PASO 12

**Decisión**:
- Tests del agregado (PaymentOrder): Validaciones y reglas de negocio
- Tests de servicios de aplicación: Casos exitosos y de error con mocks
- Tests de mappers: Verificación de conversiones entre capas

**Razón**:
- Separar tests por capas facilita el mantenimiento
- Los tests del agregado no requieren mocks (lógica pura)
- Los tests de servicios requieren mocks para aislar dependencias
- Los tests de mappers verifican conversiones críticas

**Impacto**:
- Cobertura completa de cada capa
- Tests rápidos y aislados
- Facilita el debugging (sabes qué capa falla)

---

### Decisión: Convenciones de Nombrado

**Fecha**: Implementación de tests unitarios  
**Paso**: PASO 12

**Decisión**:
- Nombres estilo `should[Behavior]When[Condition]()`
- Usar `@DisplayName` con lenguaje natural
- AAA (Arrange – Act – Assert)

**Razón**:
- Nombres descriptivos facilitan la lectura
- `@DisplayName` mejora la legibilidad en reportes
- AAA estructura los tests de forma clara

**Impacto**:
- Tests más legibles y mantenibles
- Reportes de tests más claros
- Facilita la colaboración en equipo

---

### Decisión: AssertJ sobre JUnit Assertions

**Fecha**: Implementación de tests unitarios  
**Paso**: PASO 12

**Decisión**:
- Usar AssertJ: `assertThat()`, `assertThatThrownBy()`
- Evitar assertions de JUnit cuando sea posible

**Razón**:
- AssertJ proporciona API más fluida y legible
- Mejores mensajes de error
- Soporte para excepciones más claro

**Impacto**:
- Tests más legibles
- Mensajes de error más informativos
- Facilita el debugging

---

### Decisión: Mockito para Servicios de Aplicación

**Fecha**: Implementación de tests unitarios  
**Paso**: PASO 12

**Decisión**:
- Usar `@ExtendWith(MockitoExtension.class)`
- `@Mock` para dependencias
- `@InjectMocks` para el servicio bajo test

**Razón**:
- Aísla el servicio bajo test de sus dependencias
- Permite verificar interacciones (verify)
- Facilita el testing de casos de error

**Impacto**:
- Tests rápidos (sin Spring context)
- Control total sobre dependencias
- Fácil de mockear comportamientos

---

### Decisión: SpringBootTest para Mappers

**Fecha**: Implementación de tests unitarios  
**Paso**: PASO 12

**Decisión**:
- Usar `@SpringBootTest` para tests de mappers
- Inyectar el mapper real (no mock)

**Razón**:
- MapStruct genera implementaciones en tiempo de compilación
- Necesita el contexto de Spring para inyectar el mapper
- Verifica que el mapper funciona correctamente

**Impacto**:
- Tests más lentos (requieren Spring context)
- Verifica la implementación real del mapper
- Detecta problemas de configuración de MapStruct

---

### Corrección: UnnecessaryStubbingException

**Fecha**: Implementación de tests unitarios  
**Paso**: PASO 12

**Problema**:
- Mockito lanzaba `UnnecessaryStubbingException` cuando se hacía stubbing de métodos no usados
- Ejemplo: `when(paymentOrderDomainService.generateReference())` cuando la orden ya tenía referencia

**Corrección aplicada**:
- Eliminar stubs innecesarios
- Solo hacer stubbing de métodos que realmente se usan en el test

**Razón**:
- Mockito detecta stubs innecesarios para evitar tests mal escritos
- Los stubs innecesarios indican que el test no está bien diseñado

**Impacto**:
- Tests más limpios y precisos
- Evita confusión sobre qué se está probando
- Facilita el mantenimiento

---

### Corrección: Import de doThrow

**Fecha**: Implementación de tests unitarios  
**Paso**: PASO 12

**Problema**:
- Error de compilación: `cannot find symbol: method doThrow`

**Corrección aplicada**:
- Agregar import: `import static org.mockito.Mockito.doThrow;`

**Razón**:
- `doThrow` es un método estático de Mockito que necesita import

**Impacto**:
- El código compila correctamente
- Se puede usar `doThrow` para mockear excepciones

---

### Corrección: Alias en Imports (Java no soporta)

**Fecha**: Implementación de tests unitarios  
**Paso**: PASO 12

**Problema**:
- Error de compilación: `';' expected` al intentar usar alias en import
- `import ... as GeneratedPaymentAmount;` (sintaxis de Kotlin/Python)

**Corrección aplicada**:
- Eliminar alias y usar nombre completamente calificado directamente en el código

**Razón**:
- Java no soporta aliases en imports
- Las expresiones pueden usar nombres completamente calificados directamente

**Impacto**:
- El código compila correctamente
- Las expresiones son más largas pero más explícitas

---

## PASO 13 - Tests de Integración (WebTestClient)

### Decisión: Usar WebTestClient sobre RestAssured

**Fecha**: Implementación de tests de integración  
**Paso**: PASO 13

**Decisión**:
- Usar `WebTestClient` de Spring WebFlux para tests de integración
- No usar RestAssured

**Razón**:
- WebTestClient es parte del ecosistema Spring
- Funciona bien con Spring Boot Test
- API fluida y legible
- Soporte nativo para reactive streams

**Impacto**:
- Tests más integrados con Spring
- API más fluida que RestAssured
- Facilita el testing de endpoints REST

---

### Decisión: H2 Real en Tests de Integración

**Fecha**: Implementación de tests de integración  
**Paso**: PASO 13

**Decisión**:
- Usar H2 real (no mocks) en tests de integración
- Limpiar H2 en `@BeforeEach` usando `repository.deleteAll()`

**Razón**:
- Los tests de integración deben validar el flujo completo
- H2 real valida que la persistencia funcione correctamente
- Limpiar H2 asegura que los tests sean independientes

**Impacto**:
- Tests más lentos (requieren base de datos)
- Tests más realistas (validan persistencia real)
- Tests independientes (cada test empieza con H2 limpio)

---

### Decisión: Captura de Valores en WebTestClient

**Fecha**: Implementación de tests de integración  
**Paso**: PASO 13

**Decisión**:
- Usar array para capturar valores del response: `String[] paymentOrderId = new String[1];`
- Usar `.value()` con Consumer para extraer valores

**Razón**:
- WebTestClient no tiene método `.value(String.class)` directo
- Usar array permite capturar valores en lambdas (variables efectivamente finales)
- Alternativa a `.consumeWith()` que también funciona

**Impacto**:
- Permite capturar valores del response para usar en tests posteriores
- Facilita el testing de flujos que requieren el ID generado

---

### Decisión: IBANs con minLength: 15

**Fecha**: Implementación de tests de integración  
**Paso**: PASO 13

**Decisión**:
- Usar IBANs con al menos 15 caracteres en tests
- Ejemplo: `"EC123456789012345678"` (18 caracteres)

**Razón**:
- El OpenAPI define `minLength: 15` para IBANs
- IBANs más cortos causarán error de validación
- Cumple con las especificaciones del contrato

**Impacto**:
- Tests pasan validaciones de OpenAPI
- Evita errores de validación en tests
- Alineado con el contrato

---

### Decisión: Fechas Futuras en Tests

**Fecha**: Implementación de tests de integración  
**Paso**: PASO 13

**Decisión**:
- Usar `LocalDate.now().plusDays(1).toString()` para fechas en tests
- No usar fechas fijas como "2025-10-31" que pueden estar en el pasado

**Razón**:
- El dominio valida que `requestedExecutionDate` no esté en el pasado
- Fechas fijas pueden fallar si la fecha actual es posterior
- Fechas dinámicas aseguran que siempre sean futuras

**Impacto**:
- Tests no fallan por fechas en el pasado
- Tests más robustos y mantenibles
- Cumple con validaciones del dominio

---

### Decisión: Verificación de ProblemDetail (RFC 7807)

**Fecha**: Implementación de tests de integración  
**Paso**: PASO 13

**Decisión**:
- Verificar ProblemDetail en tests de error (404, 400)
- Verificar `status`, `title`, `detail` en respuestas de error

**Razón**:
- RFC 7807 es el estándar para errores HTTP
- ProblemDetail proporciona estructura consistente
- Facilita el manejo de errores en el cliente

**Impacto**:
- Tests verifican que los errores sigan el estándar
- Facilita el debugging (errores estructurados)
- Mejora la experiencia del desarrollador

---

### Decisión: Casos de Error Cubiertos

**Fecha**: Implementación de tests de integración  
**Paso**: PASO 13

**Decisión**:
- Cubrir casos de error comunes:
  - 404: Orden no encontrada
  - 400: Campos faltantes (debtorAccount, instructedAmount)
  - 400: Valores inválidos (amount <= 0, IBAN muy corto, fecha en el pasado)

**Razón**:
- Los tests de integración deben validar manejo de errores
- Casos de error comunes deben estar cubiertos
- Facilita la detección de regresiones

**Impacto**:
- Cobertura completa de casos de error
- Tests detectan problemas en manejo de errores
- Facilita el mantenimiento

---

### Decisión: ID Cumple Patrón ^PO-[0-9]+$

**Fecha**: Implementación de tests de integración  
**Paso**: PASO 13

**Decisión**:
- Usar IDs que cumplan el patrón `^PO-[0-9]+$` en tests de error
- Ejemplo: `"PO-99999999999999999999999999999999"` (solo números)

**Razón**:
- El OpenAPI define el patrón para `paymentOrderId`
- IDs con letras causarán `ConstraintViolationException`
- Cumple con las especificaciones del contrato

**Impacto**:
- Tests no fallan por validación de patrón
- Tests alineados con el contrato OpenAPI
- Evita errores de validación

---

## PASO 14 - Quality Gates (JaCoCo, Checkstyle, SpotBugs)

### Decisión: Cobertura Mínima de 85%

**Fecha**: Configuración de quality gates  
**Paso**: PASO 14

**Decisión**:
- Configurar JaCoCo con cobertura mínima de 85% (actualizada desde 80%)

**Razón**:
- 85% es un estándar razonable para proyectos empresariales
- Balance entre cobertura completa y esfuerzo de testing
- Cumple con los requisitos del playbook

**Impacto**:
- Los builds fallan si la cobertura es menor a 85%
- Motiva a escribir tests adicionales cuando sea necesario
- Asegura calidad del código

---

### Decisión: Exclusiones de JaCoCo

**Fecha**: Configuración de quality gates  
**Paso**: PASO 14

**Decisión**:
- Excluir de cobertura:
  - Código generado (`**/generated/**`)
  - Entidades JPA (`**/PaymentOrderEntity.class`)
  - Implementaciones MapStruct (`**/*MapperImpl.class`)
  - Clase principal (`**/PaymentInitiationServiceApplication.class`)
  - Configuración (`**/config/**`)

**Razón**:
- El código generado no debe contar para cobertura (no es código propio)
- Las entidades JPA son simples POJOs (getters/setters)
- Las implementaciones MapStruct son generadas automáticamente
- La clase principal es boilerplate de Spring Boot
- La configuración es simple y no requiere tests

**Impacto**:
- La cobertura se calcula solo sobre código propio
- Facilita alcanzar el 85% mínimo
- Enfoque en código de negocio

---

### Decisión: project.afterEvaluate para Exclusiones

**Fecha**: Configuración de quality gates  
**Paso**: PASO 14

**Decisión**:
- Usar `project.afterEvaluate` para configurar exclusiones de JaCoCo
- No usar `afterEvaluate` dentro de `tasks.named()`

**Razón**:
- `afterEvaluate` dentro de `tasks.named()` causa errores de compilación
- `project.afterEvaluate` se ejecuta después de que las clases estén compiladas
- Permite acceder a `classDirectories` correctamente

**Impacto**:
- Las exclusiones se aplican correctamente
- El build funciona sin errores
- La cobertura se calcula correctamente

---

### Decisión: Tests Adicionales para Mejorar Cobertura

**Fecha**: Configuración de quality gates  
**Paso**: PASO 14

**Decisión**:
- Agregar tests adicionales para:
  - PaymentOrderDomainService (generateReference, validate)
  - GlobalExceptionHandler (todos los handlers)
  - PaymentOrderReferenceGenerator (formato, unicidad)
  - PaymentOrderRepositoryAdapter (save nuevo, save actualización, findByReference)
  - Value Objects (equals, hashCode, toString)
  - Casos edge en PaymentOrder.changeStatus()

**Razón**:
- La cobertura inicial era 54%, necesitaba mejorarse
- Estos componentes son críticos y deben estar cubiertos
- Los tests adicionales mejoran la confianza en el código

**Impacto**:
- Cobertura mejoró de 54% a 69%
- Componentes críticos ahora están cubiertos
- Facilita el mantenimiento futuro
- ⚠️ PENDIENTE: Revisar reporte HTML para identificar componentes específicos sin cobertura y agregar tests adicionales hasta alcanzar 85%

---

### Decisión: Checkstyle maxWarnings: 100

**Fecha**: Configuración de quality gates  
**Paso**: PASO 14

**Decisión**:
- Configurar `maxWarnings = 100` en Checkstyle

**Razón**:
- Permite warnings aceptables (MagicNumber en tests, VisibilityModifier con Lombok, etc.)
- Evita que el build falle por warnings menores
- Balance entre calidad y pragmatismo

**Impacto**:
- El build no falla por warnings menores
- Se pueden ignorar warnings conocidos y aceptables
- Facilita el desarrollo

---

### Decisión: SpotBugs con valueOf (no strings)

**Fecha**: Configuración de quality gates  
**Paso**: PASO 14

**Decisión**:
- Usar `com.github.spotbugs.snom.Effort.valueOf('MAX')` y `Confidence.valueOf('HIGH')`
- No usar strings directamente

**Razón**:
- `valueOf` es type-safe y evita errores de compilación
- Las strings pueden tener typos que no se detectan hasta runtime
- Mejor práctica de Gradle

**Impacto**:
- Configuración type-safe
- Errores de configuración se detectan en tiempo de compilación
- Código más robusto

---

### Corrección: Orden de Imports en Tests

**Fecha**: Configuración de quality gates  
**Paso**: PASO 14

**Problema**:
- Checkstyle mostraba warnings de ImportOrder en tests nuevos
- Imports de `java.*` estaban después de `org.*`

**Corrección aplicada**:
- Reordenar imports según el orden: java.* → org.* → com.*
- Separar grupos con líneas en blanco

**Razón**:
- Cumple con las reglas de Checkstyle configuradas
- Mantiene la consistencia del código

**Impacto**:
- Los warnings de ImportOrder fueron corregidos
- El código cumple con las reglas de estilo

---

## PASO 15 - Dockerización (Dockerfile y docker-compose.yml)

### Decisión: Dockerfile Multi-Stage

**Fecha**: Dockerización del microservicio  
**Paso**: PASO 15

**Decisión**:
- Usar Dockerfile multi-stage con dos stages:
  - Builder: eclipse-temurin:21-jdk-alpine (para compilar)
  - Runtime: eclipse-temurin:21-jre-alpine (para ejecutar)

**Razón**:
- Reduce el tamaño de la imagen final (solo JRE, no JDK completo)
- Mejora la seguridad (menos superficie de ataque)
- Acelera el build (caché de layers)
- Separación clara entre build y runtime

**Impacto**:
- Imagen final más pequeña y optimizada
- Build más rápido en CI/CD
- Mejor práctica de Docker

---

### Decisión: Usuario No-Root

**Fecha**: Dockerización del microservicio  
**Paso**: PASO 15

**Decisión**:
- Crear usuario no-root (spring:spring) y ejecutar la aplicación con ese usuario

**Razón**:
- Mejora la seguridad (principio de menor privilegio)
- Buenas prácticas de Docker
- Requisito común en entornos de producción

**Impacto**:
- Mayor seguridad en contenedores
- Cumple con estándares de seguridad
- Reduce riesgo de escalación de privilegios

---

### Decisión: Health Check con wget

**Fecha**: Dockerización del microservicio  
**Paso**: PASO 15

**Decisión**:
- Usar wget para health checks en lugar de curl
- Configurar HEALTHCHECK en Dockerfile y docker-compose.yml

**Razón**:
- wget es más ligero que curl en Alpine
- Health checks permiten monitoreo automático
- Integración con orquestadores (Docker Compose, Kubernetes)

**Impacto**:
- Monitoreo automático de salud del servicio
- Reinicio automático si el servicio falla
- Mejor observabilidad

---

### Decisión: .dockerignore

**Fecha**: Dockerización del microservicio  
**Paso**: PASO 15

**Decisión**:
- Excluir build/, .gradle/, out/, .idea/, .vscode/, *.iml, .git/, Dockerfile, docker-compose.yml, **/test-results/, **/reports/, ai/

**Razón**:
- Reduce el tamaño del build context
- Acelera el build (menos archivos para copiar)
- Evita copiar archivos innecesarios al contenedor
- Mejora la seguridad (no copiar archivos sensibles)

**Impacto**:
- Builds más rápidos
- Imágenes más pequeñas
- Mejor seguridad

---

### Decisión: Perfil Docker (application-docker.yml)

**Fecha**: Dockerización del microservicio  
**Paso**: PASO 15

**Decisión**:
- Crear application-docker.yml con configuración específica para Docker
- Deshabilitar H2 console en producción
- Configurar logging a nivel INFO

**Razón**:
- Separación de configuración por ambiente
- Seguridad (no exponer H2 console en producción)
- Logging apropiado para producción

**Impacto**:
- Configuración específica para contenedores
- Mayor seguridad
- Logging optimizado

---

### Decisión: Variables de Entorno en docker-compose.yml

**Fecha**: Dockerización del microservicio  
**Paso**: PASO 15

**Decisión**:
- Configurar SPRING_PROFILES_ACTIVE=docker
- Configurar JAVA_OPTS=-Xmx512m -Xms256m

**Razón**:
- Activar perfil Docker automáticamente
- Limitar memoria del contenedor
- Buenas prácticas de Docker

**Impacto**:
- Configuración automática del perfil
- Control de recursos del contenedor
- Facilita el despliegue

---

## PASO 16 - README.md Final

### Decisión: Estructura del README

**Fecha**: Documentación final del proyecto  
**Paso**: PASO 16

**Decisión**:
- Crear README.md con secciones claras y bien organizadas
- Incluir descripción, arquitectura, stack técnico, ejecución, testing y uso de IA
- Usar formato Markdown con tablas y ejemplos de código

**Razón**:
- Facilita la comprensión del proyecto para nuevos desarrolladores
- Documenta todas las decisiones técnicas importantes
- Proporciona guías claras de uso y ejecución
- Mantiene trazabilidad del uso de IA

**Impacto**:
- Proyecto más accesible y fácil de entender
- Onboarding más rápido para nuevos desarrolladores
- Documentación completa y profesional

---

### Decisión: Mejora del README con Elementos Adicionales

**Fecha**: Mejora del README  
**Paso**: PASO 16 (mejora)

**Decisión**:
- Incorporar elementos de READMEs de referencia para hacerlo más robusto
- Agregar sección de DTOs principales
- Mejorar API Reference con ejemplos de request/response completos
- Expandir sección de pruebas con detalles de estrategia
- Agregar diagrama de arquitectura en formato texto
- Incluir sección de recursos adicionales

**Razón**:
- README más completo facilita el uso del proyecto
- Ejemplos de request/response ayudan a entender la API
- Información detallada de pruebas demuestra calidad
- Diagrama de arquitectura ayuda a visualizar la estructura

**Impacto**:
- README más profesional y completo
- Mejor experiencia para desarrolladores que usen el proyecto
- Documentación que refleja la calidad del código

---

### Decisión: Incluir Información de IA

**Fecha**: Documentación final del proyecto  
**Paso**: PASO 16

**Decisión**:
- Incluir sección sobre uso de IA en el desarrollo
- Referenciar la carpeta `ai/` y su contenido
- Explicar la trazabilidad del proceso de desarrollo

**Razón**:
- Transparencia sobre el uso de IA en el desarrollo
- Facilita la comprensión de decisiones de diseño
- Mantiene trazabilidad completa del proceso

**Impacto**:
- Mayor transparencia en el proceso de desarrollo
- Facilita el mantenimiento futuro
- Documentación completa del proceso

---

## Notas

- Las decisiones y correcciones se irán agregando conforme avance el desarrollo
- Cada decisión importante debe documentarse aquí para mantener trazabilidad
- Las correcciones manuales son especialmente importantes para entender qué se ajustó respecto a lo generado por IA

