# 0016: Code Architecture for Training Catalogue Module

## Status

Accepted

## Date

2025-07-24

## Decision

Use Layered Architecture for the Training Catalogue module.

## Context

* We are developing a module responsible for maintaining a catalog of training programs and training offers.
* This module primarily serves as a data repository with no business logic.
* The main responsibilities include storing and retrieving training data, and processing events from other services.
* We need to choose a code architecture that balances simplicity with maintainability.

## Solutions

### Layered Architecture:
* Organize the code into distinct layers (presentation, business logic, data access).
* Each layer has a specific responsibility and communicates only with adjacent layers.
* Simple to understand and implement, with clear separation of concerns.

### Port and Adapters Architecture (Hexagonal):
* Isolate the domain logic from external concerns using ports and adapters.
* Domain logic is completely independent of infrastructure concerns.
* Requires more interfaces and abstractions to achieve this isolation.

### No Architecture:
* Proceed with development without a defined architectural style.
* Can lead to technical debt and increased maintenance costs in the long run.

## Decision Rationale

* **Simplicity** - The Training Catalogue module has relatively straightforward business logic, primarily focused on storing and retrieving data. A layered architecture provides sufficient structure without unnecessary complexity.

* **Appropriate for the Domain** - Unlike core domains like Open Trainings or Reviews that have complex business rules, the Training Catalogue is more of a supporting domain that primarily serves as a data repository. The additional complexity of ports and adapters would not provide significant benefits.

* **Development Efficiency** - Layered architecture allows for faster development with fewer abstractions, which is appropriate for this module's scope and complexity.

* **Maintainability** - Despite being simpler than ports and adapters, layered architecture still provides good separation of concerns, making the code maintainable and testable.

* **Integration Requirements** - The module primarily integrates with other services through Kafka events and REST APIs, which are well-supported by a layered architecture.

## Comparison with Other Modules

* **Core Domains (Open Trainings, Reviews)** - These modules use Port and Adapters with DDD because they contain complex business logic and rules that benefit from domain isolation and rich modeling.

* **Training Catalogue** - Uses Layered Architecture because it's primarily a data catalog with no business logic, where the benefits of more complex architectures don't justify the additional complexity.

## Consequences

* **Faster Development** - Layered architecture allows for quicker implementation with fewer abstractions.
* **Simpler Codebase** - Easier to understand and maintain for new team members.

### Positive Risks and Considerations:

* Sufficient separation of concerns for testability and maintainability.
* Appropriate level of complexity for the module's requirements.
* Faster development and onboarding.

### Negative Risks and Considerations:

* Potential for tighter coupling between layers compared to ports and adapters.
* May be more difficult to change infrastructure components (e.g., database) without affecting other layers.
* Less isolation of domain logic from external concerns.