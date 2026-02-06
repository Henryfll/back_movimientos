# 🚀 Microservicio de Movimientos - Guía de Despliegue Docker

## 📋 Tabla de Contenidos

- [Prerrequisitos](#prerrequisitos)
- [Arquitectura del Proyecto](#arquitectura-del-proyecto)
- [Variables de Entorno](#variables-de-entorno)
- [Configuración de Docker](#configuración-de-docker)
- [Guía de Inicio Rápido](#guía-de-inicio-rápido)
- [Verificación de Servicios](#verificación-de-servicios)
- [Acceso a la Aplicación](#acceso-a-la-aplicación)

---

## 📦 Prerrequisitos

### Software Requerido

| Software | Versión Mínima | Versión Recomendada |
|----------|-----------------|---------------------|
| Docker | 20.10+ | 24.0+ |
| Docker Compose | 2.0+ | 2.20+ |
| Java | 21 | 21 LTS |
| Git | 2.0+ | Latest |

### Recursos del Sistema

```
┌─────────────────────────────────────────────────────────────┐
│  RECURSOS MÍNIMOS REQUERIDOS                                │
├─────────────────────────────────────────────────────────────┤
│  • RAM: 4 GB (2 GB para Docker + 2 GB para app)            │
│  • CPU: 2 núcleos                                           │
│  • Disco: 2 GB libres                                       │
├─────────────────────────────────────────────────────────────┤
│  RECURSOS RECOMENDADOS                                     │
├─────────────────────────────────────────────────────────────┤
│  • RAM: 8 GB                                                │
│  • CPU: 4 núcleos                                           │
│  • Disco: 5 GB libres                                       │
└─────────────────────────────────────────────────────────────┘
```

### Verificar Instalación

```bash
# Verificar Docker
docker --version
# Ejemplo de salida: Docker version 24.0.6, build ed223bc

# Verificar Docker Compose
docker compose version
# Ejemplo de salida: Docker Compose version v2.21.0

# Verificar recursos disponibles
docker system info | grep -E "Total Memory|CPU"
```

---

## 🏗️ Arquitectura del Proyecto

```
back_movimientos/
├── 📁 src/main/
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
├── 📁 gradle/
│   └── wrapper/            # Gradle Wrapper
├── 📄 Dockerfile           # Definición de imagen Docker
├── 📄 docker-compose.yml   # Orquestación de servicios
├── 📄 build.gradle        # Configuración Gradle
└── 📄 README.md           # Este archivo
```

---

## 🔐 Variables de Entorno

### Valores por Defecto (Desarrollo)

| Variable | Valor por Defecto | Descripción |
|----------|-------------------|-------------|
| `DB_URL` | `jdbc:postgresql://postgres:5432/movimientos` | URL de conexión a PostgreSQL |
| `DB_USERNAME` | `postgres` | Usuario de la base de datos |
| `DB_PASSWORD` | `postgres` | Contraseña de la base de datos |
| `JPA_DDL_AUTO` | `update` | Estrategia de Hibernate (update/validate/none) |
| `SHOW_SQL` | `false` | Mostrar SQL en logs (true/false) |
| `BCRYPT_STRENGTH` | `10` | Factor de trabajo para BCrypt |
| `SPRING_PROFILES_ACTIVE` | `default` | Perfil de Spring activo |

### ⚠️ Configuración de Producción

> **ADVERTENCIA:** En producción, NUNCA uses los valores por defecto. Usa secrets o archivos `.env`.

```bash
# Ejemplo de archivo .env.production
DB_URL=jdbc:postgresql://db.example.com:5432/movimientos
DB_USERNAME=movimientos_user
DB_PASSWORD=contraseña_segura
JPA_DDL_AUTO=validate
SHOW_SQL=false
SPRING_PROFILES_ACTIVE=production
BCRYPT_STRENGTH=12
```

---

## 🐳 Configuración de Docker

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

## ⚡ Guía de Inicio Rápido

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

## ✅ Verificación de Servicios

### Estado de Contenedores

```bash
# Ver todos los contenedores
docker-compose ps

# Salida esperada:
# NAME                  IMAGE               STATUS              PORTS
# movimientos-app       app:latest          Up                  0.0.0.0:8080->8080/tcp
# movimientos-db        postgres:15-alpine  Up                  0.0.0.0:5432->5432/tcp
```

### Verificar Conexión a Base de Datos

```bash
# Conectar a PostgreSQL
docker exec -it movimientos-db psql -U postgres -d movimientos

# Ver tablas
\dt

# Salir
\q
```

### Verificar Salud de la App

```bash
# Health check básico
curl -s http://localhost:8080/movimientos/actuator/health

# Ver respuesta JSON esperada:
# {"status":"UP"}
```

---

## 🌐 Acceso a la Aplicación

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
