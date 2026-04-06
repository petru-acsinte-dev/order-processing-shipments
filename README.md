# Order Processing: Shipments Microservice

This microservice is part of the [Order Processing](https://github.com/petru-acsinte-dev) 
portfolio project. It was extracted from the 
[monolith](https://github.com/petru-acsinte-dev/order-processing-monolith).

It manages order fulfillment and shipping. When notified by the orders service, 
it creates a fulfillment record and, once fulfilled, records the shipment and 
notifies the orders service that the order has shipped.

**REST API** — explore and test via Swagger UI at `http://localhost:8082/swagger-ui/index.html`

### Responsibilities
- Fulfillment record creation (triggered by orders service)
- Shipment recording
- Inter-service notification to orders on shipment completion

### Key Technologies
- Spring Boot 3.5 · Spring Security · PostgreSQL · Flyway · MapStruct · RabbitMQ · Testcontainers

### Related
- [order-processing-common](https://github.com/petru-acsinte-dev/order-processing-common) — shared library
- [order-processing-users](https://github.com/petru-acsinte-dev/order-processing-users)
- [order-processing-orders](https://github.com/petru-acsinte-dev/order-processing-orders)
- [User Story](https://github.com/petru-acsinte-dev/order-processing-monolith/blob/master/OrdersProcessor/docs/UserStory.md)
- [Design Document](https://github.com/petru-acsinte-dev/order-processing-monolith/blob/master/OrdersProcessor/docs/DesignDoc.md)
- [Development journal](docs/journal/daily-journal.md).
