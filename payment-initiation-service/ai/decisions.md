# Decisiones Arquitectónicas y Correcciones Manuales

Este archivo documenta las decisiones de diseño, correcciones manuales aplicadas al código generado por IA, trade-offs considerados y justificaciones técnicas.

## Estructura de Documentación

Cada entrada debe incluir:
- **Fecha**: Fecha de la decisión o corrección
- **Decisión/Problema**: Descripción clara de la decisión tomada o problema encontrado
- **Contexto**: Situación que motivó la decisión
- **Opciones consideradas**: Alternativas evaluadas (si aplica)
- **Solución elegida**: La solución implementada
- **Justificación**: Razones técnicas y de negocio
- **Trade-offs**: Ventajas y desventajas de la decisión
- **Archivos afectados**: Lista de archivos modificados

---

## Fase 1: Configuración Inicial del Proyecto

### Decisión 1: Estructura del Proyecto
**Fecha**: 2025-01-27

**Decisión**: Crear el proyecto en un subdirectorio `payment-initiation-service` dentro del workspace principal.

**Justificación**: Permite mantener el proyecto separado de los archivos de documentación y recursos del challenge (WSDL, Postman collection, etc.).

---

### Decisión 2: Configuración de Checkstyle
**Fecha**: 2025-01-27

**Decisión**: Usar configuración "media" con reglas balanceadas:
- Longitud de línea: 120 caracteres (razonable para proyectos modernos)
- Complejidad ciclomática: máximo 15
- Longitud de método: máximo 150 líneas
- Reglas de naming estándar de Java
- Exclusión automática de código generado

**Justificación**: 
- No queremos reglas extremas que frenen el desarrollo
- Las reglas deben ser suficientes para mantener calidad sin ser restrictivas
- La exclusión de código generado es esencial para evitar falsos positivos

**Archivos creados**:
- `config/checkstyle/checkstyle.xml`: Configuración principal con reglas media
- `config/checkstyle/suppressions.xml`: Exclusiones para código generado (MapStruct, OpenAPI)

---

### Decisión 3: Configuración de SpotBugs
**Fecha**: 2025-01-27

**Decisión**: Declarar el plugin de SpotBugs pero dejar la configuración detallada para el PASO 14 según las reglas del proyecto.

**Justificación**: 
- El usuario especificó que la configuración detallada está en el PASO 14 del super_prompt.txt
- Por ahora solo necesitamos que el plugin esté declarado sin romper el build
- Se creó un archivo básico de exclusión para código generado

**Archivos creados**:
- `config/spotbugs/exclude.xml`: Exclusiones básicas para código generado

---

### Decisión 4: Versiones de Dependencias
**Fecha**: 2025-01-27

**Versiones seleccionadas**:
- Spring Boot: 3.2.0
- MapStruct: 1.5.5.Final
- Lombok: 1.18.30
- Checkstyle: 10.12.5
- SpotBugs: 6.0.0
- JaCoCo: 0.8.11
- Gradle: 8.5

**Justificación**: 
- Versiones estables y compatibles con Java 21
- Spring Boot 3.2.0 es una versión estable de la serie 3.x
- MapStruct 1.5.5.Final es compatible con Java 21
- Las versiones de herramientas de calidad son las más recientes estables

---

### Decisión 5: Configuración de Base de Datos
**Fecha**: 2025-01-27

**Decisión**: Usar H2 en memoria para desarrollo local, con consola H2 habilitada.

**Justificación**: 
- Según las reglas del proyecto, H2 es la base de datos especificada
- En memoria es suficiente para desarrollo y testing
- La consola H2 facilita la depuración durante el desarrollo

---

---

### Corrección 1: Configuración de Checkstyle - LineLength
**Fecha**: 2025-01-27

**Problema**: Error al ejecutar Checkstyle: "TreeWalker is not allowed as a parent of LineLength"

**Causa**: En Checkstyle 10.x, el módulo `LineLength` es un check de nivel de archivo y no puede estar dentro de `TreeWalker`. Debe estar al mismo nivel que `FileLength` en el módulo `Checker`.

**Solución**: Movido `LineLength` fuera de `TreeWalker`, colocándolo después de `FileLength` y antes de `TreeWalker`.

**Archivo modificado**: `config/checkstyle/checkstyle.xml`

---

### Corrección 2: Configuración de Checkstyle - VariableDeclarationUsageDistance
**Fecha**: 2025-01-27

**Problema**: Error al ejecutar Checkstyle: "Property 'max' does not exist" para `VariableDeclarationUsageDistance`

**Causa**: En Checkstyle 10.x, la propiedad `max` fue reemplazada por `allowedDistance` en el módulo `VariableDeclarationUsageDistance`.

**Solución**: Cambiado `max` por `allowedDistance` en la configuración del módulo.

**Archivo modificado**: `config/checkstyle/checkstyle.xml`

**Resultado**: Checkstyle ahora se ejecuta correctamente sin errores.

---

---

## Fase 2: Análisis del Servicio SOAP Legacy y Mapeo a BIAN

### Análisis del WSDL y XML de Ejemplo - PaymentOrderService

**Fecha**: 2025-01-27

**Contexto**: Análisis del servicio SOAP legacy para entender la funcionalidad existente y mapearla al Service Domain BIAN "Payment Initiation" con Behavior Qualifier "PaymentOrder".

---

#### 1. Operaciones SOAP Disponibles

El servicio `PaymentOrderService` expone **2 operaciones principales**:

| Operación SOAP | Descripción | SOAP Action |
|----------------|-------------|-------------|
| `SubmitPaymentOrder` | Envía una nueva orden de pago para procesamiento | `submit` |
| `GetPaymentOrderStatus` | Consulta el estado actual de una orden de pago existente | `status` |

**Detalles técnicos**:
- **Namespace**: `http://legacy.bank/payments`
- **Estilo de binding**: Document/Literal
- **Endpoint**: `http://soap-mock:8081/legacy/payments`

---

#### 2. Estructuras de Datos Principales

##### 2.1. SubmitPaymentOrderRequest

Campos de entrada para crear una orden de pago:

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `externalId` | string | Sí | Identificador externo proporcionado por el cliente |
| `debtorIban` | string | Sí | IBAN de la cuenta del deudor (pagador) |
| `creditorIban` | string | Sí | IBAN de la cuenta del acreedor (beneficiario) |
| `amount` | decimal | Sí | Monto de la orden de pago |
| `currency` | string | Sí | Código de moneda (ISO 4217, ej: USD, EUR) |
| `remittanceInfo` | string | No | Información de remesa (propósito del pago) |
| `requestedExecutionDate` | date | Sí | Fecha solicitada de ejecución (formato ISO 8601) |

**Ejemplo de request**:
```xml
<SubmitPaymentOrderRequest>
  <externalId>EXT-123</externalId>
  <debtorIban>EC12DEBTOR</debtorIban>
  <creditorIban>EC98CREDITOR</creditorIban>
  <amount>150.75</amount>
  <currency>USD</currency>
  <remittanceInfo>Factura 001-123</remittanceInfo>
  <requestedExecutionDate>2025-10-31</requestedExecutionDate>
</SubmitPaymentOrderRequest>
```

##### 2.2. SubmitPaymentOrderResponse

Respuesta al crear una orden de pago:

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `paymentOrderId` | string | Identificador único de la orden de pago (ej: PO-0001) |
| `status` | string | Estado inicial de la orden (ej: ACCEPTED) |

**Ejemplo de response**:
```xml
<SubmitPaymentOrderResponse>
  <paymentOrderId>PO-0001</paymentOrderId>
  <status>ACCEPTED</status>
</SubmitPaymentOrderResponse>
```

##### 2.3. GetPaymentOrderStatusRequest

Solicitud para consultar el estado de una orden:

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `paymentOrderId` | string | Sí | Identificador de la orden de pago a consultar |

##### 2.4. GetPaymentOrderStatusResponse

Respuesta con el estado actual de la orden:

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `paymentOrderId` | string | Identificador de la orden de pago |
| `status` | string | Estado actual de la orden |
| `lastUpdate` | dateTime | Fecha y hora de la última actualización (ISO 8601) |

**Ejemplo de response**:
```xml
<GetPaymentOrderStatusResponse>
  <paymentOrderId>PO-0001</paymentOrderId>
  <status>SETTLED</status>
  <lastUpdate>2025-10-30T16:25:30Z</lastUpdate>
</GetPaymentOrderStatusResponse>
```

---

#### 3. Estados Posibles de la Orden de Pago (Legacy)

Basado en los ejemplos XML y el análisis del WSDL, se identifican los siguientes estados:

| Estado Legacy | Descripción | Contexto de Uso |
|---------------|-------------|-----------------|
| `ACCEPTED` | Orden aceptada | Estado inicial retornado en `SubmitPaymentOrderResponse` |
| `SETTLED` | Orden liquidada/completada | Estado final en `GetPaymentOrderStatusResponse` |

**Estados inferidos** (no aparecen en ejemplos pero son comunes en sistemas de pago):
- `PENDING`: Orden pendiente de procesamiento
- `PROCESSING`: Orden en proceso
- `REJECTED`: Orden rechazada
- `FAILED`: Orden fallida
- `CANCELLED`: Orden cancelada

**Nota**: Los estados exactos del sistema legacy deberían confirmarse con documentación adicional o pruebas del sistema real.

---

#### 4. Mapeo SOAP Legacy → BIAN Payment Initiation (PaymentOrder BQ)

##### 4.1. Mapeo de Operaciones

| Operación SOAP | Operación BIAN | Método HTTP | Endpoint REST |
|----------------|----------------|-------------|---------------|
| `SubmitPaymentOrder` | `Initiate` | POST | `/payment-initiation/payment-orders` |
| `GetPaymentOrderStatus` | `Retrieve Status` | GET | `/payment-initiation/payment-orders/{id}/status` |
| - | `Retrieve` (nuevo) | GET | `/payment-initiation/payment-orders/{id}` |

**Justificación**:
- `SubmitPaymentOrder` mapea a `Initiate` (creación de recurso)
- `GetPaymentOrderStatus` mapea a `Retrieve Status` (consulta de estado)
- Se agrega `Retrieve` para obtener el detalle completo de la orden (no existe en SOAP legacy)

##### 4.2. Mapeo de Campos de Datos

| Campo SOAP Legacy | Campo BIAN | Tipo BIAN | Notas |
|-------------------|------------|-----------|-------|
| `externalId` | `externalReference` | string | Referencia externa del cliente |
| `debtorIban` | `debtorAccount.iban` | string | IBAN dentro del objeto `debtorAccount` |
| `creditorIban` | `creditorAccount.iban` | string | IBAN dentro del objeto `creditorAccount` |
| `amount` | `instructedAmount.amount` | decimal | Monto dentro del objeto `instructedAmount` |
| `currency` | `instructedAmount.currency` | string | Moneda dentro del objeto `instructedAmount` |
| `remittanceInfo` | `remittanceInformation` | string | Información de remesa |
| `requestedExecutionDate` | `requestedExecutionDate` | date | Fecha solicitada (ISO 8601) |
| `paymentOrderId` | `paymentOrderReference` | string | Identificador BIAN de la orden |
| `status` | `paymentOrderStatus` | enum | Estado de la orden (ver mapeo de estados) |
| `lastUpdate` | `lastUpdated` | dateTime | Última actualización (ISO 8601) |

**Estructuras BIAN adicionales** (no presentes en SOAP legacy):
- `createdAt`: Fecha de creación de la orden
- `updatedAt`: Fecha de última actualización (puede diferir de `lastUpdated`)

##### 4.3. Mapeo de Estados

| Estado Legacy | Estado BIAN | Descripción BIAN |
|---------------|-------------|------------------|
| `ACCEPTED` | `INITIATED` | Orden iniciada y aceptada |
| `PENDING` | `PENDING` | Orden pendiente de procesamiento |
| `PROCESSING` | `PROCESSED` | Orden procesada |
| `SETTLED` | `COMPLETED` | Orden completada/liquidada |
| `REJECTED` | `FAILED` | Orden rechazada/fallida |
| `CANCELLED` | `CANCELLED` | Orden cancelada |

**Justificación del mapeo**:
- `ACCEPTED` → `INITIATED`: Estado inicial cuando la orden es aceptada
- `SETTLED` → `COMPLETED`: Estado final cuando la orden ha sido liquidada
- `REJECTED` → `FAILED`: Ambos indican un resultado negativo
- `CANCELLED` se mantiene igual en ambos sistemas

##### 4.4. Estructura de Objetos BIAN

**InstructedAmount** (agrupa amount + currency):
```json
{
  "amount": 150.75,
  "currency": "USD"
}
```

**DebtorAccount** (agrupa información del deudor):
```json
{
  "iban": "EC12DEBTOR"
}
```

**CreditorAccount** (agrupa información del acreedor):
```json
{
  "iban": "EC98CREDITOR"
}
```

---

#### 5. Decisiones de Diseño para la Migración

##### 5.1. Operación Retrieve Adicional

**Decisión**: Agregar operación `Retrieve` (GET `/payment-initiation/payment-orders/{id}`) que no existe en el SOAP legacy.

**Justificación**:
- BIAN recomienda tener operación `Retrieve` para obtener el detalle completo del recurso
- El SOAP solo tiene `GetPaymentOrderStatus` que retorna estado, pero no el detalle completo
- Facilita la integración con sistemas que necesitan información completa de la orden

**Trade-offs**:
- ✅ Mejor alineación con estándares BIAN
- ✅ Mayor flexibilidad para consumidores
- ⚠️ Requiere implementación adicional no presente en el sistema legacy

##### 5.2. Estructura de Objetos Anidados

**Decisión**: Agrupar campos relacionados en objetos (ej: `instructedAmount`, `debtorAccount`, `creditorAccount`).

**Justificación**:
- Alineación con estándares BIAN y ISO 20022
- Mejor organización semántica de los datos
- Facilita extensibilidad futura (ej: agregar más campos a `debtorAccount`)

##### 5.3. Nomenclatura BIAN

**Decisión**: Usar nomenclatura BIAN estándar:
- `paymentOrderReference` en lugar de `paymentOrderId`
- `externalReference` en lugar de `externalId`
- `remittanceInformation` en lugar de `remittanceInfo`

**Justificación**:
- Cumplimiento con estándares BIAN
- Consistencia con otros servicios del ecosistema bancario
- Mejor legibilidad y claridad semántica

---

#### 6. Resumen del Mapeo

**Operaciones**:
- `SubmitPaymentOrder` → `POST /payment-initiation/payment-orders` (Initiate)
- `GetPaymentOrderStatus` → `GET /payment-initiation/payment-orders/{id}/status` (Retrieve Status)
- *(Nuevo)* → `GET /payment-initiation/payment-orders/{id}` (Retrieve)

**Campos clave**:
- Todos los campos del SOAP tienen equivalente en BIAN
- Se agregan campos adicionales (`createdAt`, `updatedAt`) para trazabilidad
- Se agrupan campos relacionados en objetos estructurados

**Estados**:
- Estados legacy mapean a estados BIAN estándar
- Se mantiene compatibilidad semántica entre ambos sistemas

**Archivos analizados**:
- `Prueba-tecnica-Java-migracion/legacy/PaymentOrderService.wsdl`
- `Prueba-tecnica-Java-migracion/legacy/samples/SubmitPaymentOrderRequest.xml`
- `Prueba-tecnica-Java-migracion/legacy/samples/SubmitPaymentOrderResponse.xml`
- `Prueba-tecnica-Java-migracion/legacy/samples/GetPaymentOrderStatusRequest.xml`
- `Prueba-tecnica-Java-migracion/legacy/samples/GetPaymentOrderStatusResponse.xml`

---

## Fase 3: Diseño del Contrato OpenAPI 3.0

### Decisión 1: Estructura del Contrato OpenAPI

**Fecha**: 2025-01-27

**Contexto**: Creación del contrato OpenAPI 3.0 basado en el análisis del WSDL legacy y la colección Postman, siguiendo el enfoque contract-first.

**Decisión**: Crear un contrato OpenAPI 3.0 completo con:
- Versión OpenAPI: 3.0.3
- Información del API con descripción BIAN
- Servidor de desarrollo local
- 3 endpoints principales (POST, GET /{id}, GET /{id}/status)
- Schemas completos con validaciones
- Manejo de errores RFC 7807

**Justificación**:
- OpenAPI 3.0.3 es la versión estable más reciente
- Contract-first permite generar código automáticamente
- RFC 7807 es estándar para manejo de errores en APIs REST
- Validaciones en el contrato facilitan la generación de código con validaciones

**Archivos creados**:
- `openapi/openapi.yaml`: Contrato completo

---

### Decisión 2: Endpoints y Operaciones

**Fecha**: 2025-01-27

**Decisión**: Definir 3 endpoints REST:

1. **POST `/payment-initiation/payment-orders`** (operationId: `initiatePaymentOrder`)
   - Mapea a SOAP `SubmitPaymentOrder`
   - Retorna 201 Created con `InitiatePaymentOrderResponse`
   - Maneja errores 400 y 500

2. **GET `/payment-initiation/payment-orders/{id}`** (operationId: `retrievePaymentOrder`)
   - Nueva operación no presente en SOAP legacy
   - Retorna 200 OK con `RetrievePaymentOrderResponse` (detalle completo)
   - Maneja errores 404 y 500

3. **GET `/payment-initiation/payment-orders/{id}/status`** (operationId: `retrievePaymentOrderStatus`)
   - Mapea a SOAP `GetPaymentOrderStatus`
   - Retorna 200 OK con `PaymentOrderStatusResponse` (solo estado)
   - Maneja errores 404 y 500

**Justificación**:
- Alineación con operaciones BIAN estándar (Initiate, Retrieve, Retrieve Status)
- Separación clara entre obtener detalle completo vs. solo estado
- Códigos HTTP semánticos (201 para creación, 200 para consultas, 404 para no encontrado)

**Trade-offs**:
- ✅ Mejor alineación BIAN con operación Retrieve adicional
- ✅ Separación clara de responsabilidades
- ⚠️ Requiere implementación de operación adicional no presente en legacy

---

### Decisión 3: Schemas y Validaciones

**Fecha**: 2025-01-27

**Decisiones de diseño de schemas**:

#### 3.1. InitiatePaymentOrderRequest

**Campos requeridos**:
- `externalReference`: string (1-50 caracteres)
- `debtorAccount`: objeto Account
- `creditorAccount`: objeto Account
- `instructedAmount`: objeto PaymentAmount
- `requestedExecutionDate`: date (ISO 8601)

**Campos opcionales**:
- `remittanceInformation`: string (máx. 500 caracteres)

**Justificación**:
- `remittanceInformation` es opcional porque en el WSDL legacy tiene `minOccurs="0"`
- Validaciones de longitud previenen datos excesivamente largos
- Estructura alineada con ejemplo de Postman collection

#### 3.2. Account Schema

**Estructura**:
```yaml
Account:
  required: [iban]
  properties:
    iban:
      type: string
      minLength: 15
      maxLength: 34
      pattern: '^[A-Z]{2}[0-9]{2}[A-Z0-9]+$'
```

**Justificación**:
- IBAN tiene formato estándar internacional (2 letras país + 2 dígitos + alfanumérico)
- Longitud mínima 15 y máxima 34 según estándar IBAN
- Validación con regex para asegurar formato correcto

#### 3.3. PaymentAmount Schema

**Estructura**:
```yaml
PaymentAmount:
  required: [amount, currency]
  properties:
    amount:
      type: number
      format: double
      minimum: 0.01
    currency:
      type: string
      pattern: '^[A-Z]{3}$'
      enum: [USD, EUR, GBP, JPY, CAD, AUD, CHF, CNY]
```

**Justificación**:
- Monto mínimo 0.01 previene valores negativos o cero
- Currency con patrón ISO 4217 (3 letras mayúsculas)
- Enum con monedas comunes para validación estricta (puede extenderse)

#### 3.4. PaymentOrderStatus Enum

**Valores**:
- `INITIATED`: Estado inicial cuando se crea la orden
- `PENDING`: Orden pendiente de procesamiento
- `PROCESSED`: Orden procesada
- `COMPLETED`: Orden completada/liquidada
- `FAILED`: Orden fallida/rechazada
- `CANCELLED`: Orden cancelada

**Extensión x-mapping**:
Se agregó extensión `x-mapping` para documentar el mapeo de estados legacy:
```yaml
x-mapping:
  legacy:
    ACCEPTED: INITIATED
    PENDING: PENDING
    PROCESSING: PROCESSED
    SETTLED: COMPLETED
    REJECTED: FAILED
    CANCELLED: CANCELLED
```

**Justificación**:
- Estados alineados con ciclo de vida BIAN estándar
- Extensión x-mapping ayuda a entender la migración desde SOAP legacy
- Facilita la implementación de mappers

#### 3.5. ProblemDetail (RFC 7807)

**Estructura completa**:
```yaml
ProblemDetail:
  required: [type, title, status]
  properties:
    type: string (URI)
    title: string
    status: integer
    detail: string (opcional)
    instance: string (URI, opcional)
```

**Justificación**:
- RFC 7807 es estándar para manejo de errores en APIs REST
- Proporciona información estructurada y útil para debugging
- Compatible con Spring Boot ProblemDetail

---

### Decisión 4: Validaciones y Restricciones

**Fecha**: 2025-01-27

**Validaciones implementadas**:

1. **paymentOrderReference**: Patrón `^PO-[A-Z0-9-]+$`
   - Formato: PO- seguido de alfanumérico y guiones
   - Ejemplo: PO-0001, PO-2024-001

2. **IBAN**: Patrón `^[A-Z]{2}[0-9]{2}[A-Z0-9]+$`
   - 2 letras país + 2 dígitos + alfanumérico
   - Longitud 15-34 caracteres

3. **Currency**: Enum con monedas comunes
   - USD, EUR, GBP, JPY, CAD, AUD, CHF, CNY
   - Patrón adicional: 3 letras mayúsculas

4. **Amount**: Mínimo 0.01
   - Previene valores negativos o cero
   - Tipo number con formato double para decimales

5. **Strings**: Longitudes máximas
   - `externalReference`: máx. 50 caracteres
   - `remittanceInformation`: máx. 500 caracteres

**Justificación**:
- Validaciones en el contrato permiten generar código con validaciones automáticas
- Previene datos inválidos antes de llegar al dominio
- Facilita mensajes de error claros al cliente

**Trade-offs**:
- ✅ Validaciones tempranas mejoran la experiencia del cliente
- ✅ Reduce carga de validación en el dominio
- ⚠️ Requiere mantener validaciones sincronizadas entre contrato y código

---

### Decisión 5: Formato de Fechas y Timestamps

**Fecha**: 2025-01-27

**Decisiones**:
- `requestedExecutionDate`: `format: date` (ISO 8601, ej: "2025-10-31")
- `createdAt`: `format: date-time` (ISO 8601, ej: "2025-10-30T10:15:30Z")
- `lastUpdated`: `format: date-time` (ISO 8601, ej: "2025-10-30T16:25:30Z")

**Justificación**:
- ISO 8601 es estándar internacional para fechas y tiempos
- Formato date para fechas de ejecución (solo fecha)
- Formato date-time para timestamps (fecha + hora + timezone)
- Compatible con Java LocalDate y LocalDateTime/OffsetDateTime

---

### Decisión 6: Ejemplos en el Contrato

**Fecha**: 2025-01-27

**Decisión**: Incluir ejemplos en:
- Request body del POST (InitiatePaymentOrderRequest)
- Todos los schemas de respuesta
- Valores de enum (PaymentOrderStatus)

**Justificación**:
- Facilita el testing y desarrollo
- Documenta el formato esperado de los datos
- Mejora la experiencia del desarrollador que consume el API
- Alineado con el ejemplo de la colección Postman

---

## Fase 4: Configuración de OpenAPI Generator

### Decisión 1: Configuración de la Tarea openApiGenerate

**Fecha**: 2025-01-27

**Contexto**: Necesidad de configurar el plugin OpenAPI Generator para generar automáticamente interfaces de API y DTOs desde el contrato OpenAPI 3.0, siguiendo el enfoque contract-first.

**Decisión**: Configurar la tarea `openApiGenerate` con:
- **Generator**: `spring` (genera código Spring Boot compatible)
- **Input**: `$rootDir/openapi/openapi.yaml`
- **Output**: `$buildDir/generated`
- **Paquetes**:
  - `apiPackage`: `com.bank.paymentinitiation.generated.api`
  - `modelPackage`: `com.bank.paymentinitiation.generated.model`
  - `invokerPackage`: `com.bank.paymentinitiation.generated.invoker`
- **ConfigOptions**:
  - `interfaceOnly: 'true'`: Solo genera interfaces, no implementaciones
  - `useSpringBoot3: 'true'`: Usa anotaciones y dependencias de Spring Boot 3
  - `useTags: 'true'`: Usa tags del OpenAPI para organizar endpoints
  - `dateLibrary: 'java8'`: Usa tipos de fecha de Java 8+ (LocalDate, LocalDateTime)
  - `serializationLibrary: 'jackson'`: Usa Jackson para serialización JSON
  - `hideGenerationTimestamp: 'true'`: No incluye timestamp en código generado (mejor para versionado)

**Justificación**:
- `interfaceOnly: true` permite implementar los controladores manualmente siguiendo arquitectura hexagonal
- `useSpringBoot3: true` asegura compatibilidad con Spring Boot 3.2.0
- `dateLibrary: java8` es compatible con Java 21 y evita dependencias de Joda Time
- `jackson` es el estándar de Spring Boot para serialización JSON
- `hideGenerationTimestamp` evita cambios innecesarios en commits cuando se regenera

**Archivos generados**:
- `build/generated/src/main/java/com/bank/paymentinitiation/generated/api/PaymentOrdersApi.java`
- `build/generated/src/main/java/com/bank/paymentinitiation/generated/model/*.java` (8 clases)

---

### Decisión 2: Integración con Source Sets

**Fecha**: 2025-01-27

**Decisión**: Agregar el directorio generado como sourceDir del sourceSet main:
```groovy
sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/src/main/java")
        }
    }
}
```

**Justificación**:
- Permite que el código generado sea compilado automáticamente
- No requiere configuración adicional en el IDE
- El código generado está disponible en tiempo de compilación

---

### Decisión 3: Dependencia de Tareas

**Fecha**: 2025-01-27

**Decisión**: Hacer que `compileJava` dependa de `openApiGenerate`:
```groovy
tasks.named('compileJava') {
    dependsOn tasks.openApiGenerate
}
```

**Justificación**:
- Asegura que el código se genere antes de compilar
- Evita errores de compilación por clases faltantes
- Automatiza el proceso de build

---

### Decisión 4: Dependencias Adicionales para Código Generado

**Fecha**: 2025-01-27

**Contexto**: El código generado por OpenAPI Generator requiere dependencias adicionales que no están incluidas por defecto en Spring Boot.

**Dependencias agregadas**:

1. **`io.swagger.core.v3:swagger-annotations:2.2.21`**
   - **Propósito**: Anotaciones Swagger/OpenAPI para documentación de la API
   - **Uso**: El código generado usa anotaciones como `@Operation`, `@ApiResponse`, `@Tag`
   - **Justificación**: Necesario para que las interfaces generadas compilen correctamente

2. **`org.openapitools:jackson-databind-nullable:0.2.6`**
   - **Propósito**: Soporte para campos nullable en Jackson
   - **Uso**: Permite manejar campos opcionales/nullable en los DTOs generados
   - **Justificación**: El código generado usa `@JsonNullable` para campos opcionales

3. **`jakarta.validation:jakarta.validation-api:3.0.2`**
   - **Propósito**: API de validación Jakarta (Bean Validation)
   - **Uso**: Anotaciones de validación como `@NotNull`, `@Size`, `@Pattern` en los modelos generados
   - **Justificación**: El código generado incluye anotaciones de validación que requieren esta API

4. **`jakarta.annotation:jakarta.annotation-api:2.1.1`**
   - **Propósito**: Anotaciones Jakarta estándar
   - **Uso**: Anotaciones como `@Generated`, `@Nullable`, `@Nonnull`
   - **Justificación**: El código generado usa anotaciones Jakarta estándar

**Justificación general**:
- Estas dependencias son requeridas por el código generado por OpenAPI Generator
- Spring Boot Starter Web incluye algunas de estas, pero no todas en las versiones correctas
- Agregarlas explícitamente asegura compatibilidad y evita conflictos de versiones
- Son dependencias de runtime, no solo de compilación

**Trade-offs**:
- ✅ Asegura que el código generado compile y funcione correctamente
- ✅ Versiones explícitas evitan conflictos
- ⚠️ Aumenta ligeramente el tamaño del JAR final (dependencias pequeñas)

**Verificación**:
- ✅ `./gradlew openApiGenerate` ejecutado exitosamente
- ✅ `./gradlew compileJava` ejecutado exitosamente
- ✅ Código generado compila sin errores
- ✅ Todas las dependencias resueltas correctamente

---

## Fase 5: Arquitectura Hexagonal y Estructura de Paquetes

### Decisión 1: Adopción de Arquitectura Hexagonal (Ports & Adapters)

**Fecha**: 2025-01-27

**Contexto**: Necesidad de organizar el código del microservicio de manera que separe claramente las preocupaciones de negocio (dominio) de las preocupaciones técnicas (infraestructura), facilitando el mantenimiento, testing y evolución del sistema.

**Decisión**: Adoptar Arquitectura Hexagonal (también conocida como Ports & Adapters) como patrón arquitectónico principal del proyecto.

**Justificación**:
- **Independencia del dominio**: El dominio (lógica de negocio) no depende de frameworks ni tecnologías específicas
- **Testabilidad**: Facilita el testing unitario al poder mockear fácilmente los puertos
- **Flexibilidad**: Permite cambiar la infraestructura (base de datos, frameworks) sin afectar el dominio
- **Alineación con BIAN**: BIAN recomienda separación clara entre lógica de negocio y adaptadores
- **Mantenibilidad**: Código más organizado y fácil de entender

**Principios aplicados**:
- **Dependency Inversion**: Las capas internas (dominio) definen interfaces, las capas externas (adaptadores) las implementan
- **Separation of Concerns**: Cada capa tiene una responsabilidad clara
- **Framework Independence**: El dominio no conoce Spring, JPA, etc.

**Trade-offs**:
- ✅ Mayor testabilidad y mantenibilidad
- ✅ Facilita cambios en infraestructura
- ✅ Código más organizado y claro
- ⚠️ Más capas y abstracciones (ligeramente más complejo inicialmente)
- ⚠️ Requiere más archivos/clases (pero mejor organizados)

---

### Decisión 2: Organización de Paquetes

**Fecha**: 2025-01-27

**Decisión**: Organizar el código en las siguientes capas y paquetes:

#### 2.1. Domain Layer (`domain/`)

**Propósito**: Contiene la lógica de negocio pura, sin dependencias de frameworks.

**Paquetes**:
- `domain/model/`: Entidades de dominio, value objects, enums
  - `PaymentOrder`: Aggregate root
  - `PaymentOrderReference`: Value object
  - `PaymentOrderStatus`: Enum
- `domain/port/in/`: Puertos de entrada (casos de uso/interfaces)
  - `InitiatePaymentOrderUseCase`
  - `RetrievePaymentOrderUseCase`
  - `RetrievePaymentOrderStatusUseCase`
- `domain/port/out/`: Puertos de salida (repositorios/interfaces)
  - `PaymentOrderRepository`
- `domain/exception/`: Excepciones de dominio
  - `DomainException`: Base exception
  - `PaymentOrderNotFoundException`
  - `InvalidPaymentException`
- `domain/service/`: Servicios de dominio (lógica que no pertenece a una entidad)
  - `PaymentOrderDomainService`

**Reglas**:
- ❌ NO debe tener dependencias de Spring, JPA, o cualquier framework
- ✅ Solo código Java puro
- ✅ Puede usar librerías estándar de Java

#### 2.2. Application Layer (`application/`)

**Propósito**: Orquesta los casos de uso, coordina entre dominio e infraestructura.

**Paquetes**:
- `application/service/`: Implementaciones de casos de uso
  - `InitiatePaymentOrderService`: Implementa `InitiatePaymentOrderUseCase`
  - `RetrievePaymentOrderService`: Implementa `RetrievePaymentOrderUseCase`
  - `RetrievePaymentOrderStatusService`: Implementa `RetrievePaymentOrderStatusUseCase`
- `application/mapper/`: Mappers opcionales para transformaciones complejas

**Reglas**:
- ✅ Puede usar Spring para inyección de dependencias
- ✅ Depende del dominio (usa puertos)
- ✅ No depende directamente de adaptadores (usa interfaces del dominio)

#### 2.3. Adapter In (REST) (`adapter.in.rest/`)

**Propósito**: Adaptador de entrada que recibe peticiones HTTP y las convierte en llamadas a casos de uso.

**Paquetes**:
- `adapter.in.rest/`: Controlador REST
  - `PaymentOrdersController`: Implementa `PaymentOrdersApi` (generado por OpenAPI)
- `adapter.in.rest/mapper/`: Mappers para convertir DTOs ↔ Domain
  - `PaymentOrderRestMapper`: MapStruct mapper

**Reglas**:
- ✅ Depende de dominio (usa casos de uso)
- ✅ Depende de application (usa servicios de aplicación)
- ✅ Usa DTOs generados por OpenAPI Generator (`generated.model`)
- ✅ Convierte excepciones de dominio a respuestas HTTP

#### 2.4. Adapter Out (Persistence) (`adapter.out.persistence/`)

**Propósito**: Adaptador de salida que persiste entidades de dominio usando JPA.

**Paquetes**:
- `adapter.out.persistence/entity/`: Entidades JPA
  - `PaymentOrderEntity`: Entidad JPA
- `adapter.out.persistence/jpa/`: Repositorios Spring Data JPA
  - `PaymentOrderJpaRepository`: Interface Spring Data JPA
- `adapter.out.persistence/mapper/`: Mappers para convertir Domain ↔ Entity
  - `PaymentOrderPersistenceMapper`: MapStruct mapper
- `adapter.out.persistence/PaymentOrderRepositoryAdapter.java`: Implementa `PaymentOrderRepository`

**Reglas**:
- ✅ Implementa interfaces del dominio (`PaymentOrderRepository`)
- ✅ Usa JPA para persistencia
- ✅ Convierte entre entidades JPA y modelos de dominio

#### 2.5. Config (`config/`)

**Propósito**: Configuración de Spring (beans, configuración de aplicación).

**Paquetes**:
- `config/`: Clases de configuración
  - `ApplicationConfig`: Configuración de servicios de aplicación

**Reglas**:
- ✅ Define beans de Spring
- ✅ Configura dependencias entre capas

**Justificación de la estructura**:
- **Clara separación de responsabilidades**: Cada capa tiene un propósito bien definido
- **Fácil navegación**: La estructura de paquetes refleja la arquitectura
- **Escalabilidad**: Fácil agregar nuevos adaptadores (ej: adapter.out.external para llamadas a servicios externos)
- **Alineación con convenciones**: Sigue convenciones estándar de arquitectura hexagonal

---

### Decisión 3: Convenciones de Nomenclatura

**Fecha**: 2025-01-27

**Decisiones de nomenclatura**:

1. **Use Cases (Ports In)**:
   - Sufijo: `UseCase`
   - Ejemplo: `InitiatePaymentOrderUseCase`

2. **Application Services**:
   - Sufijo: `Service`
   - Ejemplo: `InitiatePaymentOrderService`

3. **Repositories (Ports Out)**:
   - Sufijo: `Repository`
   - Ejemplo: `PaymentOrderRepository`

4. **Repository Adapters**:
   - Sufijo: `RepositoryAdapter`
   - Ejemplo: `PaymentOrderRepositoryAdapter`

5. **Mappers**:
   - Sufijo: `Mapper`
   - Ejemplo: `PaymentOrderRestMapper`, `PaymentOrderPersistenceMapper`

6. **Entities (JPA)**:
   - Sufijo: `Entity`
   - Ejemplo: `PaymentOrderEntity`

7. **Controllers**:
   - Sufijo: `Controller`
   - Ejemplo: `PaymentOrdersController`

**Justificación**:
- Nomenclatura consistente facilita la comprensión del código
- Los sufijos indican claramente el rol de cada clase
- Facilita la búsqueda y navegación en el IDE

---

### Decisión 4: Uso de JavaDoc en Clases Base

**Fecha**: 2025-01-27

**Decisión**: Todas las clases/interfaces base creadas incluyen JavaDoc descriptivo que explica:
- El propósito de la clase/interfaz
- Su rol en la arquitectura hexagonal
- Sus responsabilidades
- Dependencias y relaciones con otras clases
- TODO comments indicando qué implementar

**Justificación**:
- Facilita la comprensión del código para nuevos desarrolladores
- Documenta las decisiones arquitectónicas
- Los TODO comments guían la implementación
- Mejora la mantenibilidad a largo plazo

---

## Fase 6: Implementación del Modelo de Dominio

### Decisión 1: Value Objects con Lombok @Value

**Fecha**: 2025-01-27

**Contexto**: Necesidad de crear value objects inmutables para encapsular conceptos del dominio y mantener invariantes de negocio.

**Decisión**: Usar Lombok `@Value` para todos los value objects:
- `PaymentAmount`: Encapsula amount y currency, valida amount > 0
- `PayerReference`: Encapsula referencia del pagador, valida no vacío
- `PayeeReference`: Encapsula referencia del beneficiario, valida no vacío
- `ExternalReference`: Encapsula referencia externa, valida no vacío
- `PaymentOrderReference`: Encapsula referencia de orden, valida patrón PO-[A-Z0-9-]+

**Justificación**:
- `@Value` genera automáticamente: campos finales, constructor, equals/hashCode, toString, getters
- Inmutabilidad garantizada (todos los campos son final)
- Factory methods estáticos (`of()`) para creación controlada con validación
- Validación en constructores privados asegura invariantes
- Código más limpio y menos boilerplate

**Trade-offs**:
- ✅ Inmutabilidad garantizada
- ✅ Validación automática en creación
- ✅ Menos código boilerplate
- ⚠️ Dependencia de Lombok (pero ya está en el proyecto)

---

### Decisión 2: Enum PaymentStatus

**Fecha**: 2025-01-27

**Decisión**: Crear enum `PaymentStatus` con 6 valores siguiendo el ciclo de vida BIAN:
- `INITIATED`: Orden creada y aceptada
- `PENDING`: Orden pendiente de procesamiento
- `PROCESSED`: Orden procesada
- `COMPLETED`: Orden completada/liquidada
- `FAILED`: Orden fallida/rechazada
- `CANCELLED`: Orden cancelada

**Justificación**:
- Alineado con estándares BIAN Payment Initiation
- Estados claros y bien definidos
- Facilita validación de transiciones
- Type-safe (mejor que strings)

**Nota**: Se creó `PaymentStatus` aunque ya existía `PaymentOrderStatus`. Se mantiene `PaymentStatus` como el enum principal del dominio, y `PaymentOrderStatus` puede ser usado para mapeos externos si es necesario.

---

### Decisión 3: Aggregate Root PaymentOrder con Lombok @Builder

**Fecha**: 2025-01-27

**Decisión**: Implementar `PaymentOrder` como aggregate root usando Lombok `@Builder` con `toBuilder = true`:
- Todos los campos como especificado
- Inmutabilidad mediante `@Value` y `@Builder`
- `toBuilder = true` permite crear copias modificadas (útil para cambiar estado)

**Justificación**:
- `@Builder` facilita la creación de instancias complejas
- `toBuilder = true` permite inmutabilidad con capacidad de modificación (patrón builder)
- Facilita métodos como `changeStatus()` que retornan nuevas instancias
- Mantiene el dominio framework-agnostic (sin Spring/JPA)

**Campos del aggregate**:
- `paymentOrderReference`: String (identificador único)
- `externalReference`: ExternalReference (value object)
- `payerReference`: PayerReference (value object)
- `payeeReference`: PayeeReference (value object)
- `instructedAmount`: PaymentAmount (value object)
- `remittanceInformation`: String (opcional, puede ser null)
- `requestedExecutionDate`: LocalDate
- `status`: PaymentStatus (enum)
- `createdAt`: LocalDateTime
- `updatedAt`: LocalDateTime

---

### Decisión 4: Métodos de Dominio en PaymentOrder

**Fecha**: 2025-01-27

**Decisiones sobre métodos de dominio**:

#### 4.1. Método `validate()`

**Propósito**: Valida que todos los campos requeridos estén presentes y no sean null.

**Validaciones**:
- paymentOrderReference no null ni vacío
- externalReference no null
- payerReference no null
- payeeReference no null
- instructedAmount no null (ya validado en value object)
- requestedExecutionDate no null
- status no null
- createdAt no null

**Justificación**:
- Asegura invariantes del aggregate antes de persistir
- Validación centralizada en el dominio
- Lanza `IllegalArgumentException` para violaciones (no excepciones de dominio)

#### 4.2. Método `initiate()`

**Propósito**: Inicia la orden estableciendo status a INITIATED.

**Comportamiento**:
- Verifica que el estado actual permita iniciar (null o ya INITIATED)
- Crea nueva instancia con status INITIATED
- Establece createdAt y updatedAt a ahora

**Justificación**:
- Encapsula la lógica de iniciación en el dominio
- Retorna nueva instancia (inmutabilidad)
- Valida que no se pueda iniciar una orden ya iniciada

#### 4.3. Método `changeStatus()`

**Propósito**: Cambia el estado respetando transiciones válidas.

**Transiciones válidas**:
- `INITIATED` → `PENDING`, `CANCELLED`
- `PENDING` → `PROCESSED`, `CANCELLED`, `FAILED`
- `PROCESSED` → `COMPLETED`, `FAILED`
- `COMPLETED`, `FAILED`, `CANCELLED`: Estados terminales (no permiten transiciones)

**Justificación**:
- Encapsula reglas de negocio sobre transiciones de estado
- Previene transiciones inválidas
- Lanza `IllegalStateException` para transiciones inválidas
- Estados terminales no permiten cambios (regla de negocio)

**Trade-offs**:
- ✅ Lógica de negocio en el dominio (donde debe estar)
- ✅ Type-safe (enum en lugar de strings)
- ✅ Fácil de testear
- ⚠️ Si se agregan nuevos estados, hay que actualizar el método (pero es el lugar correcto)

---

### Decisión 5: Validación en Value Objects

**Fecha**: 2025-01-27

**Decisión**: Todos los value objects validan en sus constructores privados:
- `PaymentAmount`: Valida value > 0 y currency no null/vacío
- `PayerReference`, `PayeeReference`, `ExternalReference`: Validan string no null/vacío
- `PaymentOrderReference`: Valida patrón regex PO-[A-Z0-9-]+

**Justificación**:
- Fail-fast: Errores detectados al crear el objeto
- Invariantes garantizadas: No se pueden crear objetos inválidos
- Factory methods controlan la creación
- Mensajes de error claros

**Trade-offs**:
- ✅ Garantiza objetos válidos siempre
- ✅ No requiere validación externa
- ⚠️ Puede lanzar excepciones en tiempo de creación (pero es el comportamiento deseado)

---

### Decisión 6: Inmutabilidad del Dominio

**Fecha**: 2025-01-27

**Decisión**: Todo el modelo de dominio es inmutable:
- Value objects: `@Value` de Lombok (campos final)
- PaymentOrder: `@Value` + `@Builder` con `toBuilder = true`

**Justificación**:
- Previene modificaciones accidentales
- Facilita concurrencia (objetos inmutables son thread-safe)
- Hace el código más predecible
- Alineado con principios DDD (value objects y entidades inmutables cuando es posible)

**Patrón para modificaciones**:
- Métodos como `changeStatus()` retornan nuevas instancias
- `toBuilder()` permite crear copias modificadas
- Original nunca se modifica

---

## Fase 7: Implementación de Puertos y Servicios de Aplicación

### Decisión 1: Separación de Puertos In/Out

**Fecha**: 2025-01-27

**Contexto**: Necesidad de definir claramente los contratos entre capas siguiendo principios de arquitectura hexagonal.

**Decisión**: Separar puertos en dos categorías:

#### 1.1. Puertos de Entrada (Ports In / Driving Ports)

**Ubicación**: `domain.port.in`

**Propósito**: Definen los casos de uso que el dominio expone. Son llamados por los adaptadores de entrada (REST controllers).

**Interfaces**:
- `InitiatePaymentOrderUseCase`: Caso de uso para iniciar una orden
- `RetrievePaymentOrderUseCase`: Caso de uso para recuperar una orden completa
- `RetrievePaymentOrderStatusUseCase`: Caso de uso para recuperar solo el estado

**Características**:
- Usan tipos del dominio (PaymentOrder, PaymentStatus)
- Usan String para referencias (simplifica la interfaz, la conversión a PaymentOrderReference se hace en la implementación)
- Lanzan excepciones de dominio

**Justificación**:
- Separación clara entre lo que el dominio expone (ports in) y lo que necesita (ports out)
- Facilita el testing (se pueden mockear fácilmente)
- El dominio controla sus contratos

#### 1.2. Puertos de Salida (Ports Out / Driven Ports)

**Ubicación**: `domain.port.out`

**Propósito**: Definen las dependencias que el dominio necesita. Son implementados por los adaptadores de salida (persistence, external services).

**Interfaces**:
- `PaymentOrderRepository`: Contrato para persistir y recuperar órdenes

**Características**:
- Usan tipos del dominio (PaymentOrder)
- Usan String para referencias (simplifica la interfaz)
- Retornan Optional para casos donde el recurso puede no existir

**Justificación**:
- El dominio define qué necesita, no cómo se implementa
- Permite cambiar la implementación (JPA, JDBC, etc.) sin afectar el dominio
- Facilita el testing (se pueden mockear fácilmente)

---

### Decisión 2: Uso de String en Lugar de Value Objects en Interfaces

**Fecha**: 2025-01-27

**Decisión**: Usar `String` para referencias en las interfaces de puertos en lugar de `PaymentOrderReference` (value object).

**Ejemplos**:
- `RetrievePaymentOrderUseCase.retrieve(String paymentOrderReference)`
- `PaymentOrderRepository.findByReference(String paymentOrderReference)`

**Justificación**:
- **Simplicidad**: Las interfaces son más simples y fáciles de usar
- **Flexibilidad**: Los adaptadores pueden validar y convertir a value objects según sea necesario
- **Menos acoplamiento**: Los adaptadores no necesitan conocer todos los value objects del dominio
- **Conversión controlada**: La conversión de String a PaymentOrderReference se hace donde es necesario (en los servicios de aplicación o adaptadores)

**Trade-offs**:
- ✅ Interfaces más simples
- ✅ Menos dependencias entre capas
- ⚠️ Validación de formato debe hacerse en los servicios/adaptadores (pero es aceptable)

**Alternativa considerada**: Usar PaymentOrderReference directamente
- ❌ Aumenta el acoplamiento
- ❌ Los adaptadores deben conocer value objects del dominio
- ❌ Más complejo para casos simples

---

### Decisión 3: Implementación de Servicios de Aplicación

**Fecha**: 2025-01-27

**Decisión**: Implementar los servicios de aplicación que orquestan los casos de uso:

#### 3.1. InitiatePaymentOrderService

**Responsabilidades**:
1. Validar el payment order (usando domain service)
2. Generar referencia si no existe
3. Iniciar la orden (método `initiate()` del aggregate)
4. Persistir usando el repositorio

**Lógica de negocio**:
- Valida el payment order antes de persistir
- Genera referencia única si no se proporciona
- Usa el método `initiate()` del aggregate para establecer estado y timestamps
- Lanza `InvalidPaymentException` si la validación falla

#### 3.2. RetrievePaymentOrderService

**Responsabilidades**:
1. Validar que la referencia no sea null/vacía
2. Buscar en el repositorio
3. Lanzar excepción si no existe

**Lógica de negocio**:
- Validación básica de entrada (referencia no vacía)
- Delegación al repositorio
- Conversión de Optional vacío a excepción de dominio

#### 3.3. RetrievePaymentOrderStatusService

**Responsabilidades**:
1. Validar que la referencia no sea null/vacía
2. Buscar en el repositorio
3. Extraer y retornar solo el status

**Lógica de negocio**:
- Similar a RetrievePaymentOrderService pero retorna solo el status
- Reutiliza la misma lógica de búsqueda

**Justificación**:
- **Orquestación**: Los servicios coordinan entre dominio e infraestructura
- **Validaciones básicas**: Validan entrada antes de delegar
- **Manejo de excepciones**: Convierten Optional a excepciones de dominio
- **Separación de responsabilidades**: Cada servicio tiene una responsabilidad clara

---

### Decisión 4: PaymentOrderDomainService - Validaciones y Generación

**Fecha**: 2025-01-27

**Decisiones sobre el domain service**:

#### 4.1. Validación de Fecha de Ejecución

**Lógica**: Valida que `requestedExecutionDate` no sea en el pasado.

**Justificación**:
- Regla de negocio importante: no se pueden crear órdenes con fechas pasadas
- Esta validación no pertenece al aggregate (es una regla de negocio más compleja)
- El domain service es el lugar apropiado para validaciones que requieren contexto externo (fecha actual)

#### 4.2. Generación de Referencias

**Formato**: `PO-{UUID}` (ej: PO-550e8400-e29b-41d4-a716-446655440000)

**Justificación**:
- UUID garantiza unicidad
- Formato PO-xxx es consistente con BIAN
- El domain service es responsable de generar identificadores de negocio

**Trade-offs**:
- ✅ UUID garantiza unicidad global
- ✅ Formato consistente
- ⚠️ UUIDs son largos (pero aceptable para referencias de negocio)

**Alternativa considerada**: Secuencial (PO-0001, PO-0002, ...)
- ❌ Requiere coordinación (locks, secuencias)
- ❌ Más complejo en sistemas distribuidos
- ❌ UUID es más simple y escalable

---

### Decisión 5: Manejo de Excepciones de Dominio

**Fecha**: 2025-01-27

**Decisión**: Los servicios de aplicación lanzan excepciones de dominio:
- `InvalidPaymentException`: Para validaciones fallidas
- `PaymentOrderNotFoundException`: Para recursos no encontrados

**Justificación**:
- **Excepciones de dominio**: Representan errores de negocio, no técnicos
- **Manejo en adaptadores**: Los adaptadores (REST controllers) convierten estas excepciones a respuestas HTTP apropiadas
- **Separación de responsabilidades**: El dominio no conoce HTTP, los adaptadores no conocen reglas de negocio

**Mapeo a HTTP** (en adaptadores):
- `InvalidPaymentException` → 400 Bad Request
- `PaymentOrderNotFoundException` → 404 Not Found

**Trade-offs**:
- ✅ Separación clara entre errores de negocio y técnicos
- ✅ Facilita el manejo de errores en adaptadores
- ⚠️ Requiere conversión en adaptadores (pero es el lugar correcto)

---

### Decisión 6: Actualización de PaymentOrderNotFoundException

**Fecha**: 2025-01-27

**Decisión**: Cambiar `PaymentOrderNotFoundException` para usar `String` en lugar de `PaymentOrderReference`.

**Justificación**:
- Consistencia con las interfaces de puertos (que usan String)
- Simplifica el uso de la excepción
- La validación del formato se puede hacer en los servicios si es necesario

**Trade-offs**:
- ✅ Consistencia con interfaces
- ✅ Más simple de usar
- ⚠️ No valida formato automáticamente (pero se puede validar antes de lanzar)

---

## Fase 8: Configuración de H2 y Entidad JPA

### Decisión 1: Configuración de H2 en Memoria

**Fecha**: 2025-01-27

**Contexto**: Necesidad de configurar una base de datos para persistencia durante desarrollo y testing.

**Decisión**: Usar H2 en memoria con las siguientes configuraciones:
- URL: `jdbc:h2:mem:paymentdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- Consola H2 habilitada en `/h2-console`
- JPA con `ddl-auto: update`
- Actuator con endpoints health e info

**Configuración específica**:
- `DB_CLOSE_DELAY=-1`: Mantiene la base de datos en memoria mientras la JVM esté activa
- `DB_CLOSE_ON_EXIT=FALSE`: No cierra la base de datos al cerrar la aplicación
- `ddl-auto: update`: Actualiza el esquema automáticamente (útil para desarrollo)

**Justificación**:
- **H2 en memoria**: Rápido, no requiere instalación, perfecto para desarrollo/testing
- **Consola H2**: Facilita debugging y verificación de datos durante desarrollo
- **ddl-auto: update**: Automatiza la creación/actualización de tablas
- **Actuator**: Proporciona endpoints de health check para monitoreo

**Trade-offs**:
- ✅ Rápido y simple para desarrollo
- ✅ No requiere configuración de base de datos externa
- ⚠️ Datos se pierden al reiniciar (aceptable para desarrollo/testing)
- ⚠️ No adecuado para producción (pero se puede cambiar fácilmente)

---

### Decisión 2: Estructura de PaymentOrderEntity

**Fecha**: 2025-01-27

**Decisión**: Crear `PaymentOrderEntity` con:
- **UUID como ID técnico** (primary key, generado automáticamente)
- **paymentOrderReference como identificador de negocio** (único, indexado)
- **Campos aplanados** de value objects del dominio
- **Enum separado** para status (PaymentOrderStatusEntity)

**Campos de la entidad**:
- `id`: UUID (técnico, generado)
- `paymentOrderReference`: String (negocio, único)
- `externalReference`: String (de ExternalReference.value)
- `payerReference`: String (de PayerReference.value)
- `payeeReference`: String (de PayeeReference.value)
- `amount`: BigDecimal (de PaymentAmount.value)
- `currency`: String (de PaymentAmount.currency)
- `remittanceInformation`: String (opcional)
- `requestedExecutionDate`: LocalDate
- `status`: PaymentOrderStatusEntity (enum)
- `createdAt`: LocalDateTime
- `updatedAt`: LocalDateTime

**Justificación**:
- **UUID como ID técnico**: Evita problemas de secuencias, único globalmente, no expone información de negocio
- **paymentOrderReference como identificador de negocio**: Es el identificador que usa el dominio y los clientes
- **Campos aplanados**: Simplifica el mapeo JPA, value objects se reconstruyen en el mapper
- **Enum separado**: Mantiene independencia entre persistencia y dominio

**Trade-offs**:
- ✅ UUID garantiza unicidad sin coordinación
- ✅ paymentOrderReference es el identificador de negocio (no el ID técnico)
- ✅ Campos aplanados simplifican JPA
- ⚠️ Requiere mapper para reconstruir value objects (pero es el lugar correcto)

---

### Decisión 3: UUID vs paymentOrderReference como Primary Key

**Fecha**: 2025-01-27

**Decisión**: Usar UUID como primary key técnico y paymentOrderReference como identificador de negocio único.

**Justificación**:
- **UUID como PK**:
  - No expone información de negocio
  - Único globalmente
  - No requiere secuencias o coordinación
  - Mejor para sistemas distribuidos
  
- **paymentOrderReference como identificador de negocio**:
  - Es el identificador que usan los clientes
  - Formato legible (PO-xxx)
  - Indexado único para búsquedas rápidas
  - No es la primary key (evita problemas si cambia el formato)

**Alternativa considerada**: Usar paymentOrderReference como primary key
- ❌ Si cambia el formato, requiere migración compleja
- ❌ Mezcla identificador de negocio con clave técnica
- ❌ UUID es más flexible y escalable

**Trade-offs**:
- ✅ Separación clara entre ID técnico e identificador de negocio
- ✅ Flexibilidad para cambiar formato de referencia sin afectar PK
- ⚠️ Requiere índice adicional en paymentOrderReference (pero es necesario para búsquedas)

---

### Decisión 4: Aplanamiento de Value Objects

**Fecha**: 2025-01-27

**Decisión**: Aplanar value objects del dominio en campos simples de la entidad:
- `PayerReference.value` → `payerReference` (String)
- `PayeeReference.value` → `payeeReference` (String)
- `ExternalReference.value` → `externalReference` (String)
- `PaymentAmount.value` → `amount` (BigDecimal)
- `PaymentAmount.currency` → `currency` (String)

**Justificación**:
- **Simplicidad JPA**: JPA maneja mejor tipos simples que objetos anidados
- **Mapeo en mapper**: El mapper (PaymentOrderPersistenceMapper) se encarga de reconstruir value objects
- **Independencia**: La entidad no depende de value objects del dominio
- **Performance**: Menos joins, consultas más simples

**Trade-offs**:
- ✅ Más simple para JPA
- ✅ Mejor performance (menos joins)
- ⚠️ Requiere mapper para reconstruir value objects (pero es el lugar correcto)
- ⚠️ Validación de value objects se hace en el mapper (no en la entidad)

---

### Decisión 5: Enum Separado para Status

**Fecha**: 2025-01-27

**Decisión**: Crear `PaymentOrderStatusEntity` enum separado en el paquete `entity`, independiente del enum `PaymentStatus` del dominio.

**Justificación**:
- **Independencia de capas**: La capa de persistencia no depende del dominio
- **Flexibilidad**: Permite tener valores diferentes si es necesario (aunque en este caso son iguales)
- **Mapeo controlado**: El mapper controla la conversión entre enums
- **Principio de inversión de dependencias**: La persistencia no conoce el dominio directamente

**Mapeo**:
- El mapper (PaymentOrderPersistenceMapper) convierte entre:
  - `PaymentStatus` (dominio) ↔ `PaymentOrderStatusEntity` (persistencia)

**Trade-offs**:
- ✅ Independencia entre capas
- ✅ Flexibilidad para cambios futuros
- ⚠️ Duplicación de valores (pero mantiene separación de responsabilidades)

---

### Decisión 6: Timestamps Automáticos con Callbacks JPA

**Fecha**: 2025-01-27

**Decisión**: Usar callbacks JPA (`@PrePersist`, `@PreUpdate`) para establecer timestamps automáticamente:
- `createdAt`: Se establece en `@PrePersist` si es null
- `updatedAt`: Se establece en `@PrePersist` y `@PreUpdate`

**Justificación**:
- **Automático**: No requiere lógica manual en servicios
- **Consistente**: Todos los registros tienen timestamps
- **Auditoría**: Facilita el tracking de cambios

**Trade-offs**:
- ✅ Automático y consistente
- ✅ No requiere lógica manual
- ⚠️ Depende de JPA (pero es aceptable en la capa de persistencia)

---

### Decisión 7: Índice Único en paymentOrderReference

**Fecha**: 2025-01-27

**Decisión**: Crear índice único en `paymentOrderReference` usando `@Index` en `@Table`.

**Justificación**:
- **Búsquedas rápidas**: El método `findByPaymentOrderReference` es muy usado
- **Unicidad garantizada**: El índice único asegura que no haya duplicados
- **Performance**: Índice mejora el rendimiento de búsquedas

**Trade-offs**:
- ✅ Búsquedas rápidas
- ✅ Unicidad garantizada a nivel de base de datos
- ⚠️ Overhead mínimo en inserts (aceptable)

---

## Fase 9: Configuración de MapStruct y Mappers

### Decisión 1: Configuración de MapStruct

**Fecha**: 2025-01-27

**Contexto**: Necesidad de configurar MapStruct para transformaciones entre capas (DTO ↔ Domain ↔ Entity).

**Decisión**: Usar MapStruct 1.5.5.Final con configuración:
- `componentModel = "spring"`: Genera beans Spring para inyección de dependencias
- `unmappedTargetPolicy = ReportingPolicy.ERROR`: Falla la compilación si hay campos no mapeados

**Justificación**:
- **componentModel = "spring"**: Permite inyectar mappers como beans Spring
- **unmappedTargetPolicy = ERROR**: Detecta errores de mapeo en tiempo de compilación
- **MapStruct**: Genera código en tiempo de compilación (mejor performance que reflection)

**Dependencias**:
- `implementation "org.mapstruct:mapstruct:1.5.5.Final"`
- `annotationProcessor "org.mapstruct:mapstruct-processor:1.5.5.Final"`
- `annotationProcessor "org.projectlombok:lombok-mapstruct-binding:0.2.0"` (compatibilidad Lombok)

**Trade-offs**:
- ✅ Compile-time code generation (rápido, type-safe)
- ✅ Detecta errores en tiempo de compilación
- ⚠️ Requiere recompilación si cambian los modelos (pero es el comportamiento esperado)

---

### Decisión 2: PaymentOrderRestMapper - Mapeo DTO ↔ Domain

**Fecha**: 2025-01-27

**Decisión**: Crear mapper REST con 4 métodos principales y métodos @Named para conversiones complejas.

#### 2.1. Método toDomain con paymentOrderReference como parámetro

**Firma**: `PaymentOrder toDomain(InitiatePaymentOrderRequest request, String paymentOrderReference)`

**Justificación**:
- `paymentOrderReference` se genera en el controlador (no en el mapper)
- El mapper no debe tener responsabilidad de generar identificadores
- Permite flexibilidad en cómo se genera la referencia

**Mapeos**:
- `Account.iban` → `PayerReference` / `PayeeReference` (value objects)
- `PaymentAmount` (generated, Double) → `PaymentAmount` (domain, BigDecimal)
- `externalReference` (String) → `ExternalReference` (value object)
- `status`, `createdAt`, `updatedAt` se ignoran (se establecen en dominio)

#### 2.2. Métodos de respuesta (toInitiateResponse, toRetrieveResponse, toStatusResponse)

**Conversiones necesarias**:
- `PaymentStatus` (domain) → `PaymentOrderStatus` (generated enum)
- Value objects → `Account` y `PaymentAmount` (generated)
- `LocalDateTime` → `OffsetDateTime` (para timestamps)

**Justificación**:
- Los DTOs de OpenAPI usan `OffsetDateTime` (estándar ISO 8601 con timezone)
- El dominio usa `LocalDateTime` (más simple, asume UTC)
- La conversión se hace en el mapper usando `ZoneOffset.UTC`

---

### Decisión 3: PaymentOrderPersistenceMapper - Mapeo Domain ↔ Entity

**Fecha**: 2025-01-27

**Decisión**: Crear mapper de persistencia que aplane value objects y maneje conversiones de enums.

#### 3.1. Método toEntity (Domain → Entity)

**Mapeos**:
- Value objects aplanados: `PayerReference.value` → `payerReference` (String)
- `PaymentAmount.value` → `amount` (BigDecimal)
- `PaymentAmount.currency` → `currency` (String)
- `PaymentStatus` (domain) → `PaymentOrderStatusEntity` (persistence)
- `id` se ignora (generado por JPA)

**Justificación**:
- JPA maneja mejor tipos simples que objetos anidados
- El aplanamiento simplifica el esquema de base de datos
- El mapper reconstruye value objects al cargar

#### 3.2. Método toDomain (Entity → Domain)

**Mapeos**:
- Campos simples → Value objects: `payerReference` (String) → `PayerReference`
- `amount` + `currency` → `PaymentAmount` (value object)
- `PaymentOrderStatusEntity` → `PaymentStatus` (domain)

**Justificación**:
- Reconstruye value objects desde campos aplanados
- Mantiene la inmutabilidad del dominio
- El dominio no conoce la estructura de la base de datos

---

### Decisión 4: Manejo de Ambigüedades de Tipos

**Fecha**: 2025-01-27

**Problema**: `PaymentAmount` y `PaymentStatus` existen tanto en el dominio como en `generated.model`, causando ambigüedades en MapStruct.

**Decisión**: Usar nombres completamente calificados (fully qualified names) en métodos @Named:
- `com.bank.paymentinitiation.generated.model.PaymentAmount` en lugar de solo `PaymentAmount`
- Esto evita ambigüedades en tiempo de compilación

**Ejemplo**:
```java
@Named("generatedPaymentAmountToDomain")
default PaymentAmount generatedPaymentAmountToDomain(
        com.bank.paymentinitiation.generated.model.PaymentAmount generatedAmount) {
    // ...
}
```

**Justificación**:
- **Claridad**: Deja explícito qué tipo se está usando
- **Evita errores**: MapStruct puede resolver correctamente los tipos
- **Mantenibilidad**: Facilita entender el código

**Trade-offs**:
- ✅ Elimina ambigüedades completamente
- ✅ Código más explícito
- ⚠️ Nombres más largos (pero aceptable para claridad)

---

### Decisión 5: Conversión de Timestamps (LocalDateTime → OffsetDateTime)

**Fecha**: 2025-01-27

**Problema**: Los DTOs de OpenAPI usan `OffsetDateTime` pero el dominio usa `LocalDateTime`.

**Decisión**: Crear método @Named `localDateTimeToOffsetDateTime` que:
- Convierte `LocalDateTime` a `OffsetDateTime` usando `ZoneOffset.UTC`
- Retorna `null` si el input es `null`

**Implementación**:
```java
@Named("localDateTimeToOffsetDateTime")
default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
    return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
}
```

**Justificación**:
- **OpenAPI estándar**: OffsetDateTime es el estándar para timestamps en APIs REST
- **Dominio simple**: LocalDateTime es más simple y suficiente para el dominio
- **UTC por defecto**: Asume UTC para simplificar (puede extenderse si se necesita timezone)

**Trade-offs**:
- ✅ Alineado con estándares OpenAPI
- ✅ Dominio simple (no necesita timezone)
- ⚠️ Asume UTC (pero es común en sistemas bancarios)

---

### Decisión 6: Métodos @Named para Conversiones Complejas

**Fecha**: 2025-01-27

**Decisión**: Usar métodos @Named para todas las conversiones que no son directas:

**PaymentOrderRestMapper**:
- `stringToExternalReference`: String → ExternalReference
- `ibanToPayerReference`: String (IBAN) → PayerReference
- `ibanToPayeeReference`: String (IBAN) → PayeeReference
- `payerReferenceToAccount`: PayerReference → Account (generated)
- `payeeReferenceToAccount`: PayeeReference → Account (generated)
- `generatedPaymentAmountToDomain`: PaymentAmount (generated) → PaymentAmount (domain)
- `domainPaymentAmountToGenerated`: PaymentAmount (domain) → PaymentAmount (generated)
- `domainStatusToGenerated`: PaymentStatus → PaymentOrderStatus
- `localDateTimeToOffsetDateTime`: LocalDateTime → OffsetDateTime

**PaymentOrderPersistenceMapper**:
- `stringToExternalReference`: String → ExternalReference
- `stringToPayerReference`: String → PayerReference
- `stringToPayeeReference`: String → PayeeReference
- `entityToPaymentAmount`: PaymentOrderEntity → PaymentAmount (combina amount + currency)
- `domainStatusToEntity`: PaymentStatus → PaymentOrderStatusEntity
- `entityStatusToDomain`: PaymentOrderStatusEntity → PaymentStatus

**Justificación**:
- **Reutilización**: Los métodos @Named pueden reutilizarse en múltiples mapeos
- **Claridad**: Hace explícitas las conversiones complejas
- **Testabilidad**: Los métodos pueden testearse independientemente
- **Mantenibilidad**: Fácil modificar conversiones en un solo lugar

---

### Decisión 7: Conversión de PaymentAmount (Double ↔ BigDecimal)

**Fecha**: 2025-01-27

**Problema**: Los DTOs generados usan `Double` para amount, pero el dominio usa `BigDecimal`.

**Decisión**: Convertir en métodos @Named:
- `generatedPaymentAmountToDomain`: `Double` → `BigDecimal` usando `BigDecimal.valueOf()`
- `domainPaymentAmountToGenerated`: `BigDecimal` → `Double` usando `doubleValue()`

**Justificación**:
- **BigDecimal en dominio**: Precisión exacta para montos monetarios (mejor práctica)
- **Double en DTOs**: OpenAPI Generator genera Double por defecto
- **Conversión controlada**: El mapper controla la conversión de manera segura

**Trade-offs**:
- ✅ Precisión en el dominio (BigDecimal)
- ✅ Compatibilidad con DTOs generados (Double)
- ⚠️ Conversión en cada mapeo (pero es necesaria)

---

### Decisión 8: Conversión de Enums (PaymentStatus ↔ PaymentOrderStatus)

**Fecha**: 2025-01-27

**Problema**: El dominio usa `PaymentStatus` y los DTOs generados usan `PaymentOrderStatus` (ambos tienen los mismos valores pero son tipos diferentes).

**Decisión**: Convertir usando `name()` y `fromValue()`:
- Domain → Generated: `PaymentOrderStatus.fromValue(domainStatus.name())`
- Generated → Domain: `PaymentStatus.valueOf(generatedStatus.name())`

**Justificación**:
- Ambos enums tienen los mismos valores (INITIATED, PENDING, etc.)
- La conversión por nombre es segura y simple
- Mantiene independencia entre capas

---

## Fase 10: Implementación de Controlador REST y Componentes de Soporte

### Decisión 1: PaymentOrdersController - Implementación de PaymentOrdersApi

**Fecha**: 2025-01-27

**Contexto**: Necesidad de implementar el controlador REST que expone los endpoints de la API.

**Decisión**: Crear `PaymentOrdersController` que implementa `PaymentOrdersApi` (interfaz generada por OpenAPI Generator).

**Características**:
- Implementa la interfaz generada (contract-first)
- Inyecta use cases, mapper y generador de referencias
- Usa `@RestController`, `@RequiredArgsConstructor`, `@Slf4j`
- Logging para trazabilidad

**Endpoints implementados**:

#### 1.1. POST /payment-initiation/payment-orders

**Flujo**:
1. Valida request con `@Valid`
2. Genera `paymentOrderReference` usando `PaymentOrderReferenceGenerator`
3. Mapea DTO → dominio usando `mapper.toDomain(request, paymentOrderReference)`
4. Llama `InitiatePaymentOrderUseCase.initiate()`
5. Mapea dominio → `InitiatePaymentOrderResponse`
6. Retorna `ResponseEntity.status(HttpStatus.CREATED).body(response)`

**Justificación**:
- Validación en el controlador (Bean Validation)
- Generación de referencia antes del mapeo (el mapper la necesita)
- Delegación a use case (lógica de negocio)
- Mapeo a response DTO (separación de capas)

#### 1.2. GET /payment-initiation/payment-orders/{id}

**Flujo**:
1. Llama `RetrievePaymentOrderUseCase.retrieve(id)`
2. Mapea dominio → `RetrievePaymentOrderResponse`
3. Retorna `ResponseEntity.ok(response)`

**Justificación**:
- Delegación directa al use case
- Mapeo simple a response DTO

#### 1.3. GET /payment-initiation/payment-orders/{id}/status

**Flujo**:
1. Llama `RetrievePaymentOrderUseCase.retrieve(id)` (no `RetrievePaymentOrderStatusUseCase`)
2. Mapea dominio → `PaymentOrderStatusResponse` usando `mapper.toStatusResponse()`
3. Retorna `ResponseEntity.ok(response)`

**Decisión importante**: Usa `RetrievePaymentOrderUseCase` en lugar de `RetrievePaymentOrderStatusUseCase` porque:
- La respuesta necesita `paymentOrderReference` y `lastUpdated`
- `RetrievePaymentOrderStatusUseCase` solo retorna `PaymentStatus`
- Es más eficiente usar el use case que retorna la orden completa

**Justificación**:
- La respuesta requiere más información que solo el status
- Evita duplicación de lógica
- El use case completo ya está disponible

---

### Decisión 2: PaymentOrderReferenceGenerator - Generación de Referencias Únicas

**Fecha**: 2025-01-27

**Decisión**: Crear componente Spring que genera referencias únicas en formato "PO-{UUID compacto}".

**Formato**: "PO-{UUID sin guiones, mayúsculas}"
- Ejemplo: "PO-A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6"

**Implementación**:
```java
public String generate() {
    UUID uuid = UUID.randomUUID();
    String compactUuid = uuid.toString().replace("-", "").toUpperCase();
    return "PO-" + compactUuid;
}
```

**Justificación**:
- **UUID garantiza unicidad**: No requiere coordinación ni secuencias
- **Formato compacto**: Sin guiones, más legible
- **Prefijo PO-**: Consistente con BIAN y formato esperado
- **Componente Spring**: Fácil de inyectar y testear

**Trade-offs**:
- ✅ Garantiza unicidad global
- ✅ No requiere coordinación
- ✅ Formato legible
- ⚠️ UUIDs son largos (pero aceptable para referencias de negocio)

**Alternativa considerada**: Usar secuencia numérica (PO-0001, PO-0002, ...)
- ❌ Requiere coordinación (locks, secuencias)
- ❌ Más complejo en sistemas distribuidos
- ❌ UUID es más simple y escalable

---

### Decisión 3: ApplicationConfig - Configuración de Beans

**Fecha**: 2025-01-27

**Decisión**: Crear clase de configuración Spring que define beans para servicios de aplicación.

**Beans definidos**:
1. `PaymentOrderDomainService`: Recibe `PaymentOrderRepository`
2. `InitiatePaymentOrderService`: Recibe `PaymentOrderRepository` y `PaymentOrderDomainService`
3. `RetrievePaymentOrderService`: Recibe `PaymentOrderRepository`
4. `RetrievePaymentOrderStatusService`: Recibe `PaymentOrderRepository`

**Justificación**:
- **Servicios no son componentes**: Los servicios de aplicación no tienen `@Service` porque queremos control explícito
- **Inyección explícita**: Los beans se crean explícitamente con sus dependencias
- **PaymentOrderRepository automático**: `PaymentOrderRepositoryAdapter` tiene `@Component`, Spring lo inyecta automáticamente
- **Orden de creación**: Los beans se crean en el orden correcto (domain service primero)

**Trade-offs**:
- ✅ Control explícito sobre la creación de beans
- ✅ Facilita el testing (pueden mockearse fácilmente)
- ⚠️ Más código de configuración (pero mejora la claridad)

**Alternativa considerada**: Usar `@Service` en servicios de aplicación
- ❌ Menos control sobre la creación
- ❌ Más difícil de testear
- ❌ Configuración implícita (menos clara)

---

### Decisión 4: PaymentOrderRepositoryAdapter - Implementación Completa

**Fecha**: 2025-01-27

**Decisión**: Completar la implementación del adaptador de repositorio.

#### 4.1. Método save()

**Implementación**:
```java
public PaymentOrder save(PaymentOrder order) {
    // 1. Convert domain to entity
    PaymentOrderEntity entity = persistenceMapper.toEntity(order);
    
    // 2. Save entity
    PaymentOrderEntity savedEntity = jpaRepository.save(entity);
    
    // 3. Convert entity back to domain
    PaymentOrder savedOrder = persistenceMapper.toDomain(savedEntity);
    
    // 4. Return domain model
    return savedOrder;
}
```

**Justificación**:
- **Conversión bidireccional**: Domain → Entity → Domain
- **JPA maneja ID**: El mapper ignora el ID, JPA lo genera
- **Retorna dominio**: El adaptador siempre retorna el modelo de dominio

#### 4.2. Método findByReference()

**Implementación**:
```java
public Optional<PaymentOrder> findByReference(String paymentOrderReference) {
    // 1. Find entity by reference string
    Optional<PaymentOrderEntity> entityOptional = jpaRepository.findByPaymentOrderReference(paymentOrderReference);
    
    // 2. Convert entity to domain if found
    if (entityOptional.isEmpty()) {
        return Optional.empty();
    }
    
    PaymentOrder domain = persistenceMapper.toDomain(entityOptional.get());
    
    // 3. Return Optional
    return Optional.of(domain);
}
```

**Justificación**:
- **Busca por referencia de negocio**: Usa `findByPaymentOrderReference` del JPA repository
- **Conversión condicional**: Solo convierte si la entidad existe
- **Retorna Optional**: Consistente con el contrato del puerto

---

### Decisión 5: PaymentOrderDomainService - Constructor con Repository

**Fecha**: 2025-01-27

**Decisión**: Agregar constructor a `PaymentOrderDomainService` que recibe `PaymentOrderRepository`.

**Justificación**:
- **Necesario para ApplicationConfig**: El bean necesita el constructor para inyectar dependencias
- **Futuras validaciones**: El domain service podría necesitar el repositorio para validaciones complejas (ej: unicidad de external reference)
- **Flexibilidad**: Permite agregar lógica que requiera acceso al repositorio

**Trade-offs**:
- ✅ Permite validaciones más complejas en el futuro
- ✅ Facilita la creación del bean en ApplicationConfig
- ⚠️ El domain service ahora depende del repositorio (pero es aceptable para validaciones)

---

### Decisión 6: Uso de RetrievePaymentOrderUseCase en retrievePaymentOrderStatus

**Fecha**: 2025-01-27

**Problema**: Existen dos use cases:
- `RetrievePaymentOrderUseCase`: Retorna `PaymentOrder` completo
- `RetrievePaymentOrderStatusUseCase`: Retorna solo `PaymentStatus`

**Decisión**: Usar `RetrievePaymentOrderUseCase` en el endpoint de status.

**Justificación**:
- **Respuesta requiere más información**: `PaymentOrderStatusResponse` necesita `paymentOrderReference` y `lastUpdated`, no solo el status
- **Evita duplicación**: No necesita crear otro use case que retorne solo parte de la orden
- **Eficiencia aceptable**: Obtener la orden completa es aceptable para incluir todos los campos necesarios

**Trade-offs**:
- ✅ Respuesta completa con todos los campos necesarios
- ✅ Evita duplicación de lógica
- ⚠️ Obtiene más datos de los estrictamente necesarios (pero aceptable)

**Alternativa considerada**: Usar `RetrievePaymentOrderStatusUseCase` y construir la respuesta manualmente
- ❌ Requeriría obtener `paymentOrderReference` y `lastUpdated` de otra fuente
- ❌ Más complejo y propenso a errores
- ❌ No hay beneficio real en obtener solo el status

---

## Fase 11: Implementación de GlobalExceptionHandler

### Decisión 1: Manejo Global de Excepciones con RFC 7807

**Fecha**: 2025-01-27

**Contexto**: Necesidad de convertir excepciones de dominio a respuestas HTTP siguiendo estándares.

**Decisión**: Crear `GlobalExceptionHandler` usando `@RestControllerAdvice` y `org.springframework.http.ProblemDetail` (Spring 6+).

**Justificación**:
- **RFC 7807**: Estándar para Problem Details for HTTP APIs
- **ProblemDetail de Spring**: Implementación nativa de Spring 6+ que serializa automáticamente a `application/problem+json`
- **@RestControllerAdvice**: Maneja excepciones de todos los controladores REST
- **Separación de responsabilidades**: El adaptador convierte excepciones de dominio a HTTP

**Características**:
- Content-type automático: `application/problem+json` (Spring lo maneja automáticamente)
- Estructura estándar: title, status, detail, properties adicionales
- Logging diferenciado: WARN para errores de negocio, ERROR para errores técnicos

---

### Decisión 2: Mapeo de Excepciones de Dominio a HTTP

**Fecha**: 2025-01-27

**Decisión**: Mapear excepciones de dominio a códigos HTTP apropiados:

#### 2.1. PaymentOrderNotFoundException → 404 NOT FOUND

**Mapeo**:
- HTTP Status: 404
- Title: "Payment Order Not Found"
- Detail: Mensaje de la excepción
- Property adicional: `paymentOrderReference`

**Justificación**:
- **404 es apropiado**: El recurso no existe
- **Property adicional**: Incluye el paymentOrderReference para facilitar debugging
- **Logging WARN**: Es un error de negocio esperado (recurso no encontrado)

#### 2.2. InvalidPaymentException → 400 BAD REQUEST

**Mapeo**:
- HTTP Status: 400
- Title: "Invalid Payment Order"
- Detail: Mensaje de la excepción

**Justificación**:
- **400 es apropiado**: El request es inválido según reglas de negocio
- **Detail útil**: El mensaje de la excepción explica qué está mal
- **Logging WARN**: Es un error de negocio esperado (validación fallida)

#### 2.3. Exception genérica → 500 INTERNAL SERVER ERROR

**Mapeo**:
- HTTP Status: 500
- Title: "Internal Server Error"
- Detail: Mensaje genérico (no expone detalles internos)

**Justificación**:
- **500 es apropiado**: Error inesperado del sistema
- **No expone detalles**: El mensaje genérico no revela información sensible
- **Logging ERROR**: Incluye stack trace completo para debugging interno
- **Seguridad**: No expone detalles de implementación a clientes

---

### Decisión 3: Uso de org.springframework.http.ProblemDetail

**Fecha**: 2025-01-27

**Decisión**: Usar `org.springframework.http.ProblemDetail` en lugar del `ProblemDetail` generado por OpenAPI.

**Justificación**:
- **Nativo de Spring 6+**: Implementación oficial de Spring
- **Serialización automática**: Spring serializa automáticamente a `application/problem+json`
- **API más rica**: Métodos como `forStatusAndDetail()`, `setProperty()`, etc.
- **Consistencia**: Usa la misma implementación que Spring usa internamente

**Trade-offs**:
- ✅ API más rica y fácil de usar
- ✅ Serialización automática a `application/problem+json`
- ✅ Consistente con Spring
- ⚠️ Diferente del ProblemDetail generado por OpenAPI (pero es aceptable, ambos siguen RFC 7807)

**Alternativa considerada**: Usar `ProblemDetail` generado por OpenAPI
- ❌ API menos rica
- ❌ Requeriría configuración manual para content-type
- ❌ Menos integrado con Spring

---

### Decisión 4: Propiedades Adicionales en ProblemDetail

**Fecha**: 2025-01-27

**Decisión**: Incluir propiedades adicionales en ProblemDetail cuando sea útil:
- `paymentOrderReference` en `PaymentOrderNotFoundException`

**Justificación**:
- **RFC 7807 permite properties**: El estándar permite propiedades adicionales
- **Facilita debugging**: El cliente puede ver exactamente qué referencia no se encontró
- **Información útil**: No es información sensible, es útil para el cliente

**Trade-offs**:
- ✅ Facilita debugging y troubleshooting
- ✅ Información útil sin exponer detalles sensibles
- ⚠️ Aumenta ligeramente el tamaño de la respuesta (pero es aceptable)

---

### Decisión 5: Logging Diferenciado por Severidad

**Fecha**: 2025-01-27

**Decisión**: Usar diferentes niveles de logging según el tipo de excepción:
- **WARN**: Para excepciones de negocio (PaymentOrderNotFoundException, InvalidPaymentException)
- **ERROR**: Para excepciones técnicas (Exception genérica)

**Justificación**:
- **WARN para errores de negocio**: Son esperados y no indican problemas del sistema
- **ERROR para errores técnicos**: Indican problemas reales que requieren atención
- **Stack trace solo en ERROR**: No es necesario para errores de negocio, sí para errores técnicos

**Trade-offs**:
- ✅ Facilita el monitoreo y alertas
- ✅ Reduce ruido en logs (errores de negocio no son críticos)
- ✅ Stack trace solo cuando es necesario

---

### Decisión 6: No Exponer Detalles Internos

**Fecha**: 2025-01-27

**Decisión**: En `handleGenericException`, usar un mensaje genérico que no expone detalles internos.

**Mensaje usado**: "An unexpected error occurred. Please contact support if the problem persists."

**Justificación**:
- **Seguridad**: No expone información sensible (stack traces, nombres de clases, etc.)
- **UX**: Mensaje amigable para el usuario
- **Logging interno**: El stack trace completo se registra en logs internos (nivel ERROR)

**Trade-offs**:
- ✅ Seguro (no expone detalles internos)
- ✅ Mensaje amigable
- ⚠️ Menos información para debugging del cliente (pero el stack trace está en logs internos)

---

### Decisión 7: Content-Type application/problem+json

**Fecha**: 2025-01-27

**Decisión**: Usar content-type `application/problem+json` para todas las respuestas de error.

**Implementación**: Automático con `org.springframework.http.ProblemDetail` en Spring 6+.

**Justificación**:
- **RFC 7807 estándar**: El estándar especifica `application/problem+json`
- **Automático en Spring 6+**: Spring serializa ProblemDetail automáticamente con este content-type
- **Consistente**: Todas las respuestas de error usan el mismo formato

**Trade-offs**:
- ✅ Estándar RFC 7807
- ✅ Automático (no requiere configuración manual)
- ✅ Consistente en toda la aplicación

---

## 12. Tests Unitarios - Estrategia y Decisiones

**Fecha**: 2025-11-20

**Decisión**: Implementar tests unitarios siguiendo mejores prácticas de testing para el microservicio Payment Initiation.

**Justificación**:
- Los tests unitarios son fundamentales para garantizar la calidad del código
- Permiten detectar regresiones tempranamente
- Facilitan el refactoring seguro
- Documentan el comportamiento esperado del código

**Estrategia de testing**:
- **Dominio**: Tests exhaustivos del aggregate PaymentOrder (validaciones, transiciones de estado, reglas de negocio)
- **Aplicación**: Tests de servicios con mocks de repositorios
- **Adapters**: Tests de mappers para verificar transformaciones correctas

**Cobertura objetivo**:
- Dominio (Aggregate, Value Objects): 85-95%
- Servicios de Aplicación: 90-100%
- Mappers: cubrir campos clave (IDs, referencias, monto, status)

**Casos de prueba críticos**:
1. **PaymentOrder aggregate**:
   - Validaciones de campos requeridos (null, blank)
   - Transiciones válidas de estado (INITIATED→PENDING, PENDING→PROCESSED, etc.)
   - Transiciones inválidas (INITIATED→PROCESSED directamente, terminal states)
   - Método initiate() y changeStatus()
   - Verificación de updatedAt en transiciones

2. **Servicios de aplicación**:
   - InitiatePaymentOrderService: iniciación exitosa, validación falla, uso de referencia existente, no guardar cuando inválido
   - RetrievePaymentOrderService: recuperación exitosa, not found, input inválido (null/blank)
   - RetrievePaymentOrderStatusService: recuperación de status, not found, input inválido

3. **Mappers**:
   - PaymentOrderRestMapper: Request→Domain, Domain→Response (Initiate, Retrieve, Status)
   - PaymentOrderPersistenceMapper: Domain→Entity, Entity→Domain, conversión de todos los status

**Herramientas y frameworks**:
- JUnit 5 (jupiter) con @DisplayName
- AssertJ para aserciones fluidas
- Mockito con @ExtendWith(MockitoExtension.class) para servicios
- @SpringBootTest para mappers (requieren contexto Spring)

**Convenciones**:
- Naming: should[Behavior]When[Condition]() o should[ExpectedResult]()
- Estructura: Patrón AAA (Arrange-Act-Assert)
- Helpers: Métodos helper para crear datos de prueba (createValidPaymentOrder(), etc.)
- Verificaciones: verify() para interacciones con mocks, assertThat() para aserciones

**Resultado**: 53 tests unitarios implementados, todos pasando.

**Decisiones específicas de implementación**:

1. **PaymentOrderTest**:
   - Se crearon helpers `createValidPaymentOrder()` y `createValidPaymentOrderBuilder()` para evitar código repetido
   - Tests organizados por categorías: validaciones, transiciones válidas, transiciones inválidas, casos especiales
   - Se verifica que `updatedAt` se actualiza cuando cambia el estado
   - Se verifica que cambiar al mismo status retorna la misma instancia (no crea nueva)

2. **InitiatePaymentOrderServiceTest**:
   - Se usa `doNothing()` para mockear `validate()` que es void
   - Se usa `when().thenAnswer()` para mockear `save()` y retornar el mismo order
   - Se verifica que no se guarda cuando el order es inválido

3. **RetrievePaymentOrderServiceTest y RetrievePaymentOrderStatusServiceTest**:
   - Tests similares pero enfocados en diferentes aspectos (order completo vs solo status)
   - Se verifica que no se llama al repositorio cuando el input es inválido (null o blank)

4. **PaymentOrderRestMapperTest**:
   - Requiere `@SpringBootTest` porque MapStruct necesita contexto Spring para inyectar dependencias
   - Se verifica mapeo bidireccional: Request→Domain y Domain→Response
   - Se verifica conversión de value objects (PayerReference → Account, PaymentAmount → generated.PaymentAmount)

5. **PaymentOrderPersistenceMapperTest**:
   - Se verifica aplanamiento de value objects (domain → entity)
   - Se verifica reconstrucción de value objects (entity → domain)
   - Test específico para verificar conversión bidireccional de todos los valores de PaymentStatus enum

**Lecciones aprendidas**:
- El patrón AAA (Arrange-Act-Assert) hace los tests más legibles y mantenibles
- Los helpers y builders reducen significativamente el código repetido
- `@DisplayName` mejora la legibilidad de los reportes de tests
- Mockito `doNothing()` y `doThrow()` son necesarios para métodos void
- `@SpringBootTest` solo es necesario para mappers que requieren contexto Spring
- Mostrar logs completos sin filtros es esencial para depuración correcta

---

## 13. Tests de Integración - Estrategia y Decisiones

**Fecha**: 2025-11-20

**Decisión**: Implementar tests de integración usando Spring Boot Test + WebTestClient para verificar el funcionamiento end-to-end de los endpoints REST.

**Justificación**:
- Los tests de integración verifican que todos los componentes trabajen juntos correctamente
- Validan el flujo completo: HTTP → Controller → Mapper → Use Case → Repository → JPA → H2
- Aseguran que el contrato OpenAPI se cumpla correctamente
- Verifican que las validaciones y el manejo de errores funcionen como se espera

**Estrategia de testing**:
- **Framework**: Spring Boot Test con `@SpringBootTest(webEnvironment = RANDOM_PORT)` y `@AutoConfigureWebTestClient`
- **Base de datos**: H2 real (in-memory) configurada en `application.yml`
- **Sin mocks**: Los tests usan componentes reales para verificar el flujo completo
- **Limpieza de estado**: `@BeforeEach` limpia la base de datos con `repository.deleteAll()`
- **Validación completa**: Se verifican todos los campos de las respuestas usando `jsonPath()`

**Tests implementados**:
1. **POST exitoso**: Verifica creación con todos los campos y validación de respuesta completa
2. **GET exitoso**: Verifica recuperación completa de orden de pago
3. **GET /status exitoso**: Verifica recuperación de solo el estado
4. **404 Not Found**: Verifica manejo de órdenes inexistentes (tanto GET como GET /status)
5. **400 Bad Request**: Verifica validaciones (campos faltantes, amount inválido, fecha inválida)

**Correcciones realizadas**:
1. **OpenAPI currency pattern**: Eliminado `pattern` de `currency` porque los enums no pueden tener validación de patrón. Esto causaba `UnexpectedTypeException` en Bean Validation.
2. **Orden de validación en InitiatePaymentOrderService**: Cambiado para llamar a `initiate()` antes de `validate()`, porque `validate()` requiere `status` y `createdAt` que se establecen en `initiate()`.
3. **UUID en PaymentOrderReference**: Corregido `generateReference()` para eliminar guiones del UUID y cumplir el patrón `PO-[A-Z0-9-]+`.
4. **IBANs en tests**: Cambiados de `EC12DEBTOR`/`EC98CREDITOR` (12 caracteres) a `EC123456789012345678`/`EC987654321098765432` (20 caracteres) para cumplir `minLength: 15`.
5. **Handlers de excepciones**: Agregados handlers para `MethodArgumentNotValidException` (validaciones Bean Validation) y `HttpMessageNotReadableException` (JSON malformado, fechas inválidas).

**Alineación con OpenAPI y Postman**:
- Los endpoints coinciden exactamente con el contrato OpenAPI
- Los JSON de request/response coinciden con los ejemplos de la colección Postman
- Los códigos de estado HTTP son correctos (201, 200, 400, 404, 500)
- Las respuestas de error usan RFC 7807 (`application/problem+json`)

**Cobertura alcanzada**:
- ✅ POST /payment-initiation/payment-orders (creación exitosa)
- ✅ GET /payment-initiation/payment-orders/{id} (recuperación exitosa)
- ✅ GET /payment-initiation/payment-orders/{id}/status (recuperación de estado)
- ✅ Manejo de errores 404 (orden no encontrada)
- ✅ Manejo de errores 400 (validaciones: campos faltantes, amount inválido, fecha inválida)

**Lecciones aprendidas**:
1. Los enums generados por OpenAPI no pueden tener validación `@Pattern` simultánea
2. El orden de las operaciones en servicios de aplicación es crítico (initiate antes de validar)
3. Los tests de integración exponen problemas que los unitarios no detectan (validaciones de Bean Validation, serialización JSON)
4. Es importante manejar todas las excepciones de Spring (`MethodArgumentNotValidException`, `HttpMessageNotReadableException`) para retornar códigos HTTP correctos

## 14. Configuración de Quality Gates (JaCoCo, Checkstyle, SpotBugs)

**Fecha**: 2025-01-27

**Contexto**: Necesidad de configurar los quality gates del proyecto para garantizar calidad de código, cobertura adecuada y detección de problemas potenciales.

---

### Decisión 1: Configuración de JaCoCo - Cobertura de Código

**Fecha**: 2025-01-27

**Decisión**: Configurar JaCoCo con:
- **Cobertura mínima**: 85% a nivel de proyecto
- **Cobertura mínima por clase**: 80% (con exclusiones para código generado, entidades, etc.)
- **Reportes**: HTML y XML habilitados
- **Exclusiones**: Código generado, entidades JPA, implementaciones MapStruct, clase principal, configuración

**Justificación**:
- **85% es un umbral razonable**: Suficiente para garantizar calidad sin ser extremadamente restrictivo
- **Exclusiones apropiadas**: El código generado, entidades JPA (solo getters/setters), y MapStruct impls no necesitan ser testeados directamente
- **Reporte HTML**: Facilita la visualización de cobertura en navegador
- **Verificación automática**: `jacocoTestCoverageVerification` falla el build si no se alcanza el umbral

**Configuración implementada**:
```groovy
jacocoTestCoverageVerification {
    dependsOn jacocoTestReport
    
    violationRules {
        rule {
            limit {
                minimum = 0.85  // 85% mínimo
            }
        }
        
        rule {
            element = 'CLASS'
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.80  // 80% por clase
            }
            excludes = [/* exclusiones */]
        }
    }
    
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                '**/generated/**',
                '**/PaymentOrderEntity.class',
                '**/*MapperImpl.class',
                '**/PaymentInitiationServiceApplication.class',
                '**/config/**'
            ])
        }))
    }
}
```

**Exclusiones aplicadas**:
- `**/generated/**`: Código generado por OpenAPI Generator no debe ser testado
- `**/PaymentOrderEntity.class`: Entidades JPA son principalmente getters/setters
- `**/*MapperImpl.class`: Implementaciones generadas por MapStruct
- `**/PaymentInitiationServiceApplication.class`: Clase principal Spring Boot
- `**/config/**`: Clases de configuración Spring

**Trade-offs**:
- ✅ Garantiza cobertura adecuada del código propio
- ✅ Exclusiones razonables para código que no requiere tests directos
- ⚠️ 85% puede ser difícil de alcanzar inicialmente (pero es un objetivo realista con tests adecuados)

---

### Decisión 2: Configuración de Checkstyle - Estilo de Código

**Fecha**: 2025-01-27

**Decisión**: Configurar Checkstyle con:
- **maxWarnings**: 10 (permite algunos warnings antes de fallar el build)
- **Exclusiones**: Código generado y implementaciones de MapStruct usando `BeforeExecutionExclusionFileFilter`

**Justificación**:
- **maxWarnings: 10**: Permite algunas infracciones menores sin bloquear el build, pero mantiene estándares básicos
- **Exclusiones automáticas**: Evita falsos positivos en código generado que no controlamos
- **BeforeExecutionExclusionFileFilter**: Es la forma recomendada de excluir archivos en Checkstyle 10.x

**Configuración en checkstyle.xml**:
```xml
<module name="BeforeExecutionExclusionFileFilter">
    <property name="fileNamePattern" value="generated/.*"/>
</module>
<module name="BeforeExecutionExclusionFileFilter">
    <property name="fileNamePattern" value=".*MapperImpl\.java$"/>
</module>
```

**Patrones de exclusión**:
- `generated/.*`: Excluye todo el código en paquetes que contengan "generated"
- `.*MapperImpl\.java$`: Excluye todas las implementaciones generadas por MapStruct

**Trade-offs**:
- ✅ Permite mantener estándares sin ser extremadamente restrictivo
- ✅ Exclusiones automáticas evitan falsos positivos
- ⚠️ maxWarnings: 10 puede permitir demasiados warnings (pero es configurable)

---

### Decisión 3: Configuración de SpotBugs - Detección de Bugs

**Fecha**: 2025-01-27

**Decisión**: Configurar SpotBugs con:
- **Effort**: MAX (análisis más exhaustivo)
- **Report Level**: HIGH (solo reportar problemas de alta confianza)
- **Configuración por tarea**: Usar `tasks.named()` con `Effort.valueOf()` y `Confidence.valueOf()` (NO strings directamente)

**Justificación**:
- **Effort MAX**: Más análisis, mejor detección de problemas
- **Report Level HIGH**: Solo problemas de alta confianza, reduce falsos positivos
- **Configuración por tarea**: En SpotBugs 6.0.0+, `effort` y `reportLevel` no aceptan strings directamente, deben usarse los enums correspondientes

**Configuración implementada**:
```groovy
tasks.named('spotbugsMain') {
    effort = com.github.spotbugs.snom.Effort.valueOf('MAX')
    reportLevel = com.github.spotbugs.snom.Confidence.valueOf('HIGH')
    excludeFilter = file("$rootDir/config/spotbugs/exclude.xml")
    reports {
        html.required = true
        xml.required = false
    }
}
```

**Exclusiones en exclude.xml**:
- Código generado (paquetes `com.bank.paymentinitiation.generated`)
- Implementaciones MapStruct (`*MapperImpl`)
- Entidades JPA (`PaymentOrderEntity`)
- Clase principal (`PaymentInitiationServiceApplication`)

**Trade-offs**:
- ✅ Detección exhaustiva de problemas potenciales
- ✅ Solo problemas de alta confianza (reduce falsos positivos)
- ⚠️ Effort MAX puede ser más lento (pero aceptable para quality gates)

**Alternativa considerada**: Usar strings directamente (`effort = 'max'`)
- ❌ En SpotBugs 6.0.0+ esto causa errores de compilación
- ❌ Debe usarse `Effort.valueOf('MAX')` y `Confidence.valueOf('HIGH')`

---

### Decisión 4: Configuración del Task Check - Quality Gates Integrados

**Fecha**: 2025-01-27

**Decisión**: Configurar el task `check` para ejecutar todos los quality gates en el orden correcto:
- Dependencias: `checkstyleMain`, `checkstyleTest`, `spotbugsMain`, `spotbugsTest`, `test`, `jacocoTestCoverageVerification`
- Finalizado por: `jacocoTestReport` (para evitar dependencias circulares)

**Justificación**:
- **Todas las verificaciones**: El task `check` debe ejecutar todas las verificaciones de calidad
- **Orden correcto**: Tests primero, luego verificación de cobertura, luego reporte
- **finalizedBy para jacocoTestReport**: `jacocoTestReport` depende de `test`, pero queremos que se ejecute después de la verificación, no antes

**Configuración implementada**:
```groovy
check {
    dependsOn 'checkstyleMain'
    dependsOn 'checkstyleTest'
    dependsOn 'spotbugsMain'
    dependsOn 'spotbugsTest'
    dependsOn 'test'
    dependsOn 'jacocoTestCoverageVerification'
    finalizedBy 'jacocoTestReport'  // Ejecuta después de los tests para evitar dependencias circulares
}
```

**Orden de ejecución**:
1. Checkstyle (main y test)
2. SpotBugs (main y test)
3. Tests unitarios e integración
4. Verificación de cobertura JaCoCo
5. Generación de reporte JaCoCo (finalizedBy)

**Trade-offs**:
- ✅ Todos los quality gates se ejecutan con un solo comando (`./gradlew check`)
- ✅ Orden correcto garantiza que los reportes se generen después de las verificaciones
- ⚠️ Puede tomar más tiempo (pero es aceptable para quality gates completos)

**Alternativa considerada**: Hacer `jacocoTestReport` depender de `test` en el task `check`
- ❌ Causaría dependencias circulares (check → test → jacocoTestReport → check)
- ✅ `finalizedBy` es la solución correcta

---

### Decisión 5: Exclusiones de Código Generado

**Fecha**: 2025-01-27

**Decisión**: Excluir código generado de todos los quality gates:
- **JaCoCo**: Exclusiones en `afterEvaluate` para código generado
- **Checkstyle**: `BeforeExecutionExclusionFileFilter` con patrón `generated/.*`
- **SpotBugs**: Exclusiones en `exclude.xml` para paquetes `com.bank.paymentinitiation.generated`

**Justificación**:
- **Código generado no debe ser testado/verificado**: No es código propio, es generado automáticamente
- **Falsos positivos**: El código generado puede tener problemas de estilo o bugs que no podemos controlar
- **Consistencia**: Las exclusiones deben ser consistentes en todas las herramientas

**Código generado excluido**:
- OpenAPI Generator: Interfaces y modelos generados desde `openapi.yaml`
- MapStruct: Implementaciones generadas de mappers (`*MapperImpl`)

**Trade-offs**:
- ✅ Evita falsos positivos en código que no controlamos
- ✅ Focus en calidad del código propio
- ⚠️ El código generado no se verifica (pero es aceptable porque no lo escribimos nosotros)

---

### Decisión 6: Exclusiones de Entidades JPA y Configuración

**Fecha**: 2025-01-27

**Decisión**: Excluir entidades JPA y clases de configuración de cobertura JaCoCo.

**Justificación**:
- **Entidades JPA**: Principalmente getters/setters generados por Lombok, no requieren tests directos
- **Clases de configuración**: Configuración de Spring, no contiene lógica de negocio que deba testearse
- **Focus en lógica de negocio**: Los tests deben enfocarse en lógica de negocio, no en boilerplate

**Trade-offs**:
- ✅ Permite enfocar los tests en lógica de negocio importante
- ✅ Evita penalizar cobertura por código que no requiere tests exhaustivos
- ⚠️ Las entidades JPA podrían beneficiarse de algunos tests de validación (pero no son críticos)

---

## 15. Decisión sobre Estructura del README.md

**Fecha**: 2025-01-27

**Decisión**: Crear un README.md completo y estructurado que incluya todas las secciones necesarias para facilitar la comprensión, ejecución y mantenimiento del proyecto.

**Justificación**:
- **Documentación completa**: Un README bien estructurado es la primera impresión del proyecto
- **Facilita onboarding**: Nuevos desarrolladores pueden entender y ejecutar el proyecto rápidamente
- **Referencia rápida**: Comandos comunes y estructura del proyecto documentados
- **Profesionalismo**: Demuestra atención al detalle y buenas prácticas

**Estructura elegida**:
1. **Descripción del proyecto**: Contexto BIAN y migración SOAP → REST
2. **Arquitectura hexagonal**: Diagrama y explicación de la estructura de paquetes
3. **Stack técnico**: Lista completa de tecnologías con versiones
4. **Cómo ejecutar**: Comandos específicos para ejecutar, testear y verificar calidad
5. **Cómo probar con Postman**: Ejemplos de requests/responses y referencia a colección
6. **Uso de IA**: Documentación completa de la carpeta `ai/` y prácticas aplicadas

**Características adicionales**:
- Uso de emojis para mejor legibilidad visual
- Ejemplos de código y JSON para facilitar comprensión
- Referencias a estándares (BIAN, RFC 7807, OpenAPI 3.0)
- Checklist de estado del proyecto
- Estructura visual del proyecto

**Trade-offs**:
- ✅ Documentación completa y profesional
- ✅ Facilita onboarding y mantenimiento
- ✅ Referencia rápida para comandos comunes
- ⚠️ README más largo (pero mejor documentado)

---

## Próximos Pasos

Las siguientes decisiones se documentarán conforme se avance en la implementación:
- Configuración de Docker
- Documentación final
