# Diario de desarrollo — Task Manager API

Registro cronológico del proceso de construcción del proyecto.
Cada fase documenta qué se construyó, qué archivos se tocaron y las decisiones técnicas tomadas.

---

## Índice

- [Stack y tecnologías](#stack-y-tecnologías)
- [Fase 1 — Configuración inicial del proyecto](#fase-1--configuración-inicial-del-proyecto)
- [Fase 2 — Autenticación JWT](#fase-2--autenticación-jwt)
- [Cosas a tener en cuenta](#cosas-a-tener-en-cuenta)

---

## Stack y tecnologías

| Tecnología | Versión | Por qué |
|------------|---------|---------|
| Java | 21 LTS | Versión LTS más moderna, la más adoptada en empresas actualmente |
| Spring Boot | 3.5.14 | Estable y ampliamente usado en producción. Se evitó 4.x por ser demasiado nuevo |
| Spring Security + JWT | 6.x / jjwt 0.12.6 | Estándar del sector para APIs REST sin estado |
| Spring Data JPA | incluido en Boot | Evita escribir SQL repetitivo, gestiona las relaciones entre entidades automáticamente |
| H2 Database | incluido en Boot | Base de datos en memoria para desarrollo, sin instalar nada extra |
| Lombok | incluido en Boot | Elimina código boilerplate: getters, setters, constructores, builders |
| Maven | 3.8+ | Gestor de dependencias estándar en entornos enterprise |

---

## Fase 1 — Configuración inicial del proyecto

**Rama:** `develop`  
**Commit:** `feat: estructura base del proyecto con Spring Boot 3.5 y JWT`

### Qué se hizo

Generación del proyecto base con Spring Initializr y configuración del entorno.

### Archivos creados

| Archivo | Descripción |
|---------|-------------|
| `pom.xml` | Dependencias del proyecto. Se añadieron manualmente las 3 librerías JWT (jjwt-api, jjwt-impl, jjwt-jackson) ya que Spring Initializr no las incluye |
| `src/main/resources/application.properties` | Configuración de H2, JPA y los valores del JWT |
| `README.md` | Documentación pública del proyecto |
| `docs/desarrollo.md` | Este archivo |

### Archivos eliminados

| Archivo | Motivo |
|---------|--------|
| `HELP.md` | Generado automáticamente por Spring Initializr, no aporta nada al portafolio |

### Estructura de carpetas creada

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

### Decisiones técnicas

**Spring Boot 3.5.14 en lugar de 4.x** — La versión 4.x existe pero es demasiado nueva para un portafolio orientado a encontrar trabajo. Las empresas siguen en 3.x y es lo que los recruiters reconocen.

**H2 en lugar de PostgreSQL/MySQL** — Para desarrollo local, H2 arranca solo sin instalar nada. El esquema se crea y destruye en cada arranque. Se añadirá PostgreSQL cuando el proyecto esté más maduro.

**Package `com.fjconde.taskmanager`** — Los guiones no son válidos en nombres de paquetes Java, por eso `task-manager-api` se convierte en `taskmanager`.

### Comandos Git

```bash
git init
git remote add origin https://github.com/JaviConde97/task-manager-api.git
git checkout -b develop
git add .
git commit -m "feat: estructura base del proyecto con Spring Boot 3.5 y JWT"
git push -u origin develop

# Crear rama main y establecerla como rama por defecto en GitHub
git checkout -b main
git push -u origin main
```

---

## Fase 2 — Autenticación JWT

**Rama:** `feature/auth`  
**Commit:** `feat: implementación completa de autenticación JWT`

### Qué se hizo

Implementación completa del sistema de registro, login y autenticación basada en tokens JWT. Esta es la base de toda la seguridad de la API.

### Archivos creados

| Archivo | Descripción |
|---------|-------------|
| `entity/Usuario.java` | Entidad JPA que representa la tabla `usuarios` |
| `entity/Tarea.java` | Entidad JPA que representa la tabla `tareas` |
| `repository/UsuarioRepository.java` | Acceso a BD para usuarios: `findByEmail`, `existsByEmail` |
| `repository/TareaRepository.java` | Acceso a BD para tareas: `findByUsuarioId`, `findByIdAndUsuarioId` |
| `security/JwtService.java` | Genera y valida tokens JWT |
| `security/JwtAuthFilter.java` | Filtro que intercepta cada petición y verifica el token |
| `config/AppConfig.java` | Beans de `UserDetailsService` y `PasswordEncoder` |
| `config/SecurityConfig.java` | Reglas de seguridad: rutas públicas vs protegidas |
| `dto/auth/RegistroRequest.java` | Datos de entrada para registro: nombre, email, password |
| `dto/auth/LoginRequest.java` | Datos de entrada para login: email, password |
| `dto/auth/AuthResponse.java` | Respuesta con el token JWT |
| `dto/tarea/TareaRequest.java` | Datos de entrada para crear/actualizar tarea |
| `dto/tarea/TareaResponse.java` | Datos de salida de una tarea (evita exponer la entidad directa) |
| `service/AuthService.java` | Lógica de registro y login |
| `service/TareaService.java` | Lógica de CRUD de tareas |
| `controller/AuthController.java` | Endpoints: POST `/api/auth/registro` y `/api/auth/login` |
| `controller/TareaController.java` | Endpoints: GET/POST/PUT/DELETE `/api/tareas` |
| `exception/RecursoNoEncontradoException.java` | Excepción personalizada → HTTP 404 |
| `exception/GlobalExceptionHandler.java` | Captura todos los errores y devuelve JSON limpio |

### Archivos modificados

| Archivo | Qué cambió |
|---------|------------|
| `pom.xml` | Se añadieron las dependencias JWT (jjwt-api, jjwt-impl, jjwt-jackson v0.12.6) |
| `application.properties` | Se añadió configuración de H2, JPA y los parámetros `jwt.secret` y `jwt.expiration`. Se eliminó `spring.jpa.database-platform` (deprecado en H2) y se añadió `spring.jpa.open-in-view=false` |

### Orden de implementación y por qué

El orden no es arbitrario — cada capa depende de la anterior:

```
Entity → Repository → Security → DTO → Service → Controller
```

1. **Entities** primero porque todo lo demás las referencia
2. **Repositories** sobre las entities para tener acceso a BD
3. **Security** antes que los services porque los services la necesitan
4. **DTOs** definen el contrato antes de escribir la lógica
5. **Services** con toda la lógica de negocio
6. **Controllers** al final, solo orquestan llamadas al service

### Cómo funciona el JWT

Un token JWT tiene tres partes separadas por puntos:

```
eyJhbGciOiJIUzUxMiJ9               ← Header: algoritmo (HS512)
.eyJzdWIiOiJqYXZpQHRlc3QuY29tIn0   ← Payload: email + fechas de creación y expiración
.s1NeFVR_bEAHl8rWOgBf...            ← Signature: garantiza que nadie ha manipulado el token
```

Flujo completo de una petición autenticada:

```
Cliente                              Servidor
  │                                     │
  │─── POST /api/auth/login ───────────>│
  │    { email, password }              │  AuthService valida credenciales
  │                                     │  JwtService genera el token
  │<─── { "token": "eyJ..." } ──────────│
  │                                     │
  │  (cliente guarda el token)          │
  │                                     │
  │─── GET /api/tareas ────────────────>│
  │    Authorization: Bearer eyJ...     │  JwtAuthFilter intercepta
  │                                     │  → extrae token del header
  │                                     │  → valida firma y expiración
  │                                     │  → carga usuario de BD
  │                                     │  → registra autenticación en Spring
  │                                     │  TareaController recibe la petición
  │<─── [ lista de tareas ] ────────────│  (solo las del usuario autenticado)
```

### Problema encontrado y solución

**Referencia circular entre beans de Spring:**

`SecurityConfig` inyectaba `JwtAuthFilter`, que necesitaba `UserDetailsService`, que estaba definido dentro del propio `SecurityConfig`. Ciclo cerrado que Spring no puede resolver.

**Solución:** separar `UserDetailsService` y `PasswordEncoder` en una clase `AppConfig` independiente, rompiendo el ciclo.

```
Antes (ciclo):
SecurityConfig → JwtAuthFilter → UserDetailsService ← SecurityConfig ❌

Después (sin ciclo):
AppConfig → UserDetailsService
SecurityConfig → JwtAuthFilter → UserDetailsService (de AppConfig) ✅
```

### Comandos Git

```bash
git checkout develop
git checkout -b feature/auth
git add .
git commit -m "feat: implementación completa de autenticación JWT"
git push origin feature/auth
```

---

## Cosas a tener en cuenta

Detalles del código que no son obvios a primera vista pero son importantes.

**`@AuthenticationPrincipal` en los controllers**  
En lugar de leer el token manualmente en cada endpoint, Spring Security inyecta directamente el usuario autenticado. El controller no sabe nada de JWT — solo recibe el usuario ya validado. Más limpio y más seguro.

**`findByIdAndUsuarioId` en el repositorio de tareas**  
La seguridad no solo está en el JWT. Aunque alguien tenga un token válido, solo puede acceder a sus propias tareas. Si el usuario 1 intenta obtener la tarea con id 5 que pertenece al usuario 2, el repositorio no la encuentra. Doble capa de protección.

**`passwordEncoder.encode()` en el registro**  
Las contraseñas nunca se guardan en texto plano. BCrypt las transforma en un hash irreversible. Si alguien accediera a la base de datos no podría recuperar las contraseñas originales.

**`TareaResponse` en lugar de devolver `Tarea` directamente**  
Devolver la entidad JPA directamente expone campos internos (`usuario_id`, relaciones lazy, etc.) y puede causar errores de serialización. El DTO controla exactamente qué información sale de la API.

**La base de datos es en memoria**  
H2 se reinicia con cada arranque. Todo lo que se inserte desaparece al parar el servidor. Es intencionado para desarrollo — en producción se usará PostgreSQL.

**El secreto JWT no es seguro para producción**  
La clave en `application.properties` es un placeholder. En producción iría en una variable de entorno, nunca en el código fuente ni en el repositorio.

---

*Próxima fase: tests unitarios con JUnit y Mockito*
