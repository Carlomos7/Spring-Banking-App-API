package com.example.banking.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@Tag(name = "Root", description = "Root endpoint providing basic API information")
public class RootController {

    @Operation(summary = "API Home", description = "Provides basic information about the API and a link to the documentation.")
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("name","Banking API","docs","/swagger-ui/index.html");
    }
}
