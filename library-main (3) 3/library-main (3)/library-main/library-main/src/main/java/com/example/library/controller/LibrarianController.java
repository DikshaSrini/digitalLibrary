package com.example.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/librarian")
public class LibrarianController {

    @GetMapping("/dashboard")
    public String librarianDashboard(Model model) {
        model.addAttribute("message", "Welcome Librarian!");
        return "librarian/dashboard"; // Must match the template path
    }
}
