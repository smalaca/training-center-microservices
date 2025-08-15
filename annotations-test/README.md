# ArchUnit Architecture Tests

This module contains ArchUnit tests that validate the architectural choices used in the Training Center microservices project.

## Purpose

These tests ensure that all microservices follow the documented architectural patterns:

- **Port and Adapters Architecture with DDD** for core domains (Open Trainings, Reviews, Training Programs)
- **Layered Architecture** for supporting domains (Training Catalogue)
- **Proper usage of architectural annotations**
- **Clean separation of concerns**

## Architecture Decision Records

The tests validate compliance with these Architecture Decision Records:
- ADR 0008: Code Architecture for Training Offer Service
- ADR 0010: Code Architecture for Training Programs Service  
- ADR 0015: Code Architecture for Reviews Service
- ADR 0016: Code Architecture for Training Catalogue Module

## Test Categories

### 1. Domain-Driven Design Architecture Tests (`DomainDrivenDesignArchitectureTest`)

Validates:
- DDD annotations are placed in correct packages (`..domain..`)
- Application layer components are in `..application..` packages
- Domain layer doesn't depend on application or infrastructure layers
- Application layer doesn't depend on infrastructure layer

### 2. Port and Adapters Architecture Tests (`PortsAndAdaptersArchitectureTest`)

Validates:
- Driving and Driven Ports are in application layer
- Driving and Driven Adapters are in infrastructure layer
- Ports and Adapters don't violate layer boundaries

### 3. Layered Architecture Tests (`LayeredArchitectureTest`)

Validates:
- Domain layer independence from frameworks (Spring, JPA, REST)
- Proper naming conventions for architectural components
- Clean dependency rules between layers
- Immutability of domain events

### 4. Architectural Choices Validation (`ArchitecturalChoicesValidationTest`)

Validates:
- Architectural annotations exist and are properly defined
- Naming conventions are followed
- Package structure matches microservice boundaries
- Test utilities are properly organized

### 5. Simple Architecture Tests (`SimpleArchitectureTest`)

Basic validation that:
- Architectural annotations are public
- DDD package contains proper interfaces

## Running the Tests

```bash
# Run all architecture tests
mvn test -pl annotations-test

# Run specific test class
mvn test -pl annotations-test -Dtest=DomainDrivenDesignArchitectureTest

# Run tests with verbose output
mvn test -pl annotations-test -X
```

## Adding New Microservices

When adding new microservices to the classpath for testing:

1. Add the microservice as a test dependency in `pom.xml`
2. Ensure the microservice follows the established architectural patterns
3. Run the tests to validate compliance
4. Fix any violations or adjust rules if architectural decisions change

## Extending the Tests

To add new architectural rules:

1. Create new test methods with `@ArchTest` annotation
2. Use ArchUnit's fluent API to define rules
3. Add `allowEmptyShould(true)` if rules might not match any classes
4. Provide clear `because()` explanations for rule purposes

## Benefits

- **Continuous Architecture Validation**: Automatically checks architectural compliance
- **Documentation**: Tests serve as executable documentation of architectural decisions
- **Refactoring Safety**: Prevents accidental violations during code changes
- **Team Alignment**: Ensures all developers follow the same architectural patterns
- **Quality Gates**: Can be integrated into CI/CD pipelines to prevent architectural drift

## ArchUnit Version

This project uses ArchUnit 1.2.1, which is compatible with Java 17.

## Further Reading

- [ArchUnit Documentation](https://www.archunit.org/)
- [Training Center Architecture Decision Records](../documentation/architecture-decision-record/records/)
- [Port and Adapters Pattern](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)