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

## Generated Admin Account (in dev)

| Field    | Value             |
|----------|-------------------|
| Email    | admin@admin.com   |
| Password | admin             |

The admin account is automatically created on startup via `AdminInitializer`. You can change the default values by adjusting the startup environment variables.

## Role System

The application uses a simple role system with two roles:

- **USER** — default role for newly registered users
- **ADMIN** — has access to protected endpoints (e.g. Actuator)

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

### Review Module (`/review`)

Reviews are managed by `ReviewController`. Requires a valid JWT in the `Authorization` header.

- **POST `/review`** -- Creates a new review. Expects a JSON body with `movieId`, and optionally `reviewText` and `rating`. Uses the authenticated user as the reviewer. Returns a `ReviewResponseDto` with HTTP `201 Created`.

### Admin Endpoints (`/admin/`, ADMIN role required)

These endpoints are grouped under the **`/admin/`** path and require an `ADMIN` JWT:

| Path | Description |
|------|-------------|
| `GET /admin/actuator/health` | Health check |
| `GET /admin/actuator/info` | App info |
| `GET /admin/actuator/modulith` | Modulith module structure |

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

### Create Review
```bash
curl -X POST 'localhost:8080/review' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: Bearer <JWT_TOKEN>' \
  --data '{
  "movieId" : "<MOVIE_ID>",
  "reviewText" : "Great movie!",
  "rating" : 5
}'
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

Generate an Ed25519 key pair on your local machine:

```bash
ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/vps_deploy -N ""
```

This creates `~/.ssh/vps_deploy` (private key) and `~/.ssh/vps_deploy.pub` (public key).  
The **private key** will later be stored as `VPS_SSH_KEY` in GitHub Secrets.

### 3. Install the Public Key for `deploy`

From your local machine:

```bash
ssh-copy-id -i ~/.ssh/vps_deploy.pub deploy@<VPS_IP>
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

### 5. GitHub Environment & Secrets

The `deploy` job in the pipeline references an environment called `deploy environment`. Secrets must be stored there, not at the repo level.

#### 5.1 Create the Environment

1. **GitHub → Repo → Settings → Environments → New environment**
2. Name: `deploy environment`
3. **Save**

#### 5.2 Add Secrets

In the new environment, under **Environment secrets**, add the following:

Copy your private key to the clipboard:

```bash
cat ~/.ssh/vps_deploy | pbcopy
```

Then click **"Add secret"** and paste the value.

| Secret | Description |
|--------|-------------|
| `VPS_HOST` | Public IP of your VPS |
| `VPS_PORT` | SSH port (usually `22`) |
| `VPS_USER` | SSH user (e.g. `deploy`) |
| `VPS_SSH_KEY` | Full content of `~/.ssh/vps_deploy` (including `-----BEGIN OPENSSH PRIVATE KEY-----` / `-----END OPENSSH PRIVATE KEY-----`) |
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

### 6. Deployment

Push to `main` – the pipeline runs `spotless:check`, tests, builds the JAR, copies it with `Dockerfile` and `docker-compose.yml` to your VPS, and runs `docker compose up --build -d`.  
PostgreSQL data persists across deploys via a Docker volume (`postgres_data`).

## Testing

All endpoints were tested with [Yaak](https://yaak.app/).  
Pre-built Yaak templates for testing the APIs can be found in the `/yaak` folder.
On the vps, you can access the endpoints by following this path naming pattern: `<YOUR_VPS_IP>:8080/endpoint-path`

## Contributing

This is **my** personal Spring Boot preconfiguration.  
If you see a useful tool or architecture improvement that fits in here – **feel free to open an issue or submit a PR 🤔😃**!

## Linting
```bash
mvn spotless:check
```

```bash
mvn spotless:apply
```

## Sources
https://medium.com/@tericcabrel/implement-jwt-authentication-in-a-spring-boot-3-application-5839e4fd8fac