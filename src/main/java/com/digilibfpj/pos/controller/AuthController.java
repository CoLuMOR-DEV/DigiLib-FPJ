package com.digilibfpj.pos.controller;

import com.digilibfpj.pos.entity.Customer;
import com.digilibfpj.pos.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam(defaultValue = "false") boolean isMember,
                               Model model) {
        if (customerRepository.findByUsername(username).isPresent()) {
            model.addAttribute("errorMessage", "Username already exists");
            return "auth/signup";
        }

        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(passwordEncoder.encode(password));
        customer.setRole("USER");
        customer.setIsMember(isMember);
        customerRepository.save(customer);

        return "redirect:/login/user?registered=true";
    }
}
