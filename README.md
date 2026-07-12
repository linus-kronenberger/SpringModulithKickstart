# Spring Modulith Kickstart

This repository is a preconfigured **Spring Modulith** (using [Spring Boot Kotlin](https://spring.io/guides/tutorials/spring-boot-kotlin) and maven) project with a movie review platform as the domain example.
The app is split into three modules: `user`, `movie`, and `review`. Authentication is handled via JWT tokens.

## Architecture

This project follows a **Domain-Driven Design (DDD)-inspired architecture**, implemented with **Spring Modulith**.  
The goal: a monolith with clean, enforceable module boundaries – without the complexity of microservices.

### Module Structure

Modules reflect the business domains:

```
com.example.springmodulithkickstart
├── user/      @ApplicationModule(type = CLOSED)
├── movie/     @ApplicationModule(type = CLOSED)
├── review/    @ApplicationModule(type = CLOSED)
└── shared/    @ApplicationModule(type = OPEN)
```
### Hexagonal Architecture per Module

Each module is structured inside-out:

| Layer | Contains |
|---|---|
| `api/` | Controllers, DTOs |
| `domain/` | Service interfaces, domain enums, events |
| `infrastructure/` | JPA entities, service implementations, mappers |

### Domain Events 

**Modules communicate via events**, not direct method calls!

```kotlin
// movie → published
MovieServiceImpl.publishEvent(MovieCreatedEvent(movieId, title))

// review → consumed
@ApplicationModuleListener
ReviewServiceImpl.onMovieCreated(event: MovieCreatedEvent)
```

Via the ApplicationEventPublisher, the MovieCreatedEvent informs the ReviewModule that a new film has been added, triggering the creation of a review for every user. This serves as an example for the event communication.

### Libraries

**Web & API**
- **Spring Web** (Servlet stack) – REST API
- **SpringDoc OpenAPI 2.8.6** – Swagger UI at `/admin/swagger/`, OpenAPI JSON at `/admin/api-docs`

**Database & Persistence**
- **H2** (in-memory) – Development database
- **PostgreSQL 17** (via Docker) – Production database
- **Spring Data JPA + Hibernate** – ORM layer
- **Flyway** (prod only) – Versioned SQL migrations

**Security**
- **Spring Security + JJWT 0.11.5** – JWT-based authentication with role-based access (USER / ADMIN)

**Caching**
- **ConcurrentMapCacheManager** (dev) – Simple in-memory cache
- **Redis 7** (prod) – Distributed cache via Docker

**Testing & Quality**
- **MockK** – Kotlin-native mocking (replaces Mockito)
- **Spotless Maven Plugin** – Code style enforcement (`mvn spotless:apply` / `mvn spotless:check`)

**Architecture & Deployment**
- **Spring Modulith** – Module boundary enforcement, runtime insights via Actuator
- **Docker + Docker Compose** – Containerized deployment of app, PostgreSQL, and Redis

### Profiles

Spring profiles switch between development and production:

| Aspect | `dev` (default) | `prod` |
|---|---|---|
| Database | H2 in-memory | PostgreSQL |
| Caching | ConcurrentMap | Redis |

## Generated Admin Account

| Field    | Value             |
|----------|-------------------|
| Email    | admin@admin.com   |
| Password | admin             |

The admin account is automatically created on startup via `AdminInitializer`. You can change the default values by adjusting the startup environment variables.

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

The pipeline (`.github/workflows/deploy.yml`) automatically builds and deploys to your VPS on every push to `main`.

### 1. VPS Setup

Log in as `root` via SSH or the hoster's console and run:

```bash
# Create deploy user
adduser deploy
usermod -aG sudo deploy

# Allow port 8080
ufw allow 22/tcp
ufw allow 8080/tcp
ufw --force enable

# Install Docker
curl -fsSL https://get.docker.com | sh
usermod -aG docker deploy

# Create app directory
mkdir -p /home/deploy/app
chown deploy:deploy /home/deploy/app
```

### 2. SSH Key Set-Up

Generate a key pair on your local machine (if you don't have one yet):

```bash
ssh-keygen -t rsa -b 4096 -f ~/.ssh/vps_deploy
```

This creates `~/.ssh/vps_deploy` (private key) and `~/.ssh/vps_deploy.pub` (public key).  
The **private key** will later be stored as `VPS_SSH_KEY` in GitHub Secrets.

### 3. Install the Public Key for `deploy`

From your local machine:

```bash
sudo ssh-copy-id -i ~/.ssh/vps_deploy.pub deploy@<VPS_IP>
```

If that doesn't work (e.g. `deploy` user has no password), log in as `root` and manually add the key:

```bash
ssh root@<VPS_IP>
mkdir -p /home/deploy/.ssh
echo "<content of ~/.ssh/vps_deploy.pub>" >> /home/deploy/.ssh/authorized_keys
chmod 700 /home/deploy/.ssh
chmod 600 /home/deploy/.ssh/authorized_keys
chown -R deploy:deploy /home/deploy/.ssh
exit
```

### 4. Test the Connection

```bash
ssh -i ~/.ssh/vps_deploy deploy@<VPS_IP>
```

Successful login → you're ready.

### 5. GitHub Secrets

Navigate to **GitHub → Settings → Secrets and variables → Actions** and add the following secrets.  
All secrets are **required for production** — the defaults are only suitable for local development.

| Secret | Description |
|--------|-------------|
| `VPS_HOST` | Public IP of your VPS |
| `VPS_PORT` | SSH port (usually `22`) |
| `VPS_USER` | SSH user (e.g. `deploy`) |
| `VPS_SSH_KEY` | Content of the private SSH key (e.g. `~/.ssh/vps_deploy`) |
| `DB_USER` | PostgreSQL user |
| `DB_NAME` | PostgreSQL database name |
| `DB_PASSWORD` | PostgreSQL password |
| `ACTIVE_PROFILE` | Spring active profile (`prod`) |
| `ADMIN_FULL_NAME` | Admin full name |
| `ADMIN_MAIL` | Admin account email |
| `ADMIN_PASSWORD` | Admin account password |
| `JWT_SECRET_KEY` | Base64-encoded HMAC-SHA256 key for JWT signing |
| `JWT_EXPIRATION_TIME` | JWT token validity in milliseconds (e.g. `3600000` = 1 hour) |
| `REDIS_HOST` | Redis hostname (Docker service name: `redis`) |
| `REDIS_PORT` | Redis port (usually `6379`) |

### 4. Finish Deployment

Push to `main` – the pipeline runs `spotless:check`, builds the JAR, packages it with `Dockerfile` and `docker-compose.yml`, copies the archive to your VPS, and runs `docker compose up --build -d`.  
PostgreSQL data persists across deploys via a Docker volume (`postgres_data`).

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