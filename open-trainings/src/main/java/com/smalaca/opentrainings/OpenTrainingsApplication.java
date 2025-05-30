package com.smalaca.opentrainings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OpenTrainingsApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenTrainingsApplication.class, args);
    }
}
