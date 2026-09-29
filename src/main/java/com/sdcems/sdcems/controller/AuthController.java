package com.sdcems.sdcems.controller;

import com.sdcems.sdcems.model.User;
import com.sdcems.sdcems.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository repo;

    public AuthController(UserRepository repo) {
        this.repo = repo;
    }


     // ================= REGISTER PAGE =================

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // ================= REGISTER =================

    @PostMapping("/register")
    @ResponseBody
    public String register(@RequestBody User user) {

        if (repo.findByEmail(user.getEmail()).isPresent()) {
            return "Email already exists";
        }

        repo.save(user);

        return "Registered Successfully";
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            jakarta.servlet.http.HttpSession session) {

        Optional<User> existing = repo.findByEmail(email);

        // User doesn't exist
        if (existing.isEmpty()) {
            return "redirect:/?error=UserNotFound";
        }

        // Wrong password
        if (!existing.get().getPassword().equals(password)) {
            return "redirect:/?error=WrongPassword";
        }

        // Store logged-in user's ID in session
        session.setAttribute("userId", existing.get().getId());

        // Redirect to upload page
        return "redirect:/uploadPage";
    }
}