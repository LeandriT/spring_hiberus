# Ejemplos XML Legacy - PaymentOrderService

Esta carpeta contiene copias de los artefactos legacy analizados durante el desarrollo.

## Archivos Almacenados

### PaymentOrderService.wsdl
- **Origen**: `Prueba-tecnica-Java-migracion/legacy/PaymentOrderService.wsdl`
- **Propósito**: WSDL del servicio SOAP legacy que se está migrando a REST BIAN
- **Análisis**: Documentado en `../decisions-wsdl-analysis.md`

### XML Ejemplos (Referencias)
Los siguientes XML de ejemplo se encuentran en:
- `Prueba-tecnica-Java-migracion/legacy/samples/SubmitPaymentOrderRequest.xml`
- `Prueba-tecnica-Java-migracion/legacy/samples/SubmitPaymentOrderResponse.xml`
- `Prueba-tecnica-Java-migracion/legacy/samples/GetPaymentOrderStatusRequest.xml`
- `Prueba-tecnica-Java-migracion/legacy/samples/GetPaymentOrderStatusResponse.xml`

## Uso

Estos archivos se utilizaron para:
1. Analizar la estructura del servicio SOAP legacy
2. Identificar operaciones y estructuras de datos
3. Crear el mapeo completo a BIAN Payment Initiation
4. Documentar las transformaciones necesarias para la migración

## Referencias

- Análisis completo: `../decisions-wsdl-analysis.md`
- Decisiones de mapeo: `../decisions.md`
- Prompt utilizado: `../prompts.md`

