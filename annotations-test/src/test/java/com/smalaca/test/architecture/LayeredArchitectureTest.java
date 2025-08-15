package com.smalaca.test.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Comprehensive ArchUnit test suite to validate layered architecture patterns.
 * These tests can be extended as microservices are added to the classpath.
 */
@AnalyzeClasses(packages = "com.smalaca")
public class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule domain_layer_should_not_depend_on_springframework =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework..")
            .allowEmptyShould(true)
            .because("Domain layer should not depend on Spring framework");

    @ArchTest
    static final ArchRule domain_layer_should_not_depend_on_jpa =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("javax.persistence..")
            .orShould().dependOnClassesThat().resideInAPackage("jakarta.persistence..")
            .allowEmptyShould(true)
            .because("Domain layer should not depend on JPA");

    @ArchTest
    static final ArchRule domain_layer_should_not_depend_on_rest =
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework.web..")
            .orShould().dependOnClassesThat().resideInAPackage("javax.ws.rs..")
            .orShould().dependOnClassesThat().resideInAPackage("jakarta.ws.rs..")
            .allowEmptyShould(true)
            .because("Domain layer should not depend on REST frameworks");

    @ArchTest
    static final ArchRule application_layer_should_not_depend_on_web_mvc =
        noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework.web..")
            .allowEmptyShould(true)
            .because("Application layer should not depend on web frameworks directly");

    @ArchTest
    static final ArchRule entities_should_not_depend_on_services =
        noClasses()
            .that().haveSimpleNameEndingWith("Entity")
            .should().dependOnClassesThat().haveSimpleNameEndingWith("Service")
            .allowEmptyShould(true)
            .because("Entities should not depend on services");

    @ArchTest
    static final ArchRule repositories_should_be_interfaces =
        classes()
            .that().haveSimpleNameEndingWith("Repository")
            .and().resideInAPackage("..domain..")
            .should().beInterfaces()
            .allowEmptyShould(true)
            .because("Domain repositories should be interfaces");

    @ArchTest
    static final ArchRule factories_should_be_in_domain =
        classes()
            .that().haveSimpleNameEndingWith("Factory")
            .and().areNotAnnotations()
            .should().resideInAPackage("..domain..")
            .allowEmptyShould(true)
            .because("Factories should be in domain layer");

    @ArchTest
    static final ArchRule specifications_should_be_in_domain =
        classes()
            .that().haveSimpleNameEndingWith("Specification")
            .and().areNotAnnotations()
            .should().resideInAPackage("..domain..")
            .allowEmptyShould(true)
            .because("Specifications should be in domain layer");

    @ArchTest
    static final ArchRule controllers_should_only_access_application_layer =
        classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().onlyAccessClassesThat()
            .resideInAnyPackage(
                "..application..",
                "..domain..",
                "java..",
                "org.springframework..",
                "com.smalaca..",
                "org.slf4j..",
                "lombok.."
            )
            .allowEmptyShould(true)
            .because("Controllers should primarily access application layer");

    @ArchTest
    static final ArchRule events_should_be_immutable =
        classes()
            .that().haveSimpleNameEndingWith("Event")
            .and().resideInAPackage("..domain..")
            .should().haveOnlyFinalFields()
            .allowEmptyShould(true)
            .because("Domain events should be immutable");
}