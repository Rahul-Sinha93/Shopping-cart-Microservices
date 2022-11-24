package com.fdmgroup.cloudGateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBackController {

    @GetMapping("/orderServiceFallBack")
    public String orderServerFallBack(){
        return "Order Service is Down.";
    }

    @GetMapping("/paymentServiceFallBack")
    public String paymentServerFallBack(){
        return "Payment Service is Down.";
    }

    @GetMapping("/productServiceFallBack")
    public String productServerFallBack(){
        return "Product Service is Down.";
    }
}
