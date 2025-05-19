package com.smalaca.trainingoffer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TrainingOfferApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingOfferApplication.class, args);
    }
}