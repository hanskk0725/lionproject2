package com.example.lionproject2backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Lionproject2BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lionproject2BackendApplication.class, args);
    }

}
