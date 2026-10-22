# Order Service

Spring Boot service for creating and retrieving customer service orders. It
persists orders in PostgreSQL and publishes order requests to Kafka. Redis is
used to track idempotency keys.

## Technology Stack

- **Language/runtime:** Java 21
- **Application framework:** Spring Boot 4.1.1
- **REST API:** Spring WebFlux with annotated controllers
- **API documentation:** springdoc OpenAPI 3.0.0 with Swagger UI
- **Persistence:** Spring Data JPA and Hibernate
- **Database:** PostgreSQL
- **Messaging:** Spring Kafka
- **Idempotency:** Redis
- **Build:** Maven, using the Maven Wrapper
- **Tests:** JUnit 5 with Spring Boot test support

The application follows a layered structure: the REST controller delegates to
the order service, which coordinates persistence, idempotency, and event
publishing.

## Requirements

- Java 21
- PostgreSQL, with a database named `orders`
- Redis at `localhost:6379`
- Apache Kafka at `localhost:9092`

For local development, the following Docker commands start Kafka and Redis:

```powershell
docker run -d --name kafka-broker -p 9092:9092 apache/kafka:latest
docker run -d --name redis -p 6379:6379 redis:latest
```

The default configuration is in `src/main/resources/application.yaml`. The
service listens on port `8080` and connects to
`jdbc:postgresql://localhost:5432/orders` as `postgres` with password `root`.
Hibernate schema management is set to `update`. Kafka uses the
`service-requested-topic` topic, and Redis uses its default local connection.
Change these local-development settings for your environment; do not use the
sample database credentials in production.

## Run

From the project directory, run:

```powershell
.\mvnw.cmd spring-boot:run
```

Or, from Bash, use the included script to run with the `dev` profile:

```bash
./run.sh
```

The service starts at `http://localhost:8080`.

## Run with Docker

Build and start the image from the project directory:

```powershell
docker build -t order-service .
docker run --rm -p 8080:8080 order-service
```

Configure the datasource, Kafka, and Redis Spring properties for your
environment when running the container. The default `localhost` addresses in
`application.yaml` refer to the container itself, not services running on the
host.

## REST API

All endpoints use the `/orders` base path.

### Create an order

`POST /orders`

The request requires a nonblank `idempotency-key` header and a JSON body:

```http
idempotency-key: 47d01d7e-9909-47c2-9f0b-9fe9b5aab9bc
Content-Type: application/json
```

```json
{
  "customerId": 1,
  "customerName": "Alex Morgan",
  "serviceDescription": "Replace a damaged screen"
}
```

`customerId` must be 1 or greater; `customerName` and `serviceDescription`
must not be blank. The service assigns the initial status as `PROCESSING` and
generates an ID and creation time. Request fields for `id`, `status`, and
`createdAt` are ignored.

On success, the endpoint returns `200 OK` with an order response:

```json
{
  "id": "generated-order-uuid",
  "customerId": 1,
  "customerName": "Alex Morgan",
  "serviceDescription": "Replace a damaged screen",
  "status": "PROCESSING",
  "createdAt": "2026-10-22T12:50:25.461"
}
```

Invalid or missing required values return `400 Bad Request`. A request with an
idempotency key already marked as processing returns `425 Too Early`.

### List orders

`GET /orders`

Returns the orders as a JSON array. Each item has the same shape as the order
response shown above.

### Find an order by ID

`GET /orders/by-id?id={id}`

Pass the order UUID as the `id` query parameter. For example:

```text
GET /orders/by-id?id=generated-order-uuid
```

## Order status and timestamps

The status enum defines `PROCESSING`, `COMPLETED`, and `CANCELED`. The create
endpoint sets new orders to `PROCESSING`; there is no endpoint to change the
status.

Currently, response DTO construction resets the status to `PROCESSING` and the
timestamp to the time the response is mapped. As a result, list and lookup
responses do not reflect a different persisted status or the original
`createdAt` value.

## Idempotency and Kafka

The service uses Redis to acquire an idempotency key for 24 hours. After an
order is saved, it publishes the `OrderRequestDto` to Kafka's
`service-requested-topic`, using the customer ID as the Kafka record key and
including the idempotency key in the record headers.

**Current limitation:** the idempotency response is not saved after order
creation. The Redis key remains marked `PROCESSING` until it expires, so a
retry using the same key during that period returns `425 Too Early` rather
than the original order response.

## API Documentation

Once the service is running, open Swagger UI at:

```text
http://localhost:8080/swagger-ui.html
```

The generated OpenAPI JSON is available at:

```text
http://localhost:8080/v3/api-docs
```

## Tests

Run the test suite with:

```powershell
.\mvnw.cmd test
```

## Author

Dr. Alfonso José Barroso Barajas
