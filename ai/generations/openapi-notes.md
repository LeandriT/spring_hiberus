# Notas sobre el Contrato OpenAPI Generado

## Archivo: openapi.yaml

**Fecha de creación**: 2025-11-20
**Versión OpenAPI**: 3.0.3
**Estado**: ✅ Generado por IA, sin modificaciones manuales

## Contenido Generado

### Endpoints Definidos
1. **POST /payment-initiation/payment-orders** (initiatePaymentOrder)
   - Request: InitiatePaymentOrderRequest
   - Response 201: InitiatePaymentOrderResponse
   - Errores: 400, 422, 500 (ProblemDetail)

2. **GET /payment-initiation/payment-orders/{paymentOrderId}** (retrievePaymentOrder)
   - Response 200: RetrievePaymentOrderResponse
   - Errores: 404, 500 (ProblemDetail)

3. **GET /payment-initiation/payment-orders/{paymentOrderId}/status** (retrievePaymentOrderStatus)
   - Response 200: PaymentOrderStatusResponse
   - Errores: 404, 500 (ProblemDetail)

### Schemas Definidos
- InitiatePaymentOrderRequest
- InitiatePaymentOrderResponse
- RetrievePaymentOrderResponse
- PaymentOrderStatusResponse
- PaymentAccount (con iban)
- PaymentAmount (amount + currency)
- PaymentOrderStatus (enum)
- ProblemDetail (RFC 7807)

## Decisiones de Diseño Aplicadas

### ✅ Validaciones Correctas
- **IBAN**: `minLength: 15, maxLength: 34` (sin pattern)
- **Currency**: `enum` sin pattern (USD, EUR, GBP, etc.)
- **PaymentOrderStatus**: `enum` con 7 estados
- **paymentOrderId**: `pattern: '^PO-[0-9]+$'` solo en path parameters

### ✅ Estructura BIAN-Aligned
- Objetos anidados: `debtorAccount.iban`, `creditorAccount.iban`
- Objeto `instructedAmount` con `amount` y `currency`
- Nombres de campos alineados con BIAN: `externalReference`, `remittanceInformation`

### ✅ Ejemplos Actualizados
- Todos los IBANs en ejemplos tienen al menos 15 caracteres
- Ejemplo: `"EC123456789012345678"` (20 caracteres)

## Modificaciones Futuras (si aplican)

_Esta sección se completará si se realizan modificaciones manuales al contrato después de la generación inicial._

## Referencias

- Análisis WSDL: `../decisions-wsdl-analysis.md`
- Colección Postman: `../../Prueba-tecnica-Java-migracion/postman_collection.json`
- BIAN Standard: Payment Initiation Service Domain

