# 0005: Saga Placement in Open Trainings Service

## Status

Accepted

## Date

2024-12-18

## Decision

Implement the Offer Acceptance Saga directly within the Open Trainings service rather than creating a separate dedicated saga coordination service.

## Context

* As described in ADR 0004, we've decided to use the Saga Orchestration pattern for implementing the offer acceptance process.
* The offer acceptance process involves multiple steps and services, including participant registration, offer validation, discount code application, and training place booking.
* We need to decide where to place the saga orchestrator: either within an existing service (Open Trainings) or as a new dedicated service.
* The orchestrator needs to coordinate actions across multiple services while maintaining a clear process flow and centralized error handling.

## Solutions

### Saga within Open Trainings Service

* Implement the Offer Acceptance Saga as part of the Open Trainings service.
* The Open Trainings service would be responsible for orchestrating the entire offer acceptance process.
* The saga would be tightly integrated with the Open Trainings domain model.
* Communication with other services would be done through well-defined interfaces.

### Dedicated Saga Service

* Create a new microservice specifically for managing the Offer Acceptance Saga.
* This service would be responsible solely for orchestrating the saga process.
* It would be independent of any specific domain service.
* It would communicate with all relevant services through well-defined interfaces.

## Decision Rationale

* **Domain Alignment** - The offer acceptance process is fundamentally a core business process within the Open Trainings domain. The saga coordinates actions that are primarily related to training offers, which are owned by the Open Trainings service.
* **Reduced Complexity** - Implementing the saga within the Open Trainings service reduces the overall architectural complexity by avoiding an additional microservice that would require its own deployment, monitoring, and maintenance.
* **Data Locality** - Many of the entities involved in the saga (offers, trainings) are already within the Open Trainings service, reducing the need for cross-service communication for these operations.
* **Pragmatic Approach** - At our current scale and complexity level, the benefits of a dedicated saga service do not outweigh the increased operational complexity it would introduce.

## Consequences

* **Increased Responsibility** - The Open Trainings service has increased responsibility and complexity by managing the saga orchestration.
* **Potential Coupling** - There's a risk of tighter coupling between the saga implementation and the Open Trainings service.
* **Service Size** - The Open Trainings service may grow larger than ideal for a microservice due to the additional saga orchestration logic.

### Positive Risks and Considerations

* Simpler deployment and operational model with one fewer microservice to manage.
* Reduced latency for saga operations related to Open Trainings entities.
* Clearer ownership of the entire offer acceptance process.
* Easier debugging and tracing of the offer acceptance workflow.

### Negative Risks and Considerations

* If the saga coordination logic grows significantly, it might make the Open Trainings service too large.
* The Open Trainings service might become a bottleneck if the saga processing requires significant resources.
* Future changes to the saga might impact the Open Trainings service even if they're not directly related to its core functionality.

## Mitigations

* We've implemented clear separation of concerns within the service, with the saga coordination logic in its own package structure.
* The saga is designed with proper interfaces that could enable extraction to a separate service if needed in the future.
* We're using event-driven communication which maintains loose coupling between services even though the saga coordinator is within Open Trainings.