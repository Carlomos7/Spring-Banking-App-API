package com.example.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("dev")
public class DevSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.disable()) // Disable CSRF for H2 console access in dev
          .headers(h -> h.frameOptions(f -> f.sameOrigin())) // Allow H2 console to load in a frame
          .authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**", "/auth/**").permitAll() // Permit access to H2 console and auth endpoints
            .anyRequest().authenticated()
          )
          .httpBasic(Customizer.withDefaults()); // Use basic auth for simplicity in dev
        return http.build();
    }
}
