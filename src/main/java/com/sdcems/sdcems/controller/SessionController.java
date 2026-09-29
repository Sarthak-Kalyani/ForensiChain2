package com.sdcems.sdcems.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
public class SessionController {

    @PostMapping("/setUser")
    public String setUser(@RequestParam int userId, HttpSession session) {
        session.setAttribute("userId", userId);
        return "SESSION SET";
    }

    @GetMapping("/getUser")
    public Object getUser(HttpSession session) {
        return session.getAttribute("userId");
    }
}