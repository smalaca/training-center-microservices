# 0017: CQRS Usage Across Modules

## Status

Accepted

## Date

2025-07-24

## Decision

Implement a system-wide CQRS (Command Query Responsibility Segregation) pattern across modules, where:
- Training Program and Training Offer modules serve as the command part, handling business logic and data modifications
- Training Catalogue module serves as the query part, focusing exclusively on data retrieval operations

## Context

* Our system consists of multiple modules that need to work together to provide a cohesive training management platform.
* We need to optimize for different operational requirements: complex business logic processing for training program and offer management versus efficient data retrieval for catalogue browsing.
* The system needs to maintain a clear separation of concerns between data modification operations and data retrieval operations.
* We want to leverage the benefits of CQRS while maintaining operational simplicity and clear architectural boundaries.
* The modules have different scaling requirements, with read operations typically requiring more scalability than write operations.

## Solutions

### Distributed CQRS Across Modules:
* Separate command and query responsibilities into different modules.
* Command modules (Training Program, Training Offer) handle business logic and data modifications.
* Query module (Training Catalogue) focuses exclusively on data retrieval operations.
* Use event-based communication to synchronize data between command and query modules.

### Internal CQRS Within Each Module:
* Each module implements its own CQRS pattern internally.
* Command and query responsibilities are separated within each module but not across modules.
* Each module maintains its own read and write models.

### Traditional Layered Architecture:
* Use the same modules for both command and query operations.
* No clear separation between read and write operations at the module level.
* Potentially less efficient for specialized operations but simpler to implement.

## Decision Rationale

* **Specialized Optimization** - By dedicating the Training Catalogue module to query operations, we can optimize it specifically for efficient data retrieval, while the Training Program and Training Offer modules can focus on complex business logic and data integrity.

* **Clear Separation of Concerns** - This approach provides a clear architectural boundary between command and query operations, making the system easier to understand, maintain, and evolve.

* **Scalability** - Query operations typically require more scalability than command operations. By separating these responsibilities into different modules, we can scale them independently according to their specific requirements.

* **Simplified Module Design** - Each module can be designed with a clearer focus: command modules focus on business rules and data consistency, while the query module focuses on efficient data retrieval and presentation.

* **Reduced Complexity Within Modules** - While the Training Program and Training Offer modules still implement internal CQRS for their specific domains, the Training Catalogue module can use a simpler layered architecture as it only handles query operations.

* **Event-Driven Architecture Alignment** - This approach aligns well with our event-driven architecture, where command modules publish events that are consumed by the query module to update its read models.

## Consequences

* **Data Flow** - Command modules (Training Program, Training Offer) publish events when data changes, which are consumed by the query module (Training Catalogue) to update its read models.

* **Consistency Model** - The system follows eventual consistency, where the query module may not immediately reflect changes made in command modules.

* **Module Responsibilities**:
  - Training Program module: Manages the lifecycle of training programs, including creation, review, and release.
  - Training Offer module: Manages training offers, including pricing, scheduling, and availability.
  - Training Catalogue module: Provides efficient read access to training programs and offers data for presentation purposes.

### Positive Risks and Considerations:

* Improved performance through specialized optimization of read and write operations.
* Enhanced scalability by allowing independent scaling of command and query modules.
* Better maintainability with clear separation of responsibilities across modules.
* Simplified module design with more focused responsibilities.
* Flexibility to evolve command and query modules independently.

### Negative Risks and Considerations:

* Eventual consistency means the Training Catalogue may not immediately reflect changes made in command modules.
* Increased operational complexity in managing multiple modules and their interactions.
* Need for careful event design to ensure all necessary data is propagated from command to query modules.
* Potential for data duplication across modules, requiring careful management of storage resources.
* Additional complexity in testing and debugging cross-module interactions.