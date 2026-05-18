# Backend Despachos — Innovatech Chile

API REST de gestión de despachos desarrollada con Spring Boot 3 + MySQL, contenedorizada con Docker y desplegada en AWS EC2 mediante CI/CD con GitHub Actions.

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 + Spring Boot 3 | Framework del backend |
| Spring Data JPA + Hibernate | Acceso a base de datos |
| MySQL 8.0 | Base de datos relacional |
| Docker (multi-stage) | Maven build → JRE producción |
| Docker Compose | Orquestación backend + MySQL |
| GitHub Actions | Pipeline CI/CD automático |
| AWS EC2 | Servidor de producción |
| Swagger / OpenAPI | Documentación del API |

## Endpoints del API

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/despachos` | Listar todos los despachos |
| GET | `/despachos/{id}` | Obtener despacho por ID |
| POST | `/despachos` | Crear nuevo despacho |
| PUT | `/despachos/{id}` | Actualizar despacho |
| DELETE | `/despachos/{id}` | Eliminar despacho |
| GET | `/despachos/health` | Verificar estado del servicio |
| GET | `/swagger-ui.html` | Documentación interactiva |

## Estructura del proyecto

```
backend-despachos/
├── src/main/java/com/innovatech/despachos/
│   ├── DespachosApplication.java    # Clase principal Spring Boot
│   ├── controller/
│   │   └── DespachoController.java  # Endpoints REST
│   ├── service/
│   │   └── DespachoService.java     # Lógica de negocio
│   ├── repository/
│   │   └── DespachoRepository.java  # Acceso a BD con JPA
│   └── model/
│       └── Despacho.java            # Entidad JPA
├── src/main/resources/
│   └── application.properties       # Configuración (sin credenciales)
├── Dockerfile                       # Multi-stage: Maven → JRE
├── docker-compose.yml               # Backend + MySQL + named volume
├── .env.example                     # Plantilla de variables de entorno
├── .dockerignore
└── .github/
    └── workflows/
        └── deploy.yml               # Pipeline CI/CD GitHub Actions
```

## Desarrollo local

```bash
# 1. Copiar variables de entorno
cp .env.example .env
# Editar .env con tus valores locales

# 2. Levantar con Docker Compose (backend + MySQL)
docker-compose --env-file .env up --build

# La API estará disponible en:
# → http://localhost:8081/despachos
# → http://localhost:8081/swagger-ui.html
```

## Variables de entorno

| Variable | Descripción | Ejemplo |
|---|---|---|
| `DB_NAME` | Nombre de la base de datos | `despachos_db` |
| `DB_USERNAME` | Usuario MySQL | `devops_user` |
| `DB_PASSWORD` | Contraseña MySQL | `MiContrasena123!` |

## Verificar funcionamiento

```bash
# Healthcheck
curl http://localhost:8081/despachos/health

# Listar despachos
curl http://localhost:8081/despachos

# Crear despacho
curl -X POST http://localhost:8081/despachos \
  -H "Content-Type: application/json" \
  -d '{"cliente":"Juan Pérez","producto":"Laptop","direccion":"Av. Providencia 1234"}'

# Ver volumen (persistencia)
docker volume ls
docker volume inspect despachos_db_data
```

## Pipeline CI/CD

El pipeline se activa automáticamente al hacer `push` a la rama `deploy`:

1. **Java setup** → Configura JDK 21 con cache de Maven
2. **Tests** → Ejecuta `mvn clean verify` (tests unitarios)
3. **Login Docker Hub** → Autenticación con secrets
4. **Build & Push** → Imagen multi-stage a Docker Hub (tags: `latest` + SHA)
5. **SSH Deploy** → Se conecta a EC2 y levanta con docker-compose

## Secrets de GitHub requeridos

| Secret | Descripción |
|---|---|
| `DOCKERHUB_USERNAME` | Usuario de Docker Hub |
| `DOCKERHUB_TOKEN` | Token de acceso de Docker Hub |
| `EC2_BACKEND_HOST` | IP de la instancia EC2 |
| `EC2_USER` | Usuario SSH (`ec2-user`) |
| `EC2_SSH_KEY` | Contenido del archivo `.pem` |
| `DB_NAME` | Nombre de la base de datos |
| `DB_USERNAME` | Usuario MySQL |
| `DB_PASSWORD` | Contraseña MySQL |

## Decisiones técnicas

**¿Por qué multi-stage build?**
La imagen final no incluye Maven ni el JDK completo, solo el JRE. Resultado: ~200MB en vez de ~600MB.

**¿Por qué named volume para MySQL?**
Los datos persisten independientemente del ciclo de vida del contenedor. Si MySQL falla y se reinicia, o si se hace un redeploy, los datos no se pierden.

**¿Por qué `depends_on` con `condition: service_healthy`?**
Spring Boot intentaría conectarse a MySQL antes de que esté listo, causando errores. El healthcheck garantiza que MySQL acepta conexiones antes de iniciar el backend.

**¿Por qué `DB_ENDPOINT: db-despachos` en vez de una IP?**
Docker tiene DNS interno que resuelve el nombre del servicio a la IP del contenedor. Las IPs de contenedores pueden cambiar; los nombres de servicio no.

---

*ISY1101 — Introducción a Herramientas DevOps | EP2 | Innovatech Chile*
