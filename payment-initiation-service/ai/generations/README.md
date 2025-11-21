# Fragmentos Generados por IA

Esta carpeta contiene fragmentos relevantes de código, configuración o documentación generados por IA que son especialmente importantes o que requieren referencia futura.

## Estructura

Los archivos se organizarán por paso del playbook o por componente:
- `step0-*`: Archivos del PASO 0
- `step1-*`: Archivos del PASO 1
- `openapi-*`: Contratos OpenAPI generados
- `controllers-*`: Controladores REST generados
- `domain-*`: Modelos de dominio generados
- etc.

## Formato

Cada archivo debe incluir:
- **Origen**: Qué prompt generó este código
- **Fecha**: Cuándo se generó
- **Versión**: Versión inicial vs. versiones corregidas manualmente
- **Notas**: Cambios manuales aplicados después de la generación

## Ejemplo

```markdown
# build.gradle - PASO 0

**Origen**: Prompt "Crear proyecto Spring Boot 3 con Java 21"  
**Fecha**: Inicio del proyecto  
**Versión**: 1.0 (generada por IA)

**Notas**:
- Se eliminó la dependencia de `compileJava` sobre `openApiGenerate` temporalmente
- Se agregó `onlyIf` para que openApiGenerate solo se ejecute si existe openapi.yaml
```

---

## Archivos Actuales

- (Se irán agregando conforme se generen fragmentos relevantes)

