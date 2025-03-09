package com.proyecto.blog.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class LoginViewController {

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        // Aquí puedes pasar datos al modelo si lo necesitas
        return "login"; // Thymeleaf buscará "login.html" en src/main/resources/templates
    }
}

