package com.smalaca.personaldatamanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PersonalDataManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonalDataManagementApplication.class, args);
    }
}
