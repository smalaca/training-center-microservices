package com.smalaca.reviews.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.smalaca.reviews")
public class NoCyclesTest {
    @ArchTest
    static final ArchRule noCycles = slices()
        .matching("com.smalaca.reviews.(domain|application|infrastructure).(*)..")
        .namingSlices("$1-$2")
        .should().beFreeOfCycles();
}
