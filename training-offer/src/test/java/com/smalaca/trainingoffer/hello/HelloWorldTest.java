package com.smalaca.trainingoffer.hello;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldTest {

    @Test
    void shouldCreateHelloWorldWithGivenMessage() {
        String message = "Test message";

        HelloWorld helloWorld = new HelloWorld(message);

        assertThat(helloWorld.message()).isEqualTo(message);
    }

    @Test
    void shouldCreateDefaultHelloWorld() {
        HelloWorld helloWorld = HelloWorld.defaultHello();

        assertThat(helloWorld.message()).isEqualTo("Hello, World!");
    }
}