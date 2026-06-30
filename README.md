# SansaWeigh 

Microservicio de gestión de estaciones de pesaje de paquetes para la empresa de logística **SansaWeigh**, desarrollado con Spring Boot 4.

## Documentación

La documentación completa del proyecto está construida con **Docsify** y se encuentra en la carpeta `docs/`.

### Cómo levantar la documentación

Desde la raíz del proyecto, ejecuta:

```bash
cd docs
python -m http.server 3000
```

Luego abre en tu navegador:

http://localhost:3000

### Contenido de la documentación

- **Inicio**: descripción general del sistema
- **Arquitectura**: capas y reglas de negocio
- **Configuración del Entorno**: requisitos y cómo ejecutar el proyecto
- **Manual de Usuario**: guía de uso de la API
- **API (Swagger)**: especificación de endpoints

##  Tecnologías

- Java 17
- Spring Boot 4.1
- Spring Data MongoDB / Redis
- JUnit 5, Mockito & AssertJ
- Docsify + Swagger UI (OpenAPI 3.1)

## Ejecución rápida

```bash
# Levantar bases de datos
docker run -d --name mongo-sansa -p 27017:27017 mongo:latest
docker run -d --name redis-sansa -p 6379:6379 redis:latest

# Ejecutar la aplicación
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080` y Swagger UI en `http://localhost:8080/swagger-ui/index.html`.

