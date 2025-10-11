package com.smalaca.reviews.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.smalaca.reviews", importOptions = {ImportOption.DoNotIncludeTests.class})
public class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule layers = layeredArchitecture()
        .consideringOnlyDependenciesInLayers()
        .layer("application").definedBy("..application..")
        .layer("domain").definedBy("..domain..")
        .layer("infrastructure").definedBy("..infrastructure..")

        .whereLayer("application").mayOnlyAccessLayers("domain")
        .whereLayer("infrastructure").mayOnlyAccessLayers("application", "domain")
        .whereLayer("infrastructure").mayNotBeAccessedByAnyLayer();
}
