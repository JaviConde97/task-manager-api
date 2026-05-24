# Task Manager API

API REST para gestión de tareas con autenticación JWT.
Cada usuario gestiona únicamente sus propias tareas.

---

## Stack

Java 21 · Spring Boot 3.5 · Spring Security · JWT · JPA/Hibernate · H2 (dev) · PostgreSQL (prod) · Docker · Maven

---

## Funcionalidades

- Registro e inicio de sesión con token JWT
- CRUD completo de tareas por usuario autenticado
- Aislamiento de datos: cada usuario solo accede a sus propias tareas
- Validación de campos en todas las peticiones
- Manejo centralizado de errores con respuestas JSON limpias
- Documentación interactiva con Swagger UI
- Perfiles separados para desarrollo (H2) y producción (PostgreSQL)
- Dockerizado con docker-compose

---

## Estructura

```
src/main/java/com/fjconde/taskmanager/
├── config/
├── controller/
├── dto/
│   ├── auth/
│   └── tarea/
├── entity/
├── exception/
├── repository/
├── security/
└── service/
```

---

## Endpoints

### Autenticación (públicos)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/registro` | Registrar nuevo usuario |
| POST | `/api/auth/login` | Iniciar sesión → devuelve token JWT |

### Tareas (requieren token JWT)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/tareas` | Listar tareas del usuario |
| POST | `/api/tareas` | Crear tarea |
| PUT | `/api/tareas/{id}` | Actualizar tarea |
| DELETE | `/api/tareas/{id}` | Eliminar tarea |

---

## Uso

**1. Registro:**
```json
POST /api/auth/registro
{
  "nombre": "Javi",
  "email": "javi@example.com",
  "password": "12345678"
}
```

**2. Login** → devuelve el token:
```json
POST /api/auth/login
→ { "token": "eyJhbGci..." }
```

**3. Usar el token** en el header de cada petición:
```
Authorization: Bearer <token>
```

**4. Crear tarea:**
```json
POST /api/tareas
{
  "titulo": "Estudiar Spring Security",
  "descripcion": "Repasar filtros y JWT"
}
```

**5. Actualizar tarea:**
```json
PUT /api/tareas/1
{
  "titulo": "Estudiar Spring Security",
  "descripcion": "Repasar filtros y JWT",
  "completada": true
}
```

---

## Ejecución

### Opción 1 — Local (modo desarrollo con H2)

**Requisitos:** Java 21+, Maven 3.8+

```bash
git clone https://github.com/JaviConde97/task-manager-api.git
cd task-manager-api
./mvnw spring-boot:run
```

La API arranca en `http://localhost:8080`.

### Opción 2 — Docker (modo producción con PostgreSQL)

**Requisitos:** Docker y Docker Compose

```bash
git clone https://github.com/JaviConde97/task-manager-api.git
cd task-manager-api
docker-compose up --build
```

La API arranca en `http://localhost:8080` con PostgreSQL como base de datos.

---

## Swagger UI

Documentación interactiva disponible en `http://localhost:8080/swagger-ui.html`.

Permite explorar y probar todos los endpoints directamente desde el navegador.
Para los endpoints de tareas, haz clic en **Authorize** e introduce el token JWT obtenido en el login.

---

## Consola H2

Disponible en `http://localhost:8080/h2-console` (solo en modo desarrollo).

- JDBC URL: `jdbc:h2:mem:taskmanagerdb`
- Usuario: `sa` · Contraseña: *(vacía)*

---

## Colección Postman

Disponible en [`docs/postman_collection.json`](docs/postman_collection.json).

Importa el archivo en Postman para tener todas las peticiones listas. El token JWT se guarda automáticamente al hacer login o registro.

---

## Documentación técnica

Decisiones de arquitectura, orden de implementación y explicaciones del código en [`docs/desarrollo.md`](docs/desarrollo.md).
