package com.digilibfpj.pos.controller;

import com.digilibfpj.pos.dto.CheckoutRequest;
import com.digilibfpj.pos.dto.CheckoutResponse;
import com.digilibfpj.pos.service.CheckoutService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public CheckoutResponse checkout(@RequestBody CheckoutRequest checkoutRequest) {
        return checkoutService.processCheckout(checkoutRequest);
    }
}
