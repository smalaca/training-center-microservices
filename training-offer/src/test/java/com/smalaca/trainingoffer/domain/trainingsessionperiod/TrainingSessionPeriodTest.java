package com.smalaca.trainingoffer.domain.trainingsessionperiod;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class TrainingSessionPeriodTest {
    @Test
    void shouldFulfillEqualsContract() {
        EqualsVerifier.simple().forClass(TrainingSessionPeriod.class)
                .withNonnullFields("startDate", "endDate", "startTime", "endTime")
                .verify();
    }
}