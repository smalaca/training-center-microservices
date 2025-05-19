package com.smalaca.trainingoffer.hello;

public record HelloWorld(String message) {
    public static HelloWorld defaultHello() {
        return new HelloWorld("Hello, World!");
    }
}