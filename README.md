# Liverpool Test — User Management API

REST API para gestión de usuarios con resolución de dirección por código postal, construida con **Spring Boot 4** siguiendo **arquitectura hexagonal**.

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Framework | Spring Boot 4.0.3 + Spring Web MVC |
| Persistencia | MongoDB 7 + Spring Data MongoDB |
| Caché | Redis 7 + Spring Cache |
| API externa | COPOMEX via OpenFeign (Spring Cloud 2025.1.0) |
| Documentación | SpringDoc OpenAPI 3 (Swagger UI) |
| Validación | Jakarta Bean Validation |
| Build | Maven Wrapper |
| Contenedores | Docker + Docker Compose |

---

## Arquitectura

El proyecto sigue **arquitectura hexagonal (Ports & Adapters)**:

```
src/main/java/com/liverpool/liverpooltest/
├── domain/                          # Núcleo — sin dependencias de frameworks
│   ├── model/                       # User, Address
│   ├── port/
│   │   ├── in/  UserUseCase         # Puerto de entrada (driving)
│   │   └── out/ UserRepositoryPort  # Puerto de salida (driven)
│   │        SepomexPort
│   └── exception/                   # UserNotFoundException, PostalCodeNotFoundException
│
├── application/
│   └── service/ UserService         # Orquesta la lógica de negocio
│
└── infrastructure/                  # Adaptadores
    ├── adapter/
    │   ├── in/rest/                 # REST Controller + DTOs de entrada/salida
    │   └── out/
    │       ├── persistence/         # MongoDB (UserRepositoryAdapter)
    │       └── external/copomex/    # Feign client hacia api.copomex.com
    ├── config/                      # CacheConfig, OpenApiConfig
    └── exception/                   # GlobalExceptionHandler, ErrorResponse
```

La regla de dependencia se mantiene estrictamente: **infrastructure → application → domain**. El dominio no importa nada de Spring.

---

## Endpoints

Base URL: `http://localhost:8080/api/v1`

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/users` | Crea un usuario; resuelve la dirección desde COPOMEX |
| `GET` | `/users` | Lista usuarios paginada (`?page`, `?size`, `?sort`) |
| `GET` | `/users/{id}` | Obtiene un usuario por ID |
| `PATCH` | `/users/{id}` | Actualización parcial (solo los campos enviados) |
| `DELETE` | `/users/{id}` | Elimina un usuario |

### Crear usuario — cuerpo de ejemplo

```json
{
  "name": "Juan",
  "paternalLastName": "Pérez",
  "maternalLastName": "García",
  "email": "juan@example.com",
  "postalCode": "06600"
}
```

### Respuesta

```json
{
  "id": "abc123",
  "name": "Juan",
  "paternalLastName": "Pérez",
  "maternalLastName": "García",
  "email": "juan@example.com",
  "address": {
    "postalCode": "06600",
    "municipality": "Cuauhtémoc",
    "state": "Ciudad de México",
    "city": "Ciudad de México",
    "neighborhoods": ["Juárez", "Cuauhtémoc"],
    "country": "México"
  }
}
```

### Listado paginado — respuesta

```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 42,
  "totalPages": 3
}
```

---

## Decisiones de diseño destacadas

**Integración con COPOMEX con caché Redis**
Al crear o actualizar un usuario con código postal, la API consulta [api.copomex.com](https://api.copomex.com) para resolver municipio, estado, ciudad y colonias. La respuesta queda en caché en Redis con un TTL de 24 horas para evitar llamadas repetidas al mismo código postal.

**PATCH con actualización parcial**
El endpoint de actualización acepta cualquier subconjunto de campos. Los campos no enviados conservan su valor actual. El código postal sólo llama a COPOMEX si se incluye en la petición.

**DTO de paginación personalizado**
En lugar de exponer el objeto `Page` de Spring (con decenas de campos internos), se devuelve un `PageResponse<T>` limpio con únicamente: `content`, `page`, `size`, `totalElements`, `totalPages`.

**Manejo de errores centralizado**
`GlobalExceptionHandler` traduce excepciones de dominio a respuestas HTTP con estructura uniforme:
```json
{ "status": 404, "message": "User not found with id: xyz", "timestamp": "..." }
```

---

## Levantar el entorno local

### Requisitos previos

- Docker y Docker Compose instalados
- Java 17+ (para desarrollo local sin Docker)

### Con Docker Compose (recomendado)

```bash
docker-compose up
```

Levanta:
- **MongoDB 7** en `localhost:27017`
- **Redis 7** en `localhost:6379`
- **Aplicación** en `http://localhost:8080`

Variables de entorno disponibles:

```bash
SERVER_PORT=8080        # Puerto de la aplicación (default: 8080)
MONGODB_URI=...         # URI completa de MongoDB
COPOMEX_TOKEN=...       # Token de api.copomex.com (default: pruebas)
REDIS_HOST=localhost    # Host de Redis
REDIS_PORT=6379         # Puerto de Redis
```

### Solo infraestructura (desarrollo local)

```bash
# Levantar solo MongoDB y Redis
docker-compose up mongodb redis

# Ejecutar la aplicación con perfil local
./mvnw spring-boot:run
```

El perfil `local` (`application-local.yml`) apunta a las instancias de Docker en localhost.

---

## Tests

```bash
./mvnw test
```

**32 tests** distribuidos en tres capas:

| Suite | Tipo | Tests |
|---|---|---|
| `UserControllerTest` | Slice (`@WebMvcTest`) | 16 |
| `UserServiceTest` | Unitario (Mockito) | 10 |
| `CopomexAdapterTest` | Unitario (Mockito) | 5 |
| `LiverpoolTestApplicationTests` | Integración (`@SpringBootTest`) | 1 |

Los tests son completamente independientes de infraestructura externa: MongoDB y Redis no necesitan estar corriendo para ejecutarlos.

---

## Swagger UI

Con la aplicación corriendo:

```
http://localhost:8080/swagger-ui.html
```

Documentación OpenAPI en JSON:

```
http://localhost:8080/api-docs
```

---

## Colección Postman

El archivo `Liverpool-Users-API.postman_collection.json` incluye los 5 endpoints listos para ejecutar.

**Importar:**
1. Abrir Postman → **Import**
2. Seleccionar `Liverpool-Users-API.postman_collection.json`

**Variables de la colección:**

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `baseUrl` | `http://localhost:8080` | URL base de la API |
| `userId` | *(vacío)* | Se llena automáticamente al crear un usuario |

**Flujo de prueba sugerido:**
1. `POST /users` — crea el usuario y guarda el `id` en `{{userId}}`
2. `GET /users` — lista paginada
3. `GET /users/{{userId}}` — consulta por ID
4. `PATCH /users/{{userId}}` — actualización parcial
5. `DELETE /users/{{userId}}` — eliminación

Cada request incluye tests automáticos que verifican el status HTTP y la estructura de la respuesta.

---

## Compilar sin tests

```bash
./mvnw clean package -DskipTests
```

El artefacto se genera en `target/liverpool-test-0.0.1-SNAPSHOT.jar`.
