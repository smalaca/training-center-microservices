package com.smalaca.discount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DiscountApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscountApplication.class, args);
    }
}