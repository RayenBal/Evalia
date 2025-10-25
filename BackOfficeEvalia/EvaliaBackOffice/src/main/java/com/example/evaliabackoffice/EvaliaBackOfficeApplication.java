package com.example.evaliabackoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.example.evaliabackoffice")
public class EvaliaBackOfficeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvaliaBackOfficeApplication.class, args);
    }

}
