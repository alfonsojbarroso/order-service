# Order Service Sequence Diagram

This diagram follows the current REST implementation for creating, listing,
and retrieving orders. The Kafka producer is not included in these request
flows because the REST controller does not call it.

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Controller as OrderController
    participant Service as OrderService
    participant DTO as OrderDto
    participant Model as OrderModel
    participant Repository as OrderRepository
    participant DB as PostgreSQL

    alt POST /orders
        Client->>Controller: POST /orders with order JSON
        Note over Controller,DTO: JSON binding constructs OrderDto; its constructor sets status to IN PROGRESS and createdAt to now
        Controller->>Service: save(orderDto)
        Service->>DTO: toModel()
        DTO-->>Service: OrderModel
        Service->>Repository: save(orderModel)
        Repository->>DB: INSERT order
        DB-->>Repository: Saved order with generated UUID
        Repository-->>Service: Saved OrderModel
        Service->>Model: toDto()
        Model->>DTO: Construct with persisted fields
        Note over DTO: DTO constructor again sets status to IN PROGRESS and createdAt to now
        DTO-->>Model: OrderDto
        Model-->>Service: OrderDto
        Service-->>Controller: OrderDto
        Controller-->>Client: 200 OK with JSON
        Note over Client,DTO: id and createdAt are excluded from JSON by @JsonIgnore
    else GET /orders
        Client->>Controller: GET /orders
        Controller->>Service: fetchAll()
        Service->>Repository: findAll()
        Repository->>DB: SELECT all orders
        DB-->>Repository: OrderModel list
        Repository-->>Service: OrderModel list
        loop Each order
            Service->>Model: toDto()
            Model->>DTO: Construct with persisted fields
            Note over DTO: DTO constructor sets status to IN PROGRESS and createdAt to now
            DTO-->>Model: OrderDto
            Model-->>Service: OrderDto
        end
        Service-->>Controller: List<OrderDto>
        Controller-->>Client: 200 OK with JSON array
    else GET /orders/by-id?id={id}
        Client->>Controller: GET /orders/by-id?id={id}
        Controller->>Service: findById(id.trim())
        Service->>Repository: findById(id)
        Repository->>DB: SELECT order by ID
        DB-->>Repository: OrderModel or no row
        Repository-->>Service: Optional<OrderModel>
        alt Order found
            Service->>Model: toDto()
            Model->>DTO: Construct with persisted fields
            Note over DTO: DTO constructor sets status to IN PROGRESS and createdAt to now
            DTO-->>Model: OrderDto
            Model-->>Service: OrderDto
            Service-->>Controller: OrderDto
            Controller-->>Client: 200 OK with JSON
        else Order not found
            Note over Service: Optional.get() throws NoSuchElementException; no not-found response is implemented here
        end
    end
```
