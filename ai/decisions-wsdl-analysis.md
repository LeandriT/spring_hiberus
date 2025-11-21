# Análisis del WSDL Legacy y Mapeo a BIAN Payment Initiation

## Fecha: 2025-11-20

## Paso: Análisis del servicio SOAP legacy (PASO 2)

---

## 1. Operaciones SOAP Disponibles Relacionadas con Órdenes de Pago

El servicio `PaymentOrderService` expone las siguientes operaciones SOAP:

### 1.1. SubmitPaymentOrder
- **Acción SOAP**: `submit`
- **Propósito**: Crear/Iniciar una nueva orden de pago
- **Request**: `SubmitPaymentOrderRequest`
- **Response**: `SubmitPaymentOrderResponse`
- **Mapeo BIAN**: `Initiate PaymentOrder` (POST /payment-initiation/payment-orders)

### 1.2. GetPaymentOrderStatus
- **Acción SOAP**: `status`
- **Propósito**: Consultar el estado de una orden de pago existente
- **Request**: `GetPaymentOrderStatusRequest`
- **Response**: `GetPaymentOrderStatusResponse`
- **Mapeo BIAN**: `Retrieve PaymentOrder Status` (GET /payment-initiation/payment-orders/{id}/status)

### Nota sobre Operaciones Adicionales
- La colección Postman también incluye `Retrieve PaymentOrder` (GET /payment-initiation/payment-orders/{id})
- Esta operación no existe en el WSDL legacy, pero es necesaria en el modelo BIAN para recuperar la orden completa

---

## 2. Estructuras de Datos Principales (Campos Clave)

### 2.1. SubmitPaymentOrderRequest (Crear Orden de Pago)

| Campo SOAP Legacy | Tipo | Requerido | Descripción |
|------------------|------|-----------|-------------|
| `externalId` | string | Sí | Identificador externo de la orden (proporcionado por el cliente) |
| `debtorIban` | string | Sí | IBAN de la cuenta ordenante |
| `creditorIban` | string | Sí | IBAN de la cuenta beneficiaria |
| `amount` | decimal | Sí | Importe del pago |
| `currency` | string | Sí | Código de moneda (ISO 4217, ej: USD, EUR) |
| `remittanceInfo` | string | No (opcional) | Información de remesas/referencia del pago |
| `requestedExecutionDate` | date | Sí | Fecha de ejecución solicitada |

**Ejemplo**:
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

### 2.2. SubmitPaymentOrderResponse (Respuesta de Creación)

| Campo SOAP Legacy | Tipo | Descripción |
|------------------|------|-------------|
| `paymentOrderId` | string | Identificador único de la orden de pago generado por el sistema |
| `status` | string | Estado inicial de la orden (ej: ACCEPTED) |

**Ejemplo**:
```xml
<SubmitPaymentOrderResponse>
  <paymentOrderId>PO-0001</paymentOrderId>
  <status>ACCEPTED</status>
</SubmitPaymentOrderResponse>
```

### 2.3. GetPaymentOrderStatusRequest (Consultar Estado)

| Campo SOAP Legacy | Tipo | Requerido | Descripción |
|------------------|------|-----------|-------------|
| `paymentOrderId` | string | Sí | Identificador de la orden de pago a consultar |

### 2.4. GetPaymentOrderStatusResponse (Respuesta de Estado)

| Campo SOAP Legacy | Tipo | Descripción |
|------------------|------|-------------|
| `paymentOrderId` | string | Identificador de la orden de pago |
| `status` | string | Estado actual de la orden (ej: SETTLED) |
| `lastUpdate` | dateTime | Fecha y hora de la última actualización del estado |

**Ejemplo**:
```xml
<GetPaymentOrderStatusResponse>
  <paymentOrderId>PO-0001</paymentOrderId>
  <status>SETTLED</status>
  <lastUpdate>2025-10-30T16:25:30Z</lastUpdate>
</GetPaymentOrderStatusResponse>
```

---

## 3. Estados Posibles de la Orden de Pago en el Servicio Legacy

Según los ejemplos XML proporcionados, se observan los siguientes estados:

### Estados Observados en Ejemplos:
1. **ACCEPTED** - Estado inicial cuando se crea la orden (SubmitPaymentOrderResponse)
2. **SETTLED** - Estado cuando la orden ha sido liquidada/procesada (GetPaymentOrderStatusResponse)

### Estados Potenciales Adicionales (Inferidos del Ciclo de Vida de Pagos):
Aunque no aparecen en los ejemplos, el ciclo de vida típico de una orden de pago puede incluir:
- **PENDING** - Orden pendiente de procesamiento
- **PROCESSING** - Orden en proceso
- **REJECTED** - Orden rechazada
- **FAILED** - Orden fallida
- **CANCELLED** - Orden cancelada

**Nota**: Estos estados adicionales son inferidos del dominio de pagos y deben validarse con el negocio o documentación adicional.

---

## 4. Mapeo de Conceptos al Service Domain BIAN Payment Initiation y BQ PaymentOrder

### 4.1. Mapeo de Operaciones SOAP → Operaciones BIAN REST

| Operación SOAP Legacy | Operación BIAN | Método HTTP | Endpoint REST BIAN |
|----------------------|----------------|-------------|-------------------|
| `SubmitPaymentOrder` | `Initiate PaymentOrder` | POST | `/payment-initiation/payment-orders` |
| `GetPaymentOrderStatus` | `Retrieve PaymentOrder Status` | GET | `/payment-initiation/payment-orders/{paymentOrderId}/status` |
| *(No existe en SOAP)* | `Retrieve PaymentOrder` | GET | `/payment-initiation/payment-orders/{paymentOrderId}` |

### 4.2. Mapeo de Campos: SubmitPaymentOrderRequest → PaymentOrderInitiateRequest (BIAN)

| Campo SOAP Legacy | Campo BIAN REST | Tipo BIAN | Transformación Requerida |
|------------------|-----------------|-----------|-------------------------|
| `externalId` | `externalReference` | string | Renombrar campo |
| `debtorIban` | `debtorAccount.iban` | string | Anidar en objeto `debtorAccount` |
| `creditorIban` | `creditorAccount.iban` | string | Anidar en objeto `creditorAccount` |
| `amount` | `instructedAmount.amount` | decimal | Anidar en objeto `instructedAmount` |
| `currency` | `instructedAmount.currency` | string | Anidar en objeto `instructedAmount` |
| `remittanceInfo` | `remittanceInformation` | string | Renombrar campo (mantener opcional) |
| `requestedExecutionDate` | `requestedExecutionDate` | date (ISO 8601) | Mantener igual |

**Ejemplo de Transformación**:
```json
// SOAP Request (SubmitPaymentOrderRequest)
{
  "externalId": "EXT-123",
  "debtorIban": "EC12DEBTOR",
  "creditorIban": "EC98CREDITOR",
  "amount": 150.75,
  "currency": "USD",
  "remittanceInfo": "Factura 001-123",
  "requestedExecutionDate": "2025-10-31"
}

// BIAN REST Request (PaymentOrderInitiateRequest)
{
  "externalReference": "EXT-123",
  "debtorAccount": {
    "iban": "EC12DEBTOR"
  },
  "creditorAccount": {
    "iban": "EC98CREDITOR"
  },
  "instructedAmount": {
    "amount": 150.75,
    "currency": "USD"
  },
  "remittanceInformation": "Factura 001-123",
  "requestedExecutionDate": "2025-10-31"
}
```

### 4.3. Mapeo de Campos: SubmitPaymentOrderResponse → PaymentOrderInitiateResponse (BIAN)

| Campo SOAP Legacy | Campo BIAN REST | Tipo BIAN | Transformación Requerida |
|------------------|-----------------|-----------|-------------------------|
| `paymentOrderId` | `paymentOrderId` | string | Mantener igual |
| `status` | `status` | string | Mantener igual (posible normalización a enum BIAN) |

### 4.4. Mapeo de Campos: GetPaymentOrderStatusResponse → PaymentOrderStatus (BIAN)

| Campo SOAP Legacy | Campo BIAN REST | Tipo BIAN | Transformación Requerida |
|------------------|-----------------|-----------|-------------------------|
| `paymentOrderId` | `paymentOrderId` | string | Mantener igual |
| `status` | `status` | string | Mantener igual (posible normalización a enum BIAN) |
| `lastUpdate` | `lastUpdate` | dateTime (ISO 8601) | Mantener igual |

### 4.5. Estructura de PaymentOrder Completa (BIAN - Retrieve)

Según el estándar BIAN y la colección Postman, el endpoint `Retrieve PaymentOrder` debe devolver una estructura más completa que incluye:
- `paymentOrderId`
- `externalReference` (mapping desde externalId)
- `debtorAccount` (objeto con iban)
- `creditorAccount` (objeto con iban)
- `instructedAmount` (objeto con amount y currency)
- `remittanceInformation`
- `requestedExecutionDate`
- `status`
- `lastUpdate`
- Posiblemente campos adicionales del ciclo de vida de la orden

---

## 5. Consideraciones de Implementación

### 5.1. Estados de PaymentOrder
- **Recomendación**: Definir un enum para los estados (`PaymentOrderStatus`) que incluya:
  - Estados confirmados en ejemplos: `ACCEPTED`, `SETTLED`
  - Estados adicionales del ciclo de vida si son necesarios
  - Validar con el dominio de negocio si se requieren más estados

### 5.2. Validaciones Requeridas
- Validar formato de IBAN (debtor y creditor)
- Validar códigos de moneda según ISO 4217
- Validar formato de fechas (ISO 8601 para REST, date/dateTime para SOAP)
- Validar que `requestedExecutionDate` no sea en el pasado
- Validar que `amount` sea positivo

### 5.3. Identificadores
- `paymentOrderId`: Generado por el sistema, formato "PO-XXXX" según ejemplos
- `externalReference`: Proporcionado por el cliente, debe ser único por cliente

### 5.4. Campos Opcionales vs Requeridos
- `remittanceInformation` es opcional en SOAP, mantener opcional en REST
- Todos los demás campos son requeridos

---

## 6. Resumen del Mapeo Completo

### Operaciones:
1. **SubmitPaymentOrder** (SOAP) → **Initiate PaymentOrder** (BIAN REST POST)
2. **GetPaymentOrderStatus** (SOAP) → **Retrieve PaymentOrder Status** (BIAN REST GET /status)
3. **Nueva operación BIAN**: **Retrieve PaymentOrder** (BIAN REST GET) - No existe en SOAP legacy

### Estructuras Principales:
- **PaymentOrder**: Agregado principal que contiene toda la información de la orden
- **PaymentOrderStatus**: Objeto ligero para consulta rápida de estado
- **Account**: Objeto anidado para representar cuentas (debtor/creditor) con IBAN
- **InstructedAmount**: Objeto anidado para representar monto con moneda

### Cambios Principales en la Estructura:
- **Flattening → Nested**: IBANs se anidan en objetos Account, amount/currency se anidan en InstructedAmount
- **Naming**: externalId → externalReference, remittanceInfo → remittanceInformation
- **Enriquecimiento**: Nueva operación Retrieve para obtener orden completa (no solo estado)

---

## Referencias

- WSDL Legacy: `Prueba-tecnica-Java-migracion/legacy/PaymentOrderService.wsdl`
- XML Ejemplos: `Prueba-tecnica-Java-migracion/legacy/samples/`
- Colección Postman: `Prueba-tecnica-Java-migracion/postman_collection.json`
- BIAN Standard: Payment Initiation Service Domain

