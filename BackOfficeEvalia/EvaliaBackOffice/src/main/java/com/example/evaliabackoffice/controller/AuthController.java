package com.example.evaliabackoffice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // maps to src/main/resources/templates/login.html
    }
}
