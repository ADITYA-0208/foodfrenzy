package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import com.example.demo.entities.User;


@Controller
public class AuthController {

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userRegistration", new User());
        return "register";  // The name of the HTML template
    }
    @PostMapping("/register")
    public String handleRegister(@ModelAttribute User userRegistration, Model model) {
        // Simulate registration logic (e.g., save to database, check for errors)
        if (userRegistration.getUemail() == null || userRegistration.getUpassword() == null) {
            model.addAttribute("error", "Registration failed. Please provide all details.");
            return "register";  // Return to register form if there's an error
        }
        // Successfully save the user (e.g., to the database)
        return "redirect:/login";  // Redirect to login page on successful registration
    }
}
