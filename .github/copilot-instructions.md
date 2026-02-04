# Copilot Instructions for Editorial Microservicios

## Project Overview

**Editorial Management System** - A microservices-based platform for managing authors and publications using Spring Boot 3.x, React, PostgreSQL, and Docker Compose.

```
Frontend (React:3000) ↔ Authors Service (8001) + Publications Service (8002)
                       ↓                        ↓
                  PostgreSQL (5431)    PostgreSQL (5432)
```

Two independent Spring Boot microservices with separate databases, communicating via HTTP/REST.

---

## Architecture & Key Patterns

### Microservices Structure

- **authors-service**: Manages authors (CRUD, pagination, validation)
- **publications-service**: Manages publications with state machine workflow
- **frontend**: React app with tab-based UI for both domains

Each service has:
- `entity/` - JPA entities (extend `BaseEntity` for `id`, `createdAt`, `updatedAt`)
- `dto/` - Transfer objects with validation annotations (`@NotBlank`, `@Email`, etc.)
- `service/` - Business logic with `@Transactional`, logging via `@Slf4j`
- `controller/` - REST endpoints with `@RestController`, `@RequestMapping`
- `repository/` - JPA repositories extending `JpaRepository<T, Long>`
- `exception/` - Custom exceptions extending `RuntimeException`

### Inter-Service Communication (Adapter Pattern)

Publications Service calls Authors Service via `AuthorServiceClient`:
- Located in: `publications-service/client/AuthorServiceClient.java`
- Configuration: `RestClientConfig.java` (5s timeout for both connect/read)
- Endpoint verification: `GET /api/authors/{id}/exists`
- Endpoint info fetch: `GET /api/authors/{id}`
- Exception handling: Wraps failures as `AuthorServiceException`

**When adding cross-service calls**: Create wrapper in `client/` folder, inject `RestTemplate` bean, handle timeouts/errors gracefully with logging.

### Error Handling

Both services use `@RestControllerAdvice` for centralized exception handling:
- Custom exceptions get meaningful HTTP status codes (409 for conflicts, 404 for not found)
- Validation errors return field-level error messages
- All responses include `timestamp`, `status`, `error`, `message`
- Failures logged with `@Slf4j` at appropriate levels (warn, error)

---

## Critical Developer Workflows

### Build & Run via Docker Compose

```bash
# Start all services (from project root)
docker-compose up -d

# View logs
docker-compose logs -f [authors-service|publications-service|frontend]

# Stop without deleting data
docker-compose stop

# Destroy everything (including DB data)
docker-compose down -v
```

Services are healthy only when databases pass healthchecks. Check status with `docker-compose ps`.

### Database Configuration

- **Authors DB**: PostgreSQL 5431, database `authors_db`
- **Publications DB**: PostgreSQL 5432, database `publications_db`
- **Credentials**: User/password from `.env` (defaults to `postgres/postgres`)

Services auto-initialize schemas via Spring Data JPA `ddl-auto: update`.

### Local Development (without Docker)

Requires: Java 17, Maven 3.9+

```bash
# Each service
cd authors-service
mvn clean install
mvn spring-boot:run

# Frontend
cd frontend
npm install
npm run dev
```

API base URLs configured in `src/main/resources/application.yml` per service and `frontend/src/api.js` for React.

### Testing Strategy

- Unit tests use mocked repositories
- Service layer tested via JUnit/Mockito in `src/test/java`
- Integration tests should mock inter-service calls (use `@MockBean` for `AuthorServiceClient`)
- Frontend tests use Vitest (configured in `vite.config.js`)

---

## Project-Specific Conventions

### Naming & Packages

- Java packages: `com.editorial.{serviceName}.{layer}` (e.g., `com.editorial.authors.service`)
- DTOs: `{Entity}DTO` with `@Builder`, `@Data`, Lombok annotations
- Entities: Plain JPA classes, use `@Entity`, `@Table`, `@Column` with constraints
- Mappers: `{Entity}Mapper` for DTO ↔ Entity conversions
- Exceptions: Named descriptively (e.g., `AuthorNotFoundException`, `AuthorAlreadyExistsException`)

### Common Imports & Dependencies

- **Lombok**: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`, `@RequiredArgsConstructor`, `@Slf4j`
- **Spring**: `@Service`, `@Component`, `@Repository`, `@RestController`, `@Transactional`
- **Validation**: `jakarta.validation.constraints.*` (`@NotBlank`, `@Email`, `@Size`, etc.)
- **JSON**: `com.fasterxml.jackson.annotation.JsonProperty` for API contracts
- **Pagination**: `org.springframework.data.domain.Page`, `Pageable`, `PageRequest`

### Frontend Conventions

- React components in `src/components/` with `.jsx` extension
- API calls via `src/api.js` using axios (separate instances per service)
- CSS modules co-located with components (e.g., `AuthorsTab.jsx` + `AuthorsTab.css`)
- Tab-based navigation with state management via `useState`
- Environment variables: `REACT_APP_AUTHORS_API_URL`, `REACT_APP_PUBLICATIONS_API_URL`

---

## Critical Files to Know

| Purpose | File |
|---------|------|
| Architecture overview | [README.md](../README.md) |
| Quick-start commands | [QUICKSTART.md](../QUICKSTART.md) |
| Design patterns explained | [DESIGN_PATTERNS.md](../DESIGN_PATTERNS.md) |
| Service composition | [docker-compose.yml](../docker-compose.yml) |
| Authors entity/service | [authors-service/src/main/java/com/editorial/authors](../authors-service/src/main/java/com/editorial/authors) |
| Publications entity/service | [publications-service/src/main/java/com/editorial/publications](../publications-service/src/main/java/com/editorial/publications) |
| Inter-service adapter | [publications-service/.../client/AuthorServiceClient.java](../publications-service/src/main/java/com/editorial/publications/client/AuthorServiceClient.java) |
| Frontend entry | [frontend/src/App.jsx](../frontend/src/App.jsx) |

---

## Common Tasks for AI Agents

**Adding a new endpoint**:
1. Add method to service layer (with `@Transactional` if writes)
2. Add corresponding `@RequestMapping`/`@GetMapping`/`@PostMapping` in controller
3. Handle validation in DTO, exceptions in handler
4. Add repository method if querying differently

**Fixing inter-service calls**:
1. Check `AuthorServiceClient` timeout and exception handling
2. Verify target service URL in `application.yml`
3. Ensure client is injected and error recovery is explicit

**Debugging failures**:
1. Check Docker logs: `docker-compose logs -f {service}`
2. Verify DB connectivity: `docker-compose ps` (health status)
3. Validate JSON contracts match DTO fields (`@JsonProperty` overrides)
4. Check pagination params (default page=0, size=10)

---

## Key Dependencies & Versions

- **Java**: 17 (maven.compiler.target/source)
- **Spring Boot**: 3.2.1 (from parent pom)
- **PostgreSQL Driver**: 42.7.1
- **Lombok**: Latest from Spring Boot parent
- **React**: 18.x via Vite
- **Axios**: Latest (frontend HTTP client)

Database migrations handled by JPA `ddl-auto: update` (not Flyway/Liquibase).
