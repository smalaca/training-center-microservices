package com.smalaca.trainingprograms.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.smalaca.trainingprograms")
public class NoCyclesTest {
    @ArchTest
    static final ArchRule noCycles = slices()
        .matching("com.smalaca.trainingprograms.(application|domain|infrastructure)..")
        .namingSlices("$1")
        .should().beFreeOfCycles();
}
