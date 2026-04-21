# Demo AWS Microservice Platform

This repository was reshaped from a single Spring Boot demo into:

- `user-service`: PostgreSQL-backed user CRUD and Kafka publisher
- `cache-service`: Redis-backed cache API plus Kafka and RabbitMQ consumers
- `file-service`: S3 file access API and RabbitMQ publisher
- `api-gateway`: single entry point for `/users`, `/cache`, and `/files`

## Architecture

- `user-service` publishes user lifecycle events to Kafka topic `user.events`
- `cache-service` consumes those Kafka events and keeps cached user snapshots in Redis
- `file-service` publishes S3 access activity to RabbitMQ exchange `file.activity.exchange`
- `cache-service` consumes RabbitMQ file activity messages and stores recent activity in Redis
- `api-gateway` forwards requests to each backend service

## Local Run

1. Export AWS credentials if you want live S3 access.
2. Start the stack:

```bash
docker compose up --build
```

3. Use the gateway:

```bash
curl http://localhost:8080/users
curl "http://localhost:8080/cache/entries"
curl "http://localhost:8080/files?folder=demo"
```

## CI/CD

GitHub Actions now:

- runs `./mvnw clean verify` on every pull request and push
- builds and pushes Docker images for the gateway and each microservice on `main` or `master`

## Environment Variables

- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATA_REDIS_HOST`, `SPRING_DATA_REDIS_PORT`, `SPRING_DATA_REDIS_PASSWORD`
- `APP_KAFKA_BOOTSTRAP_SERVERS`
- `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_PORT`, `SPRING_RABBITMQ_USERNAME`, `SPRING_RABBITMQ_PASSWORD`
- `APP_AWS_REGION`, `APP_AWS_ACCESS_KEY_ID`, `APP_AWS_SECRET_ACCESS_KEY`, `APP_AWS_ENDPOINT`, `APP_STORAGE_BUCKET`
# microservice-thread-leetcode-algo-desg-pattern
