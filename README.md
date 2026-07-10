# Spring Modulith Kickstart

## Welcome

This repository is a template setup for a server-side application in the domain of a simple movie review platform. It uses Spring Modulith to structure the code into three modules: `user`, `movie`, and `review`. Authentication is handled via JWT tokens.

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

## API Endpoints

### User Module (`/auth`)

Registration and authentication are handled by `AuthenticationController`. Both endpoints are publicly accessible.

- **POST `/auth/signup`** -- Creates a new user account. Expects a JSON body with `email`, `password`, and `fullName`. Returns the created `User` entity.

- **POST `/auth/login`** -- Authenticates an existing user. Expects a JSON body with `email` and `password`. Returns a JWT token and its expiration time in seconds.

### Movie Module (`/movie`)

Movie management is handled by `MovieController`. Both endpoints require a valid JWT in the `Authorization` header.

- **POST `/movie/new`** -- Registers a new movie. Expects a JSON body with `movieTitle` and `movieDescription`. Returns a confirmation string.

- **GET `/movie/all`** -- Retrieves all registered movies. Returns a list of `MovieDTO` objects, each containing `movieTitle` and `movieDescription`.

### Review Module

A `ReviewController` stub exists but currently exposes no endpoints. This module is a placeholder for future review functionality.

### Register
```bash
curl -X POST 'localhost:8080/auth/signup' \
  --header 'User-Agent: yaak' \
  --header 'Accept: */*' \
  --header 'Content-Type: application/json' \
  --data '{
  "email" : "max.mustermann@gmail.com",
  "password" : "test",
  "fullName" : "Max Mustermann"
}'
```

### Login
```bash
curl -X POST 'localhost:8080/auth/login' \
  --header 'User-Agent: yaak' \
  --header 'Accept: */*' \
  --header 'Content-Type: application/json' \
  --data '{
  "email" : "max.mustermann@gmail.com",
  "password" : "test"
}'
```

## Linting
```bash
mvn spotless:check
```

```bash
mvn spotless:apply
```

## Sources
https://medium.com/@tericcabrel/implement-jwt-authentication-in-a-spring-boot-3-application-5839e4fd8fac