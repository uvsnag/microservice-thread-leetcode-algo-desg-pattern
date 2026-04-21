# Demo AWS Microservices Platform

## Project Structure & Code Guidelines

> A learning-oriented microservices project demonstrating key concepts for senior Java/Spring interviews.

---

## Architecture Overview

```
┌─────────────┐      ┌──────────────────────────────────────────────────────────────────┐
│  Frontend   │─────>│  API Gateway (8080)                                              │
│  React:3000 │      │  - JWT validation filter                                         │
└─────────────┘      │  - Rate Limiting (Redis + Token Bucket)                          │
                      │  - Circuit Breaker (Resilience4j)                                │
                      │  - Correlation ID propagation                                    │
                      │  - Routes to all downstream services                            │
                      └──────┬───────┬───────┬───────┬────────┬────────────────────────┘
                             │       │       │       │        │
                 ┌───────────┘  ┌────┘  ┌────┘  ┌───┘   ┌────┘
                 ▼              ▼       ▼       ▼        ▼
          ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐
          │Auth Svc  │  │User Svc  │  │Cache Svc │  │File Svc  │  │Notification Svc  │
          │  :8084   │  │  :8081   │  │  :8082   │  │  :8083   │  │    :8085         │
          │          │  │          │  │          │  │          │  │                  │
          │ JWT      │  │ MyBatis  │  │ Redis    │  │ AWS S3   │  │ In-memory store  │
          │ MyBatis  │  │ Kafka    │  │ Kafka    │  │ RabbitMQ │  │ Kafka consumer   │
          │ Kafka    │  │ PostgreSQL│ │ RabbitMQ │  │          │  │ RabbitMQ consumer│
          │ PostgreSQL│  │          │  │ Fanout   │  │          │  │ Fanout+Topic     │
          └──────────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────────────┘
                             │              │              │              │
                             │   ┌──────────┘              │              │
                             │   │     ┌───────────────────┘              │
                             ▼   ▼     ▼                                  │
                          ┌────────┐ ┌──────────┐                         │
                          │ Kafka  │ │ RabbitMQ │ ◄───────────────────────┘
                          │        │ │  DLQ     │
                          │ Topics:│ │  Fanout  │
                          │ user.  │ │  Topic   │
                          │ auth.  │ │  Direct  │
                          └────────┘ └──────────┘
```

### Service Summary

| Service              | Port | Technologies                                     | Purpose                                         |
|----------------------|------|--------------------------------------------------|-------------------------------------------------|
| **api-gateway**      | 8080 | Spring Cloud Gateway, JWT, Redis, Resilience4j   | Central routing, JWT auth, rate limiting, CB     |
| **auth-service**     | 8084 | JWT (jjwt), MyBatis, PostgreSQL, Kafka producer  | Login, token refresh, token validation, audit    |
| **user-service**     | 8081 | MyBatis, PostgreSQL, Kafka producer              | Read-only user queries from `adm_usr` table      |
| **cache-service**    | 8082 | Redis, Kafka consumer, RabbitMQ (DLQ, Fanout)    | Caches events, broadcast, dead-letter handling   |
| **file-service**     | 8083 | AWS S3 SDK, RabbitMQ producer                    | List and download files from S3                  |
| **notification-svc** | 8085 | Kafka consumer, RabbitMQ (Fanout, Topic)         | Stores event notifications, wildcard routing     |
| **learning-service** | 8086 | Spring Boot Web, SpringDoc OpenAPI                | Design patterns, algorithms, data structures demos|
| **frontend**         | 3000 | React, Vite, Axios                               | Web UI for interacting with all services         |

---

## Microservice Concepts Demonstrated

### 1. API Gateway Pattern
- `api-gateway` routes all traffic through a single entry point
- JWT validation happens at the gateway level before reaching downstream services
- User identity forwarded via `X-User-Id`, `X-Company-Code`, `X-User-Name` headers
- Correlation ID (`X-Request-Id`) injected into every request for distributed tracing

### Where API Gateway Route Distribution Is Implemented

- **Route config file**: `api-gateway/src/main/resources/application.yml`
    - `spring.cloud.gateway.routes` maps request paths to downstream services.
    - `/auth/**` → `auth-service` (8084)
    - `/users/**` → `user-service` (8081)
    - `/cache/**` → `cache-service` (8082)
    - `/files/**` → `file-service` (8083)
    - `/notifications/**` → `notification-service` (8085)

- **Security filter**: `api-gateway/.../filter/JwtAuthenticationFilter.java`
- **Rate limiter config**: `api-gateway/.../config/RateLimiterConfig.java`
- **Fallback controller**: `api-gateway/.../controller/FallbackController.java`

### 2. Authentication & Authorization (JWT)
- `auth-service` handles login against PostgreSQL (`adm_usr` table)
- Returns access token + refresh token
- API Gateway validates JWT on every protected request
- **Audit logging**: Login success/failure events published to Kafka `auth.events` topic

### 3. Event-Driven Architecture
- **Kafka Topics**:
    - `user.events` — User service publishes search/view events → consumed by cache-service and notification-service
    - `auth.events` — Auth service publishes login/refresh/validate events → consumed by cache-service and notification-service
- **RabbitMQ Exchanges**:
    - **Direct Exchange** (`file.activity.exchange`) — File activities routed to specific queues
    - **Fanout Exchange** (`system.broadcast.exchange`) — Broadcasts to ALL consumers
    - **Topic Exchange** (`event.topic.exchange`) — Wildcard routing with `event.#`
    - **Dead Letter Queue** (`file.activity.dlq`) — Failed messages re-routed for retry

### 4. Rate Limiting (Token Bucket Algorithm)
- Implemented at API Gateway using `RequestRateLimiter` filter
- Backed by Redis for distributed rate limiting
- Global: 10 req/s, burst 20 | Auth endpoints: 5 req/s (brute-force protection)
- Key resolver: by authenticated user ID or client IP

### 5. Circuit Breaker Pattern (Resilience4j)
- States: CLOSED → OPEN → HALF_OPEN → CLOSED
- Sliding window 10 calls, opens at 50% failure rate
- Auto recovery after 10s, per-service instances
- Fallback: 503 Service Unavailable

### 6. Database Access (MyBatis)
- Read-only access to PostgreSQL via MyBatis XML mapper
- snake_case → camelCase result mapping, composite primary key
- Parametrized queries preventing SQL injection
- **Local Docker PostgreSQL** with init.sql seed data

### 7. Caching (Redis)
- Event-driven cache population from Kafka and RabbitMQ
- Manual cache entries via REST API
- Also powers distributed rate limiting in API Gateway

### 8. Cloud Services (AWS S3)
- File listing and download, supports LocalStack endpoint

### 9. API Documentation (Swagger/OpenAPI)
- All services at `/swagger-ui.html` and `/v3/api-docs`

### 10. Containerization (Docker)
- Multi-stage Dockerfile, Docker Compose with health checks
- All services connect to **local Docker PostgreSQL** (no external DB dependency)

### 11. Global Exception Handling
- `@RestControllerAdvice` with consistent `{ error, status, timestamp }` responses
- Handles validation, business, and unexpected errors

### 12. Distributed Tracing (Correlation ID)
- Gateway injects `X-Request-Id` → downstream `CorrelationIdFilter` → SLF4J MDC

### 13. Message Queue Patterns (RabbitMQ)
- Direct Exchange, Fanout Exchange, Topic Exchange, Dead Letter Queue

---

## Project Package Structure

```
{service}/src/main/java/com/example/{service}/
├── {Service}Application.java     # Spring Boot main class (ONLY)
├── config/                       # Configuration classes
│   ├── RabbitMQConfig.java       # RabbitMQ beans (DLQ, Fanout, Topic)
│   ├── RateLimiterConfig.java    # Rate limiter key resolver (gateway)
│   ├── CorrelationIdFilter.java  # MDC correlation ID filter
│   └── S3Config.java             # AWS S3 client (file-service)
├── controller/                   # REST controllers
├── service/                      # Business logic
├── mapper/                       # MyBatis mapper interfaces
├── model/                        # Domain entities
├── dto/                          # Data transfer objects (records)
├── event/                        # Event publishing + event records
├── listener/                     # Kafka/RabbitMQ consumers
├── exception/                    # @RestControllerAdvice handlers
├── security/                     # JWT components (auth-service)
└── filter/                       # Gateway filters (api-gateway)
```

---

## How to Start the Project

### Option 1: Full Docker Compose (Recommended)

```bash
docker compose up --build
# Frontend: http://localhost:3000 | Gateway: http://localhost:8080 | RabbitMQ: http://localhost:15672
```

### Option 2: Local Development

```bash
docker compose up postgres redis zookeeper kafka rabbitmq
./mvnw clean package -DskipTests
# Run each service in separate terminals
cd frontend && npm install && npm run dev
```

---

## API Endpoints Quick Reference

### Auth Service (8084)
| Method | Path             | Description                     |
|--------|------------------|---------------------------------|
| POST   | `/auth/login`    | Login (coCd, usrId, password)   |
| POST   | `/auth/refresh`  | Refresh access token            |
| GET    | `/auth/validate` | Validate JWT                    |

### User Service (8081)
| Method | Path                      | Description                  |
|--------|---------------------------|------------------------------|
| GET    | `/users`                  | List all users               |
| GET    | `/users/{coCd}/{usrId}`   | Get user by composite key    |
| GET    | `/users/company/{coCd}`   | Users by company             |
| GET    | `/users/search?keyword=x` | Search by name/email/id      |
| GET    | `/users/by-age/{age}`     | Filter by age                |
| GET    | `/users/count`            | Total user count             |

### Cache Service (8082)
| Method | Path                      | Description                            |
|--------|---------------------------|----------------------------------------|
| POST   | `/cache/entries`          | Set cache entry (key, value)           |
| GET    | `/cache/entries/{key}`    | Get cache entry                        |
| GET    | `/cache/entries`          | List all entries                       |
| GET    | `/cache/users/{id}`       | Get cached user event                  |
| GET    | `/cache/file-activities`  | Recent file activities                 |
| GET    | `/cache/auth-activities`  | Recent auth activities                 |
| POST   | `/cache/broadcast`        | Broadcast via RabbitMQ Fanout Exchange |

### File Service (8083)
| Method | Path              | Description                    |
|--------|-------------------|--------------------------------|
| GET    | `/files`          | List S3 files                  |
| GET    | `/files/download` | Download file from S3          |

### Notification Service (8085)
| Method | Path                        | Description                 |
|--------|-----------------------------|-----------------------------|
| GET    | `/notifications`            | Recent notifications        |
| GET    | `/notifications/by-type`    | Filter by type              |
| GET    | `/notifications/count`      | Total count                 |

**Notification Types**: `USER_EVENT`, `AUTH_EVENT`, `FILE_EVENT`, `BROADCAST`, `TOPIC_EVENT`

---

## Data Flow Examples

### Login Flow (with Kafka Audit)
```
Browser → POST /auth/login → Gateway (open path, rate limited 5/s)
         → Auth Service → PostgreSQL → Kafka (auth.events)
         → Cache Service → Redis | Notification Service → In-memory
         ← { accessToken, refreshToken, usrId, coCd, usrNm }
```

### User Search Flow
```
Browser → GET /users/search?keyword=john → Gateway (JWT + rate limit + CB)
         → User Service → PostgreSQL → Kafka (user.events)
         → Cache Service → Redis | Notification Service → In-memory
         ← List<UserResponse>
```

### Broadcast Flow (Fanout Exchange)
```
POST /cache/broadcast?message=Maintenance at 10PM
         → RabbitMQ Fanout → Cache Queue + Notification Queue
```

### Circuit Breaker Flow
```
Service DOWN → 5+ failures → CB OPEN → 503 fallback → 10s wait → HALF_OPEN → recovery
```

---

## Learning Service (CS Fundamentals)

The `learning-service` (port 8086) provides interactive demos for design patterns, sorting algorithms, searching algorithms, and data structures. Each demo includes detailed comments explaining the concept, when to use it, and key interview points.

**Access via API Gateway**: `http://localhost:8080/api/learning/**` (no JWT required)

### Design Patterns

| Endpoint | Pattern | Real-world Example |
|---|---|---|
| `GET /api/learning/patterns/singleton` | Singleton | Spring beans, DB connection pool |
| `GET /api/learning/patterns/factory` | Factory | Notification system (Email/SMS/Push) |
| `GET /api/learning/patterns/strategy` | Strategy | Pricing calculator (Regular/Premium/Bulk) |
| `GET /api/learning/patterns/observer` | Observer | Stock price monitor, Kafka/RabbitMQ |
| `GET /api/learning/patterns/builder` | Builder | HTTP request builder, Lombok @Builder |
| `GET /api/learning/patterns/decorator` | Decorator | Coffee shop add-ons, Java I/O streams |
| `GET /api/learning/patterns/adapter` | Adapter | Payment gateway integration, JDBC |
| `GET /api/learning/patterns/template-method` | Template Method | Data export pipeline (CSV/JSON/XML) |

### Sorting Algorithms

| Endpoint | Algorithm | Time (Best/Avg/Worst) | Space | Stable |
|---|---|---|---|---|
| `GET /api/learning/algorithms/sorting/bubble` | Bubble Sort | O(n) / O(n²) / O(n²) | O(1) | Yes |
| `GET /api/learning/algorithms/sorting/selection` | Selection Sort | O(n²) / O(n²) / O(n²) | O(1) | No |
| `GET /api/learning/algorithms/sorting/insertion` | Insertion Sort | O(n) / O(n²) / O(n²) | O(1) | Yes |
| `GET /api/learning/algorithms/sorting/merge` | Merge Sort | O(n log n) all cases | O(n) | Yes |
| `GET /api/learning/algorithms/sorting/quick` | Quick Sort | O(n log n) / O(n log n) / O(n²) | O(log n) | No |

### Searching Algorithms

| Endpoint | Algorithm | Time | Prerequisite |
|---|---|---|---|
| `GET /api/learning/algorithms/searching/binary` | Binary Search + Linear Search | O(log n) vs O(n) | Sorted array |

### Graph Algorithms

| Endpoint | Algorithm | Key Concepts |
|---|---|---|
| `GET /api/learning/algorithms/graph/bfs` | BFS (Breadth-First Search) | Level-order traversal, shortest path (unweighted), grid BFS |
| `GET /api/learning/algorithms/graph/dfs` | DFS (Depth-First Search) | Recursive/iterative, cycle detection (3-color), topological sort, connected components |

### Data Structures

| Endpoint | Structure | Key Operations |
|---|---|---|
| `GET /api/learning/data-structures/linked-list` | Singly Linked List | Insert, delete, reverse, find middle, cycle detection |
| `GET /api/learning/data-structures/stack` | Stack (LIFO) | Push, pop, peek, valid parentheses check |
| `GET /api/learning/data-structures/queue` | Queue (FIFO) + Priority Queue | Circular queue, min-heap priority queue |
| `GET /api/learning/data-structures/binary-tree` | Binary Search Tree | Insert, search, in/pre/post-order traversal, height |
| `GET /api/learning/data-structures/hash-map` | HashMap (custom) | Put, get, remove, chaining, rehashing |
| `GET /api/learning/data-structures/graph` | Graph (Adjacency List) | BFS, DFS, shortest path, cycle detection |

### Multithreading & Concurrency

| Endpoint | Topic | Key Concepts |
|---|---|---|
| `GET /api/learning/multithread/thread-basics` | Thread Basics | Thread class, Runnable, Callable/Future, join, lifecycle states |
| `GET /api/learning/multithread/synchronization` | Synchronization | synchronized, volatile, wait/notify, race conditions, deadlock |
| `GET /api/learning/multithread/executor-service` | ExecutorService | FixedThreadPool, invokeAll/invokeAny, CompletableFuture chaining |
| `GET /api/learning/multithread/concurrency-utils` | Concurrency Utilities | ReentrantLock, ReadWriteLock, AtomicInteger (CAS), CountDownLatch, CyclicBarrier, Semaphore |
| `GET /api/learning/multithread/concurrent-collections` | Concurrent Collections | ConcurrentHashMap, CopyOnWriteArrayList, BlockingQueue, ThreadLocal |

### LeetCode Practice (34 Problems)

| Endpoint | Category | # Problems | Topics |
|---|---|---|---|
| `GET /api/learning/leetcode/sliding-window` | Sliding Window | 8 | Longest Substring (#3), Character Replacement (#424), Min Window Substring (#76), Permutation in String (#567), Find All Anagrams (#438), Subarray Product < K (#713), Max Average Subarray (#643), Longest Subarray with Sum K |
| `GET /api/learning/leetcode/two-pointers` | Two Pointers (Opposite) | 6 | Two Sum II (#167), Container With Most Water (#11), 3Sum (#15), 4Sum (#18), Remove Duplicates (#26), Valid Palindrome (#125) |
| `GET /api/learning/leetcode/prefix-sum` | Prefix Sum | 4 | Subarray Sum Equals K (#560), Continuous Subarray Sum (#523), Range Sum Query (#303), Product Except Self (#238) |
| `GET /api/learning/leetcode/array` | Array Manipulation | 6 | Merge Intervals (#56), Insert Interval (#57), Rotate Array (#189), Set Matrix Zeroes (#73), Spiral Matrix (#54), Gas Station (#134) |
| `GET /api/learning/leetcode/string` | String Problems | 6 | Group Anagrams (#49), Valid Anagram (#242), Longest Palindromic Substring (#5), Palindromic Substrings (#647), String to Integer (#8), Encode/Decode Strings (#271) |
| `GET /api/learning/leetcode/bonus` | Bonus Problems | 4 | Top K Frequent Elements (#347), Sort Colors (#75), Move Zeroes (#283), Majority Element (#169) |

---

## Demo Login Credentials

| Company | User ID  | Password    | Notes          |
|---------|----------|-------------|----------------|
| DEMO    | admin    | admin123    | Root user      |
| DEMO    | john     | john123     |                |
| DEMO    | jane     | jane123     |                |
| DEMO    | bob      | bob123      | Contractor     |
| DEMO    | alice    | alice123    |                |
| DEMO    | charlie  | charlie123  | Security       |
| DEMO    | diana    | diana123    |                |
| DEMO    | inactive | inactive123 | Returns 403    |
| ACME    | manager  | manager123  |                |
| ACME    | dev01    | dev123      |                |
| ACME    | qa01     | qa123       | Contractor     |
| TECH    | cto      | cto123      |                |
| TECH    | sre01    | sre123      |                |

---

## Kafka Topics

| Topic          | Producer       | Consumers                        |
|----------------|----------------|----------------------------------|
| `user.events`  | user-service   | cache-service, notification-svc  |
| `auth.events`  | auth-service   | cache-service, notification-svc  |

## RabbitMQ Exchanges

| Exchange                     | Type    | Purpose                    |
|------------------------------|---------|----------------------------|
| `file.activity.exchange`     | Direct  | File download/list events  |
| `system.broadcast.exchange`  | Fanout  | System-wide broadcasts     |
| `event.topic.exchange`       | Topic   | Wildcard event routing     |
| `file.activity.dlx`         | Direct  | Dead letter re-routing     |

---

## Interview Discussion Topics

1. **Why API Gateway?** — Single entry point, cross-cutting concerns (auth, CORS, rate limiting, circuit breaking)
2. **Kafka vs RabbitMQ** — Kafka for event streaming (user & auth), RabbitMQ for task queuing + routing patterns
3. **JWT Authentication** — Stateless auth, access + refresh tokens, gateway-level validation
4. **MyBatis vs JPA** — MyBatis for fine-grained SQL control, legacy/complex DB schemas
5. **Event-Driven Architecture** — Loose coupling, eventual consistency, multiple consumers
6. **Caching Strategy** — Event-driven cache population, Redis for fast reads
7. **Rate Limiting** — Token bucket, Redis-backed distributed, per-endpoint config
8. **Circuit Breaker** — Resilience4j, sliding window, failure threshold, fallback
9. **Dead Letter Queue** — Failed message handling, retry strategies
10. **Message Exchange Patterns** — Direct vs Fanout vs Topic exchanges
11. **Global Exception Handling** — `@RestControllerAdvice`, consistent error responses
12. **Distributed Tracing** — Correlation ID propagation, MDC logging
13. **Docker & CI/CD** — Multi-stage builds, health checks, GitHub Actions
14. **Database per Service** — Shared DB acceptable for read-only scenarios
15. **API Documentation** — OpenAPI/Swagger
16. **Testing Strategy** — Unit tests (Mockito), controller tests (MockMvc)
17. **Resilience Patterns** — Rate limiting + circuit breaker + retry as defense layers
18. **BFS vs DFS** — When to use each, time/space complexity, graph vs tree traversal
19. **Java Multithreading** — Thread lifecycle, synchronized vs Lock, volatile vs Atomic, CompletableFuture
20. **Concurrency Utilities** — CountDownLatch vs CyclicBarrier, Semaphore, ConcurrentHashMap internals
21. **LeetCode Patterns** — Sliding window, two pointers, prefix sum, array manipulation, string processing
