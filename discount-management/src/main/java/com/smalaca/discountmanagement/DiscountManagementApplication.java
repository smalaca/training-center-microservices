package com.smalaca.discountmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DiscountManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscountManagementApplication.class, args);
    }
}