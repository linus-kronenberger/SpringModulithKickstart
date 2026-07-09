# Spring Modulith Kickstart

## Welcome 😀
This repository is a template setup for a server side application (with the domain of a simple movie review app) providing some API's:


## Commands
## Install maven
```bash
brew install maven
```
## Install the dependencies
```bash
mvn clean install
```
## Start the app
```bash
mvn spring-boot:run
```

## Test the endpoints
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
### 
## Linting
```bash
mvn spotless:check
```

```bash
mvn spotless:apply
```

## Sources
https://medium.com/@tericcabrel/implement-jwt-authentication-in-a-spring-boot-3-application-5839e4fd8fac