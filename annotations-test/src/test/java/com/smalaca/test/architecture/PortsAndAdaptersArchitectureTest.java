package com.smalaca.test.architecture;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.architecture.portsandadapters.DrivingPort;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * ArchUnit tests to validate Port and Adapters architectural pattern.
 * Tests that ports and adapters are properly placed and follow hexagonal architecture rules.
 */
@AnalyzeClasses(packages = "com.smalaca")
public class PortsAndAdaptersArchitectureTest {

    @ArchTest
    static final ArchRule driving_ports_should_be_in_application_packages =
        classes()
            .that().areAnnotatedWith(DrivingPort.class)
            .should().resideInAPackage("..application..")
            .because("Driving ports should be part of the application layer");

    @ArchTest
    static final ArchRule driven_ports_should_be_in_application_packages =
        classes()
            .that().areAnnotatedWith(DrivenPort.class)
            .should().resideInAPackage("..application..")
            .because("Driven ports should be part of the application layer");

    @ArchTest
    static final ArchRule driving_adapters_should_be_in_infrastructure_packages =
        classes()
            .that().areAnnotatedWith(DrivingAdapter.class)
            .should().resideInAPackage("..infrastructure..")
            .orShould().resideInAPackage("..rest..")
            .orShould().resideInAPackage("..kafka..")
            .orShould().resideInAPackage("..web..")
            .because("Driving adapters should be part of the infrastructure layer");

    @ArchTest
    static final ArchRule driven_adapters_should_be_in_infrastructure_packages =
        classes()
            .that().areAnnotatedWith(DrivenAdapter.class)
            .should().resideInAPackage("..infrastructure..")
            .orShould().resideInAPackage("..repository..")
            .orShould().resideInAPackage("..kafka..")
            .orShould().resideInAPackage("..jpa..")
            .because("Driven adapters should be part of the infrastructure layer");

    @ArchTest
    static final ArchRule driving_ports_should_not_be_in_domain_packages =
        noClasses()
            .that().areAnnotatedWith(DrivingPort.class)
            .should().resideInAPackage("..domain..")
            .because("Driving ports should not be in domain packages");

    @ArchTest
    static final ArchRule driven_ports_should_not_be_in_domain_packages =
        noClasses()
            .that().areAnnotatedWith(DrivenPort.class)
            .should().resideInAPackage("..domain..")
            .because("Driven ports should not be in domain packages");

    @ArchTest
    static final ArchRule driving_adapters_should_not_be_in_domain_packages =
        noClasses()
            .that().areAnnotatedWith(DrivingAdapter.class)
            .should().resideInAPackage("..domain..")
            .because("Driving adapters should not be in domain packages");

    @ArchTest
    static final ArchRule driven_adapters_should_not_be_in_domain_packages =
        noClasses()
            .that().areAnnotatedWith(DrivenAdapter.class)
            .should().resideInAPackage("..domain..")
            .because("Driven adapters should not be in domain packages");

    @ArchTest
    static final ArchRule driving_adapters_should_not_be_in_application_packages =
        noClasses()
            .that().areAnnotatedWith(DrivingAdapter.class)
            .should().resideInAPackage("..application..")
            .because("Driving adapters should not be in application packages");

    @ArchTest
    static final ArchRule driven_adapters_should_not_be_in_application_packages =
        noClasses()
            .that().areAnnotatedWith(DrivenAdapter.class)
            .should().resideInAPackage("..application..")
            .because("Driven adapters should not be in application packages");
}