package com.ts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TravelSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(TravelSystemApplication.class, args);
    }
}
