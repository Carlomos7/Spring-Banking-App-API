package com.example.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("dev")
public class DevSecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService(){
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserDetails user = User.withUsername("spring")
            .password(encoder.encode("secret")) // stored as bcrypt hash
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.disable()) // Disable CSRF for H2 console access in dev
          .headers(h -> h.frameOptions(f -> f.sameOrigin())) // Allow H2 console to load in a frame
          .authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**", "/auth/**").permitAll() // Permit access to H2 console and auth endpoints
            .anyRequest().authenticated()
          )
          .httpBasic(Customizer.withDefaults())
          .formLogin(Customizer.withDefaults());
        return http.build();
    }


}
