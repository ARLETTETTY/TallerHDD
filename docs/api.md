# API - Documentación de Endpoints

La especificación completa de la API está disponible de forma interactiva mediante **Swagger UI**.

## Acceder a Swagger UI

Con la aplicación corriendo, abre en tu navegador:

http://localhost:8080/swagger-ui/index.html

## Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/pesajes` | Registrar un nuevo pesaje |
| GET | `/api/pesajes` | Obtener registros (filtrando por fecha) |
| PUT | `/api/pesajes/{id}/estado` | Actualizar el estado de un pesaje |

## Especificación OpenAPI

La definición OpenAPI 3.1 se genera automáticamente y puede consultarse en:

http://localhost:8080/v3/api-docs