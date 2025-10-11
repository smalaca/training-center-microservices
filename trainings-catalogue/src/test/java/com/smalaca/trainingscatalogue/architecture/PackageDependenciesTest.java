package com.smalaca.trainingscatalogue.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

class PackageDependenciesTest {
    private static final String BASE = "com.smalaca.trainingscatalogue";

    @Test
    void moduleShouldNotDependOnOtherModulesPackages() {
        JavaClasses classes = new ClassFileImporter().importPackages(BASE);

        ArchRuleDefinition.noClasses()
            .that().resideInAPackage(BASE + "..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "com.smalaca.opentrainings..",
                "com.smalaca.trainingprograms..",
                "com.smalaca.trainingoffer..",
                "com.smalaca.reviews..",
                "com.smalaca.discountmanagement..",
                "com.smalaca.paymentgateway..",
                "com.smalaca.personaldatamanagement.."
            )
            .because("Modules must communicate via defined interfaces or events (0003-event-driven-architecture.md)")
            .check(classes);
    }
}
