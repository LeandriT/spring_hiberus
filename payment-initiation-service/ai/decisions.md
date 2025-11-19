# Decisiones de Diseño - Payment Initiation Service

Este archivo documenta las decisiones arquitectónicas, correcciones manuales, trade-offs y justificaciones técnicas tomadas durante el desarrollo del proyecto.

## Propósito

Cada decisión incluye:
- **Contexto**: Situación o problema que llevó a la decisión
- **Opciones consideradas**: Alternativas evaluadas
- **Decisión tomada**: Qué se eligió y por qué
- **Impacto**: Consecuencias de la decisión
- **Correcciones manuales**: Cambios realizados sobre código generado por IA

---

## Decisiones Arquitectónicas

### 1. Estructura del Proyecto - Arquitectura Hexagonal

**Contexto:**
El proyecto requiere migración de SOAP a REST manteniendo el dominio de negocio independiente del framework.

**Opciones consideradas:**
- Arquitectura tradicional en capas (Controller → Service → Repository)
- Arquitectura hexagonal (Ports & Adapters)
- Arquitectura limpia (Clean Architecture)

**Decisión:**
Se adopta arquitectura hexagonal con estructura de paquetes:
- `domain`: Modelos de negocio, puertos in/out, excepciones de dominio
- `application`: Servicios de aplicación que implementan casos de uso
- `adapter.in.rest`: Adaptadores REST que implementan interfaces generadas de OpenAPI
- `adapter.out.persistence`: Adaptadores de persistencia con JPA

**Justificación:**
- Separa el dominio del framework (no depende de Spring)
- Facilita testing del dominio de forma aislada
- Permite cambiar frameworks sin tocar el dominio
- Alineado con principios BIAN de independencia del dominio

**Impacto:**
- Mayor complejidad inicial pero mayor mantenibilidad
- Facilita la evolución futura del sistema

---

### 2. Configuración de SpotBugs en build.gradle

**Contexto:**
Error durante la configuración inicial de SpotBugs: `effort` y `reportLevel` no aceptan strings directamente en la versión 6.0.0 del plugin.

**Opciones consideradas:**
- Usar la configuración por defecto
- Configurar las tareas `spotbugsMain` y `spotbugsTest` individualmente
- Reducir la versión del plugin

**Decisión:**
Se configuran las tareas `spotbugsMain` y `spotbugsTest` de forma explícita con reportes HTML, eliminando la configuración directa de `effort` y `reportLevel` en el bloque `spotbugs {}`.

**Justificación:**
- El plugin cambió su API en versiones recientes
- La configuración por tarea es más explícita y compatible
- Se mantiene la funcionalidad de generación de reportes

**Impacto:**
- Configuración más verbosa pero más clara
- Compatible con la versión actual del plugin

---

### 3. Checkstyle Warning en Clase Principal

**Contexto:**
Checkstyle genera un warning sobre la clase `PaymentInitiationServiceApplication` indicando que las clases de servicios públicos no deberían tener constructor público o por defecto.

**Opciones consideradas:**
- Suprimir el warning con anotación
- Agregar un constructor privado explícito
- Modificar la regla de Checkstyle para excluir la clase principal
- Ignorar el warning (solo es warning, no error)

**Decisión:**
Mantener el warning sin cambios. Es el patrón estándar de Spring Boot y el warning no impide la compilación.

**Justificación:**
- Es el patrón estándar recomendado por Spring Boot
- El warning no afecta la funcionalidad
- Cambiar esto añadiría complejidad innecesaria
- Puede revisarse más adelante si se considera necesario

**Impacto:**
- Reporte de Checkstyle muestra un warning pero el build pasa correctamente
- Código sigue el patrón estándar de Spring Boot

---

## Correcciones Manuales sobre Código Generado por IA

### 1. Configuración de SpotBugs (PASO 0)

**Código generado:**
```gradle
spotbugs {
    toolVersion = '4.8.3'
    effort = 'max'
    reportLevel = 'high'
}
```

**Error encontrado:**
```
Cannot set the value of extension 'spotbugs' property 'effort' of type com.github.spotbugs.snom.Effort using an instance of type java.lang.String.
```

**Corrección aplicada:**
```gradle
spotbugs {
    toolVersion = '4.8.3'
}

spotbugsMain {
    reports {
        html {
            required = true
            outputLocation = file("$buildDir/reports/spotbugs/main.html")
        }
    }
}

spotbugsTest {
    reports {
        html {
            required = true
            outputLocation = file("$buildDir/reports/spotbugs/test.html")
        }
    }
}
```

**Razón:**
El plugin SpotBugs cambió su API y requiere configuración por tarea en lugar de configuración global para `effort` y `reportLevel`.

---

## Trade-offs

### 1. H2 en Memoria vs. Persistencia en Archivo

**Contexto:**
H2 puede ejecutarse en modo memoria o en archivo.

**Decisión inicial:**
H2 en memoria (`jdbc:h2:mem:paymentdb`) para desarrollo y testing.

**Trade-off:**
- ✅ Ventaja: Más rápido, fácil para tests, no requiere configuración de archivos
- ❌ Desventaja: Los datos se pierden al reiniciar la aplicación

**Consideración futura:**
Para Docker en producción, podría necesitarse modo archivo si se requiere persistencia entre reinicios.

---

## Notas Adicionales

### Uso de MapStruct con Lombok

**Configuración:**
Se incluye `lombok-mapstruct-binding` para compatibilidad entre Lombok y MapStruct.

**Importante:**
El orden de los procesadores de anotaciones es crítico. Lombok debe ejecutarse antes que MapStruct.

---

## Análisis del Servicio SOAP Legacy (PASO 2)

### Fuentes Analizadas

- **WSDL**: `PaymentOrderService.wsdl`
- **XMLs de ejemplo**:
  - `SubmitPaymentOrderRequest.xml` / `SubmitPaymentOrderResponse.xml`
  - `GetPaymentOrderStatusRequest.xml` / `GetPaymentOrderStatusResponse.xml`
- **Colección Postman**: `postman_collection.json` (endpoints REST objetivo)

---

### 1. Operaciones SOAP Disponibles

El servicio legacy `PaymentOrderService` expone las siguientes operaciones:

#### 1.1 SubmitPaymentOrder
- **Operación SOAP**: `SubmitPaymentOrder` (soapAction: "submit")
- **Propósito**: Enviar/crear una nueva orden de pago
- **Request**: `SubmitPaymentOrderRequest`
- **Response**: `SubmitPaymentOrderResponse`

#### 1.2 GetPaymentOrderStatus
- **Operación SOAP**: `GetPaymentOrderStatus` (soapAction: "status")
- **Propósito**: Consultar el estado actual de una orden de pago existente
- **Request**: `GetPaymentOrderStatusRequest`
- **Response**: `GetPaymentOrderStatusResponse`

**Observación**: El servicio legacy NO expone una operación para recuperar los datos completos de una orden de pago (solo su estado). Sin embargo, la colección Postman incluye `GET /payment-initiation/payment-orders/{id}` para recuperar la orden completa. Esto es una mejora funcional en la versión REST.

---

### 2. Estructuras de Datos Principales

#### 2.1 SubmitPaymentOrderRequest (Entrada)
```
- externalId: string (requerido) → Referencia externa del cliente
- debtorIban: string (requerido) → IBAN de la cuenta deudora
- creditorIban: string (requerido) → IBAN de la cuenta acreedora
- amount: decimal (requerido) → Monto del pago
- currency: string (requerido) → Código de moneda (ej: "USD")
- remittanceInfo: string (opcional) → Información de remesas/factura
- requestedExecutionDate: date (requerido) → Fecha solicitada de ejecución
```

#### 2.2 SubmitPaymentOrderResponse (Salida)
```
- paymentOrderId: string → Identificador único de la orden de pago (generado por el sistema)
- status: string → Estado inicial de la orden (ej: "ACCEPTED")
```

#### 2.3 GetPaymentOrderStatusRequest (Entrada)
```
- paymentOrderId: string (requerido) → ID de la orden a consultar
```

#### 2.4 GetPaymentOrderStatusResponse (Salida)
```
- paymentOrderId: string → ID de la orden consultada
- status: string → Estado actual de la orden (ej: "SETTLED")
- lastUpdate: dateTime → Última fecha/hora de actualización del estado
```

---

### 3. Estados Posibles de la Orden de Pago

Basado en los XMLs de ejemplo, se identifican los siguientes estados:

**Estados encontrados en ejemplos:**
- `ACCEPTED` → Orden aceptada al momento de la creación
- `SETTLED` → Orden liquidada/completada

**Estados inferidos para ciclo de vida completo (según BIAN y mejores prácticas):**
- `INITIATED` → Orden creada e iniciada
- `PENDING` → Orden pendiente de procesamiento
- `PROCESSED` → Orden procesada (en tránsito)
- `COMPLETED` → Orden completada exitosamente
- `FAILED` → Orden fallida (por validación o procesamiento)
- `CANCELLED` → Orden cancelada

**Mapeo Legacy → Dominio:**
- `ACCEPTED` (legacy) → `INITIATED` (dominio BIAN)
- `SETTLED` (legacy) → `COMPLETED` (dominio BIAN)

**Secuencia de estados esperada:**
```
INITIATED → PENDING → PROCESSED → COMPLETED
                      ↓
                   FAILED
```

---

### 4. Mapeo a BIAN Payment Initiation y PaymentOrder

#### 4.1 Mapeo de Operaciones SOAP → REST BIAN

| Operación SOAP | Operación REST BIAN | Método HTTP | Endpoint BIAN |
|----------------|---------------------|-------------|---------------|
| `SubmitPaymentOrder` | `InitiatePaymentOrder` | POST | `/payment-initiation/payment-orders` |
| `GetPaymentOrderStatus` | `RetrievePaymentOrderStatus` | GET | `/payment-initiation/payment-orders/{id}/status` |
| *No existe en SOAP* | `RetrievePaymentOrder` | GET | `/payment-initiation/payment-orders/{id}` |

**Nota**: La operación `RetrievePaymentOrder` (GET completa) es una mejora funcional en la versión REST que no existía en SOAP.

#### 4.2 Mapeo de Campos Legacy → BIAN

| Campo Legacy | Campo BIAN | Tipo BIAN | Observaciones |
|--------------|------------|-----------|---------------|
| `externalId` | `externalReference` | `ExternalReference` | Value object de dominio |
| `debtorIban` | `debtorAccount.iban` | `String` | Objeto anidado en JSON |
| `creditorIban` | `creditorAccount.iban` | `String` | Objeto anidado en JSON |
| `amount` | `instructedAmount.amount` | `BigDecimal` | Objeto anidado `PaymentAmount` |
| `currency` | `instructedAmount.currency` | `String` | Parte de `PaymentAmount` |
| `remittanceInfo` | `remittanceInformation` | `String` | Nombre más descriptivo |
| `requestedExecutionDate` | `requestedExecutionDate` | `LocalDate` | Sin cambios |
| `paymentOrderId` | `paymentOrderReference` | `String` | Término BIAN: "reference" vs "id" |

#### 4.3 Estructura de Respuestas REST BIAN (según Postman)

**InitiatePaymentOrderResponse:**
- Debe incluir: `paymentOrderReference`, `status`, campos de cuenta, monto, y timestamps (`createdAt`, `updatedAt`)

**RetrievePaymentOrderResponse:**
- Debe incluir todos los campos de la orden completa (similar a InitiatePaymentOrderResponse pero sin crear nueva orden)

**PaymentOrderStatusResponse:**
- Debe incluir: `paymentOrderReference`, `paymentOrderStatus`, `lastUpdated`

---

### 5. Campos que se Pueden Ignorar para el Alcance Mínimo

#### 5.1 Campos del Legacy NO Requeridos Inicialmente

**Para el alcance mínimo del challenge, se puede ignorar:**
- ❌ Información de tarifas/fees
- ❌ Información de tipos de cambio (si aplica)
- ❌ Metadata de trazabilidad avanzada (más allá de timestamps básicos)
- ❌ Información de beneficiario/destinatario extendida (solo IBAN necesario)
- ❌ Referencias a órdenes relacionadas (padre/hijo)
- ❌ Información de reversos/cancelaciones avanzadas (solo estados básicos)
- ❌ Códigos de error detallados (solo mensajes básicos)
- ❌ Información de routing bancario (interno, no expuesto en API)

#### 5.2 Campos Requeridos para el Alcance Mínimo

**Campos ESENCIALES que DEBEN implementarse:**
- ✅ `externalReference` (identificador externo del cliente)
- ✅ `debtorAccount.iban` (cuenta deudora)
- ✅ `creditorAccount.iban` (cuenta acreedora)
- ✅ `instructedAmount` (monto y moneda)
- ✅ `remittanceInformation` (información de remesas - opcional pero recomendado)
- ✅ `requestedExecutionDate` (fecha de ejecución solicitada)
- ✅ `paymentOrderReference` (identificador único generado)
- ✅ `status` / `paymentOrderStatus` (estado de la orden)
- ✅ `createdAt`, `updatedAt`, `lastUpdated` (timestamps)

---

### 6. Decisiones de Diseño Basadas en el Análisis

#### 6.1 Identificador de Orden de Pago

**Contexto**: 
El legacy usa `paymentOrderId`, BIAN usa `paymentOrderReference`.

**Decisión**: 
Usar `paymentOrderReference` como identificador de negocio (CR-reference-id en BIAN). Internamente puede tener un UUID técnico en la base de datos, pero el `paymentOrderReference` es el identificador expuesto en la API.

**Justificación**:
- Alineado con estándares BIAN
- El término "reference" es más semántico que "id"
- Permite diferentes formatos de referencia (no solo UUIDs)

---

#### 6.2 Estados de la Orden

**Contexto**: 
El legacy solo muestra `ACCEPTED` y `SETTLED`. Necesitamos un conjunto completo de estados.

**Decisión**: 
Implementar enum `PaymentStatus` con valores: `INITIATED`, `PENDING`, `PROCESSED`, `COMPLETED`, `FAILED`, `CANCELLED`.

**Justificación**:
- Cubre el ciclo de vida completo de una orden
- Permite transiciones de estado claras
- Alineado con prácticas de dominios de pagos
- Facilita la evolución futura

---

#### 6.3 Estructura de Cuentas (Account Objects)

**Contexto**: 
El legacy usa `debtorIban` y `creditorIban` como strings simples. La colección Postman usa objetos anidados.

**Decisión**: 
Usar objetos `debtorAccount` y `creditorAccount` con campo `iban`, permitiendo extensión futura (nombre, tipo, etc.).

**Justificación**:
- Más semántico y alineado con BIAN
- Facilita extensión futura (nombre del beneficiario, etc.)
- Consistente con la colección Postman

---

#### 6.4 Operación RetrievePaymentOrder Completa

**Contexto**: 
El legacy solo permite consultar el estado, no la orden completa.

**Decisión**: 
Implementar `GET /payment-initiation/payment-orders/{id}` para recuperar la orden completa.

**Justificación**:
- Mejora funcional sobre el servicio legacy
- Estándar REST (si puedes crear, puedes recuperar)
- Requerido por la colección Postman
- Facilita la integración con clientes

---

### 7. Resumen Ejecutivo

**Operaciones a migrar:**
1. ✅ `SubmitPaymentOrder` → `POST /payment-initiation/payment-orders` (InitiatePaymentOrder)
2. ✅ `GetPaymentOrderStatus` → `GET /payment-initiation/payment-orders/{id}/status` (RetrievePaymentOrderStatus)
3. ✅ **Nueva**: `GET /payment-initiation/payment-orders/{id}` (RetrievePaymentOrder)

**Campos principales:**
- Referencia externa, cuentas (deudor/acreedor), monto, moneda, información de remesas, fecha de ejecución
- Identificador interno generado, estado, timestamps

**Estados:**
- `INITIATED`, `PENDING`, `PROCESSED`, `COMPLETED`, `FAILED`, `CANCELLED`

**Mejoras sobre el legacy:**
- Recuperación de orden completa (no solo estado)
- Estructura de cuentas más semántica (objetos anidados)
- Terminología BIAN-aligned (`reference` vs `id`)
- API REST estándar (más integrable)

---

