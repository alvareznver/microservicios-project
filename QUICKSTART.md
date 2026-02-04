# 🚀 Guía Rápida - Editorial Microservicios

## ⚡ Inicio Rápido (5 minutos)

### Paso 1: Verificar requisitos
```bash
# Verificar Docker
docker --version
docker-compose --version
```

### Paso 2: Navegar a la carpeta del proyecto
```bash
cd microservicios-project
```

### Paso 3: Iniciar los servicios
```bash
# Opción A: En primer plano (ver logs en vivo)
docker-compose up

# Opción B: En background
docker-compose up -d
```

### Paso 4: Esperar que todo esté listo
```bash
# Verificar estado
docker-compose ps

# Debería ver "Up (healthy)" en todos los servicios
```

### Paso 5: Abrir en navegador
```
Aplicación: http://localhost:3000
```

---

## 📱 Usar la Aplicación

### Crear un Autor

1. Ir a pestaña "👥 Authors"
2. Click en "+ New Author"
3. Llenar formulario:
   - First Name: Juan
   - Last Name: Pérez
   - Email: juan@example.com
   - Organization: Universidad
   - Biography: (opcional)
4. Click "Create Author"

### Crear una Publicación

1. Ir a pestaña "📄 Publications"
2. Click en "+ New Publication"
3. Llenar formulario:
   - Title: Mi primer artículo
   - Author: (seleccionar autor creado)
   - Content: Contenido del artículo
4. Click "Create Publication"

### Cambiar Estado de Publicación

1. En pestaña Publications, buscar la publicación
2. Click en botón acción (Send to Review, Approve, Publish, etc.)
3. Confirmar en modal que aparece

---

## 🛑 Detener los Servicios

```bash
# Detener sin eliminar datos
docker-compose stop

# Detener y eliminar contenedores (datos persisten)
docker-compose down

# Detener y eliminar TODO (incluido datos)
docker-compose down -v
```

---

## 🔍 Ver Logs

```bash
# Todos los logs
docker-compose logs -f

# Logs específicos
docker-compose logs -f authors-service
docker-compose logs -f publications-service
docker-compose logs -f frontend

# Últimas 100 líneas
docker-compose logs --tail=100
```

---

## 🧪 Probar APIs Directamente

### Authors Service

```bash
# Listar autores
curl http://localhost:8001/api/authors

# Obtener autor específico
curl http://localhost:8001/api/authors/1

# Crear autor
curl -X POST http://localhost:8001/api/authors \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ana","lastName":"García","email":"ana@example.com"}'
```

### Publications Service

```bash
# Listar publicaciones
curl http://localhost:8002/api/publications

# Obtener publicación
curl http://localhost:8002/api/publications/1

# Crear publicación
curl -X POST http://localhost:8002/api/publications \
  -H "Content-Type: application/json" \
  -d '{"title":"Artículo","content":"Contenido","authorId":1}'

# Cambiar estado
curl -X PATCH "http://localhost:8002/api/publications/1/status?status=IN_REVIEW"
```

---

## 📊 Puertos de Servicios

```
Frontend:           http://localhost:3000
Authors API:        http://localhost:8001/api
Publications API:   http://localhost:8002/api
Authors DB:         localhost:5431 (PostgreSQL)
Publications DB:    localhost:5432 (PostgreSQL)
```

---

## 🐛 Problemas Comunes

### "Puerto ya en uso"
```bash
# Ver qué proceso ocupa puerto
lsof -i :3000
lsof -i :8001
lsof -i :8002

# Cambiar puerto en docker-compose.yml
# ports:
#   - "8001:8001"  ← cambiar primer número
```

### "Servicio no responde"
```bash
# Revisar logs
docker-compose logs authors-service

# Esperar a que esté healthy (puede tomar 30-40 segundos)
docker-compose ps

# Si sigue fallando, reiniciar
docker-compose restart authors-service
```

### "Base de datos no inicia"
```bash
# Limpiar volúmenes
docker-compose down -v

# Reconstruir desde cero
docker-compose build --no-cache
docker-compose up
```

---

## 📚 Documentación Adicional

- **README.md**: Documentación completa
- **DESIGN_PATTERNS.md**: Patrones implementados
- **BPMN_DOCUMENTATION.md**: Proceso BPMN del sistema
- **docker-compose.yml**: Configuración de servicios

---

## 💡 Tips Útiles

### Limpiar todo
```bash
docker-compose down -v  # Elimina contenedores, networks, volúmenes
```

### Reconstruir imágenes
```bash
docker-compose build --no-cache
```

### Acceder a shell de BD
```bash
# Authors DB
docker exec -it db-authors psql -U postgres -d authors_db

# Publications DB  
docker exec -it db-publications psql -U postgres -d publications_db
```

### Variables de ambiente
Editar `.env` para cambiar valores como:
- `DB_USER`: Usuario BD
- `DB_PASSWORD`: Contraseña BD
- Puertos de servicios
- URLs de APIs

---

**Última actualización**: Enero 2024  
**Versión**: 1.0.0
