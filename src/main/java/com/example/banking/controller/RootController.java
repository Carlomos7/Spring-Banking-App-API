package com.example.banking.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class RootController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the Banking API";
    }
}
