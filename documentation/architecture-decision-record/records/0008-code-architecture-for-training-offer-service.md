# 0008:  Code Architecture for Training Offer Service

## Status

Accepted

## Date

2024-12-20

## Decision

Use Port and Adapters Architecture with the usage of Tactical Domain-Driven Design (DDD) patterns.

```mermaid
flowchart TB
    subgraph "Training Offer Service"
        subgraph "Domain Layer"
            AGG[Aggregates]
            VO[Value Objects]
            DOM_SVC[Domain Services]
            
            AGG --- VO
            AGG --- DOM_SVC
        end
        
        subgraph "Application Layer"
            APP_SVC[Application Services]
            PORTS_OUT[Output Ports]
            PORTS_IN[Input Ports]
            
            APP_SVC --> PORTS_OUT
            PORTS_IN --> APP_SVC
            APP_SVC --> AGG
        end
        
        subgraph "Infrastructure Layer"
            REST_ADPT[REST Adapter]
            KAFKA_ADPT[Kafka Adapter]
            REPO_ADPT[Repository Adapter]
            DB[(Database)]
            
            REST_ADPT --> PORTS_IN
            KAFKA_ADPT --> PORTS_IN
            PORTS_OUT --> REPO_ADPT
            REPO_ADPT --> DB
        end
    end
    
    Client[Client] --> REST_ADPT
    Events[Event Bus / Kafka] --> KAFKA_ADPT
    
    classDef domain fill:#f9d,stroke:#333,stroke-width:2px
    classDef application fill:#adf,stroke:#333,stroke-width:2px
    classDef infrastructure fill:#ad9,stroke:#333,stroke-width:2px
    classDef external fill:#ddd,stroke:#333,stroke-width:1px
    
    class AGG,VO,DOM_SVC domain
    class APP_SVC,PORTS_OUT,PORTS_IN application
    class REST_ADPT,KAFKA_ADPT,REPO_ADPT,DB infrastructure
    class Client,Events external
```

## Context

* We are developing a service responsible for managing training offers, including creation, publication, and management of training offers.
* This service is responsible for the business logic of one of our core domains, as it directly impacts our ability to create and manage training offerings.
* We need to choose a code architecture that promotes maintainability, testability, and flexibility in the face of evolving business requirements.

## Solutions

### Port and Adapters Architecture with DDD:
* Isolate the domain logic from external concerns (databases, message queues, etc.) using ports and adapters.
* Utilize DDD patterns (e.g., Aggregates, Value Objects, Repositories) to model the domain effectively and ensure high cohesion and low coupling.

### Port and Adapters Architecture without DDD:
* Isolate the domain logic, but without the specific benefits of DDD patterns for domain modeling.

### Layered Architecture:
* Organize the code into layers (e.g., presentation, application, domain, infrastructure).
* May lead to tighter coupling between layers compared to port and adapters.

### No Architecture:
* Proceed with development without a defined architectural style.
* Can lead to technical debt and increased maintenance costs in the long run.

## Decision Rationale

* **Core Domain Importance** - As this is a core domain, it's crucial to have a robust architecture that facilitates maintainability, extensibility, and the ability to adapt to changing business needs.
* **Domain Isolation** - Port and Adapters architecture effectively isolates the core domain logic from external concerns, making it easier to test, maintain, and evolve independently.
* **DDD for Domain Modeling** - Utilizing DDD patterns helps to create a rich and expressive domain model, improving code clarity, reducing complexity, and facilitating better communication within the development team.
* **Encapsulation** - DDD patterns promote encapsulation of domain logic, making it less susceptible to changes in external dependencies.
* **Consistency with Other Services** - Using the same architectural approach as the Open Trainings service ensures consistency across our codebase, making it easier for developers to work across different services.

## Consequences

* **Increased initial development time**: Implementing DDD patterns might require a slightly steeper learning curve and potentially more upfront design effort.
* **Potential for over-engineering**: There's a risk of over-engineering the domain model if DDD patterns are not applied judiciously.

### Positive Risks and Considerations:

* Improved code quality, maintainability, and testability.
* Enhanced flexibility and adaptability to changing requirements.
* Improved communication and collaboration within the development team.
* Consistency with other services in the system.

### Negative Risks and Considerations:

* Potential for increased complexity if not implemented correctly.
* Risk of over-engineering the domain model.