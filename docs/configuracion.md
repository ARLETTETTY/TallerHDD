# Configuración del Entorno

## Requisitos

- Java 17+
- Maven
- Docker (para MongoDB y Redis)

## Levantar las bases de datos

```bash
docker run -d --name mongo-sansa -p 27017:27017 mongo:latest
docker run -d --name redis-sansa -p 6379:6379 redis:latest
```

## Ejecutar el proyecto

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.