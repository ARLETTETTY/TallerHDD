# Arquitectura del Sistema

El proyecto sigue una arquitectura en capas sobre Spring Boot 4.x.

## Capas

- **Controller**: expone los endpoints REST (crear, actualizar, obtener por fecha).
- **Service**: contiene la lógica de negocio (conversión, clasificación, restricciones, máquina de estados).
- **Repository**: persistencia en MongoDB.
- **Integration**: cliente externo de especificaciones de balanzas con fallback a Redis.

## Reglas de Negocio

- **Unidad Sansa**: 1 S = 1.337 kg.
- **Clasificación**: Liviano (≤10 S), Mediano (>10 y ≤50 S), Pesado (>50 S).
- **Restricción horaria**: no se procesan Pesados entre 20:00 y 06:00.
- **Balanza Prima**: balanzas con ID primo no registran Pesados en días impares del mes.
- **Máquina de estados**: INGRESADO → PESADO → APROBADO/RECHAZADO → DESPACHADO.