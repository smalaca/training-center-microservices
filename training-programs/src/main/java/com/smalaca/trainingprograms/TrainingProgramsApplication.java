package com.smalaca.trainingprograms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrainingProgramsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingProgramsApplication.class, args);
    }
}