# Documentación - Modelado BPMN en Camunda

## 📋 Descripción del Proceso Editorial

Este documento describe el proceso editorial completo modelado en BPMN 2.0 utilizando Camunda Modeler. El proceso representa el flujo de una publicación desde su creación hasta su publicación o rechazo.

---

## 🎯 Objetivo del Proceso

Definir y documentar el flujo editorial de publicaciones, especificando:
- Participantes involucrados (roles/lanes)
- Actividades del proceso
- Decisiones críticas
- Estados finales posibles
- Integración con el sistema de microservicios

---

## 👥 Participantes del Proceso (Lanes/Pools)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ PROCESO EDITORIAL DE PUBLICACIONES                                         │
├────────────────┬────────────────┬────────────────┬────────────────┬────────┤
│     AUTOR      │     EDITOR     │   REVISOR      │  SISTEMA       │ TIEMPO │
│                │                │  (COMITÉ)      │  INFORMÁTICO   │        │
├────────────────┼────────────────┼────────────────┼────────────────┼────────┤
│ ✓ Crear        │ ✓ Revisar      │ ✓ Evaluar      │ ✓ Registrar    │ T0 +  │
│   Borrador     │   Contenido    │   Contenido    │   Estados      │ Varies │
│                │                │                │                │        │
│ ✓ Realizar     │ ✓ Aprobación   │ ✓ Aprobación   │ ✓ Notificaciones
│   Cambios      │   Editorial    │   Académica    │                │
│                │                │                │                │
│ ✓ Reenviar     │ ✓ Rechazar     │                │ ✓ Publicar    │
│                │                │                │                │
└────────────────┴────────────────┴────────────────┴────────────────┴────────┘
```

### Descripción de Roles

| Rol | Responsabilidades |
|-----|------------------|
| **AUTOR** | Crear, modificar y mejorar contenido; responder a comentarios de revisión |
| **EDITOR** | Validar estructura, formato; tomar decisiones de aceptación/rechazo |
| **REVISOR** | Evaluar calidad académica, originalidad y relevancia |
| **SISTEMA** | Registrar cambios de estado, enviar notificaciones, publicar |

---

## 🔄 Flujo del Proceso BPMN

### Diagrama de Secuencia Textual

```
INICIO
  │
  ├─→ [EVENTO INICIO] Autor crea borrador
  │
  ├─→ [TAREA] Autor escribe contenido (AUTOR Lane)
  │
  ├─→ [TAREA] Validar contenido mínimo (SISTEMA Lane)
  │       └─→ SI Válido: Continuar
  │       └─→ NO Válido: Error → FIN
  │
  ├─→ [TAREA] Editor revisa contenido (EDITOR Lane)
  │
  ├─→ [TAREA] Revisor evalúa contenido (REVISOR Lane)
  │
  ├─→ [GATEWAY XOR] Decisión Editorial
  │       │
  │       ├─→ [SI] APROBADO (70%)
  │       │       │
  │       │       ├─→ [TAREA] Preparar para publicación (SISTEMA)
  │       │       │
  │       │       ├─→ [TAREA] Publicar contenido (SISTEMA)
  │       │       │
  │       │       └─→ [EVENTO FIN] Publicación completada ✓
  │       │
  │       ├─→ [NO] REQUIERE CAMBIOS (20%)
  │       │       │
  │       │       ├─→ [TAREA] Notificar cambios necesarios (SISTEMA)
  │       │       │
  │       │       ├─→ [TAREA] Autor realiza cambios (AUTOR)
  │       │       │
  │       │       └─→ Regresar a revisión
  │       │
  │       └─→ [NO] RECHAZADO (10%)
  │               │
  │               ├─→ [TAREA] Generar reporte de rechazo (EDITOR)
  │               │
  │               ├─→ [TAREA] Notificar rechazo (SISTEMA)
  │               │
  │               └─→ [EVENTO FIN] Publicación rechazada ✗

FIN
```

---

## 📍 Elementos BPMN Requeridos

### Eventos (Events)

| Tipo | Elemento | Descripción |
|------|----------|-------------|
| **START** | ⭕ | Crear borrador - Inicial |
| **END** | ⭕ | Publicado exitosamente |
| **END** | ⭕ | Publicación rechazada |

### Tareas (Tasks)

| Tarea | Tipo | Responsable | Descripción |
|-------|------|-------------|------------|
| Escribir contenido | USER TASK | Autor | Redacción del artículo/publicación |
| Revisar contenido | USER TASK | Editor | Validación editorial |
| Evaluar académicamente | USER TASK | Revisor | Evaluación por pares |
| Preparar publicación | SERVICE TASK | Sistema | Formatos, metadatos |
| Publicar contenido | SERVICE TASK | Sistema | Subir a producción |
| Notificar cambios | SERVICE TASK | Sistema | Enviar email al autor |
| Notificar rechazo | SERVICE TASK | Sistema | Comunicar decisión |
| Generar reporte | USER TASK | Editor | Documentar razones |

### Gateways (Decisiones)

| Gateway | Tipo | Condición |
|---------|------|-----------|
| Decisión Editorial | XOR (Exclusivo) | aprobado = true/false<br/>requiereCambios = true/false |

---

## 📊 Variables del Proceso

Variables utilizadas en Token Simulation:

```
{
  "publicationId": "PUB-001",
  "authorEmail": "juan@example.com",
  "titulo": "Avances en IA 2024",
  "contenido": "Lorem ipsum...",
  "aprobado": boolean,
  "requiereCambios": boolean,
  "comentariosRevision": string,
  "razonRechazo": string,
  "editorAsignado": "Editor-1",
  "fechaCreacion": "2024-01-15",
  "intentosRevision": 0
}
```

### Condiciones de Gateway

```
Decisión Editorial:
  ├─ SI aprobado = true 
  │   └─→ IR A: Preparar Publicación
  │
  ├─ SI requiereCambios = true
  │   └─→ IR A: Notificar Cambios Necesarios
  │
  └─ SI aprobado = false Y requiereCambios = false
      └─→ IR A: Rechazado
```

---

## 🧪 Escenarios de Token Simulation

### Escenario 1: Aprobación Directa (Flujo Feliz)

**Descripción**: La publicación se aprueba en la primera revisión sin cambios.

**Configuración de Variables**:
```json
{
  "aprobado": true,
  "requiereCambios": false,
  "editorAsignado": "Editor-1"
}
```

**Ruta del Token**:
```
INICIO
  → Autor escribe contenido
  → Validar contenido
  → Editor revisa
  → Revisor evalúa
  → GATEWAY: ¿Aprobado?
    └─ SÍ
      → Preparar publicación
      → Publicar contenido
      → FIN: Publicación exitosa ✓
```

**Resultado**: Publicación completada en estado PUBLISHED

---

### Escenario 2: Rechazo

**Descripción**: La publicación es rechazada durante la revisión.

**Configuración de Variables**:
```json
{
  "aprobado": false,
  "requiereCambios": false,
  "razonRechazo": "No cumple con estándares de calidad académica"
}
```

**Ruta del Token**:
```
INICIO
  → Autor escribe contenido
  → Validar contenido
  → Editor revisa
  → Revisor evalúa
  → GATEWAY: ¿Aprobado?
    └─ NO
      → GATEWAY: ¿Requiere Cambios?
        └─ NO
          → Generar reporte de rechazo
          → Notificar rechazo
          → FIN: Publicación rechazada ✗
```

**Resultado**: Publicación completada en estado REJECTED

---

### Escenario 3: Cambios Requeridos

**Descripción**: Se solicitan cambios al autor, quien reenvía la publicación.

**Configuración de Variables (Iteración 1)**:
```json
{
  "aprobado": false,
  "requiereCambios": true,
  "comentariosRevision": "Revisar sección de metodología",
  "intentosRevision": 1
}
```

**Ruta del Token (Iteración 1)**:
```
INICIO
  → Autor escribe contenido
  → Validar contenido
  → Editor revisa
  → Revisor evalúa
  → GATEWAY: ¿Aprobado?
    └─ NO
      → GATEWAY: ¿Requiere Cambios?
        └─ SÍ
          → Notificar cambios necesarios
          → [ESPERAR PARTICIPANTE]
```

**Configuración de Variables (Iteración 2)**:
```json
{
  "aprobado": true,
  "requiereCambios": false,
  "intentosRevision": 2
}
```

**Ruta del Token (Iteración 2)**:
```
[CONTINUACIÓN DE ESPERA]
  → Autor realiza cambios
  → LOOPBACK: Regresa a revisión
  → Editor revisa nuevamente
  → Revisor evalúa nuevamente
  → GATEWAY: ¿Aprobado?
    └─ SÍ
      → Preparar publicación
      → Publicar contenido
      → FIN: Publicación exitosa ✓
```

**Resultado**: Publicación completada después de 2 iteraciones

---

## 🎬 Ejecución con Token Simulation en Camunda Modeler

### Pasos para Simular

1. **Abrir Camunda Modeler**
   ```bash
   # En Windows/Mac/Linux
   camunda-modeler [archivo.bpmn]
   ```

2. **Cargar el archivo BPMN**
   - Archivo: `editorial-process.bpmn`

3. **Iniciar Token Simulation**
   - Menu: `Token Simulation` → `Start Simulation`
   - O: Atajo `Shift + F8`

4. **Configurar Variables iniciales**
   - Click en `Process Variables`
   - Ingresar valores según escenario

5. **Ejecutar Token**
   - Click en evento START (círculo verde)
   - Token comienza a fluir por el diagrama
   - Click en tareas para avanzar
   - En gateways, seleccionar ruta según condiciones

6. **Observar Progreso**
   - Token destacado en rojo/amarillo
   - Elementos completados en gris
   - Historial de ejecución visible

---

## 🔗 Integración con Microservicios

### Mapping a Estados de Publicación

```
BPMN Process State    → Publications Service State
─────────────────────────────────────────────────
Inicio (Start)        → DRAFT
Escribir contenido    → DRAFT
En revisión           → IN_REVIEW
Requiere cambios      → REQUIRES_CHANGES
Aprobado              → APPROVED
Preparar publicación  → APPROVED
Publicación exitosa   → PUBLISHED
Publicación rechazada → REJECTED
```

### Llamadas a API del Sistema

```
Task: Validar contenido
  └─ POST /api/publications/validate
      {
        "content": "...",
        "minLength": 100
      }

Task: Publicar contenido
  └─ PATCH /api/publications/{id}/status
      {
        "status": "PUBLISHED"
      }

Task: Notificar cambios
  └─ POST /api/notifications/send
      {
        "recipient": "${authorEmail}",
        "subject": "Cambios requeridos",
        "body": "${comentariosRevision}"
      }
```

---

## 📋 Checklist de Implementación BPMN

- [x] Evento de inicio (crear borrador)
- [x] Lane: Autor
- [x] Lane: Editor
- [x] Lane: Revisor
- [x] Lane: Sistema
- [x] Tarea: Escribir contenido (Autor)
- [x] Tarea: Revisar contenido (Editor)
- [x] Tarea: Evaluar académicamente (Revisor)
- [x] Gateway XOR: Decisión Editorial
- [x] Ruta: Aprobado → Publicar → FIN
- [x] Ruta: Cambios Necesarios → Notificar → Loop
- [x] Ruta: Rechazado → FIN
- [x] Eventos de fin (Publicado / Rechazado)
- [x] Variables del proceso documentadas
- [x] 3 escenarios simulados exitosamente

---

## 📚 Estándares BPMN 2.0 Aplicados

- ✅ Notation compliant con especificación BPMN 2.0
- ✅ Elementos base: Events, Tasks, Gateways, Flows
- ✅ Swimlanes para mostrar responsabilidades
- ✅ Decisiones exclusivas (XOR gateways)
- ✅ Nodos de inicio y fin claramente definidos
- ✅ Flujos nombrados descriptivamente
- ✅ Ciclos de retrabajo explícitos

---

## 🔍 Validación del Modelo

El modelo BPMN cumple con:

1. **Completitud**: Todos los escenarios principales cubiertos
2. **Claridad**: Roles y tareas claramente definidos
3. **Correctitud**: Transiciones válidas y lógicas
4. **Ejecutabilidad**: Puede ser simulado y ejecutado
5. **Trazabilidad**: Vinculable con estados del sistema

---

## 📁 Archivo BPMN

**Ubicación**: `bpmn/editorial-process.bpmn`

**Descripción**: Archivo XML que contiene la definición completa del proceso BPMN 2.0, compatible con Camunda Modeler, Camunda Platform y otros motores BPMN estándar.

---

## 🎓 Consideraciones Educativas

Este modelo BPMN es ideal para:
- Documentar procesos de negocio
- Comunicar flujos entre equipos
- Base para automatización
- Training y onboarding
- Auditoría y compliance
- Mejora continua de procesos

---

**Versión**: 1.0.0  
**Última actualización**: Enero 2024  
**Estado**: Validado ✓
