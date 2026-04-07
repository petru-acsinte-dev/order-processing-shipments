# Order Processing: Shipments Microservice

This microservice is part of the [Order Processing](https://github.com/petru-acsinte-dev) 
portfolio project. It was extracted from the 
[monolith](https://github.com/petru-acsinte-dev/order-processing-monolith).

It manages order fulfillment and shipping. When notified by the orders service, 
it creates a fulfillment record and, once fulfilled, records the shipment and 
notifies the orders service that the order has shipped.

**REST API** — explore and test via Swagger UI:
- Local: `http://localhost:8082/shipments/swagger-ui/index.html`
- Live: `http://52.205.87.85/shipments/swagger-ui/index.html`

### Responsibilities
- Consumes `OrderConfirmedEvent` from RabbitMQ to create fulfillment records
- Publishes `OrderShippedEvent` to RabbitMQ on shipment completion
- Shipment recording

### Key Technologies
- Spring Boot 3.5 · Spring Security · PostgreSQL · Flyway · MapStruct · RabbitMQ · Testcontainers

### Known Limitations
- Transactional Outbox pattern not yet implemented — if RabbitMQ publish fails after shipment is committed, the orders service will not be notified. Tracked in [issue #2](https://github.com/petru-acsinte-dev/order-processing-shipments/issues/2).

### Related
- [order-processing-common](https://github.com/petru-acsinte-dev/order-processing-common) — shared library
- [order-processing-users](https://github.com/petru-acsinte-dev/order-processing-users)
- [order-processing-orders](https://github.com/petru-acsinte-dev/order-processing-orders)
- [User Story](https://github.com/petru-acsinte-dev/order-processing-monolith/blob/master/OrdersProcessor/docs/UserStory.md)
- [Design Document](https://github.com/petru-acsinte-dev/order-processing-monolith/blob/master/OrdersProcessor/docs/DesignDoc.md)
- [Development journal — monolith phase](https://github.com/petru-acsinte-dev/order-processing-monolith/blob/master/OrdersProcessor/docs/journal/daily-journal.md)
- [Development journal](docs/journal/daily-journal.md)
