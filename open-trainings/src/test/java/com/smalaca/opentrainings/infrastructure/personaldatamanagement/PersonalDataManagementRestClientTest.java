package com.smalaca.opentrainings.infrastructure.personaldatamanagement;

import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataRequest;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataResponse;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonalDataManagementRestClientTest {
    private static final Faker FAKER = new Faker();

    private final PersonalDataManagementRestClient client = new PersonalDataManagementRestClient();

    @Test
    void shouldReturnFailedResponseWhenSaveIsCalled() {
        PersonalDataRequest request = personalDataRequest();

        PersonalDataResponse response = client.save(request);

        assertThat(response.isFailed()).isTrue();
        assertThat(response.participantId()).isNull();
    }

    private PersonalDataRequest personalDataRequest() {
        return new PersonalDataRequest(
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                FAKER.address().mailBox());
    }
}