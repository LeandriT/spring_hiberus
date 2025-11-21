# Análisis del WSDL Legacy - PaymentOrderService

**Fecha**: PASO 2  
**Origen**: Análisis manual del WSDL y XML de ejemplo

## Resumen Ejecutivo

El servicio SOAP legacy `PaymentOrderService` expone dos operaciones principales para gestionar órdenes de pago:
1. **SubmitPaymentOrder**: Crear/iniciar una nueva orden de pago
2. **GetPaymentOrderStatus**: Consultar el estado de una orden existente

## Estructura del WSDL

**Namespace**: `http://legacy.bank/payments`  
**Endpoint**: `http://soap-mock:8081/legacy/payments`  
**Binding Style**: Document/Literal

## Operaciones Detalladas

### 1. SubmitPaymentOrder

**SOAP Action**: `submit`

**Request Schema**:
```xml
<xsd:element name="SubmitPaymentOrderRequest">
  <xsd:complexType>
    <xsd:sequence>
      <xsd:element name="externalId" type="xsd:string"/>
      <xsd:element name="debtorIban" type="xsd:string"/>
      <xsd:element name="creditorIban" type="xsd:string"/>
      <xsd:element name="amount" type="xsd:decimal"/>
      <xsd:element name="currency" type="xsd:string"/>
      <xsd:element name="remittanceInfo" type="xsd:string" minOccurs="0"/>
      <xsd:element name="requestedExecutionDate" type="xsd:date"/>
    </xsd:sequence>
  </xsd:complexType>
</xsd:element>
```

**Response Schema**:
```xml
<xsd:element name="SubmitPaymentOrderResponse">
  <xsd:complexType>
    <xsd:sequence>
      <xsd:element name="paymentOrderId" type="xsd:string"/>
      <xsd:element name="status" type="xsd:string"/>
    </xsd:sequence>
  </xsd:complexType>
</xsd:element>
```

**Ejemplo Request**:
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

**Ejemplo Response**:
```xml
<SubmitPaymentOrderResponse>
  <paymentOrderId>PO-0001</paymentOrderId>
  <status>ACCEPTED</status>
</SubmitPaymentOrderResponse>
```

### 2. GetPaymentOrderStatus

**SOAP Action**: `status`

**Request Schema**:
```xml
<xsd:element name="GetPaymentOrderStatusRequest">
  <xsd:complexType>
    <xsd:sequence>
      <xsd:element name="paymentOrderId" type="xsd:string"/>
    </xsd:sequence>
  </xsd:complexType>
</xsd:element>
```

**Response Schema**:
```xml
<xsd:element name="GetPaymentOrderStatusResponse">
  <xsd:complexType>
    <xsd:sequence>
      <xsd:element name="paymentOrderId" type="xsd:string"/>
      <xsd:element name="status" type="xsd:string"/>
      <xsd:element name="lastUpdate" type="xsd:dateTime"/>
    </xsd:sequence>
  </xsd:complexType>
</xsd:element>
```

**Ejemplo Request**:
```xml
<GetPaymentOrderStatusRequest>
  <paymentOrderId>PO-0001</paymentOrderId>
</GetPaymentOrderStatusRequest>
```

**Ejemplo Response**:
```xml
<GetPaymentOrderStatusResponse>
  <paymentOrderId>PO-0001</paymentOrderId>
  <status>SETTLED</status>
  <lastUpdate>2025-10-30T16:25:30Z</lastUpdate>
</GetPaymentOrderStatusResponse>
```

## Estados Identificados

De los ejemplos XML, se identifican los siguientes estados:
- **ACCEPTED**: Estado inicial cuando se crea la orden
- **SETTLED**: Estado final cuando la orden ha sido liquidada

**Nota**: El dominio BIAN implementará un conjunto más completo de estados.

## Observaciones Importantes

1. **Formato de paymentOrderId**: Los ejemplos usan formato `PO-0001`, lo que sugiere un patrón `PO-{número}`
2. **IBANs en ejemplos**: Los IBANs en los ejemplos son cortos (`EC12DEBTOR`, `EC98CREDITOR`), pero el OpenAPI debe validar IBANs reales (minLength: 15)
3. **remittanceInfo es opcional**: El campo puede estar ausente en el request
4. **lastUpdate solo en GetStatus**: El timestamp de última actualización solo aparece en la respuesta de GetPaymentOrderStatus, no en SubmitPaymentOrder

## Referencias

- WSDL completo: `Prueba-tecnica-Java-migracion/legacy/PaymentOrderService.wsdl`
- XML de ejemplo: `Prueba-tecnica-Java-migracion/legacy/samples/`

