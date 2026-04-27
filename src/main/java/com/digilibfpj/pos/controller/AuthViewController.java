package com.digilibfpj.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    @GetMapping("/login/user")
    public String userLoginPage() {
        return "auth/user-login";
    }

    @GetMapping("/login/admin")
    public String adminLoginPage() {
        return "auth/admin-login";
    }
}
