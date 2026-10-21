# Order Service

Spring Boot service for creating and retrieving customer service orders. Orders
are persisted in PostgreSQL. The project uses Java 21 and Spring Boot 4.1.1.

## Technology Stack

- **Language/runtime:** Java 21
- **Application framework:** Spring Boot 4.1.1
- **REST API:** Spring WebFlux starter with annotated Spring REST controllers
- **Persistence:** Spring Data JPA and Hibernate
- **Database:** PostgreSQL
- **Messaging:** Spring for Apache Kafka; the producer serializes order DTOs as JSON
- **JSON:** Jackson
- **Build:** Maven, using the Maven Wrapper
- **Tests:** JUnit 5 with Spring Boot test support for JPA, Kafka, and WebFlux

The application follows a layered structure: the REST controller delegates to
the order service, which uses a Spring Data repository for persistence.

## Requirements

- Java 21
- PostgreSQL, with a database named `orders`
- Apache Kafka at `localhost:9092` if using the Kafka producer

The default configuration is in `src/main/resources/application.yaml`. It uses
port `8080`, connects to `jdbc:postgresql://localhost:5432/orders` as
`postgres` with password `root`, and sets Hibernate schema management to
`update`. Change these values for your environment before running the service.

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

## REST API

All endpoints use the `/orders` base path.

### Create an order

`POST /orders`

Request body:

```json
{
  "customerName": "Alex Morgan",
  "serviceDescription": "Replace a damaged screen"
}
```

The service assigns the initial status as `IN PROGRESS` and generates the
creation timestamp and database ID. Although the DTO accepts a `status` field,
the current implementation replaces it with the initial status. The response
contains `customerName`, `serviceDescription`, and `status`; `id` and
`createdAt` are excluded from JSON.

### List orders

`GET /orders`

Returns the orders as a JSON array. Each item includes `customerName`,
`serviceDescription`, and `status`.

### Find an order by ID

`GET /orders/by-id?id={id}`

Pass the order ID as the `id` query parameter. The current API excludes IDs
from JSON responses, so callers need to obtain the ID through another means
before using this endpoint.

## Order status

The status enum defines `IN PROGRESS`, `COMPLETED`, and `CANCELED`. The current
REST API only creates orders with `IN PROGRESS`; it does not expose an endpoint
to change an order's status.

## Kafka

`OrderProducerService` can publish an `OrderDto` to the
`service-requested-topic` topic. Kafka is configured at `localhost:9092`.
Publishing is not currently invoked by the REST controller's create endpoint;
creating an order through `POST /orders` saves it to PostgreSQL only.

## Tests

Run the test suite with:

```powershell
.\mvnw.cmd test
```

## Author

Dr. Alfonso José Barroso Barajas