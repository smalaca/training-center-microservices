package com.smalaca.opentrainings.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.smalaca.opentrainings")
public class NoCyclesTest {
    @ArchTest
    static final ArchRule noCycles = slices()
        // Group by layer only to ensure no cyclic dependency between layers
        .matching("com.smalaca.opentrainings.(application|domain|infrastructure)..")
        .namingSlices("$1")
        .should().beFreeOfCycles();
}
