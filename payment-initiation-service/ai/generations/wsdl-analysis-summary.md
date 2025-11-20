# Análisis del WSDL - PaymentOrderService

**Fecha de generación**: 2025-01-27

**Prompt usado**: Ver `../prompts.md` - Prompt 2

**Herramienta**: Cursor

**Estado**: `original` (análisis generado por IA, documentado en decisions.md)

---

## Resumen Ejecutivo

Análisis del servicio SOAP legacy `PaymentOrderService` para migración a REST API alineada con BIAN Payment Initiation Service Domain.

### Operaciones Identificadas

1. **SubmitPaymentOrder** → Mapea a BIAN `Initiate` (POST)
2. **GetPaymentOrderStatus** → Mapea a BIAN `Retrieve Status` (GET)

### Campos Clave

**Request (SubmitPaymentOrder)**:
- externalId, debtorIban, creditorIban, amount, currency, remittanceInfo, requestedExecutionDate

**Response (SubmitPaymentOrder)**:
- paymentOrderId, status

**Request (GetPaymentOrderStatus)**:
- paymentOrderId

**Response (GetPaymentOrderStatus)**:
- paymentOrderId, status, lastUpdate

### Estados Legacy

- ACCEPTED (estado inicial)
- SETTLED (estado final)
- Estados inferidos: PENDING, PROCESSING, REJECTED, FAILED, CANCELLED

### Mapeo BIAN

Ver análisis completo en `../decisions.md` sección "Fase 2: Análisis del Servicio SOAP Legacy y Mapeo a BIAN".

---

**Nota**: Este es un resumen. El análisis completo con todas las decisiones de diseño está documentado en `ai/decisions.md`.

