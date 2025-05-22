# 0005: No Layers Architecture for Training Portfolio Module

## Status

Accepted

## Date

2025-05-19

## Decision

Use a simple, non-layered architecture for the Training Portfolio module.

## Context

* We are developing the Training Portfolio module, which is responsible for providing a read-optimized view of training programs.
* This module consumes events from the Training Management module and maintains a read model of training programs.
* The module provides REST APIs for retrieving training program data with support for pagination, filtering, and sorting.
* As part of our CQRS implementation, this module focuses solely on the query (read) side of the application.

## Solutions

### Layered Architecture

* Organize the code into layers (e.g., presentation, application, domain, infrastructure).
* Each layer has a specific responsibility and depends on the layers below it.
* Provides clear separation of concerns but may introduce unnecessary complexity for simple read models.

### Port and Adapter Architecture (Hexagonal Architecture)

* Isolate the domain logic from external concerns using ports (interfaces) and adapters (implementations).
* Provides excellent isolation of the domain but may be overkill for a simple read model.

### No Layers Architecture

* Organize the code in a flat structure without explicit layers.
* Components interact directly with each other.
* Simpler to implement and understand for straightforward use cases.
* May be less maintainable for complex domains but suitable for simple read models.

## Decision Rationale

* **Simplicity** - The Training Portfolio module has a straightforward responsibility: maintaining and providing access to a read model. A non-layered architecture keeps the implementation simple and easy to understand.
* **Read-Only Nature** - As a read-only module, the business logic is minimal, primarily focusing on data retrieval and transformation. Complex architectural patterns designed for rich domain logic are unnecessary.
* **Event-Driven Updates** - The read model is updated based on events from the Training Management module, not through direct commands. This simplifies the module's responsibilities and reduces the need for complex architectural patterns.
* **Alignment with CQRS** - In our CQRS implementation, we've deliberately separated the complex command (write) logic in the Training Management module from the simpler query (read) logic in the Training Portfolio module. This separation allows us to use a more complex architecture where needed (in the write model) and a simpler one where appropriate (in the read model).
* **Reduced Overhead** - A non-layered architecture reduces the overhead of maintaining abstractions, interfaces, and layer boundaries, leading to faster development and easier maintenance for this specific module.

## Consequences

* **Reduced Complexity** - A simpler architecture leads to less code, fewer abstractions, and easier understanding of the codebase.
* **Faster Development** - With fewer architectural constraints, development can proceed more quickly.
* **Potential for Tight Coupling** - Without explicit layer boundaries, there's a risk of components becoming tightly coupled, which could make future changes more difficult.

### Positive Risks and Considerations

* Simplified codebase that is easier to understand and maintain.
* Faster development and iteration.
* Reduced cognitive load for developers working on the module.
* Better alignment with the module's straightforward responsibilities.

### Negative Risks and Considerations

* Risk of tight coupling between components if not carefully designed.
* Potential difficulty in extending the module with complex features in the future.
* May be less familiar to developers accustomed to more structured architectures.