package com.smalaca.test.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Simple ArchUnit test to verify the framework is working correctly.
 */
@AnalyzeClasses(packages = "com.smalaca")
public class SimpleArchitectureTest {

    @ArchTest
    static final ArchRule annotations_should_be_public =
        classes()
            .that().areAnnotations()
            .should().bePublic()
            .because("Architecture annotations should be public");

    @ArchTest
    static final ArchRule ddd_annotations_should_exist =
        classes()
            .that().resideInAPackage("com.smalaca.domaindrivendesign")
            .should().beInterfaces()
            .because("DDD package should contain annotation interfaces");
}