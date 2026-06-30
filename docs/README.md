# SansaWeigh 

Microservicio para gestionar estaciones de pesaje de paquetes de la empresa de logística **SansaWeigh**.

## ¿Qué hace el sistema?

- Clasifica paquetes por peso usando la unidad propietaria **Sansa** (1 S = 1.337 kg)
- Calcula y valida reglas de negocio dinámicas
- Persiste el historial de pesajes en **MongoDB**
- Cachea configuraciones de balanzas en **Redis**
- Se integra con un registro externo de especificaciones de balanzas

## Tecnologías

- Java 17
- Spring Boot 4.1
- Spring Data MongoDB / Redis
- JUnit 5, Mockito & AssertJ