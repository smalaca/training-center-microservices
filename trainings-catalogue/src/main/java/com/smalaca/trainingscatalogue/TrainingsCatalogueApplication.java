package com.smalaca.trainingscatalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TrainingsCatalogueApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingsCatalogueApplication.class, args);
    }
}