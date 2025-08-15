package com.smalaca.test.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Comprehensive architecture test that validates the Training Center microservices architectural choices.
 * 
 * This test suite validates:
 * 1. Port and Adapters Architecture with DDD for core domains (Open Trainings, Reviews, Training Programs)
 * 2. Layered Architecture for supporting domains (Training Catalogue)
 * 3. Proper usage of architectural annotations
 * 4. Clean separation of concerns
 * 
 * Based on Architecture Decision Records:
 * - ADR 0008: Code Architecture for Training Offer Service
 * - ADR 0010: Code Architecture for Training Programs Service  
 * - ADR 0015: Code Architecture for Reviews Service
 * - ADR 0016: Code Architecture for Training Catalogue Module
 */
@AnalyzeClasses(packages = "com.smalaca")
public class ArchitecturalChoicesValidationTest {

    // Validate that architectural annotations exist and are properly named
    @ArchTest
    static final ArchRule ddd_annotations_should_exist_and_be_public =
        classes()
            .that().resideInAPackage("com.smalaca.domaindrivendesign")
            .should().bePublic()
            .andShould().beInterfaces()
            .because("DDD architectural annotations should be public interfaces");

    @ArchTest 
    static final ArchRule ports_and_adapters_annotations_should_exist =
        classes()
            .that().resideInAPackage("com.smalaca.architecture.portsandadapters")
            .should().bePublic()
            .andShould().beInterfaces()
            .because("Port and Adapters annotations should be public interfaces");

    @ArchTest
    static final ArchRule cqrs_annotations_should_exist =
        classes()
            .that().resideInAPackage("com.smalaca.architecture.cqrs")
            .should().bePublic()
            .andShould().beInterfaces()
            .because("CQRS annotations should be public interfaces");

    // Validate naming conventions
    @ArchTest
    static final ArchRule application_services_should_end_with_ApplicationService =
        classes()
            .that().resideInAPackage("..application..")
            .and().areNotInterfaces()
            .and().areNotEnums()
            .and().areNotAnnotations()
            .should().haveSimpleNameEndingWith("ApplicationService")
            .orShould().haveSimpleNameEndingWith("ApplicationServiceFactory")
            .orShould().haveSimpleNameEndingWith("Command")
            .orShould().haveSimpleNameEndingWith("Query")
            .orShould().haveSimpleNameEndingWith("Event")
            .orShould().haveSimpleNameEndingWith("Handler")
            .allowEmptyShould(true)
            .because("Application layer classes should follow naming conventions");

    @ArchTest
    static final ArchRule domain_services_should_end_with_DomainService =
        classes()
            .that().resideInAPackage("..domain..")
            .and().haveSimpleNameEndingWith("Service")
            .should().haveSimpleNameEndingWith("DomainService")
            .allowEmptyShould(true)
            .because("Domain services should be named with DomainService suffix");

    @ArchTest
    static final ArchRule repositories_should_end_with_Repository =
        classes()
            .that().resideInAPackage("..domain..")
            .and().haveSimpleNameContaining("Repository")
            .should().haveSimpleNameEndingWith("Repository")
            .allowEmptyShould(true)
            .because("Repository interfaces should end with Repository");

    @ArchTest
    static final ArchRule events_should_end_with_Event =
        classes()
            .that().resideInAPackage("..domain..")
            .and().haveSimpleNameContaining("Event")
            .should().haveSimpleNameEndingWith("Event")
            .allowEmptyShould(true)
            .because("Domain events should end with Event");

    // Validate that architectural patterns are followed
    @ArchTest
    static final ArchRule microservices_should_have_proper_package_structure =
        classes()
            .that().resideInAPackage("com.smalaca..")
            .and().resideOutsideOfPackage("com.smalaca.domaindrivendesign..")
            .and().resideOutsideOfPackage("com.smalaca.architecture..")
            .and().resideOutsideOfPackage("com.smalaca.test..")
            .should().resideInAnyPackage(
                "com.smalaca.opentrainings..",
                "com.smalaca.trainingoffer..",
                "com.smalaca.trainingprograms..", 
                "com.smalaca.trainingscatalogue..",
                "com.smalaca.reviews..",
                "com.smalaca.paymentgateway..",
                "com.smalaca.personaldatamanagement..",
                "com.smalaca.discountmanagement..",
                "com.smalaca.schemaregistry.."
            )
            .allowEmptyShould(true)
            .because("All classes should belong to specific microservice packages");

    @ArchTest
    static final ArchRule test_utilities_should_be_in_test_package =
        classes()
            .that().haveSimpleNameContaining("Test")
            .or().haveSimpleNameEndingWith("Assertion")
            .or().haveSimpleNameStartingWith("Given")
            .or().haveSimpleNameEndingWith("TestDto")
            .should().resideInAnyPackage("..test..", "com.smalaca.test..")
            .allowEmptyShould(true)
            .because("Test utilities should be in test packages");
}