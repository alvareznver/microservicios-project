# Editorial Management System - Microservicios

Sistema de gestión de autores y publicaciones basado en arquitectura de microservicios con Spring Boot, React y Docker Compose.

## 📋 Descripción

Este proyecto implementa una solución completa para gestionar:
- **Autores**: Registro, consulta y actualización de información de autores
- **Publicaciones**: Creación, gestión de estados editoriales y publicación de contenidos

## 🏗️ Arquitectura

### Componentes

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (React)                        │
│                       :3000                                 │
└──────────┬──────────────────────────────────────┬──────────┘
           │                                      │
    ┌──────▼─────────┐                   ┌───────▼──────────┐
    │ Authors Service│                   │Publications Svc  │
    │   (Spring)     │                   │   (Spring)       │
    │    :8001       │◄──────────────────┤    :8002         │
    └──────┬─────────┘  HTTP/REST        └───────┬──────────┘
           │                                      │
    ┌──────▼──────────┐                  ┌───────▼──────────┐
    │  PostgreSQL DB  │                  │  PostgreSQL DB   │
    │  (authors_db)   │                  │(publications_db) │
    │    :5431        │                  │    :5432         │
    └─────────────────┘                  └──────────────────┘
```

## 🚀 Requisitos

- Docker y Docker Compose (versión 20.10+)
- Git
- (Opcional) Java 17+ y Maven 3.9+ para desarrollo local

## 📦 Instalación y Ejecución

### 1. Clonar o descargar el repositorio

```bash
cd microservicios-project
```

### 2. Configurar variables de entorno

El archivo `.env` ya está preconfigurado. Modificar si es necesario:

```bash
cat .env
```

### 3. Construir y ejecutar con Docker Compose

```bash
# Construir todas las imágenes
docker-compose build

# Iniciar todos los servicios
docker-compose up

# En background (recomendado)
docker-compose up -d
```

### 4. Verificar que los servicios estén activos

```bash
docker-compose ps
```

Debería ver algo como:
```
CONTAINER ID   IMAGE                    STATUS
...            authors-service          Up (healthy)
...            publications-service     Up (healthy)
...            db-authors              Up (healthy)
...            db-publications         Up (healthy)
...            frontend               Up
```

### 5. Acceder a la aplicación

- **Frontend**: http://localhost:3000
- **Authors API**: http://localhost:8001/api/authors
- **Publications API**: http://localhost:8002/api/publications

## 📚 API Endpoints

### Authors Service (Puerto 8001)

```
POST   /api/authors              - Crear autor
GET    /api/authors              - Listar autores (paginado)
GET    /api/authors/{id}         - Obtener autor específico
GET    /api/authors/{id}/exists  - Verificar si autor existe
PUT    /api/authors/{id}         - Actualizar autor
DELETE /api/authors/{id}         - Eliminar autor (soft delete)
```

### Publications Service (Puerto 8002)

```
POST   /api/publications                        - Crear publicación
GET    /api/publications                        - Listar publicaciones (paginado)
GET    /api/publications/{id}                   - Obtener publicación específica
GET    /api/publications/author/{authorId}     - Listar por autor
PATCH  /api/publications/{id}/status           - Cambiar estado editorial
```

## 🔄 Estados Editoriales de Publicaciones

```
DRAFT ──→ IN_REVIEW ──→ APPROVED ──→ PUBLISHED
              ↓
          REJECTED
              ↓
    REQUIRES_CHANGES ──→ IN_REVIEW
```

## 🏛️ Principios SOLID Implementados

### S - Single Responsibility Principle (SRP)
- **Controllers**: Manejan solo requests/responses
- **Services**: Contienen lógica de negocio
- **Repositories**: Acceso a datos
- **Mappers**: Conversión de DTOs
- **Validators**: Validación de reglas de negocio

### O - Open/Closed Principle
- Clases abstractas base (`BaseEntity`) extendidas por entidades derivadas
- Uso de interfaces para inyección de dependencias

### L - Liskov Substitution Principle
- Las clases derivadas (`Author`, `Publication`) son sustitutos válidos de `BaseEntity`

### I - Interface Segregation Principle
- DTOs específicos para cada caso de uso
- Repositories con métodos específicos

### D - Dependency Inversion Principle
- Inyección de dependencias mediante Spring
- Servicios dependen de abstracciones (interfaces), no implementaciones concretas

## 🎨 Patrones de Diseño Utilizados

### 1. **Repository Pattern**
Abstrae el acceso a datos mediante `JpaRepository`.

**Ubicación**: 
- `authors-service/src/main/java/com/editorial/authors/repository/AuthorRepository.java`
- `publications-service/src/main/java/com/editorial/publications/repository/PublicationRepository.java`

### 2. **Adapter Pattern**
`AuthorServiceClient` adapta la interfaz del servicio de Autores para su uso en Publicaciones.

**Ubicación**: 
- `publications-service/src/main/java/com/editorial/publications/client/AuthorServiceClient.java`

### 3. **Strategy Pattern**
`PublicationStatusValidator` implementa diferentes estrategias de validación según el estado.

**Ubicación**: 
- `publications-service/src/main/java/com/editorial/publications/service/PublicationStatusValidator.java`

### 4. **Mapper/DTO Pattern**
Convierte entre entidades JPA y DTOs de API.

**Ubicación**: 
- `authors-service/src/main/java/com/editorial/authors/service/AuthorMapper.java`
- `publications-service/src/main/java/com/editorial/publications/service/PublicationMapper.java`

## 🗂️ Estructura de Carpetas

```
microservicios-project/
├── authors-service/
│   ├── src/main/java/com/editorial/authors/
│   │   ├── AuthorsServiceApplication.java
│   │   ├── controller/          # Controladores REST
│   │   ├── service/             # Lógica de negocio
│   │   ├── repository/          # Acceso a datos
│   │   ├── entity/              # Entidades JPA
│   │   ├── dto/                 # Data Transfer Objects
│   │   └── exception/           # Excepciones personalizadas
│   ├── src/main/resources/
│   │   └── application.yml      # Configuración de aplicación
│   ├── pom.xml                  # Dependencias Maven
│   └── Dockerfile
│
├── publications-service/
│   ├── src/main/java/com/editorial/publications/
│   │   ├── PublicationsServiceApplication.java
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── client/              # Cliente HTTP para otros servicios
│   │   ├── config/              # Configuraciones
│   │   └── exception/
│   ├── src/main/resources/
│   │   └── application.yml
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/
│   ├── src/
│   │   ├── components/          # Componentes React
│   │   ├── App.jsx              # Componente principal
│   │   ├── api.js               # Cliente API (Axios)
│   │   └── index.css
│   ├── public/
│   │   └── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── Dockerfile
│
├── docker-compose.yml           # Orquestación de servicios
├── .env                         # Variables de entorno
└── README.md                    # Este archivo
```

## 🔧 Desarrollo Local

### Construir Authors Service localmente

```bash
cd authors-service
mvn clean package
java -jar target/authors-service-1.0.0.jar
```

### Construir Publications Service localmente

```bash
cd publications-service
mvn clean package
java -jar target/publications-service-1.0.0.jar
```

### Ejecutar Frontend en modo desarrollo

```bash
cd frontend
npm install
npm run dev
```

## 📝 Ejemplo de Uso

### 1. Crear un Autor

```bash
curl -X POST http://localhost:8001/api/authors \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan@example.com",
    "organization": "Universidad Nacional",
    "biography": "Investigador en ciencias computacionales"
  }'
```

Respuesta:
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "organization": "Universidad Nacional",
  "biography": "Investigador en ciencias computacionales",
  "active": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### 2. Crear una Publicación

```bash
curl -X POST http://localhost:8002/api/publications \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Avances en Inteligencia Artificial",
    "content": "Este artículo explora los últimos avances...",
    "authorId": 1
  }'
```

### 3. Cambiar Estado de Publicación

```bash
curl -X PATCH "http://localhost:8002/api/publications/1/status?status=IN_REVIEW" \
  -H "Content-Type: application/json"
```

## 🐛 Troubleshooting

### Los servicios no inician

```bash
# Ver logs de un servicio específico
docker-compose logs authors-service
docker-compose logs publications-service

# Ver todos los logs
docker-compose logs -f
```

### Puertos ya están en uso

Cambiar en `docker-compose.yml` los puertos expuestos:
```yaml
ports:
  - "8001:8001"  # Cambiar primer puerto
```

### Base de datos no se inicializa

```bash
# Limpiar volúmenes
docker-compose down -v
docker-compose up
```

## 🧪 Testing

### Ejecutar tests unitarios

```bash
cd authors-service
mvn test

cd ../publications-service
mvn test
```

## 📊 Diagrama UML de Clases

```
┌─────────────────────┐
│   BaseEntity        │  (Abstract)
│─────────────────────│
│ - id: Long          │
│ - createdAt: LocalDateTime │
│ - updatedAt: LocalDateTime │
└──────────┬──────────┘
           △
           │
    ┌──────┴──────┐
    │             │
┌───┴────┐    ┌──┴─────┐
│ Author │    │Publication│
└────────┘    └──────────┘
```
