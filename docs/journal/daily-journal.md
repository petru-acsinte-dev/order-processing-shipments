OrderProcessor - Shipments microservice - Daily journal
=

2026-03-26
-
- created new GitHub repository for shipments
- created Spring Boot project stub for shipments with dependencies matching the monolith, plus the new common library
- ported fulfillment and shipment classes from the monolith project: dao, entities, repos, service, controllers, configurations, mappers, props, exceptions, constants
- reworked the monolith Flyway scripts into a fresh, clean fulfillments and shipments initialization script
- added fulfillment creation capability to fulfillment controller (to be called by orders Feign client when orders are confirmed)
