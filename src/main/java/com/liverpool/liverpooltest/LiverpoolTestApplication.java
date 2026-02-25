package com.liverpool.liverpooltest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LiverpoolTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiverpoolTestApplication.class, args);
    }

}
