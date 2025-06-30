# 0012: CQRS Implementation for Open Trainings Service

## Status

Accepted

## Date

2024-12-30

## Decision

Implement CQRS (Command Query Responsibility Segregation) pattern within the Open Trainings service using a shared database approach with code-level separation between commands and queries.

## Context

* The Open Trainings service manages offers and orders as core business entities with complex read and write operations.
* We need to optimize read operations (queries) for presentation and reporting while maintaining transactional integrity for write operations (commands).
* The service requires clear separation of concerns between data modification and data retrieval operations.
* We want to maintain simplicity by avoiding the complexity of separate read and write databases while still gaining benefits of CQRS.
* The system needs to support different optimization strategies for reads versus writes without creating operational overhead.

## Solutions

### Shared Database CQRS:
* Use the same database tables for both read and write operations.
* Separate responsibilities at the code level with distinct packages and annotations.
* Domain entities handle write operations, view entities handle read operations.
* Both entity types map to the same database tables but serve different purposes.

### Separate Database CQRS:
* Maintain separate read and write databases.
* Synchronize data between databases using event-driven mechanisms.
* Allows independent scaling and optimization of read vs write operations.

### No CQRS Pattern:
* Use the same entities and repositories for both read and write operations.
* Simpler approach but less flexibility for optimization and separation of concerns.

## Decision Rationale

* **Simplicity with Benefits** - Shared database CQRS provides the architectural benefits of CQRS without the operational complexity of maintaining separate databases.
* **Clear Separation of Concerns** - Code-level separation between queries (read operations) and commands (write operations) improves maintainability and readability.
* **Optimized Query Models** - Query-specific view entities can be optimized for presentation without affecting the domain model.
* **Transactional Consistency** - Using the same database ensures ACID properties are maintained without complex synchronization mechanisms.
* **Performance Optimization** - Read operations can be optimized independently from write operations through different query strategies and caching.
* **Testability** - Clear separation makes it easier to test command and query operations independently.
* **Consistency with Architecture** - Aligns with the Port and Adapters architecture already implemented in the service.

## Consequences

* **Code Organization** - The service has distinct packages: `query` for read operations and `application`/`domain` for write operations.
* **Annotation-Based Separation** - Uses `@QueryOperation` and `@CommandOperation` annotations to clearly mark the separation.
* **Dual Entity Mapping** - Domain entities (`Offer`, `Order`) and view entities (`OfferView`, `OrderView`) both map to the same database tables (`OFFERS`, `ORDERS`).
* **Repository Separation** - Separate repository interfaces for command operations (`OfferRepository`, `OrderRepository`) and query operations (`OfferViewRepository`, `OrderViewRepository`).

### Positive Risks and Considerations:

* Improved query performance through optimized view models and queries.
* Enhanced maintainability with clear separation between read and write responsibilities.
* Better testability of individual operations.
* Flexibility to evolve read and write models independently.
* Foundation for future migration to separate databases if needed.

### Negative Risks and Considerations:

* Potential code duplication between domain entities and view entities.
* Need to maintain mapping consistency between entities that share tables.
* Additional complexity in understanding which entity to use for specific operations.
* Risk of accidentally using wrong entity type for operations.