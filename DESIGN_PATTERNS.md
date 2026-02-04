# Patrones de Diseño Implementados

## Resumen Ejecutivo

Este documento describe los patrones de diseño aplicados en el proyecto de Microservicios para Editorial. Se han implementado **4 patrones principales** que mejoran la mantenibilidad, escalabilidad y desacoplamiento del código.

---

## 1. Repository Pattern (Patrón de Repositorio)

### Descripción
El patrón Repository abstrae la lógica de acceso a datos, proporcionando una colección como interfaz para acceder a objetos de dominio.

### Ubicación en el Código

**Authors Service:**
```
authors-service/src/main/java/com/editorial/authors/repository/AuthorRepository.java
```

**Publications Service:**
```
publications-service/src/main/java/com/editorial/publications/repository/PublicationRepository.java
```

### Implementación

#### AuthorRepository
```java
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByEmail(String email);
    Page<Author> findByActive(Boolean active, Pageable pageable);
    boolean existsByEmail(String email);
}
```

#### PublicationRepository
```java
@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    List<Publication> findByAuthorId(Long authorId);
    Page<Publication> findByStatus(PublicationStatus status, Pageable pageable);
    Page<Publication> findByAuthorId(Long authorId, Pageable pageable);
    long countByStatus(PublicationStatus status);
}
```

### Beneficios Implementados
- ✅ **Abstracción de Base de Datos**: Los servicios no conocen detalles de persistencia
- ✅ **Facilidad de Testing**: Fácil crear mocks del repositorio
- ✅ **Cambio de BD sin afectar lógica**: Solo cambiar implementación del repositorio
- ✅ **Reutilización de Código**: CRUD básico heredado de JpaRepository

### Principios SOLID
- **D - Dependency Inversion**: Los servicios dependen de abstracciones (JpaRepository)
- **S - Single Responsibility**: Repositorio solo maneja acceso a datos

---

## 2. Adapter Pattern (Patrón Adaptador)

### Descripción
El patrón Adapter permite la comunicación entre interfaces incompatibles. En este caso, adapta la interfaz del servicio de Autores para su uso dentro del servicio de Publicaciones.

### Ubicación en el Código
```
publications-service/src/main/java/com/editorial/publications/client/AuthorServiceClient.java
```

### Implementación

```java
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorServiceClient {

    private final RestTemplate restTemplate;

    @Value("${authors.service.url}")
    private String authorsServiceUrl;

    /**
     * Adapter method: Verifies author existence
     */
    public boolean authorExists(Long authorId) {
        try {
            String url = authorsServiceUrl + "/authors/" + authorId + "/exists";
            Boolean exists = restTemplate.getForObject(url, Boolean.class);
            return exists != null && exists;
        } catch (RestClientException e) {
            log.warn("Failed to verify author existence for id: {}", authorId, e);
            throw new AuthorServiceException("Unable to verify author with id: " + authorId, e);
        }
    }

    /**
     * Adapter method: Retrieves author information
     */
    public AuthorInfoDTO getAuthorInfo(Long authorId) {
        try {
            String url = authorsServiceUrl + "/authors/" + authorId;
            return restTemplate.getForObject(url, AuthorInfoDTO.class);
        } catch (RestClientException e) {
            log.warn("Failed to fetch author info for id: {}", authorId, e);
            throw new AuthorServiceException("Unable to fetch author with id: " + authorId, e);
        }
    }
}
```

### Uso en PublicationService

```java
@Service
public class PublicationService {
    
    private final AuthorServiceClient authorServiceClient;

    public PublicationDTO createPublication(PublicationDTO dto) {
        // El adapter valida que el autor existe en el otro servicio
        if (!authorServiceClient.authorExists(dto.getAuthorId())) {
            throw new AuthorNotFoundException("Author not found with id: " + dto.getAuthorId());
        }
        // ...resto del código
    }

    private PublicationDTO enrichPublication(PublicationDTO dto) {
        try {
            // El adapter enriquece la publicación con datos del autor
            var authorInfo = authorServiceClient.getAuthorInfo(dto.getAuthorId());
            dto.setAuthor(authorInfo);
        } catch (Exception e) {
            log.warn("Could not enrich publication with author info");
        }
        return dto;
    }
}
```

### Beneficios Implementados
- ✅ **Desacoplamiento Inter-Servicios**: Publications no conoce detalles de Authors
- ✅ **Manejo Centralizado de Errores**: Todas las llamadas HTTP se manejan en un lugar
- ✅ **Configurabilidad**: URL del servicio externo se configura por ambiente
- ✅ **Reintentos y Timeouts**: Gestión centralizada de resiliencia

### Principios SOLID
- **S - Single Responsibility**: AuthorServiceClient solo maneja comunicación HTTP
- **D - Dependency Inversion**: Publications depende del cliente, no de HTTP directo
- **O - Open/Closed**: Fácil agregar más métodos al cliente sin modificar otros código

---

## 3. Strategy Pattern (Patrón Estrategia)

### Descripción
El patrón Strategy define una familia de algoritmos, los encapsula y los hace intercambiables. Se utiliza para validar diferentes estrategias según el estado editorial.

### Ubicación en el Código
```
publications-service/src/main/java/com/editorial/publications/service/PublicationStatusValidator.java
```

### Implementación

```java
@Component
public class PublicationStatusValidator {

    /**
     * Strategy: Validates status change according to business rules
     */
    public void validate(Publication publication, PublicationStatus newStatus) {
        switch (newStatus) {
            case IN_REVIEW:
                validateDraftToReview(publication);
                break;
            case APPROVED:
                validateReviewToApproved(publication);
                break;
            case REJECTED:
                validateReviewToRejected(publication);
                break;
            case PUBLISHED:
                validateApprovedToPublished(publication);
                break;
            case REQUIRES_CHANGES:
                validateReviewToRequiresChanges(publication);
                break;
        }
    }

    private void validateDraftToReview(Publication publication) {
        if (publication.getContent() == null || publication.getContent().trim().isEmpty()) {
            throw new PublicationInvalidStateException("Publication content cannot be empty");
        }
    }

    private void validateReviewToApproved(Publication publication) {
        if (publication.getEditorName() == null || publication.getEditorName().trim().isEmpty()) {
            throw new PublicationInvalidStateException("Editor name is required for approval");
        }
    }

    private void validateReviewToRejected(Publication publication) {
        if (publication.getRejectionReason() == null || publication.getRejectionReason().trim().isEmpty()) {
            throw new PublicationInvalidStateException("Rejection reason is required");
        }
    }

    private void validateApprovedToPublished(Publication publication) {
        // Todas las publicaciones aprobadas pueden ser publicadas
    }

    private void validateReviewToRequiresChanges(Publication publication) {
        if (publication.getReviewComments() == null || publication.getReviewComments().trim().isEmpty()) {
            throw new PublicationInvalidStateException("Review comments are required when requesting changes");
        }
    }
}
```

### Uso en PublicationService

```java
public PublicationDTO changeStatus(Long id, PublicationStatus newStatus) {
    Publication publication = publicationRepository.findById(id)
            .orElseThrow(() -> new PublicationNotFoundException("Publication not found with id: " + id));

    // Validar transición de estado
    if (!publication.canChangeStatus(newStatus)) {
        throw new PublicationInvalidStateException(
                "Cannot change publication status from " + publication.getStatus() + " to " + newStatus);
    }

    // Usar la estrategia de validación apropiada
    statusValidator.validate(publication, newStatus);

    publication.setStatus(newStatus);
    Publication updated = publicationRepository.save(publication);
    
    return enrichPublication(publicationMapper.entityToDTO(updated));
}
```

### Definición de Estados en Entidad Publication

```java
public boolean canChangeStatus(PublicationStatus newStatus) {
    // Define transition rules
    return switch (this.status) {
        case DRAFT -> newStatus == PublicationStatus.IN_REVIEW;
        case IN_REVIEW -> newStatus == PublicationStatus.APPROVED || 
                        newStatus == PublicationStatus.REJECTED ||
                        newStatus == PublicationStatus.REQUIRES_CHANGES;
        case REQUIRES_CHANGES -> newStatus == PublicationStatus.IN_REVIEW;
        case APPROVED -> newStatus == PublicationStatus.PUBLISHED;
        case PUBLISHED, REJECTED -> false; // Final states
    };
}
```

### Beneficios Implementados
- ✅ **Validaciones Específicas por Estado**: Cada transición tiene sus propias reglas
- ✅ **Fácil Mantenimiento**: Agregar nuevas validaciones sin modificar código existente
- ✅ **Separación de Conceptos**: Lógica de validación separada del servicio
- ✅ **Testabilidad**: Fácil de testear cada estrategia independientemente

### Principios SOLID
- **S - Single Responsibility**: Cada método valida una transición específica
- **O - Open/Closed**: Abierto para agregar nuevas validaciones, cerrado para modificación
- **D - Dependency Inversion**: El servicio depende del validador, no de la lógica directa

---

## 4. Mapper/DTO Pattern (Patrón de Mapeo)

### Descripción
El patrón Mapper proporciona una capa de abstracción entre entidades JPA y DTOs de API, evitando exponer la estructura interna de la base de datos.

### Ubicación en el Código

**Authors Service:**
```
authors-service/src/main/java/com/editorial/authors/service/AuthorMapper.java
```

**Publications Service:**
```
publications-service/src/main/java/com/editorial/publications/service/PublicationMapper.java
```

### Implementación

#### AuthorMapper

```java
@Component
public class AuthorMapper {

    public AuthorDTO entityToDTO(Author entity) {
        if (entity == null) {
            return null;
        }

        return AuthorDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .biography(entity.getBiography())
                .organization(entity.getOrganization())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Author dtoToEntity(AuthorDTO dto) {
        if (dto == null) {
            return null;
        }

        return Author.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .biography(dto.getBiography())
                .organization(dto.getOrganization())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public void updateEntityFromDTO(AuthorDTO dto, Author entity) {
        if (dto == null) {
            return;
        }

        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
        // ...más campos
    }
}
```

#### Uso en AuthorService

```java
@Service
public class AuthorService {
    
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorDTO createAuthor(AuthorDTO dto) {
        Author author = authorMapper.dtoToEntity(dto);
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.entityToDTO(savedAuthor);
    }

    @Transactional(readOnly = true)
    public AuthorDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + id));
        return authorMapper.entityToDTO(author);
    }
}
```

### DTOs Definidos

#### AuthorDTO
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    // ...más campos con validaciones
}
```

### Beneficios Implementados
- ✅ **Encapsulación de Datos**: API no expone estructura de BD
- ✅ **Validación en DTOs**: Validaciones declarativas en clases separadas
- ✅ **Versionamiento de API**: Fácil cambiar DTOs sin afectar entidades
- ✅ **Campos Protegidos**: Control de qué campos se pueden leer/escribir
- ✅ **Flexibilidad**: Mismo DTO para múltiples operaciones o DTOs específicos

### Principios SOLID
- **S - Single Responsibility**: Mappers solo convierten entre tipos
- **I - Interface Segregation**: DTOs específicos para diferentes operaciones
- **D - Dependency Inversion**: Controladores dependen de DTOs, no de entidades

---

## Matriz de Aplicación de Patrones

| Patrón | Ubicación | Propósito | Principios SOLID |
|--------|-----------|----------|------------------|
| **Repository** | AuthorRepository, PublicationRepository | Abstracción de datos | D, S |
| **Adapter** | AuthorServiceClient | Inter-service communication | S, D, O |
| **Strategy** | PublicationStatusValidator | Validaciones específicas por estado | S, O, D |
| **Mapper** | AuthorMapper, PublicationMapper | Conversión Entity ↔ DTO | S, I, D |

---

## Arquitectura de Capas

```
┌────────────────────────────────────────┐
│         REST Controller                │ (Punto de entrada)
│  Maneja requests/responses HTTP        │
└────────────┬─────────────────────────┘
             │
┌────────────▼─────────────────────────┐
│         Service Layer                │ (Lógica de Negocio)
│  - Orquestra operaciones              │
│  - Usa Validators (Strategy)          │
│  - Usa Adapters para otros servicios  │
└────────────┬─────────────────────────┘
             │
       ┌─────┴──────┬───────────┐
       │            │           │
┌──────▼──┐ ┌──────▼────┐ ┌───▼──────────┐
│Repository│ │  Mapper   │ │   Client    │
│  (Data)  │ │ (DTO Conv)│ │ (Adapter)   │
└──────────┘ └───────────┘ └─────────────┘
       │
┌──────▼─────────────────────────────┐
│    Database/External Services      │
└────────────────────────────────────┘
```

---

## Conclusiones

La aplicación de estos 4 patrones de diseño proporciona:

1. **Mantenibilidad**: Código organizado en responsabilidades claras
2. **Escalabilidad**: Fácil agregar nuevas funcionalidades sin romper existentes
3. **Testabilidad**: Todas las capas pueden ser testeadas independientemente
4. **Desacoplamiento**: Servicios débilmente acoplados y altamente cohesivos
5. **Flexibilidad**: Cambios en una capa no afectan otras

Estos patrones, combinados con los principios SOLID, crean una arquitectura robusta y profesional.
