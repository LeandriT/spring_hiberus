# Fragmentos Generados por IA

Este directorio contiene fragmentos relevantes de código generado por IA durante el desarrollo del proyecto.

## Propósito

Documentar y preservar versiones iniciales de código generado por IA para:
- **Trazabilidad**: Entender qué generó la IA vs. qué se modificó manualmente
- **Referencia**: Consultar decisiones de diseño iniciales
- **Aprendizaje**: Analizar patrones y mejoras aplicadas
- **Auditoría**: Evidenciar el uso responsable de IA en el desarrollo

## Estructura de Archivos

Cada fragmento debe incluir en su encabezado:
- **Fecha de generación**: Cuándo fue generado
- **Prompt usado**: Referencia al prompt en `../prompts.md`
- **Herramienta**: Cursor, ChatGPT, etc.
- **Estado**: 
  - `original`: Sin modificaciones
  - `modificado`: Ajustado manualmente
  - `reemplazado`: Completamente reescrito
- **Cambios aplicados**: Descripción breve de modificaciones (si aplica)

## Ejemplo de Estructura

```
generations/
├── README.md
├── openapi-initial.yaml          # Contrato OpenAPI generado inicialmente
├── controller-skeleton.java      # Esqueleto de controlador generado
├── domain-model-draft.java       # Borrador del modelo de dominio
└── test-template.java            # Plantilla de test generada
```

## Convenciones de Nomenclatura

- Usar nombres descriptivos que indiquen el propósito del archivo
- Incluir sufijos como `-initial`, `-draft`, `-skeleton` cuando sea apropiado
- Mantener la extensión original del archivo (`.java`, `.yaml`, etc.)

## Notas Importantes

⚠️ **IMPORTANTE**: Todo código generado por IA debe ser:
1. Revisado manualmente
2. Adaptado a las necesidades específicas del proyecto
3. Probado exhaustivamente
4. Documentado en `../decisions.md` si se aplican cambios significativos

---

*Este directorio se irá poblando conforme avance el desarrollo del proyecto.*

