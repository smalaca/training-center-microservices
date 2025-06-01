package com.smalaca.trainingprograms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TrainingProgramsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingProgramsApplication.class, args);
    }
}