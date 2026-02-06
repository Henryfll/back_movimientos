# Microservicio de Movimientos - Guía de Despliegue Docker

## Tabla de Contenidos

- [Prerrequisitos](#prerrequisitos)
- [Arquitectura del Proyecto](#arquitectura-del-proyecto)
- [Guía de Inicio Rápido](#guía-de-inicio-rápido)
- [Acceso a la Aplicación](#acceso-a-la-aplicación)

---

##  Prerrequisitos

### Software Requerido

| Software | Versión Mínima | Versión Recomendada |
|----------|-----------------|---------------------|
| Docker | 20.10+ | 24.0+ |
| Docker Compose | 2.0+ | 2.20+ |
| Java | 21 | 21 LTS |


## Arquitectura del Proyecto

```
back_movimientos/
├── src/main/
│   ├── java/com/ms/movimientos/
│   │   ├── controller/     # Controladores REST
│   │   ├── service/         # Lógica de negocio
│   │   ├── repository/      # Acceso a datos
│   │   ├── entity/          # Entidades JPA
│   │   ├── dto/            # Objetos de transferencia
│   │   ├── config/         # Configuraciones
│   │   └── exception/      # Manejo de excepciones
│   └── resources/
│       └── application.yaml # Configuración principal
├── gradle/
│   └── wrapper/            # Gradle Wrapper
├── Dockerfile           # Definición de imagen Docker
├── docker-compose.yml   # Orquestación de servicios
├── build.gradle        # Configuración Gradle
└── README.md           # Este archivo
```

---

## Configuración de Docker

### Dockerfile

El proyecto utiliza construcción **multi-etapas** para optimizar el tamaño de la imagen:

```dockerfile
# Etapa 1: Builder (con JDK)
FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

# Etapa 2: Runtime (con JRE, más pequeña)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
version: '3.8'

services:
  # Base de datos PostgreSQL
  postgres:
    image: postgres:15-alpine
    container_name: movimientos-db
    environment:
      POSTGRES_DB: movimientos
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - movimientos-network

  # Aplicación Spring Boot
  app:
    build: .
    container_name: movimientos-app
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/movimientos
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    networks:
      - movimientos-network

networks:
  movimientos-network:
    driver: bridge

volumes:
  postgres_data:
```

---

##  Guía de Inicio Rápido

### Paso 1: Clonar el Repositorio

```bash
# Clonar el repositorio (si aplica)
git clone https://github.com/tu-repo/back_movimientos.git
cd back_movimientos

# O si ya tienes el proyecto localmente
cd /ruta/a/back_movimientos
```

### Paso 2: Verificar Estructura

```bash
# Verificar archivos necesarios
ls -la | grep -E "Dockerfile|docker-compose.yml|build.gradle"

# Verificar estructura de directorios
tree -L 3 -I 'build|.gradle|.git' .
```

### Paso 3: Construir Imágenes

```bash
# Construir imágenes Docker
docker-compose build

# Ver imágenes construidas
docker images | grep -E "postgres|movimientos"
```

### Paso 4: Iniciar Contenedores

```bash
# Opción 1: En primer plano (ver logs en tiempo real)
docker-compose up

# Opción 2: En segundo plano (recomendado)
docker-compose up -d

# Opción 3: Con rebuild completo
docker-compose up --build -d
```

### Paso 5: Verificar Inicio

```bash
# Ver estado de contenedores
docker-compose ps

# Verificar que la app responde
curl http://localhost:8080/movimientos/actuator/health
```

---


##  Acceso a la Aplicación

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **App Principal** | `http://localhost:8080/movimientos` | API REST principal |
| **Swagger UI** | `http://localhost:8080/movimientos/swagger-ui.html` | Documentación interactiva |
| **OpenAPI Docs** | `http://localhost:8080/movimientos/v3/api-docs` | Especificación OpenAPI JSON |
| **PostgreSQL** | `localhost:5432` | Puerto de base de datos |

### Endpoints Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/movimientos/api/clientes` | Listar clientes |
| GET | `/movimientos/api/cuentas` | Listar cuentas |
| GET | `/movimientos/api/movimientos` | Listar movimientos |
| GET | `/movimientos/api/reportes` | Generar reportes |
| POST | `/movimientos/api/clientes` | Crear cliente |
| POST | `/movimientos/api/cuentas` | Crear cuenta |
| POST | `/movimientos/api/movimientos` | Registrar movimiento |

---


**Última actualización:** Febrero 2025
