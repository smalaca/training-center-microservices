# 0004: Saga Orchestration for Offer Acceptance

## Status

Accepted

## Date

2024-12-17

## Decision

Use the Saga Orchestration pattern for implementing the offer acceptance process.

## Context

* The offer acceptance process involves multiple steps and services, including participant registration, offer validation, discount code application, and training place booking.
* The process needs to maintain data consistency across multiple services while allowing for independent scaling and deployment.
* If any step in the process fails, we need to ensure that the system remains in a consistent state through compensating actions.
* The process needs to be resilient to failures and support eventual consistency.

## Solutions

### Saga Choreography

* Implement the offer acceptance process as a choreographed saga where each service publishes events that trigger the next step in the process.
* Each service is responsible for its own part of the process and reacts to events from other services.
* The saga maintains its state and coordinates the process through event exchange.
* Compensating actions are triggered automatically when a step fails.

### Saga Orchestration

* Implement a central orchestrator service that coordinates the entire offer acceptance process.
* The orchestrator would call each service in sequence and handle failures by invoking compensating actions.
* The orchestrator would maintain the state of the entire process.

### Two-Phase Commit (2PC)

* Use a distributed transaction protocol to ensure atomicity across all services involved in the offer acceptance process.
* This would require all services to support the 2PC protocol and would introduce tight coupling.

## Decision Rationale

* **Centralized Control** - Orchestration provides a central coordinator that has visibility into the entire process, making it easier to track and manage the state of the saga.
* **Clear Process Flow** - The orchestrator defines a clear sequence of steps, making the process flow easier to understand and reason about.
* **Simplified Error Handling** - The orchestrator can detect failures and initiate appropriate compensating actions, centralizing error handling logic.
* **Reduced Complexity in Services** - Services don't need to know about the overall process or other services; they just need to respond to commands from the orchestrator.
* **Easier Monitoring and Debugging** - With a central orchestrator, it's easier to monitor the progress of a saga and diagnose issues when they occur.

## Consequences

* **Potential Bottleneck** - The orchestrator can become a bottleneck if not properly designed and scaled.
* **Single Point of Failure** - If the orchestrator fails, the entire process may be affected, requiring careful consideration of resilience patterns.
* **Eventual Consistency** - The system will still be eventually consistent, which may require additional consideration in the UI and user experience design.

### Positive Risks and Considerations

* The centralized nature of orchestration makes it easier to understand, monitor, and debug the process.
* The orchestrator provides a clear audit trail of all steps in the process, which can be valuable for troubleshooting and compliance.
* The orchestrator can implement more complex process flows and decision logic than would be practical in a choreographed approach.

### Negative Risks and Considerations

* The orchestrator needs to be carefully designed to avoid becoming a bottleneck or single point of failure.
* Changes to the process flow often require changes to the orchestrator, which can be more centralized than in a choreographed approach.