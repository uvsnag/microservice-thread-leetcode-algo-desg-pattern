# 50 Senior Java/Microservices Interview Questions

> All questions are directly related to the concepts and implementations in this demo project.

---

## API Gateway & Routing (Q1–Q7)

### Q1. Why do we need an API Gateway in a microservices architecture?
**Answer:** An API Gateway provides a single entry point for all client requests, handling cross-cutting concerns like authentication, rate limiting, CORS, logging, and request routing. Without it, clients would need to know the addresses of every service and handle auth individually. In this project, `api-gateway` routes `/users/**` to user-service, `/auth/**` to auth-service, etc., and validates JWT before forwarding.

### Q2. How does Spring Cloud Gateway differ from Zuul?
**Answer:** Spring Cloud Gateway is built on reactive (Project Reactor + Netty) while Zuul 1.x was servlet-based (blocking I/O). Gateway uses `GlobalFilter` and `GatewayFilter` with non-blocking `Mono<Void>` return types. It supports predicates (Path, Method, Header) and filters (RequestRateLimiter, CircuitBreaker) natively via YAML configuration. This project uses it with reactive Redis for rate limiting and Resilience4j for circuit breaking.

### Q3. Explain how the JwtAuthenticationFilter works as a GlobalFilter.
**Answer:** It implements `GlobalFilter` (runs for every request) and `Ordered` (priority -100 = runs first). It checks if the path matches open patterns (e.g., `/auth/**`). If not, it extracts the `Bearer` token from the `Authorization` header, parses JWT claims using the HMAC key, and mutates the request to add `X-User-Id`, `X-Company-Code`, and `X-User-Name` headers for downstream services. If the token is invalid, it returns 401 immediately without forwarding.

### Q4. How do you configure route-specific vs global filters in Spring Cloud Gateway?
**Answer:** Global filters apply via `spring.cloud.gateway.default-filters` (e.g., `RequestRateLimiter` with 10 req/s). Route-specific filters are under each route's `filters` section. In this project, the auth-service route has a stricter rate limit (5 req/s) while the user-service route has a dedicated `CircuitBreaker` filter. Global default-filters are inherited by all routes unless overridden.

### Q5. How does the gateway forward user identity to downstream services?
**Answer:** After JWT validation, the filter mutates the `ServerHttpRequest` with `.mutate().header("X-User-Id", claims.getSubject())`. Downstream services receive these headers directly. This is stateless — no session is stored. Each service can extract user context from headers without re-validating the JWT, which is a common pattern for internal service communication behind a trusted gateway.

### Q6. What is the purpose of the `/fallback` endpoint?
**Answer:** The fallback controller returns a 503 response when a circuit breaker is in OPEN state. When a downstream service (e.g., user-service) is unavailable and the circuit breaker trips, requests are automatically forwarded to `/fallback` instead of timing out. This provides a graceful degradation experience — users get an immediate response instead of hanging connections.

### Q7. How does Correlation ID propagation work across services?
**Answer:** The API Gateway adds `X-Request-Id` header via `AddRequestHeader` default filter. Each downstream service has a `CorrelationIdFilter` (servlet `Filter`) that extracts this header and puts it into SLF4J MDC as `correlationId`. This allows tracing a single request across all log files. If the header is missing (direct calls without gateway), a random UUID is generated as fallback.

---

## JWT Authentication (Q8–Q14)

### Q8. Explain the difference between access tokens and refresh tokens. Why use both?
**Answer:** Access tokens are short-lived (1 hour in this project) and used for API authentication. Refresh tokens are long-lived (24 hours) and used only to obtain new access tokens. This minimizes exposure: if an access token is stolen, it expires quickly. The refresh token is only sent to `/auth/refresh`, reducing its attack surface. This project implements both — `generateAccessToken` includes `usrId`, `coCd`, `usrNm`, while `generateRefreshToken` only includes `usrId` and `coCd` with type `"refresh"`.

### Q9. How is the JWT secret key managed in this project?
**Answer:** The key is a Base64-encoded 512-bit secret configured via `app.jwt.secret` property (environment variable `APP_JWT_SECRET`). Both `auth-service` (token generation) and `api-gateway` (token validation) share the same key. In production, this should use a secrets manager (AWS Secrets Manager, HashiCorp Vault). The key is decoded using `Decoders.BASE64.decode()` and converted to `SecretKey` via `Keys.hmacShaKeyFor()`.

### Q10. Why is `verifyPassword` checking for `$1$` prefix?
**Answer:** `$1$` indicates MD5 crypt hash format (`$1$salt$hash`). Many legacy systems store passwords in this format. The method uses `Md5Crypt.md5Crypt()` from Apache Commons Codec to verify. If the password doesn't start with `$1$`, it falls back to plain-text comparison (for demo data). In production, you should use BCrypt (`$2a$`) or Argon2. This demonstrates handling legacy password formats during migration.

### Q11. What happens when a JWT expires? How does the client handle it?
**Answer:** When the JWT expires, `Jwts.parser().parseSignedClaims()` throws `ExpiredJwtException`. The gateway catches it and returns 401. The React frontend's Axios interceptor detects 401 responses, clears the stored tokens from `localStorage`, and redirects to `/login`. The user can also proactively use `/auth/refresh` with the refresh token before the access token expires to get a new pair seamlessly.

### Q12. Why does the gateway validate JWT instead of each downstream service?
**Answer:** Centralizing JWT validation at the gateway means downstream services don't need JWT libraries or the secret key. It follows the "trust the gateway" pattern — internal services trust headers (`X-User-Id`) from the gateway within the trusted network. This reduces duplication and simplifies service code. However, in zero-trust architectures, each service might re-validate.

### Q13. What claims are stored in the JWT? Why?
**Answer:** Access token: `sub` (usrId), `coCd`, `usrNm`, `type` ("access"), `iat`, `exp`. Refresh token: `sub`, `coCd`, `type` ("refresh"), `iat`, `exp`. The `type` claim distinguishes tokens — the refresh endpoint checks `type == "refresh"` to prevent using access tokens for refresh. Minimal claims reduce token size while providing essential user context for downstream services.

### Q14. How does the auth-service publish login audit events?
**Answer:** After each login attempt (success or failure), `AuthEventPublisher` serializes an `AuthEvent` record to JSON and sends it to Kafka topic `auth.events` via `KafkaTemplate.send()`. This provides an audit trail for security monitoring. Failed login attempts include the reason ("User not found", "Wrong password", "Account inactive"). Both `cache-service` and `notification-service` consume these events asynchronously.

---

## Kafka (Q15–Q22)

### Q15. Why use Kafka instead of RabbitMQ for user and auth events?
**Answer:** Kafka is designed for event streaming and log retention. User search events and auth audit logs are append-only event logs that might be replayed or analyzed later. Kafka retains messages even after consumption (configurable retention). RabbitMQ is better for task distribution where messages should be processed once and acknowledged. This project uses both to demonstrate the distinction.

### Q16. Explain the Kafka producer configuration in user-service.
**Answer:** The producer uses `StringSerializer` for both key and value. The key is the composite identifier (e.g., `"DEMO:john"` for view events, action name for search events). Keys ensure related events go to the same partition, maintaining ordering per user. The `KafkaTemplate.send(topic, key, value)` call is asynchronous — it returns a `CompletableFuture`. Serialization failures are logged but don't affect the main request flow.

### Q17. How do multiple consumers (cache-service, notification-service) both receive the same Kafka message?
**Answer:** Each service uses a different `groupId` in `@KafkaListener`. Kafka delivers each message to one consumer per consumer group. Since cache-service uses `groupId="cache-service"` and notification-service uses `groupId="notification-service"`, both receive every message independently. Within a single group, Kafka distributes partitions among consumers for load balancing.

### Q18. What is `auto-offset-reset: earliest` and when should you use it?
**Answer:** It determines where a new consumer group starts reading. `earliest` reads from the beginning of the topic (catches up on missed messages). `latest` only reads new messages from the point of subscription. In this project, `earliest` ensures that if cache-service restarts, it processes any events published while it was down. In production, use `earliest` for critical events and `latest` for real-time-only streams.

### Q19. What happens if a Kafka consumer fails to process a message?
**Answer:** By default with auto-commit, the offset is committed before processing completes, so the message won't be re-delivered. For critical processing, use manual offset commit (`enable-auto-commit: false`). Spring Kafka also supports `ErrorHandler` and `DeadLetterPublishingRecoverer` to send failed messages to a DLT (Dead Letter Topic). The `@DltHandler` annotation can handle these.

### Q20. How would you add a new Kafka topic for a new event type?
**Answer:** 1) Define the topic name in `application.yml` (e.g., `app.kafka.order-topic: order.events`). 2) Create the event record class. 3) Create a publisher with `KafkaTemplate`. 4) In consuming services, add the topic to their config and create a `@KafkaListener` method. Kafka auto-creates topics if `KAFKA_AUTO_CREATE_TOPICS_ENABLE: true` (set in Docker compose). For production, pre-create topics with proper partition/replication settings.

### Q21. Explain the difference between Kafka topic partitions and consumer groups.
**Answer:** Partitions are the unit of parallelism — a topic is divided into N partitions, each an ordered log. Consumer groups coordinate consumption: each partition is assigned to exactly one consumer within a group. If you have 3 partitions and 2 consumers in a group, one consumer gets 2 partitions. If you add a 3rd consumer, each gets 1. More consumers than partitions means some are idle. The message key determines the partition via hashing.

### Q22. How does this project handle Kafka serialization?
**Answer:** Both producer and consumer use `StringSerializer`/`StringDeserializer`. Events are manually serialized to JSON using `ObjectMapper.writeValueAsString()` on the producer side and `ObjectMapper.readValue()` on the consumer side. An alternative is using `JsonSerializer`/`JsonDeserializer` with type info headers, or Avro with Schema Registry for schema evolution. String-based JSON is simpler but less type-safe.

---

## RabbitMQ (Q23–Q30)

### Q23. Explain the three RabbitMQ exchange types used in this project.
**Answer:**
- **Direct Exchange** (`file.activity.exchange`): Routes messages to queues by exact routing key match. File events with key `file.activity.cache` go only to the cache queue.
- **Fanout Exchange** (`system.broadcast.exchange`): Ignores routing keys and copies messages to ALL bound queues. Used for system-wide announcements.
- **Topic Exchange** (`event.topic.exchange`): Routes by pattern matching. `event.#` matches any routing key starting with `event.` (e.g., `event.user.login`, `event.file.upload`). `*` matches one word, `#` matches zero or more.

### Q24. What is a Dead Letter Queue and why is it important?
**Answer:** A DLQ receives messages that couldn't be processed by the main queue. In this project, `file.activity.cache.queue` has `x-dead-letter-exchange: file.activity.dlx` and `x-dead-letter-routing-key: file.activity.dlq`. If a consumer throws an exception and the message is rejected (nacked), RabbitMQ routes it to the DLQ instead of discarding it. This enables debugging failed messages, manual retry, and preventing poison messages from blocking the queue.

### Q25. How do you configure a DLQ in Spring Boot with RabbitMQ?
**Answer:** Use `QueueBuilder.durable(queueName).withArgument("x-dead-letter-exchange", "dlx-name").withArgument("x-dead-letter-routing-key", "dlq-routing-key").build()`. Then create the DLX exchange and DLQ queue, and bind them. In this project, `RabbitMQConfig` creates: 1) Main queue with DLX args, 2) `file.activity.dlx` DirectExchange, 3) `file.activity.dlq` Queue, 4) Binding between DLQ and DLX.

### Q26. When would you use Fanout vs Topic exchange?
**Answer:** **Fanout**: When every consumer needs every message (system announcements, config changes, cache invalidation). No routing logic needed. **Topic**: When consumers need messages matching a pattern (e.g., notification-service subscribes to `event.#` for all events, but an audit-service might subscribe to `event.auth.*` for auth-only events). Topic is more flexible but adds routing key complexity.

### Q27. How does RabbitMQ ensure message durability?
**Answer:** Three levels: 1) **Durable exchange**: `new DirectExchange(name, true, false)` — survives broker restart. 2) **Durable queue**: `QueueBuilder.durable(name)` — queue definition persists. 3) **Persistent messages**: `MessageProperties.PERSISTENT` — messages written to disk. RabbitMQ only guarantees durability when all three are set. The `RabbitTemplate` in Spring Boot sends persistent messages by default.

### Q28. What is the difference between `@RabbitListener` and `@KafkaListener`?
**Answer:** `@RabbitListener(queues = "queue-name")` consumes from a specific RabbitMQ queue. Messages are acknowledged per-message (auto or manual). `@KafkaListener(topics = "topic", groupId = "group")` consumes from a Kafka topic partition. Offset-based, meaning you track position in the log. RabbitMQ removes messages after ack; Kafka retains them. RabbitMQ supports complex routing; Kafka is simpler but higher throughput.

### Q29. How does this project demonstrate multiple consumers for the same RabbitMQ exchange?
**Answer:** The `system.broadcast.exchange` (Fanout) has two queues bound: `system.broadcast.cache.queue` and `system.broadcast.notification.queue`. Cache-service listens to its queue via `BroadcastListener`, notification-service to its queue. When a broadcast message is published, both services receive a copy. This is the pub/sub pattern in RabbitMQ, analogous to Kafka consumer groups but using exchange-queue bindings.

### Q30. What happens if RabbitMQ is unavailable when a service starts?
**Answer:** With `spring.rabbitmq.listener.simple.missing-queues-fatal: false`, the service starts even if queues don't exist yet. The listener retries connecting. If RabbitMQ is completely down, `RabbitTemplate.convertAndSend()` throws `AmqpConnectException`. To handle this gracefully, wrap publish calls in try-catch. For critical messages, consider using a transactional outbox pattern or message persistence.

---

## Rate Limiting (Q31–Q34)

### Q31. How does the Token Bucket algorithm work for rate limiting?
**Answer:** Each user/IP gets a "bucket" with tokens. Each request consumes one token. Tokens are replenished at a fixed rate (`replenishRate: 10` = 10 tokens/second). `burstCapacity: 20` is the maximum bucket size, allowing short bursts. If the bucket is empty (all tokens consumed), the request gets HTTP 429 Too Many Requests. Redis stores the bucket state, enabling distributed rate limiting across multiple gateway instances.

### Q32. Why use Redis for rate limiting instead of in-memory?
**Answer:** In-memory rate limiting only works for a single instance. With multiple API Gateway instances behind a load balancer, each would track limits independently — a user could send 10x the limit across N instances. Redis provides a centralized, atomic counter accessible by all instances. Spring Cloud Gateway's `RequestRateLimiter` uses Redis with Lua scripts for atomic token bucket operations.

### Q33. How does the KeyResolver determine whose rate to limit?
**Answer:** The `userKeyResolver` bean first checks for `X-User-Id` header (set by JWT filter for authenticated users). If present, rate limits per user. Otherwise, it falls back to client IP address. This means: authenticated users are rate-limited by identity (fair regardless of IP), unauthenticated users (e.g., login attempts) are rate-limited by IP. The auth route has a stricter limit (5/s) to prevent brute-force attacks.

### Q34. How do you configure different rate limits for different endpoints?
**Answer:** Define `default-filters` for global rate limits, then override per-route in the `filters` section. In this project, the global default is 10 req/s with burst 20. The `/auth/**` route overrides with 5 req/s and burst 10. You could also use different `KeyResolver` beans for different routes (e.g., rate-limit login by IP but API calls by user ID).

---

## Circuit Breaker (Q35–Q38)

### Q35. Explain the three states of a circuit breaker.
**Answer:**
- **CLOSED** (normal): All requests pass through. Failures are counted in a sliding window.
- **OPEN** (tripped): When failure rate exceeds threshold (50% in this project), the circuit opens. All requests immediately fail with the fallback response (503). No calls reach the downstream service.
- **HALF_OPEN** (testing): After `waitDurationInOpenState` (10s), the circuit allows a limited number of trial calls (`permittedNumberOfCallsInHalfOpenState: 3`). If they succeed, it transitions to CLOSED. If they fail, it goes back to OPEN.

### Q36. Why use circuit breakers in microservices?
**Answer:** Without a circuit breaker, when a downstream service is slow or down, the calling service accumulates blocked threads waiting for timeouts, eventually causing cascading failure. The circuit breaker "fails fast" — immediately returns a fallback response, preserving the gateway's thread pool. Combined with a time limiter (`timeoutDuration: 5s`), it prevents resource exhaustion.

### Q37. How is Resilience4j configured in this project's API Gateway?
**Answer:** Via YAML: `resilience4j.circuitbreaker.configs.default` sets the base config (sliding window 10, threshold 50%). Service-specific instances (`userServiceCB`, `fileServiceCB`) inherit from default but can override. Routes reference them: `CircuitBreaker` filter with `name: userServiceCB` and `fallbackUri: forward:/fallback`. The fallback controller returns 503 with a JSON error message. Actuator exposes CB state at `/actuator/circuitbreakers`.

### Q38. What is the sliding window and how does it affect circuit breaker behavior?
**Answer:** The sliding window counts the last N calls (`slidingWindowSize: 10`). Only after `minimumNumberOfCalls: 5` calls does the failure rate calculation begin. With a window of 10 and threshold of 50%, the circuit opens after 5+ failures in the last 10 calls. There are two types: COUNT_BASED (last N calls) and TIME_BASED (calls within last N seconds). This project uses COUNT_BASED (default).

---

## Redis & Caching (Q39–Q42)

### Q39. Why use `StringRedisTemplate` instead of `RedisTemplate<Object, Object>`?
**Answer:** `StringRedisTemplate` uses `StringRedisSerializer` for both keys and values, making data human-readable in Redis (useful for debugging with `redis-cli`). `RedisTemplate<Object, Object>` uses JDK serialization by default, which produces binary data and has Java class-path coupling. Since this project stores JSON strings manually via `ObjectMapper`, `StringRedisTemplate` is the natural choice.

### Q40. Explain the Redis data structures used in this project.
**Answer:**
- **Strings** (`SET`/`GET`): Used for cache entries (`entries::key`) and user events (`users::DEMO:john`). Simple key-value pairs.
- **Lists** (`LPUSH`/`LRANGE`/`LTRIM`): Used for file activities (`files::activity`) and auth activities (`auth::activity`). `LPUSH` adds to the front, `LTRIM` keeps only the latest 25/50 items — effectively a capped collection (like a ring buffer). `LRANGE 0 24` retrieves the 25 most recent items.

### Q41. How does event-driven cache population work?
**Answer:** Instead of the cache-service polling databases or APIs, events flow in via Kafka and RabbitMQ. When a user is searched (`user-service` → Kafka → `cache-service`), the event is cached in Redis. When a file is listed (`file-service` → RabbitMQ → `cache-service`), the activity is recorded. This is eventual consistency — the cache may be slightly behind, but it's populated without synchronous HTTP calls between services.

### Q42. How would you handle cache invalidation in this architecture?
**Answer:** Options: 1) **TTL-based**: Set expiration on Redis keys (`redisTemplate.expire(key, duration)`). 2) **Event-driven**: Publish a cache-invalidation event when data changes. 3) **Write-through**: Update cache on every write. 4) **Cache-aside**: Services check cache first, fall back to DB, then populate cache. This project uses event-driven population without invalidation (read-only data), which is acceptable for append-only event logs.

---

## MyBatis & Database (Q43–Q46)

### Q43. Why choose MyBatis over JPA/Hibernate for this project?
**Answer:** MyBatis provides full SQL control, which is essential when: 1) Working with legacy databases with unconventional schemas (like `adm_usr` with 50+ columns). 2) Complex queries that don't map well to JPQL. 3) Read-only access where ORM features (dirty checking, lazy loading) aren't needed. 4) Performance-sensitive queries where hand-tuned SQL outperforms generated SQL. MyBatis XML mappers give clear visibility into the exact SQL executed.

### Q44. How does the `resultMap` handle composite primary keys?
**Answer:** In the MyBatis XML mapper, multiple `<id>` elements define the composite key: `<id column="usr_id" property="usrId"/>` and `<id column="co_cd" property="coCd"/>`. The mapper method `findByCompositeKey` takes both as `@Param`: `findByCompositeKey(@Param("coCd") String coCd, @Param("usrId") String usrId)`. MyBatis uses both fields for identity comparison and caching.

### Q45. How does `map-underscore-to-camel-case` work and when is it insufficient?
**Answer:** MyBatis automatically maps `usr_nm` → `usrNm`, `co_cd` → `coCd` for simple queries returning a single type. However, for complex mappings (composite keys, nested objects, column aliasing), an explicit `<resultMap>` is needed. In this project, `admUsrResultMap` explicitly maps all 50+ columns because the model uses the auto-mapping setting but still defines the full `resultMap` for clarity and composite key handling.

### Q46. What SQL injection protections does MyBatis provide?
**Answer:** MyBatis uses `#{param}` (PreparedStatement parameters) which are SQL-injection safe — values are bound as parameters, not concatenated. The `searchByKeyword` query uses `LOWER(usr_nm) LIKE LOWER(CONCAT('%', #{keyword}, '%'))` — the `#{keyword}` is properly escaped. Using `${}` (string substitution) would be vulnerable. Always use `#{}` for user input and `${}` only for trusted column/table names.

---

## Docker & Infrastructure (Q47–Q50)

### Q47. Explain the multi-stage Dockerfile used in this project.
**Answer:** Stage 1 (`maven:3.9.9-eclipse-temurin-17`): Copies all source code, runs `mvnw package -DskipTests` for a single module using `-pl ${MODULE_NAME} -am` (also-make = build dependencies). This produces the JAR. Stage 2 (`eclipse-temurin:17-jre`): Copies only the JAR file. The final image contains only the JRE and the application JAR — no Maven, source code, or build tools. This reduces the image from ~800MB to ~200MB.

### Q48. How does Docker Compose handle service startup ordering?
**Answer:** `depends_on` with `condition: service_healthy` ensures proper ordering. PostgreSQL has a healthcheck (`pg_isready`), so auth-service and user-service only start after PostgreSQL is healthy. Kafka depends on Zookeeper. Services depending on Kafka use `kafka-broker-api-versions` healthcheck. Without health checks, `depends_on` only waits for the container to start, not for the service inside to be ready.

### Q49. Why does the init.sql set `search_path`?
**Answer:** PostgreSQL supports multiple schemas within a database. The `SET search_path TO us_fwd, public` ensures that tables are created in the `us_fwd` schema. The JDBC URL includes `?currentSchema=us_fwd` so services query the correct schema. This mirrors production setups where multiple applications share a database server but use separate schemas for isolation, while sharing the same physical database for operational simplicity.

### Q50. How would you scale this architecture in production?
**Answer:** 1) **API Gateway**: Run multiple instances behind a load balancer; Redis-backed rate limiting works across instances. 2) **Stateless services**: user-service, auth-service can scale horizontally (no session state). 3) **Kafka**: Increase partitions for higher throughput; add consumers within the same group. 4) **Database**: Read replicas for user-service, write instance for auth-service. 5) **Redis**: Redis Cluster for high availability. 6) **Container orchestration**: Kubernetes with HPA (Horizontal Pod Autoscaler) based on CPU/memory metrics. 7) **Service mesh**: Istio for advanced traffic management, mutual TLS between services.
