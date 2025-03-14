package com.example.library.controller;

import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Tag(name = "Authorization Controller", description = "Role based entry point by authentication and authorization.")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // To hash passwords

    @GetMapping("/login")
    @Operation(summary = "Login Page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    @Operation(summary = "Registration Page")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    @Operation(summary = "Onboarded user")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        System.out.println("Received user: " + user);

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Password cannot be empty");
            return "redirect:/register";
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        System.out.println("User saved!");

        return "redirect:/login"; // Redirect to login page
    }

    @GetMapping("/redirectBasedOnRole")
    @Operation(summary = "Role based signing in")
    public String redirectBasedOnRole(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            switch (role) {
                case "ADMIN":
                    return "redirect:/admin/dashboard";
                case "LIBRARIAN":
                    return "redirect:/librarian/dashboard";
                case "USER":
                    return "redirect:/user/dashboard";
                default:
                    return "redirect:/login"; // Default redirect if no role is found
            }
        }
        return "redirect:/login"; // Default redirect if no authorities are found
    }
}