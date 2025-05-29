# 0005: Event Sourcing for Offer Acceptance Saga

## Status

Accepted

## Date

2024-12-18

## Decision

Implement Event Sourcing pattern for the Offer Acceptance Saga to maintain the complete history of state changes and enable reliable reconstruction of saga state.

## Context

* The offer acceptance process is implemented as a saga orchestration pattern that coordinates multiple steps across different services.
* The saga needs to maintain its state across multiple events and be able to recover from failures.
* We need to track the complete history of the saga execution for auditing, debugging, and compliance purposes.
* The saga state needs to be reconstructed reliably in case of system failures or restarts.
* The process involves multiple events like offer acceptance requests, person registration, training place booking, and discount code application.

## Solutions

### Event Sourcing

* Store all state changes as a sequence of events in an event store.
* Reconstruct the current state by replaying all events from the beginning.
* Use the events as the source of truth for the saga state.
* Maintain a complete history of all events that have affected the saga.

### Traditional State Storage

* Store only the current state of the saga in a database.
* Update the state directly when changes occur.
* No historical record of how the state evolved over time.
* Simpler to implement but lacks the benefits of a complete event history.

### Checkpointing with Partial History

* Store periodic snapshots of the saga state along with recent events.
* Reconstruct the current state by starting from the latest snapshot and applying subsequent events.
* A compromise between full event sourcing and traditional state storage.

## Decision Rationale

* **Complete Audit Trail** - Event sourcing provides a complete history of all events that have affected the saga, which is valuable for auditing, debugging, and compliance purposes.
* **Reliable State Reconstruction** - The saga state can be reliably reconstructed by replaying events, which is crucial for recovery from failures.
* **Temporal Query Capability** - The event history allows us to query the state of the saga at any point in time, which is useful for debugging and analysis.
* **Natural Fit with Saga Pattern** - The saga pattern already deals with a sequence of events and commands, making event sourcing a natural fit.
* **Consistency with Event-Driven Architecture** - Event sourcing aligns well with our event-driven architecture and complements other patterns like CQRS.

## Consequences

* **Increased Complexity** - Implementing event sourcing adds complexity to the system, particularly in terms of event schema evolution and handling.
* **Performance Considerations** - Reconstructing state by replaying events can be resource-intensive, especially for long-running sagas with many events.
* **Event Schema Evolution** - As the system evolves, we need to carefully manage changes to event schemas to ensure backward compatibility.

### Positive Risks and Considerations

* The complete event history provides valuable insights into the execution of the saga and can help identify patterns and optimize the process.
* Event sourcing naturally supports the implementation of temporal queries and historical analysis.
* The approach aligns well with our existing event-driven architecture and complements other patterns like CQRS and the outbox pattern.

### Negative Risks and Considerations

* The event store can grow large over time, requiring strategies for managing storage and performance.
* Developers need to understand the event sourcing pattern and its implications, which may require training and adjustment.
* Debugging can be more complex as it involves understanding the sequence of events that led to a particular state.

## Implementation Details

In our implementation of the Offer Acceptance Saga:

* Each saga instance maintains a list of consumed events with their timestamps.
* Events are stored in the `events` list within the `OfferAcceptanceSaga` class.
* The `consumed` method records each event along with its timestamp.
* The `load` method allows reconstructing the saga state by replaying events.
* The `readLastEvent` method provides access to the most recent event, which is useful for debugging and monitoring.

This implementation ensures that we have a complete history of all events that have affected each saga instance, allowing for reliable state reconstruction and providing a valuable audit trail for debugging and compliance purposes.