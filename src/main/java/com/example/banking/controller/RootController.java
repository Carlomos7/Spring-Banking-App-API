package com.example.banking.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("name","Banking API","docs","/swagger-ui/index.html");
    }
}
