package com.smalaca.test.architecture;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.domaindrivendesign.DomainEntity;
import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.domaindrivendesign.DomainRepository;
import com.smalaca.domaindrivendesign.DomainService;
import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.domaindrivendesign.ValueObject;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * ArchUnit tests to validate Domain-Driven Design architectural choices.
 * Tests that DDD annotations are properly used and domain layer rules are followed.
 */
@AnalyzeClasses(packages = "com.smalaca")
public class DomainDrivenDesignArchitectureTest {

    @ArchTest
    static final ArchRule domain_entities_should_be_in_domain_packages =
        classes()
            .that().areAnnotatedWith(DomainEntity.class)
            .should().resideInAPackage("..domain..")
            .allowEmptyShould(true)
            .because("Domain entities should reside in domain packages");

    @ArchTest
    static final ArchRule aggregate_roots_should_be_in_domain_packages =
        classes()
            .that().areAnnotatedWith(AggregateRoot.class)
            .should().resideInAPackage("..domain..")
            .allowEmptyShould(true)
            .because("Aggregate roots should reside in domain packages");

    @ArchTest
    static final ArchRule value_objects_should_be_in_domain_packages =
        classes()
            .that().areAnnotatedWith(ValueObject.class)
            .should().resideInAPackage("..domain..")
            .allowEmptyShould(true)
            .because("Value objects should reside in domain packages");

    @ArchTest
    static final ArchRule domain_services_should_be_in_domain_packages =
        classes()
            .that().areAnnotatedWith(DomainService.class)
            .should().resideInAPackage("..domain..")
            .allowEmptyShould(true)
            .because("Domain services should reside in domain packages");

    @ArchTest
    static final ArchRule domain_repositories_should_be_in_domain_packages =
        classes()
            .that().areAnnotatedWith(DomainRepository.class)
            .should().resideInAPackage("..domain..")
            .allowEmptyShould(true)
            .because("Domain repositories should reside in domain packages");

    @ArchTest
    static final ArchRule domain_events_should_be_in_domain_packages =
        classes()
            .that().areAnnotatedWith(DomainEvent.class)
            .should().resideInAPackage("..domain..")
            .allowEmptyShould(true)
            .because("Domain events should reside in domain packages");

    @ArchTest
    static final ArchRule factories_should_be_in_domain_packages =
        classes()
            .that().areAnnotatedWith(Factory.class)
            .should().resideInAPackage("..domain..")
            .allowEmptyShould(true)
            .because("Factories should reside in domain packages");

    @ArchTest
    static final ArchRule application_services_should_be_in_application_packages =
        classes()
            .that().areAnnotatedWith(ApplicationLayer.class)
            .should().resideInAPackage("..application..")
            .allowEmptyShould(true)
            .because("Application services should reside in application packages");

    @ArchTest
    static final ArchRule domain_packages_should_not_depend_on_application_packages =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..application..")
            .allowEmptyShould(true)
            .because("Domain layer should not depend on application layer");

    @ArchTest
    static final ArchRule domain_packages_should_not_depend_on_infrastructure_packages =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .allowEmptyShould(true)
            .because("Domain layer should not depend on infrastructure layer");

    @ArchTest
    static final ArchRule application_packages_should_not_depend_on_infrastructure_packages =
        noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .allowEmptyShould(true)
            .because("Application layer should not depend on infrastructure layer");
}