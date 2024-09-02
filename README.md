# Goriant Game Server with Microservices Architecture

## Tech Stacks

- Java 21
- Gradle 8.8
- Netty 4.1.110.Final
- Google Protobuf
- Flyway
- Redis
- MongoDB
- PostgreSQL

## DevOps & Infra

- Docker
- Kubernetes
- GCP

## Services

- goriant-game-server
- goriant-oauth
- goriant-notification

## Guideline Start Services

### Project checkout structure

+ goriant-studio (root)
+++ goriant-ms (https://github.com/goriant-studio/goriant-ms)
+++ goriant-models (https://github.com/goriant-studio/goriant-models)

```shell

# change directory to goriant-ms
cd goriant-studio/goriant-ms

# Generate Proto
./gradlew :services:goriant-game-server:generateProto

# Start Game Server
./gradlew :services:goriant-game-server:run

```