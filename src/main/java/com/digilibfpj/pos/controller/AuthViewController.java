package com.digilibfpj.pos.controller;

import com.digilibfpj.pos.repository.BookRepository;
import com.digilibfpj.pos.repository.SupplierRepository;
import com.digilibfpj.pos.service.AdminDashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    private final BookRepository bookRepository;
    private final SupplierRepository supplierRepository;
    private final AdminDashboardService adminDashboardService;

    public AuthViewController(BookRepository bookRepository,
                              SupplierRepository supplierRepository,
                              AdminDashboardService adminDashboardService) {
        this.bookRepository = bookRepository;
        this.supplierRepository = supplierRepository;
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        return "home";
    }

    @GetMapping("/login/user")
    public String userLoginPage() {
        return "auth/user-login";
    }

    @GetMapping("/login/admin")
    public String adminLoginPage() {
        return "auth/admin-login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("totalBooks", bookRepository.count());
        model.addAttribute("totalSuppliers", supplierRepository.count());
        model.addAttribute("lowStockItems", adminDashboardService.getLowStockAlerts());
        model.addAttribute("lowStockCount", adminDashboardService.getLowStockAlerts().size());
        return "admin-dashboard";
    }
}
