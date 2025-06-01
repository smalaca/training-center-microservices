package com.smalaca.reviews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ReviewsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReviewsApplication.class, args);
    }
}