package com.digilibfpj.pos.bootstrap;

import com.digilibfpj.pos.entity.Customer;
import com.digilibfpj.pos.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.username:admin}")
    private String adminUsername;

    @Value("${admin.default.password:admin123}")
    private String adminPassword;

    public AdminAccountInitializer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.findByUsername(adminUsername).isEmpty()) {
            Customer admin = new Customer();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole("ADMIN");
            admin.setIsMember(false);
            customerRepository.save(admin);
        }
    }
}
