package com.smalaca.personaldatamanagement.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonalDataTest {

    @Test
    void shouldSayHello() {
        String actual = new PersonalData().sayHello();

        assertThat(actual).isEqualTo("Hello");
    }
}