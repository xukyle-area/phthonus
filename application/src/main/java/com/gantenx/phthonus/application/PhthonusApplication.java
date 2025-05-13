package com.gantenx.phthonus.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.gantenx.phthonus.*")
public class PhthonusApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhthonusApplication.class, args);
    }
}
