package com.example.evaliaproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@EnableJpaAuditing
//@SpringBootApplication(
//        scanBasePackages = {
//                "com.example.evaliaproject",
//                "com.example.evaliabackoffice"
//        }
//)
//@EntityScan(basePackages = {
//        "com.example.evaliaproject.entity",
//        "com.example.evaliabackoffice.entity"
//})
//@EnableJpaRepositories(basePackages = {
//        "com.example.evaliaproject.repository",
//        "com.example.evaliabackoffice.repository"
//})

@SpringBootApplication
public class EvaliaProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvaliaProjectApplication.class, args);
    }

}
