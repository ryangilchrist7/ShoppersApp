package com.shoppersapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
// import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
// @EnableJpaRepositories("com.shoppersapp.repositories")
// @EntityScan("com.shoppersapp.model")
public class ShoppersAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShoppersAppApplication.class, args);
    }
}