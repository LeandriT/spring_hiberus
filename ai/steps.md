# Pasos de Implementación - Payment Initiation Challenge

Este documento contiene los pasos de implementación del proyecto, diseñados para ser ejecutados paso a paso con asistencia de IA.

## ⚠️ Instrucciones de Uso

1. **Copia cada PASO individualmente** en Cursor Pro.
2. **Revisa el código generado** antes de continuar al siguiente paso.
3. **Ejecuta tests** después de cada paso crítico (PASO 4, PASO 9, PASO 10, etc.).
4. **Documenta correcciones manuales** en `ai/decisions.md`.
5. **Los pasos marcados con ⚠️ IMPORTANTE** contienen correcciones críticas que deben aplicarse.

## Contexto del Proyecto

- **Dominio BIAN**: Payment Initiation
- **BQ principal**: PaymentOrder
- **Migración**: SOAP → REST (BIAN-aligned)
- **Arquitectura**: Hexagonal (Ports & Adapters)
- **Enfoque**: Contract-First con OpenAPI 3.0
- **Stack**: Java 21, Spring Boot 3, H2, Gradle, MapStruct, WebTestClient

## Artefactos de Soporte

- WSDL legacy: `Prueba-tecnica-Java-migracion/legacy/PaymentOrderService.wsdl`
- XML ejemplos: `Prueba-tecnica-Java-migracion/legacy/samples/`
- Colección Postman: `Prueba-tecnica-Java-migracion/postman_collection.json`

---

## Pasos de Implementación

### Pendiente de definición

_Los pasos de implementación se agregarán aquí conforme se vayan definiendo o ejecutando._

---

## Estado de Implementación

| Paso | Descripción | Estado | Notas |
|------|-------------|--------|-------|
| - | - | Pendiente | - |

---

## Próximos Pasos Sugeridos

1. Configuración inicial del proyecto (Gradle, Spring Boot)
2. Definición del contrato OpenAPI (contract-first)
3. Generación de código desde OpenAPI
4. Implementación del dominio (modelos, value objects)
5. Implementación de puertos (interfaces)
6. Implementación de adaptadores (REST, persistencia)
7. Implementación de servicios de aplicación
8. Tests unitarios
9. Tests de integración
10. Validación con Postman

