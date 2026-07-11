# Spring Modulith Kickstart

## Welcome

This repository is a preconfigured **Spring Modulith** project with a movie review platform as the domain example.  
[Spring Modulith](https://spring.io/projects/spring-modulith) is a Spring Boot extension that helps structure monolithic applications into logical modules, enforce module boundaries at compile time, and optionally expose module insights at runtime – a pragmatic middle ground between a big ball of mud and microservices.

The app is split into three modules: `user`, `movie`, and `review`. Authentication is handled via JWT tokens.

## Architecture

### Philosophy

This project follows a **Domain-Driven Design (DDD)-inspired architecture**, implemented with **Spring Modulith**.  
The goal: a monolith with clean, enforceable module boundaries – without the operational complexity of microservices.

### Module Structure

Modules reflect the business domains:

```
com.example.springmodulithkickstart
├── user/      @ApplicationModule(type = OPEN)
├── movie/     (closed – no @ApplicationModule)
├── review/    (closed – no @ApplicationModule)
└── shared/    @ApplicationModule(type = OPEN)
```

Each module follows a consistent **Hexagonal Architecture** layout:

```
<module>/
  ├── api/               # REST controllers (Presentation Layer)
  │   ├── dto/           # Request / Response DTOs
  ├── domain/            # Business logic interfaces + enums
  └── infrastructure/    # Implementation details
      ├── db/            # JPA entities
      ├── mapper/        # Entity-to-DTO mapping (optional)
      └── ...
```

- **`user`** and **`shared`** are marked `@ApplicationModule(type = OPEN)` because other modules need to import them (JwtService and Shared Kernel respectively).
- **`movie`** and **`review`** are closed by default – direct imports between them are not allowed.
- Communication between closed modules happens exclusively through **Domain Events** (see below).

### Hexagonal Architecture per Module

Each module is structured inside-out:

| Layer | Contains | Depends on |
|---|---|---|
| `api/` | Controllers, DTOs | → `domain/` (interfaces) |
| `domain/` | Service interfaces, domain enums, events | nothing (pure business logic) |
| `infrastructure/` | JPA entities, service implementations, mappers | → `domain/` |

Controllers only know the service interfaces from `domain/`, never the implementation. This enables swappability and testable architecture.

### Domain Events – Loose Coupling Between Modules

The central architectural principle: **modules communicate via events**, not direct method calls.

```kotlin
// movie → published
MovieServiceImpl.publishEvent(MovieCreatedEvent(movieId, title))

// review → consumed
@ApplicationModuleListener
ReviewServiceImpl.onMovieCreated(event: MovieCreatedEvent)
```

The `MovieCreatedEvent` lives in the `shared` module (Shared Kernel).  
`movie` publishes it via Spring `ApplicationEventPublisher`, `review` consumes it with Spring Modulith's `@ApplicationModuleListener`.  
This avoids a direct dependency between `movie` and `review`.

```
┌──────────┐  MovieCreatedEvent  ┌──────────┐
│  movie   │ ──────────────────> │  review  │
│ (closed) │      (via shared)   │ (closed) │
└──────────┘                     └──────────┘
```

### Dependency Graph

```
user ──> shared  (JwtService interface for security filter)
movie ──> shared (MovieCreatedEvent)
review ──> shared (MovieCreatedEvent)
review ──> movie (implicitly via movieId foreign key – data-only)
```

Spring Modulith enforces module boundaries at **compile time** and exposes them at **runtime** via the Actuator endpoint `/admin/actuator/modulith` as JSON.

### Libraries

| Concern | Library | Rationale |
|---|---|---|
| **Module Architecture** | [Spring Modulith](https://spring.io/projects/spring-modulith) 2.1.0 | Enforces module boundaries, provides runtime insights, supports application events |
| **Build Tool** | Maven | Spring ecosystem standard, excellent module test integration |
| **Language** | Kotlin 2.3.21 | Type-safe, expressive, null-safe – ideal for DDD entities |
| **REST API** | Spring Web (Servlet stack) | Proven, wide community, simple controllers |
| **Database (dev)** | H2 (in-memory) | Lightweight, no external service needed |
| **Database (prod)** | PostgreSQL 17 (via Docker) | Production standard, robust, open source |
| **JPA / ORM** | Spring Data JPA + Hibernate | Standard ORM, `@Entity`, `@CreationTimestamp`, etc. |
| **Migrations** | Flyway (prod only) | Versioned SQL migrations, declarative and safe |
| **Security** | Spring Security + JJWT (0.11.5) | JWT-based authentication, role-based (USER / ADMIN) |
| **API Documentation** | SpringDoc OpenAPI 2.8.6 | Swagger UI at `/admin/swagger/`, OpenAPI JSON at `/admin/api-docs` |
| **Caching (dev)** | Spring Cache (ConcurrentMapCacheManager) | Simple, no infrastructure needed |
| **Caching (prod)** | Spring Cache + Redis 7 | Distributed cache, ideal for production |
| **Testing** | MockK (instead of Mockito) | Kotlin-native mocking library, compatible with Spring Boot |
| **Linting** | Spotless Maven Plugin | Enforced code style, automatic via `mvn spotless:apply` |
| **Deployment** | Docker + Docker Compose | Self-contained containers for app, PostgreSQL, and Redis |

### Profiles

Spring profiles switch between development and production:

| Aspect | `dev` (default) | `prod` |
|---|---|---|
| Database | H2 in-memory | PostgreSQL |
| Migrations | `data.sql` (disabled) | Flyway (V1__init.sql) |
| Caching | ConcurrentMap | Redis |
| Activation | automatic (default) | `SPRING_PROFILES_ACTIVE=prod`

| Field    | Value             |
|----------|-------------------|
| Email    | admin@admin.com   |
| Password | admin             |

The admin account is automatically created on startup via `AdminInitializer`.

## Role System

The application uses a simple role system with two roles:

- **USER** — default role for newly registered users
- **ADMIN** — has access to protected endpoints (e.g. Swagger, Actuator)

The role is stored as an enum string in the `users` table and included as a `role` claim in the JWT token.

## Commands

### Install maven
```bash
brew install maven
```

### Install the dependencies
```bash
mvn clean install
```

### Start the app
```bash
mvn spring-boot:run
```

### Run Flyway migrations manually
```bash
mvn flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/mydb -Dflyway.user=postgres -Dflyway.password=your_password
```

## API Endpoints

### User Module (`/auth`)

Registration and authentication are handled by `AuthenticationController`. Both endpoints are publicly accessible.

- **POST `/auth/signup`** -- Creates a new user account with role `USER`. Expects a JSON body with `email`, `password`, and `fullName`. Returns a JWT token.

- **POST `/auth/login`** -- Authenticates an existing user. Expects a JSON body with `email` and `password`. Returns a JWT token.

### Movie Module (`/movie`)

Movie management is handled by `MovieController`. Both endpoints require a valid JWT in the `Authorization` header.

- **POST `/movie/new`** -- Registers a new movie. Expects a JSON body with `title` and `description`. Returns a confirmation string.

- **GET `/movie/all`** -- Retrieves all registered movies. Returns a list of `MovieDTO` objects, each containing `title` and `description`.

### Review Module

A `ReviewController` stub exists but currently exposes no endpoints. This module is a placeholder for future review functionality.

### Actuator & Swagger (Admin only, under `/admin/`)

These endpoints are grouped under the **`/admin/`** path and require an `ADMIN` JWT:

| Path | Description |
|------|-------------|
| `GET /admin/actuator/health` | Health check |
| `GET /admin/actuator/info` | App info |
| `GET /admin/actuator/modulith` | Modulith module structure |
| `GET /admin/swagger/*` | Swagger UI |
| `GET /admin/api-docs` | OpenAPI JSON specification |

### Register (new user)
```bash
curl -X POST 'localhost:8080/auth/signup' \
  --header 'Content-Type: application/json' \
  --data '{
  "email" : "max.mustermann@gmail.com",
  "password" : "test",
  "fullName" : "Max Mustermann"
}'
```

### Login (user)
```bash
curl -X POST 'localhost:8080/auth/login' \
  --header 'Content-Type: application/json' \
  --data '{
  "email" : "max.mustermann@gmail.com",
  "password" : "test"
}'
```

### Login (admin)
```bash
curl -X POST 'localhost:8080/auth/login' \
  --header 'Content-Type: application/json' \
  --data '{
  "email" : "admin@admin.com",
  "password" : "admin"
}'
```

### Create Movie
```bash
curl -X POST 'localhost:8080/movie/new' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: Bearer <JWT_TOKEN>' \
  --data '{
  "title" : "Inception",
  "description" : "A thief who steals corporate secrets through dream-sharing technology."
}'
```

### Get All Movies
```bash
curl -X GET 'localhost:8080/movie/all' \
  --header 'Authorization: Bearer <JWT_TOKEN>'
```

### Swagger UI
```bash
# Requires admin JWT — open in browser or use curl:
curl -X GET 'localhost:8080/admin/swagger/index.html' \
  --header 'Authorization: Bearer <ADMIN_JWT>'
```

### OpenAPI Docs
```bash
curl -X GET 'localhost:8080/admin/api-docs' \
  --header 'Authorization: Bearer <ADMIN_JWT>'
```

### Actuator Health
```bash
curl -X GET 'localhost:8080/admin/actuator/health' \
  --header 'Authorization: Bearer <ADMIN_JWT>'
```

### Actuator Modulith
```bash
curl -X GET 'localhost:8080/admin/actuator/modulith' \
  --header 'Authorization: Bearer <ADMIN_JWT>'
```

This endpoint returns the module structure including module and aggregate mapping as JSON.

## Deployment

The pipeline (`.github/workflows/deploy.yml`) automatically builds and deploys to your VPS on every push to `main` or `production`.

### 1. SSH Key

Generate a key pair on your local machine (if you don't have one yet):

```bash
ssh-keygen -t rsa -b 4096 -f ~/.ssh/vps_deploy
```

Copy the public key to your VPS:

```bash
ssh-copy-id -i ~/.ssh/vps_deploy.pub ubuntu@<VPS_IP>
```

Test the connection:
```bash
ssh -i ~/.ssh/vps_deploy ubuntu@<VPS_IP>
```

The **private key** (`~/.ssh/vps_deploy`) will be stored as `VPS_SSH_KEY` in GitHub Secrets.

### 2. Initial VPS Setup (once)

SSH into your VPS and run:

```bash
# Allow port 8080
sudo ufw allow 22/tcp
sudo ufw allow 8080/tcp
sudo ufw --force enable

# Install Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker ubuntu

# Create app directory
mkdir -p /home/ubuntu/app
```

Then **log out and back in** (to pick up the `docker` group).

No Java or manual container setup needed – everything runs in Docker containers via Docker Compose.

### 3. GitHub Secrets

| Secret | Value |
|--------|-------|
| `VPS_HOST` | Public IP of your VPS |
| `VPS_PORT` | `22` |
| `VPS_USER` | `ubuntu` |
| `VPS_SSH_KEY` | Content of the **private** key file (e.g. `~/.ssh/vps_deploy`) |
| `DB_PASSWORD` | PostgreSQL password (used for both the DB container and the app) |

### 4. Done

Push to `main` – the pipeline runs `spotless:check`, builds the JAR, packages it with `Dockerfile` and `docker-compose.yml`, copies the archive to your VPS, and runs `docker compose up --build -d`.  
PostgreSQL data persists across deploys via a Docker volume (`postgres_data`).

### Useful VPS Commands

```bash
# View app logs
ssh ubuntu@<VPS_IP> "docker compose -f /home/ubuntu/app/docker-compose.yml logs -f"

# Restart app manually
ssh ubuntu@<VPS_IP> "cd /home/ubuntu/app && docker compose restart app"

# Stop everything
ssh ubuntu@<VPS_IP> "cd /home/ubuntu/app && docker compose down"

# Check container status
ssh ubuntu@<VPS_IP> "docker ps"
```

## Testing

All endpoints were tested with [Yaak](https://yaak.app/).  
Pre-built Yaak templates for testing the APIs can be found in the `/yaak` folder.

## Contributing

This is **my** personal Spring Boot preconfiguration.  
If you see a useful tool or architecture improvement that fits in here – **feel free to open an issue or submit a PR**!

## Linting
```bash
mvn spotless:check
```

```bash
mvn spotless:apply
```

## Sources
https://medium.com/@tericcabrel/implement-jwt-authentication-in-a-spring-boot-3-application-5839e4fd8fac