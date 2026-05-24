# Task Manager API

API REST de gestión de tareas con autenticación JWT, construida con Spring Boot 3 y Java 21.

Cada usuario solo puede ver y gestionar sus propias tareas. La autenticación está basada en tokens JWT con Spring Security.

---

## Tecnologías

- Java 21
- Spring Boot 3.5
- Spring Security + JWT (jjwt 0.12)
- Spring Data JPA + Hibernate
- H2 Database (desarrollo)
- Lombok
- Maven

---

## Funcionalidades

- Registro e inicio de sesión de usuarios
- Generación y validación de tokens JWT
- CRUD completo de tareas por usuario autenticado
- Cada usuario solo accede a sus propias tareas
- Manejo centralizado de excepciones

---

## Estructura del proyecto

```
src/main/java/com/fjconde/taskmanager/
├── config/          # Configuración de seguridad
├── controller/      # Endpoints REST (auth y tareas)
├── dto/             # Objetos de transferencia de datos
│   ├── auth/
│   └── tarea/
├── entity/          # Entidades JPA (Usuario, Tarea)
├── exception/       # Manejo global de errores
├── repository/      # Repositorios JPA
├── security/        # Filtro JWT y servicio de tokens
└── service/         # Lógica de negocio
```

---

## Endpoints

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/registro` | Registrar nuevo usuario |
| POST | `/api/auth/login` | Iniciar sesión, devuelve JWT |

### Tareas (requieren JWT)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/tareas` | Listar tareas del usuario |
| POST | `/api/tareas` | Crear nueva tarea |
| PUT | `/api/tareas/{id}` | Actualizar tarea |
| DELETE | `/api/tareas/{id}` | Eliminar tarea |

---

## Cómo ejecutar

### Requisitos
- Java 21
- Maven 3.8+

### Pasos

```bash
# Clonar el repositorio
git clone https://github.com/JaviConde97/task-manager-api.git
cd task-manager-api

# Ejecutar
./mvnw spring-boot:run
```

La API arranca en `http://localhost:8080`.
La consola H2 está disponible en `http://localhost:8080/h2-console`.

---

## Ejemplo de uso

**Registro:**
```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Javi", "email": "javi@example.com", "password": "12345678"}'
```

**Login y uso del token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "javi@example.com", "password": "12345678"}'

# Usar el token devuelto:
curl http://localhost:8080/api/tareas \
  -H "Authorization: Bearer <token>"
```

---

## Estado del proyecto

🚧 En desarrollo
